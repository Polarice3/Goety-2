package com.Polarice3.Goety.api.ritual;

import com.Polarice3.Goety.common.ritual.type.*;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RitualType {
    public static Map<String, IRitualType> RITUAL_TYPE_LIST = Maps.newHashMap();
    public static IRitualType ANIMATION = new AnimationRitualType();
    public static IRitualType NECROTURGY = new NecroturgyRitualType();
    public static IRitualType FORGE = new ForgeRitualType();
    public static IRitualType MAGIC = new MagicRitualType();
    public static IRitualType ADEPT_NETHER = new AdeptNetherRitualType();
    public static IRitualType EXPERT_NETHER = new ExpertNetherRitualType();
    public static IRitualType SABBATH = new SabbathRitualType();
    public static IRitualType END = new EndRitualType();
    public static IRitualType SKY = new SkyRitualType();
    public static IRitualType STORM = new StormRitualType();
    public static IRitualType GEOTURGY = new GeoturgyRitualType();
    public static IRitualType FROST = new FrostRitualType();
    public static IRitualType DEEP = new DeepRitualType();
    public static IRitualType OVERGROWN = new OvergrownRitualType();

    public static void addRitualType(String id, IRitualType ritualType){
        RITUAL_TYPE_LIST.put(id, ritualType);
    }

    public static Map<String, IRitualType> getRitualTypeList(){
        Map<String, IRitualType> rituals = Maps.newHashMap();
        rituals.put(ANIMATION.getName(), ANIMATION);
        rituals.put(NECROTURGY.getName(), NECROTURGY);
        rituals.put(FORGE.getName(), FORGE);
        rituals.put(MAGIC.getName(), MAGIC);
        rituals.put(ADEPT_NETHER.getName(), ADEPT_NETHER);
        rituals.put(EXPERT_NETHER.getName(), EXPERT_NETHER);
        rituals.put(SABBATH.getName(), SABBATH);
        rituals.put(END.getName(), END);
        rituals.put(SKY.getName(), SKY);
        rituals.put(STORM.getName(), STORM);
        rituals.put(GEOTURGY.getName(), GEOTURGY);
        rituals.put(FROST.getName(), FROST);
        rituals.put(DEEP.getName(), DEEP);
        rituals.put(OVERGROWN.getName(), OVERGROWN);
        if (!RITUAL_TYPE_LIST.isEmpty()){
            rituals.putAll(RITUAL_TYPE_LIST);
        }
        return rituals;
    }

    public static List<IRitualType> getAllRitualType() {
        List<IRitualType> list = new ArrayList<>();
        for (IRitualType ritualType : getRitualTypeList().values()){
            if (ritualType != null) {
                list.add(ritualType);
            }
        }
        return list;
    }

    public static IRitualType getRitualType(String craftType){
        if (getRitualTypeList().containsKey(craftType)){
            return getRitualTypeList().get(craftType);
        }
        return null;
    }
}
