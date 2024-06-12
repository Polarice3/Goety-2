package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FireTornado extends AbstractCyclone {

    public FireTornado(EntityType<? extends AbstractCyclone> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
        this.setSize(2.0F);
    }

    public FireTornado(Level level, LivingEntity shooter, double xPower, double yPower, double zPower) {
        super(ModEntityType.FIRE_TORNADO.get(), shooter, xPower, yPower, zPower, level);
        this.setSize(2.0F);
    }

    public FireTornado(Level p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
        super(ModEntityType.FIRE_TORNADO.get(), p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
        this.setSize(2.0F);
    }

    public void tick() {
        super.tick();
        if (this.isInPowderSnow){
            this.lifespan += 10;
        }
        if (this.tickCount % 20 == 0){
            this.playSound(ModSounds.FIRE_TORNADO_AMBIENT.get(), 1.0F, 0.5F);
        }
    }

    public void fakeRemove(double x, double y, double z){
        FireTornado fireTornadoEntity = new FireTornado(this.level, this.getTrueOwner(), x, y, z);
        fireTornadoEntity.setOwner(this.getTrueOwner());
        fireTornadoEntity.setTarget(this.getTarget());
        fireTornadoEntity.setLifespan(this.getLifespan());
        fireTornadoEntity.setTotalLife(this.getTotalLife());
        fireTornadoEntity.setSpun(this.getSpun());
        fireTornadoEntity.setSize(this.getSize());
        fireTornadoEntity.setPos(this.getX(), this.getY(), this.getZ());
        this.level.addFreshEntity(fireTornadoEntity);
        this.remove();
    }

    public void remove() {
        if (!this.level.isClientSide){
            if (this.getLifespan() >= this.getTotalLife()) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for (int k = 0; k < 200; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
                if (this.getTrueOwner() instanceof Apostle apostle){
                    apostle.setTornadoCoolDown(apostle.getTornadoCoolDown() + MathHelper.secondsToTicks(45));
                }
            }
        }
        this.discard();
    }

    public void hurtMobs(LivingEntity living){
        if (this.getTrueOwner() != null) {
            if (this.getTrueOwner() instanceof Apostle) {
                if (living.hurt(this.damageSources().indirectMagic(this, this.getTrueOwner()), AttributesConfig.ApostleMagicDamage.get().floatValue() / 1.5F)){
                    living.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 1200));
                }
            } else {
                living.hurt(this.damageSources().indirectMagic(this, this.getTrueOwner()), 4.0F);
            }
        } else {
            if (!living.fireImmune()) {
                living.hurt(this.damageSources().inFire(), 4.0F);
            }
        }
        if (living instanceof Player player){
            if (player.isBlocking()) {
                player.disableShield(true);
            }
        } else if (living instanceof AbstractHauntedArmor hauntedArmor){
            if (hauntedArmor.isBlocking()){
                hauntedArmor.disableShield(true);
            }
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
    }

}
