package com.Polarice3.Goety.api.ritual;

import com.Polarice3.Goety.common.ritual.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum RitualType implements net.minecraftforge.common.IExtensibleEnum {
    NONE(() -> "none"),
    ANIMATION(new AnimationRitualType()),
    NECROTURGY(new NecroturgyRitualType()),
    FORGE(new ForgeRitualType()),
    MAGIC(new MagicRitualType()),
    ADEPT_NETHER(new AdeptNetherRitualType()),
    EXPERT_NETHER(new ExpertNetherRitualType()),
    SABBATH(new SabbathRitualType()),
    END(new EndRitualType()),
    SKY(new SkyRitualType()),
    STORM(new StormRitualType()),
    GEOTURGY(new GeoturgyRitualType()),
    FROST(new FrostRitualType()),
    DEEP(new DeepRitualType());

    private final IRitualType type;

    RitualType(IRitualType type){
        this.type = type;
    }

    public static RitualType create(String name, IRitualType type){
        throw new IllegalStateException("Enum not extended");
    }

    public IRitualType getType(){
        return this.type;
    }

    public static IRitualType getRitualType(String craftType){
        for (RitualType ritualType : RitualType.values()){
            if (ritualType.getType() != null) {
                if (Objects.equals(ritualType.getType().getName(), craftType)) {
                    return ritualType.getType();
                }
            }
        }
        return null;
    }

    public static List<IRitualType> getAllRitualType(){
        List<IRitualType> list = new ArrayList<>();
        for (RitualType ritualType : RitualType.values()){
            if (ritualType.getType() != null) {
                list.add(ritualType.getType());
            }
        }
        return list;
    }
}
