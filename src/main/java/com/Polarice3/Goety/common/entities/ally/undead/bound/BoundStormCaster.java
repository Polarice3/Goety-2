package com.Polarice3.Goety.common.entities.ally.undead.bound;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.storm.DischargeSpell;
import com.Polarice3.Goety.common.magic.spells.storm.MonsoonSpell;
import com.Polarice3.Goety.common.magic.spells.storm.ShockingSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
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
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoundStormCaster extends AbstractBoundIllager {
    private static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(BoundStormCaster.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BoundStormCaster.class, EntityDataSerializers.INT);
    public static String IDLE = "idle";
    public static String SHOCK = "shock";
    public static String CLOUD = "cloud";
    public static String DISCHARGE = "discharge";
    protected int castingTime;
    public static ItemStack STAFF = new ItemStack(ModItems.STORM_STAFF.get());
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState shockAnimationState = new AnimationState();
    public AnimationState cloudAnimationState = new AnimationState();
    public AnimationState dischargeAnimationState = new AnimationState();

    public BoundStormCaster(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(1, new DischargeSpellGoal());
        this.goalSelector.addGoal(2, new MonsoonSpellGoal());
        this.goalSelector.addGoal(3, new ShockGoal());
        this.goalSelector.addGoal(4, new AvoidTargetGoal<>(this, LivingEntity.class, 3.0F, 1.0D, 1.6D){
            @Override
            public boolean canUse() {
                return super.canUse() && !BoundStormCaster.this.isAttacking();
            }
        });
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.StormCasterHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.StormCasterArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FLYING_SPEED, 0.15D)
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
        if (Objects.equals(animation, IDLE)){
            return 1;
        } else if (Objects.equals(animation, SHOCK)){
            return 2;
        } else if (Objects.equals(animation, CLOUD)){
            return 3;
        } else if (Objects.equals(animation, DISCHARGE)){
            return 4;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
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
                        this.shockAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.shockAnimationState);
                        break;
                    case 3:
                        this.cloudAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.cloudAnimationState);
                        break;
                    case 4:
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

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    public boolean isAttacking(){
        return this.getCurrentAnimation() == this.getAnimationState(SHOCK) || this.getCurrentAnimation() == this.getAnimationState(CLOUD) || this.getCurrentAnimation() == this.getAnimationState(DISCHARGE);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            this.idleAnimationState.animateWhen(!this.isAttacking() && !this.walkAnimation.isMoving(), this.tickCount);
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
            }
        } else if (this.level instanceof ServerLevel serverLevel){
            if (this.isAlive()) {
                ServerParticleUtil.windParticle(serverLevel, ColorUtil.WHITE, 0.5F + serverLevel.random.nextFloat() * 0.5F, 0.0F, this.getId(), this.position());
                if (serverLevel.random.nextInt(20) == 0){
                    Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
                    Vec3 vec31 = vec3.add(this.random.nextDouble(), 1.0D, this.random.nextDouble());
                    ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 2));
                }
            }
        }
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.WIND.get();
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
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            if (livingentity != null
                    && livingentity.isAlive()
                    && BoundStormCaster.this.hasLineOfSight(livingentity)
                    && BoundStormCaster.this.distanceTo(livingentity) <= 8.0D) {
                if (BoundStormCaster.this.isCastingSpell()) {
                    return false;
                } else {
                    return BoundStormCaster.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            return livingentity != null
                    && livingentity.isAlive()
                    && BoundStormCaster.this.hasLineOfSight(livingentity)
                    && BoundStormCaster.this.distanceTo(livingentity) <= 8.0D
                    && this.shockTime > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && BoundStormCaster.this.hasLineOfSight(livingentity)){
                BoundStormCaster.this.getLookControl().setLookAt(livingentity, (float)BoundStormCaster.this.getMaxHeadYRot(), (float)BoundStormCaster.this.getMaxHeadXRot());
            }
            BoundStormCaster.this.navigation.stop();
            BoundStormCaster.this.setAnimationState(SHOCK);
            this.nextAttackTickCount = BoundStormCaster.this.tickCount + 100;
            this.shockTime = 30;
        }

        @Override
        public void stop() {
            super.stop();
            BoundStormCaster.this.setAnimationState(IDLE);
            this.shockTime = 0;
        }

        @Override
        public void tick() {
            super.tick();
            if (BoundStormCaster.this.getTarget() != null) {
                BoundStormCaster.this.getLookControl().setLookAt(BoundStormCaster.this.getTarget(), (float)BoundStormCaster.this.getMaxHeadYRot(), (float)BoundStormCaster.this.getMaxHeadXRot());
                if (this.shockTime > 0) {
                    --this.shockTime;
                    if (this.shockTime < 20 && MobUtil.hasVisualLineOfSight(BoundStormCaster.this, BoundStormCaster.this.getTarget())) {
                        new ShockingSpell().mobSpellResult(BoundStormCaster.this, STAFF);
                    }
                }
            }
        }
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
        }

        public boolean canUse() {
            return BoundStormCaster.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            BoundStormCaster.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            BoundStormCaster.this.setIsCastingSpell(0);
            BoundStormCaster.this.setAnimationState(IDLE);
        }

        public void tick() {
            if (BoundStormCaster.this.getTarget() != null) {
                BoundStormCaster.this.getLookControl().setLookAt(BoundStormCaster.this.getTarget(), (float)BoundStormCaster.this.getMaxHeadYRot(), (float)BoundStormCaster.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class StormCasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && BoundStormCaster.this.getCurrentAnimation() != BoundStormCaster.this.getAnimationState(SHOCK)) {
                if (BoundStormCaster.this.isCastingSpell()) {
                    return false;
                } else if (livingentity.distanceTo(BoundStormCaster.this) > 13.0F) {
                    BoundStormCaster.this.getNavigation().moveTo(livingentity, 1.1F);
                    return false;
                } else {
                    return BoundStormCaster.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            BoundStormCaster.this.castingTime = this.getCastingTime();
            this.nextAttackTickCount = BoundStormCaster.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                BoundStormCaster.this.playSound(soundevent, 1.0F, 1.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            BoundStormCaster.this.setAnimationState(IDLE);
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                BoundStormCaster.this.playSound(BoundStormCaster.this.getCastingSoundEvent(), 1.0F, 1.0F);
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

    class MonsoonSpellGoal extends BoundStormCaster.StormCasterUseSpellGoal {

        public void start() {
            super.start();
            BoundStormCaster.this.setAnimationState(CLOUD);
        }

        @Override
        protected void performSpellCasting() {
            if (BoundStormCaster.this.getTarget() != null){
                new MonsoonSpell().mobSpellResult(BoundStormCaster.this, STAFF);
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

    class DischargeSpellGoal extends BoundStormCaster.StormCasterUseSpellGoal {

        @Override
        public boolean canUse() {
            return super.canUse() && BoundStormCaster.this.getTarget() != null && BoundStormCaster.this.getTarget().distanceTo(BoundStormCaster.this) < 4.0D;
        }

        public void start() {
            super.start();
            BoundStormCaster.this.setAnimationState(DISCHARGE);
        }

        @Override
        protected void performSpellCasting() {
            if (BoundStormCaster.this.getTarget() != null) {
                new DischargeSpell().mobSpellResult(BoundStormCaster.this, STAFF);
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

    class MoveToTargetGoal extends Goal {
        @Nullable
        private LivingEntity target;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = BoundStormCaster.this.getTarget();
            if (BoundStormCaster.this.isAttacking()){
                return false;
            } else if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return this.target.distanceTo(BoundStormCaster.this) > 13.0D;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null && this.target.isAlive() && this.target.distanceTo(BoundStormCaster.this) > 6.0D && !BoundStormCaster.this.isAttacking();
        }

        @Override
        public void stop() {
            BoundStormCaster.this.getNavigation().stop();
        }

        public void tick() {
            if (this.target != null) {
                BoundStormCaster.this.getNavigation().moveTo(this.target, 1.1F);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
