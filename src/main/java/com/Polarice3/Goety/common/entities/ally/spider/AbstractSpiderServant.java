package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.Tags;

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
    public static int PATROL_RANGE = MobsConfig.ServantPatrolRange.get();
    private final NearestAttackableTargetGoal<Player> targetGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);
    public boolean limitedLifespan;
    public int limitedLifeTicks;
    public LivingEntity commandPosEntity;
    public BlockPos commandPos;
    public BlockPos boundPos;
    public int commandTick;
    public int killChance;
    public int noHealTime;

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
        if (!this.level.isClientSide) {
            if (!this.isHostile()) {
                if (this.tickCount <= 10) {
                    if (this.getFirstPassenger() instanceof AbstractSkeleton){
                        this.getFirstPassenger().discard();
                    }
                }
            }
            if (!this.hasEffect(GoetyEffects.WILD_RAGE.get())) {
                if (this.getTarget() instanceof IOwned ownedEntity) {
                    if (this.getTrueOwner() != null && (ownedEntity.getTrueOwner() == this.getTrueOwner())) {
                        this.setTarget(null);
                        if (this.getLastHurtByMob() == ownedEntity) {
                            this.setLastHurtByMob(null);
                        }
                    }
                    if (ownedEntity.getTrueOwner() == this) {
                        this.setTarget(null);
                        if (this.getLastHurtByMob() == ownedEntity) {
                            this.setLastHurtByMob(null);
                        }
                    }
                    if (MobUtil.ownerStack(this, ownedEntity)) {
                        this.setTarget(null);
                        if (this.getLastHurtByMob() == ownedEntity) {
                            this.setLastHurtByMob(null);
                        }
                    }
                }
                if (this.getTrueOwner() != null) {
                    if (this.getLastHurtByMob() == this.getTrueOwner()) {
                        this.setLastHurtByMob(null);
                    }
                }
            }
            if (this.getTrueOwner() != null) {
                if (this.getTrueOwner() instanceof Mob mobOwner) {
                    if (mobOwner.getTarget() != null && this.getTarget() == null) {
                        this.setTarget(mobOwner.getTarget());
                    }
                    if (mobOwner instanceof Apostle apostle) {
                        if (this.distanceTo(apostle) > 32) {
                            this.teleportTowards(apostle);
                        }
                    }
                    if (mobOwner.getType().is(Tags.EntityTypes.BOSSES)) {
                        if (mobOwner.isRemoved() || mobOwner.isDeadOrDying()) {
                            this.kill();
                        }
                    }
                }
                if (this.getTrueOwner() instanceof IOwned owned) {
                    if (this.getTrueOwner().isDeadOrDying() || !this.getTrueOwner().isAlive()) {
                        if (owned.getTrueOwner() != null) {
                            this.setTrueOwner(owned.getTrueOwner());
                        } else if (!this.isHostile() && !this.isNatural() && !(owned instanceof Enemy) && !owned.isHostile()) {
                            this.kill();
                        }
                    }
                }
                for (Mob target : this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)))) {
                    if (target instanceof IOwned owned) {
                        if (this.getTrueOwner() != owned.getTrueOwner()
                                && target.getTarget() == this.getTrueOwner()) {
                            this.setTarget(target);
                        }
                    }
                }
            }
            if (this.getTarget() != null) {
                if (this.getTarget().isRemoved() || this.getTarget().isDeadOrDying()) {
                    this.setTarget(null);
                }
            }
            this.mobSense();
            if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
                this.lifeSpanDamage();
            }
        }
        this.stayingMode();
        if (this.killChance > 0){
            --this.killChance;
        }
        if (MobsConfig.StayingServantChunkLoad.get()) {
            if (this.level instanceof ServerLevel serverLevel) {
                if (this.isStaying()) {
                    if (this.getTrueOwner() instanceof Player player) {
                        if (player.tickCount % 10 == 0) {
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    ChunkPos pos = new ChunkPos(this.blockPosition().offset(i * 16, 0, j * 16));
                                    serverLevel.setChunkForced(pos.x, pos.z, true);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.isCommanded()){
            if (this.getNavigation().isStableDestination(this.commandPos) || this.commandPosEntity != null){
                --this.commandTick;
                if (this.commandPosEntity != null){
                    this.getNavigation().moveTo(this.commandPosEntity, 1.25D);
                } else {
                    this.getNavigation().moveTo(this.commandPos.getX() + 0.5D, this.commandPos.getY(), this.commandPos.getZ() + 0.5D, 1.25D);
                }

                if (this.getNavigation().isStuck() || this.commandTick <= 0){
                    this.commandPosEntity = null;
                    this.commandPos = null;
                } else if (this.commandPos.closerToCenterThan(
                        this.getControlledVehicle() != null ? this.getControlledVehicle().position() : this.position(),
                        this.getControlledVehicle() != null ? this.getControlledVehicle().getBbWidth() + 1.0D : this.getBbWidth() + 1.0D)){
                    if (this.commandPosEntity != null &&
                            this.getBoundingBox().inflate(1.25D).intersects(this.commandPosEntity.getBoundingBox())){
                        this.commandPosEntity = null;
                    }
                    this.moveTo(this.commandPos, this.getYRot(), this.getXRot());
                    this.commandPos = null;
                }
            } else {
                this.commandPos = null;
            }
        }
        if (this.isWandering()){
            if (this.isStaying()) {
                this.setStaying(false);
            }
        }
        if (this.isPatrolling()){
            if (this.getTarget() != null){
                if (this.getTarget().distanceToSqr(this.vec3BoundPos()) > Mth.square(PATROL_RANGE)){
                    this.setTarget(null);
                    if (!this.isCommanded()){
                        this.getNavigation().moveTo(this.boundPos.getX(), this.boundPos.getY(), this.boundPos.getZ(), 1.0F);
                    }
                }
            }
            if (!this.isCommanded() && this.distanceToSqr(this.vec3BoundPos()) > Mth.square(PATROL_RANGE)){
                this.getNavigation().moveTo(this.boundPos.getX(), this.boundPos.getY(), this.boundPos.getZ(), 1.0F);
            }
        }
        if (this.noHealTime <= 0){
            this.healServant(this);
        } else {
            --this.noHealTime;
        }
        boolean flag = this.isSunSensitive() && this.isSunBurnTick() && MobsConfig.UndeadServantSunlightBurn.get();
        if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem() && MobsConfig.UndeadServantSunlightHelmet.get()) {
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        this.broadcastBreakEvent(EquipmentSlot.HEAD);
                        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.setSecondsOnFire(8);
            }
        }
    }

    public void stayingMode(){
        AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null) {
            if (this.isStaying()){
                if (this.navigation.getPath() != null) {
                    this.navigation.stop();
                }
                if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    modifiableattributeinstance.removeModifier(SPEED_MODIFIER);
                    modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER);
                }
                this.stayingPosition();
                if (this.isWandering()) {
                    this.setWandering(false);
                }
            } else {
                if (modifiableattributeinstance.hasModifier(SPEED_MODIFIER)) {
                    modifiableattributeinstance.removeModifier(SPEED_MODIFIER);
                }
            }
        }
    }

    public void mobSense(){
        if (MobsConfig.MobSense.get()) {
            if (this.isAlive()) {
                if (this.getTarget() != null) {
                    if (this.getTarget() instanceof Mob mob) {
                        if (mob.getTarget() == null || mob.getTarget().isDeadOrDying()){
                            mob.setTarget(this);
                        }
                        if (!mob.getBrain().isActive(Activity.FIGHT) && !(mob instanceof Warden)) {
                            mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, this.getUUID(), 600L);
                            mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this, 600L);
                        }
                    }
                }
            }
        }
    }

    public void stayingPosition(){
        if (this.getTarget() != null){
            this.getLookControl().setLookAt(this.getTarget(), this.getMaxHeadYRot(), this.getMaxHeadXRot());
            double d2 = this.getTarget().getX() - this.getX();
            double d1 = this.getTarget().getZ() - this.getZ();
            this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            this.yBodyRot = this.getYRot();
        }
    }

    protected boolean isSunSensitive() {
        return false;
    }

    protected void updateNoActionTime() {
        if (this.isHostile()) {
            float f = this.getLightLevelDependentMagicValue();
            if (f > 0.5F) {
                this.noActionTime += 2;
            }
        }
    }

    public void lifeSpanDamage(){
        this.limitedLifeTicks = 20;
        this.hurt(this.damageSources().starve(), 1.0F);
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
            return trueOwner.isAlliedTo(entityIn)
                    || entityIn.isAlliedTo(trueOwner)
                    || entityIn == trueOwner
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
        this.entityData.define(SUMMONED_FLAGS, (byte)0);
        this.entityData.define(UPGRADE_FLAGS, (byte)0);
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
        if (compound.contains("Upgraded")) {
            this.setUpgraded(compound.getBoolean("Upgraded"));
        }
        if (compound.contains("wandering")) {
            this.setWandering(compound.getBoolean("wandering"));
        }
        if (compound.contains("staying")) {
            this.setStaying(compound.getBoolean("staying"));
        }
        if (compound.contains("commandPos")){
            this.commandPos = NbtUtils.readBlockPos(compound.getCompound("commandPos"));
        }
        if (compound.contains("commandPosEntity")){
            if (EntityFinder.getLivingEntityByUuiD(compound.getUUID("commandPosEntity")) != null) {
                this.commandPosEntity = EntityFinder.getLivingEntityByUuiD(compound.getUUID("commandPosEntity"));
            }
        }
        if (compound.contains("boundPos")){
            this.boundPos = NbtUtils.readBlockPos(compound.getCompound("boundPos"));
        }
        if (compound.contains("noHealTime")){
            this.noHealTime = compound.getInt("noHealTime");
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
        compound.putBoolean("Upgraded", this.isUpgraded());
        compound.putBoolean("wandering", this.isWandering());
        compound.putBoolean("staying", this.isStaying());
        if (this.commandPos != null){
            compound.put("commandPos", NbtUtils.writeBlockPos(this.commandPos));
        }
        if (this.commandPosEntity != null) {
            compound.putUUID("commandPosEntity", this.commandPosEntity.getUUID());
        }
        compound.putInt("commandTick", this.commandTick);
        if (this.boundPos != null){
            compound.put("boundPos", NbtUtils.writeBlockPos(this.boundPos));
        }
        compound.putInt("noHealTime", this.noHealTime);
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

    public void setFollowing(){
        this.setBoundPos(null);
        this.setWandering(false);
        this.setStaying(false);
    }

    public boolean canUpdateMove(){
        return true;
    }

    public void updateMoveMode(Player player){
        if (!this.isWandering() && !this.isStaying() && !this.isPatrolling()){
            this.setBoundPos(null);
            this.setWandering(true);
            this.setStaying(false);
            player.displayClientMessage(Component.translatable("info.goety.servant.wander", this.getDisplayName()), true);
        } else if (!this.isStaying() && !this.isPatrolling()){
            this.setBoundPos(null);
            this.setWandering(false);
            this.setStaying(true);
            player.displayClientMessage(Component.translatable("info.goety.servant.staying", this.getDisplayName()), true);
        } else if (!this.isPatrolling()){
            this.setBoundPos(this.blockPosition());
            this.setWandering(false);
            this.setStaying(false);
            player.displayClientMessage(Component.translatable("info.goety.servant.patrol", this.getDisplayName()), true);
        } else {
            this.setBoundPos(null);
            this.setWandering(false);
            this.setStaying(false);
            player.displayClientMessage(Component.translatable("info.goety.servant.follow", this.getDisplayName()), true);
        }
        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);

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

    public void setCommandPos(BlockPos blockPos){
        this.setCommandPos(blockPos, true);
    }

    public void setCommandPos(BlockPos blockPos, boolean removeEntity){
        if (removeEntity) {
            this.commandPosEntity = null;
        }
        this.commandPos = blockPos;
        this.commandTick = MathHelper.secondsToTicks(10);
    }

    public void setCommandPosEntity(LivingEntity living){
        this.commandPosEntity = living;
        this.setCommandPos(living.blockPosition(), false);
    }

    public boolean isCommanded(){
        return this.commandPos != null;
    }

    public BlockPos getBoundPos(){
        return this.boundPos;
    }

    public void setBoundPos(BlockPos blockPos){
        this.boundPos = blockPos;
    }

    public Vec3 vec3BoundPos(){
        return Vec3.atBottomCenterOf(this.boundPos);
    }

    public void dropEquipment(EquipmentSlot equipmentSlot, ItemStack stack){
        if (this.getEquipmentDropChance(equipmentSlot) > 0.0F) {
            this.spawnAtLocation(stack);
        }
    }

    public boolean isMoving() {
        return !(this.walkAnimation.speed() < 0.01F);
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
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.checkHostility();
        if (pReason != MobSpawnType.MOB_SUMMONED && this.getTrueOwner() == null){
            this.setNatural(true);
        }
        this.setWandering(this.getTrueOwner() == null);
        this.setStaying(false);
        return pSpawnData;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (MobsConfig.MinionsMasterImmune.get()) {
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

    public void setTrueOwner(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
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

    @Override
    public void awardKillScore(Entity entity, int p_19954_, DamageSource damageSource) {
        super.awardKillScore(entity, p_19954_, damageSource);
        if (this.getMasterOwner() instanceof ServerPlayer serverPlayer) {
            ModCriteriaTriggers.SERVANT_KILLED_ENTITY.trigger(serverPlayer, entity, damageSource);
        }
    }

    public void teleportTowards(Entity entity) {
        if (!this.level.isClientSide() && this.isAlive()) {
            for(int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, d1, d2, d3);
                if (event.isCanceled()) {
                    break;
                }
                if (this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    public void teleportHits(){
        this.level.broadcastEntityEvent(this, (byte) 46);
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
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

}
