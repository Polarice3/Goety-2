package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface ISoulRepair {

    default void repairTick(ItemStack stack, Entity entityIn, boolean isSelected){
        ItemHelper.repairTick(stack, entityIn, isSelected);
    }
}
