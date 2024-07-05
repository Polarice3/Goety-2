package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.magic.spells.frost.FrostNovaSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IceGolem extends AbstractGolemServant{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(IceGolem.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(IceGolem.class, EntityDataSerializers.BYTE);
    public int attackTick;
    public int smashTick;
    public int attackCool;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();

    public IceGolem(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AttackGoal(this));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.IceGolemHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.IceGolemArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.IceGolemDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.IceGolemFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.IceGolemHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.IceGolemArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.IceGolemDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.IceGolemFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.ICE_GOLEM_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.ICE_GOLEM_STEP.get(), 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ICE_GOLEM_DEATH.get();
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
        } else if (Objects.equals(animation, "attack")){
            return 3;
        } else if (Objects.equals(animation, "smash")){
            return 4;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.smashAnimationState);
        return animationStates;
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
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 4:
                        this.smashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                }
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

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean meleeAttacking) {
        this.setFlag(1, meleeAttacking);
        this.attackTick = 0;
        if (meleeAttacking){
            this.setAnimationState("attack");
            this.playSound(ModSounds.ICE_GOLEM_SWING.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public boolean isSmashing() {
        return this.getFlag(2);
    }

    public void setSmashing(boolean smashing) {
        this.setFlag(2, smashing);
        this.smashTick = 0;
        if (smashing){
            this.setAnimationState("smash");
            this.playSound(ModSounds.ICE_GOLEM_PRE_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isFire()){
            amount *= 1.33F;
        }
        if (ModDamageSource.freezeAttacks(source) || source == DamageSource.FREEZE){
            return false;
        }
        if (this.isUpgraded()){
            if (!source.isMagic() && source.getDirectEntity() instanceof LivingEntity livingentity) {
                if (!source.isExplosion()) {
                    livingentity.hurt(DamageSource.thorns(this), 2.0F);
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.setAggressive(true);
        } else if (p_21375_ == 6){
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.FROST;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            if (!this.isMeleeAttacking() && !this.isSmashing()){
                if (!this.isMoving()){
                    this.setAnimationState("idle");
                } else {
                    this.setAnimationState("walk");
                }
            }
            if (this.attackCool > 0){
                --this.attackCool;
                this.navigation.stop();
            } else {
                if (this.isMeleeAttacking()){
                    ++this.attackTick;
                    if (this.attackTick >= 20){
                        this.attackCool = 10;
                        this.setMeleeAttacking(false);
                    }
                } else {
                    this.attackTick = 0;
                }
                if (this.isSmashing()){
                    ++this.smashTick;
                    if (this.smashTick == 19){
                        if (this.level instanceof ServerLevel serverLevel) {
                            new FrostNovaSpell().SpellResult(serverLevel, this, ItemStack.EMPTY);
                        }
                    }
                    if (this.smashTick >= MathHelper.secondsToTicks(2.5F)){
                        this.attackCool = 10;
                        this.setSmashing(false);
                    }
                } else {
                    this.smashTick = 0;
                }
            }

            if (this.isUpgraded()){
                if (this.level instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.SNOWFLAKE, this, 1.0F);
                    for (LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0F))) {
                        if (!living.isFreezing() && living.canFreeze() && MobUtil.validEntity(living) && !MobUtil.areAllies(this, living)) {
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SNOWFLAKE, living);
                            living.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, 0));
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((item == Items.ICE || item == Items.PACKED_ICE || item == Items.BLUE_ICE) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    if (item == Items.ICE){
                        this.heal(0.25F);
                    } else if (item == Items.PACKED_ICE){
                        this.heal(3.0F);
                    } else {
                        this.heal(18.0F);
                    }
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    pPlayer.swing(p_230254_2_);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }

    static class AttackGoal extends Goal {
        public IceGolem iceGolem;
        private int delayCounter;

        public AttackGoal(IceGolem iceGolem){
            this.iceGolem = iceGolem;
        }

        @Override
        public boolean canUse() {
            return this.iceGolem.getTarget() != null
                    && this.iceGolem.getTarget().isAlive()
                    && !this.iceGolem.isMeleeAttacking()
                    && !this.iceGolem.isSmashing()
                    && this.iceGolem.attackCool <= 0
                    && this.iceGolem.hasLineOfSight(this.iceGolem.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return (this.iceGolem.isMeleeAttacking() || this.iceGolem.isSmashing()) && this.iceGolem.attackCool <= 0;
        }

        @Override
        public void start() {
            this.iceGolem.setAggressive(true);
            this.iceGolem.level.broadcastEntityEvent(this.iceGolem, (byte) 5);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            this.iceGolem.setMeleeAttacking(false);
            this.iceGolem.setAggressive(false);
            this.iceGolem.setSmashing(false);
            this.iceGolem.level.broadcastEntityEvent(this.iceGolem, (byte) 6);
            this.iceGolem.setAnimationState("idle");
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.iceGolem.getTarget();
            if (livingentity == null) {
                return;
            }

            if (this.iceGolem.targetClose(livingentity, this.iceGolem.distanceToSqr(livingentity))) {
                MobUtil.instaLook(this.iceGolem, livingentity);
                float chance = 0.05F;
                if (this.getNearbyTargets().size() >= 4){
                    chance += 0.45F;
                }
                if (!this.iceGolem.isMeleeAttacking() && !this.iceGolem.isSmashing()) {
                    if (this.iceGolem.random.nextFloat() > chance) {
                        this.iceGolem.setMeleeAttacking(true);
                    } else {
                        this.iceGolem.setSmashing(true);
                    }
                } else {
                    this.iceGolem.navigation.stop();
                }
            } else {
                this.iceGolem.getLookControl().setLookAt(livingentity, this.iceGolem.getMaxHeadYRot(), this.iceGolem.getMaxHeadXRot());
                if (--this.delayCounter <= 0) {
                    this.delayCounter = 10;
                    this.iceGolem.getNavigation().moveTo(livingentity, 1.25F);
                }
            }
            if (this.iceGolem.attackTick == 10){
                this.iceGolem.swing(InteractionHand.MAIN_HAND);
                if (this.iceGolem.targetClose(livingentity, this.iceGolem.distanceToSqr(livingentity))) {
                    this.iceGolem.doHurtTarget(livingentity);
                    this.iceGolem.playSound(ModSounds.ICE_GOLEM_ATTACK.get(), this.iceGolem.getSoundVolume(), this.iceGolem.getVoicePitch());
                }
            }
        }

        public List<LivingEntity> getNearbyTargets(){
            return this.iceGolem.level.getEntitiesOfClass(LivingEntity.class, this.iceGolem.getBoundingBox().inflate(4.0D, 2.0D, 4.0D), living -> SummonTargetGoal.predicate(this.iceGolem).test(living));
        }
    }
}
