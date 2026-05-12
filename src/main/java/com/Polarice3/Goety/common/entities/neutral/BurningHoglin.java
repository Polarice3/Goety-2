package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.ICharger;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.ai.ChargeGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class BurningHoglin extends Summoned implements ICharger, HoglinBase {
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(BurningHoglin.class, EntityDataSerializers.BOOLEAN);
    public float explosionPower = 5.0F;
    public int windUpTime = 20;

    public BurningHoglin(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BHChargeGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING, false);
    }

    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putFloat("ExplosionPower", this.getExplosionPower());
        p_31485_.putInt("WindUpTime", this.getWindUpTime());
    }

    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("ExplosionPower")) {
            this.setExplosionPower(p_31474_.getFloat("ExplosionPower"));
        }
        if (p_31474_.contains("WindUpTime")) {
            this.setWindUpTime(p_31474_.getInt("WindUpTime"));
        }
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof BurningHoglin;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.HoggingLimit.get();
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    public SoundEvent getAmbientSound() {
        return ModSounds.MCD_HOGLIN_AMBIENT.get();
    }

    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.MCD_HOGLIN_HURT.get();
    }

    public SoundEvent getDeathSound() {
        return ModSounds.MCD_HOGLIN_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.MCD_HOGLIN_STEP.get();
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        if (this.isCharging()) {
            this.playSound(ModSounds.MCD_HOGLIN_GALLOP.get(), 1.0F, 1.0F);
        } else {
            this.playSound(this.getStepSound(), 0.15F, 1.0F);
        }
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            if (this.isCharging()){
                this.walkAnimation.setSpeed(this.walkAnimation.speed() + 0.8F);
            }
        }
        if (this.level instanceof ServerLevel serverLevel) {
            double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
            serverLevel.sendParticles(ModParticleTypes.SMALL_FIRE.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, d0, d1, d2, 1.0F);
        }
    }

    @Override
    public void lifeSpanDamage() {
        this.explode(2.0F);
    }

    @Override
    public void dismiss() {
        this.explode(2.0F);
    }

    @Override
    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    @Override
    public void setCharging(boolean flag) {
        this.entityData.set(DATA_CHARGING, flag);
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
    }

    public int getWindUpTime() {
        return this.windUpTime;
    }

    public void setWindUpTime(int windUpTime) {
        this.windUpTime = windUpTime;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        this.explode(2.0F);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        this.explode(this.getExplosionPower());
        return true;
    }

    public void explode(float explosionPower) {
        if (!this.level.isClientSide) {
            if (this.level instanceof ServerLevel serverLevel){
                ColorUtil colorUtil = new ColorUtil(0xff8905);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, explosionPower, 1), this.getX(), this.getY(), this.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, explosionPower, 1), this.getX(), this.getY(), this.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                for (int i = 0; i < 32; ++i) {
                    ColorUtil colorUtil1 = new ColorUtil(0xac9b8f);
                    serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), this.getRandomX(1.0F), this.getRandomY(), this.getRandomZ(1.0F), 0, colorUtil1.red, colorUtil1.green, colorUtil1.blue, 1.0F);
                    serverLevel.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(), this.getRandomX(0.5F), this.getY(), this.getRandomZ(0.5F), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
            this.dead = true;
            new SpellExplosion(this.level, this.getTrueOwner() != null ? this.getTrueOwner() : this, this.damageSources().explosion(this, this.getTrueOwner() != null ? this.getTrueOwner() : this), this.getX(), this.getY(), this.getZ(), explosionPower, 0);
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, (1.0F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.2F) * 0.7F);
            CameraShake.cameraShake(this.level, this.position(), explosionPower * 2, 0.1F, 0, 20);
            this.discard();
        }
    }

    @Override
    public int getAttackAnimationRemainingTicks() {
        return 0;
    }

    public static class BHChargeGoal extends ChargeGoal {
        public static int CHARGE_TIME = 10;
        public BurningHoglin hoglin;
        public int chargeTick = 0;

        public BHChargeGoal(BurningHoglin mob) {
            super(mob, 2.0F, 4.0D, 32.0D, 0, 200);
            this.hoglin = mob;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.chargeTick >= CHARGE_TIME) {
                return false;
            }
            return this.windup > 0 || this.chargeTick > 0;
        }

        @Override
        public void start() {
            this.chargeTick = 0;
            this.hoglin.playSound(ModSounds.MCD_HOGLIN_GROWL.get());
            this.windup = this.hoglin.getWindUpTime();
            this.charger.setSprinting(true);
        }

        @Override
        public void tick() {
            --this.windup;
            if (this.windup > 0) {
                if (this.windup > 5) {
                    Vec3 chargePos = findChargePoint(this.charger, this.chargeTarget);
                    boolean canSeeTarget = this.charger.getSensing().hasLineOfSight(this.chargeTarget);
                    if (canSeeTarget) {
                        this.chargePos = chargePos;
                    }
                }
                if (this.windup == 1) {
                    this.hoglin.playSound(ModSounds.MCD_HOGLIN_SQUEEL.get());
                }
                if (this.charger instanceof ICharger chargeMob) {
                    chargeMob.setCharging(true);
                }
                MobUtil.instaLook(this.charger, new Vec3(this.chargeTarget.getX(), this.chargeTarget.getY() - 1, this.chargeTarget.getZ()));
            } else {
                ++this.chargeTick;
                this.charger.setDeltaMovement(this.chargePos.x, this.charger.getDeltaMovement().y, this.chargePos.z);
                if (this.charger.distanceToSqr(this.chargeTarget.getX(), this.chargeTarget.getY(), this.chargeTarget.getZ()) <= this.getAttackReachSqr(this.chargeTarget)) {
                    if (!this.hasAttacked) {
                        this.hasAttacked = true;
                        this.charger.doHurtTarget(this.chargeTarget);
                    }
                }
                if (this.hoglin.horizontalCollision) {
                    this.hoglin.explode(this.hoglin.getExplosionPower());
                }
            }
        }

        @Override
        public void stop() {
            this.hoglin.explode(this.hoglin.getExplosionPower());
            super.stop();
        }

        protected Vec3 findChargePoint(Entity attacker, Entity target) {
            double deltaX = attacker.getX() - target.getX();
            double deltaY = attacker.getY() - target.getY();
            double deltaZ = attacker.getZ() - target.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            float angle = 4.5F;
            double extraX = -(deltaX / distance * (double)angle * 0.4);
            double extraZ = -(deltaZ / distance * (double)angle * 0.4);
            return new Vec3(extraX, target.getY(), extraZ);
        }

        public double getAttackReachSqr(LivingEntity target) {
            return 6.0F + target.getBbWidth();
        }
    }
}
