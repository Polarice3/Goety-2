package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.api.blocks.entities.IWindPowered;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SquallGolem extends AbstractGolemServant implements IWindPowered {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(SquallGolem.class, EntityDataSerializers.BYTE);
    private int startingUpTick;
    private int shuttingDownTick;
    public int activeTime;
    public int attackTick;
    private int idleTime;
    public int noveltyTick;
    public int homeTick;
    public boolean isNovelty = false;
    public boolean requiresPower = true;
    public boolean proximity = false;
    public BlockPos homePos;
    public AnimationState activateAnimationState = new AnimationState();
    public AnimationState deactivateAnimationState = new AnimationState();
    public AnimationState offAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState alertAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();

    public SquallGolem(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new SquallGolem.MeleeGoal());
        this.goalSelector.addGoal(5, new SquallGolem.AttackGoal(1.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && SquallGolem.this.isNotProcessing();
            }
        });
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && SquallGolem.this.isNotProcessing();
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SquallGolemHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SquallGolemDamage.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SquallGolemHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SquallGolemDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActiveTime", this.activeTime);
        pCompound.putInt("StartingUpTick", this.startingUpTick);
        pCompound.putInt("ShuttingDownTick", this.shuttingDownTick);
        pCompound.putBoolean("Activated", this.isActivated());
        pCompound.putBoolean("RequiresPower", this.requiresPower());
        pCompound.putBoolean("Proximity", this.isProximity());
        if (this.getHomePos() != null){
            pCompound.put("HomePos", NbtUtils.writeBlockPos(this.getHomePos()));
        }
        pCompound.putInt("HomeTick", this.homeTick);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActiveTime")){
            this.activeTime = pCompound.getInt("ActiveTime");
        }
        if (pCompound.contains("StartingUpTick")) {
            this.startingUpTick = pCompound.getInt("StartingUpTick");
        }
        if (pCompound.contains("ShuttingDownTick")) {
            this.shuttingDownTick = pCompound.getInt("ShuttingDownTick");
        }
        if (pCompound.contains("Activated")) {
            this.setActivated(pCompound.getBoolean("Activated"));
        }
        if (pCompound.contains("RequiresPower")){
            this.setRequiresPower(pCompound.getBoolean("RequiresPower"));
        }
        if (pCompound.contains("Proximity")){
            this.setProximity(pCompound.getBoolean("Proximity"));
        }
        if (pCompound.contains("HomePos")){
            this.homePos = NbtUtils.readBlockPos(pCompound.getCompound("HomePos"));
        }
        if (pCompound.contains("HomeTick")){
            this.homeTick = pCompound.getInt("HomeTick");
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource p_21239_) {
        return ModSounds.SQUALL_GOLEM_HURT.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos p_20135_, @NotNull BlockState p_20136_) {
        this.playSound(ModSounds.SQUALL_GOLEM_STEP.get(), 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SQUALL_GOLEM_DEATH.get();
    }

    private boolean getGolemFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setGolemFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getGolemFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setGolemFlags(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public boolean isStartingUp() {
        return this.getGolemFlag(2);
    }

    public void setStartingUp(boolean activating) {
        this.setGolemFlags(2, activating);
    }

    public boolean isActivated() {
        return this.getGolemFlag(4);
    }

    public void setActivated(boolean activated) {
        this.setGolemFlags(4, activated);
    }

    public boolean isShuttingDown() {
        return this.getGolemFlag(8);
    }

    public void setShuttingDown(boolean deactivating) {
        this.setGolemFlags(8, deactivating);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isStartingUp() || this.isShuttingDown() || !this.isActivated();
    }

    public boolean hasLineOfSight(@NotNull Entity p_149755_) {
        return !this.isStartingUp() && !this.isShuttingDown() && this.isActivated() && super.hasLineOfSight(p_149755_);
    }

    @Override
    public int xpReward() {
        return 20;
    }

    @Override
    public boolean isWandering() {
        return false;
    }

    @Override
    public void updateMoveMode(Player player) {
        if (!this.isStaying()){
            this.setStaying(true);
            player.displayClientMessage(Component.translatable("info.goety.servant.staying", this.getDisplayName()), true);
        } else {
            this.setStaying(false);
            player.displayClientMessage(Component.translatable("info.goety.servant.follow", this.getDisplayName()), true);
        }
        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason != MobSpawnType.MOB_SUMMONED && pReason != MobSpawnType.COMMAND){
            this.setActivated(true);
            this.setRequiresPower(false);
        } else {
            this.setActivated(false);
        }
        this.setStartingUp(false);
        this.setShuttingDown(false);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.activateAnimationState);
        animationStates.add(this.deactivateAnimationState);
        animationStates.add(this.offAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.alertAnimationState);
        animationStates.add(this.walkAnimationState);
        return animationStates;
    }

    public void stopMostAnimations(AnimationState animationState0){
        for (AnimationState animationState : this.getAnimations()){
            if (animationState != animationState0) {
                animationState.stop();
            }
        }
    }

    public void stopAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    public void tick() {
        super.tick();
        if (this.isDeadOrDying()){
            this.stopAnimations();
        }
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (!this.isStartingUp() && !this.isShuttingDown()){
                    if (!this.isActivated()){
                        this.stopMostAnimations(this.offAnimationState);
                        this.offAnimationState.startIfStopped(this.tickCount);
                    } else {
                        if (!this.isMeleeAttacking()) {
                            this.attackAnimationState.stop();
                            if (this.isMoving()) {
                                this.stopMostAnimations(this.walkAnimationState);
                                this.walkAnimationState.startIfStopped(this.tickCount);
                            } else {
                                if (this.isNovelty){
                                    this.stopMostAnimations(this.alertAnimationState);
                                    this.alertAnimationState.startIfStopped(this.tickCount);
                                } else {
                                    this.stopMostAnimations(this.idleAnimationState);
                                    this.idleAnimationState.startIfStopped(this.tickCount);
                                }
                            }
                        } else if (this.isMeleeAttacking()) {
                            ++this.attackTick;
                        }
                    }
                } else if (this.isStartingUp()){
                    this.stopMostAnimations(this.activateAnimationState);
                    this.activateAnimationState.startIfStopped(this.tickCount);
                } else if (this.isShuttingDown()){
                    this.stopMostAnimations(this.deactivateAnimationState);
                    this.deactivateAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        if (!this.level.isClientSide) {
            if (this.isAlive()){
                if (this.isStartingUp() || this.isActivated()) {
                    if (this.tickCount % 7 == 0) {
                        if (!this.isAggressive()) {
                            this.playSound(ModSounds.SQUALL_GOLEM_WIND_SLOW.get());
                        } else {
                            this.playSound(ModSounds.SQUALL_GOLEM_WIND_FAST.get());
                        }
                    }
                }
                if (this.proximity){
                    LivingEntity livingEntity = null;
                    for (LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox()
                            .inflate(4))){
                        if (SummonTargetGoal.predicate(this).test(living)){
                            livingEntity = living;
                        }
                    }
                    if (livingEntity != null){
                        this.activeTime = 100;
                    }
                }
                if (this.requiresPower) {
                    if (this.activeTime > 0) {
                        --this.activeTime;
                        if (!this.isStartingUp() && !this.isActivated()) {
                            this.setStartingUp(true);
                            this.level.broadcastEntityEvent(this, (byte) 25);
                        }
                    } else if (this.homePos != null){
                        ++this.homeTick;
                        if (this.getNavigation().isStableDestination(this.homePos)){
                            this.getNavigation().moveTo(this.homePos.getX() + 0.5D, this.homePos.getY(), this.homePos.getZ() + 0.5D, 1.0D);
                            if (this.getNavigation().isStuck() || this.homeTick >= MathHelper.secondsToTicks(10)){
                                this.homeTick = 0;
                                this.homePos = null;
                            } else if (this.homePos.closerToCenterThan(this.position(), this.getBbWidth() + 1.0D)){
                                this.moveTo(this.homePos, this.getYRot(), this.getXRot());
                                this.homePos = null;
                            }
                        } else {
                            this.homePos = null;
                        }
                    } else {
                        if (!this.isShuttingDown() && this.isActivated()) {
                            this.setShuttingDown(true);
                            this.level.broadcastEntityEvent(this, (byte) 26);
                        }
                    }
                }
                if (this.isStartingUp() && !this.isActivated()){
                    this.getNavigation().stop();
                    ++this.startingUpTick;
                    if (this.startingUpTick == 1){
                        this.playSound(ModSounds.SQUALL_GOLEM_ACTIVATE.get(), 2.0F, 1.0F);
                        this.playSound(ModSounds.SQUALL_GOLEM_WIND_START.get());
                    }
                    if (this.startingUpTick >= MathHelper.secondsToTicks(2.6F)){
                        this.setStartingUp(false);
                        this.level.broadcastEntityEvent(this, (byte) 27);
                        this.setActivated(true);
                        this.level.broadcastEntityEvent(this, (byte) 29);
                        this.startingUpTick = 0;
                    }
                } else if (this.isShuttingDown() && this.isActivated()){
                    this.getNavigation().stop();
                    ++this.shuttingDownTick;
                    if (this.shuttingDownTick == 1){
                        this.playSound(ModSounds.SQUALL_GOLEM_DEACTIVATE.get(), 2.0F, 1.0F);
                        this.playSound(ModSounds.SQUALL_GOLEM_WIND_START.get());
                    }
                    if (this.shuttingDownTick >= MathHelper.secondsToTicks(2.6F)){
                        this.setShuttingDown(false);
                        this.level.broadcastEntityEvent(this, (byte) 28);
                        this.setActivated(false);
                        this.level.broadcastEntityEvent(this, (byte) 30);
                        this.shuttingDownTick = 0;
                    }
                }
                if (!this.isStartingUp() && !this.isShuttingDown()) {
                    if (this.isMeleeAttacking()) {
                        ++this.attackTick;
                    }
                    if (this.isActivated() && !this.isMeleeAttacking() && !this.isMoving()) {
                        ++this.idleTime;
                        if (this.level.random.nextFloat() <= 0.05F && this.hurtTime <= 0 && (this.getTarget() == null || this.getTarget().isDeadOrDying()) && !this.isNovelty && this.idleTime >= MathHelper.secondsToTicks(10)) {
                            this.idleTime = 0;
                            this.isNovelty = true;
                            this.level.broadcastEntityEvent(this, (byte) 22);
                        }
                    } else {
                        this.isNovelty = false;
                        this.level.broadcastEntityEvent(this, (byte) 23);
                    }
                    if (this.isNovelty){
                        ++noveltyTick;
                        this.level.broadcastEntityEvent(this, (byte) 24);
                        if (this.noveltyTick >= 150 || this.getTarget() != null || this.hurtTime > 0){
                            this.isNovelty = false;
                            this.noveltyTick = 0;
                            this.level.broadcastEntityEvent(this, (byte) 23);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stayingMode() {
    }

    public boolean isNotActive(){
        return !this.isActivated() || this.isStartingUp() || this.isShuttingDown();
    }

    public boolean fullyInactive(){
        return !this.isActivated() && !this.isStartingUp() && !this.isShuttingDown();
    }

    public boolean isNotProcessing(){
        return !this.isStartingUp() && !this.isShuttingDown();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isNotActive()){
            if (!source.isBypassInvul()){
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 5){
            this.attackTick = 0;
        } else if (pId == 6){
            this.stopAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (pId == 8){
            this.stopAnimations();
            this.activateAnimationState.start(this.tickCount);
        } else if (pId == 11){
            this.attackAnimationState.stop();
        } else if (pId == 12){
            this.stopAnimations();
            this.deactivateAnimationState.start(this.tickCount);
        } else if (pId == 13){
            this.stopAnimations();
            this.offAnimationState.start(this.tickCount);
        } else if (pId == 17){
            this.setMeleeAttacking(true);
        } else if (pId == 18){
            this.setMeleeAttacking(false);
        } else if (pId == 19){
            this.setAggressive(true);
        } else if (pId == 31){
            this.setAggressive(false);
        } else if (pId == 22){
            this.isNovelty = true;
        } else if (pId == 23){
            this.isNovelty = false;
            this.noveltyTick = 0;
        } else if (pId == 24){
            ++this.noveltyTick;
        } else if (pId == 25){
            this.setStartingUp(true);
        } else if (pId == 26){
            this.setShuttingDown(true);
        } else if (pId == 27){
            this.setStartingUp(false);
        } else if (pId == 28){
            this.setShuttingDown(false);
        } else if (pId == 29){
            this.setActivated(true);
        } else if (pId == 30){
            this.setActivated(false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F + enemy.getBbWidth()) + 1.0D;
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
            this.level.broadcastEntityEvent(this, (byte) 17);
        }
        return true;
    }

    @Override
    public int activeTicks() {
        return this.activeTime;
    }

    @Override
    public void activate(int tick) {
        this.activeTime = tick;
    }

    public boolean requiresPower(){
        return this.requiresPower;
    }

    public void setRequiresPower(boolean power){
        this.requiresPower = power;
    }

    public boolean isProximity(){
        return this.proximity;
    }

    public void setProximity(boolean power){
        this.proximity = power;
    }

    public BlockPos getHomePos() {
        return this.homePos;
    }

    public void setHomePos(BlockPos blockPos){
        this.homePos = blockPos;
    }

    public void setCommandPos(BlockPos blockPos, boolean removeEntity){
        if (this.isActivated() && !this.isStartingUp() && !this.isShuttingDown()){
            super.setCommandPos(blockPos, removeEntity);
        }
    }

    public void setCommandPosEntity(LivingEntity living){
        if (this.isActivated() && !this.isStartingUp() && !this.isShuttingDown()){
            super.setCommandPosEntity(living);
        }
    }

    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((item == ModBlocks.JADE_BLOCK.get().asItem() || item == ModItems.JADE.get()) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (item == ModBlocks.JADE_BLOCK.get().asItem()){
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    } else {
                        this.heal((this.getMaxHealth() / 4.0F) / 8.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 0.25F, 1.0F);
                    }
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                } else if (itemstack.isEmpty() && this.fullyInactive()) {
                    float f = (float)Mth.floor((Mth.wrapDegrees(pPlayer.getYRot() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    this.setYRot(f);
                    this.setYHeadRot(f);
                    this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 0.5F);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(SquallGolem.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return SquallGolem.this.getTarget() != null
                    && SquallGolem.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            SquallGolem.this.setAggressive(true);
            SquallGolem.this.level.broadcastEntityEvent(SquallGolem.this, (byte) 19);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            SquallGolem.this.getNavigation().stop();
            SquallGolem.this.setAggressive(false);
            SquallGolem.this.level.broadcastEntityEvent(SquallGolem.this, (byte) 31);
        }

        @Override
        public void tick() {
            LivingEntity livingentity = SquallGolem.this.getTarget();
            if (livingentity == null) {
                return;
            }

            SquallGolem.this.getLookControl().setLookAt(livingentity, SquallGolem.this.getMaxHeadYRot(), SquallGolem.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                SquallGolem.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, SquallGolem.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity enemy, double distToEnemySqr) {
            if (SquallGolem.this.targetClose(enemy, distToEnemySqr)) {
                SquallGolem.this.doHurtTarget(enemy);
            }
        }

    }

    class MeleeGoal extends Goal {
        private float yRot;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return SquallGolem.this.getTarget() != null
                    && SquallGolem.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return SquallGolem.this.attackTick < MathHelper.secondsToTicks(1.7083F);
        }

        @Override
        public void start() {
            SquallGolem.this.setMeleeAttacking(true);
            SquallGolem.this.level.broadcastEntityEvent(SquallGolem.this, (byte) 17);
            if (SquallGolem.this.getTarget() != null){
                MobUtil.instaLook(SquallGolem.this, SquallGolem.this.getTarget());
            }
            this.yRot = SquallGolem.this.yBodyRot;
        }

        @Override
        public void stop() {
            SquallGolem.this.setMeleeAttacking(false);
            SquallGolem.this.level.broadcastEntityEvent(SquallGolem.this, (byte) 18);
        }

        @Override
        public void tick() {
            SquallGolem.this.setYRot(this.yRot);
            SquallGolem.this.yBodyRot = this.yRot;
            SquallGolem.this.getNavigation().stop();
            if (SquallGolem.this.attackTick == 1) {
                SquallGolem.this.playSound(ModSounds.SQUALL_GOLEM_ATTACK.get(), 5.0F, 1.0F);
                SquallGolem.this.level.broadcastEntityEvent(SquallGolem.this, (byte) 6);
            }
            if (SquallGolem.this.attackTick == 15) {
                AABB aabb = makeAttackRange(SquallGolem.this.getX() + SquallGolem.this.getHorizontalLookAngle().x * 2,
                        SquallGolem.this.getY(),
                        SquallGolem.this.getZ() + SquallGolem.this.getHorizontalLookAngle().z * 2, 5, 3, 5);
                for (LivingEntity target : SquallGolem.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != SquallGolem.this && !target.isAlliedTo(SquallGolem.this) && !SquallGolem.this.isAlliedTo(target)) {
                        this.hurtTarget(target);
                    }
                }
                if (SquallGolem.this.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = new BlockPos(SquallGolem.this.getX() + SquallGolem.this.getHorizontalLookAngle().x * 2, SquallGolem.this.getY() - 1.0F, SquallGolem.this.getZ() + SquallGolem.this.getHorizontalLookAngle().z * 2);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, SquallGolem.this.getX() + SquallGolem.this.getHorizontalLookAngle().x * 2, SquallGolem.this.getY() + 0.25D, SquallGolem.this.getZ() + SquallGolem.this.getHorizontalLookAngle().z * 2, 3.0F);
                    }
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)SquallGolem.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)SquallGolem.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            boolean flag = target.hurt(DamageSource.mobAttack(SquallGolem.this), f);
            if (flag) {
                if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                    if (livingEntity.getBoundingBox().getSize() > SquallGolem.this.getBoundingBox().getSize()){
                        livingEntity.knockback(f1 * 0.5F, Mth.sin(SquallGolem.this.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(SquallGolem.this.getYRot() * ((float)Math.PI / 180F)));
                    } else {
                        MobUtil.forcefulKnockBack(livingEntity, f1 * 0.5F, Mth.sin(SquallGolem.this.getYRot() * ((float)Math.PI / 180F)), -Mth.cos(SquallGolem.this.getYRot() * ((float)Math.PI / 180F)), 0.5D);
                    }
                    SquallGolem.this.setDeltaMovement(SquallGolem.this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                SquallGolem.this.doEnchantDamageEffects(SquallGolem.this, target);
                SquallGolem.this.setLastHurtMob(target);
            }
        }

        /**
         * Based of @Crimson_Steve codes.
         */
        public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
