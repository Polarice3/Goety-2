package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class Pyroclast extends ThrowableProjectile {
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(Pyroclast.class, EntityDataSerializers.BOOLEAN);
    public float explosionPower = 1.5F;
    public int potency = 0;
    public int flaming = 0;

    public Pyroclast(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public Pyroclast(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.PYROCLAST.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public Pyroclast(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.PYROCLAST.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean pDangerous) {
        this.entityData.set(DATA_DANGEROUS, pDangerous);
    }

    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setPotency(int potency) {
        this.potency = potency;
    }

    public int getPotency() {
        return this.potency;
    }

    public void setFlaming(int flaming) {
        this.flaming = flaming;
    }

    public int getFlaming() {
        return this.flaming;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.getExplosionPower());
        pCompound.putInt("Potency",this.getPotency());
        pCompound.putInt("Flaming",this.getFlaming());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
        }
        if (pCompound.contains("Potency")){
            this.setPotency(pCompound.getInt("Potency"));
        }
        if (pCompound.contains("Flaming")){
            this.setFlaming(pCompound.getInt("Flaming"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            //So Pyroclasts will immediately explode on and damage mobs on standing in a volcano
            for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1.0F))){
                if (this.canHitEntity(entity)){
                    this.explode();
                    this.hitEntity(entity);
                }
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.level.addParticle(ParticleTypes.LARGE_SMOKE, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
        this.level.addParticle(ParticleTypes.FLAME, d0 + level.random.nextDouble()/2, d1 + 0.5D, d2 + level.random.nextDouble()/2, 0.0D, 0.0D, 0.0D);
    }

    public void explode(){
        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();
            boolean flag = this.isDangerous();
            if (owner instanceof Player){
                if (!SpellConfig.PyroclastGriefing.get()){
                    flag = false;
                }
            }
            this.level.explode(owner, this.getX(), this.getY(), this.getZ(), this.explosionPower, flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
            this.discard();
        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        this.explode();
    }

    public void hitEntity(Entity entity){
        Entity entity1 = this.getOwner();
        float damage = 6.0F;
        if (entity1 instanceof Player){
            damage = SpellConfig.PyroclastDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        }
        entity.hurt(ModDamageSource.modFireball(this.getOwner(), this.level), damage + this.potency);

        if (this.flaming != 0){
            entity.setSecondsOnFire(5 * this.flaming);
        }
        if (entity1 instanceof LivingEntity) {
            this.doEnchantDamageEffects((LivingEntity)entity1, entity);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide){
            Entity entity = pResult.getEntity();
            this.hitEntity(entity);
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            } else if (this.getOwner().isPassengerOfSameVehicle(pEntity)){
                return false;
            } else if (MobUtil.areAllies(pEntity, this.getOwner())){
                return false;
            } else if (pEntity instanceof Projectile projectile && projectile.getOwner() == this.getOwner()){
                return false;
            } else if (this.getOwner() instanceof IOwned ownedOwner){
                if (pEntity instanceof IOwned owned1){
                    if (ownedOwner.getTrueOwner() == owned1.getTrueOwner()){
                        return false;
                    } else if (ownedOwner.getTrueOwner() == owned1){
                        return false;
                    }
                } else if (ownedOwner.getTrueOwner() == pEntity){
                    return false;
                }
            }
        }
        return (!pEntity.isSpectator() && pEntity.isAlive() && pEntity.isPickable()) || this.getOwner() == null;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
