package com.Polarice3.Goety.common.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class RitualChecker {
    public Level level;
    public BlockPos initialPos;
    public Predicate<BlockState> predicate;
    public Predicate<BlockEntity> blockEntityPredicate;
    public int xRange, yRange, zRange, totalCount;

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, Predicate<BlockEntity> blockEntityPredicate, int xRange, int yRange, int zRange, int totalCount) {
        this.level = pLevel;
        this.initialPos = pPos;
        this.predicate = pPredicate;
        this.blockEntityPredicate = blockEntityPredicate;
        this.xRange = xRange;
        this.yRange = yRange;
        this.zRange = zRange;
        this.totalCount = totalCount;
    }

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int xRange, int yRange, int zRange, int totalCount) {
        this(pLevel, pPos, pPredicate, blockEntity -> false, xRange, yRange, zRange, totalCount);
    }

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, Predicate<BlockEntity> blockEntityPredicate, int range, int totalCount) {
        this(pLevel, pPos, pPredicate, blockEntityPredicate, range, range, range, totalCount);
    }

    public RitualChecker(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int range, int totalCount) {
        this(pLevel, pPos, pPredicate, blockEntity -> false, range, range, range, totalCount);
    }

    public boolean hasBlocks(Predicate<BlockState> pPredicate, int totalCount) {
        return predicateTotal(pPredicate, totalCount).checkBlocks();
    }

    public boolean hasBlockEntities(Predicate<BlockEntity> pPredicate, int totalCount) {
        return predicateEntityTotal(pPredicate, totalCount).checkBlocks(true);
    }

    public RitualChecker predicateTotal(Predicate<BlockState> pPredicate, int totalCount) {
        return setPredicate(pPredicate).setTotalCount(totalCount);
    }

    public RitualChecker predicateEntityTotal(Predicate<BlockEntity> pPredicate, int totalCount) {
        return setBEPredicate(pPredicate).setTotalCount(totalCount);
    }

    public RitualChecker setPredicate(Predicate<BlockState> pPredicate) {
        RitualChecker finder = this;
        finder.predicate = pPredicate;
        return finder;
    }

    public RitualChecker setBEPredicate(Predicate<BlockEntity> pPredicate) {
        RitualChecker finder = this;
        finder.blockEntityPredicate = pPredicate;
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

    public boolean isCorrectBlockEntity(BlockEntity blockEntity, BlockPos blockPos) {
        return this.blockEntityPredicate.test(blockEntity);
    }

    public boolean checkBlocks() {
        return checkBlocks(false);
    }

    public boolean checkBlocks(boolean entity) {
        int currentCount = 0;

        for (int i = -this.xRange; i <= this.xRange; ++i) {
            for (int j = -this.yRange; j <= this.yRange; ++j) {
                for (int k = -this.zRange; k <= this.zRange; ++k) {
                    BlockPos blockpos1 = this.initialPos.offset(i, j, k);
                    BlockState blockstate = this.level.getBlockState(blockpos1);
                    BlockEntity blockEntity = this.level.getBlockEntity(blockpos1);
                    if (entity) {
                        if (this.isCorrectBlockEntity(blockEntity, blockpos1)) {
                            ++currentCount;
                        }
                    } else {
                        if (this.isCorrectBlock(blockstate, blockpos1)){
                            ++currentCount;
                        }
                    }
                }
            }
        }

        return currentCount >= this.totalCount;
    }

    public int getXRange() {
        return this.xRange;
    }

    public int getYRange() {
        return this.yRange;
    }

    public int getZRange() {
        return this.zRange;
    }
}
