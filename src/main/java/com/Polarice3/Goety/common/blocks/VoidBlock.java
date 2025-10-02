package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class VoidBlock extends Block {

    public VoidBlock() {
        super(Properties.of()
                .noCollission()
                .emissiveRendering((state, world, pos) -> true)
                .lightLevel((state) -> 1)
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F, 1200.0F)
                .sound(SoundType.HONEY_BLOCK));
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        BlockFinder.voidedEffect(pLevel, pState, pEntity);
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }
}
