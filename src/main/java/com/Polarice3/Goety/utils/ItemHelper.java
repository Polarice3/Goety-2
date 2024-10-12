package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;

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

    public static void hurtNoEntity(ItemStack itemStack, int pAmount, Level level){
        if (!level.isClientSide) {
            if (itemStack.isDamageableItem()) {
                if (itemStack.hurt(pAmount, level.getRandom(), null)) {
                    itemStack.shrink(1);
                    itemStack.setDamageValue(0);
                }
            }
        }
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
        addAndConsumeItem(player, hand, toAdd, true);
    }

    public static void addAndConsumeItem(Player player, InteractionHand hand, ItemStack toAdd, boolean addToInventory) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() == 1) {
            if (!player.isCreative()) {
                player.setItemInHand(hand, toAdd);
            } else if (addToInventory){
                if (!player.getInventory().add(toAdd)) {
                    player.drop(toAdd, false, true);
                }
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
        int i = 0;
        if (living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem helmet && helmet.getMaterial() == material) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    if (living.getItemBySlot(equipmentSlot).getItem() instanceof ArmorItem armorItem) {
                        if (armorItem.getMaterial() == material) {
                            ++i;
                        }
                    }
                }
            }
        }
        return i >= 4;
    }

    public static boolean isFullEquipped(LivingEntity living){
        int i = 0;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
            if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR){
                if (!living.getItemBySlot(equipmentSlot).isEmpty()){
                    ++i;
                }
            }
        }
        return i >= 4;
    }

    public static boolean isFullArmored(LivingEntity living){
        int i = 0;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
            if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR){
                if (living.getItemBySlot(equipmentSlot).getItem() instanceof ArmorItem){
                    ++i;
                }
            }
        }
        return i >= 4;
    }

    public static boolean noArmor(LivingEntity living){
        int i = 0;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()){
            if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR){
                if (living.getItemBySlot(equipmentSlot).isEmpty()){
                    ++i;
                }
            }
        }
        return i >= 4;
    }

    public static void repairTick(ItemStack stack, Entity entityIn, boolean isSelected){
        if (ItemConfig.SoulRepair.get()) {
            if (entityIn instanceof Player player) {
                if (!(player.swinging && isSelected)) {
                    if (stack.isDamaged()) {
                        if (SEHelper.getSoulsContainer(player)){
                            int i = 1;
                            if (!stack.getAllEnchantments().isEmpty() && ItemConfig.SoulRepairEnchant.get()) {
                                i += stack.getAllEnchantments().size();
                            }
                            if (SEHelper.getSoulsAmount(player, ItemConfig.ItemsRepairAmount.get() * i)){
                                if (player.tickCount % 20 == 0) {
                                    stack.setDamageValue(stack.getDamageValue() - 1);
                                    SEHelper.decreaseSouls(player, ItemConfig.ItemsRepairAmount.get() * i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Based on fluid codes from @Vazkii.
     */
    public static boolean isValidFluidContainerToDrain(ItemStack stack, Fluid fluid) {
        if (stack.isEmpty() || stack.getCount() != 1) {
            return false;
        }

        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> {
            FluidStack simulate = handler.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
            return !simulate.isEmpty() && simulate.getFluid() == fluid && simulate.getAmount() == FluidType.BUCKET_VOLUME;
        }).orElse(false);
    }

    public static ItemStack drain(Fluid fluid, ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(handler -> {
                    handler.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    return handler.getContainer();
                })
                .orElse(stack);
    }

    public static boolean isValidFluidContainerToFill(ItemStack stack, Fluid fluid) {
        if (stack.isEmpty()) {
            return false;
        }

        ItemStack container = stack;
        if (stack.getCount() > 1) {
            container = new ItemStack(stack.getItem());
        }

        return container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> {
            int amount = handler.fill(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
            return amount == FluidType.BUCKET_VOLUME;
        }).orElse(false);
    }

    public static ItemStack fill(Fluid fluid, ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(handler -> {
                    handler.fill(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    return handler.getContainer();
                })
                .orElse(stack);
    }

    public static void setItemEffect(ItemStack stack, LivingEntity victim){
        if (stack.getItem() instanceof TieredItem weapon){
            if (weapon.getTier() == ModTiers.DARK) {
                victim.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 60));
            }
            if (weapon == ModItems.FELL_BLADE.get() && victim.getRandom().nextBoolean()) {
                victim.addEffect(new MobEffectInstance(GoetyEffects.BUSTED.get(), MathHelper.secondsToTicks(5)));
            }
            if (weapon == ModItems.FROZEN_BLADE.get()) {
                victim.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(2)));
            }
        } else if (stack.getItem() instanceof PhilosophersMaceItem){
            int i2 = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
            victim.addEffect(new MobEffectInstance(GoetyEffects.GOLD_TOUCHED.get(), 300, i2));
        }
    }
}
