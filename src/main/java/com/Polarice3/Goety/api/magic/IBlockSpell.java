package com.Polarice3.Goety.api.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IBlockSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target){
        return true;
    }

    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction){
        return rightBlock(worldIn, caster, target);
    }

    @Deprecated
    default void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction) {
        blockResult(worldIn, caster, target);
    }

    @Deprecated
    default void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        blockResult(worldIn, caster, ItemStack.EMPTY, target);
    }

    default void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target) {
    }

    default void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target, Direction direction) {
        blockResult(worldIn, caster, staff, target);
    }

}
