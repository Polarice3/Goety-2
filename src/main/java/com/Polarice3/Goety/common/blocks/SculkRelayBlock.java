package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SculkRelayBlock extends Block {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D,
            16.0D, 8.0D, 16.0D);
    public static final VoxelShape SHAPE_POST = Block.box(2.0D, 8.0D, 2.0D,
            14.0D, 10.0D, 14.0D);
    public static final VoxelShape SHAPE_POST_2 = Block.box(4.0D, 10.0D, 4.0D,
            12.0D, 16.0D, 12.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_POST, SHAPE_POST_2);

    public SculkRelayBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(SoundType.SCULK));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222211_) {
        p_222211_.add(WATERLOGGED);
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return SHAPE;
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(BlockState p_222232_) {
        return true;
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return SHAPE;
    }

    public BlockState updateShape(BlockState p_222204_, Direction p_222205_, BlockState p_222206_, LevelAccessor p_222207_, BlockPos p_222208_, BlockPos p_222209_) {
        if (p_222204_.getValue(WATERLOGGED)) {
            p_222207_.scheduleTick(p_222208_, Fluids.WATER, Fluids.WATER.getTickDelay(p_222207_));
        }

        return super.updateShape(p_222204_, p_222205_, p_222206_, p_222207_, p_222208_, p_222209_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_222171_) {
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(p_222171_.getLevel().getFluidState(p_222171_.getClickedPos()).getType() == Fluids.WATER));
    }

    @SuppressWarnings("deprecation")
    public void spawnAfterBreak(BlockState p_222192_, ServerLevel p_222193_, BlockPos p_222194_, ItemStack p_222195_, boolean p_222196_) {
        super.spawnAfterBreak(p_222192_, p_222193_, p_222194_, p_222195_, p_222196_);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? 5 : 0;
    }
}
