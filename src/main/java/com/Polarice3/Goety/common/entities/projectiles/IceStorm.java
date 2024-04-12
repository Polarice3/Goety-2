package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class IceStorm extends MagicProjectile {
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(IceStorm.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(IceStorm.class, EntityDataSerializers.FLOAT);
    public int duration = 0;
    public int range = 0;

    public IceStorm(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
        this.noPhysics = true;
    }

    public IceStorm(double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(ModEntityType.ICE_STORM.get(), p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
        this.noPhysics = true;
    }

    public IceStorm(LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(ModEntityType.ICE_STORM.get(), p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
        this.noPhysics = true;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0.0F);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Size", this.getSize());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
        pCompound.putInt("Duration", this.getDuration());
        pCompound.putInt("Range", this.getRange());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Size")) {
            this.setSize(pCompound.getFloat("Size"));
        }
        if (pCompound.contains("ExtraDamage")) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
        if (pCompound.contains("Duration")) {
            this.setDuration(pCompound.getInt("Duration"));
        }
        if (pCompound.contains("Range")) {
            this.setRange(pCompound.getInt("Range"));
        }
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + (0.2F * i)) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    public void setSize(float p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    public float getSize() {
        return this.entityData.get(ID_SIZE);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    protected float getInertia() {
        return 0.68F + Math.min(this.boltSpeed, 0.32F);
    }

    public void tick() {
        super.tick();
        Entity entity = this.getOwner();
        if (!this.level.isClientSide){
            if (this.level instanceof ServerLevel serverLevel){
                ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, this.getX(), (this.getY() - 0.5F)+ (this.getSize() / 4.0F), this.getZ(), (this.getSize() / 4.0F) + 0.5F);
                ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, this.getX(), (this.getY() + 1.0F) + (this.getSize() / 4.0F), this.getZ(), (this.getSize() / 4.0F) + 0.5F);
                ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, this.getX(), (this.getY() + 0.5F) + (this.getSize() / 4.0F), this.getZ(), (this.getSize() / 2.0F) + 1.0F);
            }
            this.setSize(this.getSize() + 0.05F);
            if (this.tickCount >= MathHelper.secondsToTicks(5) + (this.getRange() * 10)){
                this.discard();
            }
            float baseDamage = SpellConfig.IceStormDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0F))){
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    if (entity != null) {
                        if (!MobUtil.areAllies(entity, livingEntity) && livingEntity != entity) {
                            if (livingEntity.hurt(ModDamageSource.frostBreath(this, entity), baseDamage + this.getExtraDamage())){
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1 + this.getDuration())));
                            }
                        }
                    } else {
                        if (livingEntity.hurt(ModDamageSource.frostBreath(this, this), baseDamage + this.getExtraDamage())){
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1 + this.getDuration())));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
