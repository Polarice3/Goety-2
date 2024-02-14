package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block blockIn) {
        super(blockIn, new Item.Properties().tab(Goety.TAB));
    }
}
