package com.Polarice3.Goety.api.items.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ISoulDiscount {

    int getSoulDiscount(EquipmentSlot equipmentSlot);

    default Component soulDiscountTooltip(ItemStack itemStack){
        return Component.literal(String.valueOf(this.getSoulDiscount(LivingEntity.getEquipmentSlotForItem(itemStack)))).append("% ").append(Component.translatable("info.goety.armor.discount")).withStyle(ChatFormatting.DARK_AQUA);
    }
}
