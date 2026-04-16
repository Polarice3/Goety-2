package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.items.curios.IActivatable;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TargetingMonocleItem extends SingleStackItem implements IActivatable {
    private static final String IS_ACTIVE = "Activated";

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this)) {
            if (player.isCrouching() || !isActive(itemStack)) {
                setIsActive(itemStack, !isActive(itemStack));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TOCK.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack);
    }

    public static void setIsActive(ItemStack stack, boolean activate){
        if (stack.getTag() != null) {
            stack.getTag().putBoolean(IS_ACTIVE, activate);
        } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putBoolean(IS_ACTIVE, activate);
        }
    }

    public static boolean isActive(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getBoolean(IS_ACTIVE);
        } else {
            return false;
        }
    }
}
