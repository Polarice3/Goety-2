package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.entities.projectiles.IllBomb;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IllBombItem extends Item {
    public IllBombItem() {
        super(new Properties()
                .stacksTo(16)
                );
    }

    public InteractionResultHolder<ItemStack> use(Level p_41128_, Player p_41129_, InteractionHand p_41130_) {
        ItemStack itemstack = p_41129_.getItemInHand(p_41130_);
        p_41128_.playSound((Player)null, p_41129_.getX(), p_41129_.getY(), p_41129_.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (p_41128_.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!p_41128_.isClientSide) {
            IllBomb thrownegg = new IllBomb(p_41129_, p_41128_);
            thrownegg.shootFromRotation(p_41129_, p_41129_.getXRot(), p_41129_.getYRot(), 0.0F, 1.0F, 1.0F);
            p_41128_.addFreshEntity(thrownegg);
        }

        p_41129_.awardStat(Stats.ITEM_USED.get(this));
        if (!p_41129_.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        p_41129_.getCooldowns().addCooldown(ModItems.ILL_BOMB.get(), 20);
        return InteractionResultHolder.sidedSuccess(itemstack, p_41128_.isClientSide());
    }
}
