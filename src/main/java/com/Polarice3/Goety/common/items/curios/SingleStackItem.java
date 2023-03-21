package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import net.minecraft.world.item.Item;

public class SingleStackItem extends Item {

    public SingleStackItem() {
        super(new Properties().tab(Goety.TAB).stacksTo(1));
    }

}
