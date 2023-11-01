package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class GrandLavaball extends ExplosiveProjectile{
    public float explosionPower = 1.0F;

    public GrandLavaball(EntityType<? extends ExplosiveProjectile> type, Level world) {
        super(type, world);
    }

    public GrandLavaball(Level worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(ModEntityType.GRAND_LAVABALL.get(), x, y, z, accelX, accelY, accelZ, worldIn);
    }

    public GrandLavaball(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntityType.GRAND_LAVABALL.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    public boolean defaultDangerous(){
        return true;
    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.level.addParticle(ParticleTypes.FLAME, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            boolean flag = this.isDangerous();
            Explosion.BlockInteraction mode = flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, flag, mode);
            this.discard();
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            entity.hurt(ModDamageSource.modFireball(this.getOwner(), this.level), 6.0F);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }

        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof Owned owned0 && this.getOwner() instanceof Owned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
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

    @Override
    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    @Override
    public float getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
