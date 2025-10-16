package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

public class EndMudCauldronBlock extends AbstractCauldronBlock {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    public EndMudCauldronBlock(Properties p_153498_) {
        super(p_153498_, ModCauldronInteraction.MUD);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    protected double getContentHeight(BlockState p_153528_) {
        return (6.0D + (double) p_153528_.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    public boolean isFull(BlockState p_153555_) {
        return p_153555_.getValue(LEVEL) == 3;
    }

    public void entityInside(BlockState p_153534_, Level p_153535_, BlockPos p_153536_, Entity p_153537_) {
        if (!p_153535_.isClientSide && p_153537_.isOnFire() && this.isEntityInsideContent(p_153534_, p_153536_, p_153537_)) {
            p_153537_.clearFire();
            if (p_153537_.mayInteract(p_153535_, p_153536_)) {
                this.handleEntityOnFireInside(p_153534_, p_153535_, p_153536_);
            }
        }

    }

    protected void handleEntityOnFireInside(BlockState p_153556_, Level p_153557_, BlockPos p_153558_) {
        lowerFillLevel(p_153556_, p_153557_, p_153558_);
    }

    public static void lowerFillLevel(BlockState p_153560_, Level p_153561_, BlockPos p_153562_) {
        int i = p_153560_.getValue(LEVEL) - 1;
        BlockState blockstate = i == 0 ? Blocks.CAULDRON.defaultBlockState() : p_153560_.setValue(LEVEL, Integer.valueOf(i));
        p_153561_.setBlockAndUpdate(p_153562_, blockstate);
        p_153561_.gameEvent(GameEvent.BLOCK_CHANGE, p_153562_, GameEvent.Context.of(blockstate));
    }

    public int getAnalogOutputSignal(BlockState p_153530_, Level p_153531_, BlockPos p_153532_) {
        return p_153530_.getValue(LEVEL);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153549_) {
        p_153549_.add(LEVEL);
    }
}
