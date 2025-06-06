package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ToweringBlock extends Block {
    public static final IntegerProperty LEVEL = ModStateProperties.LEVEL_TOWER;

    public ToweringBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    /*public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isLoaded(pPos)) {
            return;
        }
        BlockState blockState = pLevel.getBlockState(pPos.below());
        if (blockState.getBlock() == this) {
            if (blockState.getValue(LEVEL) < this.levelLimit) {
                pLevel.setBlock(pPos, pState.setValue(LEVEL, blockState.getValue(LEVEL) + 1), 3);
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LEVEL, 0));
            }
        } else {
            if (pState.getValue(LEVEL) > 0){
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LEVEL, 0));
            }
        }
    }

    public BlockState updateShape(BlockState p_153152_, Direction p_153153_, BlockState p_153154_, LevelAccessor p_153155_, BlockPos p_153156_, BlockPos p_153157_) {
        if (p_153153_ == Direction.DOWN) {
            p_153155_.scheduleTick(p_153156_, this, 1);
        }

        return super.updateShape(p_153152_, p_153153_, p_153154_, p_153155_, p_153156_, p_153157_);
    }*/

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor levelaccessor = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockState = levelaccessor.getBlockState(blockpos.below());
        if (blockState.getBlock() == this){
            if (blockState.getValue(LEVEL) < 3){
                return this.defaultBlockState().setValue(LEVEL, blockState.getValue(LEVEL) + 1);
            }
        }
        return this.defaultBlockState().setValue(LEVEL, 0);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51778_) {
        p_51778_.add(LEVEL);
    }
}
