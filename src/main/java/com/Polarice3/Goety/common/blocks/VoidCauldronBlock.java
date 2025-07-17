package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModCauldronInteraction;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class VoidCauldronBlock extends AbstractCauldronBlock {
    public VoidCauldronBlock(BlockBehaviour.Properties p_153498_) {
        super(p_153498_, ModCauldronInteraction.VOID);
    }

    protected double getContentHeight(BlockState p_153500_) {
        return 0.9375D;
    }

    public boolean isFull(BlockState p_153511_) {
        return true;
    }

    public void entityInside(BlockState p_153506_, Level p_153507_, BlockPos p_153508_, Entity p_153509_) {
        if (this.isEntityInsideContent(p_153506_, p_153508_, p_153509_) && p_153509_ instanceof LivingEntity livingEntity) {
            BlockFinder.voidedEffect(p_153507_, livingEntity);
        }

    }

    public int getAnalogOutputSignal(BlockState p_153502_, Level p_153503_, BlockPos p_153504_) {
        return 3;
    }
}
