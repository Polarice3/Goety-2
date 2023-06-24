package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MossifyBlockEffect extends BrewEffect {
    public MossifyBlockEffect() {
        super("mossify", MobEffectCategory.NEUTRAL, 0x647233);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 1)){
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (blockState.is(BlockTags.MOSS_REPLACEABLE) || blockState.is(Blocks.MOSS_BLOCK)) {
                    if (serverLevel.getBlockState(blockPos.above()).isAir()) {
                        CaveFeatures.MOSS_PATCH_BONEMEAL.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.random, blockPos.above());
                    }
                } else if (blockState.is(Blocks.COBBLESTONE)){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
                } else if (blockState.is(Blocks.COBBLESTONE_SLAB)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), blockState);
                } else if (blockState.is(Blocks.COBBLESTONE_STAIRS)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), blockState);
                } else if (blockState.is(Blocks.COBBLESTONE_WALL)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), blockState);
                } else if (blockState.is(Blocks.STONE_BRICKS)){
                    serverLevel.setBlockAndUpdate(blockPos, Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
                } else if (blockState.is(Blocks.STONE_BRICK_SLAB)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState(), blockState);
                } else if (blockState.is(Blocks.STONE_BRICK_STAIRS)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), blockState);
                } else if (blockState.is(Blocks.STONE_BRICK_WALL)){
                    BlockFinder.copyValues(serverLevel, blockPos, Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState(), blockState);
                }
            }
        }
    }
}
