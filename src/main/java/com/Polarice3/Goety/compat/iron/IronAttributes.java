package com.Polarice3.Goety.compat.iron;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class IronAttributes {
    public static final Attribute FIRE_MAGIC_RESIST = getResistanceAttribute("fire");
    public static final Attribute ICE_MAGIC_RESIST = getResistanceAttribute("ice");
    public static final Attribute LIGHTNING_MAGIC_RESIST = getResistanceAttribute("lightning");
    public static final Attribute HOLY_MAGIC_RESIST = getResistanceAttribute("holy");
    public static final Attribute ENDER_MAGIC_RESIST = getResistanceAttribute("ender");
    public static final Attribute BLOOD_MAGIC_RESIST = getResistanceAttribute("blood");
    public static final Attribute EVOCATION_MAGIC_RESIST = getResistanceAttribute("evocation");
    public static final Attribute NATURE_MAGIC_RESIST = getResistanceAttribute("nature");

    public static final Attribute FIRE_SPELL_POWER = getPowerAttribute("fire");
    public static final Attribute ICE_SPELL_POWER = getPowerAttribute("ice");
    public static final Attribute LIGHTNING_SPELL_POWER = getPowerAttribute("lightning");
    public static final Attribute HOLY_SPELL_POWER = getPowerAttribute("holy");
    public static final Attribute ENDER_SPELL_POWER = getPowerAttribute("ender");
    public static final Attribute BLOOD_SPELL_POWER = getPowerAttribute("blood");
    public static final Attribute EVOCATION_SPELL_POWER = getPowerAttribute("evocation");
    public static final Attribute NATURE_SPELL_POWER = getPowerAttribute("nature");

    private static Attribute getResistanceAttribute(String id) {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", id + "_magic_resist"));
    }

    private static Attribute getPowerAttribute(String id){
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", id + "_spell_power"));
    }

    public static List<AttributeInstance> power(Mob mob){
        List<AttributeInstance> list = new ArrayList<>();
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES){
            if (attribute.getDescriptionId().contains("irons_spellbooks") && attribute.getDescriptionId().contains("_spell_power")){
                if (mob.getAttribute(attribute) != null){
                    list.add(mob.getAttribute(attribute));
                }
            }
        }
        return list;
    }

    public static List<AttributeInstance> resistances(Mob mob){
        List<AttributeInstance> list = new ArrayList<>();
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES){
            if (attribute.getDescriptionId().contains("irons_spellbooks") && attribute.getDescriptionId().contains("_magic_resist")){
                if (mob.getAttribute(attribute) != null){
                    list.add(mob.getAttribute(attribute));
                }
            }
        }
        return list;
    }
}
