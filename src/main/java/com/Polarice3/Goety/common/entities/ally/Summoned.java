package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
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
        this.followGoal();
        this.targetSelectGoal();
    }

    public void followGoal(){
        this.goalSelector.addGoal(5, new FollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F));
    }

    public void targetRetaliateGoal() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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
            for (int i = 0; i < pLevel.random.nextInt(10) + 10; ++i) {
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

    public static class FollowOwnerGoal<T extends Mob & IServant> extends Goal {
        public final T summonedEntity;
        public LivingEntity owner;
        public final LevelReader level;
        public final double followSpeed;
        public final PathNavigation navigation;
        public int timeToRecalcPath;
        public final float stopDistance;
        public final float startDistance;
        public float oldWaterCost;

        public FollowOwnerGoal(T summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.level = summonedEntity.level;
            this.followSpeed = speed;
            this.navigation = summonedEntity.getNavigation();
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(summonedEntity.getNavigation() instanceof GroundPathNavigation) && !(summonedEntity.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null){
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(Mth.square(this.stopDistance)));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
                if (this.summonedEntity.getControlledVehicle() != null){
                    this.navigation.moveTo(this.owner, this.followSpeed + 0.25D);
                    if (this.summonedEntity.getControlledVehicle() instanceof Mob mob){
                        mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                    }
                } else if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                        if (this.owner instanceof Mob){
                            flag |= !this.summonedEntity.hasLineOfSight(this.owner) && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                        } else {
                            flag &= this.canTeleport();
                        }
                        if (flag) {
                            this.tryToTeleportNearEntity();
                        } else {
                            this.navigation.moveTo(this.owner, this.followSpeed);
                        }
                    }
                }
            }
        }

        protected boolean canTeleport() {
            return MobsConfig.ServantTeleport.get();
        }

        protected void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        protected boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        protected boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        protected int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

    public static class FollowOwnerWaterGoal extends Goal {
        protected final Summoned summonedEntity;
        private LivingEntity owner;
        private final LevelReader level;
        private final double followSpeed;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private final PathNavigation navigation;
        private final float maxDist;
        private final float minDist;

        public FollowOwnerWaterGoal(Summoned summonedEntity, double speed, float minDist, float maxDist) {
            this.summonedEntity = summonedEntity;
            this.level = summonedEntity.level;
            this.followSpeed = speed;
            this.navigation = summonedEntity.getNavigation();
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else if (this.summonedEntity.distanceTo(livingentity) >= 1024.0F) {
                return false;
            } else if (!this.summonedEntity.isFollowing()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                if (!livingentity.isAlive()) {
                    return false;
                } else {
                    this.path = this.summonedEntity.getNavigation().createPath(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    }
                }
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null){
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double)(this.maxDist * this.maxDist));
            }
        }

        public void start() {
            this.summonedEntity.getNavigation().moveTo(this.path, this.followSpeed);
            this.ticksUntilNextPathRecalculation = 0;
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
        }

        public void tick() {
            this.summonedEntity.getLookControl().setLookAt(this.owner, 30.0F, 30.0F);
            double d0 = this.summonedEntity.distanceToSqr(this.owner.getX(), this.owner.getY(), this.owner.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || this.owner.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.summonedEntity.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = this.owner.getX();
                this.pathedTargetY = this.owner.getY();
                this.pathedTargetZ = this.owner.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.summonedEntity.getRandom().nextInt(7);
                double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                boolean flag = d0 > Mth.square(range);
                if (this.owner instanceof Mob){
                    flag |= !this.summonedEntity.hasLineOfSight(this.owner) && d0 >= Mth.square(8.0D);
                } else {
                    flag &= MobsConfig.ServantTeleport.get();
                }
                if (flag){
                    this.tryToTeleportNearEntity();
                }
                if (d0 > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for(int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.level.noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        private int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

    public static class WanderGoal<T extends PathfinderMob & IServant> extends RandomStrollGoal {
        public final T summonedEntity;
        protected final float probability;

        public WanderGoal(T entity, double speedModifier) {
            this(entity, speedModifier, 0.001F);
        }

        public WanderGoal(T entity, double speedModifier, float probability) {
            this(entity, speedModifier, 120, probability);
        }

        public WanderGoal(T entity, double speedModifier, int interval, float probability) {
            super(entity, speedModifier, interval, false);
            this.summonedEntity = entity;
            this.probability = probability;
        }

        public boolean canUse() {
            if (super.canUse()){
                return (!this.summonedEntity.isStaying() && !this.summonedEntity.isCommanded() || this.summonedEntity.getTrueOwner() == null) && !(this.summonedEntity.getNavigation() instanceof WaterBoundPathNavigation);
            } else {
                return false;
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.summonedEntity.isGuardingArea()){
                return randomBoundPos();
            } else if (this.mob.isInWaterOrBubble()) {
                Vec3 vec3 = this.landRandomPos(15, 7);
                return vec3 == null ? this.defaultRandomPos() : vec3;
            } else {
                return this.mob.getRandom().nextFloat() >= this.probability ? this.landRandomPos(10, 7) : this.defaultRandomPos();
            }
        }

        public Vec3 defaultRandomPos(){
            return super.getPosition();
        }

        @Nullable
        public Vec3 landRandomPos(int xz, int y){
            if (this.summonedEntity.getTrueOwner() != null
                    && this.summonedEntity.isFollowing()){
                Vec3 vec3 = null;

                for (int i = 0; i < 10; ++i){
                    BlockPos blockPos = this.summonedEntity.getTrueOwner().blockPosition()
                            .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz),
                                    this.summonedEntity.getRandom().nextIntBetweenInclusive(-y, y),
                                    this.summonedEntity.getRandom().nextIntBetweenInclusive(-xz, xz));
                    BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                    if (blockPos1 != null){
                        vec3 = Vec3.atBottomCenterOf(blockPos1);
                        break;
                    }
                }

                return vec3;
            }
            return LandRandomPos.getPos(this.mob, xz, y);
        }

        public Vec3 randomBoundPos(){
            Vec3 vec3 = null;
            int range = GUARDING_RANGE / 2;

            for (int i = 0; i < 10; ++i){
                BlockPos blockPos = this.summonedEntity.getBoundPos()
                        .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
                BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                if (blockPos1 != null){
                    vec3 = Vec3.atBottomCenterOf(blockPos1);
                    break;
                }
            }

            return vec3;
        }
    }

    public class WaterWanderGoal<T extends PathfinderMob & IServant> extends RandomStrollGoal {
        private final T summonedEntity;

        public WaterWanderGoal(T entity) {
            this(entity, 1.0D);
        }

        public WaterWanderGoal(T entity, double speedModifier) {
            this(entity, speedModifier, 120);
        }

        public WaterWanderGoal(T entity, double speedModifier, int interval) {
            super(entity, speedModifier, interval, false);
            this.summonedEntity = entity;
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.summonedEntity.isGuardingArea()){
                return randomBoundPos();
            }
            return super.getPosition();
        }

        public Vec3 randomBoundPos(){
            Vec3 vec3 = null;
            int range = GUARDING_RANGE / 2;

            for (int i = 0; i < 10; ++i){
                BlockPos blockPos = this.summonedEntity.getBoundPos()
                        .offset(this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range),
                                this.summonedEntity.getRandom().nextIntBetweenInclusive(-range, range));
                if (this.summonedEntity.getNavigation() instanceof WaterBoundPathNavigation){
                    if (GoalUtils.isWater(this.summonedEntity, blockPos)){
                        vec3 = Vec3.atBottomCenterOf(blockPos);
                        break;
                    }
                } else {
                    BlockPos blockPos1 = LandRandomPos.movePosUpOutOfSolid(this.summonedEntity, blockPos);
                    if (blockPos1 != null){
                        vec3 = Vec3.atBottomCenterOf(blockPos1);
                        break;
                    }
                }
            }

            return vec3;
        }

        public boolean canUse() {
            if (super.canUse()){
                return (!Summoned.this.isStaying() && !Summoned.this.isCommanded()) || Summoned.this.getTrueOwner() == null;
            } else {
                return false;
            }
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

    public static class ReturnToGuardPos<T extends PathfinderMob & IServant> extends MoveToBlockGoal {
        protected final T servant;
        public int range;

        public ReturnToGuardPos(T servant, double speed, int range) {
            super(servant, speed, range);
            this.servant = servant;
            this.range = range;
        }

        @Override
        public boolean canUse() {
            if (this.servant.isGuardingArea()) {
                if (super.canUse()) {
                    return this.servant.distanceToSqr(this.servant.vec3BoundPos()) > Mth.square(this.range);
                }
            }
            return false;
        }

        protected boolean findNearestBlock() {
            if (this.servant.isGuardingArea()) {
                this.blockPos = this.servant.getBoundPos();
                return this.blockPos != null;
            }
            return false;
        }

        @Override
        protected boolean isValidTarget(LevelReader p_25619_, BlockPos p_25620_) {
            if (this.servant.isGuardingArea()) {
                return BlockFinder.samePos(this.servant.getBoundPos(), p_25620_);
            }
            return false;
        }
    }

    public static class GoToWaterGoal extends Goal {
        private final Summoned mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public GoToWaterGoal(Summoned p_i48910_1_, double p_i48910_2_) {
            this.mob = p_i48910_1_;
            this.speedModifier = p_i48910_2_;
            this.level = p_i48910_1_.level;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.getTrueOwner() != null){
                if (!this.mob.getTrueOwner().isInWater()){
                    return false;
                }
            } else if (!this.level.isDay()) {
                return false;
            }
            if (this.mob.isInWater()) {
                return false;
            }
            Vec3 vector3d = this.getWaterPos();
            if (vector3d == null) {
                return false;
            } else {
                this.wantedX = vector3d.x;
                this.wantedY = vector3d.y;
                this.wantedZ = vector3d.z;
                return true;
            }
        }

        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            RandomSource random = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }

    public static class NaturalAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        protected Summoned summoned;

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass) {
            this(summoned, tClass, 10, true, null);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, boolean pMustSee) {
            this(summoned, tClass, 10, pMustSee, null);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
            this(summoned, tClass, 10, pMustSee, predicate);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, int time, boolean pMustSee, @Nullable Predicate<LivingEntity> predicate) {
            this(summoned, tClass, time, pMustSee, false, predicate);
        }

        public NaturalAttackGoal(Summoned summoned, Class<T> tClass, int time, boolean pMustSee, boolean pMustReach, Predicate<LivingEntity> predicate) {
            super(summoned, tClass, time, pMustSee, pMustReach, predicate);
            this.summoned = summoned;
        }

        public boolean canUse() {
            return super.canUse() && this.summoned.isNatural() && this.summoned.getTrueOwner() == null && this.target != null;
        }
    }
}
