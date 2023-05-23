package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.SculkConverterBlockEntity;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class SculkConverterBlock extends EnchanteableBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SculkConverterBlock() {
        super(Properties.of(Material.SCULK)
                .strength(3.0F, 3.0F)
                .sound(SoundType.SCULK_CATALYST)
                .lightLevel((p_220873_) -> 6));
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        if (pPlayer.getItemInHand(pHand).isEmpty()) {
            if (!pLevel.isClientSide) {
                BlockEntity tileentity = pLevel.getBlockEntity(pPos);
                if (tileentity instanceof SculkConverterBlockEntity converterBlockEntity) {
                    converterBlockEntity.findRelay();
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos) || pLevel.hasNeighborSignal(pPos.above());
        if (flag != pState.getValue(POWERED)) {
            if (flag) {
                BlockEntity tileentity = pLevel.getBlockEntity(pPos);
                if (tileentity instanceof SculkConverterBlockEntity converterBlockEntity) {
                    converterBlockEntity.activate();
                }
            }

            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(flag)), 3);
        }

    }

    public static void bloom(ServerLevel p_222095_, BlockPos p_222096_, BlockState p_222097_, RandomSource p_222098_) {
        p_222095_.scheduleTick(p_222096_, p_222097_.getBlock(), 8);
        ServerParticleUtil.spawnRedstoneParticles(p_222095_, p_222096_);
        p_222095_.playSound((Player)null, p_222096_, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + p_222098_.nextFloat() * 0.4F);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222115_) {
        p_222115_.add(LIT, POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SculkConverterBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_222100_, BlockState p_222101_, BlockEntityType<T> p_222102_) {
        return (world, pos, state, blockEntity) -> {
            if (!world.isClientSide) {
                if (blockEntity instanceof SculkConverterBlockEntity blockEntity1) {
                    blockEntity1.tick();
                }
            }
        };
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }
}
