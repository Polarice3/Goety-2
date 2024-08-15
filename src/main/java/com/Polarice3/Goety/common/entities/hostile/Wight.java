package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.TeleportShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.CarrionMaggot;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.DelayedSummon;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Wight extends Summoned implements Enemy, NeutralMob {
    private static final EntityDataAccessor<Boolean> IS_CLONE = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HIDE = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.INT);
    public static String IDLE = "idle";
    public static String WALK = "walk";
    public static String ATTACK = "attack";
    public static String SMASH = "smash";
    public static String UNLEASH = "unleash";
    public static String SUMMON = "summon";
    private final ModServerBossInfo bossInfo;
    public int screamTick;
    public int attackTick;
    public int hidingTime = 0;
    public int hidingCooldown = 0;
    public int deathTime = 0;
    public int regenHeal = 0;
    public int spawnCool = 100;
    public int teleportCool = 0;
    public int lookTime = 0;
    public double prevX;
    public double prevY;
    public double prevZ;
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    public DamageSource deathBlow = DamageSource.GENERIC;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();
    public AnimationState unleashAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();

    public Wight(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.setHostile(true);
        this.waterNavigation = new WaterBoundPathNavigation(this, worldIn);
        this.groundNavigation = new WightNavigation(this, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL,0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StarePlayerGoal(this, 64.0F));
        this.goalSelector.addGoal(5, new AttackGoal(1.5D));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 0.8D, 10));
        this.targetSelector.addGoal(1, new WightLookForPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public void targetSelectGoal(){
    }

    public void addTargetGoal(){
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WightHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WightDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WightHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WightDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CLONE, false);
        this.entityData.define(DATA_HIDE, false);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag p_32520_) {
        super.addAdditionalSaveData(p_32520_);
        p_32520_.putInt("HidingTime", this.hidingTime);
        p_32520_.putInt("HidingCooldown", this.hidingCooldown);
        p_32520_.putInt("RegenHeal", this.regenHeal);
        p_32520_.putInt("SpawnCool", this.spawnCool);
        p_32520_.putInt("TeleportCool", this.teleportCool);
        p_32520_.putBoolean("Hiding", this.isHiding());
        p_32520_.putBoolean("Clone", this.isHallucination());
        this.addPersistentAngerSaveData(p_32520_);
    }

    public void readAdditionalSaveData(CompoundTag p_32511_) {
        super.readAdditionalSaveData(p_32511_);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.setHallucination(p_32511_.getBoolean("Clone"));
        this.setHide(p_32511_.getBoolean("Hiding"));
        this.hidingTime = p_32511_.getInt("HidingTime");
        this.hidingCooldown = p_32511_.getInt("HidingCooldown");
        this.regenHeal = p_32511_.getInt("RegenHeal");
        this.spawnCool = p_32511_.getInt("SpawnCool");
        this.teleportCool = p_32511_.getInt("TeleportCool");
        this.readPersistentAngerSaveData(this.level, p_32511_);
    }

    public boolean isEasterEgg(){
        return this.getCustomName() != null
                && (this.getCustomName().getString().contains("Walter")
                || this.getCustomName().getString().contains("Waltuh")
                || this.getCustomName().getString().contains("Heisenberg"));
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(MathHelper.minutesToTicks(10));
    }

    public void setRemainingPersistentAngerTime(int p_32515_) {
        this.remainingPersistentAngerTime = p_32515_;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_32509_) {
        this.persistentAngerTarget = p_32509_;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public Vec3 spawnLocation(Entity target) {
        Vec3 targetPos = target.position();
        double d0 = this.getRandom().nextInt(70) - 35.0D;
        double d1 = this.getRandom().nextInt(70) - 35.0D;
        double posX = targetPos.x() + d0;
        double posY = targetPos.y() + 10.0D;
        double posZ = targetPos.z() + d1;

        for(int i = 100; i >= 0; --posY) {
            BlockPos blockPos = new BlockPos(posX, posY, posZ);
            --i;
            if (this.level.noCollision(this, this.getBoundingBox().move(blockPos))
                    && this.level.getBlockState(blockPos.below()).isSolidRender(this.level, blockPos.below())) {
                break;
            }
        }

        return new Vec3(posX, posY, posZ);
    }

    @Override
    protected float getSoundVolume() {
        return 3.0F;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHiding()) {
            super.playAmbientSound();
        }
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.WIGHT_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.WIGHT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.WIGHT_DEATH.get();
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")){
            return 1;
        } else if (Objects.equals(animation, "attack")){
            return 2;
        } else if (Objects.equals(animation, "smash")){
            return 3;
        } else if (Objects.equals(animation, "unleash")){
            return 4;
        } else if (Objects.equals(animation, "summon")){
            return 5;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.smashAnimationState);
        animationStates.add(this.unleashAnimationState);
        animationStates.add(this.summonAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 3:
                        this.smashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                    case 4:
                        this.unleashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.unleashAnimationState);
                        break;
                    case 5:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                }
            }
        }

        if (DATA_HIDE.equals(p_219422_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    public void die(DamageSource cause) {
        if (this.isHallucination()){
            this.lifeSpanDamage();
        } else if (this.deathTime > 0) {
            super.die(cause);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; ++i) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        if (this.deathTime == 1){
            this.die(this.deathBlow);
        }
        if (this.deathTime >= 60) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get() && !this.isHallucination()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public int xpReward() {
        return 40;
    }

    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        if (this.isHiding()){
            return 0.1F;
        } else {
            return super.getStandingEyeHeight(p_32517_, p_32518_);
        }
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || this.isHiding() || this.isHallucination();
    }

    public boolean displayFireAnimation() {
        return !this.isHiding() && super.displayFireAnimation();
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        if (this.isHallucination()){
            return false;
        } else if (this.isHiding()){
            return true;
        } else {
            return super.isInvisibleTo(p_20178_);
        }
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isHiding();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return super.isInvulnerableTo(p_20122_) || this.isHiding();
    }

    private boolean getWightFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setWightFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getWightFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setWightFlags(1, attacking);
        this.attackTick = 0;
    }

    public boolean isScreaming() {
        return this.getWightFlag(2) || this.getWightFlag(8);
    }

    public void setScreaming(boolean scream) {
        this.setWightFlags(2, scream);
        this.screamTick = 0;
        if (!this.level.isClientSide) {
            if (scream) {
                this.setAnimationState(UNLEASH);
                this.applyDarkness();
            }
        }
    }

    public boolean isClimbing() {
        return this.getWightFlag(4);
    }

    public void setClimbing(boolean climbing) {
        this.setWightFlags(4, climbing);
    }

    public boolean isSummoning() {
        return this.getWightFlag(8);
    }

    public void setSummoning(boolean scream) {
        this.setWightFlags(8, scream);
        this.screamTick = 0;
        if (!this.level.isClientSide) {
            if (scream) {
                this.setAnimationState(SUMMON);
            }
        }
    }

    public boolean isHallucination() {
        return this.entityData.get(IS_CLONE);
    }

    public void setHallucination(boolean shadowClone) {
        this.entityData.set(IS_CLONE, shadowClone);
    }

    public void spawnHallucination(){
        this.setHallucination(true);
        this.level.broadcastEntityEvent(this, (byte) 6);
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDE);
    }

    public void setHide(boolean hide) {
        this.entityData.set(DATA_HIDE, hide);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return !this.isHallucination() ? super.getDefaultLootTable() : null;
    }

    public void applyDarkness(){
        if (this.level instanceof ServerLevel serverLevel) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DARKNESS, MathHelper.secondsToTicks(6), 0, false, false);
            MobEffectUtil.addEffectToPlayersAround(serverLevel, this, this.position(), (double)20, mobeffectinstance, 200);
        }
        if (!this.isSilent()){
            this.level.playSound((Player)null, this.prevX, this.prevY, this.prevZ, ModSounds.WIGHT_TELEPORT_SCREAM.get(), this.getSoundSource(), this.getSoundVolume() + 1.0F, this.getVoicePitch());
            this.playSound(ModSounds.WIGHT_TELEPORT_SCREAM.get(), this.getSoundVolume() + 1.0F, this.getVoicePitch());
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose p_21047_) {
        if (this.isHiding()){
            return EntityDimensions.scalable(0.1F, 0.1F);
        } else if (p_21047_ == Pose.CROUCHING){
            return super.getDimensions(p_21047_).scale(1.0F, 0.25F);
        } else {
            return super.getDimensions(p_21047_);
        }
    }

    public boolean hurt(DamageSource p_32494_, float p_32495_) {
        if (this.isInvulnerableTo(p_32494_)) {
            return false;
        } else {
            if (p_32494_ == DamageSource.IN_WALL || p_32494_ == DamageSource.DROWN){
                return false;
            }

            if (!this.isMeleeAttacking() && this.attackTick <= 0) {
                if (this.teleportCool <= 0) {
                    if (!(p_32494_ instanceof NoKnockBackDamageSource) && p_32495_ < this.getHealth()) {
                        if (!this.level.isClientSide() && ((p_32495_ < this.getHealth() && p_32494_.getEntity() instanceof LivingEntity) || (!(p_32494_.getEntity() instanceof LivingEntity) && !p_32494_.isFire() && !p_32494_.isMagic()))) {
                            if (this.teleport()) {
                                this.teleportCool = MathHelper.secondsToTicks(2);
                            }
                        }
                    }
                }
            }

            if (this.isHallucination() && p_32495_ > 0.0F){
                this.lifeSpanDamage();
            }

            if (this.isDeadOrDying()) {
                this.deathBlow = p_32494_;
            }

            return super.hurt(p_32494_, p_32495_);
        }
    }

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
    }

    protected boolean canRide(Entity p_219462_) {
        return false;
    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.bossInfo.setVisible(this.isAggressive() && !this.isHallucination());
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        BlockPos standPos0 = this.blockPosition().offset(0, 2, 0);
        BlockPos standPos1 = this.blockPosition().offset(0, 1, 0);

        boolean frontStand = this.level.getBlockState(standPos0.relative(this.getDirection())).getCollisionShape(this.level, standPos0.relative(this.getDirection())).isEmpty()
                && this.level.getBlockState(standPos1.relative(this.getDirection())).getCollisionShape(this.level, standPos1.relative(this.getDirection())).isEmpty();

        boolean shouldStand = this.level.getBlockState(standPos0).getCollisionShape(this.level, standPos0).isEmpty()
                && this.level.getBlockState(standPos1).getCollisionShape(this.level, standPos1).isEmpty();

        if ((frontStand && shouldStand && !this.isInWall()) || this.isClimbing()) {
            this.setPose(Pose.STANDING);
        } else {
            this.setPose(Pose.CROUCHING);
        }

        if (this.isClimbing()){
            this.animationSpeed += 0.8F;
        }
    }

    public void aiStep() {
        this.jumping = false;
        if (!this.level.isClientSide) {
            if (!this.isDeadOrDying()) {
                this.setAggressive(this.getTarget() != null);
                if (!this.isMeleeAttacking() && !this.isScreaming()) {
                    this.setAnimationState(IDLE);
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                }
                if (this.level instanceof ServerLevel serverLevel) {
                    Vec3 vec3 = this.getDeltaMovement();
                    if (this.isUnderWater()
                            && (this.getTarget() == null
                            || this.getTarget().getBlockY() > (this.getBlockY() + 2))) {
                        this.setDeltaMovement(vec3.x(), 0.25D, vec3.z());
                    } else if (this.isInWater()
                            && this.getTarget() != null
                            && (this.getTarget().getBlockY() + 2) < this.getBlockY()){
                        this.setDeltaMovement(vec3.x(), -0.25D, vec3.z());
                    }

                    this.climb(serverLevel);
                    this.updatePersistentAnger(serverLevel, true);
                    if (!this.isHiding()) {
                        this.hidingTime = 0;
                        if (this.regenHeal > 0){
                            if (this.tickCount % 10 == 0){
                                --this.regenHeal;
                                if (!this.isOnFire()) {
                                    this.heal(1.0F);
                                    Vec3 vector3d = this.getDeltaMovement();
                                    serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D, 0.5F);
                                }
                            }
                        }
                        for (int i = 0; i < 2; ++i) {
                            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                    } else {
                        ++this.hidingTime;
                        if (this.hidingTime >= MathHelper.secondsToTicks(5)) {
                            this.setHide(false);
                            this.level.broadcastEntityEvent(this, (byte) 5);
                            this.hidingCooldown = MathHelper.secondsToTicks(this.level.random.nextInt(5) + 5);
                            if (this.getTarget() != null) {
                                this.teleportNearTo(this.getTarget());
                                this.teleportHit(serverLevel);
                            } else {
                                this.teleport();
                                this.teleportHit(serverLevel);
                            }
                        }
                    }
                }
                if (this.isScreaming()){
                    if (this.getTarget() != null) {
                        MobUtil.instaLook(this, this.getTarget());
                    }
                    this.getNavigation().stop();
                    ++this.screamTick;
                    int total = this.isSummoning() ? 30 : 4;
                    if (this.screamTick >= total){
                        for (int i = 0; i < 64; ++i){
                            if (this.teleport()){
                                this.setScreaming(false);
                                this.setSummoning(false);
                                this.level.broadcastEntityEvent(this, (byte) 9);
                                break;
                            }
                        }
                    }
                }
                if (this.hidingCooldown > 0) {
                    --this.hidingCooldown;
                }
                if (this.spawnCool > 0 && !this.isHiding()){
                    --this.spawnCool;
                }
                if (this.teleportCool > 0){
                    --this.teleportCool;
                }
                if (this.isHallucination()){
                    if (this.getTrueOwner() != null){
                        if (this.getTrueOwner().isDeadOrDying()){
                            this.lifeSpanDamage();
                        }
                    }
                }
            }
        }

        if (this.isHiding() || this.isScreaming()){
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
            this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        }

        super.aiStep();
    }

    @Override
    public void lifeSpanDamage() {
        if (this.level instanceof ServerLevel serverLevel){
            for(int i = 0; i < this.level.random.nextInt(10) + 10; ++i) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.SMOKE, this);
            }
        }
        this.discard();
    }

    public int getClimbHeight(BlockPos pPos) {
        int y = 0;
        BlockPos blockPos = pPos;

        while (this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos)) {
            blockPos = blockPos.above();
            ++y;
            if (y > this.level.getMaxBuildHeight()) {
                break;
            }
        }

        if (y > 0) {
            y += 1;
        }

        return y;
    }

    public boolean isClimbable(BlockPos pPos, int pHeight) {
        for (int i = 1; i <= pHeight; i++) {
            BlockPos above = pPos.above(i);
            if (this.level.getBlockState(above).isSolidRender(this.level, above)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public BlockPos getClimbPos(BlockPos pPos) {
        for (int i = -1; i <= 1; ++i){
            for (int k = -1; k <= 1; ++k){
                BlockPos blockPos = pPos.offset(i, 0, k);
                if (this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos)) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    @Nullable
    public BlockPos getClimbablePos(BlockPos pPos, int pHeight) {
        for (int i = -1; i <= 1; ++i){
            for (int k = -1; k <= 1; ++k){
                BlockPos blockPos = pPos.offset(i, 0, k);
                if (this.isClimbable(blockPos, pHeight)) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    public static double horizontalDistance(Vec3 firstPos, Vec3 secondPos) {
        Vec2 vec2 = new Vec2((float) firstPos.x, (float) firstPos.z);
        Vec2 vec21 = new Vec2((float) secondPos.x, (float) secondPos.z);
        return Math.sqrt(vec21.distanceToSqr(vec2));
    }

    public static boolean haveBlocksAround(Level level, BlockPos pPos, int rangeX, int rangeY, int rangeZ) {
        for (int i = -rangeX; i <= rangeX; ++i){
            for (int j = -rangeY; j <= rangeY; ++j){
                for (int k = -rangeZ; k <= rangeZ; ++k){
                    BlockPos blockPos = pPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(blockPos);
                    if (!blockState.getCollisionShape(level, blockPos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Based on Climbing codes from Man From the Fog Reimagined: <a href="https://github.com/z3n01d/man-from-the-fog-reimagined/blob/master/src/main/java/com/zen/the_fog/common/entity/the_man/TheManEntity.java#L292">...</a>
     */
    public void climb(ServerLevel serverLevel) {
        if (this.getTarget() == null) {
            this.setClimbing(false);
            return;
        }

        boolean blocksAbove = haveBlocksAround(serverLevel, this.blockPosition().above(2), 1, 0, 1);
        this.setClimbing(blocksAbove && horizontalDistance(this.position(), this.getTarget().position()) <= 5.0D && this.getTarget().getBlockY() > this.getBlockY());

        if (this.isClimbing()) {
            this.setDeltaMovement(0.0D, 0.25D, 0.0D);
            BlockPos blockPos = this.getClimbPos(this.blockPosition());
            if (blockPos != null){
                this.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
                int height = this.getClimbHeight(blockPos);
                BlockPos blockPos1 = this.getClimbablePos(blockPos, height);
                if (blockPos1 != null){
                    Vec3 vec3 = Vec3.atCenterOf(blockPos1.above());
                    this.setPos(vec3.x(), this.getY(), vec3.z());
                }
            }
        }
    }

    protected boolean teleport() {
        if (!this.level.isClientSide() && !this.isHallucination() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 32.0D;
            double d1 = this.getY() + (double)(this.random.nextInt(32) - 16);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 32.0D;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    public boolean teleportNearTo(Entity p_32501_) {
        if (!this.level.isClientSide() && !this.isHallucination() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            Vec3 vec3 = new Vec3(this.getX() - p_32501_.getX(), this.getY(0.5D) - p_32501_.getEyeY(), this.getZ() - p_32501_.getZ());
            vec3 = vec3.normalize();
            double d0 = 16.0D;
            double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * d0;
            double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3.y * d0;
            double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * d0;
            return this.teleport(d1, d2, d3);
        } else {
            return false;
        }
    }

    private boolean teleport(double p_32544_, double p_32545_, double p_32546_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);

        while(blockpos$mutableblockpos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER) && !this.isSwimming();
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, p_32544_, p_32545_, p_32546_);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (this.isSwimming() || (this.getTarget() != null && this.getTarget().isInWater())){
                flag2 = MobUtil.randomWaterTeleport(this, event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            }
            if (flag2) {
                if (this.getTarget() != null && !this.isScreaming() && this.getTarget().distanceTo(this) >= 4.0D && this.spawnCool <= 0 && this.level.random.nextFloat() <= 0.25F){
                    this.setSummoning(true);
                    this.level.broadcastEntityEvent(this, (byte) 8);
                    this.playSound(ModSounds.WIGHT_SCREAM.get(), 3.0F, 1.0F);
                    int amount = 3 + this.level.random.nextInt(1 + this.level.getDifficulty().getId());
                    for (int j = 0; j < amount; ++j){
                        CarrionMaggot carrionMaggot = new CarrionMaggot(ModEntityType.CARRION_MAGGOT.get(), this.level);
                        BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), carrionMaggot, this.level);
                        carrionMaggot.setTrueOwner(this);
                        carrionMaggot.setLimitedLife(MobUtil.getSummonLifespan(this.level));
                        carrionMaggot.moveTo(blockPos, this.getYRot(), this.getXRot());
                        carrionMaggot.finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                        DelayedSummon delayedSummon = new DelayedSummon(this.level, blockPos, carrionMaggot, true, true, this);
                        delayedSummon.setLifeSpan(MathHelper.secondsToTicks(this.random.nextInt(1 + j)));
                        this.level.addFreshEntity(delayedSummon);
                    }
                    this.spawnCool = MathHelper.secondsToTicks(10);
                } else if ((this.level.random.nextFloat() <= 0.25F && this.hidingCooldown <= 0) || this.isScreaming()){
                    this.setHide(true);
                    this.level.broadcastEntityEvent(this, (byte) 4);
                    this.applyDarkness();
                } else if (this.level.random.nextFloat() <= 0.25F){
                    this.applyDarkness();
                    if (this.level.random.nextFloat() <= 0.15F && this.getTarget() != null){
                        int amount = 4 + this.level.random.nextInt(1 + this.level.getDifficulty().getId());
                        for (int j = 0; j < amount; ++j){
                            Wight falseWight = new Wight(ModEntityType.WIGHT.get(), this.level);
                            Vec3 vec31 = Vec3.atCenterOf(BlockFinder.SummonRadius(this.blockPosition(), falseWight, this.level));
                            falseWight.setTrueOwner(this);
                            falseWight.setLimitedLife(MathHelper.secondsToTicks(5 + this.level.random.nextInt(10)));
                            falseWight.setPos(vec31);
                            double d2 = this.getTarget().getX() - falseWight.getX();
                            double d1 = this.getTarget().getZ() - falseWight.getZ();
                            falseWight.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                            float f = (float) Mth.atan2(d1, d2);
                            float f2 = f + (float) j * (float) Math.PI * 0.25F + 4.0F;
                            vec31 = new Vec3(this.getTarget().getX() + (double) Mth.cos(f2) * 4.0D, this.getTarget().getY(), this.getTarget().getZ() + (double) Mth.sin(f2) * 4.0D);
                            falseWight.setPos(vec31);
                            falseWight.finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(new BlockPos(vec31)), MobSpawnType.MOB_SUMMONED, null, null);
                            falseWight.spawnHallucination();
                            falseWight.setHealth(this.getHealth());
                            this.level.addFreshEntity(falseWight);
                        }
                    }
                }
                if (this.level instanceof ServerLevel serverLevel){
                    for (int i = 0; i < 16; ++i) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                this.prevX + (double)this.getBbWidth() * (2.0D * this.random.nextDouble() - 1.0D) * 0.5D,
                                this.prevY + (double)this.getBbHeight() * this.random.nextDouble(),
                                this.prevZ + (double)this.getBbWidth() * (2.0D * this.random.nextDouble() - 1.0D) * 0.5D,
                                0, 0.0D, 0.0D, 0.0D, 0.5F);
                    }
                    serverLevel.sendParticles(new TeleportInShockwaveParticleOption(4, 1), this.prevX, this.prevY + 0.5F, this.prevZ, 0, 0, 0, 0, 0.5F);
                    if (!this.isHiding()){
                        this.teleportHit(serverLevel);
                    }
                }
                this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level.playSound((Player)null, this.prevX, this.prevY, this.prevZ, ModSounds.WRAITH_TELEPORT.get(), this.getSoundSource(), 1.0F, 0.5F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public void teleportHit(ServerLevel serverLevel){
        for (int i = 0; i < 16; ++i) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
        }
        serverLevel.sendParticles(new TeleportShockwaveParticleOption(), this.getX(), this.getY() + 0.5F, this.getZ(), 0, 0, 0, 0, 0.5F);
        if (!this.isSilent()) {
            this.playSound(ModSounds.WRAITH_TELEPORT.get(), 1.0F, 0.5F);
        }
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().inflate(1.0D).intersects(enemy.getBoundingBox());
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (this.isHallucination()){
            if (this.doHurtTarget(1.0F, entityIn)) {
                if (entityIn instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                }
                this.lifeSpanDamage();
                return true;
            }
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag){
            if (entityIn instanceof LivingEntity living){
                if (entityIn instanceof Player player){
                    if (SEHelper.getSoulsAmount(player, 10)){
                        SEHelper.decreaseSouls(player, 10);
                        this.regenHeal += 10;
                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                    }
                } else {
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                }
                if (this.isEasterEgg()){
                    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
                }
            }
        }
        return flag;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.setHide(true);
        } else if (p_21375_ == 5){
            this.setHide(false);
        } else if (p_21375_ == 6){
            this.setHallucination(true);
        } else if (p_21375_ == 7){
            this.setHallucination(false);
        } else if (p_21375_ == 8){
            this.setSummoning(true);
        } else if (p_21375_ == 9){
            this.setSummoning(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Nullable
    public static Wight findWight(Entity entity){
        return findWight(entity,
                wight -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)
                        && !wight.isAggressive());
    }

    @Nullable
    public static Wight findWight(Entity entity, Predicate<Wight> predicate){
        List<Wight> wightList = entity.level.getEntitiesOfClass(Wight.class,
                entity.getBoundingBox().inflate(64.0D),
                predicate.and(wight -> !wight.isHallucination()));
        Wight wight = null;
        for (Wight wight1 : wightList){
            if (wight1 != null){
                wight = wight1;
            }
        }

        return wight;
    }

    static class WightNavigation extends WallClimberNavigation {
        public WightNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected @NotNull PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new WightNodeEvaluator();
            this.nodeEvaluator.setCanFloat(true);
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class WightNodeEvaluator extends WalkNodeEvaluator {
        public void prepare(PathNavigationRegion region, Mob mob) {
            super.prepare(region, mob);
            this.entityWidth = Mth.floor(mob.getBbWidth() + 1.0F);
            this.entityHeight = Mth.floor(1.0F);
            this.entityDepth = Mth.floor(mob.getBbWidth() + 1.0F);
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(Wight.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return Wight.this.getTarget() != null
                    && Wight.this.getTarget().isAlive()
                    && !Wight.this.isScreaming()
                    && !Wight.this.isHiding();
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            Wight.this.setMeleeAttacking(false);
            Wight.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = Wight.this.getTarget();
            if (livingentity == null || Wight.this.isHiding() || Wight.this.isScreaming()) {
                return;
            }

            Wight.this.getLookControl().setLookAt(livingentity, Wight.this.getMaxHeadYRot(), Wight.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                Wight.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, Wight.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (!Wight.this.isHiding()) {
                boolean smash = Wight.this.random.nextBoolean();
                if (!Wight.this.isMeleeAttacking() && Wight.this.targetClose(enemy, distToEnemySqr)){
                    Wight.this.setMeleeAttacking(true);
                    Wight.this.playSound(ModSounds.WIGHT_PRE_SWING.get(), Wight.this.getSoundVolume(), Wight.this.getVoicePitch());
                    if (smash) {
                        Wight.this.setAnimationState(SMASH);
                    } else {
                        Wight.this.setAnimationState(ATTACK);
                    }
                }
                if (Wight.this.isMeleeAttacking()) {
                    if (Wight.this.attackTick < MathHelper.secondsToTicks(1.7F)) {
                        Wight.this.setYBodyRot(Wight.this.getYHeadRot());
                        if (Wight.this.getCurrentAnimation() == Wight.this.getAnimationState(SMASH)) {
                            if (Wight.this.attackTick == 20) {
                                Wight.this.playSound(ModSounds.WIGHT_SWING.get(), Wight.this.getSoundVolume(), Wight.this.getVoicePitch() - 0.25F);
                                if (Wight.this.targetClose(enemy, distToEnemySqr)) {
                                    Wight.this.doHurtTarget(enemy);
                                    if (enemy instanceof Player player){
                                        Wight.this.maybeDisableShield(player, new ItemStack(Items.STONE_AXE), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
                                    }
                                }
                            }
                        } else {
                            if (Wight.this.attackTick == 14) {
                                Wight.this.playSound(ModSounds.WIGHT_SWING.get(), Wight.this.getSoundVolume(), Wight.this.getVoicePitch() - 0.25F);
                                this.massiveSweep(Wight.this, 3.0D, 100.0D);
                            }
                        }
                    } else {
                        Wight.this.setMeleeAttacking(false);
                        Wight.this.teleport();
                    }
                }
            } else {
                Wight.this.setMeleeAttacking(false);
            }
        }

        public void massiveSweep(LivingEntity source, double range, double arc){
            List<LivingEntity> hits = MobUtil.getAttackableLivingEntitiesNearby(source, range, 1.0F, range, range);
            for (LivingEntity target : hits) {
                float targetAngle = (float) ((Math.atan2(target.getZ() - source.getZ(), target.getX() - source.getX()) * (180 / Math.PI) - 90) % 360);
                float attackAngle = source.yBodyRot % 360;
                if (targetAngle < 0) {
                    targetAngle += 360;
                }
                if (attackAngle < 0) {
                    attackAngle += 360;
                }
                float relativeAngle = targetAngle - attackAngle;
                float hitDistance = (float) Math.sqrt((target.getZ() - source.getZ()) * (target.getZ() - source.getZ()) + (target.getX() - source.getX()) * (target.getX() - source.getX())) - target.getBbWidth() / 2f;
                if (hitDistance <= range && (relativeAngle <= arc / 2 && relativeAngle >= -arc / 2) || (relativeAngle >= 360 - arc / 2 || relativeAngle <= -360 + arc / 2)) {
                    Wight.this.doHurtTarget(target);
                }
            }
        }

    }

    static class StarePlayerGoal extends Goal{
        protected final Mob mob;
        @Nullable
        protected Player lookAt;
        protected final float lookDistance;

        public StarePlayerGoal(Mob p_148118_, float p_148120_) {
            this.mob = p_148118_;
            this.lookDistance = p_148118_.getAttribute(Attributes.FOLLOW_RANGE) != null ? (float) p_148118_.getAttributeValue(Attributes.FOLLOW_RANGE) : p_148120_;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.getTarget() != null){
                return false;
            }

            for (Player player : this.mob.level.players()) {
                if (this.lookAt == null) {
                    if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)
                            && player.distanceTo(this.mob) <= this.lookDistance) {
                        this.lookAt = player;
                    }
                }
            }

            return this.lookAt != null;
        }

        public boolean canContinueToUse() {
            if (this.lookAt == null){
                return false;
            } else if (!this.lookAt.isAlive()) {
                return false;
            } else if (this.mob.distanceTo(this.lookAt) > this.lookDistance) {
                return false;
            } else {
                return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.lookAt) && this.mob.getTarget() == null;
            }
        }

        public void stop() {
            this.lookAt = null;
        }

        public void tick() {
            if (this.lookAt != null && this.lookAt.isAlive()) {
                if (this.mob.level.players().size() > 1) {
                    for (Player player : this.mob.level.players().stream().filter(player -> player != this.lookAt).toList()) {
                        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)
                                && player.distanceTo(this.mob) <= this.lookDistance
                                && SEHelper.getSoulAmountInt(player) > SEHelper.getSoulAmountInt(this.lookAt)) {
                            this.lookAt = player;
                        }
                    }
                }
                this.mob.getNavigation().stop();
                MobUtil.instaLook(this.mob, this.lookAt);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class WightLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final Wight wight;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

        public WightLookForPlayerGoal(Wight wight, @Nullable Predicate<LivingEntity> predicate) {
            super(wight, Player.class, 10, false, false, predicate);
            this.wight = wight;
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((livingEntity) -> {
                return MobUtil.isDirectlyLooking(livingEntity, this.wight) || wight.closerThan(livingEntity, 4.0D) || wight.getTarget() == livingEntity;
            });
        }

        public boolean canUse() {
            this.pendingTarget = this.wight.level.getNearestPlayer(this.startAggroTargetConditions, this.wight);
            return this.pendingTarget != null && !this.wight.isHallucination();
        }

        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.wight.setScreaming(true);
            if (this.pendingTarget != null) {
                this.pendingTarget.playSound(ModSounds.WIGHT_SCREAM.get(), 3.0F, 0.5F);
                if (!this.wight.level.isClientSide){
                    if (this.pendingTarget instanceof ServerPlayer serverPlayer){
                        ModNetwork.sendToClient(serverPlayer, new SPlayPlayerSoundPacket(ModSounds.WIGHT_SCREAM.get(), 3.0F, 0.5F));
                    }
                }
                MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DARKNESS, MathHelper.secondsToTicks(6), 0, false, false);
                this.pendingTarget.addEffect(mobeffectinstance);
            }
            this.wight.playSound(ModSounds.WIGHT_SCREAM.get(), 3.0F, 0.5F);
            this.teleportTime = 0;
        }

        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                this.wight.lookAt(this.pendingTarget, 10.0F, 10.0F);
                return true;
            } else {
                return this.target != null && this.continueAggroTargetConditions.test(this.wight, this.target) || super.canContinueToUse();
            }
        }

        public void tick() {
            if (this.wight.getTarget() == null) {
                super.setTarget((LivingEntity)null);
            }

            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            } else {
                if (this.target != null && !this.wight.isPassenger() && !this.wight.isMeleeAttacking()) {
                    if (this.target.distanceToSqr(this.wight) > 256.0D && this.teleportTime++ >= this.adjustedTickDelay(MathHelper.secondsToTicks(3.5F)) && this.wight.teleportNearTo(this.target)) {
                        this.teleportTime = 0;
                    }
                }

                super.tick();
            }

        }
    }
}
