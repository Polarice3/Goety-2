package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.mixin.MobAccessor;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSpiderServant extends Spider implements PlayerRideable, IServant, OwnableEntity, ICustomAttributes {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> HOSTILE = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> NATURAL = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Byte> SUMMONED_FLAGS = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> UPGRADE_FLAGS = SynchedEntityData.defineId(AbstractSpiderServant.class, EntityDataSerializers.BYTE);
    private final NearestAttackableTargetGoal<Player> targetGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);
    public boolean limitedLifespan;
    public int limitedLifeTicks;
    @Nullable
    private LivingEntity priorityTarget;
    public LivingEntity commandPosEntity;
    public BlockPos commandPos;
    public BlockPos priorityPos;
    public BlockPos boundPos;
    public String boundDim = Level.OVERWORLD.location().toString();
    public int priorityTime;
    public int commandTick;
    public int killChance;
    public int noHealTime;
    public int hasSummonCheck;
    public int revivingTime;
    public long ticketTime = 0;

    public AbstractSpiderServant(EntityType<? extends Spider> p_33786_, Level p_33787_) {
        super(p_33786_, p_33787_);
        this.checkHostility();
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new Owned.OwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new Owned.OwnerHurtTargetGoal<>(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.followGoal();
        this.targetSelectGoal();
    }

    public void followGoal(){
        this.goalSelector.addGoal(5, new Summoned.FollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F));
    }

    public void targetSelectGoal(){
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public void setConfigurableAttributes(){
    }

    public void checkDespawn() {
        if (this.isHostile()){
            super.checkDespawn();
        }
    }

    public boolean isInvisibleTo(Player p_20178_) {
        if (p_20178_ == this.getMasterOwner()){
            return false;
        } else {
            return super.isInvisibleTo(p_20178_);
        }
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
        }
    }

    public void tick(){
        super.tick();
        this.ownedTick();
        this.servantTick();
    }

    @Override
    public long getTicketTime() {
        return this.ticketTime;
    }

    @Override
    public void setTicketTime(long ticketTime) {
        this.ticketTime = ticketTime;
    }

    @Override
    public long decreaseTicketTime() {
        return --this.ticketTime;
    }

    @Override
    public boolean burnSunTick() {
        return this.isSunBurnTick();
    }

    protected void updateNoActionTime() {
        if (this.isHostile()) {
            float f = this.getLightLevelDependentMagicValue();
            if (f > 0.5F) {
                this.noActionTime += 2;
            }
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
        this.entityData.define(SUMMONED_FLAGS, (byte)0);
        this.entityData.define(UPGRADE_FLAGS, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readOwnedData(compound);
        this.readServantData(compound);
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.saveOwnedData(compound);
        this.saveServantData(compound);
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(SUMMONED_FLAGS);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(SUMMONED_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(SUMMONED_FLAGS, (byte)(i & 255));
    }

    public boolean isWandering() {
        return this.getFlag(1);
    }

    public void setWandering(boolean wandering) {
        this.setFlags(1, wandering);
    }

    public boolean isStaying(){
        return this.getFlag(2) && !this.isCommanded() && !this.isVehicle();
    }

    public void setStaying(boolean staying){
        this.setFlags(2, staying);
    }

    public boolean canUpdateMove(){
        return true;
    }

    public boolean isUpgraded() {
        return (this.entityData.get(UPGRADE_FLAGS) & 4) != 0;
    }

    public void setUpgraded(boolean upgraded) {
        byte b0 = this.entityData.get(UPGRADE_FLAGS);
        if (upgraded) {
            this.entityData.set(UPGRADE_FLAGS, (byte)(b0 | 4));
        } else {
            this.entityData.set(UPGRADE_FLAGS, (byte)(b0 & -5));
        }
    }

    public void upgrade(){
        this.setUpgraded(true);
    }

    public void downgrade(){
        this.setUpgraded(false);
    }

    public void setCommandPos(BlockPos blockPos, boolean removeEntity) {
        if (removeEntity) {
            this.commandPosEntity = null;
        }
        this.commandPos = blockPos;
        this.setCommandTick(MathHelper.secondsToTicks(10));
    }

    public BlockPos getCommandPos(){
        return this.commandPos;
    }

    public void setCommandPosEntity(@Nullable LivingEntity living){
        this.commandPosEntity = living;
        if (living != null) {
            this.setCommandPos(living.blockPosition(), false);
        }
    }

    @Nullable
    public LivingEntity getCommandPosEntity(){
        return this.commandPosEntity;
    }

    public int getCommandTick(){
        return this.commandTick;
    }

    @Override
    public void setCommandTick(int commandTick) {
        this.commandTick = commandTick;
    }

    public boolean isCommanded(){
        return this.commandPos != null;
    }

    public BlockPos getBoundPos(){
        return this.boundPos;
    }

    public void setBoundPos(BlockPos blockPos){
        this.boundPos = blockPos;
        this.setBoundDim(this.level.dimension());
    }

    public Vec3 vec3BoundPos(){
        return Vec3.atBottomCenterOf(this.boundPos);
    }

    @Override
    public String getBoundDim() {
        return this.boundDim;
    }

    public void setBoundDim(String string) {
        this.boundDim = string;
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (this.isGuardingArea() && !this.isPrioritizing()){
            if (target != null) {
                if (target.distanceToSqr(this.vec3BoundPos()) <= Mth.square(GUARDING_RANGE)) {
                    this.overrideSetTarget(target);
                }
            } else {
                this.overrideSetTarget(null);
            }
        } else {
            this.overrideSetTarget(target);
        }
    }

    public void overrideSetTarget(@Nullable LivingEntity target){
        super.setTarget(target);
    }

    @Nullable
    public LivingEntity getPriorityTarget() {
        return this.priorityTarget;
    }

    @Override
    public void setPriorityTarget(@Nullable LivingEntity priorityTarget) {
        this.overrideSetTarget(priorityTarget);
        this.priorityTarget = priorityTarget;
        if (priorityTarget != null) {
            this.setPriorityTime(100);
            this.setPriorityPos(priorityTarget.blockPosition());
        }
    }

    @Override
    public int getPriorityTime() {
        return this.priorityTime;
    }

    @Override
    public void setPriorityTime(int time) {
        this.priorityTime = time;
    }

    @Override
    public BlockPos getPriorityPos() {
        return this.priorityPos;
    }

    @Override
    public void setPriorityPos(BlockPos priorityPos) {
        this.priorityPos = priorityPos;
    }
    @Deprecated
    public void normalSetTarget(@Nullable LivingEntity target) {
        this.overrideSetTarget(target);
    }

    public void dropEquipment(EquipmentSlot equipmentSlot, ItemStack stack){
        if (this.getEquipmentDropChance(equipmentSlot) > 0.0F) {
            this.spawnAtLocation(stack);
        }
    }

    public boolean isMoving() {
        return !(this.walkAnimation.speed() < 0.01F);
    }

    public int getNoHealTime() {
        return this.noHealTime;
    }

    @Override
    public void setNoHealTime(int time) {
        this.noHealTime = time;
    }

    @Override
    public int getKillChance() {
        return this.killChance;
    }

    @Override
    public void setKillChance(int killChance) {
        this.killChance = killChance;
    }

    public void warnKill(Player player){
        this.killChance = 60;
        player.displayClientMessage(Component.translatable("info.goety.servant.tryKill", this.getDisplayName()), true);
    }

    public void tryKill(Player player){
        this.kill();
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        AttributeInstance instance = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (instance != null){
            instance.addPermanentModifier(new AttributeModifier("Random spawn bonus", randomsource.triangle(0.0D, 0.11485000000000001D), AttributeModifier.Operation.MULTIPLY_BASE));
        }
        this.setLeftHanded(randomsource.nextFloat() < 0.05F);

        MobAccessor mobAccessor = (MobAccessor) this;
        mobAccessor.setSpawnType(pReason);
        this.checkHostility();
        if (pReason != MobSpawnType.MOB_SUMMONED && this.getTrueOwner() == null){
            this.setNatural(true);
        }
        this.setWandering(this.getTrueOwner() == null);
        this.setStaying(false);
        return pSpawnData;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (MobsConfig.ServantsMasterImmune.get()) {
            if (source.getEntity() instanceof Summoned summoned) {
                if (!summoned.isHostile() && !this.isHostile()) {
                    if (this.getTrueOwner() != null && summoned.getTrueOwner() == this.getTrueOwner()) {
                        return false;
                    }
                }
            }
        }
        boolean flag = super.hurt(source, amount);
        if (flag){
            this.noHealTime = MathHelper.secondsToTicks(MobsConfig.ServantHealHalt.get());
        }
        return flag;
    }

    public void die(DamageSource pCause) {
        if (!this.level.isClientSide && this.hasCustomName() && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getTrueOwner() instanceof ServerPlayer) {
            this.getTrueOwner().sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }
        super.die(pCause);
    }

    @Nullable
    public LivingEntity getTrueOwner() {
        if (!this.level.isClientSide){
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
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
        return pPotioneffect.getEffect() != GoetyEffects.GOLD_TOUCHED.get() && super.canBeAffected(pPotioneffect);
    }

    public int getExperienceReward() {
        if (this.isHostile()) {
            this.xpReward = this.xpReward();
            return super.getExperienceReward();
        }

        return 0;
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
    public boolean isPreventingPlayerRest(Player p_33036_) {
        return this.isHostile();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return this.isHostile();
    }

    public boolean removeWhenFarAway(double p_27519_) {
        return this.isHostile();
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties foodProperties = itemstack.getFoodProperties(this);
                if (foodProperties != null){
                    this.heal((float)foodProperties.getNutrition());
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.gameEvent(GameEvent.EAT, this);
                    this.eat(this.level, itemstack);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    pPlayer.swing(pHand);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
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

}
