package com.Polarice3.Goety.common.items.brew;

import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.utils.BrewUtils;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowableBrewItem extends BrewItem{
    public ThrowableBrewItem() {
        super();
    }

    public InteractionResultHolder<ItemStack> use(Level p_43303_, Player p_43304_, InteractionHand p_43305_) {
        ItemStack itemstack = p_43304_.getItemInHand(p_43305_);
        if (!p_43303_.isClientSide) {
            ThrownBrew thrownBrew = new ThrownBrew(p_43303_, p_43304_);
            thrownBrew.setItem(itemstack);
            float velocity = 0.5F + BrewUtils.getVelocity(itemstack);
            thrownBrew.shootFromRotation(p_43304_, p_43304_.getXRot(), p_43304_.getYRot(), -20.0F, velocity, 1.0F);
            p_43303_.addFreshEntity(thrownBrew);
        }

        p_43304_.awardStat(Stats.ITEM_USED.get(this));
        if (!p_43304_.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, p_43303_.isClientSide());
    }
}
