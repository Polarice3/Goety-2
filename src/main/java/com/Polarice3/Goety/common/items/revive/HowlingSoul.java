package com.Polarice3.Goety.common.items.revive;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.items.ModItems;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class HowlingSoul extends ReviveServantItem {
    public HowlingSoul(){
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();

        Entity entity;
        if (getSummon(stack, level) != null){
            entity = getSummon(stack, level);
        } else {
            entity = new BlackBeast(ModEntityType.BLACK_BEAST.get(), level);
            IOwned owned = (IOwned) entity;
            owned.setTrueOwner(player);
        }
        if (entity instanceof BlackBeast blackBeast) {
            boolean flag = target instanceof BlackWolf;
            if (flag) {
                if (blackBeast.getTrueOwner() == player) {
                    if (RitualRequirements.canSummon(level, player, ModEntityType.BLACK_BEAST.get())) {
                        blackBeast.setHealth(blackBeast.getMaxHealth());
                        blackBeast.setPos(target.getX(), target.getY(), target.getZ());
                        blackBeast.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                        if (level.addFreshEntity(blackBeast)) {
                            blackBeast.spawnAnim();
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SCULK_SOUL, blackBeast);
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.POOF, blackBeast);
                                }
                            }
                            blackBeast.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                            blackBeast.playSound(ModSounds.BLACK_BEAST_ROAR.get(), 2.0F, 0.5F);
                            target.discard();
                            player.swing(hand);
                            player.getCooldowns().addCooldown(ModItems.HOWLING_SOUL.get(), MathHelper.secondsToTicks(30));
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

}
