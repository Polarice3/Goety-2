package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.Queue;

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

    private boolean removeWaterBreadthFirstSearch(Level pLevel, BlockPos pPos, int pAreaOfEffect) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pPos, 0));
        int i = 0;
        BlockState state = pLevel.getBlockState(pPos);

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = pLevel.getBlockState(blockpos1);
                FluidState fluidstate = pLevel.getFluidState(blockpos1);
                Material material = blockstate.getMaterial();
                if (state.canBeHydrated(pLevel, pPos, fluidstate, blockpos1)) {
                    if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(pLevel, blockpos1, blockstate).isEmpty()) {
                        ++i;
                        if (j < 6 * (pAreaOfEffect + 1)) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (blockstate.getBlock() instanceof LiquidBlock) {
                        pLevel.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6 * (pAreaOfEffect + 1)) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        BlockEntity blockentity = blockstate.hasBlockEntity() ? pLevel.getBlockEntity(blockpos1) : null;
                        Block.dropResources(blockstate, pLevel, blockpos1, blockentity);
                        pLevel.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6 * (pAreaOfEffect + 1)) {
                            queue.add(new Tuple<>(blockpos1, j + 1));
                        }
                    }
                }
            }

            if (i > (64 * (pAreaOfEffect + 1))) {
                break;
            }
        }

        return i > 0;
    }

    @Override
    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        if (pTarget.getMobType() == MobType.WATER){
            pTarget.hurt(DamageSource.DRY_OUT, pAmplifier + 5.0F);
        }
    }
}
