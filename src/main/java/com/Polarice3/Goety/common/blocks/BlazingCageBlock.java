package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.BlazingCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.SpiderNestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;

import javax.annotation.Nullable;

public class BlazingCageBlock extends TrainingBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BlazingCageBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(50.0F, 1200.0F)
                .sound(SoundType.NETHERITE_BLOCK)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlazingCageBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return createTickerHelper(p_152757_, ModBlockEntities.BLAZING_CAGE.get(), p_152755_.isClientSide ? SpiderNestBlockEntity::clientTick : SpiderNestBlockEntity::serverTick);
    }
}
