package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.entities.OminousBrazierStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

public class OminousBrazierStatueBlock extends StatueBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public OminousBrazierStatueBlock() {
        super(ModBlocks.OminousStoneProperties()
                .lightLevel(((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0))
                .noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.TRUE));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        RandomSource randomsource = pLevel.getRandom();
        if (canLight(pState)){
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.TRUE));
        } else {
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.FALSE));
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean canLight(BlockState p_51322_) {
        return !p_51322_.getValue(LIT);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ? pState.setValue(LIT, pFacingState.getValue(LIT)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : pState;
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
        super.createBlockStateDefinition(p_58112_);
        p_58112_.add(LIT);
    }

    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        if (p_151997_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new OminousBrazierStatueBlockEntity(p_151996_, p_151997_);
        } else {
            return null;
        }
    }

    public void animateTick(BlockState p_222593_, Level p_222594_, BlockPos p_222595_, RandomSource p_222596_) {
        if (p_222593_.getValue(LIT) && p_222593_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            Direction facing = p_222593_.getValue(FACING);
            double d0 = (double)p_222595_.getX() + facing.getStepX() + 1.0D;
            double d1 = (double)p_222595_.getY() + 1.0D;
            double d2 = (double)p_222595_.getZ() + facing.getStepZ() + 1.0D;
            if (facing == Direction.WEST) {
                d2 = (double)p_222595_.getZ() + facing.getStepZ();
            } else if (facing == Direction.EAST) {
                d0 = (double)p_222595_.getX() + facing.getStepX();
            } else if (facing == Direction.SOUTH) {
                d0 = (double)p_222595_.getX() + facing.getStepX();
                d2 = (double)p_222595_.getZ() + facing.getStepZ();
            }
            p_222594_.addParticle(ModParticleTypes.BIG_FIRE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            p_222594_.addParticle(ModParticleTypes.BIG_FIRE_DROP.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
