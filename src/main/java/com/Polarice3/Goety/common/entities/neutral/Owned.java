package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class Owned extends PathfinderMob implements IOwned, ICustomAttributes{
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> HOSTILE = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.BOOLEAN);
    private final NearestAttackableTargetGoal<Player> targetGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);
    public boolean limitedLifespan;
    public int limitedLifeTicks;

    protected Owned(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        ICustomAttributes.applyAttributesForEntity(type, this);
        this.checkHostility();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return null;
    }

    public void checkHostility() {
        if (!this.level.isClientSide) {
            if (this.getTrueOwner() instanceof Enemy){
                this.setHostile(true);
            }
            if (this instanceof Enemy){
                this.setHostile(true);
            }
        }
    }

    public void aiStep() {
        this.updateSwingTime();
        if (this.isHostile()){
            this.updateNoActionTime();
        }
        super.aiStep();
    }

    public void tick(){
        super.tick();
        if (this.getTarget() instanceof Owned){
            Owned ownedEntity = (Owned) this.getTarget();
            if (ownedEntity.getTrueOwner() == this.getTrueOwner() && this.getTrueOwner() != null){
                this.setTarget(null);
                if (this.getLastHurtByMob() == ownedEntity){
                    this.setLastHurtByMob(null);
                }
            }
            if (ownedEntity.getTrueOwner() == this){
                this.setTarget(null);
                if (this.getLastHurtByMob() == ownedEntity){
                    this.setLastHurtByMob(null);
                }
            }
        }
        if (this.getTrueOwner() instanceof Owned){
            if (this.getTrueOwner().isDeadOrDying() || !this.getTrueOwner().isAlive()){
                this.kill();
            }
        }
        if (this.getLastHurtByMob() == this.getTrueOwner()){
            this.setLastHurtByMob(null);
        }
        if (this.getTrueOwner() instanceof Mob){
            Mob mobOwner = (Mob) this.getTrueOwner();
            if (mobOwner.getTarget() != null && this.getTarget() == null){
                this.setTarget(mobOwner.getTarget());
            }
        }
        for (Owned target : this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)))) {
            if (target.getTrueOwner() != this.getTrueOwner()
                    && this.getTrueOwner() != target.getTrueOwner()
                    && target.getTarget() == this.getTrueOwner()
                    && this.getTrueOwner() != null){
                this.setTarget(target);
            }
        }
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.lifeSpanDamage();
        }
    }

    protected void updateNoActionTime() {
        float f = this.getLightLevelDependentMagicValue();
        if (f > 0.5F) {
            this.noActionTime += 2;
        }

    }

    public void lifeSpanDamage(){
        this.limitedLifeTicks = 20;
        this.hurt(DamageSource.STARVE, 1.0F);
    }

    public Team getTeam() {
        if (this.getTrueOwner() != null) {
            LivingEntity livingentity = this.getTrueOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.getTrueOwner() != null) {
            LivingEntity livingentity = this.getTrueOwner();
            if (entityIn == livingentity) {
                return true;
            }
            return livingentity.isAlliedTo(entityIn);
        }
        if (entityIn instanceof Owned && ((Owned) entityIn).getTrueOwner() != null && ((Owned) entityIn).getTrueOwner() == this.getTrueOwner() && this.getTrueOwner() != null){
            return true;
        }
        return super.isAlliedTo(entityIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(HOSTILE, false);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }

        if (compound.contains("isHostile")){
            this.setHostile(compound.getBoolean("isHostile"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }

        this.checkHostility();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.isHostile()){
            compound.putBoolean("isHostile", this.isHostile());
        }
        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    public void setLimitedLife(int limitedLifeTicksIn) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.checkHostility();
        return pSpawnData;
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setTrueOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
    }

    public void setHostile(boolean hostile){
        this.entityData.set(HOSTILE, hostile);
        this.targetSelector.addGoal(2, this.targetGoal);
    }

    public boolean isHostile(){
        return this.entityData.get(HOSTILE);
    }

    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return /*pPotioneffect.getEffect() != ModEffects.GOLDTOUCHED.get() &&*/ super.canBeAffected(pPotioneffect);
    }

    public int getExperienceReward() {
        if (this.isHostile()) {
            this.xpReward = this.xpReward();
        }

        return super.getExperienceReward();
    }

    public int xpReward(){
        return 5;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.isHostile();
    }

    public static boolean checkHostileSpawnRules(EntityType<? extends Owned> pType, ServerLevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom) && checkMobSpawnRules(pType, pLevel, pReason, pPos, pRandom);
    }

    public class OwnerHurtTargetGoal extends TargetGoal {
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtTargetGoal(Owned summonedEntity) {
            super(summonedEntity, false);
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            LivingEntity livingentity = Owned.this.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.attacker = livingentity.getLastHurtMob();
                int i = livingentity.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.attacker != Owned.this.getTrueOwner();
            }
        }

        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity livingentity = Owned.this.getTrueOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtMobTimestamp();
            }

            super.start();
        }
    }

    public class OwnerHurtByTargetGoal extends TargetGoal {
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtByTargetGoal(Owned summonedEntity) {
            super(summonedEntity, false);
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            LivingEntity livingentity = Owned.this.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.attacker = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.attacker != Owned.this.getTrueOwner();
            }
        }

        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity livingentity = Owned.this.getTrueOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }

    public static class LesserFollowOwnerGoal extends Goal {
        private final Owned ownedEntity;
        private LivingEntity owner;
        private final LevelReader level;
        private final double followSpeed;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float maxDist;
        private final float minDist;
        private float oldWaterCost;

        public LesserFollowOwnerGoal(Owned ownedEntity, double speed, float minDist, float maxDist) {
            this.ownedEntity = ownedEntity;
            this.level = ownedEntity.level;
            this.followSpeed = speed;
            this.navigation = ownedEntity.getNavigation();
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(ownedEntity.getNavigation() instanceof GroundPathNavigation) && !(ownedEntity.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.ownedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.ownedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else {
                return !(this.ownedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.ownedEntity.getPathfindingMalus(BlockPathTypes.WATER);
            this.ownedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.ownedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.ownedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.ownedEntity.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.ownedEntity.isLeashed() && !this.ownedEntity.isPassenger()) {
                    this.navigation.moveTo(this.owner, this.followSpeed);
                }
            }
        }
    }
}
