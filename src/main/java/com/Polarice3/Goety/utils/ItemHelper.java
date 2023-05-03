package com.Polarice3.Goety.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemHelper {

    public static <T extends LivingEntity> void hurtAndRemove(ItemStack stack, int pAmount, T pEntity) {
        if (!pEntity.level.isClientSide && (!(pEntity instanceof Player) || !((Player)pEntity).getAbilities().instabuild)) {
            if (stack.isDamageableItem()) {
                if (stack.hurt(pAmount, pEntity.getRandom(), pEntity instanceof ServerPlayer ? (ServerPlayer)pEntity : null)) {
                    stack.shrink(1);
                    stack.setDamageValue(0);
                }
            }
        }
    }

    public static <T extends LivingEntity> void hurtAndBreak(ItemStack itemStack, int pAmount, T pEntity) {
        itemStack.hurtAndBreak(pAmount, pEntity, (p_220045_0_) -> p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

    public static ItemEntity itemEntityDrop(LivingEntity livingEntity, ItemStack itemStack){
        return new ItemEntity(livingEntity.level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack);
    }

    public static ItemStack findItem(Player playerEntity, Item item){
        ItemStack foundStack = ItemStack.EMPTY;
        for (int i = 0; i <= playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && itemStack.getItem() == item) {
                foundStack = itemStack;
                break;
            }
        }
        return foundStack;
    }

    public static ItemStack findItem(Player playerEntity, Predicate<ItemStack> item){
        ItemStack foundStack = ItemStack.EMPTY;
        for (int i = 0; i <= playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && item.test(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }
        return foundStack;
    }
}
