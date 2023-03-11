package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

public class LightningTrap extends AbstractTrap {

    public LightningTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.CLOUD);
    }

    public LightningTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.LIGHTNING_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public float radius() {
        return 1.5F;
    }

    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel) this.level;
            float f = 1.5F;
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(this.getParticle(), this.getX() + (double) f8, this.getY() + 0.5F, this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
        }
        if (this.tickCount >= this.getDuration()) {
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightning.setPos(this.getX(),this.getY(),this.getZ());
            level.addFreshEntity(lightning);
            this.discard();
        }
    }
}
