package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HalfJadeCrystalLamp extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_1 = Block.box(12.0D, 4.0D, 1.0D,
            15.0D, 12.0D, 4.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_2 = Block.box(1.0D, 4.0D, 1.0D,
            4.0D, 12.0D, 4.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_3 = Block.box(1.0D, 4.0D, 12.0D,
            4.0D, 12.0D, 15.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_4 = Block.box(12.0D, 4.0D, 12.0D,
            15.0D, 12.0D, 15.0D);
    public static final VoxelShape SHAPE_ORB = Block.box(4.0D, 6.0D, 4.0D,
            12.0D, 14.0D, 12.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_BASE_PILLAR_1, SHAPE_BASE_PILLAR_2, SHAPE_BASE_PILLAR_3, SHAPE_BASE_PILLAR_4, SHAPE_ORB);

    public HalfJadeCrystalLamp() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .lightLevel((i) -> 14)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        if (blockpos.getY() < iworld.getMaxBuildHeight() - 1 && iworld.getBlockState(blockpos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(WATERLOGGED, flag);
        } else {
            return null;
        }
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

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {
        Vec3 vec3 = new Vec3(0.5D, 0.75D, 0.5D);
        vec3 = vec3.add(p_220829_.getX(), p_220829_.getY(), p_220829_.getZ());
        for (int i = 0; i < 8; ++i) {
            vec3 = vec3.offsetRandom(p_220830_, 0.5F);
            p_220828_.addParticle(new MagicSmokeParticleOption(0xecf7f1, 0xecf7f1, p_220828_.getRandom().nextIntBetweenInclusive(20, 60), 0.2F), vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
        }
    }
}
