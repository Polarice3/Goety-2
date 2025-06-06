package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.IBlockSpell;

public abstract class BlockSpell extends Spell implements IBlockSpell {

    public int defaultCastDuration() {
        return 0;
    }
}
