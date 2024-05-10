package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.storm.DischargeSpell;
import com.Polarice3.Goety.common.magic.spells.storm.MonsoonSpell;
import com.Polarice3.Goety.common.magic.spells.storm.ShockingSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StormCaster extends HuntingIllagerEntity{
    private static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(StormCaster.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(StormCaster.class, EntityDataSerializers.INT);
    protected int castingTime;
    public static ItemStack STAFF = new ItemStack(ModItems.STORM_STAFF.get());
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState shockAnimationState = new AnimationState();
    public AnimationState cloudAnimationState = new AnimationState();
    public AnimationState dischargeAnimationState = new AnimationState();

    public StormCaster(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
        this.xpReward = 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(1, new DischargeSpellGoal());
        this.goalSelector.addGoal(2, new MonsoonSpellGoal());
        this.goalSelector.addGoal(3, new ShockGoal());
        this.goalSelector.addGoal(4, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D){
            @Override
            public boolean canUse() {
                return super.canUse() && StormCaster.this.getCurrentAnimation() < 3;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.StormCasterHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.StormCasterArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.StormCasterDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.StormCasterHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.StormCasterArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.StormCasterDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING_SPELL, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.castingTime = p_33732_.getInt("StormSpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        p_33734_.putInt("StormSpellTicks", this.castingTime);
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
        } else if (Objects.equals(animation, "walk")){
            return 2;
        } else if (Objects.equals(animation, "shock")){
            return 3;
        } else if (Objects.equals(animation, "cloud")){
            return 4;
        } else if (Objects.equals(animation, "discharge")){
            return 5;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.walkAnimationState);
        list.add(this.shockAnimationState);
        list.add(this.cloudAnimationState);
        list.add(this.dischargeAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
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
                        this.idleAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.shockAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.shockAnimationState);
                        break;
                    case 4:
                        this.cloudAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.cloudAnimationState);
                        break;
                    case 5:
                        this.dischargeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.dischargeAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(IS_CASTING_SPELL) > 0;
        } else {
            return this.castingTime > 0;
        }
    }

    public void setIsCastingSpell(int id) {
        this.entityData.set(IS_CASTING_SPELL, (byte)id);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.castingTime > 0) {
            --this.castingTime;
        }

    }

    protected int getSpellCastingTime() {
        return this.castingTime;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.STORM_CASTER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.STORM_CASTER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.STORM_CASTER_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (ModDamageSource.shockAttacks(p_34149_) || p_34149_.is(DamageTypeTags.IS_LIGHTNING)) {
            p_34150_ *= 0.15F;
        }

        if (p_34149_.is(DamageTypeTags.IS_LIGHTNING)){
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300));
        }

        return p_34150_;
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (this.getCurrentAnimation() < 3) {
                    if (this.isMoving()) {
                        this.setAnimationState("walk");
                    } else {
                        this.setAnimationState("idle");
                    }
                }
            }
        } else if (this.level instanceof ServerLevel serverLevel){
            ServerParticleUtil.circularParticles(serverLevel, ParticleTypes.CLOUD, this, 1.0F);
            if (serverLevel.random.nextInt(20) == 0){
                Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
                Vec3 vec31 = vec3.add(this.random.nextDouble(), 1.0D, this.random.nextDouble());
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 2));
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    public boolean isAlliedTo(Entity pEntity) {
        if (pEntity == this) {
            return true;
        } else if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof Vex vex && vex.getOwner() != null) {
            return this.isAlliedTo(vex.getOwner());
        } else if (pEntity instanceof LivingEntity && ((LivingEntity)pEntity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.WIND.get();
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.STORM_CASTER_CELEBRATE.get();
    }

    class ShockGoal extends Goal {
        protected int nextAttackTickCount;
        public int shockTime;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = StormCaster.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && StormCaster.this.hasLineOfSight(livingentity)) {
                if (StormCaster.this.isCastingSpell()) {
                    return false;
                } else {
                    return StormCaster.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = StormCaster.this.getTarget();
            return livingentity != null && livingentity.isAlive() && StormCaster.this.hasLineOfSight(livingentity) && this.shockTime > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            LivingEntity livingentity = StormCaster.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && StormCaster.this.hasLineOfSight(livingentity)){
                StormCaster.this.getLookControl().setLookAt(livingentity, (float)StormCaster.this.getMaxHeadYRot(), (float)StormCaster.this.getMaxHeadXRot());
            }
            StormCaster.this.navigation.stop();
            StormCaster.this.setAnimationState("shock");
            this.nextAttackTickCount = StormCaster.this.tickCount + 100;
            this.shockTime = 30;
        }

        @Override
        public void stop() {
            super.stop();
            StormCaster.this.setAnimationState("idle");
            this.shockTime = 0;
        }

        @Override
        public void tick() {
            super.tick();
            if (StormCaster.this.getTarget() != null) {
                StormCaster.this.getLookControl().setLookAt(StormCaster.this.getTarget(), (float)StormCaster.this.getMaxHeadYRot(), (float)StormCaster.this.getMaxHeadXRot());
                if (this.shockTime > 0) {
                    --this.shockTime;
                    if (this.shockTime < 20 && MobUtil.hasVisualLineOfSight(StormCaster.this, StormCaster.this.getTarget())) {
                        new ShockingSpell().SpellResult(StormCaster.this, STAFF);
                    }
                }
            }
        }
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
        }

        public boolean canUse() {
            return StormCaster.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            StormCaster.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            StormCaster.this.setIsCastingSpell(0);
            StormCaster.this.setAnimationState("idle");
        }

        public void tick() {
            if (StormCaster.this.getTarget() != null) {
                StormCaster.this.getLookControl().setLookAt(StormCaster.this.getTarget(), (float)StormCaster.this.getMaxHeadYRot(), (float)StormCaster.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class StormCasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = StormCaster.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && StormCaster.this.getCurrentAnimation() != StormCaster.this.getAnimationState("shock")) {
                if (StormCaster.this.isCastingSpell()) {
                    return false;
                } else {
                    return StormCaster.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = StormCaster.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            StormCaster.this.castingTime = this.getCastingTime();
            this.nextAttackTickCount = StormCaster.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                StormCaster.this.playSound(soundevent, 1.0F, 1.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            StormCaster.this.setAnimationState("idle");
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                StormCaster.this.playSound(StormCaster.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class MonsoonSpellGoal extends StormCasterUseSpellGoal {

        public void start() {
            super.start();
            StormCaster.this.setAnimationState("cloud");
        }

        @Override
        protected void performSpellCasting() {
            if (StormCaster.this.getTarget() != null){
                new MonsoonSpell().SpellResult(StormCaster.this, STAFF);
            }
        }

        @Override
        protected int getCastingTime() {
            return 22;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.STORM_CASTER_MONSOON.get();
        }
    }

    class DischargeSpellGoal extends StormCasterUseSpellGoal {

        @Override
        public boolean canUse() {
            return super.canUse() && StormCaster.this.getTarget() != null && StormCaster.this.getTarget().distanceTo(StormCaster.this) < 4.0D;
        }

        public void start() {
            super.start();
            StormCaster.this.setAnimationState("discharge");
        }

        @Override
        protected void performSpellCasting() {
            if (StormCaster.this.getTarget() != null) {
                new DischargeSpell().SpellResult(StormCaster.this, STAFF);
            }
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.STORM_CASTER_DISCHARGE.get();
        }

        protected int getCastWarmupTime() {
            return 10;
        }
    }
}
