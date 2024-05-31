package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NetherMeteor extends ExplosiveProjectile {

    public NetherMeteor(EntityType<? extends ExplosiveProjectile> type, Level world) {
        super(type, world);
    }

    public NetherMeteor(Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntityType.NETHER_METEOR.get(), x, y, z, accelX, accelY, accelZ, worldIn);
    }

    public NetherMeteor(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntityType.NETHER_METEOR.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    public void defaultExplosionAndDamage() {
        this.entityData.define(DATA_EXPLOSION, 4.0F);
        this.entityData.define(DATA_DAMAGE, 6.0F);
    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.level.addParticle(ParticleTypes.LARGE_SMOKE, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
        this.level.addParticle(ParticleTypes.FLAME, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            boolean flag = this.isDangerous();
            Level.ExplosionInteraction mode = flag ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), this.getExplosionPower(), flag, mode);
            this.discard();
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            entity.hurt(ModDamageSource.modFireball(this.getOwner(), this.level), this.getDamage() + this.getExtraDamage());
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }

        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() instanceof IOwned owner){
            if (pEntity instanceof IOwned entity){
                if (owner.getTrueOwner() == entity.getTrueOwner()){
                    return false;
                }
            }
            if (owner.getTrueOwner() == pEntity){
                return false;
            }
        }
        if (MobUtil.areAllies(this.getOwner(), pEntity)){
            return false;
        }
        return super.canHitEntity(pEntity);
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean ignoreExplosion(){
        return true;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    protected boolean shouldBurn() {
        return false;
    }
}
