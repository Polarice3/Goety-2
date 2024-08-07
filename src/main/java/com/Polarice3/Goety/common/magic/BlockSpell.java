package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.IBlockSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class BlockSpell extends Spell implements IBlockSpell {

    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }
}
