package com.Polarice3.Goety.common.effects.brew.modifiers;

public class BrewModifier {
    public static String CAPACITY = "capacity";

    public static String DURATION = "duration";
    public static String AMPLIFIER = "amplifier";
    public static String AOE = "aoe";
    public static String LINGER = "linger";
    public static String QUAFF = "quaff";
    public static String VELOCITY = "velocity";
    public static String AQUATIC = "aquatic";
    public static String FIRE_PROOF = "fire_proof";

    public static String HIDDEN = "hidden";
    public static String SPLASH = "splash";
    public static String LINGERING = "lingering";
    public static String GAS = "gas";
    public String id;
    public int level;

    public BrewModifier(String id, int level){
        this.id = id;
        this.level = level;
    }

    public BrewModifier(String id){
        this.id = id;
        this.level = 0;
    }

    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }
}
