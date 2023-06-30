package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import java.util.concurrent.atomic.AtomicBoolean;

public class HarvestBlockEffect extends BrewEffect {
    public HarvestBlockEffect() {
        super("harvest", MobEffectCategory.NEUTRAL, 0x67a124);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 4)) {
                BlockState state = serverLevel.getBlockState(blockPos);
                if (isMatureAndValid(state)) {
                    if (resetState(state) != state){
                        serverLevel.setBlockAndUpdate(blockPos, resetState(state));
                        dropStacks(state, serverLevel, blockPos, pAmplifier, pSource);
                    } else {
                        serverLevel.destroyBlock(blockPos, true, pSource);
                    }
                }
            }
        }
    }

    private static boolean isMatureAndValid(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        } else {
            return state.getBlock() instanceof IPlantable;
        }
    }

    private static BlockState resetState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    private static void dropStacks(BlockState state, ServerLevel world, BlockPos pos, int pAmplifier, Entity entity) {
        Item oldPlant = state.getBlock().getCloneItemStack(world, pos, state).getItem();
        AtomicBoolean removedReplant = new AtomicBoolean(false);
        ItemStack fakeHoe = new ItemStack(Items.IRON_HOE);
        if (pAmplifier > 0) {
            fakeHoe.enchant(Enchantments.BLOCK_FORTUNE, pAmplifier);
        }

        Block.getDrops(state, world, pos, null, entity, fakeHoe).forEach(stack -> {
            if (!removedReplant.get() && stack.getItem() == oldPlant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant.set(true);
            }

            Block.popResource(world, pos, stack);
        });

        state.spawnAfterBreak(world, pos, fakeHoe, true);
    }
}
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
