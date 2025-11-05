package com.Polarice3.Goety.common.entities.ai.attributes;

import com.Polarice3.Goety.api.magic.SpellType;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class SpellAttribute extends RangedAttribute {
    public SpellType spellType;

    public SpellAttribute(SpellType spellType, String p_22310_, double p_22311_, double p_22312_, double p_22313_) {
        super(p_22310_, p_22311_, p_22312_, p_22313_);
        this.spellType = spellType;
    }

    public static SpellAttribute potency(SpellType spellType, double p_22311_, double p_22312_, double p_22313_){
        return new SpellAttribute(spellType, "attribute.name.goety." + spellType.getBaseName() + "_potency", p_22311_, p_22312_, p_22313_);
    }

    public SpellType getSpellType() {
        return this.spellType;
    }
}
