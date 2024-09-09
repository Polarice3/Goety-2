package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.ShieldDebris;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Wildfire extends Summoned {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Wildfire.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHIELDS = SynchedEntityData.defineId(Wildfire.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SHIELD_HEALTH = SynchedEntityData.defineId(Wildfire.class, EntityDataSerializers.FLOAT);
    public static String IDLE = "idle";
    public static String SHOCKWAVE = "shockwave";
    public static String SHOOT = "shoot";
    public int regenShieldTick;
    protected int summonCooldown;
    protected int shockwaveCooldown;
    protected int fireHealCool;
    protected int fleeingCool;
    protected float extraFireballDamage = 0.0F;
    public AnimationState spinAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState shockwaveAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();

    public Wildfire(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonBlazes());
        this.goalSelector.addGoal(2, new ShockwaveAttackGoal());
        this.goalSelector.addGoal(3, new ShootGoal(this));
        this.goalSelector.addGoal(5, new SeekFireGoal());
        this.goalSelector.addGoal(6, new AvoidTargetGoal<>(this, LivingEntity.class, 10.0F, 1.0F, 1.3F){
            @Override
            public boolean canUse() {
                return super.canUse() && !(Wildfire.this.level.getBlockState(Wildfire.this.blockPosition()).getBlock() instanceof BaseFireBlock) && Wildfire.this.getFleeingCool() <= 0;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !(Wildfire.this.level.getBlockState(Wildfire.this.blockPosition()).getBlock() instanceof BaseFireBlock);
            }

            @Override
            public void stop() {
                super.stop();
                Wildfire.this.setFleeingCool(MathHelper.secondsToTicks(5));
            }
        });
        this.goalSelector.addGoal(7, new WanderGoal<>(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new IdleGoal());
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public void followGoal(){
        this.goalSelector.addGoal(4, new FollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WildfireHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WildfireMeleeDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.WildfireArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WildfireHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WildfireMeleeDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WildfireArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(SHIELDS, 4);
        this.entityData.define(SHIELD_HEALTH, AttributesConfig.WildfireShieldHealth.get().floatValue());
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide) {
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        this.stopAllAnimation();
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.shockwaveAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.shockwaveAnimationState);
                        break;
                    case 3:
                        this.shootAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.shootAnimationState);
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Shields", this.getShields());
        pCompound.putInt("RegenShieldTick", this.getRegenShieldTick());
        pCompound.putInt("SummonCool", this.getSummonCooldown());
        pCompound.putInt("ShockwaveCool", this.getShockwaveCooldown());
        pCompound.putInt("FireHealCool", this.getFireHealCool());
        pCompound.putInt("FleeingCool", this.getFleeingCool());
        pCompound.putFloat("ShieldHealth", this.getShieldHealth());
        pCompound.putFloat("FireballDamage", this.getFireBallDamage());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Shields")) {
            this.setShields(pCompound.getInt("Shields"));
        }
        if (pCompound.contains("RegenShieldTick")) {
            this.setRegenShieldTick(pCompound.getInt("RegenShieldTick"));
        }
        if (pCompound.contains("SummonCool")) {
            this.setSummonCooldown(pCompound.getInt("SummonCool"));
        }
        if (pCompound.contains("ShockwaveCool")) {
            this.setShockwaveCooldown(pCompound.getInt("ShockwaveCool"));
        }
        if (pCompound.contains("FireHealCool")) {
            this.setFireHealCool(pCompound.getInt("FireHealCool"));
        }
        if (pCompound.contains("FleeingCool")) {
            this.setFleeingCool(pCompound.getInt("FleeingCool"));
        }
        if (pCompound.contains("ShieldHealth")) {
            this.setShieldHealth(pCompound.getFloat("ShieldHealth"));
        }
        if (pCompound.contains("FireballDamage")) {
            this.setFireBallDamage(pCompound.getFloat("FireballDamage"));
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public int xpReward(){
        return 20;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WILDFIRE_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.WILDFIRE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WILDFIRE_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.WILDFIRE_STEP.get(), 0.3F, 1.0F);
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
        } else if (Objects.equals(animation, "shockwave")){
            return 2;
        } else if (Objects.equals(animation, "shoot")){
            return 3;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.shockwaveAnimationState);
        list.add(this.shootAnimationState);
        return list;
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception) {
                state.stop();
            }
        }
    }

    public void stopAllAnimation(){
        for (AnimationState state : this.getAllAnimations()){
            state.stop();
        }
    }

    public int getShields() {
        return Mth.clamp(this.entityData.get(SHIELDS), 0, 4);
    }

    public void setShields(int shields) {
        this.entityData.set(SHIELDS, shields);
    }

    public float getShieldHealth() {
        return this.entityData.get(SHIELD_HEALTH);
    }

    public void setShieldHealth(float shieldHealth) {
        this.entityData.set(SHIELD_HEALTH, Mth.clamp(shieldHealth, 0.0F, AttributesConfig.WildfireShieldHealth.get().floatValue()));
    }

    public void healShield(float amount){
        if (this.getShields() > 0) {
            this.setShieldHealth(this.getShieldHealth() + amount);
        }
    }

    public void breakShield(){
        if (this.getShields() > 0){
            this.setShields(this.getShields() - 1);
            this.setShieldHealth(AttributesConfig.WildfireShieldHealth.get().floatValue());
            this.invulnerableTime = 20;
            this.setRegenShieldTick(MathHelper.secondsToTicks(7));
            this.playSound(ModSounds.WILDFIRE_SHIELD_BREAK.get(), 1.2F, 1.0F);
            this.playSound(ModSounds.WILDFIRE_SHIELD_BREAK_VOCAL.get(), 1.2F, 1.0F);
        }
        if (this.level instanceof ServerLevel serverLevel){
            ParticleOptions particleOptions = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BLAZE_ROD));
            for(int i = 0; i < 10; ++i) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, particleOptions, this);
            }
        }
    }

    public void addShield(){
        if (this.getShields() < 4){
            this.setShields(this.getShields() + 1);
            this.setShieldHealth(AttributesConfig.WildfireShieldHealth.get().floatValue());
            this.setRegenShieldTick(MathHelper.secondsToTicks(7));
            this.playSound(ModSounds.WILDFIRE_SHIELD_REGEN.get(), 2.0F, 1.0F);
        }
    }

    public void setRegenShieldTick(int regenShieldTick){
        this.regenShieldTick = regenShieldTick;
    }

    public int getRegenShieldTick() {
        return this.regenShieldTick;
    }

    public void setSummonCooldown(int summonCooldown){
        this.summonCooldown = summonCooldown;
    }

    public int getSummonCooldown() {
        return this.summonCooldown;
    }

    public void setShockwaveCooldown(int shockwaveCooldown){
        this.shockwaveCooldown = shockwaveCooldown;
    }

    public int getShockwaveCooldown() {
        return this.shockwaveCooldown;
    }

    public void setFireHealCool(int fireHealCool){
        this.fireHealCool = fireHealCool;
    }

    public int getFireHealCool() {
        return this.fireHealCool;
    }

    public void setFleeingCool(int fleeingCool){
        this.fleeingCool = fleeingCool;
    }

    public int getFleeingCool() {
        return this.fleeingCool;
    }

    public float getFireBallDamage(){
        return this.extraFireballDamage;
    }

    public void setFireBallDamage(float damage){
        this.extraFireballDamage = damage;
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    public boolean causeFallDamage(float p_149683_, float p_149684_, DamageSource p_149685_) {
        return false;
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    public boolean canAnimateMove(){
        return this.getCurrentAnimation() != this.getAnimationState(SHOCKWAVE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getShields() > 0
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !this.isInvulnerableTo(source)) {
            if (source.getEntity() != null && source.getEntity() instanceof LivingEntity livingEntity) {
                this.setLastHurtByMob(livingEntity);
                if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                    double d0 = livingEntity.getX() - this.getX();

                    double d1;
                    for(d1 = livingEntity.getZ() - this.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.knockback((double)0.4F, d0, d1);
                }
            }

            amount = this.getDamageAfterArmorAbsorb(source, amount);
            amount = this.getDamageAfterMagicAbsorb(source, amount);
            this.setShieldHealth(this.getShieldHealth() - amount);

            if (this.getShieldHealth() <= 0) {
                this.breakShield();
            } else {
                this.playHurtSound(source);
            }
            return false;
        } else {
            if (source.is(DamageTypeTags.IS_FIRE) && this.getFireHealCool() <= 0){
                this.heal(amount);
                this.healShield(amount);
                this.setFireHealCool(10);
                return false;
            }
            return super.hurt(source, amount);
        }
    }

    public void aiStep() {
        super.aiStep();

        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            this.spinAnimationState.startIfStopped(this.tickCount);
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                if (this.level.random.nextFloat() <= 0.25F){
                    this.level.addParticle(ModParticleTypes.BIG_FIRE.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            LivingEntity follow = null;
            if (this.getTarget() != null){
                follow = this.getTarget();
            } else if (this.getTrueOwner() != null && this.isFollowing()){
                follow = this.getTrueOwner();
            }
            //Based this movement codes on Dungeon Mobs' Wildfire codes: https://github.com/Infamous-Misadventures/Dungeons-Mobs/blob/1.19/src/main/java/com/infamous/dungeons_mobs/entities/blaze/WildfireEntity.java
            if (follow != null
                    && ((!this.onGround() && steepDropBelow())
                    || follow.getY() > (this.getY() + 3)
                    || this.getY() < follow.getY()
                    || this.distanceTo(follow) >= 16.0D)) {
                if (this.getY() < follow.getY() + 5) {
                    this.setDeltaMovement(0.0D, 0.04D, 0.0D);
                } else {
                    this.setDeltaMovement(0.0D, -0.01D, 0.0D);
                }

                if (!this.isStaying()) {
                    double x = follow.getX() - this.getX();
                    double y = follow.getY() - this.getY();
                    double z = follow.getZ() - this.getZ();
                    double d = Math.sqrt(x * x + y * y + z * z);
                    this.setDeltaMovement(this.getDeltaMovement()
                            .add(x / d * 0.2D,
                                    y / d * 0.2D,
                                    z / d * 0.2D)
                            .scale(0.4D));
                }
                this.move(MoverType.SELF, this.getDeltaMovement());

                this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(follow.getX(), follow.getEyeY(), follow.getZ()));
            }
        }

        if (this.getShields() < 4 && this.getRegenShieldTick() > 0) {
            --this.regenShieldTick;
            if (this.getRegenShieldTick() <= 0) {
                this.addShield();
            }
        }

        if (this.getShields() > 0 && this.getShieldHealth() <= 0) {
            this.breakShield();
        }

        if (this.getSummonCooldown() > 0){
            --this.summonCooldown;
        }

        if (this.getShockwaveCooldown() > 0){
            --this.shockwaveCooldown;
        }

        if (this.getFireHealCool() > 0){
            --this.fireHealCool;
        }

        if (this.getFleeingCool() > 0){
            --this.fleeingCool;
        }
    }

    public boolean steepDropBelow() {
        boolean blockBeneath = false;

        for (int i = 0; i < 8; i++) {
            if (!this.level.getBlockState(
                    new BlockPos(this.blockPosition().getX(),
                            this.blockPosition().getY() - i,
                            this.blockPosition().getZ())).isAir()) {
                blockBeneath = true;
            }
        }

        return !blockBeneath;
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
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((itemstack.is(Tags.Items.RODS_BLAZE) || item == Items.BLAZE_POWDER) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.WILDFIRE_AMBIENT.get(), 1.0F, 1.25F);
                    float healAmount = 1.0F;
                    if (itemstack.is(Tags.Items.RODS_BLAZE)){
                        healAmount = 4.0F;
                    }
                    this.heal(healAmount);
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

    public class SummonBlazes extends Goal {

        @Override
        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof BlazeServant blazeServant && blazeServant.getTrueOwner() instanceof Wildfire;
            int i = Wildfire.this.level.getEntitiesOfClass(LivingEntity.class, Wildfire.this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D)
                    , predicate).size();
            return i < 4 && Wildfire.this.getSummonCooldown() <= 0 && Wildfire.this.getTarget() != null && Wildfire.this.getTarget().distanceTo(Wildfire.this) > 10.0D;
        }

        @Override
        public void start() {
            super.start();
            if (Wildfire.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 1 + serverLevel.random.nextInt(1); ++i1) {
                    BlazeServant blazeServant = new BlazeServant(ModEntityType.BLAZE_SERVANT.get(), serverLevel);
                    BlockPos blockPos = BlockFinder.SummonFlyingRadius(Wildfire.this.blockPosition(), blazeServant, serverLevel, 3);
                    blazeServant.setTrueOwner(Wildfire.this);
                    blazeServant.moveTo(blockPos, Wildfire.this.getYRot(), Wildfire.this.getXRot());
                    if (MobsConfig.WildfireSummonsLife.get()) {
                        blazeServant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    }
                    blazeServant.setPersistenceRequired();
                    blazeServant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(blazeServant)){
                        for (int i = 0; i < serverLevel.random.nextInt(10) + 10; ++i) {
                            serverLevel.sendParticles(ModParticleTypes.SUMMON.get(), blazeServant.getRandomX(1.5D), blazeServant.getRandomY(), blazeServant.getRandomZ(1.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
                        }
                        ColorUtil colorUtil = new ColorUtil(0xffffff);
                        ServerParticleUtil.windShockwaveParticle(serverLevel, colorUtil, 0.1F, 0.1F, 0.05F, -1, blazeServant.position());
                    }
                }
                Wildfire.this.setSummonCooldown(MathHelper.secondsToTicks(7));
            }
        }
    }

    public class ShockwaveAttackGoal extends Goal{
        private int attackTime = 0;

        public ShockwaveAttackGoal(){
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (Wildfire.this.getTarget() != null){
                if (Wildfire.this.getShockwaveCooldown() <= 0){
                    if (Wildfire.this.level.random.nextFloat() <= 0.4F) {
                        return Wildfire.this.getTarget().distanceTo(Wildfire.this) <= 4.0D;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTime < MathHelper.secondsToTicks(2.4F);
        }

        @Override
        public void start() {
            super.start();
            Wildfire.this.playSound(ModSounds.WILDFIRE_SHOCKWAVE.get());
            Wildfire.this.setAnimationState(SHOCKWAVE);
            Wildfire.this.getNavigation().stop();
            this.attackTime = 0;
        }

        public void stop() {
            this.attackTime = 0;
            Wildfire.this.setAnimationState(0);
            Wildfire.this.setShockwaveCooldown(MathHelper.secondsToTicks(4));
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            Wildfire.this.getNavigation().stop();
            ++this.attackTime;
            if (Wildfire.this.getTarget() != null) {
                Wildfire.this.getLookControl().setLookAt(Wildfire.this.getTarget(), 30.0F, 30.0F);
            }

            if (this.attackTime == 25){
                this.shockwave();
                if (Wildfire.this.level instanceof ServerLevel serverLevel){
                    Vec3 vec3 = Wildfire.this.position();
                    ColorUtil colorUtil = new ColorUtil(0xdd9c16);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 2, 1), vec3.x, BlockFinder.moveDownToGround(Wildfire.this) + 0.5F, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        public void shockwave(){
            for (LivingEntity target : Wildfire.this.level.getEntitiesOfClass(LivingEntity.class, Wildfire.this.getBoundingBox().inflate(3.0D))) {
                if (target != Wildfire.this && !MobUtil.areAllies(Wildfire.this, target)) {
                    if (Wildfire.this.doHurtTarget(target)) {
                        double d0 = target.getX() - Wildfire.this.getX();
                        double d1 = target.getZ() - Wildfire.this.getZ();
                        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                        MobUtil.push(target, d0 / d2 * 5.0D, 0.2D, d1 / d2 * 5.0D);
                    }
                }
            }
        }
    }

    //I'm becoming smaaaarrrtttteeerrrr
    public class IdleGoal extends Goal {

        public IdleGoal(){
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return Wildfire.this.isAlive() && !Wildfire.this.isMoving();
        }

        @Override
        public void stop() {
            super.stop();
            Wildfire.this.setAnimationState(0);
        }

        @Override
        public void tick() {
            Wildfire.this.setAnimationState(IDLE);
        }
    }

    public static class ShootGoal extends Goal{
        private final Wildfire mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;

        public ShootGoal(Wildfire mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                float distance = this.mob.getHealth() <= (this.mob.getMaxHealth() * 0.7F) ? 9.0F : 2.0F;
                return this.mob.hasLineOfSight(this.target) && (this.mob.distanceTo(this.target) > distance || this.mob.getFleeingCool() > 0);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            float distance = this.mob.getHealth() <= (this.mob.getMaxHealth() * 0.7F) ? 9.0F : 2.0F;
            return this.canUse() || (this.target != null && this.target.isAlive() && (this.mob.distanceTo(this.target) > distance || this.mob.getFleeingCool() > 0)) || this.attackTime > 1;
        }

        @Override
        public void start() {
            super.start();
            this.mob.playSound(ModSounds.WILDFIRE_PRE_ATTACK.get(), 1.3F, 1.0F);
        }

        public void stop() {
            this.target = null;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.attackTime <= 0 && this.target != null && this.target.distanceTo(this.mob) > 10.0F){
                this.mob.getNavigation().moveTo(this.target, 1.3F);
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            } else {
                this.mob.getNavigation().stop();
                if (this.target != null){
                    MobUtil.instaLook(this.mob, this.target);
                }
                ++this.attackTime;
                if (this.attackTime >= 1) {
                    this.mob.setAnimationState(SHOOT);
                }
                if (this.attackTime == 5) {
                    float damage = AttributesConfig.WildfireRangeDamage.get().floatValue() + this.mob.getFireBallDamage();

                    Vec3 vector3d = this.mob.getViewVector(1.0F);
                    ShieldDebris fireball = new ShieldDebris(this.mob.level, this.mob, vector3d.x, vector3d.y, vector3d.z);
                    fireball.shoot(vector3d.x, vector3d.y, vector3d.z, 1.0F, 0.0F);
                    fireball.setOwner(this.mob);
                    fireball.setPos(this.mob.getX() + vector3d.x / 2, this.mob.getEyeY() - 0.2, this.mob.getZ() + vector3d.z / 2);
                    fireball.setDamage(damage);
                    this.mob.level.addFreshEntity(fireball);
                    this.mob.playSound(ModSounds.WILDFIRE_SHOOT.get());
                }
                if (this.attackTime >= 24) {
                    this.attackTime = 0;
                    this.mob.setAnimationState(0);
                }
            }
        }
    }

    public class SeekFireGoal extends MoveToBlockGoal {

        public SeekFireGoal() {
            super(Wildfire.this, 1.3D, 16);
        }

        @Override
        public boolean canUse() {
            return Wildfire.this.getHealth() < Wildfire.this.getMaxHealth() && super.canUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader p_25153_, BlockPos p_25154_) {
            BlockState blockstate = p_25153_.getBlockState(p_25154_);
            return blockstate.getBlock() instanceof BaseFireBlock;
        }
    }
}
