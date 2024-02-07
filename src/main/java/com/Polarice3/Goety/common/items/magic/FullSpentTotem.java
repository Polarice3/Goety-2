package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.ItemConfig;
import com.Polarice3.Goety.api.items.magic.ITotem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class FullSpentTotem extends TotemOfSouls{

    public FullSpentTotem(int maxSouls) {
        super(maxSouls);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.getTag() != null) {
            if (container.getTag().getInt(SOULS_AMOUNT) > ItemConfig.CraftingSouls.get()) {
                ITotem.decreaseSouls(container, ItemConfig.CraftingSouls.get());
                return container;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }
}
