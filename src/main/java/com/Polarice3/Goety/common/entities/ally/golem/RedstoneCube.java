package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RedstoneCube extends AbstractGolemServant{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(RedstoneCube.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RedstoneCube.class, EntityDataSerializers.BYTE);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String WALK = "walk";
    public int attackTick;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public RedstoneCube(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 60, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RedstoneCubeHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RedstoneCubeDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.RedstoneCubeArmor.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.RedstoneCubeFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.RedstoneCubeHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RedstoneCubeArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.RedstoneCubeDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.RedstoneCubeFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.REDSTONE_CUBE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REDSTONE_CUBE_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.REDSTONE_CUBE_WALK.get(), 0.5F, 1.0F);
        if (this.level instanceof ServerLevel serverLevel){
            if (this.isOnGround()) {
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(16711680), 0.25F, 3), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (entityIn instanceof Mob mob){
            if (mob.getTarget() != this){
                mob.setTarget(this);
            }
        }

        return super.doHurtTarget(entityIn);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            if (pLevel instanceof ServerLevel serverLevel){
                for (int i = 0; i < 4; ++i) {
                    double d0 = (double) this.getX() + this.random.nextDouble();
                    double d1 = (double) this.getY();
                    double d2 = (double) this.getZ() + this.random.nextDouble();
                    serverLevel.sendParticles(ParticleTypes.LAVA, d0, d1, d2, 0, 0.0D, 0.5D, 0.0D, 0.5F);
                }
                this.playSound(ModSounds.REDSTONE_CUBE_BURST.get(), 0.65F, 0.7F + Math.min(this.random.nextFloat(), 0.5F));
            }
        }
        return pSpawnData;
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
        } else if (Objects.equals(animation, "walk")){
            return 2;
        } else if (Objects.equals(animation, "attack")){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
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
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
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

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attack) {
        this.setFlag(1, attack);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (!this.level.isClientSide) {
                if (this.isMeleeAttacking()) {
                    this.getNavigation().stop();
                    ++this.attackTick;
                    if (this.attackTick >= MathHelper.secondsToTicks(1)){
                        this.setMeleeAttacking(false);
                        this.setAnimationState(IDLE);
                    } else if (this.getCurrentAnimation() != this.getAnimationState(ATTACK)){
                        this.setAnimationState(ATTACK);
                    }
                } else {
                    if (this.isMoving()) {
                        this.setAnimationState(WALK);
                    } else {
                        this.setAnimationState(IDLE);
                    }
                }
                if (this.getTarget() != null){
                    if (this.getTarget().isAlive()){
                        if (this.hasLineOfSight(this.getTarget())) {
                            if (this.targetClose(this.getTarget(), this.distanceToSqr(this.getTarget()))) {
                                if (!this.isMeleeAttacking()) {
                                    this.setMeleeAttacking(true);
                                }
                                if (this.attackTick == 8) {
                                    this.doHurtTarget(this.getTarget());
                                    this.playSound(ModSounds.REDSTONE_CUBE_ATTACK.get());
                                }
                            } else {
                                this.getNavigation().moveTo(this.getTarget(), 1.25F);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + enemy.getBbWidth());
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }
}
