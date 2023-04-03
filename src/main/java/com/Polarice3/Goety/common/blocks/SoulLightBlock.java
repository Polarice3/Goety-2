package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class SoulLightBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

    public SoulLightBlock() {
        super(Properties.of(Material.DECORATION)
                .instabreak()
                .sound(SoundType.WOOL)
                .noCollission()
                .lightLevel((p_235443_0_) -> 10)
                .noOcclusion()
                .noLootTable()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(BlockStateProperties.WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, Boolean.TRUE), 3);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        double d0 = (double)pPos.getX() + 0.5D;
        double d1 = (double)pPos.getY() + 0.6D;
        double d2 = (double)pPos.getZ() + 0.5D;
        pLevel.addParticle(ModParticleTypes.BULLET_EFFECT.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        pLevel.addParticle(ModParticleTypes.SOUL_LIGHT_EFFECT.get(), d0, (double)pPos.getY() + 0.5D, d2, 0.0D, 0.0D, 0.0D);
    }
}
