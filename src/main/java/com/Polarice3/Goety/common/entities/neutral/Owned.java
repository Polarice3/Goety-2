package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.hostile.IBoss;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class Owned extends PathfinderMob implements IOwned, OwnableEntity, ICustomAttributes {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> HOSTILE = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> NATURAL = SynchedEntityData.defineId(Owned.class, EntityDataSerializers.BOOLEAN);
    private final NearestAttackableTargetGoal<Player> targetGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);
    public boolean limitedLifespan;
    public int limitedLifeTicks;

    protected Owned(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.checkHostility();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public void setConfigurableAttributes(){
    }

    public void checkHostility() {
        if (!this.level.isClientSide) {
            if (this.getTrueOwner() instanceof Enemy){
                this.setHostile(true);
            }
            if (this.getTrueOwner() instanceof IOwned owned){
                if (owned.isHostile()){
                    this.setHostile(true);
                }
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
        if (this.getTarget() instanceof IOwned ownedEntity){
            if (this.getTrueOwner() != null && (ownedEntity.getTrueOwner() == this.getTrueOwner())){
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
            if (MobUtil.ownerStack(this, ownedEntity)){
                this.setTarget(null);
                if (this.getLastHurtByMob() == ownedEntity){
                    this.setLastHurtByMob(null);
                }
            }
        }
        if (this.getTrueOwner() != null){
            if (this.getLastHurtByMob() == this.getTrueOwner()){
                this.setLastHurtByMob(null);
            }
            if (this.getTrueOwner() instanceof Mob mobOwner){
                if (mobOwner.getTarget() != null && this.getTarget() == null){
                    this.setTarget(mobOwner.getTarget());
                }
                if (mobOwner instanceof IBoss || mobOwner.getType().is(Tags.EntityTypes.BOSSES)) {
                    if (mobOwner.isRemoved() ||mobOwner.isDeadOrDying()){
                        this.kill();
                    }
                }
            }
            if (this.getTrueOwner() instanceof IOwned owned){
                if (this.getTrueOwner().isDeadOrDying() || !this.getTrueOwner().isAlive()){
                    if (owned.getTrueOwner() != null){
                        this.setTrueOwner(owned.getTrueOwner());
                    } else if (!this.isHostile() && !this.isNatural() && !(owned instanceof Enemy) && !owned.isHostile()){
                        this.kill();
                    }
                }
            }
            for (Owned target : this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)))) {
                if (this.getTrueOwner() != target.getTrueOwner()
                        && target.getTarget() == this.getTrueOwner()){
                    this.setTarget(target);
                }
            }
        }
        if (this.getTarget() != null){
            if (this.getTarget().isRemoved() || this.getTarget().isDeadOrDying()){
                this.setTarget(null);
            }
        }
        this.mobSense();
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.lifeSpanDamage();
        }
    }

    public void mobSense(){
        if (MobsConfig.MobSense.get()) {
            if (this.isAlive()) {
                if (this.getTarget() != null) {
                    if (this.getTarget() instanceof Mob mob && (mob.getTarget() == null || mob.getTarget().isDeadOrDying())) {
                        mob.setTarget(this);
                    }
                }
            }
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

    public boolean doHurtTarget(Entity entity) {
        if (this.getTrueOwner() != null) {
            float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (entity instanceof LivingEntity) {
                f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entity).getMobType());
                f1 += (float) EnchantmentHelper.getKnockbackBonus(this);
            }

            int i = EnchantmentHelper.getFireAspect(this);
            if (i > 0) {
                entity.setSecondsOnFire(i * 4);
            }

            boolean flag = entity.hurt(ModDamageSource.summonAttack(this, this.getTrueOwner()), f);
            if (flag) {
                if (f1 > 0.0F && entity instanceof LivingEntity) {
                    ((LivingEntity) entity).knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                if (entity instanceof Player player) {
                    this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
                }

                this.doEnchantDamageEffects(this, entity);
                this.setLastHurtMob(entity);
            }

            return flag;
        } else {
            return super.doHurtTarget(entity);
        }
    }

    public void maybeDisableShield(Player player, ItemStack axe, ItemStack shield) {
        if (!axe.isEmpty() && !shield.isEmpty() && axe.getItem() instanceof AxeItem && shield.is(Items.SHIELD)) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                player.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(player, (byte)30);
            }
        }

    }

    @Nullable
    public Team getTeam() {
        if (this.getTrueOwner() != null) {
            LivingEntity livingentity = this.getTrueOwner();
            if (livingentity != null && livingentity.getTeam() != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    //look at dish
    public boolean isAlliedTo(Entity entityIn) {
        if (this.getTrueOwner() != null) {
            LivingEntity trueOwner = this.getTrueOwner();
            return trueOwner.isAlliedTo(entityIn) || entityIn.isAlliedTo(trueOwner) || entityIn == trueOwner
                    || (entityIn instanceof IOwned owned && MobUtil.ownerStack(this, owned))
                    || (entityIn instanceof OwnableEntity ownable && ownable.getOwner() == trueOwner)
                    || (trueOwner instanceof Player player
                    && entityIn instanceof LivingEntity livingEntity
                    && (SEHelper.getAllyEntities(player).contains(livingEntity)
                    || SEHelper.getAllyEntityTypes(player).contains(livingEntity.getType())));
        }
        return super.isAlliedTo(entityIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(HOSTILE, false);
        this.entityData.define(NATURAL, false);
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

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
        }

        if (compound.contains("isHostile")){
            this.setHostile(compound.getBoolean("isHostile"));
        } else {
            this.checkHostility();
        }

        if (compound.contains("isNatural")){
            this.setNatural(compound.getBoolean("isNatural"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compound.putInt("OwnerClient", this.getOwnerClientId());
        }
        if (this.isHostile()){
            compound.putBoolean("isHostile", this.isHostile());
        }
        if (this.isNatural()){
            compound.putBoolean("isNatural", this.isNatural());
        }
        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    public void setLimitedLife(int limitedLifeTicksIn) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    @Override
    public void convertNewEquipment(Entity entity){
        this.populateDefaultEquipmentSlots(this.random, this.level.getCurrentDifficultyAt(this.blockPosition()));
    }

    @Nullable
    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        return this.getType();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.checkHostility();
        if (pReason != MobSpawnType.MOB_SUMMONED && this.getTrueOwner() == null){
            this.setNatural(true);
        }
        return pSpawnData;
    }

    @Nullable
    public LivingEntity getTrueOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            return this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    public LivingEntity getMasterOwner(){
        if (this.getTrueOwner() instanceof IOwned owned){
            return owned.getTrueOwner();
        } else {
            return this.getTrueOwner();
        }
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    public Entity getOwner() {
        return this.getTrueOwner();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getOwnerClientId(){
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    public void setOwnerClientId(int id){
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    public void setTrueOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
        this.setOwnerClientId(livingEntity.getId());
    }

    public void setHostile(boolean hostile){
        this.entityData.set(HOSTILE, hostile);
        this.addTargetGoal();
    }

    public void addTargetGoal(){
        this.targetSelector.addGoal(2, this.targetGoal);
    }

    public boolean isHostile(){
        return this.entityData.get(HOSTILE);
    }

    public void setNatural(boolean natural){
        this.entityData.set(NATURAL, natural);
    }

    public boolean isNatural(){
        return this.entityData.get(NATURAL);
    }

    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect() != GoetyEffects.GOLD_TOUCHED.get() && super.canBeAffected(pPotioneffect);
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
    public void push(Entity p_21294_) {
        if (!this.level.isClientSide) {
            if (p_21294_ != this.getTrueOwner()) {
                super.push(p_21294_);
            }
        }
    }

    protected void doPush(Entity p_20971_) {
        if (!this.level.isClientSide) {
            if (p_20971_ != this.getTrueOwner()) {
                super.doPush(p_20971_);
            }
        }
    }

    public boolean canCollideWith(Entity p_20303_) {
        if (p_20303_ != this.getTrueOwner()){
            return super.canCollideWith(p_20303_);
        } else {
            return false;
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.isHostile();
    }

    public boolean removeWhenFarAway(double p_27519_) {
        return this.isHostile();
    }

    public static boolean checkHostileSpawnRules(EntityType<? extends Owned> pType, ServerLevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom) && checkMobSpawnRules(pType, pLevel, pReason, pPos, pRandom);
    }

    public boolean isChargingCrossbow() {
        return false;
    }

    @Override
    public void awardKillScore(Entity entity, int p_19954_, DamageSource damageSource) {
        super.awardKillScore(entity, p_19954_, damageSource);
        if (this.getMasterOwner() instanceof ServerPlayer serverPlayer) {
            ModCriteriaTriggers.SERVANT_KILLED_ENTITY.trigger(serverPlayer, entity, damageSource);
        }
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
            Owned.this.setTarget(this.attacker);
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
