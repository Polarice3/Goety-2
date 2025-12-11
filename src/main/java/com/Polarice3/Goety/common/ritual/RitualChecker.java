package com.Polarice3.Goety.common.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class RitualChecker {
    public Level level;
    public BlockPos initialPos;
    public Predicate<BlockState> predicate;
    public int xRange, yRange, zRange, totalCount;

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int xRange, int yRange, int zRange, int totalCount) {
        this.level = pLevel;
        this.initialPos = pPos;
        this.predicate = pPredicate;
        this.xRange = xRange;
        this.yRange = yRange;
        this.zRange = zRange;
        this.totalCount = totalCount;
    }

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int range, int totalCount) {
        this(pLevel, pPos, pPredicate, range, range, range, totalCount);
    }

    public boolean hasBlocks(Predicate<BlockState> pPredicate, int totalCount) {
        return predicateTotal(pPredicate, totalCount).checkBlocks();
    }

    public RitualChecker predicateTotal(Predicate<BlockState> pPredicate, int totalCount) {
        return setPredicate(pPredicate).setTotalCount(totalCount);
    }

    public RitualChecker setPredicate(Predicate<BlockState> pPredicate) {
        RitualChecker finder = this;
        finder.predicate = pPredicate;
        return finder;
    }

    public RitualChecker setTotalCount(int totalCount) {
        RitualChecker finder = this;
        finder.totalCount = totalCount;
        return finder;
    }

    public boolean isCorrectBlock(BlockState blockState, BlockPos blockPos) {
        return this.predicate.test(blockState);
    }

    public boolean checkBlocks() {
        int currentCount = 0;

        for (int i = -this.xRange; i <= this.xRange; ++i) {
            for (int j = -this.yRange; j <= this.yRange; ++j) {
                for (int k = -this.zRange; k <= this.zRange; ++k) {
                    BlockPos blockpos1 = this.initialPos.offset(i, j, k);
                    BlockState blockstate = this.level.getBlockState(blockpos1);
                    if (this.isCorrectBlock(blockstate, blockpos1)){
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= this.totalCount;
    }
}
