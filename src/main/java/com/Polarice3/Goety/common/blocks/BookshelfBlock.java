package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BookshelfBlock extends Block {
    public float power = 1.0F;

    public BookshelfBlock(Properties p_49795_) {
        super(p_49795_);
    }

    public BookshelfBlock(Properties properties, float power){
        this(properties);
        this.power = power;
    }

    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return this.power;
    }
}
