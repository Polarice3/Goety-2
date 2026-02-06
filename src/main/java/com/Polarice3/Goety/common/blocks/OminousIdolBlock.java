package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.OminousIdolBlockEntity;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.block.OminousIdolBlockItem;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OminousIdolBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 8.0D, 15.0D);
    protected static final VoxelShape SHAPE_BOTTOM_BODY = Block.box(4.0D, 8.0D, 5.0D,
            12.0D, 16.0D, 11.0D);
    protected static final VoxelShape SHAPE_BOTTOM_BODY_WE = Block.box(5.0D, 8.0D, 4.0D,
            11.0D, 16.0D, 12.0D);
    protected static final VoxelShape BOTTOM_NORTH_AABB = Shapes.or(
            SHAPE_BASE, SHAPE_BOTTOM_BODY);
    protected static final VoxelShape BOTTOM_WEST_AABB = Shapes.or(
            SHAPE_BASE, SHAPE_BOTTOM_BODY_WE);
    protected static final VoxelShape BOTTOM_EAST_AABB = Shapes.or(
            SHAPE_BASE, SHAPE_BOTTOM_BODY_WE);
    protected static final VoxelShape BOTTOM_SOUTH_AABB = Shapes.or(
            SHAPE_BASE, SHAPE_BOTTOM_BODY);
    protected static final VoxelShape SHAPE_TOP_BODY = Block.box(4.0D, 0.0D, 5.0D,
            12.0D, 4.0D, 11.0D);
    protected static final VoxelShape SHAPE_TOP_BODY_WE = Block.box(5.0D, 0.0D, 4.0D,
            11.0D, 4.0D, 12.0D);
    protected static final VoxelShape SHAPE_HEAD = Block.box(4.0D, 4.0D, 4.0D,
            12.0D, 14.0D, 12.0D);
    protected static final VoxelShape TOP_NORTH_AABB = Shapes.or(
            SHAPE_TOP_BODY, SHAPE_HEAD);
    protected static final VoxelShape TOP_WEST_AABB = Shapes.or(
            SHAPE_TOP_BODY_WE, SHAPE_HEAD);
    protected static final VoxelShape TOP_EAST_AABB = Shapes.or(
            SHAPE_TOP_BODY_WE, SHAPE_HEAD);
    protected static final VoxelShape TOP_SOUTH_AABB = Shapes.or(
            SHAPE_TOP_BODY, SHAPE_HEAD);

    public OminousIdolBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 6.0F)
                .lightLevel((state) -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
                .noOcclusion()
                .dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE).setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return switch (pState.getValue(FACING)) {
                case SOUTH -> BOTTOM_SOUTH_AABB;
                default -> BOTTOM_NORTH_AABB;
                case WEST -> BOTTOM_WEST_AABB;
                case EAST -> BOTTOM_EAST_AABB;
            };
        } else if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return switch (pState.getValue(FACING)) {
                case SOUTH -> TOP_SOUTH_AABB;
                default -> TOP_NORTH_AABB;
                case WEST -> TOP_WEST_AABB;
                case EAST -> TOP_EAST_AABB;
            };
        }
        return Shapes.block();
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, pLevel.getFluidState(pPos.above()).getType() == Fluids.WATER), 3);
        BlockEntity tileentity = pLevel.getBlockEntity(pPos);
        if (pStack.getItem() instanceof OminousIdolBlockItem) {
            if (tileentity instanceof OminousIdolBlockEntity blockEntity) {
                if (pPlacer instanceof Player player) {
                    if (blockEntity.getTrueOwner() == null) {
                        blockEntity.setOwner(player);
                    }
                }
                if (!OminousIdolBlockItem.getIllagers(pStack, pLevel).isEmpty()){
                    for (RaiderServant illagerServant : OminousIdolBlockItem.getIllagers(pStack, pLevel)){
                        blockEntity.addIllager(illagerServant);
                    }
                }
            }
        }
    }

    public void playerWillDestroy(Level p_52755_, BlockPos p_52756_, BlockState p_52757_, Player p_52758_) {
        if (!p_52755_.isClientSide && p_52758_.isCreative()) {
            BlockFinder.preventCreativeDropFromBottomPart(p_52755_, p_52756_, p_52757_, p_52758_);
        }

        super.playerWillDestroy(p_52755_, p_52756_, p_52757_, p_52758_);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED, WATERLOGGED, FACING, HALF);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag).setValue(FACING, pContext.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER);
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

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos blockpos = blockPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? super.canSurvive(blockState, level, blockPos) : blockstate.is(this);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(POWERED, pFacingState.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    public BlockState rotate(BlockState p_51295_, Rotation p_51296_) {
        return p_51295_.setValue(FACING, p_51296_.rotate(p_51295_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_51292_, Mirror p_51293_) {
        return p_51292_.rotate(p_51293_.getRotation(p_51292_.getValue(FACING)));
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @javax.annotation.Nullable BlockEntity pTe, ItemStack pStack) {
        ItemStack itemStack = new ItemStack(this);
        if (pTe instanceof OminousIdolBlockEntity blockEntity) {
            this.setItemStackTags(itemStack, blockEntity);
        }
        popResource(pLevel, pPos, itemStack);
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            tileEntity = world.getBlockEntity(pos.below());
        }
        if (tileEntity instanceof OminousIdolBlockEntity blockEntity) {
            this.setItemStackTags(itemStack, blockEntity);
        }
        return itemStack;
    }

    public void setItemStackTags(ItemStack itemStack, OminousIdolBlockEntity tileEntity){
        if (!tileEntity.getUuids().isEmpty()){
            for (UUID uuid : tileEntity.getUuids()) {
                OminousIdolBlockItem.setUUIDs(itemStack, uuid);
            }
        } else {
            OminousIdolBlockItem.clearUUIDs(itemStack);
        }
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        if (p_151997_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new OminousIdolBlockEntity(p_151996_, p_151997_);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof OminousIdolBlockEntity blockEntity1) {
                blockEntity1.tick();
            }
        };
    }
}
