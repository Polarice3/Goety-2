package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FireBlastTrap extends Entity {
    private static final EntityDataAccessor<Boolean> IMMEDIATE = SynchedEntityData.defineId(FireBlastTrap.class, EntityDataSerializers.BOOLEAN);
    public LivingEntity owner;
    private UUID ownerUniqueId;

    public FireBlastTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public FireBlastTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.FIRE_BLAST_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IMMEDIATE, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void setImmediate(boolean immediate){
        this.entityData.set(IMMEDIATE, immediate);
    }

    public boolean getImmediate(){
        return this.entityData.get(IMMEDIATE);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverWorld) {
            float f = 1.5F;
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverWorld.sendParticles(ModParticleTypes.BURNING.get(), this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
            if (this.tickCount % 20 == 0 || this.getImmediate()){
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        serverWorld.sendParticles(ParticleTypes.FLAME, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                    }
                }
                List<LivingEntity> targets = new ArrayList<>();
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1), EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
                    if (this.owner != null) {
                        if (livingEntity != this.owner && !livingEntity.isAlliedTo(this.owner)) {
                            targets.add(livingEntity);
                        }
                    } else {
                        targets.add(livingEntity);
                    }
                }
                if (!targets.isEmpty()){
                    for (LivingEntity livingEntity : targets) {
                        if (!(livingEntity instanceof Apostle)) {
                            MobUtil.push(livingEntity, 0, 0.25, 0);
                            if (this.owner instanceof Apostle) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 1200));
                                livingEntity.hurt(DamageSource.indirectMagic(this, this.owner), AttributesConfig.ApostleMagicDamage.get().floatValue());
                            } else {
                                if (this.owner != null){
                                    livingEntity.hurt(DamageSource.indirectMagic(this, this.owner), 5.0F);
                                } else {
                                    livingEntity.hurt(DamageSource.MAGIC, 5.0F);
                                }
                            }
                            if (!livingEntity.fireImmune()){
                                livingEntity.setSecondsOnFire(8);
                            }
                        }
                    }
                }
            }
        }
        if (this.owner != null){
            if (this.owner.isDeadOrDying() || this.owner.isRemoved()){
                this.discard();
            }
        }
        if (this.tickCount % 20 == 0){
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
            this.discard();
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
