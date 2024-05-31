package com.Polarice3.Goety.common.entities.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public abstract class ExplosiveProjectile extends Fireball {
    private static final EntityDataAccessor<Boolean> DATA_UPGRADED = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_EXPLOSION = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(ExplosiveProjectile.class, EntityDataSerializers.INT);

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50166_1_, Level p_i50166_2_) {
        super(p_i50166_1_, p_i50166_2_);
    }

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50167_1_, double p_i50167_2_, double p_i50167_4_, double p_i50167_6_, double p_i50167_8_, double p_i50167_10_, double p_i50167_12_, Level p_i50167_14_) {
        super(p_i50167_1_, p_i50167_2_, p_i50167_4_, p_i50167_6_, p_i50167_8_, p_i50167_10_, p_i50167_12_, p_i50167_14_);
    }

    public ExplosiveProjectile(EntityType<? extends ExplosiveProjectile> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, Level p_i50168_9_) {
        super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_UPGRADED, false);
        this.entityData.define(DATA_DANGEROUS, this.defaultDangerous());
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
        this.entityData.define(DATA_FIERY, 0);
        this.defaultExplosionAndDamage();
    }

    public void defaultExplosionAndDamage(){
        this.entityData.define(DATA_EXPLOSION, 1.0F);
        this.entityData.define(DATA_DAMAGE, 6.0F);
    }

    public boolean defaultDangerous(){
        return false;
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean pDangerous) {
        this.entityData.set(DATA_DANGEROUS, pDangerous);
    }

    public float getExplosionPower() {
        return this.entityData.get(DATA_EXPLOSION);
    }

    public void setExplosionPower(float pExplosionPower){
        this.entityData.set(DATA_EXPLOSION, pExplosionPower);
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float extra) {
        this.entityData.set(DATA_EXTRA_DAMAGE, extra);
    }

    public int getFiery() {
        return this.entityData.get(DATA_FIERY);
    }

    public void setFiery(int fiery) {
        this.entityData.set(DATA_FIERY, fiery);
    }

    public boolean isUpgraded() {
        return this.entityData.get(DATA_UPGRADED);
    }

    public void setUpgraded(boolean upgraded) {
        this.entityData.set(DATA_UPGRADED, upgraded);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.getExplosionPower());
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
        pCompound.putInt("Fiery", this.getFiery());
        pCompound.putBoolean("Dangerous", this.isDangerous());
        pCompound.putBoolean("Upgraded", this.isUpgraded());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
        }
        if (pCompound.contains("Damage", 99)) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
        if (pCompound.contains("Fiery")) {
            this.setFiery(pCompound.getInt("Fiery"));
        }
        if (pCompound.contains("Dangerous")) {
            this.setDangerous(pCompound.getBoolean("Dangerous"));
        }
        if (pCompound.contains("Upgraded")) {
            this.setUpgraded(pCompound.getBoolean("Upgraded"));
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            if (this.isUpgraded()){
                this.level.broadcastEntityEvent(this, (byte) 120);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 121);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 120){
            this.setUpgraded(true);
        } else if (p_19882_ == 121) {
            this.setUpgraded(false);
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null && pEntity == this.getOwner()){
            return false;
        } else {
            return super.canHitEntity(pEntity);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
