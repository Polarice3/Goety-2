package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusGrowthItem extends ItemBase {

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();
        BlockState blockState = level.getBlockState(blockpos);
        if (stack.is(this)) {
            if (blockState.is(ModBlocks.END_ROCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_ROCK.get());
            } else if (blockState.is(ModBlocks.END_ROCK_BRICKS.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_ROCK_BRICKS.get());
            } else if (blockState.is(ModBlocks.END_BASALT.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_BASALT.get());
            } else if (blockState.is(ModBlocks.COBBLED_END_STONE_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_COBBLED_END_STONE_BLOCK.get());
            } else if (blockState.is(ModBlocks.END_STONE_TILES_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_STONE_TILES_BLOCK.get());
            } else if (blockState.is(ModBlocks.SMOOTH_END_STONE_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_SMOOTH_END_STONE_BLOCK.get());
            } else if (blockState.is(ModBlocks.GRASSY_SMOOTH_END_STONE_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_SMOOTH_END_STONE_HEAVY_BLOCK.get());
            } else if (blockState.is(Blocks.PURPUR_BLOCK)) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_PURPUR_BLOCK.get());
            } else if (blockState.is(Blocks.END_STONE_BRICKS)) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_STONE_BRICKS_BLOCK.get());
            } else if (blockState.is(ModBlocks.GRASSY_END_STONE_BRICKS_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.GRASSY_END_STONE_BRICKS_HEAVY_BLOCK.get());
            }
        }
        return super.useOn(pContext);
    }

    public InteractionResult grassify(Level level, ItemStack itemStack, BlockPos blockPos, Block toBlock) {
        level.setBlock(blockPos, toBlock.defaultBlockState(), 3);
        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        itemStack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
