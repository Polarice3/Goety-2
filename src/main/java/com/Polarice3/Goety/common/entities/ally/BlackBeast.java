package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.items.HowlingSoul;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlackBeast extends Summoned{
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(BlackBeast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BlackBeast.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> PREY_ID = SynchedEntityData.defineId(BlackBeast.class, EntityDataSerializers.OPTIONAL_UUID);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String WALK = "walk";
    public static String ROAR = "roar";
    public static String TO_SIT = "to_sit";
    public static String TO_STAND = "to_stand";
    public static String SIT = "sit";
    public static String DEATH = "death";
    private float interestedAngle;
    private float interestedAngleO;
    private int invisibleCool;
    public int attackTick;
    public int summonTick;
    private int summonCool;
    private int happyCool;
    public int isSittingDown;
    public int isStandingUp;
    public int deathTime = 0;
    public float deathRotation = 0.0F;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState roarAnimationState = new AnimationState();
    public AnimationState toSitAnimationState = new AnimationState();
    public AnimationState toStandAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public BlackBeast(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SummonGoal());
        this.goalSelector.addGoal(1, new HuntBreakDoorGoal(this));
        this.goalSelector.addGoal(2, new ModMeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 140.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Mob.class, 140.0F));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(1, new SummonTargetGoal(this, false, true));
        this.targetSelector.addGoal(5, new NaturalAttackGoal<>(this, Animal.class, false, BlackWolf.PREY_SELECTOR));
        this.targetSelector.addGoal(6, new NaturalAttackGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NaturalAttackGoal<>(this, AbstractSkeleton.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlackBeastHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BlackBeastArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlackBeastDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BlackBeastHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BlackBeastArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BlackBeastDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_INTERESTED_ID, false);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(PREY_ID, Optional.empty());
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getPreyId() != null) {
            pCompound.putUUID("Prey", this.getPreyId());
        }
        pCompound.putInt("InvisibleCool", this.invisibleCool);
        pCompound.putInt("SummonTick", this.summonTick);
        pCompound.putInt("SummonCool", this.summonCool);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Prey")) {
            this.setPreyId(pCompound.getUUID("Prey"));
        }
        if (pCompound.contains("InvisibleCool")) {
            this.invisibleCool = pCompound.getInt("InvisibleCool");
        }
        if (pCompound.contains("SummonTick")) {
            this.summonTick = pCompound.getInt("SummonTick");
        }
        if (pCompound.contains("SummonCool")){
            this.summonCool = pCompound.getInt("SummonCool");
        }
    }

    public boolean isPretty(){
        return this.getCustomName() != null && (this.getCustomName().getString().contains("Princess") || this.getCustomName().getString().contains("Cupcake"));
    }

    protected PathNavigation createNavigation(Level level) {
        return new BlackBeastNavigation(this, level);
    }

    @Nullable
    public LivingEntity getPrey() {
        UUID uuid = this.getPreyId();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
    }

    @Nullable
    public UUID getPreyId() {
        return this.entityData.get(PREY_ID).orElse((UUID)null);
    }

    public void setPreyId(@Nullable UUID p_184754_1_) {
        this.entityData.set(PREY_ID, Optional.ofNullable(p_184754_1_));
    }

    public void setPrey(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setPreyId(livingEntity.getUUID());
        } else {
            this.setPreyId(null);
        }
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
        } else if (Objects.equals(animation, "walk")){
            return 3;
        } else if (Objects.equals(animation, "roar")){
            return 4;
        } else if (Objects.equals(animation, "to_sit")){
            return 5;
        } else if (Objects.equals(animation, "to_stand")){
            return 6;
        } else if (Objects.equals(animation, "sit")){
            return 7;
        } else if (Objects.equals(animation, "death")){
            return 8;
        } else {
            return 0;
        }
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public void stopAllAnimation(){
        for (AnimationState state : this.getAnimations()){
            state.stop();
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
                        this.stopAllAnimation();
                        this.attackAnimationState.start(this.tickCount);
                        break;
                    case 3:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 4:
                        this.roarAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.roarAnimationState);
                        break;
                    case 5:
                        this.stopMostAnimation(this.toSitAnimationState);
                        this.toSitAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 6:
                        this.stopMostAnimation(this.toStandAnimationState);
                        this.toStandAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 7:
                        this.sitAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.sitAnimationState);
                        break;
                    case 8:
                        this.deathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.roarAnimationState);
        animationStates.add(this.toSitAnimationState);
        animationStates.add(this.toStandAnimationState);
        animationStates.add(this.sitAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSummoning();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BLACK_BEAST_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.BLACK_BEAST_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BLACK_BEAST_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.BLACK_BEAST_STEP.get(), 0.15F, 1.0F);
    }

    public boolean isSummoning(){
        return this.summonTick > 0;
    }

    public boolean isMeleeAttacking() {
        return this.attackTick > 0;
    }

    public void setIsInterested(boolean p_30445_) {
        this.entityData.set(DATA_INTERESTED_ID, p_30445_);
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 40) {
            if (this.getTrueOwner() != null && MobsConfig.BlackBeastHowlingSoul.get()){
                ItemStack itemStack = new ItemStack(ModItems.HOWLING_SOUL.get());
                HowlingSoul.setOwnerName(this.getTrueOwner(), itemStack);
                HowlingSoul.setBlackBeast(this, itemStack);
                ItemEntity itemEntity = this.spawnAtLocation(itemStack);
                if (itemEntity != null){
                    itemEntity.setExtendedLifetime();
                }
            }
            this.remove(RemovalReason.KILLED);
            this.dropExperience();
        }
        if (this.level instanceof ServerLevel serverLevel){
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            serverLevel.sendParticles(ModParticleTypes.WRAITH.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
            serverLevel.sendParticles(ModParticleTypes.WRAITH_BURST.get(), this.getRandomX(1.0D), this.getY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
        }
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.setAnimationState(DEATH);
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_21047_) {
        if (p_21047_ == Pose.CROUCHING){
            return super.getDimensions(p_21047_).scale(1.0F, 0.5F);
        } else {
            return super.getDimensions(p_21047_);
        }
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public boolean canAnimateMove(){
        return !this.isImmobile();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return super.canBeAffected(pPotioneffect) && pPotioneffect.getEffect() != MobEffects.WITHER;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }

        if (source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) {
            return false;
        }

        if (source.getEntity() instanceof Mob){
            amount = (amount + 1.0F) / 2.0F;
        }

        if (this.hasEffect(MobEffects.INVISIBILITY) && amount >= 1.0F){
            this.removeEffect(MobEffects.INVISIBILITY);
            if (this.level instanceof ServerLevel serverLevel){
                for(int i = 0; i < 8; ++i) {
                    ColorUtil colorUtil = new ColorUtil(0x3e293c);
                    serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, colorUtil.red, colorUtil.green, colorUtil.blue, 0.5F);
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);

        if (!this.level.isClientSide) {
            if (flag) {
                this.attackTick = 10;
                this.setAnimationState(ATTACK);
                this.playSound(ModSounds.BLACK_BEAST_CLAW.get(), this.getSoundVolume(), this.getVoicePitch());
                if (entityIn instanceof LivingEntity target) {
                    if (!target.hasEffect(GoetyEffects.DOOM.get()) && !MobUtil.isInSunlightNoRain(this)) {
                        int debuffDuration = MathHelper.secondsToTicks(15);
                        this.playSound(ModSounds.BLACK_BEAST_ROAR.get(), this.getSoundVolume(), 0.25F);

                        target.addEffect(new MobEffectInstance(GoetyEffects.DOOM.get(), debuffDuration, 0, false, false), this);
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, debuffDuration, 1, false, false), this);
                        target.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), debuffDuration, 0, false, false), this);
                        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MathHelper.secondsToTicks(5), 2, false, false), this);
                    }
                }
            }
        }

        return flag;
    }

    protected void customServerAiStep() {
        if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
            boolean flag = this.getPrey() != null && this.getTarget() == this.getPrey();
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(flag);
        }

        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDeadOrDying()){
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        } else {
            this.interestedAngleO = this.interestedAngle;
            if (this.isInterested()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }
            if (this.happyCool > 0) {
                --this.happyCool;
            }
        }
        if (!this.level.isClientSide()) {
            if (!this.isDeadOrDying()) {
                if (!this.isMeleeAttacking() && !this.isSummoning()) {
                    if (this.isStaying()) {
                        this.isStandingUp = MathHelper.secondsToTicks(1);
                        if (this.isSittingDown > 0
                                && this.getCurrentAnimation() != this.getAnimationState(SIT)) {
                            --this.isSittingDown;
                            this.getNavigation().stop();
                            this.setAnimationState(TO_SIT);
                        } else {
                            if (this.isSittingDown > 0) {
                                this.isSittingDown = 0;
                            }
                            this.setAnimationState(SIT);
                        }
                    } else {
                        this.isSittingDown = MathHelper.secondsToTicks(1);
                        if (this.isStandingUp > 0 && this.level.getBlockState(this.blockPosition().above()).isAir()) {
                            --this.isStandingUp;
                            this.getNavigation().stop();
                            this.setAnimationState(TO_STAND);
                        } else {
                            if (this.isStandingUp > 0) {
                                this.isStandingUp = 0;
                            }
                            if (!this.level.getBlockState(this.blockPosition().above()).isAir()
                                    && this.isCrouching()
                                    && !this.isMoving()){
                                this.setAnimationState(SIT);
                            } else {
                                this.setAnimationState(IDLE);
                            }
                        }
                    }
                }
                if (MobsConfig.BlackBeastDayStrength.get()) {
                    int boostAmp = 1;
                    int resistAmp = 0;

                    if (this.level.dayTime() >= MathHelper.minecraftDayToTicks(100)) {
                        boostAmp = 2;
                        resistAmp = 2;
                    }

                    if (this.level.dayTime() >= MathHelper.minecraftDayToTicks(50) && !MobUtil.isInSunlightNoRain(this)) {
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, boostAmp, false, false));
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, resistAmp, false, false));
                    }
                }
                if (this.attackTick > 0) {
                    --this.attackTick;
                }
                if (this.summonCool > 0) {
                    --this.summonCool;
                }
                if (this.summonTick > 0) {
                    this.getNavigation().stop();
                    --this.summonTick;
                }
                if (this.summonTick == MathHelper.secondsToTicks(1.17F)) {
                    CameraShake.cameraShake(this.level, this.position(), 10.0F, 0.1F, 0, 20);
                    this.roar();
                }

                if (this.summonTick == MathHelper.secondsToTicks(1)){
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i1 = 0; i1 < 3; ++i1) {
                            Summoned summonedentity = new BlackWolf(ModEntityType.BLACK_WOLF.get(), serverLevel);
                            BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), summonedentity, serverLevel);
                            summonedentity.setTrueOwner(this);
                            summonedentity.moveTo(blockPos, this.getYRot(), this.getXRot());
                            MobUtil.moveDownToGround(summonedentity);
                            summonedentity.setPersistenceRequired();
                            summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            if (this.getTrueOwner() != null) {
                                summonedentity.setUpgraded(CuriosFinder.hasWildRobe(this.getTrueOwner()));
                            }
                            summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                            summonedentity.setHealth(summonedentity.getMaxHealth());
                            this.setTarget(this.getTarget());
                            serverLevel.addFreshEntity(summonedentity);
                        }
                    }
                }
                if (this.getPrey() != null){
                    if (this.getTarget() == null){
                        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getPrey())
                                && MobUtil.sameDimension(this, this.getPrey())
                                && this.level.isLoaded(this.getPrey().blockPosition())) {
                            this.setTarget(this.getPrey());
                        }
                    } else if (this.getTarget() == this.getPrey()){
                        if (this.getTarget().distanceTo(this) > 64.0D
                                && MobUtil.sameDimension(this, this.getPrey())){
                            this.teleportTowards(this.getPrey());
                        }
                    }
                    if (this.getTrueOwner() != null) {
                        if (this.getPrey().isDeadOrDying()) {
                            if (this.getTrueOwner().distanceTo(this) > 32.0D
                                    && MobUtil.sameDimension(this, this.getTrueOwner())) {
                                this.teleportTowards(this.getTrueOwner());
                            }
                            if (this.getPrey() != null){
                                this.setPrey(null);
                            }
                        }
                    }
                }
            }
        }

        boolean shouldCrouch = !this.level.getBlockState(this.blockPosition().above().relative(this.getDirection())).isAir() && this.level.getBlockState(this.blockPosition().relative(this.getDirection())).isAir();

        if (!shouldCrouch && !this.isInWall()) {
            this.setPose(Pose.STANDING);
        } else {
            this.setPose(Pose.CROUCHING);
        }
    }

    public void teleportHits(){
        if (this.level instanceof ServerLevel serverLevel) {
            int i = 8;

            for (int j = 0; j < i; ++j) {
                double d0 = (double) j / 7.0D;
                ColorUtil colorUtil = new ColorUtil(0x3e293c);
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double) this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), d1, d2, d3, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
            }
        }
    }

    @Override
    public void mobSense() {
        if (MobsConfig.MobSense.get()) {
            if (this.isAlive()) {
                if (this.getTarget() != null) {
                    if (!this.isInvisible()) {
                        if (this.invisibleCool > 0) {
                            if (this.getTarget() instanceof Mob mob) {
                                if (this.getTarget() instanceof Animal animal){
                                    animal.setLastHurtByMob(this);
                                } else if (mob.getTarget() == null || mob.getTarget().isDeadOrDying()){
                                    mob.setTarget(this);
                                }
                            }
                        } else {
                            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MathHelper.secondsToTicks(30), 0, false, false));
                            this.invisibleCool = MathHelper.secondsToTicks(30);
                            if (this.level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ColorUtil colorUtil = new ColorUtil(0x3e293c);
                                    serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, colorUtil.red, colorUtil.green, colorUtil.blue, 0.5F);
                                }
                            }
                        }
                    }
                    if (this.getTarget() instanceof Mob mob) {
                        if (mob.getType().getDescriptionId().contains("nightmare_stalker")) {
                            mob.setTarget(this);
                        }
                    }
                }
            }
        }
    }

    private void roar() {
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), target -> target.isAlive() && (target instanceof BlackBeast))) {
                if (!MobUtil.areAllies(this, livingentity)) {
                    this.strongKnockback(livingentity);
                }
            }

            Vec3 vec3 = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;
                if (this.level instanceof ServerLevel serverLevel){
                    serverLevel.sendParticles(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, 0, d0, d1, d2, 0.5F);
                } else {
                    this.level.addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
                }
            }

            this.gameEvent(GameEvent.ENTITY_ROAR);
        }

    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        MobUtil.push(p_33340_, d0 / d2 * 6.0D, 0.4D, d1 / d2 * 6.0D);
    }

    public float getHeadRollAngle(float p_30449_) {
        return Mth.lerp(p_30449_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
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
            } else if (pPlayer.getMainHandItem().getItem() instanceof TaglockKit
                    && TaglockKit.hasEntity(pPlayer.getMainHandItem())
                    && !MobUtil.areAllies(pPlayer, TaglockKit.getEntity(pPlayer.getMainHandItem()))){
                this.setPrey(TaglockKit.getEntity(pPlayer.getMainHandItem()));
                this.setTarget(this.getPrey());
                if (!pPlayer.getAbilities().instabuild) {
                    TaglockKit.removeEntity(pPlayer.getMainHandItem());
                }
                if (!this.level.isClientSide) {
                    if (this.getPrey() instanceof ServerPlayer player) {
                        if (CuriosFinder.hasCurio(player, ModItems.ALARMING_CHARM.get())) {
                            player.displayClientMessage(Component.translatable("info.goety.summon.hunt").withStyle(ChatFormatting.RED), true);
                            ModNetwork.sendToClient(player, new SPlayPlayerSoundPacket(SoundEvents.WOLF_HOWL, 3.0F, 0.5F));
                        }
                    }
                }
                if (MobUtil.sameDimension(this, this.getPrey())) {
                    this.teleportTowards(this.getPrey());
                }
                if (this.isStaying()){
                    this.setStaying(false);
                }
                return InteractionResult.SUCCESS;
            } else if (pPlayer.getMainHandItem().isEmpty() && this.happyCool <= 0){
                this.happyCool = 40;
                this.level.broadcastEntityEvent(this, (byte) 102);
                this.playSound(SoundEvents.WOLF_AMBIENT, 1.0F, 0.5F);
                this.heal(1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 102){
            this.happyCool = 40;
            this.playSound(SoundEvents.WOLF_AMBIENT, 1.0F, 0.5F);
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    protected void addParticlesAroundSelf(ParticleOptions pParticleData) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(pParticleData, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    static class BlackBeastNavigation extends GroundPathNavigation {
        public BlackBeastNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected @NotNull PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new BlackBeastNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class BlackBeastNodeEvaluator extends WalkNodeEvaluator {
        public void prepare(PathNavigationRegion region, Mob mob) {
            super.prepare(region, mob);
            this.entityWidth = Mth.floor(mob.getBbWidth() + 1.0F);
            this.entityHeight = Mth.floor(1.0F);
            this.entityDepth = Mth.floor(mob.getBbWidth() + 1.0F);
        }
    }

    class SummonGoal extends Goal{
        public SummonGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            List<BlackWolf> list = BlackBeast.this.level.getEntitiesOfClass(BlackWolf.class, BlackBeast.this.getBoundingBox().inflate(32.0D), (blackWolf -> blackWolf.getTrueOwner() == BlackBeast.this));
            if (BlackBeast.this.getTarget() != null
                    && BlackBeast.this.hasLineOfSight(BlackBeast.this.getTarget())
                    && BlackBeast.this.summonCool <= 0
                    && !BlackBeast.this.isInvisible()
                    && !BlackBeast.this.isCrouching()
                    && list.size() < 3) {
                return BlackBeast.this.random.nextInt(100) == 0;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            BlackBeast.this.summonTick = MathHelper.secondsToTicks(1.5F);
            BlackBeast.this.summonCool = MathHelper.secondsToTicks(10);
            BlackBeast.this.setAnimationState(ROAR);
            BlackBeast.this.playSound(ModSounds.BLACK_BEAST_ROAR.get(), BlackBeast.this.getSoundVolume() + 2.0F, BlackBeast.this.getVoicePitch());
        }
    }

    public static class BegGoal extends Goal {
        private final BlackBeast wolf;
        private final float lookDistance;
        private int lookTime;

        public BegGoal(BlackBeast p_25063_, float p_25064_) {
            this.wolf = p_25063_;
            this.lookDistance = p_25064_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.wolf.getTarget() == null
                    && !this.wolf.isMeleeAttacking()
                    && !this.wolf.isSummoning()
                    && this.wolf.getTrueOwner() != null
                    && this.playerHoldingInteresting(this.wolf.getTrueOwner());
        }

        public boolean canContinueToUse() {
            if (this.wolf.getTarget() != null || this.wolf.isMeleeAttacking() || this.wolf.isSummoning()){
                return false;
            } else if (this.wolf.getTrueOwner() == null || !this.wolf.getTrueOwner().isAlive()) {
                return false;
            } else if (this.wolf.distanceToSqr(this.wolf.getTrueOwner()) > (double)(this.lookDistance * this.lookDistance)) {
                return false;
            } else {
                return this.lookTime > 0 && this.playerHoldingInteresting(this.wolf.getTrueOwner());
            }
        }

        public void start() {
            this.wolf.setIsInterested(true);
            this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
        }

        public void stop() {
            this.wolf.setIsInterested(false);
        }

        public void tick() {
            this.wolf.getLookControl().setLookAt(this.wolf.getTrueOwner().getX(), this.wolf.getTrueOwner().getEyeY(), this.wolf.getTrueOwner().getZ(), 10.0F, (float)this.wolf.getMaxHeadXRot());
            --this.lookTime;
        }

        private boolean playerHoldingInteresting(LivingEntity p_25067_) {
            for(InteractionHand interactionhand : InteractionHand.values()) {
                ItemStack itemstack = p_25067_.getItemInHand(interactionhand);
                if (this.wolf.isFood(itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    static class HuntBreakDoorGoal extends BreakDoorGoal {
        public HuntBreakDoorGoal(Mob p_34112_) {
            super(p_34112_, 6, difficulty -> difficulty != Difficulty.PEACEFUL);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            BlackBeast blackBeast = (BlackBeast)this.mob;
            return blackBeast.getPrey() != null
                    && blackBeast.getTarget() == blackBeast.getPrey()
                    && blackBeast.random.nextInt(reducedTickDelay(10)) == 0
                    && super.canUse();
        }

        public boolean canContinueToUse() {
            BlackBeast blackBeast = (BlackBeast)this.mob;
            return blackBeast.getPrey() != null
                    && blackBeast.getTarget() == blackBeast.getPrey()
                    && super.canContinueToUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }
}
