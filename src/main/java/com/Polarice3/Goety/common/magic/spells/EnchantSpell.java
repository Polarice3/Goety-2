package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.magic.Spell;

public class EnchantSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return 64;
    }

    @Override
    public int defaultCastDuration() {
        return 83;
    }

    @Override
    public int defaultSpellCooldown() {
        return 0;
    }
}
