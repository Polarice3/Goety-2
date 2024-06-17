package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.PartLiquidBlockEntity;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PartWaterBlockEffect extends BrewEffect {
    public PartWaterBlockEffect() {
        super("part_water", MobEffectCategory.NEUTRAL, 0x405ce2);
        this.duration = MathHelper.secondsToTicks(30);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pDuration, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 5)){
            if (pLevel.getFluidState(blockPos).is(FluidTags.WATER)){
                BlockState blockState = pLevel.getBlockState(blockPos);
                if (pLevel.setBlockAndUpdate(blockPos, ModBlocks.PART_LIQUID.get().defaultBlockState())) {
                    PartLiquidBlockEntity blockEntity = (PartLiquidBlockEntity)pLevel.getBlockEntity(blockPos);
                    if (blockEntity != null) {
                        blockEntity.setStats(blockState, 0, pDuration);
                    }
                }
            }
        }
    }
}
