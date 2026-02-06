package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoidFlameBlock extends VoidBlock {
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public VoxelShape getShape(BlockState p_49274_, BlockGetter p_49275_, BlockPos p_49276_, CollisionContext p_49277_) {
        return DOWN_AABB;
    }

    public BlockState updateShape(BlockState p_56659_, Direction p_56660_, BlockState p_56661_, LevelAccessor p_56662_, BlockPos p_56663_, BlockPos p_56664_) {
        return this.canSurvive(p_56659_, p_56662_, p_56663_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    public boolean canSurvive(BlockState p_56655_, LevelReader p_56656_, BlockPos p_56657_) {
        return canSurviveOnBlock(p_56656_.getBlockState(p_56657_.below()));
    }

    public static boolean canSurviveOnBlock(BlockState p_154651_) {
        return p_154651_.is(ModTags.Blocks.VOID_BLOCKS);
    }

    public void animateTick(BlockState p_220763_, Level p_220764_, BlockPos p_220765_, RandomSource p_220766_) {
        if (p_220766_.nextInt(24) == 0) {
            p_220764_.playLocalSound((double)p_220765_.getX() + 0.5D, (double)p_220765_.getY() + 0.5D, (double)p_220765_.getZ() + 0.5D, ModSounds.VOID_FLAME.get(), SoundSource.BLOCKS, 0.1F + (p_220766_.nextFloat() * 0.2F), p_220766_.nextFloat() * 0.7F + 0.3F, false);
        }
    }
}
