package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.client.particles.LichShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.NecroVFXPalette;
import com.Polarice3.Goety.client.particles.SoulSummonVFX;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.NecroticPool;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GraveCube extends AbstractGolemServant {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(GraveCube.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(GraveCube.class, EntityDataSerializers.BYTE);
    public static int POST_DEATH_COLOR = 0x2f4e17;
    public static ColorUtil SUMMON_COLOR = new ColorUtil(NecroVFXPalette.LIME_HEX);
    public static ColorUtil POST_DEATH = new ColorUtil(POST_DEATH_COLOR);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public DamageSource deathReason = this.damageSources().generic();
    public int attackTick;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public GraveCube(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GraveCubeHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GraveCubeDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.GraveCubeArmor.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GraveCubeFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GraveCubeHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GraveCubeArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GraveCubeDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.GraveCubeFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.GRAVE_CUBE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GRAVE_CUBE_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.GRAVE_CUBE_WALK.get(), 0.5F, 1.0F);
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

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED){
            if (pLevel instanceof ServerLevel serverLevel){
                SoulSummonVFX.emitter(serverLevel, this.getBoundingBox().getCenter(), SoulSummonVFX.Emitter.CUBE_SPAWN, 0.0F, true);
                serverLevel.sendParticles(new LichShockwaveParticleOption(SUMMON_COLOR, 8, 6, 2, 40), this.getX(), this.getY() + 0.5F, this.getZ(), 0, 0, 0, 0, 0.5F);
                this.playSound(ModSounds.GRAVE_CUBE_BURST.get(), 0.65F, 0.7F + Math.min(this.random.nextFloat(), 0.5F));
            }
        }
        return pSpawnData;
    }

    public void handleDamageEvent(DamageSource p_270229_) {
        this.invulnerableTime = 20;
        this.hurtDuration = 10;
        this.hurtTime = this.hurtDuration;
        SoundEvent soundevent = this.getHurtSound(p_270229_);
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }

        this.hurt(this.damageSources().generic(), 0.0F);
        this.lastDamageSource = p_270229_;
        this.lastDamageStamp = this.level().getGameTime();
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 1;
        } else if (Objects.equals(animation, ATTACK)){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
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

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
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
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance instance) {
        if (instance.getEffect() == GoetyEffects.SOUL_BURN.get()) {
            return false;
        }
        return super.canBeAffected(instance);
    }

    @Override
    public void die(DamageSource pCause) {
        this.deathReason = pCause;
        super.die(pCause);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20 && !this.level.isClientSide() && !this.isRemoved()) {
            if (this.deathReason != null && !this.deathReason.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                NecroticPool pool = new NecroticPool(ModEntityType.NECROTIC_POOL.get(), this.level);
                pool.setWarmupDelayTicks(0);
                if (this.deathReason == this.damageSources().generic()) {
                    pool.setRadius(pool.radius() * 1.5F);
                }
                pool.setOwner(this.getTrueOwner() != null ? this.getTrueOwner() : this);
                pool.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F);
                pool.setDuration(100);
                pool.setSoundEvent(ModSounds.SOUL_ORB_EXPLODE.get());
                pool.setWarmupColor(POST_DEATH_COLOR);
                pool.setColor(POST_DEATH_COLOR);
                pool.setPos(this.position().add(0, 0.25F, 0));
                this.level.addFreshEntity(pool);
            } else {
                this.level.broadcastEntityEvent(this, (byte)60);
            }
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            if (this.isAlive()) {
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.isCurrentAnimation(IDLE), this.tickCount);
            }
        }
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
                    this.setAnimationState(IDLE);
                }
                if (this.getTarget() != null){
                    if (this.getTarget().isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getTarget())){
                        if (this.hasLineOfSight(this.getTarget())) {
                            if (this.targetClose(this.getTarget(), this.distanceToSqr(this.getTarget()))) {
                                if (!this.isMeleeAttacking()) {
                                    this.setMeleeAttacking(true);
                                }
                                if (this.attackTick == 8) {
                                    this.doHurtTarget(this.getTarget());
                                    this.playSound(ModSounds.GRAVE_CUBE_ATTACK.get(), 0.6F, 1.0F);
                                }
                            } else {
                                this.getNavigation().moveTo(this.getTarget(), 1.25F);
                            }
                        }
                    }
                    if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getTarget())) {
                        this.setTarget(null);
                    }
                }
            }
        }
    }

    @Override
    public double getAttackReachSqr(LivingEntity enemy) {
        return this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + enemy.getBbWidth();
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

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((itemstack.is(Tags.Items.BONES) || itemstack.is(Items.BONE_BLOCK)) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.is(Items.BONE_BLOCK)){
                        this.heal(this.getMaxHealth());
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    } else {
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 0.25F, 1.0F);
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
                }
            }
        }
        return InteractionResult.PASS;
    }
}
