package com.Polarice3.Goety.api.items.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ISoulDiscount {

    @Deprecated
    default int getSoulDiscount(EquipmentSlot equipmentSlot) {
        return 0;
    }

    default int getSoulDiscount(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        return 0;
    }

    default Component soulDiscountTooltip(ItemStack itemStack){
        int discount = this.getSoulDiscount(LivingEntity.getEquipmentSlotForItem(itemStack), itemStack);
        if (discount > 0) {
            return Component.literal(String.valueOf(this.getSoulDiscount(LivingEntity.getEquipmentSlotForItem(itemStack), itemStack))).append("% ").append(Component.translatable("info.goety.armor.discount")).withStyle(ChatFormatting.DARK_AQUA);
        } else {
            return Component.empty();
        }
    }
}
