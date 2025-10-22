package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IBlockSpell extends ISpell{
    default int defaultCastDuration() {
        return 0;
    }

    @Deprecated
    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target){
        return true;
    }

    @Deprecated
    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction){
        return rightBlock(worldIn, caster, target);
    }

    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, SpellStat spellStat){
        return true;
    }

    default boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction, SpellStat spellStat){
        return rightBlock(worldIn, caster, target, spellStat);
    }

    @Deprecated
    default void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target, Direction direction) {
    }

    @Deprecated
    default void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction, SpellStat spellStat) {
        blockResult(worldIn, caster, target, spellStat);
    }

    @Deprecated
    default void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target, SpellStat spellStat) {
        blockResult(worldIn, caster, ItemStack.EMPTY, target, spellStat);
    }

    default void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target, SpellStat spellStat) {
    }

    default void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target, Direction direction, SpellStat spellStat) {
        blockResult(worldIn, caster, staff, target, spellStat);
    }

}
