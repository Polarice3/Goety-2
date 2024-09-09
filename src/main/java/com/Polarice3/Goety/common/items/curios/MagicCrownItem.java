package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.api.magic.SpellType;

public class MagicCrownItem extends SingleStackItem {
    public SpellType spellType;

    public MagicCrownItem(SpellType spellType) {
        this(new Properties().stacksTo(1), spellType);
    }

    public MagicCrownItem(Properties properties, SpellType spellType) {
        super(properties);
        this.spellType = spellType;
    }

    public SpellType getSpellType() {
        return this.spellType;
    }
}
