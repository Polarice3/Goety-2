package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class DarkAltarBlock extends BaseEntityBlock implements IForgeBlock, SimpleWaterloggedBlock {
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 13.0D, 15.0D);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public DarkAltarBlock() {
        super(ModBlocks.ShadeStoneProperties()
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.FALSE).setValue(OCCUPIED, Boolean.FALSE));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        world.getBlockTicks().willTickThisTick(pos, this);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof DarkAltarBlockEntity darkAltarTile) {
            if (darkAltarTile.itemStackHandler.isPresent()){
                IItemHandler handler = darkAltarTile.itemStackHandler.orElseThrow(RuntimeException::new);
                ItemStack itemStack = handler.getStackInSlot(0);
                if (!itemStack.isEmpty()){
                    darkAltarTile.removeItem();
                    return InteractionResult.SUCCESS;
                } else if (!player.getItemInHand(hand).isEmpty()){
                    return darkAltarTile.activate(world, pos, player, hand,
                            hit.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
                } else if (player.isCrouching()) {
                    darkAltarTile.setShowArea(!darkAltarTile.isShowArea());
                    world.playSound(null, pos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof DarkAltarBlockEntity) {
                ((DarkAltarBlockEntity) tileentity).stopRitual(false);
                tileentity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    dropInventoryItems(tileentity.getLevel(), tileentity.getBlockPos(), handler);
                });
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public static void dropInventoryItems(Level worldIn, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
        }
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

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
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
        pBuilder.add(LIT, WATERLOGGED, OCCUPIED);
    }

    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new DarkAltarBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_222092_, T p_222093_) {
        return p_222093_ instanceof DarkAltarBlockEntity ? (DarkAltarBlockEntity)p_222093_ : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof DarkAltarBlockEntity altarBlock)
                altarBlock.tick();
        };
    }
}

/*
 * MIT License
 *
 * Copyright 2020 klikli-dev, MrRiegel, Sam Bassett
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */