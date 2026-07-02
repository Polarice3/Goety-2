package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.TrailEffect;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmackStone extends SpellThrowableProjectile {
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(SmackStone.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_FIERY = SynchedEntityData.defineId(SmackStone.class, EntityDataSerializers.INT);
    public TrailEffect trail = new TrailEffect(0.2F, 6.0F);

    public SmackStone(EntityType<? extends SpellThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public SmackStone(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.SMACK_STONE.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public SmackStone(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.SMACK_STONE.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DAMAGE, SpellConfig.SmackStoneDamage.get().floatValue() * WandUtil.damageMultiply());
        this.entityData.define(DATA_FIERY, 0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putInt("Fiery", this.getFiery());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("Fiery")) {
            this.setFiery(pCompound.getInt("Fiery"));
        }
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    public int getFiery() {
        return this.entityData.get(DATA_FIERY);
    }

    public void setFiery(int fiery) {
        this.entityData.set(DATA_FIERY, fiery);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.tickCount > 2) {
                Vec3 oldPos = new Vec3(this.xOld, this.yOld + this.getBbHeight() / 2, this.zOld);
                this.trail.update(oldPos);
            }
        }
    }

    protected void onHitEntity(EntityHitResult p_37404_) {
        super.onHitEntity(p_37404_);
        if (!this.level.isClientSide) {
            Entity target = p_37404_.getEntity();
            Entity owner = this.getOwner();
            float baseDamage = this.getDamage();
            baseDamage += this.getExtraDamage();
            if (!target.onGround()) {
                baseDamage *= 2.0F;
            }
            boolean flag = target.hurt(target.damageSources().thrown(this, this.getOwner()), baseDamage);
            if (owner instanceof LivingEntity livingentity) {
                if (flag) {
                    if (target.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, target);
                    }
                }
            }

            if (flag && target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.PLUNGE.get(), MathHelper.secondsToTicks(5)));
                if (this.getFiery() > 0) {
                    livingEntity.setSecondsOnFire(5 * (this.getFiery() + 1));
                }
            }

        }
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (this.level instanceof ServerLevel serverLevel) {
            this.playSound(SoundEvents.BASALT_BREAK, 1.0F, 2.0F);
            BlockState blockState = Blocks.COBBLESTONE.defaultBlockState();
            if (this.getFiery() > 0) {
                blockState = Blocks.BLACKSTONE.defaultBlockState();
            }
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockState), this);
            this.discard();
        }

    }
}
