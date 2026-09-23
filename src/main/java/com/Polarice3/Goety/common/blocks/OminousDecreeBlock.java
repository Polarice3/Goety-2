package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.IPosHolder;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.blocks.entities.OminousDecreeBlockEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.block.OminousDecreeItem;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OminousDecreeBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OminousDecreeBlock() {
        super(ModBlocks.OminousStoneProperties()
                .noCollission()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(POWERED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState blockState) {
        return true;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
        if (pPlacer instanceof Player){
            if (pStack.getItem() instanceof OminousDecreeItem) {
                if (tileEntity instanceof OminousDecreeBlockEntity blockEntity) {
                    if (blockEntity.getTrueOwner() == null) {
                        blockEntity.setOwner(pPlacer);
                    }
                    if (IPosHolder.getPosition(pStack) != null) {
                        blockEntity.setDecreePos(IPosHolder.getPosition(pStack));
                    }
                    blockEntity.setRange(OminousDecreeItem.getRange(pStack));
                    blockEntity.setMode(OminousDecreeItem.getMode(pStack));
                    if (OminousDecreeItem.getEntityType(pStack) != null) {
                        blockEntity.setEntityType(OminousDecreeItem.getEntityType(pStack));
                    }
                }
            }
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (player.isCrouching()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            this.copyTags(itemStack, tileEntity);
        }
        return itemStack;
    }

    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        ItemStack itemStack = new ItemStack(this);
        this.copyTags(itemStack, pTe);
        popResource(pLevel, pPos, itemStack);
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
    }

    public void copyTags(ItemStack itemStack, BlockEntity blockEntity) {
        if (itemStack.getItem() instanceof OminousDecreeItem item) {
            if (blockEntity instanceof OminousDecreeBlockEntity decreeBlock) {
                GlobalPos globalPos = decreeBlock.getDecreePos();
                if (globalPos != null) {
                    item.addBlockEntityTags(itemStack, decreeBlock.getTrueOwner(), globalPos.dimension(), globalPos.pos(), null);
                }
                OminousDecreeItem.setRange(itemStack, decreeBlock.getRange());
                OminousDecreeItem.setMode(itemStack, decreeBlock.getMode().getName());

                if (decreeBlock.getEntityType() != null) {
                    OminousDecreeItem.addEntityTypeTags(itemStack, decreeBlock.getEntityType());
                }
            }
        }
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND){
            ItemStack itemStack = pPlayer.getMainHandItem();
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof OminousDecreeBlockEntity blockEntity) {
                if (blockEntity.getTrueOwner() == pPlayer) {
                    if (itemStack.is(ModItems.WAYSTONE.get()) && WaystoneItem.hasBlock(itemStack) && WaystoneItem.isSameDimension(blockEntity, itemStack)) {
                        blockEntity.setDecreePos(WaystoneItem.getPosition(itemStack));
                        pLevel.playSound(null, pPos, SoundEvents.ARROW_HIT_PLAYER, SoundSource.BLOCKS, 1.0F, 0.45F);
                        return InteractionResult.SUCCESS;
                    } else if (itemStack.is(ModItems.TAGLOCK_KIT.get()) && TaglockKit.hasEntity(itemStack) && TaglockKit.getEntity(itemStack) instanceof Mob mob && mob instanceof IServant servant && servant.getTrueOwner() == pPlayer) {
                        blockEntity.setEntityType(mob.getType());
                        pLevel.playSound(null, pPos, SoundEvents.ARROW_HIT_PLAYER, SoundSource.BLOCKS, 1.0F, 0.45F);
                        return InteractionResult.SUCCESS;
                    } else if (itemStack.getItem() instanceof IWand) {
                        boolean playSound = true;
                        if (pPlayer.isShiftKeyDown()) {
                            if (blockEntity.getRange() > 1.0D) {
                                blockEntity.setRange(blockEntity.getRange() - 1.0D);
                            } else {
                                playSound = false;
                            }
                        } else {
                            if (blockEntity.getRange() < 8.0D) {
                                blockEntity.setRange(blockEntity.getRange() + 1.0D);
                            } else {
                                playSound = false;
                            }
                        }
                        if (playSound) {
                            pLevel.playSound(null, pPos, ModSounds.TOCK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                        return InteractionResult.SUCCESS;
                    } else if (pPlayer.isShiftKeyDown() && blockEntity.getDecreePos() != null) {
                        blockEntity.setShowBlock(!blockEntity.isShowBlock());
                        pLevel.playSound(null, pPos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
                        return InteractionResult.SUCCESS;
                    } else {
                        blockEntity.cycleMode();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> box(2, 0, 0, 14, 16, 1);
            case NORTH -> box(2, 0, 15, 14, 16, 16);
            case EAST -> box(0, 0, 2, 1, 16, 14);
            case WEST -> box(15, 0, 2, 16, 16, 14);
        };
    }

    public boolean isSignalSource(BlockState p_55213_) {
        return p_55213_.hasProperty(POWERED);
    }

    public int getSignal(BlockState p_55208_, BlockGetter p_55209_, BlockPos p_55210_, Direction p_55211_) {
        return p_55208_.hasProperty(POWERED) && p_55208_.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        if (context.getClickedFace().getAxis() == Direction.Axis.Y) {
            return this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, flag);
        }
        return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, flag);
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new OminousDecreeBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof OminousDecreeBlockEntity blockEntity1) {
                blockEntity1.tick();
            }
        };
    }
}
