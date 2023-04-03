package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class WitchRobeItem extends SingleStackItem implements ICurioItem {
    public static String INVENTORY = "WITCH_ROBE_BREW";

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (ModSaveInventory.getInstance() != null) {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
                stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
            } else {
                if (!stack.getOrCreateTag().contains(INVENTORY)) {
                    stack.getOrCreateTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                }

                WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory((stack.getOrCreateTag().getInt(INVENTORY)), slotContext.entity());

                if (CuriosFinder.hasCurio(slotContext.entity(), ModItems.WITCH_HAT.get())) {
                    inventory.setIncreaseSpeed(1);
                } else {
                    inventory.setIncreaseSpeed(0);
                }

                if (!inventory.isEmpty() && inventory.isBrewable()) {
                    inventory.tick();
                }
            }
        }
    }
}
