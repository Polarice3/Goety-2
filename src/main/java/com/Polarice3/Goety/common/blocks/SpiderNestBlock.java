package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.SpiderNestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SpiderNestBlock extends TrainingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape UP_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape DOWN_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape NORTH_SHAPE = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(0.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);

    public SpiderNestBlock() {
        super(Properties.of()
                .mapColor(MapColor.TERRACOTTA_WHITE)
                .ignitedByLava()
                .randomTicks()
                .strength(0.2F, 3.0F)
                .sound(SoundType.WART_BLOCK)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    public void randomTick(BlockState p_221000_, ServerLevel p_221001_, BlockPos p_221002_, RandomSource p_221003_) {
        float f = p_221001_.getLightLevelDependentMagicValue(p_221002_);
        if (p_221001_.isAreaLoaded(p_221002_, 4) && f <= 0.5F) {
            if (p_221001_.random.nextInt(4) == 0) {
                Direction direction = Direction.getRandom(p_221003_);
                BlockPos blockPos = p_221002_.relative(direction);
                BlockState blockState = p_221001_.getBlockState(blockPos);
                if (blockState.isAir()) {
                    p_221001_.setBlock(blockPos, Blocks.COBWEB.defaultBlockState(), 2);
                }
            }
        }
    }

    public VoxelShape getShape(BlockState p_152021_, BlockGetter p_152022_, BlockPos p_152023_, CollisionContext p_152024_) {
        Direction direction = p_152021_.getValue(FACING);
        switch (direction) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case DOWN:
                return DOWN_SHAPE;
            case UP:
            default:
                return UP_SHAPE;
        }
    }

    public boolean canSurvive(BlockState p_152026_, LevelReader p_152027_, BlockPos p_152028_) {
        Direction direction = p_152026_.getValue(FACING);
        BlockPos blockpos = p_152028_.relative(direction.getOpposite());
        return p_152027_.getBlockState(blockpos).isFaceSturdy(p_152027_, blockpos, direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_152019_) {
        return this.defaultBlockState().setValue(FACING, p_152019_.getClickedFace());
    }

    public BlockState updateShape(BlockState p_152036_, Direction p_152037_, BlockState p_152038_, LevelAccessor p_152039_, BlockPos p_152040_, BlockPos p_152041_) {
        return p_152037_ == p_152036_.getValue(FACING).getOpposite() && !p_152036_.canSurvive(p_152039_, p_152040_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_152036_, p_152037_, p_152038_, p_152039_, p_152040_, p_152041_);
    }

    public BlockState rotate(BlockState p_152033_, Rotation p_152034_) {
        return p_152033_.setValue(FACING, p_152034_.rotate(p_152033_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_152030_, Mirror p_152031_) {
        return p_152030_.rotate(p_152031_.getRotation(p_152030_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152043_) {
        p_152043_.add(FACING);
    }

    public PushReaction getPistonPushReaction(BlockState p_152047_) {
        return PushReaction.DESTROY;
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SpiderNestBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return createTickerHelper(p_152757_, ModBlockEntities.SPIDER_NEST.get(), p_152755_.isClientSide ? SpiderNestBlockEntity::clientTick : SpiderNestBlockEntity::serverTick);
    }
}
