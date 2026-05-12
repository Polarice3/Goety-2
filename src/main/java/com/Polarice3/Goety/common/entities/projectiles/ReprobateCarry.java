package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ReprobateCarry extends ThrowableProjectile {
    private static final EntityDataAccessor<Boolean> BARREL = SynchedEntityData.defineId(ReprobateCarry.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> Y_ROT_VISUAL = SynchedEntityData.defineId(ReprobateCarry.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> X_ROT_VISUAL = SynchedEntityData.defineId(ReprobateCarry.class, EntityDataSerializers.FLOAT);

    public ReprobateCarry(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public ReprobateCarry(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.REPROBATE_CARRY.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public ReprobateCarry(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.REPROBATE_CARRY.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BARREL, false);
        this.entityData.define(Y_ROT_VISUAL, 0.0F);
        this.entityData.define(X_ROT_VISUAL, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsBarrel", this.isBarrel());
        compound.putFloat("VisualYRot", this.getYRotVisual());
        compound.putFloat("VisualXRot", this.getXRotVisual());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBarrel(compound.getBoolean("IsBarrel"));
        if (compound.contains("VisualYRot")) {
            this.setYRotVisual(compound.getFloat("VisualYRot"));
            this.setXRotVisual(compound.getFloat("VisualXRot"));
            this.setYRot(compound.getFloat("VisualYRot"));
            this.setXRot(compound.getFloat("VisualXRot"));
        }
    }

    public boolean isBarrel() {
        return this.entityData.get(BARREL);
    }

    public void setBarrel(boolean barrel) {
        this.entityData.set(BARREL, barrel);
    }

    public float getYRotVisual() {
        return this.entityData.get(Y_ROT_VISUAL);
    }

    public void setYRotVisual(float yRot) {
        this.entityData.set(Y_ROT_VISUAL, yRot);
    }

    public float getXRotVisual() {
        return this.entityData.get(X_ROT_VISUAL);
    }

    public void setXRotVisual(float xRot) {
        this.entityData.set(X_ROT_VISUAL, xRot);
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Vec3 vec3 = this.position();
            if (pResult instanceof EntityHitResult entityHitResult){
                vec3 = entityHitResult.getEntity().position();
            }
            this.blowUp(vec3);
        }
    }

    public void blowUp(Vec3 vec3) {
        AcidPool acidPool = new AcidPool(ModEntityType.ACID_POOL.get(), this.level);
        if (this.getOwner() instanceof LivingEntity livingEntity) {
            acidPool.setOwner(livingEntity);
        }
        acidPool.setColor(0xec67eb);
        acidPool.setWarmupColor(0xfdd4fb);
        acidPool.setRadius(2.0F);
        acidPool.setDamage(4.0F);
        acidPool.setDuration(60);
        acidPool.setPos(vec3);
        if (this.level.addFreshEntity(acidPool)) {
            this.playSound(SoundEvents.GLASS_BREAK, 2.0F, 1.0F);
            this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 1.0F);
            this.playSound(ModSounds.BREW_GAS_ALT.get(), 2.0F, 1.0F);
        }
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource p_19946_, float p_19947_) {
        if (p_19946_.getDirectEntity() != null) {
            if (this.canHitEntity(p_19946_.getDirectEntity())) {
                this.blowUp(this.position());
            }
        }
        return super.hurt(p_19946_, p_19947_);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 delta = this.getDeltaMovement();
        if (delta.lengthSqr() > 1.0E-7D) {
            float yRot = (float)(Mth.atan2(delta.x, delta.z) * (180D / Math.PI)) * -1.0F;
            float hDist = (float) Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            float xRot = (float)(Mth.atan2(delta.y, hDist) * (180D / Math.PI)) * -1.0F;
            this.setYRot(yRot);
            this.setXRot(xRot);
            if (!this.level.isClientSide) {
                this.setYRotVisual(yRot);
                this.setXRotVisual(xRot);
            }
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        Vec3 delta = this.getDeltaMovement();
        float yRot = (float)(Mth.atan2(delta.x, delta.z) * (180D / Math.PI)) * -1.0F;
        float hDist = (float) Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float xRot = (float)(Mth.atan2(delta.y, hDist) * (180D / Math.PI)) * -1.0F;
        this.setYRot(yRot);
        this.yRotO = yRot;
        this.setXRot(xRot);
        this.xRotO = xRot;
        this.setYRotVisual(yRot);
        this.setXRotVisual(xRot);
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
