package com.Polarice3.Goety.api.items.magic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IMobCharm {
    String COOL_DOWN = "CoolDown";

    default void charmTick(ItemStack itemStack) {
        if (!isNotOnCoolDown(itemStack)) {
            decreaseCoolDown(itemStack);
        }
    }

    static boolean isNotOnCoolDown(ItemStack itemStack) {
        if (itemStack.getTag() == null){
            return true;
        }
        int coolDown = itemStack.getTag().getInt(COOL_DOWN);
        return coolDown == 0;
    }

    static int currentCool(ItemStack itemStack){
        if (itemStack.getTag() != null){
            return itemStack.getTag().getInt(COOL_DOWN);
        } else {
            return 0;
        }
    }

    static void setCoolDown(ItemStack itemStack, int cool){
        if (!(itemStack.getItem() instanceof IMobCharm)) {
            return;
        }
        itemStack.getOrCreateTag().putInt(COOL_DOWN, cool);
    }

    static void decreaseCoolDown(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof IMobCharm) || itemStack.getTag() == null) {
            return;
        }
        int coolDown = itemStack.getTag().getInt(COOL_DOWN);
        if (!isNotOnCoolDown(itemStack)) {
            int finalCount = Math.max(coolDown - 1, 0);
            itemStack.getOrCreateTag().putInt(COOL_DOWN, finalCount);
        }
    }

    default boolean mobShouldUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
        return isNotOnCoolDown(itemStack);
    }

    default void mobUse(ServerLevel serverLevel, LivingEntity livingEntity, ItemStack itemStack) {
    }
}
