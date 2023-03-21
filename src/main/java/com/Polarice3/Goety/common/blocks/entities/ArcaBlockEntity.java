package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

public class ArcaBlockEntity extends OwnedBlockEntity {
    public int tickCount;
    private float activeRotation;

    public ArcaBlockEntity(BlockPos p_155301_, BlockState p_155302_) {
        super(ModBlockEntities.ARCA.get(), p_155301_, p_155302_);
    }

    public void tick() {
        ++this.tickCount;
        ++this.activeRotation;
        if (this.level instanceof ServerLevel world) {
            ChunkPos chunkPos = this.level.getChunkAt(this.worldPosition).getPos();
            if (!world.getForcedChunks().contains(chunkPos.toLong())) {
                world.setChunkForced(chunkPos.x, chunkPos.z, true);
                if (!world.isAreaLoaded(this.worldPosition, 2)) {
                    world.getChunkAt(this.worldPosition).setLoaded(true);
                }
            }
        }
    }

    public float getActiveRotation(float p_205036_1_) {
        return (activeRotation + p_205036_1_) * -0.0375F;
    }

    public void generateParticles() {
        if (SEHelper.getSESouls(getPlayer()) <= 0){
            return;
        }
        BlockPos blockpos = this.getBlockPos();

        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                double d0 = (double) blockpos.getX() + this.level.random.nextDouble();
                double d1 = (double) blockpos.getY() + this.level.random.nextDouble();
                double d2 = (double) blockpos.getZ() + this.level.random.nextDouble();
                for (int p = 0; p < 4; ++p) {
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
                }
            }
        }
    }
}
