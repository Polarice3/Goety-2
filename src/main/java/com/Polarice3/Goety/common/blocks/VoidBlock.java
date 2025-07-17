package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class VoidBlock extends Block {

    public VoidBlock() {
        super(Properties.of()
                .noCollission()
                .emissiveRendering(ModBlocks::always)
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F, 1200.0F)
                .sound(SoundType.HONEY_BLOCK));
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity
                && !CuriosFinder.hasVoidRobe(livingEntity)
                && !livingEntity.getType().is(ModTags.EntityTypes.VOID_TOUCHED_IMMUNE)) {
            livingEntity.makeStuckInBlock(pState, new Vec3(0.8D, 1.0D, 0.8D));
            BlockFinder.voidedEffect(pLevel, livingEntity);
        }
    }

    public RenderShape getRenderShape(BlockState p_222120_) {
        return RenderShape.MODEL;
    }
}
