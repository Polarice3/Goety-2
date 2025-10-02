package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.VoidShrineBlockEntity;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class VoidShrineBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 16.0D,
            16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_BASE_2 = Block.box(1.0D, 2.0D, 1.0D,
            15.0D, 4.0D, 15.0D);
    public static final VoxelShape SHAPE_PILLAR = Block.box(4.0D, 4.0D, 4.0D,
            12.0D, 12.0D, 12.0D);
    public static final VoxelShape SHAPE_TOP = Block.box(3.0D, 12.0D, 3.0D,
            13.0D, 14.0D, 13.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_BASE_2, SHAPE_PILLAR, SHAPE_TOP);
    public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public VoidShrineBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.STONE)
                .lightLevel((p_50847_) -> getScaledChargeLevel(p_50847_, 15))
                .strength(-1.0F, 3600000.0F)
                .noLootTable());
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, 0).setValue(TRIGGERED, Boolean.FALSE).setValue(OCCUPIED, Boolean.FALSE));
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getMainHandItem();
        if (itemstack.is(ModItems.VOID_SHARD.get()) && canBeCharged(state)) {
            charge(player, world, pos, state);
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            ItemStack heldItem = player.getItemInHand(hand);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VoidShrineBlockEntity pedestal) {
                pedestal.getCapability(ForgeCapabilities.ITEM_HANDLER, hit.getDirection()).ifPresent(handler -> {
                    ItemStack itemStack = handler.getStackInSlot(0);
                    if (!player.isShiftKeyDown() && !player.isCrouching() && !itemStack.isEmpty()) {
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(hand, handler.extractItem(0, 64, false));
                        } else {
                            ItemHandlerHelper.giveItemToPlayer(player, handler.extractItem(0, 64, false));
                        }
                        world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
                        pedestal.setChanged();
                    } else if (state.getValue(CHARGE) < 4) {
                        player.displayClientMessage(Component.translatable("info.goety.shrine.shard"), true);
                    }
                });
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof VoidShrineBlockEntity) {
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

    private static boolean canBeCharged(BlockState p_55895_) {
        return p_55895_.getValue(CHARGE) < 4;
    }

    public static void charge(@Nullable Entity p_270997_, Level p_270172_, BlockPos p_270534_, BlockState p_270661_) {
        BlockState blockstate = p_270661_.setValue(CHARGE, p_270661_.getValue(CHARGE) + 1);
        p_270172_.setBlock(p_270534_, blockstate, 3);
        p_270172_.gameEvent(GameEvent.BLOCK_CHANGE, p_270534_, GameEvent.Context.of(p_270997_, blockstate));
        p_270172_.playSound(null, (double)p_270534_.getX() + 0.5D, (double)p_270534_.getY() + 0.5D, (double)p_270534_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55886_) {
        p_55886_.add(CHARGE, TRIGGERED, OCCUPIED);
    }

    public boolean hasAnalogOutputSignal(BlockState p_55860_) {
        return true;
    }

    public static int getScaledChargeLevel(BlockState p_55862_, int p_55863_) {
        return Mth.floor((float)(p_55862_.getValue(CHARGE)) / 4.0F * (float)p_55863_);
    }

    public int getAnalogOutputSignal(BlockState p_55870_, Level p_55871_, BlockPos p_55872_) {
        return getScaledChargeLevel(p_55870_, 15);
    }

    public RenderShape getRenderShape(BlockState p_49753_) {
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

    public boolean isPathfindable(BlockState p_55865_, BlockGetter p_55866_, BlockPos p_55867_, PathComputationType p_55868_) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new VoidShrineBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof VoidShrineBlockEntity blockEntity1)
                blockEntity1.tick();
        };
    }
}
