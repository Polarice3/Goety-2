package com.Polarice3.Goety.api.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IBlockSpell extends ISpell{
    default int CastDuration() {
        return 0;
    }

    default void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }

    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target){
        return true;
    }

    void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target);
}
