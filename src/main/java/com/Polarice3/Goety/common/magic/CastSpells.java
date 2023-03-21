package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.common.magic.spells.*;

public record CastSpells(int spellInt) {

    public Spells getSpell() {
        return switch (spellInt) {
            case 0 -> new VexSpell();
            case 1 -> new FangSpell();
            case 2 -> new IceChunkSpell();
            case 3 -> new IllusionSpell();
            case 4 -> new FeastSpell();
            case 5 -> new SonicBoomSpell();
            case 6 -> new SoulLightSpell();
            case 7 -> new GlowLightSpell();
            case 8 -> new ZombieSpell();
            case 9 -> new SkeletonSpell();
            case 10 -> new WraithSpell();
            case 11 -> new LaunchSpell();
            case 12 -> new SoulBoltSpell();
            case 13 -> new LightningSpell();
            default -> null;
        };
    }
}
