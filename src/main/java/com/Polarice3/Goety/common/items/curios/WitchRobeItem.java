package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WitchRobeItem extends SingleStackItem {
    public static String INVENTORY = "WITCH_ROBE_BREW";

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            if (ModSaveInventory.getInstance() != null) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundTag());
                    stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                } else {
                    if (!stack.getOrCreateTag().contains(INVENTORY)) {
                        stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                    }

                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory((stack.getOrCreateTag().getInt(INVENTORY)), livingEntity);

                    if (!worldIn.isClientSide) {
                        if (CuriosFinder.hasWitchHat(livingEntity)) {
                            inventory.setIncreaseSpeed(1);
                        } else {
                            inventory.setIncreaseSpeed(0);
                        }

                        inventory.tick();
                    }
                }
            }
        }
    }
}
