package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public enum SpellType implements net.minecraftforge.common.IExtensibleEnum {
    NONE("none", null),
    NECROMANCY("necromancy", ModItems.NECRO_STAFF.get()),
    NETHER("nether", null),
    ILL("ill", ModItems.OMINOUS_STAFF.get()),
    FROST("frost", null),
    GEOMANCY("geomancy", null),
    WIND("wind", ModItems.WIND_STAFF.get()),
    STORM("storm", ModItems.STORM_STAFF.get()),
    ABYSS("abyss", null),
    VOID("void", null);

    private final Item staff;
    private final Component name;

    SpellType(String name, Item staff){
        this.name = Component.translatable("spell.goety." + name);
        this.staff = staff;
    }

    public static SpellType create(String name, String translation, Item staff){
        throw new IllegalStateException("Enum not extended");
    }

    public Component getName(){
        return name;
    }

    public Item getStaff() {
        return staff;
    }
}
