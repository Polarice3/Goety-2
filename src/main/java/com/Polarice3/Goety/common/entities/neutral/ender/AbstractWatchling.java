package com.Polarice3.Goety.common.entities.neutral.ender;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class AbstractWatchling extends AbstractEnderling {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(AbstractWatchling.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractWatchling.class, EntityDataSerializers.INT);
    private static final UUID SMASH_ATTACK_MODIFIER_UUID = UUID.fromString("b872b3d4-b0f4-4d1d-a0f4-80706dc0d252");
    private static final AttributeModifier SMASH_ATTACK_MODIFIER = new AttributeModifier(SMASH_ATTACK_MODIFIER_UUID, "Smash Attack Bonus", 0.65D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SMASH = "smash";
    public static String TELEPORT_OUT = "teleport_out";
    public static String TELEPORT_IN = "teleport_in";
    public int attackTick;
    public AnimationState blinkAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();
    public AnimationState teleportOutAnimationState = new AnimationState();
    public AnimationState teleportInAnimationState = new AnimationState();

    public AbstractWatchling(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AttackGoal(1.2F));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WatchlingHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.WatchlingArmor.get())
                .add(Attributes.FOLLOW_RANGE, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WatchlingDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WatchlingHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WatchlingArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WatchlingDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (ANIM_STATE.equals(p_33609_)) {
            if (this.level.isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        this.stopAllAnimations();
                        break;
                    case 1:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 2:
                        this.smashAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                    case 3:
                        this.teleportOutAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.teleportOutAnimationState);
                        break;
                    case 4:
                        this.teleportInAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.teleportInAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, ATTACK)){
            return 1;
        } else if (Objects.equals(animation, SMASH)){
            return 2;
        } else if (Objects.equals(animation, TELEPORT_OUT)){
            return 3;
        } else if (Objects.equals(animation, TELEPORT_IN)){
            return 4;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.smashAnimationState);
        animationStates.add(this.teleportOutAnimationState);
        animationStates.add(this.teleportInAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractWatchling;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.WatchlingLimit.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WATCHLING_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.WATCHLING_HURT.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.WATCHLING_STEP.get(), 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WATCHLING_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.blinkAnimationState.animateWhen(!this.isDeadOrDying(), this.tickCount);
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.getCurrentAnimation() == 0, this.tickCount);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.postTeleportTick <= 0) {
            if (this.getCurrentAnimation() == this.getAnimationState(TELEPORT_IN)) {
                this.setAnimationState(IDLE);
            }
        } else if (this.postTeleportTick == 13) {
            this.setAnimationState(TELEPORT_IN);
        }
        if (!this.level.isClientSide) {
            if (!this.isDeadOrDying()) {
                this.setAggressive(this.getTarget() != null);
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                } else if (this.isAttacking()) {
                    this.setAnimationState(IDLE);
                }
                AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
                if (instance != null) {
                    if (this.getCurrentAnimation() == this.getAnimationState(SMASH)) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            instance.removeModifier(SMASH_ATTACK_MODIFIER);
                            instance.addTransientModifier(SMASH_ATTACK_MODIFIER);
                        }
                    } else {
                        if (instance.hasModifier(SMASH_ATTACK_MODIFIER)) {
                            instance.removeModifier(SMASH_ATTACK_MODIFIER);
                        }
                    }
                }
                if (this.teleportCool <= 0) {
                    if (!this.isStaying()) {
                        if (!this.isHiding()) {
                            if (this.getTarget() != null) {
                                this.startHide();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isAttacking() {
        return this.getCurrentAnimation() == this.getAnimationState(ATTACK) || this.getCurrentAnimation() == this.getAnimationState(SMASH);
    }

    public int getHidingDuration() {
        return MathHelper.secondsToTicks(1);
    }

    public void startHide() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        this.setAnimationState(TELEPORT_OUT);
        if (this.preHidingTime >= 10) {
            super.startHide();
            this.preHidingTime = 0;
        }
    }

    @Override
    public void teleportAfterHiding() {
        if (this.getTarget() != null && this.teleportCool <= 0) {
            this.teleportTowards(this.getTarget(), 8.0D);
            this.teleportCool = MathHelper.secondsToTicks(12);
        }
    }

    @Override
    public void teleportIn() {
        super.teleportIn();
        this.postTeleportTick = 15;
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(AbstractWatchling.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return AbstractWatchling.this.getTarget() != null
                    && AbstractWatchling.this.getTarget().isAlive()
                    && !AbstractWatchling.this.isHiding()
                    && AbstractWatchling.this.postTeleportTick <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && !AbstractWatchling.this.isHiding()
                    && AbstractWatchling.this.postTeleportTick <= 0;
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            AbstractWatchling.this.setMeleeAttacking(false);
            AbstractWatchling.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = AbstractWatchling.this.getTarget();
            if (livingentity == null) {
                return;
            }

            AbstractWatchling.this.getLookControl().setLookAt(livingentity, AbstractWatchling.this.getMaxHeadYRot(), AbstractWatchling.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                AbstractWatchling.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, AbstractWatchling.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            boolean smash = AbstractWatchling.this.random.nextBoolean();

            if (!AbstractWatchling.this.isMeleeAttacking() && distToEnemySqr <= d0){
                AbstractWatchling.this.setMeleeAttacking(true);
                if (smash) {
                    AbstractWatchling.this.setAnimationState(SMASH);
                } else {
                    AbstractWatchling.this.setAnimationState(ATTACK);
                }
            }
            if (AbstractWatchling.this.isMeleeAttacking()) {
                float seconds = AbstractWatchling.this.getCurrentAnimation() == AbstractWatchling.this.getAnimationState(ATTACK) ? 1.7F : 1.9F;
                if (AbstractWatchling.this.attackTick < MathHelper.secondsToTicks(seconds)) {
                    MobUtil.instaLook(AbstractWatchling.this, enemy);
                    if (AbstractWatchling.this.getCurrentAnimation() == AbstractWatchling.this.getAnimationState(ATTACK)) {
                        if (AbstractWatchling.this.attackTick == 12) {
                            AbstractWatchling.this.playSound(ModSounds.WATCHLING_ATTACK.get(), AbstractWatchling.this.getSoundVolume(), AbstractWatchling.this.getVoicePitch());
                            this.massiveSweep(AbstractWatchling.this, 4.0D, 30.0D);
                        }
                    } else if (AbstractWatchling.this.getCurrentAnimation() == AbstractWatchling.this.getAnimationState(SMASH)) {
                        if (AbstractWatchling.this.attackTick == 18) {
                            AbstractWatchling.this.playSound(ModSounds.WATCHLING_SMASH.get(), AbstractWatchling.this.getSoundVolume(), AbstractWatchling.this.getVoicePitch());
                            if (distToEnemySqr <= d0) {
                                AbstractWatchling.this.doHurtTarget(enemy);
                            }
                        }
                    }
                } else {
                    AbstractWatchling.this.setMeleeAttacking(false);
                }
            }
        }

        public void massiveSweep(LivingEntity source, double range, double arc){
            List<LivingEntity> hits = MobUtil.getAttackableLivingEntitiesNearby(source, range, 1.0F, range, range);
            for (LivingEntity target : hits) {
                float targetAngle = (float) ((Math.atan2(target.getZ() - source.getZ(), target.getX() - source.getX()) * (180 / Math.PI) - 90) % 360);
                float attackAngle = source.yBodyRot % 360;
                if (targetAngle < 0) {
                    targetAngle += 360;
                }
                if (attackAngle < 0) {
                    attackAngle += 360;
                }
                float relativeAngle = targetAngle - attackAngle;
                float hitDistance = (float) Math.sqrt((target.getZ() - source.getZ()) * (target.getZ() - source.getZ()) + (target.getX() - source.getX()) * (target.getX() - source.getX())) - target.getBbWidth() / 2f;
                if (hitDistance <= range && (relativeAngle <= arc / 2 && relativeAngle >= -arc / 2) || (relativeAngle >= 360 - arc / 2 || relativeAngle <= -360 + arc / 2)) {
                    AbstractWatchling.this.doHurtTarget(target);
                }
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return this.mob.getBbWidth() * 4.0F * this.mob.getBbWidth() * 4.0F + livingEntity.getBbWidth();
        }
    }
}
