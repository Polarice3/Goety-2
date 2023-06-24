package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.MainConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

    public static void addItemEntity(Level level, BlockPos blockPos, ItemStack itemStack){
        double d0 = (double) (level.random.nextFloat() * 0.5F) + 0.25D;
        double d1 = (double) (level.random.nextFloat() * 0.5F) + 0.25D;
        double d2 = (double) (level.random.nextFloat() * 0.5F) + 0.25D;
        ItemEntity itementity = new ItemEntity(level, (double) blockPos.getX() + d0, (double) blockPos.getY() + d1, (double) blockPos.getZ() + d2, itemStack);
        itementity.setDefaultPickUpDelay();
        level.addFreshEntity(itementity);
    }

    public static void addAndConsumeItem(Player player, InteractionHand hand, ItemStack toAdd) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() == 1) {
            if (!player.isCreative()) {
                player.setItemInHand(hand, toAdd);
            }
        } else {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (!player.getInventory().add(toAdd)) {
                player.drop(toAdd, false, true);
            }
        }
    }

    public static boolean hasItem(Player player, Item item){
        return !findItem(player, item).isEmpty();
    }

    public static ItemStack findItem(Player playerEntity, Item item){
        ItemStack foundStack = ItemStack.EMPTY;
        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
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
        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && item.test(itemStack)) {
                foundStack = itemStack;
                break;
            }
        }
        return foundStack;
    }

    public static boolean findHelmet(Player player, Item item){
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == item;
    }

    public static boolean armorSet(LivingEntity living, ArmorMaterial material){
        if (living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem head) {
            if (living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem chest) {
                if (living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem legs){
                    if (living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem feet){
                        return head.getMaterial() == material && chest.getMaterial() == material && legs.getMaterial() == material && feet.getMaterial() == material;
                    }
                }
            }
        }
        return false;
    }

    public static void repairTick(ItemStack stack, Entity entityIn, boolean isSelected){
        if (MainConfig.SoulRepair.get()) {
            if (entityIn instanceof Player player) {
                if (!(player.swinging && isSelected)) {
                    if (stack.isDamaged()) {
                        if (SEHelper.getSoulsContainer(player)){
                            int i = 1;
                            if (!stack.getAllEnchantments().isEmpty()) {
                                i += stack.getAllEnchantments().size();
                            }
                            if (SEHelper.getSoulsAmount(player, MainConfig.ItemsRepairAmount.get() * i)){
                                if (player.tickCount % 20 == 0) {
                                    stack.setDamageValue(stack.getDamageValue() - 1);
                                    SEHelper.decreaseSouls(player, MainConfig.ItemsRepairAmount.get() * i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
