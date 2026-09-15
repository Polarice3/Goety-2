package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RepairPuttyItem extends ItemBase {

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();
        if (!level.isClientSide) {
            if (player.getOffhandItem().getItem() instanceof HammerItem || player.getOffhandItem().is(ModTags.Items.HAMMERS)) {
                if (target instanceof Mob mob) {
                    if (target.getType().getDescriptionId().contains("golem") || target.getType().is(ModTags.EntityTypes.REPAIRABLE)) {
                        boolean notTargetFriendly = mob.getTarget() == null || (mob.getTarget() != player && !MobUtil.areAllies(mob.getTarget(), player));
                        if (notTargetFriendly || (target instanceof IronGolem golem && golem.isPlayerCreated())) {
                            mob.heal(25.0F);
                            float f1 = 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F;
                            mob.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 7; ++i) {
                                    double d0 = mob.getRandom().nextGaussian() * 0.02D;
                                    double d1 = mob.getRandom().nextGaussian() * 0.02D;
                                    double d2 = mob.getRandom().nextGaussian() * 0.02D;
                                    serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), mob.getRandomX(1.0D), mob.getRandomY() + 0.5D, mob.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                                }
                            }
                            if (!player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
