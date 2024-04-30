package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.FollowMobClassGoal;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class Preacher extends HuntingIllagerEntity{
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Preacher.class, EntityDataSerializers.BYTE);
    public int healTick;
    public int healCool;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState healAnimationState = new AnimationState();

    public Preacher(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
        this.xpReward = 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PreacherHealGoal(this));
        this.goalSelector.addGoal(2, new FollowMobClassGoal(this, 1.0F, 8.0F,16.0F, (p_25278_) -> {
            return p_25278_ instanceof Raider && p_25278_.getClass() != this.getClass() && p_25278_.getHealth() < p_25278_.getMaxHealth() && !(p_25278_ instanceof Tormentor);
        }));
        this.goalSelector.addGoal(3, new FollowMobClassGoal(this, 1.0F, 8.0F,16.0F, (p_25278_) -> {
            return p_25278_ instanceof Raider && p_25278_.getClass() != this.getClass() && !(p_25278_ instanceof Tormentor);
        }));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
    }

    public void extraGoals(){
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PreacherHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PreacherHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Cooldown")){
            this.healCool = pCompound.getInt("Cooldown");
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Cooldown", this.healCool);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.healAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (!this.isHealing()) {
                    this.healAnimationState.stop();
                    if (!this.isMoving()) {
                        this.walkAnimationState.stop();
                        this.idleAnimationState.startIfStopped(this.tickCount);
                    } else {
                        this.idleAnimationState.stop();
                        this.walkAnimationState.startIfStopped(this.tickCount);
                    }
                } else {
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                }
            }
        }
        if (this.isHealing()) {
            ++this.healTick;
        }

        if (this.healTick > 20){
            this.setHealing(false);
            this.healTick = 0;
        }

        if (!this.level.isClientSide){
            if (this.healCool > 0){
                --this.healCool;
            }
        }
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isHealing() {
        return this.getFlag(1);
    }

    public void setHealing(boolean attacking) {
        this.setFlag(1, attacking);
        this.healTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.PREACHER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.PREACHER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.PREACHER_HURT.get();
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.PREACHER_CAST.get();
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.stopAllAnimations();
            this.healAnimationState.start(this.tickCount);
            this.setHealing(true);
        } else if (p_21375_ == 5){
            this.healTick = 0;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    public class PreacherHealGoal extends Goal{
        protected final Preacher preacher;
        public Raider target;

        public PreacherHealGoal(Preacher preacher){
            this.preacher = preacher;
        }

        @Override
        public boolean canUse() {
            this.findTarget();
            return this.target != null && this.preacher.healCool <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.preacher.healTick < 20;
        }

        protected AABB getTargetSearchArea(double p_26069_) {
            return this.preacher.getBoundingBox().inflate(p_26069_, 4.0D, p_26069_);
        }

        protected double getFollowDistance() {
            return this.preacher.getAttributeValue(Attributes.FOLLOW_RANGE);
        }

        protected void findTarget() {
            TargetingConditions targetConditions = TargetingConditions.forNonCombat().range(this.getFollowDistance()).selector(
                    p_26058_ -> p_26058_ instanceof Raider illager
                            && p_26058_.getClass() != this.preacher.getClass()
                            && p_26058_.getHealth() < p_26058_.getMaxHealth()
                            && p_26058_.isAlive()
                            && !(p_26058_ instanceof Tormentor)
                            && this.preacher.hasLineOfSight(illager)
                            && illager.getTarget() != this.preacher);
            this.target = this.preacher.level.getNearestEntity(this.preacher.level.getEntitiesOfClass(Raider.class, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> {
                return true;
            }), targetConditions, this.preacher, this.preacher.getX(), this.preacher.getEyeY(), this.preacher.getZ());
        }

        @Override
        public void start() {
            super.start();
            this.preacher.level.broadcastEntityEvent(this.preacher, (byte) 4);
            this.preacher.setHealing(true);
            this.preacher.playSound(this.preacher.getCastingSoundEvent(), this.preacher.getSoundVolume(), this.preacher.getVoicePitch());
        }

        @Override
        public void tick() {
            super.tick();
            this.preacher.navigation.stop();
            if (this.target != null && !this.target.isDeadOrDying()){
                MobUtil.instaLook(this.preacher, this.target);
                if (this.preacher.healTick == 10){
                    this.target.heal(AttributesConfig.PreacherHeal.get().floatValue());
                    if (!this.preacher.level.isClientSide) {
                        ServerLevel serverWorld = (ServerLevel) this.preacher.level;
                        for (int i = 0; i < serverWorld.random.nextInt(10) + 10; ++i) {
                            serverWorld.sendParticles(ModParticleTypes.HEAL_EFFECT_2.get(), this.target.getRandomX(1.5D), this.target.getRandomY(), this.target.getRandomZ(1.5D), 0, 0.0F, 1.0F, 0.0F, 1.0F);
                        }
                        this.target.playSound(ModSounds.HEAL_SPELL.get(), this.preacher.getSoundVolume(), this.preacher.getVoicePitch());
                    }
                    this.preacher.healCool = 20;
                }
            }
        }
    }
}
