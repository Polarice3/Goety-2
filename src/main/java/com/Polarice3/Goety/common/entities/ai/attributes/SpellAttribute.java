package com.Polarice3.Goety.common.entities.ai.attributes;

import com.Polarice3.Goety.api.magic.SpellType;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class SpellAttribute extends RangedAttribute {
    public static String POTENCY = "potency";
    public static String DISCOUNT = "discount";
    public SpellType spellType;
    public String type;

    public SpellAttribute(SpellType spellType, String p_22310_, double p_22311_, double p_22312_, double p_22313_, String type) {
        super(p_22310_, p_22311_, p_22312_, p_22313_);
        this.spellType = spellType;
        this.type = type;
    }

    public static SpellAttribute potency(SpellType spellType, double p_22311_, double p_22312_, double p_22313_){
        return new SpellAttribute(spellType, "attribute.name.goety." + spellType.getBaseName() + "_potency", p_22311_, p_22312_, p_22313_, POTENCY);
    }

    public static SpellAttribute discount(SpellType spellType, double p_22311_, double p_22312_, double p_22313_){
        return new SpellAttribute(spellType, "attribute.name.goety." + spellType.getBaseName() + "_discount", p_22311_, p_22312_, p_22313_, DISCOUNT);
    }

    public SpellType getSpellType() {
        return this.spellType;
    }

    public String getType() {
        return this.type;
    }
}
