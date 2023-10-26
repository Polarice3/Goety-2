package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class DroughtBlockEffect extends BrewEffect {
    public DroughtBlockEffect() {
        super("drought", MobEffectCategory.NEUTRAL, 0x9d8f39);
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (this.removeWaterBreadthFirstSearch(pLevel, pPos, pAreaOfEffect)) {
            pLevel.levelEvent(2001, pPos, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level p_56808_, BlockPos p_56809_, int pAreaOfEffect) {
        BlockState spongeState = p_56808_.getBlockState(p_56809_);
        Direction[] ALL_DIRECTIONS = Direction.values();
        return BlockPos.breadthFirstTraversal(p_56809_, 6 * (pAreaOfEffect + 1), 65 * (pAreaOfEffect + 1), (p_277519_, p_277492_) -> {
            for(Direction direction : ALL_DIRECTIONS) {
                p_277492_.accept(p_277519_.relative(direction));
            }

        }, (p_279054_) -> {
            if (p_279054_.equals(p_56809_)) {
                return true;
            } else {
                BlockState blockstate = p_56808_.getBlockState(p_279054_);
                FluidState fluidstate = p_56808_.getFluidState(p_279054_);
                if (!spongeState.canBeHydrated(p_56808_, p_56809_, fluidstate, p_279054_)) {
                    return false;
                } else {
                    Block block = blockstate.getBlock();
                    if (block instanceof BucketPickup) {
                        BucketPickup bucketpickup = (BucketPickup)block;
                        if (!bucketpickup.pickupBlock(p_56808_, p_279054_, blockstate).isEmpty()) {
                            return true;
                        }
                    }

                    if (blockstate.getBlock() instanceof LiquidBlock) {
                        p_56808_.setBlock(p_279054_, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        if (!blockstate.is(Blocks.KELP) && !blockstate.is(Blocks.KELP_PLANT) && !blockstate.is(Blocks.SEAGRASS) && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockentity = blockstate.hasBlockEntity() ? p_56808_.getBlockEntity(p_279054_) : null;
                        Block.dropResources(blockstate, p_56808_, p_279054_, blockentity);
                        p_56808_.setBlock(p_279054_, Blocks.AIR.defaultBlockState(), 3);
                    }

                    return true;
                }
            }
        }) > 1;
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.getMobType() == MobType.WATER){
            pTarget.hurt(pTarget.damageSources().dryOut(), pAmplifier + 5.0F);
        }
    }
}
