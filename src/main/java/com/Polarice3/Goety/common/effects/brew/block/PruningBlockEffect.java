package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;

public class PruningBlockEffect extends BrewEffect {
    public PruningBlockEffect(int soulCost) {
        super("pruning", soulCost, MobEffectCategory.NEUTRAL, 0x3a4c26);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getCubePos(pPos, pAreaOfEffect + 2)) {
            if (pLevel.getBlockState(blockPos).getBlock() instanceof LeavesBlock || pLevel.getBlockState(blockPos).is(BlockTags.LEAVES)){
                if (pAmplifier > 0){
                    if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !pLevel.restoringBlockSnapshots) {
                        ItemStack itemStack = new ItemStack(pLevel.getBlockState(blockPos).getBlock().asItem());
                        double d0 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                        double d1 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                        double d2 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                        ItemEntity itementity = new ItemEntity(pLevel, (double) blockPos.getX() + d0, (double) blockPos.getY() + d1, (double) blockPos.getZ() + d2, itemStack);
                        itementity.setDefaultPickUpDelay();
                        pLevel.addFreshEntity(itementity);
                    }
                    pLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                } else {
                    pLevel.destroyBlock(blockPos, true, pSource);
                }
            }
        }
    }
}
