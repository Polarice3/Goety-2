package com.Polarice3.Goety.common.items.revive;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class BlazingHelm extends ReviveServantItem{

    public BlazingHelm(){
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .fireResistant()
                .stacksTo(1)
        );
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();

        Entity entity;
        if (getSummon(stack, level) != null){
            entity = getSummon(stack, level);
        } else {
            entity = new Wildfire(ModEntityType.WILDFIRE.get(), level);
            IOwned owned = (IOwned) entity;
            owned.setTrueOwner(player);
        }
        if (entity instanceof Wildfire wildfire) {
            boolean flag = target instanceof BlazeServant || target instanceof Blaze;
            if (flag) {
                if (wildfire.getTrueOwner() == player) {
                    if (RitualRequirements.canSummon(level, player, ModEntityType.WILDFIRE.get())) {
                        wildfire.setHealth(wildfire.getMaxHealth());
                        wildfire.setPos(target.getX(), target.getY(), target.getZ());
                        wildfire.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                        if (level.addFreshEntity(wildfire)) {
                            wildfire.spawnAnim();
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), wildfire);
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SMOKE, wildfire);
                                }
                            }
                            wildfire.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                            wildfire.playSound(ModSounds.WILDFIRE_AMBIENT.get(), 2.0F, 0.5F);
                            target.discard();
                            player.swing(hand);
                            player.getCooldowns().addCooldown(this, MathHelper.secondsToTicks(30));
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }
}
