package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ThornTrapBrewEffect extends BrewEffect{
    public ThornTrapBrewEffect(int soulCost) {
        super("thorn_trap", soulCost, MobEffectCategory.HARMFUL, 0x4d7646);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
        Level level = pTarget.level;
        for (Direction direction : Direction.values()) {
            if (direction.getAxis().isHorizontal()){
                BlockPos blockPos = pTarget.blockPosition().relative(direction);
                BlockPos blockPos1 = BlockPos.containing(blockPos.getX(), BlockFinder.moveBlockDownToGround(level, blockPos), blockPos.getZ());
                if (ModBlocks.MAGIC_THORN.get().defaultBlockState().canSurvive(level, blockPos1)) {
                    for (int i = 0; i < pAmplifier + 1; ++i) {
                        BlockPos blockPos2 = blockPos1.offset(0, i, 0);
                        if (level.getBlockState(blockPos2).isAir()
                                || BlockFinder.canBeReplaced(level, blockPos2, blockPos1)) {
                            level.setBlockAndUpdate(blockPos2, ModBlocks.MAGIC_THORN.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}
