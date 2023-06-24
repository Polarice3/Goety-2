package com.Polarice3.Goety.common.items.brew;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SplashBrewItem extends ThrowableBrewItem{
    public SplashBrewItem() {
        super();
    }

    public InteractionResultHolder<ItemStack> use(Level p_43243_, Player p_43244_, InteractionHand p_43245_) {
        p_43243_.playSound((Player)null, p_43244_.getX(), p_43244_.getY(), p_43244_.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (p_43243_.getRandom().nextFloat() * 0.4F + 0.8F));
        return super.use(p_43243_, p_43244_, p_43245_);
    }
}
