package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.IRM;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.ScatterBomb;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.entities.util.SummonCircleVariant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class RedstoneMonstrosity extends AbstractGolemServant implements PlayerRideable, IAutoRideable, IRM {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(RedstoneMonstrosity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RedstoneMonstrosity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(RedstoneMonstrosity.class, EntityDataSerializers.BOOLEAN);
    public static String ACTIVATE = "activate";
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String WALK = "walk";
    public static String SUMMON = "summon";
    public static String TO_SIT = "to_sit";
    public static String TO_STAND = "to_stand";
    public static String SIT = "sit";
    public static String BELCH = "belch";
    public static String DEATH = "death";
    public static float SUMMON_SECONDS_TIME = 4.7F;
    public static double MELEE_RANGE = 8.0D;
    private int activateTick;
    public int attackTick;
    public int summonTick;
    private int summonCool;
    private int summonCount;
    public int belchCool;
    private int postAttackCool;
    public int isSittingDown;
    public int isStandingUp;
    public float minorGlow;
    public float glowAmount = 0.01F;
    public float bigGlow;
    public float deathRotation = 0.0F;
    public int bigGlowCool;
    public int deathTime = 0;
    public boolean clientStop;
    public AnimationState activateAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState toSitAnimationState = new AnimationState();
    public AnimationState toStandAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState belchAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public RedstoneMonstrosity(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.moveControl = new SlowRotMoveControl(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonGoal(this));
        this.goalSelector.addGoal(2, new MeleeGoal(this));
        this.goalSelector.addGoal(3, new BelchGoal(this));
        this.goalSelector.addGoal(5, new AttackGoal(this, 1.2D));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RedstoneMonstrosityHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.RedstoneMonstrosityArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 6.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RedstoneMonstrosityDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.RedstoneMonstrosityFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.RedstoneMonstrosityHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RedstoneMonstrosityArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.RedstoneMonstrosityDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.RedstoneMonstrosityFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(AUTO_MODE, false);
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger() instanceof Summoned;
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
            this.playSound(SoundEvents.ARROW_HIT_PLAYER);
            if (!this.isWandering()) {
                this.setWandering(true);
                this.setStaying(false);
            }
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        return null;
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.dimensions.height;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    public boolean isControlledByLocalInstance() {
        LivingEntity livingentity = this.getControllingPassenger();
        boolean flag = livingentity instanceof Player || this.isEffectiveAi();
        return flag && (!this.isAutonomous() || this.getControllingPassenger() instanceof Mob);
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle()
                    && rider instanceof Player
                    && !this.clientStopMoving()
                    && !this.isAutonomous()) {
                this.setYRot(rider.getYRot() * 0.5F);
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = rider.xxa * 0.23F;
                float f1 = rider.zza * 0.23F;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(this, false);
            } else {
                if (this.clientStopMoving()){
                    pTravelVector = new Vec3(0, pTravelVector.y, 0);
                }
                super.travel(pTravelVector);
            }
        }
    }

    public boolean clientStopMoving(){
        if (this.level.isClientSide) {
            return this.clientStop;
        } else {
            return this.isMeleeAttacking() || this.isBelching() || this.isSummoning();
        }
    }

    protected float nextStep() {
        return this.moveDist + 1.5F;
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new RMBodyRotateControl(this);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.REDSTONE_MONSTROSITY_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.REDSTONE_MONSTROSITY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REDSTONE_MONSTROSITY_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        if (this.isAggressive()){
            this.playSound(ModSounds.REDSTONE_MONSTROSITY_CHASE.get(), 2.0F, 1.0F);
        } else {
            this.playSound(ModSounds.REDSTONE_MONSTROSITY_STEP.get(), 2.0F, 1.0F);
        }
        CameraShake.cameraShake(this.level, this.position(), 20.0F, 0.03F, 0, 20);
    }

    @Override
    protected float getSoundVolume() {
        return 3.0F;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
        super.recreateFromPacket(p_219420_);
        if (p_219420_.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }

    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "activate")){
            return 1;
        } else if (Objects.equals(animation, "idle")){
            return 2;
        } else if (Objects.equals(animation, "attack")){
            return 3;
        } else if (Objects.equals(animation, "walk")){
            return 4;
        } else if (Objects.equals(animation, "summon")){
            return 5;
        } else if (Objects.equals(animation, "to_sit")){
            return 6;
        } else if (Objects.equals(animation, "to_stand")){
            return 7;
        } else if (Objects.equals(animation, "sit")){
            return 8;
        } else if (Objects.equals(animation, "belch")){
            return 9;
        } else if (Objects.equals(animation, "death")){
            return 10;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.activateAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.toSitAnimationState);
        animationStates.add(this.toStandAnimationState);
        animationStates.add(this.sitAnimationState);
        animationStates.add(this.belchAnimationState);
        animationStates.add(this.deathAnimationState);
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
                        this.activateAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.activateAnimationState);
                        break;
                    case 2:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 4:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 5:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 6:
                        this.stopMostAnimation(this.toSitAnimationState);
                        this.toSitAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 7:
                        this.stopMostAnimation(this.toStandAnimationState);
                        this.toStandAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 8:
                        this.sitAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.sitAnimationState);
                        break;
                    case 9:
                        this.belchAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.belchAnimationState);
                        break;
                    case 10:
                        this.deathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
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

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActivateTick", this.activateTick);
        pCompound.putInt("SummonTick", this.summonTick);
        pCompound.putInt("PostAttackCool", this.postAttackCool);
        pCompound.putInt("CoolDown", this.summonCool);
        pCompound.putInt("BelchCool", this.belchCool);
        pCompound.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActivateTick")) {
            this.activateTick = pCompound.getInt("ActivateTick");
        }
        if (pCompound.contains("SummonTick")) {
            this.summonTick = pCompound.getInt("SummonTick");
        }
        if (pCompound.contains("PostAttackCool")) {
            this.postAttackCool = pCompound.getInt("PostAttackCool");
        }
        if (pCompound.contains("CoolDown")){
            this.summonCool = pCompound.getInt("CoolDown");
        }
        if (pCompound.contains("BelchCool")) {
            this.belchCool = pCompound.getInt("BelchCool");
        }
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED || pReason == MobSpawnType.COMMAND){
            this.setPose(Pose.EMERGING);
        }
        this.isStandingUp = 0;
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean canAnimateMove(){
        return super.canAnimateMove() && this.getCurrentAnimation() == this.getAnimationState(WALK);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSummoning() || this.isActivating() || this.postAttackCool > 0;
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.summonTick <= 0 && !this.isActivating() && super.hasLineOfSight(p_149755_);
    }

    public boolean isSummoning(){
        return this.summonTick > 0;
    }

    private void glow() {
        this.minorGlow = Mth.clamp(this.minorGlow + this.glowAmount, 0, 1);
        if (this.minorGlow == 0 || this.minorGlow == 1) {
            this.glowAmount *= -1;
        }
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlag(1, attacking);
        this.attackTick = 0;
    }

    public void setBelching(boolean belching){
        this.setFlag(2, belching);
    }

    public boolean isBelching(){
        return this.getFlag(2);
    }

    public void setStaying(boolean staying){
        super.setStaying(staying);
        if (staying){
            this.isSittingDown = MathHelper.secondsToTicks(1);
        } else if (this.isFollowing()) {
            this.isStandingUp = MathHelper.secondsToTicks(1);
        }
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    //Based on @L_Ender's hurt range mechanic.
    public double canHurtRange(DamageSource pSource) {
        if (pSource.getEntity() != null){
            return this.distanceTo(pSource.getEntity());
        } else if (pSource.getSourcePosition() != null){
            return pSource.getSourcePosition().distanceTo(this.position());
        }

        return -1.0D;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.canHurtRange(pSource) > AttributesConfig.RedstoneMonstrosityHurtRange.get()
                && !pSource.isBypassInvul()) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    protected void actuallyHurt(DamageSource source, float amount) {
        if (!source.isBypassInvul()){
            amount = Math.min(amount, AttributesConfig.RedstoneMonstrosityDamageCap.get().floatValue());
        }
        super.actuallyHurt(source, amount);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(5)) {
            this.spawnAnim();
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
    }

    @Override
    public void playSound(SoundEvent p_216991_) {
        if (!this.isSilent()) {
            this.playSound(p_216991_, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public float getBigGlow() {
        return this.bigGlow;
    }

    @Override
    public float getMinorGlow() {
        return this.minorGlow;
    }

    public boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    public void tick() {
        super.tick();
        if (this.isHostile()) {
            if (this.tickCount % 10 == 0) {
                Mob mob = this.convertTo(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), false);
                if (mob != null) {
                    mob.setXRot(this.getXRot());
                    mob.setYRot(this.getYRot());
                    mob.setYBodyRot(this.getYRot());
                    mob.setYHeadRot(this.getYHeadRot());
                }
            }
        }
        if (this.isDeadOrDying()){
            this.setAnimationState(DEATH);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)){
            ++this.activateTick;
            this.isStandingUp = 0;
            this.setAnimationState(ACTIVATE);
            if (this.activateTick == 1){
                this.playSound(ModSounds.REDSTONE_MONSTROSITY_AWAKEN.get(), 10.0F, 1.0F);
            }
            if (this.activateTick == 40){
                CameraShake.cameraShake(this.level, this.position(), 40.0F, 0.5F, 0, 20);
            }
            if (this.activateTick > MathHelper.secondsToTicks(3.25F)){
                this.setPose(Pose.STANDING);
            }
        }
        if (!this.level.isClientSide){
            if (this.isAlive() && !this.isActivating()) {
                if (MobsConfig.RedstoneMonstrosityLeafBreak.get()) {
                    if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                        boolean flag = false;
                        AABB aabb = this.getBoundingBox().inflate(0.2D);

                        for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            Block block = blockstate.getBlock();
                            if (block instanceof LeavesBlock || blockstate.is(ModTags.Blocks.MONSTROSITY_BREAKS)) {
                                flag = this.level.destroyBlock(blockpos, true, this) || flag;
                            }
                        }
                    }
                }
                if (!this.isMeleeAttacking() && !this.isBelching() && !this.isSummoning()) {
                    this.level.broadcastEntityEvent(this, (byte) 7);
                    if (this.isStaying()) {
                        if (this.isSittingDown > 0){
                            --this.isSittingDown;
                            this.setAnimationState(TO_SIT);
                        } else {
                            this.setAnimationState(SIT);
                        }
                    } else if (this.isMoving()) {
                        this.setAnimationState(WALK);
                    } else {
                        if (this.isStandingUp > 0){
                            --this.isStandingUp;
                            this.setAnimationState(TO_STAND);
                        } else {
                            this.setAnimationState(IDLE);
                        }
                    }
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                    if (this.isStaying()){
                        this.isSittingDown = MathHelper.secondsToTicks(1);
                    } else {
                        this.isSittingDown = 0;
                    }
                    this.isStandingUp = 0;
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                }
                if (this.isSummoning()) {
                    this.makeBigGlow();
                    --this.summonTick;
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 5; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.ELECTRIC.get(), this.getRandomX(0.5D), this.getEyeY() - serverLevel.random.nextInt(2), this.getRandomZ(0.5D), 0, d0, d1, d2, 0.5F);
                        }
                        if (this.summonTick < MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1)
                                && this.summonTick >= MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 2)) {
                            if (!this.level.getBlockState(this.blockPosition().below()).isAir()) {
                                for (int j = 0; j < 4; ++j) {
                                    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                                    double d2 = this.blockPosition().getY() + 0.5F;
                                    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                                    serverLevel.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(), d1, d2, d3, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                                }
                            }
                        }
                        if (this.summonTick == MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1)){
                            ColorUtil colorUtil = new ColorUtil(0xff8200);
                            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 3, 1), this.getXLeft(), BlockFinder.moveDownToGround(this), this.getZLeft(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 3, 1), this.getXRight(), BlockFinder.moveDownToGround(this), this.getZRight(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            this.playSound(ModSounds.REDSTONE_MONSTROSITY_BELCH.get(), this.getSoundVolume(), 0.7F);
                            for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7.0D))) {
                                if (target != this && !target.isAlliedTo(this) && !this.isAlliedTo(target)) {
                                    if (target.hurt(DamageSource.mobAttack(this), 2.0F)){
                                        float f1 = 2.0F;
                                        if (target.getBoundingBox().getSize() > this.getBoundingBox().getSize()){
                                            target.knockback((double)(f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                                        } else {
                                            MobUtil.forcefulKnockBack(target, (double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))), 0.5D);
                                        }
                                    }
                                }
                            }
                        }
                        if (this.summonTick == MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 1.2F)) {
                            CameraShake.cameraShake(this.level, this.position(), 25.0F, 0.3F, 0, 20);
                        }
                        if (this.summonTick <= (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME - 2)) && this.summonCount != 0) {
                            for (int i = 0; i < 10; ++i){
                                BlockPos blockPos = this.blockPosition();
                                blockPos = blockPos.offset(-16 + this.level.random.nextInt(32), 0, -16 + this.level.random.nextInt(32));
                                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                                Summoned summoned = new RedstoneCube(ModEntityType.REDSTONE_CUBE.get(), this.level);
                                if (this.level.noCollision(summoned, summoned.getBoundingBox().move(vec3))){
                                    SummonCircleVariant summonCircle = new SummonCircleVariant(this.level, vec3, summoned, this);
                                    summonCircle.playSound(ModSounds.DIRT_DEBRIS.get(), 3.0F, 0.4F);
                                    this.level.addFreshEntity(summonCircle);
                                }
                            }
                            this.summonCount = 0;
                        }
                        if (this.summonTick <= 1){
                            if (this.summonCool <= 0) {
                                this.summonCool = MathHelper.secondsToTicks(13);
                            }
                            if (this.postAttackCool <= 0) {
                                this.postAttackCool = (int) MathHelper.secondsToTicks(0.5F);
                            }
                        }
                    }
                }
                if (this.summonCool > 0) {
                    --this.summonCool;
                }
                if (this.postAttackCool > 0) {
                    --this.postAttackCool;
                }
                if (this.belchCool > 0){
                    --this.belchCool;
                }
            }
        } else {
            if (this.isAlive()) {
                if (this.bigGlowCool > 0){
                    --this.bigGlowCool;
                }
                if (!this.isActivating()) {
                    if (this.bigGlow > 0.0F) {
                        if (this.bigGlowCool <= 0) {
                            this.bigGlow -= 0.1F;
                        }
                    } else {
                        this.glow();
                    }
                } else {
                    if (this.activateTick > 40){
                        this.bigGlow = 1.0F;
                    }
                }
            }
        }
    }

    public void makeBigGlow(){
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.bigGlow = 1.0F;
            this.bigGlowCool = 10;
        } else if (p_21375_ == 6){
            this.clientStop = true;
        } else if (p_21375_ == 7){
            this.clientStop = false;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 6.0F + enemy.getBbWidth()) + 1.0D;
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach
                || this.distanceTo(enemy) <= MELEE_RANGE
                || this.getBoundingBox().inflate(4.0D).intersects(enemy.getBoundingBox());
    }

    public boolean targetClose(LivingEntity entity){
        return this.targetClose(entity, this.distanceToSqr(entity));
    }

    public double getXLeft(){
        return this.getX() + (this.getHorizontalLookAngle().x * 2) + (MobUtil.getHorizontalLeftLookAngle(this).x * 2);
    }

    public double getZLeft(){
        return this.getZ() + (this.getHorizontalLookAngle().z * 2) + (MobUtil.getHorizontalLeftLookAngle(this).z * 2);
    }

    public double getXRight(){
        return this.getX() + (this.getHorizontalLookAngle().x * 2) + (MobUtil.getHorizontalRightLookAngle(this).x * 2);
    }

    public double getZRight(){
        return this.getZ() + (this.getHorizontalLookAngle().z * 2) + (MobUtil.getHorizontalRightLookAngle(this).z * 2);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level.isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
        }
        return true;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((itemstack.is(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                        || itemstack.is(ModBlocks.REINFORCED_REDSTONE_BLOCK.get().asItem()))
                        && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.is(ModBlocks.REINFORCED_REDSTONE_BLOCK.get().asItem())){
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    } else {
                        this.heal((this.getMaxHealth() / 4.0F) / 8.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 0.25F, 0.75F);
                    }
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    //Disabled this because players will look like they're floating midair during attacks when riding it.
                    /*if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }*/
                }
            }
        }
        return InteractionResult.PASS;
    }

    static class AttackGoal extends MeleeAttackGoal {
        public RedstoneMonstrosity mob;
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(RedstoneMonstrosity mob, double moveSpeed) {
            super(mob, moveSpeed, true);
            this.mob = mob;
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && !this.mob.isSummoning()
                    && !this.mob.isBelching()
                    && !this.mob.isMeleeAttacking()
                    && this.mob.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.mob.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
            if (this.mob.getTarget() == null) {
                this.mob.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return;
            }

            this.mob.getLookControl().setLookAt(livingentity, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                this.mob.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (this.mob.targetClose(enemy, distToEnemySqr)) {
                this.mob.doHurtTarget(enemy);
            }
        }

    }

    static class MeleeGoal extends Goal {
        public RedstoneMonstrosity mob;

        public MeleeGoal(RedstoneMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && !this.mob.isSummoning()
                    && !this.mob.isBelching()
                    && this.mob.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.attackTick < MathHelper.secondsToTicks(3.2917F)
                    && !this.mob.isSummoning()
                    && !this.mob.isBelching();
        }

        @Override
        public void start() {
            this.mob.setMeleeAttacking(true);
            if (this.mob.getTarget() != null){
                MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
        }

        @Override
        public void stop() {
            this.mob.setMeleeAttacking(false);
            this.mob.setAnimationState(IDLE);
        }

        @Override
        public void tick() {
            if (this.mob.attackTick < 30){
                if (this.mob.getTarget() != null){
                    MobUtil.rotateTo(this.mob, this.mob.getTarget(), 5.0F);
                }
            }
            if (this.mob.attackTick > 0 && this.mob.getCurrentAnimation() != this.mob.getAnimationState(ATTACK)) {
                this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_GROWL.get());
                this.mob.setAnimationState(ATTACK);
            }
            if (this.mob.attackTick == 34) {
                this.mob.makeBigGlow();
                this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_SMASH.get(), this.mob.getSoundVolume() * 2.0F, 0.2F);
                this.mob.playSound(SoundEvents.GENERIC_EXPLODE, this.mob.getSoundVolume() / 2.0F, 0.9F);
                Vec3 vec3 = this.mob.getHorizontalLookAngle();
                for (LivingEntity target : this.mob.level.getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().move(vec3.scale(5.0D)).inflate(MELEE_RANGE))) {
                    if (target != this.mob && !target.isAlliedTo(this.mob) && !this.mob.isAlliedTo(target)) {
                        this.hurtTarget(target);
                    }
                }
                CameraShake.cameraShake(this.mob.level, this.mob.position(), 25.0F, 0.3F, 0, 20);
                if (this.mob.level instanceof ServerLevel serverLevel){
                    ColorUtil colorUtil = new ColorUtil(0xff8200);
                    Vec3 vec31 = this.mob.position().add(vec3.scale(5.0D));
                    ServerParticleUtil.windShockwaveParticle(serverLevel, colorUtil, 2, 0, -1, vec31.add(0.0D, 1.0D, 0.0D));
                    ServerParticleUtil.windShockwaveParticle(serverLevel, colorUtil, 4, 0, -1, vec31.add(0.0D, 1.0D, 0.0D));
                    /*serverLevel.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 20, 0, true), vec31.x, this.mob.getY() + 0.25D, vec31.z, 0, 0.0D, 0.0D, 0.0D, 0);
                    serverLevel.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 10, 0, true), vec31.x, this.mob.getY() + 0.25D, vec31.z, 0, 0.0D, 0.0D, 0.0D, 0);*/
                }
            }
        }

        public void hurtTarget(Entity target) {
            float f = (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = (float)this.mob.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (target instanceof LivingEntity livingEntity){
                f += (livingEntity.getMaxHealth() * AttributesConfig.RedstoneMonstrosityHPPercentDamage.get().floatValue());
            }

            DamageSource damageSource = this.mob.getTrueOwner() != null ? ModDamageSource.summonAttack(this.mob, this.mob.getTrueOwner()) : DamageSource.mobAttack(this.mob);
            boolean flag = target.hurt(damageSource, f);
            if (flag) {
                if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                    if (livingEntity.getBoundingBox().getSize() > this.mob.getBoundingBox().getSize()){
                        livingEntity.knockback((double)(f1 * 0.5F), (double) Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F))));
                    } else {
                        MobUtil.forcefulKnockBack(livingEntity, (double)(f1 * 0.5F), (double)Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F))), 0.5D);
                    }
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                this.mob.doEnchantDamageEffects(this.mob, target);
                this.mob.setLastHurtMob(target);
            }
            if (target instanceof Player player && player.isBlocking()) {
                player.disableShield(true);
            } else if (target instanceof AbstractHauntedArmor hauntedArmor && hauntedArmor.isBlocking()){
                hauntedArmor.disableShield(true);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class BelchGoal extends Goal{
        private final RedstoneMonstrosity mob;
        private int attackTime = 0;
        private int seeTime;

        public BelchGoal(RedstoneMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (this.mob.belchCool <= 0
                    && livingentity != null
                    && livingentity.isAlive()
                    && !this.mob.targetClose(livingentity)
                    && this.mob.distanceTo(livingentity) <= 13.0F) {
                return !this.mob.isMeleeAttacking() && !this.mob.isSummoning();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (!this.mob.isMeleeAttacking() && !this.mob.isSummoning() && this.attackTime > 0 && this.mob.belchCool <= 0);
        }

        public void stop() {
            this.mob.setBelching(false);
            this.seeTime = 0;
            this.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (!this.mob.isMeleeAttacking() && !this.mob.isSummoning()) {
                if (target != null){
                    double distance = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                    boolean flag = this.mob.getSensing().hasLineOfSight(target);
                    if (flag) {
                        ++this.seeTime;
                    } else {
                        this.seeTime = 0;
                    }

                    if (distance <= Mth.square(32) && this.seeTime >= 5) {
                        this.mob.getNavigation().stop();
                    } else {
                        this.mob.getNavigation().moveTo(target, 1.0D);
                    }

                    MobUtil.instaLook(this.mob, target);
                }
                ++this.attackTime;
                if (this.attackTime == 1) {
                    this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_ARM.get(), this.mob.getSoundVolume() * 0.7F, this.mob.getVoicePitch());
                    this.mob.setBelching(true);
                    this.mob.setAnimationState(BELCH);
                } else if (this.attackTime > 5 && this.attackTime < 17){
                    this.mob.makeBigGlow();
                } else if (this.attackTime == 17) {
                    Vec3 vec3 = this.mob.position().add(this.mob.getLookAngle().scale(10));
                    for (int i = 0; i < 8; i++) {
                        ScatterBomb bomb = this.getScatterBomb(vec3);
                        this.mob.level.addFreshEntity(bomb);
                    }
                    this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_BELCH.get(), this.mob.getSoundVolume(), 0.7F);
                    this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_GROWL.get(), this.mob.getSoundVolume() - 1.5F, 0.9F);
                } else if (this.attackTime >= 37) {
                    this.mob.setBelching(false);
                    this.mob.postAttackCool = (int) MathHelper.secondsToTicks(0.5F);
                    this.attackTime = 0;
                    this.mob.belchCool = MathHelper.secondsToTicks(8);
                }
            }
        }

        @NotNull
        private ScatterBomb getScatterBomb(Vec3 vec3) {
            ScatterBomb bomb = new ScatterBomb(this.mob, this.mob.level);
            double d1 = vec3.x - this.mob.getX();
            double d2 = vec3.y - bomb.getY();
            double d3 = vec3.z - this.mob.getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3);
            bomb.setXRot(bomb.getXRot() - -20.0F);
            bomb.moveTo(this.mob.getX(), this.mob.getY() + 4.5D, this.mob.getZ());
            float velocity = 1.0F;
            if (this.mob.getTarget() != null){
                LivingEntity livingEntity = this.mob.getTarget();
                if (livingEntity.distanceTo(this.mob) <= 13.0F){
                    velocity = Math.max(0.25F, livingEntity.distanceTo(this.mob) / 13.0F);
                }
            }
            this.shoot(bomb, d1, d2 + d4, d3, velocity, 30);
            return bomb;
        }

        public void shoot(Projectile projectile, double xPower, double yPower, double zPower, float velocity, float inaccuracy) {
            Vec3 vec3 = (new Vec3(xPower, yPower, zPower)).normalize().add(
                            this.mob.random.triangle(0.0D, 0.0172275D * (double)inaccuracy),
                            this.mob.random.triangle(0.0D, 0.0172275D * 5),
                            this.mob.random.triangle(0.0D, 0.0172275D * (double)inaccuracy))
                    .scale((double)velocity);
            projectile.setDeltaMovement(vec3);
            double d0 = vec3.horizontalDistance();
            projectile.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            projectile.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            projectile.yRotO = projectile.getYRot();
            projectile.xRotO = projectile.getXRot();
        }
    }

    public static class SummonGoal extends Goal{
        public RedstoneMonstrosity mob;

        public SummonGoal(RedstoneMonstrosity mob){
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            int i = this.mob.level.getEntitiesOfClass(RedstoneCube.class, this.mob.getBoundingBox().inflate(32)).size();
            if (livingentity != null && livingentity.isAlive()) {
                return this.mob.summonCool <= 0
                        && i < 3
                        && !this.mob.isMeleeAttacking()
                        && !this.mob.isBelching()
                        && this.mob.isOnGround()
                        && this.mob.distanceTo(livingentity) < 16;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null){
                MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            this.mob.setAnimationState(SUMMON);
            this.mob.summonTick = (int) (MathHelper.secondsToTicks(SUMMON_SECONDS_TIME));
            this.mob.summonCount = 1;
        }
    }

    /**
     * Based on @lgh877's codes:<a href="https://github.com/lgh877/CrimsonStevesMoreMobs/blob/57b2dda1f29392d516fcdd1deaec3a2e77b76369/src/main/java/net/mcreator/crimson_steves_mobs/SmartBodyHelper2.java">...</a>
     */
    public static class RMBodyRotateControl extends BodyRotationControl {
        private final Mob mob;
        private static final float MAX_ROTATE = 75;
        private int headStableTime;
        private float lastStableYHeadRot;

        public RMBodyRotateControl(Mob mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void clientTick() {
            if (this.isMoving()) {
                this.mob.yBodyRot = this.mob.getYRot();
                this.rotateHeadIfNecessary();
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.headStableTime = 0;
            } else {
                if (this.notCarryingMobPassengers()) {
                    if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                        this.headStableTime = 0;
                        this.lastStableYHeadRot = this.mob.yHeadRot;
                        this.rotateBodyIfNecessary();
                    } else {
                        float limit = MAX_ROTATE;
                        ++this.headStableTime;
                        int speed = 10;
                        if (this.headStableTime > speed) {
                            limit = Math.max(1 - (this.headStableTime - speed) / speed, 0) * MAX_ROTATE;
                        }

                        this.mob.yBodyRot = approach(this.mob.yHeadRot, this.mob.yBodyRot, limit);
                    }
                }
            }
        }

        private void rotateBodyIfNecessary() {
            this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, (float)this.mob.getMaxHeadYRot());
        }

        private void rotateHeadIfNecessary() {
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
        }

        private boolean notCarryingMobPassengers() {
            return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof Mob);
        }

        private boolean isMoving() {
            double d0 = this.mob.getX() - this.mob.xo;
            double d1 = this.mob.getZ() - this.mob.zo;
            return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
        }

        public static float approach(float target, float current, float limit) {
            float delta = Mth.wrapDegrees(current - target);
            if (delta < -limit) {
                delta = -limit;
            } else if (delta >= limit) {
                delta = limit;
            }
            return target + delta * 0.55F;
        }
    }

    /**
     * Based on @lgh877's codes:<a href="https://github.com/lgh877/CrimsonStevesMoreMobs/blob/master/src/main/java/net/mcreator/crimson_steves_mobs/SlowRotMoveControl.java">...</a>
     */
    public static class SlowRotMoveControl extends MoveControl {
        public SlowRotMoveControl(Mob mob) {
            super(mob);
        }

        private float trueSpeed;
        private float additionalRot;

        public void tick() {
            if (this.operation == Operation.STRAFE) {
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float) this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, f1);
                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }
                f4 = this.trueSpeed / f4;
                f2 *= f4;
                f3 *= f4;
                float f5 = Mth.sin(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f6 = Mth.cos(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                if (!this.isWalkable(f7, f8)) {
                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;
                }
                this.mob.setSpeed(trueSpeed);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = Operation.WAIT;
            } else if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                if (d3 < (double) 2.5000003E-7F) {
                    this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
                    this.mob.setZza(trueSpeed);
                    if (this.additionalRot > 0) {
                        this.additionalRot -= 0.1F;
                    }
                    return;
                } else {
                    this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
                float f9 = (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                if (this.additionalRot < 1) {
                    this.additionalRot += 0.1F;
                }
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 5 + additionalRot * 5));
                this.mob.setSpeed(trueSpeed);
                BlockPos blockpos = this.mob.blockPosition();
                BlockState blockstate = this.mob.level.getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
                if (d2 > (double) this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getBbWidth())
                        || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double) blockpos.getY()
                        && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = Operation.JUMPING;
                }
            } else if (this.operation == Operation.JUMPING) {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                this.mob.setSpeed(this.trueSpeed);
                if (this.mob.isOnGround()) {
                    this.operation = Operation.WAIT;
                }
            } else {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
                this.mob.setZza(trueSpeed);
                if (this.additionalRot > 0) {
                    this.additionalRot -= 0.1F;
                }
            }
        }

        private boolean isWalkable(float p_24997_, float p_24998_) {
            PathNavigation pathnavigation = this.mob.getNavigation();
            if (pathnavigation != null) {
                NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
                return nodeevaluator == null || nodeevaluator.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double) p_24997_),
                        this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double) p_24998_)) == BlockPathTypes.WALKABLE;
            }
            return true;
        }
    }
}
