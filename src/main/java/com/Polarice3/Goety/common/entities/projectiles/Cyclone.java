package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Cyclone extends AbstractCyclone {

    public Cyclone(EntityType<? extends AbstractCyclone> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
    }

    public Cyclone(Level level, LivingEntity shooter, double xPower, double yPower, double zPower) {
        super(ModEntityType.CYCLONE.get(), shooter, xPower, yPower, zPower, level);
    }

    public Cyclone(Level p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
        super(ModEntityType.CYCLONE.get(), p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.tickCount % 40 == 0) {
                this.playSound(ModSounds.FLIGHT.get(), 0.5F, 0.5F);
            }
        }
    }

    public void fakeRemove(double x, double y, double z){
        Cyclone cyclone = new Cyclone(this.level, this.getTrueOwner(), x, y, z);
        cyclone.setOwner(this.getTrueOwner());
        cyclone.setTarget(this.getTarget());
        cyclone.setLifespan(this.getLifespan());
        cyclone.setTotalLife(this.getTotalLife());
        cyclone.setSpun(this.getSpun());
        cyclone.setSize(this.getSize());
        cyclone.setDamage(this.getDamage());
        cyclone.setExtraDamage(this.getExtraDamage());
        cyclone.setPos(this.getX(), this.getY(), this.getZ());
        this.level.addFreshEntity(cyclone);
        this.remove();
    }

    public void remove() {
        if (!this.level.isClientSide){
            if (this.getLifespan() >= this.getTotalLife()) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for (int k = 0; k < 50; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.CLOUD, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
            }
        }
        this.discard();
    }

    public void hurtMobs(LivingEntity living){
        if (this.getDamage() > 0.0F) {
            if (this.getTrueOwner() != null) {
                living.hurt(this.damageSources().indirectMagic(this, this.getTrueOwner()), this.getDamage());
            } else {
                living.hurt(this.damageSources().magic(), this.getDamage());
            }
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CLOUD;
    }
}
