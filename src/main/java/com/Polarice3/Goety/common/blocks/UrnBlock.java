package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.UrnBlockEntity;
import com.Polarice3.Goety.init.ModSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class UrnBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(2.0D, 0.0D, 2.0D,
            14.0D, 2.0D, 14.0D);
    public static final VoxelShape SHAPE_URN = Block.box(1.0D, 2.0D, 1.0D,
            15.0D, 13.0D, 15.0D);
    public static final VoxelShape SHAPE_TOP = Block.box(3.0D, 13.0D, 3.0D,
            13.0D, 16.0D, 13.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_URN, SHAPE_TOP);
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public UrnBlock() {
        super(Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .sound(ModSoundTypes.URN)
                .strength(0.1F, 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public BlockState updateShape(BlockState p_276307_, Direction p_276322_, BlockState p_276280_, LevelAccessor p_276320_, BlockPos p_276270_, BlockPos p_276312_) {
        if (p_276307_.getValue(WATERLOGGED)) {
            p_276320_.scheduleTick(p_276270_, Fluids.WATER, Fluids.WATER.getTickDelay(p_276320_));
        }

        return super.updateShape(p_276307_, p_276322_, p_276280_, p_276320_, p_276270_, p_276312_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_272711_) {
        FluidState fluidstate = p_272711_.getLevel().getFluidState(p_272711_.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_273169_) {
        p_273169_.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState p_272593_) {
        return p_272593_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_272593_);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @javax.annotation.Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof UrnBlockEntity urnBlock) {
                urnBlock.setCustomName(pStack.getHoverName());
            }
        }

    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof Container) {
                Containers.dropContents(pLevel, pPos, (Container)tileentity);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        super.onProjectileHit(pLevel, pState, pHit, pProjectile);
        BlockPos pos = pHit.getBlockPos();
        pLevel.destroyBlock(pos, true);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new UrnBlockEntity(p_153215_, p_153216_);
    }
}
