package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModTiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class BladeOfEnderItem extends SwordItem {

    public BladeOfEnderItem() {
        super(ModTiers.VOID, 13, -1.8F, new Item.Properties().fireResistant());
    }
}
