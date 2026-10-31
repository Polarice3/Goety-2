package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.SealerBlockEntity;
import com.Polarice3.Goety.common.items.WaystoneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/SealerBlock.java
public class SealerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public SealerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player && level.getBlockEntity(pos) instanceof SealerBlockEntity sealer) {
            sealer.setPlayerPlaced(true);
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND){
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof SealerBlockEntity sealer) {
                if (pPlayer.getMainHandItem().isEmpty() && !sealer.getWaystone().isEmpty()) {
                    if (!pLevel.isClientSide) {
                        ItemStack waystone = sealer.getWaystone();
                        if (!pPlayer.addItem(waystone)) {
                            pPlayer.drop(waystone, false);
                        }
                        sealer.setWaystone(ItemStack.EMPTY);
                        pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F);
                    }
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                } else if (sealer.getWaystone().isEmpty() && pPlayer.getMainHandItem().getItem() instanceof WaystoneItem){
                    if (!pLevel.isClientSide){
                        sealer.setWaystone(pPlayer.getMainHandItem().split(1));
                        pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F);
                    }
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? -1.0F : super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getValue(LIT) ? 3600000.0F : super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SealerBlockEntity sealer) {
            sealer.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SealerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (world, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof SealerBlockEntity sealer) {
                sealer.tick();
            }
        };
    }
}
