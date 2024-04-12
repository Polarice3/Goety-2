package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.IBlockSpell;
import net.minecraft.core.BlockPos;
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

    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target){
        return true;
    }

    public abstract void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target);
}
