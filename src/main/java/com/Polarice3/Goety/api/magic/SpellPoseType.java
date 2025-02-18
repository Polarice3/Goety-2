package com.Polarice3.Goety.api.magic;

public enum SpellPoseType implements net.minecraftforge.common.IExtensibleEnum {
    DEFAULT("GOETY_SPELL"),
    FLYING("GOETY_FLYING");

    private final String name;

    SpellPoseType(String name){
        this.name = name;
    }

    public static SpellPoseType create(String name, String translation){
        throw new IllegalStateException("Enum not extended");
    }

    public String getName(){
        return name;
    }
}
