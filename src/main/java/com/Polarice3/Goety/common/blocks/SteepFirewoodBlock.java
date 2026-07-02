package com.Polarice3.Goety.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SteepFirewoodBlock extends FirewoodBlock{
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public SteepFirewoodBlock(Properties p_51238_) {
        super(false, p_51238_);
    }

    public VoxelShape getShape(BlockState p_51309_, BlockGetter p_51310_, BlockPos p_51311_, CollisionContext p_51312_) {
        return SHAPE;
    }

    public void entityInside(BlockState p_51269_, Level p_51270_, BlockPos p_51271_, Entity p_49263_) {
        if (p_51269_.getValue(LIT)) {
            Blocks.FIRE.defaultBlockState().entityInside(p_51270_, p_51271_, p_49263_);
        } else {
            super.entityInside(p_51269_, p_51270_, p_51271_, p_49263_);
        }
    }

    public void animateTick(BlockState p_220918_, Level p_220919_, BlockPos p_220920_, RandomSource p_220921_) {
        if (p_220918_.getValue(LIT)) {
            if (p_220921_.nextInt(10) == 0) {
                p_220919_.playLocalSound((double)p_220920_.getX() + 0.5D, (double)p_220920_.getY() + 0.5D, (double)p_220920_.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220921_.nextFloat(), p_220921_.nextFloat() * 0.7F + 0.3F, false);
            }
            for(int i = 0; i < 3; ++i) {
                double d0 = (double)p_220920_.getX() + p_220921_.nextDouble();
                double d1 = (double)p_220920_.getY() + p_220921_.nextDouble() * 0.5D + 0.5D;
                double d2 = (double)p_220920_.getZ() + p_220921_.nextDouble();
                p_220919_.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
