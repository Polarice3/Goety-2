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
import net.minecraft.world.level.block.state.BlockState;

public class RedMossGrowthItem extends ItemBase {

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();
        BlockState blockState = level.getBlockState(blockpos);
        if (stack.is(this)) {
            if (blockState.is(ModBlocks.COBBLED_SILTSTONE_BLOCK.get())) {
                return this.grassify(level, stack, blockpos, ModBlocks.MOSSY_COBBLED_SILTSTONE_BLOCK.get());
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
