package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class Leapleaf extends Summoned{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(Leapleaf.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Leapleaf.class, EntityDataSerializers.BYTE);
    private static final UUID LEAP_ATTACK_MODIFIER_UUID = UUID.fromString("c8724bee-d7fe-46e5-9319-980ea1146ebb");
    private static final AttributeModifier LEAP_ATTACK_MODIFIER = new AttributeModifier(LEAP_ATTACK_MODIFIER_UUID, "Leap Attack Bonus", 1.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID LEAP_KNOCKBACK_MODIFIER_UUID = UUID.fromString("ad8d395e-7773-4138-bdc4-ea80768a2101");
    private static final AttributeModifier LEAP_KNOCKBACK_MODIFIER = new AttributeModifier(LEAP_KNOCKBACK_MODIFIER_UUID, "Leap Knockback Bonus", 2.0D, AttributeModifier.Operation.ADDITION);
    public static final int REST_TIME = MathHelper.secondsToTicks(4);
    public static String IDLE = "idle";
    public static String WALK = "walk";
    public static String SMASH = "smash";
    public static String CHARGE = "charge";
    public static String LEAP = "leap";
    public static String REST = "rest";
    public static String ALERT = "alert";
    public int attackTick;
    public int chargeTick;
    public int leapTick;
    public int restTick = 0;
    private int idleTime;
    public int noveltyTick;
    public int coolTick = MathHelper.secondsToTicks(2);
    public boolean isNovelty = false;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState leapAnimationState = new AnimationState();
    public AnimationState restAnimationState = new AnimationState();
    public AnimationState alertAnimationState = new AnimationState();

    public Leapleaf(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(1, new StrafeGoal(this));
        this.goalSelector.addGoal(2, new LeapGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.LeapleafHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.LeapleafArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.275D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.LeapleafDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.LeapleafHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.LeapleafArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.LeapleafDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    @Override
    public int xpReward() {
        return 20;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.LEAPLEAF_AMBIENT.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.getCurrentAnimation() == this.getAnimationState("idle")) {
            super.playAmbientSound();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.LEAPLEAF_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.LEAPLEAF_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.LEAPLEAF_STEP.get(), 0.15F, this.getVoicePitch());
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
        } else if (Objects.equals(animation, WALK)){
            return 2;
        } else if (Objects.equals(animation, SMASH)){
            return 3;
        } else if (Objects.equals(animation, CHARGE)){
            return 4;
        } else if (Objects.equals(animation, LEAP)){
            return 5;
        } else if (Objects.equals(animation, REST)){
            return 6;
        } else if (Objects.equals(animation, ALERT)){
            return 7;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.smashAnimationState);
        animationStates.add(this.chargeAnimationState);
        animationStates.add(this.leapAnimationState);
        animationStates.add(this.restAnimationState);
        animationStates.add(this.alertAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAllAnimations()){
            animationState.stop();
        }
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
                        this.smashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                    case 4:
                        this.chargeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chargeAnimationState);
                        break;
                    case 5:
                        this.leapAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.leapAnimationState);
                        break;
                    case 6:
                        this.restAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.restAnimationState);
                        break;
                    case 7:
                        this.alertAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.alertAnimationState);
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

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.restTick > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity p_147185_) {
        return super.hasLineOfSight(p_147185_) && this.restTick <= 0;
    }

    public boolean isCharging() {
        return this.getFlag(1);
    }

    public void setCharging(boolean leap) {
        this.setFlag(1, leap);
        this.chargeTick = 0;
    }

    public boolean isLeaping() {
        return this.getFlag(2);
    }

    public void setLeaping(boolean leaping) {
        this.setFlag(2, leaping);
        this.leapTick = 0;
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(4);
    }

    public void setMeleeAttacking(boolean leaping) {
        this.setFlag(4, leaping);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public boolean isResting(){
        return this.getCurrentAnimation() == this.getAnimationState("rest");
    }

    public boolean isChestPound(){
        return this.isCharging() && this.chargeTick < MathHelper.secondsToTicks(1.21F);
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource p_147189_) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive() && this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5D)).stream().noneMatch(living -> living == this.getTrueOwner() && CuriosFinder.hasWildRobe(living));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()){
            if (!this.level.isClientSide) {
                AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.ATTACK_DAMAGE);
                AttributeInstance modifiableattributeinstance2 = this.getAttribute(Attributes.ATTACK_KNOCKBACK);
                if (modifiableattributeinstance != null) {
                    if (this.isLeaping()) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            modifiableattributeinstance.removeModifier(LEAP_ATTACK_MODIFIER);
                            modifiableattributeinstance.addTransientModifier(LEAP_ATTACK_MODIFIER);
                        }
                    } else {
                        if (modifiableattributeinstance.hasModifier(LEAP_ATTACK_MODIFIER)) {
                            modifiableattributeinstance.removeModifier(LEAP_ATTACK_MODIFIER);
                        }
                    }
                }
                if (modifiableattributeinstance2 != null) {
                    if (this.isLeaping()) {
                        if (this.getAttribute(Attributes.ATTACK_KNOCKBACK) != null) {
                            modifiableattributeinstance2.removeModifier(LEAP_KNOCKBACK_MODIFIER);
                            modifiableattributeinstance2.addTransientModifier(LEAP_KNOCKBACK_MODIFIER);
                        }
                    } else {
                        if (modifiableattributeinstance2.hasModifier(LEAP_KNOCKBACK_MODIFIER)) {
                            modifiableattributeinstance2.removeModifier(LEAP_KNOCKBACK_MODIFIER);
                        }
                    }
                }

                if (this.restTick > 0) {
                    this.setAnimationState(REST);
                    --this.restTick;
                    if (this.restTick == (REST_TIME - 1)){
                        this.playSound(ModSounds.LEAPLEAF_REST.get(), this.getSoundVolume(), this.getVoicePitch());
                    }
                } else {
                    if (!this.isMeleeAttacking() && !this.isChestPound() && !this.isLeaping()) {
                        ++this.idleTime;
                        if (this.level.random.nextFloat() <= 0.05F && this.hurtTime <= 0 && (this.getTarget() == null || this.getTarget().isDeadOrDying()) && !this.isNovelty && this.idleTime >= MathHelper.secondsToTicks(10)) {
                            this.idleTime = 0;
                            this.isNovelty = true;
                            this.level.broadcastEntityEvent(this, (byte) 22);
                        }
                        if (!this.isMoving()) {
                            if (this.isNovelty){
                                this.setAnimationState(ALERT);
                            } else {
                                this.setAnimationState(IDLE);
                            }
                        } else {
                            this.setAnimationState(WALK);
                        }
                    } else {
                        this.isNovelty = false;
                        this.level.broadcastEntityEvent(this, (byte) 23);
                    }
                    if (this.isMeleeAttacking()) {
                        ++this.attackTick;
                        if (this.attackTick >= MathHelper.secondsToTicks(1.21F)){
                            this.setMeleeAttacking(false);
                        }
                    }
                    if (this.isCharging()) {
                        if (this.chargeTick < MathHelper.secondsToTicks(2)) {
                            ++this.chargeTick;
                        }
                    }
                    if (this.isLeaping()) {
                        ++this.leapTick;
                    }
                    if (this.coolTick > 0) {
                        --this.coolTick;
                    }
                    if (this.isNovelty){
                        ++noveltyTick;
                        this.level.broadcastEntityEvent(this, (byte) 24);
                        if (this.noveltyTick >= MathHelper.secondsToTicks(5.75F) || this.getTarget() != null || this.hurtTime > 0){
                            this.isNovelty = false;
                            this.noveltyTick = 0;
                            this.level.broadcastEntityEvent(this, (byte) 23);
                        }
                    }
                }
            }
        }
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public Vec3 getHorizontalLeftLookAngle() {
        return MobUtil.calculateViewVector(0, this.getYRot() - 90);
    }

    public Vec3 getHorizontalRightLookAngle() {
        return MobUtil.calculateViewVector(0, this.getYRot() + 90);
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + enemy.getBbWidth());
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else if (p_21375_ == 22){
            this.isNovelty = true;
        } else if (p_21375_ == 23){
            this.isNovelty = false;
            this.noveltyTick = 0;
        } else if (p_21375_ == 24){
            ++this.noveltyTick;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE_MEAL && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.LEAPLEAF_AMBIENT.get(), 1.0F, 1.25F);
                    this.heal(5.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    pPlayer.swing(p_230254_2_);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }

    class AttackGoal extends Goal {

        @Override
        public boolean canUse() {
            return Leapleaf.this.getTarget() != null
                    && Leapleaf.this.getTarget().isAlive()
                    && !Leapleaf.this.isCharging()
                    && !Leapleaf.this.isLeaping()
                    && Leapleaf.this.hasLineOfSight(Leapleaf.this.getTarget())
                    && Leapleaf.this.getTarget().distanceTo(Leapleaf.this) <= 2.5F;
        }

        @Override
        public boolean canContinueToUse() {
            return Leapleaf.this.isMeleeAttacking();
        }

        @Override
        public void start() {
            Leapleaf.this.setMeleeAttacking(true);
            Leapleaf.this.setAggressive(true);
            Leapleaf.this.level.broadcastEntityEvent(Leapleaf.this, (byte) 6);
        }

        @Override
        public void stop() {
            Leapleaf.this.setMeleeAttacking(false);
            Leapleaf.this.setAggressive(false);
            Leapleaf.this.level.broadcastEntityEvent(Leapleaf.this, (byte) 7);
        }

        @Override
        public void tick() {
            LivingEntity livingentity = Leapleaf.this.getTarget();
            if (livingentity == null) {
                return;
            }

            MobUtil.instaLook(Leapleaf.this, Leapleaf.this.getTarget());

            Leapleaf.this.getNavigation().stop();
            if (Leapleaf.this.attackTick == 1) {
                Leapleaf.this.setAnimationState(SMASH);
                Leapleaf.this.playSound(ModSounds.LEAPLEAF_SMASH.get(), Leapleaf.this.getSoundVolume(), Leapleaf.this.getVoicePitch());
            }
            if (Leapleaf.this.attackTick == 13) {
                double x = Leapleaf.this.getX() + Leapleaf.this.getHorizontalLookAngle().x * 2;
                double z = Leapleaf.this.getZ() + Leapleaf.this.getHorizontalLookAngle().z * 2;
                AABB aabb = makeAttackRange(x,
                        Leapleaf.this.getY(),
                        z, 1, 1, 1);
                for (LivingEntity target : Leapleaf.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != Leapleaf.this && !target.isAlliedTo(Leapleaf.this) && !Leapleaf.this.isAlliedTo(target)) {
                        Leapleaf.this.doHurtTarget(target);
                    }
                }
                if (Leapleaf.this.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = new BlockPos(x, Leapleaf.this.getY() - 1.0F, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, x, Leapleaf.this.getY() + 0.25D, z, 1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 2, 1), x, BlockFinder.moveDownToGround(Leapleaf.this), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class StrafeGoal extends Goal {
        public Leapleaf leapleaf;
        @Nullable
        public LivingEntity target;
        private int seeTime;
        private int strafingTime = -1;
        private boolean strafingLeft;

        public StrafeGoal(Leapleaf leapleaf) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
            this.leapleaf = leapleaf;
            this.target = leapleaf.getTarget();
        }

        @Override
        public boolean canUse() {
            this.target = this.leapleaf.getTarget();
            return this.target != null && !this.leapleaf.isMeleeAttacking() && !this.leapleaf.isCharging() && !this.leapleaf.isLeaping() && this.leapleaf.coolTick > 0;
        }

        @Override
        public void stop() {
            super.stop();
            this.seeTime = 0;
            this.leapleaf.getMoveControl().strafe(0, 0);
        }

        @Override
        public void tick() {
            this.target = leapleaf.getTarget();

            if (this.target != null) {
                double d0 = this.leapleaf.distanceTo(this.target);
                boolean flag = this.leapleaf.getSensing().hasLineOfSight(this.target);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (flag) {
                    this.leapleaf.lookAt(EntityAnchorArgument.Anchor.EYES, this.target.position());
                }

                if (this.strafingTime >= 20) {
                    if (this.leapleaf.random.nextInt(30) == 0) {
                        this.strafingLeft = !this.strafingLeft;
                    }

                    this.strafingTime = 0;
                }

                if (d0 > 7.0F && this.seeTime < 20) {
                    this.leapleaf.getNavigation().moveTo(this.target, 1.0F);
                    this.strafingTime = -1;
                } else {
                    this.leapleaf.getNavigation().stop();
                    ++this.strafingTime;
                }

                if (this.strafingTime > -1) {
                    this.leapleaf.getMoveControl().strafe(d0 < 5.0F ? -0.5F : 0.5F, this.strafingLeft ? 0.5F : -0.5F);
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class LeapGoal extends Goal {
        public Leapleaf leapleaf;
        @Nullable
        public LivingEntity target;

        public LeapGoal(Leapleaf leapleaf) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
            this.leapleaf = leapleaf;
            this.target = leapleaf.getTarget();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            this.target = this.leapleaf.getTarget();

            return this.target != null
                    && !this.leapleaf.isCharging()
                    && !this.leapleaf.isLeaping()
                    && this.leapleaf.coolTick <= 0
                    && this.leapleaf.onGround
                    && this.leapleaf.hasLineOfSight(this.target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.leapleaf.isCharging() || this.leapleaf.isLeaping();
        }

        @Override
        public void start() {
            if (this.target != null){
                MobUtil.instaLook(this.leapleaf, this.target);
            }
            this.leapleaf.setMeleeAttacking(false);
            this.leapleaf.setCharging(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.leapleaf.setCharging(false);
            this.leapleaf.setLeaping(false);
            this.leapleaf.leapTick = 0;
        }

        @Override
        public void tick() {
            this.target = this.leapleaf.getTarget();

            this.leapleaf.navigation.stop();
            if (this.target != null) {
                this.leapleaf.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                if (this.leapleaf.chargeTick == 1){
                    this.leapleaf.setAnimationState(CHARGE);
                    this.leapleaf.playSound(ModSounds.LEAPLEAF_CHARGE.get(), this.leapleaf.getSoundVolume(), this.leapleaf.getVoicePitch());
                }
                if (this.leapleaf.chargeTick < MathHelper.secondsToTicks(1.21F)){
                    this.leapleaf.navigation.stop();
                } else {
                    if (this.leapleaf.distanceTo(this.target) <= 6.5F){
                        this.leapleaf.setCharging(false);
                        this.leapleaf.setAnimationState(LEAP);
                        this.leapleaf.playSound(ModSounds.LEAPLEAF_LEAP.get(), this.leapleaf.getSoundVolume(), this.leapleaf.getVoicePitch());
                        this.leapleaf.setLeaping(true);
                    } else {
                        this.leapleaf.navigation.moveTo(this.target, 1.25F);
                    }
                }

                if (this.leapleaf.leapTick == 1) {
                    double d0 = this.target.getX() - this.leapleaf.getX();
                    double d1 = this.target.getY() - this.leapleaf.getY();
                    double d2 = this.target.getZ() - this.leapleaf.getZ();
                    this.leapleaf.setDeltaMovement(d0 * 0.15D, 0.75 + Mth.clamp(d1 * 0.05D, 0, 10), d2 * 0.15D);
                }
            }

            double x = this.leapleaf.getX() + this.leapleaf.getHorizontalLookAngle().x;
            double z = this.leapleaf.getZ() + this.leapleaf.getHorizontalLookAngle().z;
            double xLeft = this.leapleaf.getX() + this.leapleaf.getHorizontalLeftLookAngle().x;
            double zLeft = this.leapleaf.getZ() + this.leapleaf.getHorizontalLeftLookAngle().z;
            double xRight = this.leapleaf.getX() + this.leapleaf.getHorizontalRightLookAngle().x;
            double zRight = this.leapleaf.getZ() + this.leapleaf.getHorizontalRightLookAngle().z;
            if (this.leapleaf.isLeaping() && this.leapleaf.onGround && this.leapleaf.leapTick > 1) {
                AABB aabb = makeAttackRange(x,
                        this.leapleaf.getY(),
                        z, 3, 3, 3);
                boolean random = this.leapleaf.random.nextFloat() <= 0.25F;
                for (LivingEntity target : this.leapleaf.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != this.leapleaf && !target.isAlliedTo(this.leapleaf) && !this.leapleaf.isAlliedTo(target)) {
                        if (this.leapleaf.doHurtTarget(target) && (random || !target.isAlive())){
                            this.leapleaf.restTick = REST_TIME;
                        }
                    }
                }
                this.leapleaf.setLeaping(false);
                this.leapleaf.coolTick = MathHelper.secondsToTicks(2);
                if (this.leapleaf.level instanceof ServerLevel serverLevel){
                    BlockPos leftPos = new BlockPos(xLeft, this.leapleaf.getY() - 1.0F, zLeft);
                    BlockPos rightPos = new BlockPos(xRight, this.leapleaf.getY() - 1.0F, zRight);
                    BlockState leftState = serverLevel.getBlockState(leftPos);
                    BlockState rightState = serverLevel.getBlockState(rightPos);
                    BlockParticleOption leftOption = new BlockParticleOption(ParticleTypes.BLOCK, leftState);
                    BlockParticleOption rightOption = new BlockParticleOption(ParticleTypes.BLOCK, rightState);
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, leftOption, xLeft, this.leapleaf.getY() + 0.25D, zLeft, 1.5F);
                    }
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, rightOption, xRight, this.leapleaf.getY() + 0.25D, zRight, 1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(leftState.getMapColor(serverLevel, leftPos).col);
                    ColorUtil colorUtil1 = new ColorUtil(rightState.getMapColor(serverLevel, rightPos).col);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 5, 1), xLeft, BlockFinder.moveDownToGround(this.leapleaf), zLeft, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), 5, 1), xRight, BlockFinder.moveDownToGround(this.leapleaf), zRight, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    BlockPos blockPos2 = new BlockPos(xLeft, this.leapleaf.getY() + 0.25D, zLeft);
                    BlockPos blockPos3 = new BlockPos(xRight, this.leapleaf.getY() + 0.25D, zRight);

                    for (Direction direction : Direction.values()){
                        if (direction.getAxis().isHorizontal()){
                            BlockPos blockPos4 = blockPos2.relative(direction, 2);
                            BlockPos blockPos5 = blockPos3.relative(direction, 2);
                            serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), blockPos4.getX(), blockPos4.getY(), blockPos4.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                            serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), 0, colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), 0.5F);
                        }
                    }
                }
            }
        }

        public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
            return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
        }
    }
}
