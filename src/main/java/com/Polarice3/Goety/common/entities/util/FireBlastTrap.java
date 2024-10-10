package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FireBlastTrap extends Entity {
    private static final EntityDataAccessor<Boolean> IMMEDIATE = SynchedEntityData.defineId(FireBlastTrap.class, EntityDataSerializers.BOOLEAN);
    public LivingEntity owner;
    private UUID ownerUniqueId;
    private float extraDamage;
    private float areaOfEffect = 0.0F;
    private int burning = 0;

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
        this.extraDamage = compound.getFloat("ExtraDamage");
        this.areaOfEffect = compound.getFloat("AreaOfEffect");
        this.burning = compound.getInt("Burning");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
        compound.putFloat("ExtraDamage", this.extraDamage);
        compound.putFloat("AreaOfEffect", this.areaOfEffect);
        compound.putInt("Burning", this.burning);
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

    public void setBurning(int burning){
        this.burning = burning;
    }

    public int getBurning(){
        return this.burning;
    }

    public void setExtraDamage(float damage) {
        this.extraDamage = damage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setAreaOfEffect(float damage) {
        this.areaOfEffect = damage;
    }

    public float getAreaOfEffect() {
        return this.areaOfEffect;
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
        if (this.level instanceof ServerLevel serverLevel) {
            float area = this.getAreaOfEffect() / 2;
            float f = 1.5F + area;
            float f5 = (float) Math.PI * f * f;
            for (int k1 = 0; (float) k1 < f5; ++k1) {
                float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                float f8 = Mth.cos(f6) * f7;
                float f9 = Mth.sin(f6) * f7;
                serverLevel.sendParticles(ModParticleTypes.BURNING.get(), this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
            ColorUtil color = new ColorUtil(ChatFormatting.GOLD);
            ServerParticleUtil.windParticle(serverLevel, color, (f - 1.0F) + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());
            ServerParticleUtil.windParticle(serverLevel, color, f + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());

            if (this.tickCount == 20 || this.getImmediate()){
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                    }
                }
                List<Entity> targets = new ArrayList<>();
                float area0 = 1.0F + area;
                AABB aabb = this.getBoundingBox();
                AABB aabb1 = new AABB(aabb.minX - area0, aabb.minY - 1.0F, aabb.minZ - area0, aabb.maxX + area0, aabb.maxY + 1.0F, aabb.maxZ + area0);
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, aabb1)){
                    if (this.owner != null) {
                        if (entity != this.owner && !MobUtil.areAllies(entity, this.owner)) {
                            if (this.owner instanceof Mob mob && this.owner instanceof Enemy && entity instanceof Enemy) {
                                if (mob.getTarget() == entity) {
                                    targets.add(entity);
                                }
                            } else {
                                targets.add(entity);
                            }
                        }
                    } else {
                        targets.add(entity);
                    }
                }
                if (!targets.isEmpty()){
                    for (Entity entity : targets) {
                        if (this.owner instanceof Apostle) {
                            if (entity instanceof LivingEntity livingEntity) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 1200));
                            }
                            entity.hurt(ModDamageSource.magicFireBreath(this, this.owner), AttributesConfig.ApostleMagicDamage.get().floatValue());
                        } else {
                            if (this.owner != null){
                                float damage = 5.0F;
                                if (this.owner instanceof Mob mob && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null){
                                    damage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                                }
                                entity.hurt(ModDamageSource.magicFireBreath(this, this.owner), damage + this.getExtraDamage());
                            } else {
                                entity.hurt(DamageSource.MAGIC, 5.0F + this.getExtraDamage());
                            }
                        }
                        if (entity instanceof LivingEntity livingEntity) {
                            MobUtil.push(livingEntity, 0.0D, 1.0D, 0.0D, 0.5D);
                            if (this.burning > 0){
                                livingEntity.setSecondsOnFire(this.burning * 4);
                            }
                        }
                    }
                }
            }
        }
        if (this.tickCount > 20 || (this.getImmediate() && this.tickCount > 5)){
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        if (this.tickCount == 20 || (this.getImmediate() && this.tickCount == 5)){
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
        }
        if (this.owner != null){
            if (this.owner.isDeadOrDying() || this.owner.isRemoved()){
                this.discard();
            }
        }
        if (this.tickCount % 30 == 0){
            this.discard();
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
