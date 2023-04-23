package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdraftBlast extends Entity {
    public LivingEntity owner;
    private UUID ownerUniqueId;
    private float damage = SpellConfig.UpdraftBlastDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
    private float areaOfEffect = 0.0F;

    public UpdraftBlast(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public UpdraftBlast(Level worldIn, double x, double y, double z) {
        this(ModEntityType.UPDRAFT_BLAST.get(), worldIn);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
        this.damage = compound.getFloat("Damage");
        this.areaOfEffect = compound.getFloat("AreaOfEffect");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }
        compound.putFloat("Damage", this.damage);
        compound.putFloat("AreaOfEffect", this.areaOfEffect);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setAreaOfEffect(float damage) {
        this.areaOfEffect = damage;
    }

    public float getAreaOfEffect() {
        return this.areaOfEffect;
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
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 1, 0, 0, 0, 0);
            }
            if (this.tickCount % 20 == 0){
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                    }
                }
                List<LivingEntity> targets = new ArrayList<>();
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0F + area, 1.0F, 1.0F + area))){
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
                        livingEntity.hurt(ModDamageSource.windBlast(this, this.owner), this.damage);
                        MobUtil.push(livingEntity, 0, 1.0, 0);
                    }
                }
            }
        }
        if (this.tickCount == 1){
            this.playSound(ModSounds.UPDRAFT_BLAST.get(), 1.0F, 1.0F);
        }
        if (this.tickCount % 20 == 0){
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
