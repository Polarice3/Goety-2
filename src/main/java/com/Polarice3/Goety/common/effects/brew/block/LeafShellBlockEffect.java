package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

public class LeafShellBlockEffect extends BrewEffect {
    public LeafShellBlockEffect() {
        super("leaf_shell", MobEffectCategory.NEUTRAL, 0x286240);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getHollowSphere(pPos, pAreaOfEffect + 3)){
            if (pLevel.getBlockState(blockPos).isAir() || BlockFinder.canBeReplaced(pLevel, blockPos)){
                pLevel.setBlockAndUpdate(blockPos, ModBlocks.HARDENED_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE));
            }
        }
    }
}
