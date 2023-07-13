package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class GrudgeGrimoire extends Item {
    public GrudgeGrimoire(Properties p_41383_) {
        super(p_41383_);
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (pAttacker instanceof Player attacker && pTarget instanceof Player target){
            if (SEHelper.addGrudgePlayer(attacker, target)){
                pTarget.playSound(SoundEvents.ARROW_HIT_PLAYER);
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
        if (target instanceof Player target1){
            if (SEHelper.removeGrudgePlayer(player, target1)){
                target1.playSound(SoundEvents.PLAYER_LEVELUP);
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
