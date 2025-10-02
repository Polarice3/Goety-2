package com.Polarice3.Goety.common.entities.neutral.ender;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.SnarelingShot;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AbstractSnareling extends AbstractEnderling implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING = SynchedEntityData.defineId(AbstractSnareling.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractSnareling.class, EntityDataSerializers.INT);
    public int fleeCool;
    public int fleeTime;
    public int postShootTick;
    public boolean isFleeing;
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SHOOT = "shoot";
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();

    public AbstractSnareling(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new AttackGoal(1.2D));
        this.goalSelector.addGoal(4, new ShootGoal<>(this, 1.0D, 13));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !AbstractSnareling.this.isFleeing;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse()
                        && !AbstractSnareling.this.isFleeing;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SnarelingHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SnarelingArmor.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SnarelingDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SnarelingHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SnarelingArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SnarelingDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEB_SHOOTING, false);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FleeCool")) {
            this.fleeCool = compound.getInt("FleeCool");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FleeCool", this.fleeCool);
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
                        this.shootAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.shootAnimationState);
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
        } else if (Objects.equals(animation, SHOOT)){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.shootAnimationState);
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

    public void setWebShooting(boolean webShooting) {
        this.entityData.set(WEB_SHOOTING, webShooting);
    }

    public boolean isWebShooting() {
        return this.entityData.get(WEB_SHOOTING);
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractSnareling;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.SnarelingLimit.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SNARELING_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.SNARELING_HURT.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.SNARELING_STEP.get(), 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SNARELING_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.getCurrentAnimation() == 0, this.tickCount);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        /*if (this.getCurrentAnimation() == this.getAnimationState(IDLE) && !this.walkAnimation.isMoving()) {
            if (this.tickCount % 80 == 0) {
                this.playSound(ModSounds.SNARELING_AMBIENT_SWING.get(), 1.0F, this.getVoicePitch());
            }
        }*/
        if (this.getCurrentAnimation() == this.getAnimationState(ATTACK)) {
            if (this.tickCount % 20 == 0) {
                this.playSound(ModSounds.SNARELING_MELEE.get(), 1.0F, this.getVoicePitch());
            }
        }
        if (!this.level.isClientSide) {
            if (!this.isDeadOrDying()) {
                if (this.fleeCool > 0) {
                    --this.fleeCool;
                }
                if (!this.isWebShooting() && !this.isAggressive()) {
                    if (this.fleeCool <= 0) {
                        if (this.getTarget() != null) {
                            if (this.getTarget().distanceTo(this) <= 3.0D) {
                                this.isFleeing = true;
                                Vec3 vec3 = DefaultRandomPos.getPosAway(this, 16, 7, this.getTarget().position());
                                if (vec3 != null) {
                                    this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.2F);
                                }
                            }

                            if (this.isFleeing) {
                                ++this.fleeTime;

                                if (this.fleeTime >= MathHelper.secondsToTicks(5) || (this.getTarget().distanceTo(this) > 3.0D) && this.getNavigation().isDone()) {
                                    this.isFleeing = false;
                                    this.fleeTime = 0;
                                    this.fleeCool = MathHelper.secondsToTicks(5);
                                }
                            }
                        }
                    }
                    if (this.teleportCool <= 0) {
                        if (!this.isStaying()) {
                            if (this.getTarget() != null) {
                                if (this.getTarget().distanceTo(this) <= 3.5F) {
                                    int random = this.getRandom().nextIntBetweenInclusive(8, 12) * 2;
                                    if (this.teleportAway(this.getTarget(), random)) {
                                        this.teleportCool = MathHelper.secondsToTicks(6);
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.postShootTick > 0) {
                    if (this.getCurrentAnimation() == this.getAnimationState(SHOOT)) {
                        --this.postShootTick;
                        if (this.postShootTick <= 1) {
                            this.setAnimationState(IDLE);
                        }
                    } else {
                        this.postShootTick = 0;
                    }
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        SnarelingShot snowball = new SnarelingShot(this, this.level);
        Vec3 vec3 = p_33317_.getDeltaMovement();
        double d0 = p_33317_.getX() + vec3.x - this.getX();
        double d1 = p_33317_.getY() + vec3.y  - this.getEyeY();
        double d2 = p_33317_.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        snowball.setXRot(snowball.getXRot() - -20.0F);
        snowball.shoot(d0, d1 + d3 * 0.2D, d2, 0.95F, 8.0F);
        this.level.addFreshEntity(snowball);
    }

    public static class ShootGoal<T extends AbstractSnareling> extends Goal {
        private final T mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 25;
        private final double speedModifier;
        private final float attackRadius;

        public ShootGoal(T mob, double speed, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackRadius = attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null
                    && livingentity.isAlive()
                    && !this.mob.isFleeing) {
                this.target = livingentity;
                return !livingentity.hasEffect(GoetyEffects.TANGLED.get())
                        && this.mob.distanceTo(livingentity) > 3.0F;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(IDLE);
            this.target = null;
            this.attackTime = -1;
            this.mob.setWebShooting(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

                if (this.mob.distanceTo(this.target) <= this.attackRadius) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                --this.attackTime;
                if (this.attackTime == MathHelper.secondsToTicks(0.8F)) {
                    this.mob.setWebShooting(true);
                    this.mob.setAnimationState(SHOOT);
                    this.mob.postShootTick = 46;
                    this.mob.playSound(ModSounds.SNARELING_SHOOT.get(), 1.0F, this.mob.getVoicePitch());
                } else if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = MathHelper.secondsToTicks(6);
                    this.mob.setWebShooting(false);
                } else if (this.attackTime < 0) {
                    this.mob.setAnimationState(IDLE);
                    this.attackTime = MathHelper.secondsToTicks(6);
                    this.mob.setWebShooting(false);
                }
            }

        }
    }

    public class AttackGoal extends MeleeAttackGoal {

        public AttackGoal(double moveSpeed) {
            super(AbstractSnareling.this, moveSpeed, true);
        }

        @Override
        public boolean canUse() {
            return AbstractSnareling.this.getTarget() != null
                    && AbstractSnareling.this.getTarget().isAlive()
                    && AbstractSnareling.this.getTarget().hasEffect(GoetyEffects.TANGLED.get());
        }

        @Override
        public boolean canContinueToUse() {
            if (AbstractSnareling.this.getTarget() == null || AbstractSnareling.this.getTarget().isDeadOrDying() || !AbstractSnareling.this.getTarget().hasEffect(GoetyEffects.TANGLED.get())) {
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            AbstractSnareling.this.playSound(ModSounds.SNARELING_MELEE_VOCAL.get(), 1.0F, AbstractSnareling.this.getVoicePitch());
        }

        @Override
        public void stop() {
            super.stop();
            AbstractSnareling.this.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = AbstractSnareling.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(AbstractSnareling.this, target);
                this.checkAndPerformAttack(target, AbstractSnareling.this.distanceToSqr(target.getX(), target.getY(), target.getZ()));
            }
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr > d0) {
                Vec3 vec3 = enemy.position().offsetRandom(AbstractSnareling.this.getRandom(), 2.0F);
                if (AbstractSnareling.this.teleportCool <= 0) {
                    if (AbstractSnareling.this.randomTeleport(vec3.x, vec3.y, vec3.z, true)) {
                        if (!AbstractSnareling.this.level.isClientSide) {
                            ModNetwork.sendToALL(new SRepositionPacket(AbstractSnareling.this.getId(), AbstractSnareling.this.getX(), AbstractSnareling.this.getY(), AbstractSnareling.this.getZ()));
                        }
                        AbstractSnareling.this.teleportCool = MathHelper.secondsToTicks(6);
                    } else {
                        AbstractSnareling.this.getNavigation().moveTo(enemy, 1.2D);
                    }
                } else {
                    AbstractSnareling.this.getNavigation().moveTo(enemy, 1.2D);
                }
            } else {
                AbstractSnareling.this.getNavigation().stop();
                AbstractSnareling.this.setAnimationState(ATTACK);
                AbstractSnareling.this.doHurtTarget(enemy);
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return this.mob.getBbWidth() * 4.0F * this.mob.getBbWidth() * 4.0F + livingEntity.getBbWidth();
        }
    }
}
