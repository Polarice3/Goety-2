package com.Polarice3.Goety.common.entities.neutral.ender;

import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.EnderGoo;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
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

public class AbstractBlastling extends AbstractEnderling implements RangedAttackMob {
    public static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractBlastling.class, EntityDataSerializers.INT);
    public int fleeCool;
    public int fleeTime;
    public int postAttackTick;
    public int attackCoolTick;
    public boolean isFleeing;
    public static String IDLE = "idle";
    public static String PRE_ATTACK = "pre_attack";
    public static String ATTACK = "attack";
    public static String POST_ATTACK = "post_attack";
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState preAttackAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState postAttackAnimationState = new AnimationState();

    public AbstractBlastling(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new BlastlingRangedGoal(this));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !AbstractBlastling.this.isFleeing;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse()
                        && !AbstractBlastling.this.isFleeing;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlastlingHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BlastlingArmor.get())
                .add(Attributes.FOLLOW_RANGE, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlastlingDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BlastlingHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BlastlingArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BlastlingDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
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
                        this.preAttackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.preAttackAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 3:
                        this.postAttackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.postAttackAnimationState);
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
        if (Objects.equals(animation, PRE_ATTACK)){
            return 1;
        } else if (Objects.equals(animation, ATTACK)){
            return 2;
        } else if (Objects.equals(animation, POST_ATTACK)){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.preAttackAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.postAttackAnimationState);
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

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractBlastling;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.BlastlingLimit.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BLASTLING_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.BLASTLING_HURT.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.BLASTLING_STEP.get(), 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BLASTLING_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (!this.isHiding()) {
                ColorUtil colorUtil = new ColorUtil(0xf169e9);
                Vec3 vec3 = this.getHorizontalLookAngle();
                if (this.tickCount % 2 == 0) {
                    this.level.addParticle(new MagicSmokeParticle.Option(0xf4cdf6, 0xae00bc, 10 + this.level.getRandom().nextInt(10), 0.35F), this.getX() + (vec3.x / 2.0D), this.getEyeY() + 0.35F, this.getZ() + (vec3.z / 2.0D), 0.0D, 0.01D, 0.0D);
                    this.level.addParticle(ModParticleTypes.SMALL_STATION_CULT_SPELL.get(), this.getX() + (vec3.x / 2.0D), this.getEyeY() + 0.35F, this.getZ() + (vec3.z / 2.0D), colorUtil.red(), colorUtil.green(), colorUtil.blue());
                }
            }
            this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.getCurrentAnimation() == 0, this.tickCount);
        } else {
            if (this.fleeCool > 0) {
                --this.fleeCool;
            }

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
                        if (this.fleeTime >= MathHelper.secondsToTicks(5) || (this.getTarget().distanceTo(this) > 3.0D && this.getNavigation().isDone())) {
                            this.isFleeing = false;
                            this.fleeTime = 0;
                            this.fleeCool = MathHelper.secondsToTicks(5);
                        }
                    }
                }
            }

            if (this.getCurrentAnimation() == this.getAnimationState(POST_ATTACK)) {
                ++this.postAttackTick;
                if (this.postAttackTick >= MathHelper.secondsToTicks(0.5F)) {
                    this.setAnimationState(IDLE);
                    this.postAttackTick = 0;
                }
            } else {
                if (this.attackCoolTick > 0) {
                    --this.attackCoolTick;
                }
                if (this.postAttackTick > 0) {
                    this.postAttackTick = 0;
                }
            }

            if (this.teleportCool <= 0) {
                if (!this.isStaying()) {
                    if (!this.isHiding()) {
                        if (this.getTarget() != null && this.postAttackTick <= 0) {
                            if (this.getTarget().distanceTo(this) <= 10.0F) {
                                this.startHide();
                            }
                        }
                    }
                }
            }
        }
    }

    public int getHidingDuration() {
        return MathHelper.secondsToTicks(3);
    }

    public void startHide() {
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        this.setAnimationState(IDLE);
        super.startHide();
    }

    @Override
    public void teleportAfterHiding() {
        if (this.getTarget() != null) {
            this.teleportAway(this.getTarget(), 28.0D);
        } else {
            this.teleport(28.0D);
        }
        this.teleportCool = MathHelper.secondsToTicks(20);
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
    }

    //Based/Stolen from @Infamous-Misadventures codes: https://github.com/Infamous-Misadventures/Dungeons-Mobs/blob/1.19/src/main/java/com/infamous/dungeons_mobs/entities/ender/BlastlingEntity.java#L41
    private void shoot(boolean leftArm, LivingEntity target) {
        this.shoot(leftArm, target.getX(), target.getY(0.3333333333333333D), target.getZ());
        this.playSound(ModSounds.BLASTLING_SHOOT.get(), 1.0F, this.getVoicePitch());
    }

    private void shoot(boolean leftArm, double targetX, double targetY, double targetZ) {
        double d0 = this.getHeadX(leftArm);
        double d2 = this.getHeadZ(leftArm);
        double d3 = targetX - d0;
        double d4 = targetY - this.getEyeY();
        double d5 = targetZ - d2;
        EnderGoo enderGoo = new EnderGoo(this, d3, d4, d5, this.level);
        enderGoo.setPos(d0, this.getY(0.75D), d2);
        enderGoo.setYRot(this.getYRot());
        enderGoo.setXRot(this.getXRot());
        this.level.addFreshEntity(enderGoo);
    }

    private double getHeadX(boolean leftArm) {
        float f = (this.yBodyRot + (180.0F * (leftArm ? 0.0F : 0.75F))) * ((float) Math.PI / 180F);
        float f1 = Mth.cos(f);
        return this.getX() + (double) f1;
    }

    private double getHeadZ(boolean leftArm) {
        float f = (this.yBodyRot + (180.0F * (leftArm ? 0.0F : 0.75F))) * ((float) Math.PI / 180F);
        float f1 = Mth.sin(f);
        return this.getZ() + (double) f1;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public static class BlastlingRangedGoal extends Goal {
        private final AbstractBlastling mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 0;
        private int totalShots = 0;
        private int shots = 0;

        public BlastlingRangedGoal(AbstractBlastling mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (this.mob.isFleeing
                    || this.mob.mobHurtTime > 0
                    || this.mob.attackCoolTick > 0
                    || this.mob.isHiding()) {
                return false;
            } else if (livingentity != null
                    && livingentity.isAlive()
                    && livingentity.distanceTo(this.mob) > 3.0D
                    && this.mob.hasLineOfSight(livingentity)) {
                if (this.mob.teleportCool <= 0 && livingentity.distanceTo(this.mob) <= 10.0F) {
                    return false;
                } else {
                    this.totalShots = this.mob.getRandom().nextIntBetweenInclusive(1, 4) * 2;
                    this.target = livingentity;
                    return true;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.target.distanceTo(this.mob) > 3.0D
                    && this.mob.hasLineOfSight(this.target)
                    && !this.mob.isFleeing
                    && !this.mob.isHiding()
                    && this.shots < this.totalShots
                    && this.mob.mobHurtTime <= 0;
        }

        @Override
        public void start() {
            super.start();
            this.mob.setAnimationState(PRE_ATTACK);
            this.mob.playSound(ModSounds.BLASTLING_PRE_ATTACK.get());
        }

        public void stop() {
            super.stop();
            if (this.mob.getCurrentAnimation() == this.mob.getAnimationState(PRE_ATTACK) || this.mob.getCurrentAnimation() == this.mob.getAnimationState(ATTACK)) {
                this.mob.setAnimationState(POST_ATTACK);
            }
            if (this.shots >= this.totalShots) {
                this.mob.attackCoolTick = 20;
            }
            this.target = null;
            this.attackTime = 0;
            this.shots = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                ++this.attackTime;
                if (this.target.distanceTo(this.mob) > 10.0D) {
                    this.mob.getNavigation().moveTo(this.target, 1.0F);
                } else {
                    this.mob.getNavigation().stop();
                }
                MobUtil.instaLook(this.mob, this.target);
                if (this.attackTime > 20) {
                    if (this.mob.getCurrentAnimation() == this.mob.getAnimationState(PRE_ATTACK)) {
                        this.mob.setAnimationState(ATTACK);
                    }
                    if (this.attackTime % 5 == 0) {
                        ++this.shots;
                        this.mob.shoot(this.shots % 2 != 0, this.target);
                    }
                } else {
                    if (this.mob.getCurrentAnimation() != this.mob.getAnimationState(PRE_ATTACK)) {
                        this.mob.setAnimationState(PRE_ATTACK);
                    }
                }
            }
        }
    }
}
