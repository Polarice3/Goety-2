package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.common.magic.Spell;
import net.minecraft.sounds.SoundEvent;

public class CommandSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return 0;
    }
}
