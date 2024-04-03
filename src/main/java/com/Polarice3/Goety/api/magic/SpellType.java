package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public enum SpellType implements net.minecraftforge.common.IExtensibleEnum {
    NONE("none"),
    NECROMANCY("necromancy", ModItems.NECRO_STAFF.get(), ModItems.NAMELESS_STAFF.get()),
    NETHER("nether"),
    ILL("ill", ModItems.OMINOUS_STAFF.get()),
    FROST("frost", ModItems.FROST_STAFF.get()),
    GEOMANCY("geomancy"),
    WIND("wind", ModItems.WIND_STAFF.get()),
    STORM("storm", ModItems.STORM_STAFF.get()),
    ABYSS("abyss"),
    WILD("wild", ModItems.WILD_STAFF.get()),
    VOID("void");

    private final Item[] staff;
    private final Component name;

    SpellType(String name, Item... staff){
        this.name = Component.translatable("spell.goety." + name);
        this.staff = staff;
    }

    public static SpellType create(String name, String translation, Item... staff){
        throw new IllegalStateException("Enum not extended");
    }

    public Component getName(){
        return name;
    }

    public Item[] getStaffs() {
        return staff;
    }
}