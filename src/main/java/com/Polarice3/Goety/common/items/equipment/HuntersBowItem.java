package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class HuntersBowItem extends BowItem {
    public HuntersBowItem() {
        super((new Properties()).rarity(Rarity.UNCOMMON).defaultDurability(ItemConfig.HuntersBowDurability.get()));
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.getItem() instanceof BowItem;
    }
}
