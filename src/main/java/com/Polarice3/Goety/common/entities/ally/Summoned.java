package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.ServantHurtByTargetGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ai.servant.*;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Summoned extends Owned implements IServant {
    protected static final EntityDataAccessor<Byte> SUMMONED_FLAGS = SynchedEntityData.defineId(Summoned.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> UPGRADE_FLAGS = SynchedEntityData.defineId(Summoned.class, EntityDataSerializers.BYTE);
    @Nullable
    private LivingEntity priorityTarget;
    public LivingEntity commandPosEntity;
    public BlockPos commandPos;
    public BlockPos priorityPos;
    public BlockPos boundPos;
    public String boundDim = Level.OVERWORLD.location().toString();
    public List<GlobalPos> patrolList = new ArrayList<>();
    public int patrolIndex = 0;
    public int priorityTime;
    public int commandTick;
    public int killChance;
    public int noHealTime;
    public long ticketTime = 0;

    public Summoned(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetRetaliateGoal();
        this.patrolGoal();
        this.followGoal();
        this.targetSelectGoal();
    }

    public void patrolGoal() {
        this.goalSelector.addGoal(2, new ServantPatrolGoal<>(this, this.getCommandSpeed()));
    }

    public void followGoal(){
        this.goalSelector.addGoal(5, new FollowOwnerGoal<>(this, this.getFollowSpeed(), 10.0F, 2.0F));
    }

    public void targetRetaliateGoal() {
        this.targetSelector.addGoal(1, new ServantHurtByTargetGoal(this));
    }

    public void targetSelectGoal(){
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob);
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public void checkDespawn() {
        if (this.isHostile()){
            super.checkDespawn();
        }
    }

    public ItemStack getProjectile(ItemStack pShootable) {
        if (pShootable.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem)pShootable.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void tick(){
        super.tick();
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

    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean servantSunBurn() {
        return this.isSunSensitive();
    }

    @Override
    public boolean burnSunTick() {
        return this.isSunBurnTick();
    }

    public List<GlobalPos> getPatrolRoute() {
        return this.patrolList;
    }

    public void setPatrolRoute(List<GlobalPos> list) {
        this.patrolList = list;
    }

    public int getPatrolIndex() {
        return this.patrolIndex;
    }

    public void setPatrolIndex(int index) {
        this.patrolIndex = index;
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

    @Override
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

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.summonParticles(pLevel.getLevel(), pReason);
        if (this.getTrueOwner() != null){
            this.spawnUpgraded();
        }
        this.setWandering(this.getTrueOwner() == null);
        this.setStaying(false);
        this.setBoundPos(null);
        return pSpawnData;
    }

    public void summonParticles(ServerLevel pLevel, MobSpawnType pReason) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            for (int i = 0; i < pLevel.getRandom().nextInt(10) + 10; ++i) {
                pLevel.sendParticles(ModParticleTypes.SUMMON.get(), this.getRandomX(1.5D), this.getRandomY(), this.getRandomZ(1.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
            }
            if (this.getMobType() == MobType.UNDEAD) {
                pLevel.sendParticles(ModParticleTypes.SOUL_EXPLODE.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 2.0D, 0, 1.0F);
            }
        }
    }

    public boolean canSpawnArmor(){
        return this.getTrueOwner() != null && this.getSpawnType() != MobSpawnType.CONVERSION && CuriosFinder.hasCurio(this.getTrueOwner(), ModItems.RING_OF_THE_FORGE.get());
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        if (this.canSpawnArmor()){
            this.populateDefaultArmor(p_217055_, p_217056_);
        } else {
            super.populateDefaultEquipmentSlots(p_217055_, p_217056_);
        }
        this.populateDefaultWeapons(p_217055_, p_217056_);
    }

    public void populateDefaultArmor(RandomSource randomSource, DifficultyInstance difficulty) {
        if (this.canSpawnArmor()){
            this.spawnArmor(randomSource);
        }
    }

    public void spawnArmor(RandomSource randomSource) {
        for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR) {
                int i = randomSource.nextInt(2);
                float baseChance = 0.095F;
                if (this.getTrueOwner() != null) {
                    baseChance += (float) this.getTrueOwner().getAttributeValue(Attributes.LUCK) * 0.05F;
                }
                baseChance = Math.min(baseChance, 0.5F);

                if (randomSource.nextFloat() < baseChance) {
                    ++i;
                }

                if (randomSource.nextFloat() < baseChance) {
                    ++i;
                }

                if (randomSource.nextFloat() < baseChance) {
                    ++i;
                }
                ItemStack itemstack = this.getItemBySlot(equipmentslot);
                if (itemstack.isEmpty()) {
                    Item item = getEquipmentForSlot(equipmentslot, i);
                    if (item != null) {
                        this.setItemSlot(equipmentslot, new ItemStack(item));
                        this.setDropChance(equipmentslot, 0.0F);
                    }
                }
            }
        }
    }

    public void populateDefaultWeapons(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    public void die(DamageSource pCause) {
        if (!this.level.isClientSide && this.hasCustomName() && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getTrueOwner() instanceof ServerPlayer) {
            this.getTrueOwner().sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }
        super.die(pCause);
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
            this.setNoHealTime(MathHelper.secondsToTicks(MobsConfig.ServantHealHalt.get()));
        }
        return flag;
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.getMobType() == MobType.UNDEAD){
                float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                    entityIn.setSecondsOnFire(2 * (int)f);
                }
            }
            if (!this.getMainHandItem().isEmpty() && this.getMainHandItem().isDamageableItem()){
                ItemHelper.hurtAndBreak(this.getMainHandItem(), 1, this);
            }
        }

        return flag;
    }

    protected void hurtArmor(DamageSource pDamageSource, float pDamage) {
        if (!(pDamage <= 0.0F)) {
            pDamage = pDamage / 4.0F;
            if (pDamage < 1.0F) {
                pDamage = 1.0F;
            }

            for(EquipmentSlot equipmentSlotType : EquipmentSlot.values()) {
                if (equipmentSlotType.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack itemstack = this.getItemBySlot(equipmentSlotType);
                    if ((!pDamageSource.is(DamageTypeTags.IS_FIRE) || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
                        itemstack.hurtAndBreak((int) pDamage, this, (p_214023_1_) -> {
                            p_214023_1_.broadcastBreakEvent(equipmentSlotType);
                        });
                    }
                }
            }

        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SUMMONED_FLAGS, (byte)0);
        this.entityData.define(UPGRADE_FLAGS, (byte)0);
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
        return this.getFlag(2) && !this.isCommanded() && this.getControllingPassenger() == null;
    }

    public void setStaying(boolean staying){
        this.setFlags(2, staying);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readServantData(compound);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.saveServantData(compound);
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

    @Override
    public String getBoundDim() {
        return this.boundDim;
    }

    public void setBoundDim(String string) {
        this.boundDim = string;
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
        this.hurt(ModDamageSource.getDamageSource(this.level, ModDamageSource.DISMISSED), Float.MAX_VALUE);
    }

    @Override
    public void push(Entity p_21294_) {
        if (!this.level.isClientSide) {
            if (!this.isStaying()) {
                super.push(p_21294_);
            }
        }
    }

    protected void doPush(Entity p_20971_) {
        if (!this.level.isClientSide) {
            if (!this.isStaying()) {
                super.doPush(p_20971_);
            }
        }
    }

    public boolean canCollideWith(Entity p_20303_) {
        if (!this.isStaying()){
            return super.canCollideWith(p_20303_);
        } else {
            return false;
        }
    }

    public DamageSource getServantAttack(){
        return MobUtil.getServantAttack(this);
    }

    public static class FollowOwnerGoal<T extends Mob & IServant> extends ServantFollowOwnerGoal<T> {

        public FollowOwnerGoal(T summonedEntity, double speed, float startDistance, float stopDistance) {
            super(summonedEntity, speed, startDistance, stopDistance);
        }
    }

    public static class FollowOwnerWaterGoal extends ServantFollowOwnerWaterGoal<Summoned> {

        public FollowOwnerWaterGoal(Summoned summonedEntity, double speed, float minDist, float maxDist) {
            super(summonedEntity, speed, minDist, maxDist);
        }
    }

    public static class WanderGoal<T extends PathfinderMob & IServant> extends ServantWanderGoal<T> {

        public WanderGoal(T entity, double speedModifier) {
            super(entity, speedModifier, 0.001F);
        }

        public WanderGoal(T entity, double speedModifier, float probability) {
            super(entity, speedModifier, 120, probability);
        }

        public WanderGoal(T entity, double speedModifier, int interval, float probability) {
            super(entity, speedModifier, interval, probability);
        }
    }

    public class WaterWanderGoal<T extends PathfinderMob & IServant> extends ServantWaterWanderGoal<T> {

        public WaterWanderGoal(T entity) {
            super(entity, 1.0D);
        }

        public WaterWanderGoal(T entity, double speedModifier) {
            super(entity, speedModifier, 120);
        }

        public WaterWanderGoal(T entity, double speedModifier, int interval) {
            super(entity, speedModifier, interval);
        }
    }

    public static class HoverWanderGoal<T extends PathfinderMob & IServant> extends WanderGoal<T>{

        public HoverWanderGoal(T entity, double speedModifier) {
            super(entity, speedModifier);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.summonedEntity.isGuardingArea()){
                return super.getPosition();
            } else {
                Vec3 vec3 = this.summonedEntity.getViewVector(0.0F);
                int i = 8;
                Vec3 vec31 = HoverRandomPos.getPos(this.summonedEntity, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
                return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.summonedEntity, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
            }
        }
    }

    public static class ReturnToGuardPos<T extends PathfinderMob & IServant> extends ServantReturnToGuardPos<T> {

        public ReturnToGuardPos(T servant, double speed, int range) {
            super(servant, speed, range);
        }
    }

    public static class GoToWaterGoal extends ServantGoToWaterGoal<Summoned> {

        public GoToWaterGoal(Summoned p_i48910_1_, double p_i48910_2_) {
            super(p_i48910_1_, p_i48910_2_);
        }
    }

    public static class NaturalAttackGoal<T extends LivingEntity> extends ServantNaturalAttackGoal<T, Summoned> {

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass) {
            super(summoned, tClass, 10, true, null);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, boolean pMustSee) {
            super(summoned, tClass, 10, pMustSee, null);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
            super(summoned, tClass, 10, pMustSee, predicate);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, int time, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
            super(summoned, tClass, time, pMustSee, false, predicate);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, int time, boolean pMustSee, boolean pMustReach, Predicate<LivingEntity> predicate) {
            super(summoned, tClass, time, pMustSee, pMustReach, predicate);
        }
    }
}
