package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MagicLightBlockEntity extends BlockEntity {
    public MagicLightBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.MAGIC_LIGHT.get(), p_155229_, p_155230_);
    }

    public void tick(ParticleOptions particleOptions){
        if(this.level != null){
            if (this.level instanceof ServerLevel serverLevel){
                if (this.level.getGameTime() % 10 == 0){
                    double d0 = (double)this.getBlockPos().getX() + 0.5D;
                    double d2 = (double)this.getBlockPos().getZ() + 0.5D;
                    serverLevel.sendParticles(particleOptions, d0, (double)this.getBlockPos().getY() + 0.5D, d2, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        }
    }
}
