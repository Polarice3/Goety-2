package com.Polarice3.Goety.api.magic;

import net.minecraft.network.chat.Component;

public enum SpellType implements net.minecraftforge.common.IExtensibleEnum {
    NONE("none"),
    NECROMANCY("necromancy"),
    NETHER("nether"),
    ILL("ill"),
    FROST("frost"),
    GEOMANCY("geomancy"),
    WIND("wind"),
    STORM("storm"),
    ABYSS("abyss"),
    WILD("wild"),
    VOID("void");

    private final String baseName;
    private final Component name;

    SpellType(String name){
        this.baseName = name;
        this.name = Component.translatable("spell.goety." + name);
    }

    public static SpellType create(String name, String translation){
        throw new IllegalStateException("Enum not extended");
    }

    public String getBaseName() {
        return baseName;
    }

    public Component getName(){
        return name;
    }

}
