package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Piker extends HuntingIllagerEntity{
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Piker.class, EntityDataSerializers.BYTE);
    public int attackTick;
    public boolean aggressiveMode;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    
    public Piker(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(4, new PikerAttackGoal());
    }

    public void extraGoals(){
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PikerHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PikerDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.PikerArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PikerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PikerDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PikerArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
        Raid raid = this.getCurrentRaid();
        int i = 0;
        if (p_37844_ > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 1;
        }

        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, i));
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
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
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
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
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
        if (this.attackTick > 20){
            this.setMeleeAttacking(false);
        }
        if (!this.level.isClientSide){
            if (this.getTarget() != null){
                this.level.broadcastEntityEvent(this, (byte) 6);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 7);
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

    public void setMeleeAttacking(boolean attacking) {
        this.setFlag(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.PIKER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.PIKER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.PIKER_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.PIKER_STEP.get(), 1.0F, 1.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag compoundTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(accessor, instance, spawnType, groupData, compoundTag);
        if (spawnType == MobSpawnType.EVENT) {
            if (accessor.getLevel().random.nextFloat() <= 0.25F && !this.isPassenger()) {
                Trampler trampler = new Trampler(ModEntityType.TRAMPLER.get(), accessor.getLevel());
                trampler.finalizeSpawn(accessor, accessor.getLevel().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.EVENT, null, null);
                trampler.setPos(this.position());
                accessor.getLevel().addFreshEntity(trampler);
                this.startRiding(trampler);
            }
        }
        return spawnGroupData;
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

    public boolean doHurtTarget(Entity p_21372_) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (p_21372_ instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)p_21372_).getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            p_21372_.setSecondsOnFire(i * 4);
        }

        boolean flag = p_21372_.hurt(DamageSource.mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && p_21372_ instanceof LivingEntity living) {
                living.knockback((double)(f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
            }

            this.doEnchantDamageEffects(this, p_21372_);
            this.setLastHurtMob(p_21372_);
        }

        return flag;
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        if (this.getVehicle() instanceof Ravager) {
            float f = this.getVehicle().getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + enemy.getBbWidth());
        }
        return (double)(this.getBbWidth() * 5.0F * this.getBbWidth() * 5.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        return distToEnemySqr <= this.getAttackReachSqr(enemy) || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6) {
            this.aggressiveMode = true;
        } else if (p_21375_ == 7) {
            this.aggressiveMode = false;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.PIKER_CELEBRATE.get();
    }

    class PikerAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public PikerAttackGoal() {
            super(Piker.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return Piker.this.getTarget() != null && Piker.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            Piker.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = Piker.this.getTarget();
            if (livingentity == null) {
                return;
            }

            Piker.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = Piker.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

            if (--this.delayCounter <= 0 && !Piker.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                Piker.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, Piker.this.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (Piker.this.targetClose(enemy, distToEnemySqr)) {
                if (!Piker.this.isMeleeAttacking()) {
                    Piker.this.setMeleeAttacking(true);
                }
            }
        }

        @Override
        public void stop() {
            Piker.this.getNavigation().stop();
            if (Piker.this.getTarget() == null) {
                Piker.this.setAggressive(false);
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Piker.this.getTarget() != null && Piker.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return Piker.this.attackTick < 20;
        }

        @Override
        public void start() {
            Piker.this.setMeleeAttacking(true);
            Piker.this.level.broadcastEntityEvent(Piker.this, (byte) 4);
        }

        @Override
        public void stop() {
            Piker.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (Piker.this.getTarget() != null && Piker.this.getTarget().isAlive()) {
                LivingEntity livingentity = Piker.this.getTarget();
                double d0 = Piker.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                MobUtil.instaLook(Piker.this, livingentity);
                if (Piker.this.attackTick == 1){
                    Piker.this.playSound(ModSounds.PIKER_SWING.get(), Piker.this.getSoundVolume(), Piker.this.getVoicePitch());
                }
                if (Piker.this.attackTick == 8) {
                    if (Piker.this.targetClose(livingentity, d0)) {
                        if (Piker.this.doHurtTarget(livingentity)){
                            Piker.this.playSound(ModSounds.PIKER_PIKE.get(), Piker.this.getSoundVolume(), Piker.this.getVoicePitch());
                            for (Entity entity : getTargets(Piker.this.level, Piker.this, 3)){
                                if (entity instanceof LivingEntity living){
                                    if (!living.isAlliedTo(Piker.this) && !Piker.this.isAlliedTo(living) && living != livingentity && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && Piker.this.canAttack(livingentity)){
                                        Piker.this.doHurtTarget(living);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
            List<Entity> list = new ArrayList<>();
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
            List<Entity> possibleList = level.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

            for (Entity hit : possibleList) {
                if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                    list.add(hit);
                }
            }
            return list;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
