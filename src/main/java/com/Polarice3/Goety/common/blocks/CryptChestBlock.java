package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.CryptChestBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class CryptChestBlock extends ModChestBlock {
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public CryptChestBlock() {
        super(ModBlocks.CryptStoneProperties(), ModBlockEntities.CRYPT_CHEST::get);
        this.registerDefaultState(this.defaultBlockState().setValue(LOCKED, Boolean.TRUE));
    }

    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return !pState.getValue(LOCKED) ? super.getDestroyProgress(pState, pPlayer, pLevel, pPos) : -1.0F;
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState p_53149_, Level p_53150_, BlockPos p_53151_, boolean p_53152_) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    public BlockState updateShape(BlockState p_51555_, Direction p_51556_, BlockState p_51557_, LevelAccessor p_51558_, BlockPos p_51559_, BlockPos p_51560_) {
        if (p_51555_.getValue(WATERLOGGED)) {
            p_51558_.scheduleTick(p_51559_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51558_));
        }

        return super.updateShape(p_51555_, p_51556_, p_51557_, p_51558_, p_51559_, p_51560_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_53128_) {
        FluidState fluidstate = p_53128_.getLevel().getFluidState(p_53128_.getClickedPos());
        return this.defaultBlockState().setValue(FACING, p_53128_.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER)).setValue(LOCKED, false);
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (pState.getValue(LOCKED)){
            if (pLevel.getDifficulty() == Difficulty.PEACEFUL){
                pLevel.setBlock(pPos, pState.setValue(LOCKED, Boolean.FALSE), 4);
            } else {
                pLevel.playSound(null, pPos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.CONSUME;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof CryptChestBlockEntity blockEntity) {
                pPlayer.openMenu(blockEntity);
                pPlayer.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(pPlayer, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    public BlockState mirror(BlockState p_53154_, Mirror p_53155_) {
        return p_53154_.rotate(p_53155_.getRotation(p_53154_.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51562_) {
        p_51562_.add(FACING, TYPE, WATERLOGGED, LOCKED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153064_, BlockState p_153065_) {
        return new CryptChestBlockEntity(p_153064_, p_153065_);
    }
}
