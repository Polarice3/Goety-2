package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class JadeCrystalLamp extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 12.0D, 15.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_1 = Block.box(12.0D, 12.0D, 1.0D,
            15.0D, 16.0D, 4.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_2 = Block.box(1.0D, 12.0D, 1.0D,
            4.0D, 16.0D, 4.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_3 = Block.box(1.0D, 12.0D, 12.0D,
            4.0D, 16.0D, 15.0D);
    public static final VoxelShape SHAPE_BASE_PILLAR_4 = Block.box(12.0D, 12.0D, 12.0D,
            15.0D, 16.0D, 15.0D);
    public static final VoxelShape SHAPE_LOWER_CRYSTAL = Block.box(4.0D, 14.0D, 4.0D,
            12.0D, 16.0D, 12.0D);
    public static final VoxelShape SHAPE_UPPER_PILLAR_1 = Block.box(12.0D, 0.0D, 1.0D,
            15.0D, 4.0D, 4.0D);
    public static final VoxelShape SHAPE_UPPER_PILLAR_2 = Block.box(1.0D, 0.0D, 1.0D,
            4.0D, 4.0D, 4.0D);
    public static final VoxelShape SHAPE_UPPER_PILLAR_3 = Block.box(1.0D, 0.0D, 12.0D,
            4.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_UPPER_PILLAR_4 = Block.box(12.0D, 0.0D, 12.0D,
            15.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_UPPER_CRYSTAL = Block.box(4.0D, 0.0D, 4.0D,
            12.0D, 6.0D, 12.0D);
    public static final VoxelShape SHAPE_LOWER = Shapes.or(SHAPE_BASE, SHAPE_BASE_PILLAR_1, SHAPE_BASE_PILLAR_2, SHAPE_BASE_PILLAR_3, SHAPE_BASE_PILLAR_4, SHAPE_LOWER_CRYSTAL);
    public static final VoxelShape SHAPE_UPPER = Shapes.or(SHAPE_UPPER_PILLAR_1, SHAPE_UPPER_PILLAR_2, SHAPE_UPPER_PILLAR_3, SHAPE_UPPER_PILLAR_4, SHAPE_UPPER_CRYSTAL);

    public JadeCrystalLamp() {
        super(Block.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .lightLevel((i) -> 14)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, pLevel.getFluidState(pPos.above()).getType() == Fluids.WATER), 3);
    }

    public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
        if (!p_52755_.isClientSide && p_52758_.isCreative()) {
            BlockFinder.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
        }

        super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
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
            return this.defaultBlockState().setValue(WATERLOGGED, flag).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos blockpos = blockPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? super.canSurvive(blockState, level, blockPos) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
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
        pBuilder.add(WATERLOGGED, HALF);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return SHAPE_LOWER;
        } else if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return SHAPE_UPPER;
        }
        return Shapes.block();
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {
        if (p_220827_.getValue(HALF) == DoubleBlockHalf.UPPER) {
            Vec3 vec3 = new Vec3(0.5D, 0.25D, 0.5D);
            vec3 = vec3.add(p_220829_.getX(), p_220829_.getY(), p_220829_.getZ());
            for (int i = 0; i < 8; ++i) {
                vec3 = vec3.offsetRandom(p_220830_, 0.5F);
                p_220828_.addParticle(new MagicSmokeParticleOption(0xecf7f1, 0xecf7f1, p_220828_.getRandom().nextIntBetweenInclusive(20, 60), 0.2F), vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
