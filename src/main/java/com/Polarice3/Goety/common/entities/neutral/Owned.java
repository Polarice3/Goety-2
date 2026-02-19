package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;

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
    public int hasSummonCheck;
    public int revivingTime;

    protected Owned(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.checkHostility();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.ownerHurtGoals();
    }

    public void ownerHurtGoals() {
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal<>(this));
    }

    public void setConfigurableAttributes(){
    }

    public boolean isInvisibleTo(Player p_20178_) {
        if (p_20178_ == this.getMasterOwner()){
            return false;
        } else {
            return super.isInvisibleTo(p_20178_);
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
        this.ownedTick();
    }

    protected void updateNoActionTime() {
        float f = this.getLightLevelDependentMagicValue();
        if (f > 0.5F) {
            this.noActionTime += 2;
        }

    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || this.getTrueOwner() != null;
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

            boolean flag = this.doHurtTarget(f, entity);
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

    public boolean doHurtTarget(float amount, Entity target){
        return target.hurt(ModDamageSource.summonAttack(this, this.getTrueOwner()), amount);
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
            if (livingentity != null && livingentity != this && !this.areOwnedByEachOther(livingentity) && livingentity.getTeam() != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean areOwnedByEachOther(LivingEntity livingEntity){
        if (livingEntity instanceof IOwned owned){
            return owned.getTrueOwner() == this && this.getTrueOwner() == livingEntity;
        }
        return false;
    }

    //look at dish
    public boolean isAlliedTo(Entity entityIn) {
        if (this.getTrueOwner() != null) {
            LivingEntity trueOwner = this.getTrueOwner();
            return trueOwner.isAlliedTo(entityIn)
                    || entityIn.isAlliedTo(trueOwner)
                    || entityIn == trueOwner
                    || (entityIn instanceof IOwned owned && MobUtil.ownerStack(this, owned))
                    || (entityIn instanceof OwnableEntity ownable && ownable.getOwner() == trueOwner)
                    || (entityIn instanceof LivingEntity livingEntity && this.isAllyWith(livingEntity));
        }
        return super.isAlliedTo(entityIn) || (entityIn instanceof LivingEntity livingEntity && this.isAllyWith(livingEntity));
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
        this.readOwnedData(compound);
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.saveOwnedData(compound);
    }

    @Override
    public void setHasLifespan(boolean lifespan) {
        this.limitedLifespan = lifespan;
    }

    @Override
    public boolean hasLifespan() {
        return this.limitedLifespan;
    }

    @Override
    public void setLifespan(int lifespan) {
        this.limitedLifeTicks = lifespan;
    }

    @Override
    public int getLifespan() {
        return this.limitedLifeTicks;
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
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.checkHostility();
        if (pReason != MobSpawnType.MOB_SUMMONED && this.getTrueOwner() == null){
            this.setNatural(true);
        } else if (this.isNatural()) {
            this.setNatural(false);
        }
        if (this.getTrueOwner() instanceof Player) {
            this.setPersistenceRequired();
        }
        return pSpawnData;
    }

    @Nullable
    public LivingEntity getTrueOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(this.level, uuid);
        } else {
            int id = this.getOwnerClientId();
            return id <= -1 ? null : this.level.getEntity(this.getOwnerClientId()) instanceof LivingEntity living && living != this ? living : null;
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

    @Nullable
    public LivingEntity getOwner() {
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
        if (this.getTrueOwner() != null) {
            return pPotioneffect.getEffect() != GoetyEffects.GOLD_TOUCHED.get() && super.canBeAffected(pPotioneffect);
        }
        return super.canBeAffected(pPotioneffect);
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

    public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends Owned> pType, LevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(pType, pLevel, pReason, pPos, pRandom);
    }

    public static boolean checkDayMonsterSpawnRules(EntityType<? extends Owned> pType, LevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getBrightness(LightLayer.BLOCK, pPos) <= 8 && checkAnyLightMonsterSpawnRules(pType, pLevel, pReason, pPos, pRandom);
    }

    @Override
    public boolean isVehicle() {
        if (this instanceof IAutoRideable rideable && rideable.isAutonomous()){
            return false;
        } else {
            return super.isVehicle();
        }
    }

    @Override
    public int getHasSummonCheck() {
        return this.hasSummonCheck;
    }

    @Override
    public void setHasSummonCheck(int hasSummonCheck) {
        this.hasSummonCheck = hasSummonCheck;
    }

    @Override
    public int getRevivingTime() {
        return this.revivingTime;
    }

    @Override
    public void setRevivingTime(int tick) {
        this.revivingTime = tick;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.hasSummons()) {
            if (reason == RemovalReason.DISCARDED || reason == RemovalReason.KILLED) {
                if (this.level instanceof ServerLevel serverLevel) {
                    for (Entity entity : serverLevel.getAllEntities()) {
                        if (entity instanceof IOwned owned) {
                            if (owned.getTrueOwner() == this) {
                                if (this.getTrueOwner() != null) {
                                    owned.setTrueOwner(this.getTrueOwner());
                                } else {
                                    owned.removeTrueOwner();
                                }
                            }
                        }
                    }
                }
            }
        }
        super.remove(reason);
    }

    public static class OwnerHurtTargetGoal<T extends Mob & IOwned> extends TargetGoal {
        private final T owned;
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtTargetGoal(T summonedEntity) {
            super(summonedEntity, false);
            this.owned = summonedEntity;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.owned.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.attacker = livingentity.getLastHurtMob();
                int i = livingentity.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.attacker != this.owned.getTrueOwner();
            }
        }

        public void start() {
            if (this.attacker != null) {
                this.owned.setTarget(this.attacker);
                LivingEntity livingentity = this.owned.getTrueOwner();
                if (livingentity != null) {
                    this.timestamp = livingentity.getLastHurtMobTimestamp();
                }
            }
            super.start();
        }
    }

    public static class OwnerHurtByTargetGoal<T extends Mob & IOwned> extends TargetGoal {
        private final T owned;
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtByTargetGoal(T summonedEntity) {
            super(summonedEntity, false);
            this.owned = summonedEntity;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.owned.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.attacker = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.attacker != this.owned.getTrueOwner();
            }
        }

        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity livingentity = this.owned.getTrueOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getLastHurtByMobTimestamp();
            }

            super.start();
        }
    }

}
