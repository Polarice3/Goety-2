package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class InfestBlockEffect extends BrewEffect {
    public InfestBlockEffect(int cap) {
        super("infest", cap, MobEffectCategory.HARMFUL, 0x778e9a);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 1)) {
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.STONE){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_STONE.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.STONE_BRICKS){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_STONE_BRICKS.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.CRACKED_STONE_BRICKS){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_CRACKED_STONE_BRICKS.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.MOSSY_STONE_BRICKS){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_MOSSY_STONE_BRICKS.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.CHISELED_STONE_BRICKS){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_CHISELED_STONE_BRICKS.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.COBBLESTONE){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_COBBLESTONE.defaultBlockState());
                } else if (blockState.getBlock() == Blocks.DEEPSLATE){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.INFESTED_DEEPSLATE.defaultBlockState());
                }
            }
        }
    }
}
