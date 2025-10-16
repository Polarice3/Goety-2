package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModCauldronInteraction;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class VoidCauldronBlock extends AbstractCauldronBlock {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    public VoidCauldronBlock(BlockBehaviour.Properties p_153498_) {
        super(p_153498_, ModCauldronInteraction.VOID);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    protected double getContentHeight(BlockState p_153528_) {
        return (6.0D + (double) p_153528_.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    public boolean isFull(BlockState p_153555_) {
        return p_153555_.getValue(LEVEL) == 3;
    }

    public void entityInside(BlockState p_153506_, Level p_153507_, BlockPos p_153508_, Entity p_153509_) {
        if (this.isEntityInsideContent(p_153506_, p_153508_, p_153509_) && p_153509_ instanceof LivingEntity livingEntity) {
            BlockFinder.voidedEffect(p_153507_, livingEntity);
        }
    }

    public int getAnalogOutputSignal(BlockState p_153530_, Level p_153531_, BlockPos p_153532_) {
        return p_153530_.getValue(LEVEL);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153549_) {
        p_153549_.add(LEVEL);
    }
}
