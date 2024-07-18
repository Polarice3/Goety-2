package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.LookAtTargetGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class PoisonQuillVine extends AbstractVine{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(PoisonQuillVine.class, EntityDataSerializers.INT);
    public int openTick = 0;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState burstAnimationState = new AnimationState();
    public AnimationState burrowAnimationState = new AnimationState();
    public AnimationState holdAnimationState = new AnimationState();
    public AnimationState openAnimationState = new AnimationState();
    public AnimationState closeAnimationState = new AnimationState();
    public AnimationState targetAnimationState = new AnimationState();
    public AnimationState docileAnimationState = new AnimationState();

    public PoisonQuillVine(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new QuillRangedGoal(this, 14.0F));
        this.goalSelector.addGoal(2, new LookAtTargetGoal(this, 16.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PoisonQuillVineHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PoisonQuillVineArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PoisonQuillVineDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PoisonQuillVineHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PoisonQuillVineArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PoisonQuillVineDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putInt("Animation", this.getCurrentAnimation());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("Animation")){
            this.setAnimationState(p_31474_.getInt("Animation"));
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")){
            return 1;
        } else if (Objects.equals(animation, "shoot")){
            return 2;
        } else if (Objects.equals(animation, "burst")){
            return 3;
        } else if (Objects.equals(animation, "burrow")){
            return 4;
        } else if (Objects.equals(animation, "hold")){
            return 5;
        } else if (Objects.equals(animation, "open")){
            return 6;
        } else if (Objects.equals(animation, "close")){
            return 7;
        } else if (Objects.equals(animation, "target")){
            return 8;
        } else if (Objects.equals(animation, "docile")){
            return 9;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.shootAnimationState);
        list.add(this.burstAnimationState);
        list.add(this.burrowAnimationState);
        list.add(this.holdAnimationState);
        list.add(this.openAnimationState);
        list.add(this.closeAnimationState);
        list.add(this.targetAnimationState);
        list.add(this.docileAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception) {
                state.stop();
            }
        }
    }

    public void stopNoneIdleAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception && state != this.idleAnimationState) {
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.shootAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.shootAnimationState);
                        break;
                    case 3:
                        this.burstAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burstAnimationState);
                        break;
                    case 4:
                        this.burrowAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.burrowAnimationState);
                        break;
                    case 5:
                        this.holdAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.holdAnimationState);
                        break;
                    case 6:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.openAnimationState.start(this.tickCount);
                        this.stopNoneIdleAnimation(this.openAnimationState);
                        break;
                    case 7:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.closeAnimationState.start(this.tickCount);
                        this.stopNoneIdleAnimation(this.closeAnimationState);
                        break;
                    case 8:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.targetAnimationState.startIfStopped(this.tickCount);
                        this.stopNoneIdleAnimation(this.targetAnimationState);
                        break;
                    case 9:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.docileAnimationState.startIfStopped(this.tickCount);
                        this.stopNoneIdleAnimation(this.docileAnimationState);
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.MOB_SUMMONED){
            this.setPerpetual(true);
        }
        this.setAnimationState("hold");
        return pSpawnData;
    }

    @Override
    public void initRotate(ServerLevelAccessor pLevel) {
    }

    @Nullable
    @Override
    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        if (level.isWaterAt(blockPos) || level.isWaterAt(blockPos.above())){
            return ModEntityType.POISON_ANEMONE.get();
        } else {
            return super.getVariant(level, blockPos);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.POISON_QUILL_VINE_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource p_21239_) {
        return ModSounds.POISON_QUILL_VINE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.POISON_QUILL_VINE_DEATH.get();
    }

    @Override
    protected SoundEvent getBurstSound() {
        return ModSounds.POISON_QUILL_VINE_BURST.get();
    }

    @Override
    protected SoundEvent getBurrowSound() {
        return ModSounds.POISON_QUILL_VINE_BURST.get();
    }

    protected SoundEvent getCloseSound(){
        return ModSounds.POISON_QUILL_VINE_CLOSE.get();
    }

    protected SoundEvent getOpenSound(){
        return ModSounds.POISON_QUILL_VINE_OPEN.get();
    }

    @Override
    public BlockState getState() {
        return Blocks.VINE.defaultBlockState();
    }

    public static float getEmergingTime(){
        return MathHelper.secondsToTicks(1.25F);
    }

    public boolean isEmerging() {
        return this.getAge() < getEmergingTime() && !this.isActivate();
    }

    public boolean isDescending(){
        return this.getAge() < getEmergingTime() && this.isActivate();
    }

    public void shootQuill(@NotNull LivingEntity target) {
        PoisonQuill quill = new PoisonQuill(this.level, this);
        Vec3 vector3d = this.getViewVector( 1.0F);
        quill.setPos(this.getX() + vector3d.x,
                this.getEyeY(),
                this.getZ() + vector3d.z);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - quill.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        quill.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0F);
        if (this.level.addFreshEntity(quill)){
            this.playSound(ModSounds.POISON_QUILL_VINE_SHOOT.get());
        }
    }

    protected AABB getTargetSearchArea() {
        return this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide){
            if (this.getCurrentAnimation() == this.getAnimationState("target")){
                if (this.getTarget() == null){
                    this.setAnimationState("close");
                    this.playSound(this.getCloseSound());
                }
            }
            if (this.getCurrentAnimation() == this.getAnimationState("close")){
                ++this.openTick;
                if (this.openTick > 20) {
                    this.openTick = 0;
                    this.setAnimationState("docile");
                }
            }
            if (this.getCurrentAnimation() == this.getAnimationState("docile")){
                if (this.getTarget() != null){
                    this.setAnimationState("open");
                    this.playSound(this.getOpenSound());
                }
            }
            if (this.getCurrentAnimation() == this.getAnimationState("open")){
                ++this.openTick;
                if (this.openTick > 20) {
                    this.openTick = 0;
                    this.setAnimationState("target");
                }
            }
        }
    }

    public void burst(){
        super.burst();
        if (this.activeTick < 25 && this.getCurrentAnimation() != this.getAnimationState("burst")) {
            this.setAnimationState("burst");
            this.playSound(this.getBurstSound(), 2.0F, 1.0F);
        } else if (this.activeTick == 25) {
            this.setAnimationState("docile");
        }
    }

    public void burrow(){
        super.burrow();
        this.setAnimationState("burrow");
        this.playSound(this.getBurrowSound(), 2.0F, 1.0F);
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = (this.getAge() / getEmergingTime());
        EntityDimensions entitydimensions = this.getType().getDimensions();
        return entitydimensions.scale(1, i);
    }

    public static class QuillRangedGoal extends Goal {
        private final PoisonQuillVine mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = 0;
        private final float attackRadius;

        public QuillRangedGoal(PoisonQuillVine mob, float attackRadius) {
            this.mob = mob;
            this.attackRadius = attackRadius;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.TARGET));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()
                    && this.mob.hasLineOfSight(livingentity)
                    && livingentity.distanceTo(this.mob) <= this.attackRadius) {
                this.target = livingentity;
                return !this.mob.isEmerging()
                        && this.mob.getCurrentAnimation() != this.mob.getAnimationState("hold")
                        && this.mob.getCurrentAnimation() != this.mob.getAnimationState("burrow");
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            this.attackTime = -20;
        }

        public void stop() {
            if (this.mob.getCurrentAnimation() != this.mob.getAnimationState("burrow")) {
                this.mob.setAnimationState("target");
            }
            this.target = null;
            this.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                ++this.attackTime;
                if (this.attackTime == 1) {
                    this.mob.setAnimationState("shoot");
                } else if (this.attackTime == 10) {
                    this.mob.shootQuill(this.target);
                } else if (this.attackTime >= 25){
                    this.mob.setAnimationState("target");
                    this.attackTime = -20;
                }
            }
        }
    }
}
