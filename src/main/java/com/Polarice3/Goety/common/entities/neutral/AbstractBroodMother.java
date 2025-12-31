package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import com.Polarice3.Goety.common.entities.projectiles.WebShot;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CSetDeltaMovement;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
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
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class AbstractBroodMother extends Summoned implements IAutoRideable, PlayerRideableJumping, RiderShieldingMount, RangedAttackMob {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(AbstractBroodMother.class, EntityDataSerializers.BYTE);
    private static final UUID DETECTION_MODIFIER_UUID = UUID.fromString("858f6b2f-73e3-45a0-8bef-bb31e0d55be4");
    public static final AttributeModifier DETECTION_MODIFIER = new AttributeModifier(DETECTION_MODIFIER_UUID, "Light Is Blinding", -1.0D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(AbstractBroodMother.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractBroodMother.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(AbstractBroodMother.class, EntityDataSerializers.BOOLEAN);
    private static final UUID SPEED_STOP_UUID = UUID.fromString("e336b82c-bd58-4374-9875-0b50a602fef2");
    private static final AttributeModifier SPEED_STOP = new AttributeModifier(SPEED_STOP_UUID, "Stop moving", -1.0D, AttributeModifier.Operation.ADDITION);
    public static String ATTACK = "attack";
    public static String SHOOT = "shoot";
    public static String LAY_EGGS = "lay_eggs";
    public static String CHARGE = "charge";
    public static String BACK_OFF = "back_off";
    public static String JUMP = "jump";
    public static String DEATH = "death";
    public static int WEB_ATTACK = 1;
    public static int LAY_EGG_ATTACK = 2;
    public static int CHARGE_ATTACK = 3;
    public static int BACK_OFF_ATTACK = 4;
    public int deathTime = 0;
    protected int meleeTicks;
    protected int attackTicks;
    protected int jumpTicks;
    protected int multiLay;
    protected int summonCooldown;
    protected int webCooldown;
    protected int chargeCooldown;
    protected int backOffCooldown;
    public int circleTick;
    public boolean circleDirection;
    public boolean isCircling;
    public boolean clientAttacking;
    protected boolean isJumping;
    protected float playerJumpPendingScale;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState layEggsAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState backOffAnimationState = new AnimationState();
    public AnimationState jumpAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public AbstractBroodMother(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new WebGoal());
        this.goalSelector.addGoal(0, new ChargeGoal());
        this.goalSelector.addGoal(1, new BackOffGoal());
        this.goalSelector.addGoal(1, new SummonSpiders());
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 140.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Mob.class, 140.0F));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.attackGoal();
    }

    public void attackGoal(){
        this.goalSelector.addGoal(4, new ModMeleeAttackGoal(this, 1.0D, true){

            @Override
            public boolean canUse() {
                return super.canUse() && this.mob instanceof AbstractBroodMother broodMother && !broodMother.isCircling;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && this.mob instanceof AbstractBroodMother broodMother && !broodMother.isCircling;
            }

            protected void checkAndPerformAttack(LivingEntity livingEntity, double distToEnemySqr) {
                double d0 = this.getAttackReachSqr(livingEntity);
                this.mob.getLookControl().setLookAt(livingEntity, 100.0F, 100.0F);
                if (distToEnemySqr <= d0 && this.isTimeToAttack()) {
                    int i = 0;
                    float angle = 2.0F;
                    double radius = 2.0D;
                    double extraX = this.mob.getX() + 0.8D * Math.sin((double)(-this.mob.getYRot()) * Math.PI / 180.0D) + (double)angle * Math.sin((double)(-this.mob.yHeadRot) * Math.PI / 180.0D) * Math.cos((double)(-this.mob.getXRot()) * Math.PI / 180.0D);
                    double extraZ = this.mob.getZ() + 0.8D * Math.cos((double)(-this.mob.getYRot()) * Math.PI / 180.0D) + (double)angle * Math.cos((double)(-this.mob.yHeadRot) * Math.PI / 180.0D) * Math.cos((double)(-this.mob.getXRot()) * Math.PI / 180.0D);
                    List<LivingEntity> list = this.mob.level.getEntitiesOfClass(LivingEntity.class, new AABB(extraX - radius, this.mob.getY(), extraZ - radius, extraX + radius, this.mob.getY() + radius, extraZ + radius));
                    for (LivingEntity target : list) {
                        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && !MobUtil.areAllies(this.mob, target)) {
                            if (this.mob.doHurtTarget(target) || target.isBlocking()) {
                                ++i;
                            }
                        }
                    }
                    if (i > 0) {
                        this.mob.swing(InteractionHand.MAIN_HAND);
                        this.mob.playSound(ModSounds.SWING.get(), 2.0F, 0.5F);
                    }
                    this.resetAttackCooldown();
                }

            }

            @Override
            protected double getAttackReachSqr(LivingEntity target) {
                return this.mob.getBbWidth() * 1.25D * this.mob.getBbWidth() * 1.25D + target.getBbWidth();
            }
        });
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BroodMotherHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ARMOR, AttributesConfig.BroodMotherArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BroodMotherDamage.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BroodMotherHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BroodMotherArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BroodMotherDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(AUTO_MODE, false);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.stopAllAnimation();
                        this.attackAnimationState.start(this.tickCount);
                        break;
                    case 2:
                        this.stopAllAnimation();
                        this.shootAnimationState.start(this.tickCount);
                        break;
                    case 3:
                        this.stopAllAnimation();
                        this.layEggsAnimationState.start(this.tickCount);
                        break;
                    case 4:
                        this.stopAllAnimation();
                        this.chargeAnimationState.start(this.tickCount);
                        break;
                    case 5:
                        this.stopAllAnimation();
                        this.backOffAnimationState.start(this.tickCount);
                        break;
                    case 6:
                        this.stopAllAnimation();
                        this.jumpAnimationState.start(this.tickCount);
                        break;
                    case 7:
                        this.stopAllAnimation();
                        this.deathAnimationState.start(this.tickCount);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SummonCool", this.getSummonCooldown());
        pCompound.putInt("WebCool", this.getWebCooldown());
        pCompound.putInt("ChargeCool", this.getChargeCooldown());
        pCompound.putInt("BackOffCool", this.getBackOffCooldown());
        pCompound.putInt("MultiLay", this.getMultiLay());
        pCompound.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("SummonCool")) {
            this.setSummonCooldown(pCompound.getInt("SummonCool"));
        }
        if (pCompound.contains("WebCool")) {
            this.setWebCooldown(pCompound.getInt("WebCool"));
        }
        if (pCompound.contains("ChargeCool")) {
            this.setChargeCooldown(pCompound.getInt("ChargeCool"));
        }
        if (pCompound.contains("BackOffCool")) {
            this.setBackOffCooldown(pCompound.getInt("BackOffCool"));
        }
        if (pCompound.contains("MultiLay")) {
            this.setMultiLay(pCompound.getInt("MultiLay"));
        }
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractBroodMother;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.BroodMotherLimit.get();
    }

    public int xpReward() {
        return 20;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33814_) {
        return SoundEvents.SPIDER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    protected void playStepSound(BlockPos p_33804_, BlockState p_33805_) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, ATTACK)){
            return 1;
        } else if (Objects.equals(animation, SHOOT)){
            return 2;
        } else if (Objects.equals(animation, LAY_EGGS)){
            return 3;
        } else if (Objects.equals(animation, CHARGE)){
            return 4;
        } else if (Objects.equals(animation, BACK_OFF)){
            return 5;
        } else if (Objects.equals(animation, JUMP)){
            return 6;
        } else if (Objects.equals(animation, DEATH)){
            return 7;
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

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.shootAnimationState);
        animationStates.add(this.layEggsAnimationState);
        animationStates.add(this.chargeAnimationState);
        animationStates.add(this.backOffAnimationState);
        animationStates.add(this.jumpAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
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
            if (entity instanceof Mob mob){
                if (MobsConfig.ServantRideAutonomous.get()){
                    return null;
                }
                return mob;
            } else if (entity instanceof LivingEntity livingEntity
                    && this.notClientAttacking()
                    && !this.isAutonomous()) {
                return livingEntity;
            }
        }

        return null;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance instance) {
        if (instance.getEffect() == GoetyEffects.ACID_VENOM.get() || instance.getEffect() == MobEffects.POISON) {
            net.minecraftforge.event.entity.living.MobEffectEvent.Applicable event = new net.minecraftforge.event.entity.living.MobEffectEvent.Applicable(this, instance);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.canBeAffected(instance);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isVehicle()) {
            if (source.getEntity() != null && this.getFirstPassenger() != null) {
                if (source.getEntity() == this.getFirstPassenger() && MobUtil.areAllies(this, this.getFirstPassenger())) {
                    return false;
                }
            }
        }
        return super.hurt(source, amount);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 30) {
            this.spawnAnim();
            this.remove(RemovalReason.KILLED);
        }
        if (!this.getPassengers().isEmpty()) {
            this.getPassengers().forEach(Entity::stopRiding);
        }
        this.hurtTime = 1;
    }

    public void die(DamageSource p_21014_) {
        this.setAnimationState(DEATH);
        super.die(p_21014_);
    }

    @Override
    public boolean isStaying() {
        return super.isStaying() || (this.getControllingPassenger() instanceof IServant servant && servant.isStaying());
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int attackType) {
        this.entityData.set(ATTACK_TYPE, attackType);
    }

    public int getAttackTicks() {
        return this.attackTicks;
    }

    public void setAttackTicks(int attackTicks) {
        this.attackTicks = attackTicks;
    }

    public void setSummonCooldown(int summonCooldown){
        this.summonCooldown = summonCooldown;
    }

    public int getSummonCooldown() {
        return this.summonCooldown;
    }

    public void setWebCooldown(int webCooldown){
        this.webCooldown = webCooldown;
    }

    public int getWebCooldown() {
        return this.webCooldown;
    }

    public void setChargeCooldown(int chargeCooldown){
        this.chargeCooldown = chargeCooldown;
    }

    public int getChargeCooldown() {
        return this.chargeCooldown;
    }

    public void setBackOffCooldown(int backOffCooldown){
        this.backOffCooldown = backOffCooldown;
    }

    public int getBackOffCooldown() {
        return this.backOffCooldown;
    }

    public void setMultiLay(int multiLay){
        this.multiLay = multiLay;
    }

    public int getMultiLay() {
        return this.multiLay;
    }

    public boolean isMeleeAttacking() {
        return this.meleeTicks > 0;
    }

    @Override
    public float getVoicePitch() {
        return 0.5F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.setClimbing(this.horizontalCollision);
            AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.FOLLOW_RANGE);
            if (MobUtil.isInBrightLight(this)){
                if (modifiableattributeinstance != null) {
                    if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                        modifiableattributeinstance.removeModifier(DETECTION_MODIFIER);
                        modifiableattributeinstance.addTransientModifier(DETECTION_MODIFIER);
                    }
                }
            } else {
                if (modifiableattributeinstance != null) {
                    if (modifiableattributeinstance.hasModifier(DETECTION_MODIFIER)) {
                        modifiableattributeinstance.removeModifier(DETECTION_MODIFIER);
                    }
                }
            }
        }
        if (this.summonCooldown > 0) {
            --this.summonCooldown;
        }
        if (this.webCooldown > 0) {
            --this.webCooldown;
        }
        if (this.chargeCooldown > 0) {
            --this.chargeCooldown;
        }
        if (this.backOffCooldown > 0) {
            --this.backOffCooldown;
        }
        if (this.meleeTicks > 0) {
            --this.meleeTicks;
        } else {
            if (this.getCurrentAnimation() == this.getAnimationState(ATTACK)){
                this.setAnimationState(0);
            }
        }
        if (this.jumpTicks > 0){
            --this.jumpTicks;
        } else {
            if (this.getCurrentAnimation() == this.getAnimationState(JUMP)){
                this.setAnimationState(0);
            }
        }
        if (this.getAttackType() > 0) {
            ++this.attackTicks;
            if (!this.level.isClientSide) {
                this.level.broadcastEntityEvent(this, (byte) 4);
            }
        } else {
            if (!this.level.isClientSide) {
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
        }
        AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null){
            if (this.getAttackType() > 0 || this.isMeleeAttacking()) {
                modifiableattributeinstance.removeModifier(SPEED_STOP);
                modifiableattributeinstance.addTransientModifier(SPEED_STOP);
            } else {
                if (modifiableattributeinstance.hasModifier(SPEED_STOP)) {
                    modifiableattributeinstance.removeModifier(SPEED_STOP);
                }
            }
        }
        if (this.getAttackType() != 0){
            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
        }

        if (this.random.nextInt(200) == 0) {
            this.circleDirection = !this.circleDirection;
        }

        this.circleTick += this.circleDirection ? 1 : -1;

        if (this.getTarget() != null) {
            Predicate<Entity> predicate = entity -> entity.isAlive()
                    && ((entity instanceof SpiderServant spiderServant && spiderServant.getTrueOwner() == this) || entity instanceof Spider spider && spider.getTarget() != null && spider.getTarget() == this.getTarget() && this.isHostile());
            int i = this.level.getEntitiesOfClass(LivingEntity.class, this.getTarget().getBoundingBox().inflate(8.0D)
                    , predicate).size();

            if (this.getAttackType() == 0
                    && i > 0
                    && this.getTarget().distanceTo(this) >= 4.0D) {
                this.isCircling = true;
                this.circleTarget(this.getTarget(), this.circleTick);
                MobUtil.instaLook(this, this.getTarget());
            } else {
                this.isCircling = false;
            }
        } else {
            this.isCircling = false;
        }
        this.attackAI();
    }

    public void attackAI(){
        if (this.isAlive() && !this.level.isClientSide) {
            if (this.getAttackType() == WEB_ATTACK){
                if (this.getAttackTicks() == 7 && this.getTarget() != null) {
                    for (int i = 0; i < 8; ++i){
                        this.performRangedAttack(this.getTarget(), 1.0F);
                    }
                    this.playSound(ModSounds.SPIDER_SPIT.get(), 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                }
            }
            if (this.getAttackType() == LAY_EGG_ATTACK) {
                if (this.getAttackTicks() == 10) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        SpiderEgg spiderEgg = new SpiderEgg(ModEntityType.SPIDER_EGG.get(), serverLevel);
                        BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), spiderEgg, serverLevel, 1);
                        spiderEgg.setTrueOwner(this);
                        spiderEgg.moveTo(blockPos, this.getYRot(), this.getXRot());
                        spiderEgg.setPersistenceRequired();
                        spiderEgg.setLimitedLife(MathHelper.secondsToTicks(3 + serverLevel.getRandom().nextInt(3)));
                        spiderEgg.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                        if (serverLevel.addFreshEntity(spiderEgg)) {
                            spiderEgg.playSound(ModSounds.SPIDER_NEST_START.get());
                        }
                    }
                }
            }
            if (this.getAttackType() == CHARGE_ATTACK) {
                Vec3 vector3d = this.getViewVector( 1.0F);
                double distance0 = Math.sqrt(vector3d.x * vector3d.x + vector3d.y * vector3d.y + vector3d.z * vector3d.z);
                double power = 10.0D;
                double x = vector3d.x / distance0 * power * 0.2D;
                double y = vector3d.y / distance0 * power * 0.06D;
                double z = vector3d.z / distance0 * power * 0.2D;

                if (this.getAttackTicks() == 8) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 2.0F, 0.75F);
                    this.setDeltaMovement(x, y > 0.0D ? y + 0.2D : 0.2D, z);
                }

                if (this.getAttackTicks() >= 10){
                    this.areaAttack();
                }
            }
            if (this.getAttackType() == BACK_OFF_ATTACK) {
                Vec3 vector3d = this.getViewVector( 1.0F);
                double distance0 = Math.sqrt(vector3d.x * vector3d.x + vector3d.y * vector3d.y + vector3d.z * vector3d.z);
                double power = 10.0D;
                double x = -(vector3d.x / distance0 * power * 0.2D);
                double y = -(vector3d.y / distance0 * power * 0.06D);
                double z = -(vector3d.z / distance0 * power * 0.2D);

                if (this.getTarget() != null) {
                    double deltaX = this.getX() - this.getTarget().getX();
                    double deltaY = this.getY() - (this.getTarget().getY() + 1.5);
                    double deltaZ = this.getZ() - this.getTarget().getZ();
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                    x = deltaX / distance * power * 0.2D;
                    y = -(deltaY / distance * power * 0.06D);
                    z = deltaZ / distance * power * 0.2D;
                }

                if (this.getAttackTicks() == 8) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 2.0F, 0.75F);
                    this.setDeltaMovement(x, y > 0.0D ? y + 0.2D : 0.2D, z);
                }

                if (this.getAttackTicks() >= 10){
                    this.areaAttack();
                }
            }
        }
    }

    public void areaAttack(){
        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0D),
                living -> !MobUtil.areAllies(living, this) && living.isAlive())){
            double xPower = this.getX() - livingEntity.getX();
            double yPower = this.getY() - livingEntity.getY();
            double zPower = this.getZ() - livingEntity.getZ();
            double distance = Math.sqrt(xPower * xPower + yPower * yPower + zPower * zPower);
            if (this.distanceToSqr(livingEntity) < 9.0D) {
                if (livingEntity.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                    livingEntity.hurtMarked = true;
                    if (!livingEntity.hasEffect(GoetyEffects.TANGLED.get())) {
                        MobUtil.push(livingEntity, -xPower / distance * 2.0D, -yPower / distance * 2.0D + 0.5D, -zPower / distance * 2.0D);
                    }
                }
            }
        }
    }

    private void circleTarget(Entity target, int circleFrame) {
        double t = circleFrame * 0.5D * 0.8D / 10.0D;
        Vec3 movePos = target.position().add(10.0D * Math.cos(t), 0.0D, 10.0D * Math.sin(t));
        this.getNavigation().moveTo(movePos.x(), movePos.y(), movePos.z(), 1.0F);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            this.hurt(this.damageSources().starve(), Float.MAX_VALUE);
        }
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
        if (!p_33796_.is(Blocks.COBWEB) && !p_33796_.is(Blocks.AIR)) {
            super.makeStuckInBlock(p_33796_, p_33797_);
        }

    }

    @Override
    public void swing(InteractionHand p_21007_) {
        this.meleeTicks = 10;
        this.setAnimationState(ATTACK);
        super.swing(p_21007_);
    }

    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity livingEntity) {
                int i = 15;
                if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 30;
                }

                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), i * 20, 1), this);
            }
            this.playSound(ModSounds.SPIDER_BITE.get(), this.getSoundVolume(), this.getVoicePitch() + 0.25F);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public double getRiderShieldingHeight() {
        return 0.5D;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    protected float getRiddenSpeed(Player p_278241_) {
        float f = p_278241_.isSprinting() ? 0.1F : 0.0F;
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) + f;
    }

    protected Vec3 getRiddenInput(Player p_278278_, Vec3 p_275506_) {
        if ((this.onGround() && this.playerJumpPendingScale == 0.0F)) {
            return Vec3.ZERO;
        } else {
            float f = p_278278_.xxa * 0.5F;
            float f1 = p_278278_.zza;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }

            return new Vec3((double)f, 0.0D, (double)f1);
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle() && this.notClientAttacking() && rider instanceof Player player && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = this.getRiddenSpeed(player);
                float f = rider.xxa * speed;
                float f1 = rider.zza * speed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (this.getMobType() != MobType.UNDEAD) {
                    if (this.isInWater() && this.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()) > this.getFluidJumpThreshold() || this.isInLava() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType) && height > this.getFluidJumpThreshold())) {
                        Vec3 vector3d = this.getDeltaMovement();
                        this.setDeltaMovement(vector3d.x, 0.04F, vector3d.z);
                        this.hasImpulse = true;
                        if (f1 > 0.0F) {
                            float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                            float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * 0.04F), 0.0D, (double) (0.4F * f3 * 0.04F)));
                        }
                    }
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                super.travel(pTravelVector);
            }
        }
    }

    public boolean startRiding(Entity p_20330_) {
        return false;
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public double getPassengersRidingOffset() {
        return 1.9D;
    }

    @Override
    public boolean canUpdateMove() {
        return !(this.getControllingPassenger() instanceof Mob);
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setIsJumping(boolean jumping) {
        this.isJumping = jumping;
    }

    @Override
    public boolean canJump() {
        return this.notClientAttacking();
    }

    protected void tickRidden(Player p_278233_, Vec3 p_275693_) {
        super.tickRidden(p_278233_, p_275693_);
        if (p_278233_.isLocalPlayer()) {
            if (this.onGround()) {
                this.setIsJumping(false);
                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    if (!this.isSilent()) {
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 2.0F, 0.75F, false);
                    }
                    this.executeRidersJump(this.playerJumpPendingScale, p_275693_);
                }

                this.playerJumpPendingScale = 0.0F;
            }
        }
    }

    protected void executeRidersJump(float p_248808_, Vec3 p_275435_) {
        double d0 = 1.0D * (double)p_248808_ * (double)this.getBlockJumpFactor();
        double d1 = d0 + this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d1, vec3.z);
        ModNetwork.sendToServer(new CSetDeltaMovement(this.getId(), vec3.x, d1, vec3.z));
        this.setIsJumping(true);
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
        this.setAnimationState(JUMP);
        this.jumpTicks = 10;
        if (p_275435_.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
            float f1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            Vec3 vec31 = this.getDeltaMovement().add(-0.4F * f * p_248808_, 0.0D, 0.4F * f1 * p_248808_);
            this.setDeltaMovement(vec31);
            ModNetwork.sendToServer(new CSetDeltaMovement(this.getId(), vec31.x, vec31.y, vec31.z));
        }

    }

    @Override
    public void onPlayerJump(int p_21696_) {
        if (p_21696_ < 0) {
            p_21696_ = 0;
        }

        if (p_21696_ >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)p_21696_ / 90.0F;
        }
    }

    @Override
    public void handleStartJump(int p_21695_) {
    }

    @Override
    public void handleStopJump() {
    }

    public boolean notClientAttacking(){
        return !this.clientAttacking;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.clientAttacking = true;
        } else if (pId == 5){
            this.clientAttacking = false;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        WebShot webShot = new WebShot(this, this.level);
        webShot.setPos(webShot.position().add(0.0D, 1.0D, 0.0D));
        Vec3 vec3 = p_33317_.getDeltaMovement();
        double d0 = p_33317_.getX() + vec3.x - this.getX();
        double d1 = p_33317_.getEyeY() - (double)1.1F - this.getY();
        double d2 = p_33317_.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        webShot.setXRot(webShot.getXRot() - -20.0F);
        webShot.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 30);
        this.level.addFreshEntity(webShot);
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (pPlayer == this.getTrueOwner()) {
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
            } else if (!pPlayer.isCrouching()) {
                if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                    this.getFirstPassenger().stopRiding();
                    return InteractionResult.SUCCESS;
                } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                    this.doPlayerRide(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public abstract static class BroodMotherGoal extends Goal{

        public BroodMotherGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class SummonSpiders extends BroodMotherGoal {

        @Override
        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof SpiderServant spiderServant && spiderServant.getTrueOwner() instanceof AbstractBroodMother;
            int i = AbstractBroodMother.this.level.getEntitiesOfClass(LivingEntity.class, AbstractBroodMother.this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D)
                    , predicate).size();
            return i < 4
                    && AbstractBroodMother.this.getSummonCooldown() <= 0
                    && AbstractBroodMother.this.getTarget() != null
                    && (AbstractBroodMother.this.getTarget().distanceTo(AbstractBroodMother.this) > 10.0D || AbstractBroodMother.this.getMultiLay() > 0);
        }

        public boolean canContinueToUse() {
            if (AbstractBroodMother.this.getAttackTicks() <= 20) {
                return AbstractBroodMother.this.getAttackType() == LAY_EGG_ATTACK;
            }
            return false;
        }

        @Override
        public void start() {
            AbstractBroodMother.this.setAnimationState(LAY_EGGS);
            AbstractBroodMother.this.setAttackType(LAY_EGG_ATTACK);
            if (AbstractBroodMother.this.getMultiLay() <= 0){
                if (MobUtil.healthIsHalved(AbstractBroodMother.this) && AbstractBroodMother.this.getRandom().nextBoolean()){
                    AbstractBroodMother.this.setMultiLay(3);
                }
            }
        }

        public void tick() {
            AbstractBroodMother.this.getNavigation().stop();
            AbstractBroodMother.this.getMoveControl().strafe(0.0F, 0.0F);
            if (AbstractBroodMother.this.getTarget() != null) {
                AbstractBroodMother.this.getLookControl().setLookAt(AbstractBroodMother.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            AbstractBroodMother.this.setAnimationState(0);
            AbstractBroodMother.this.setAttackTicks(0);
            AbstractBroodMother.this.setAttackType(0);
            if (AbstractBroodMother.this.getMultiLay() > 1){
                AbstractBroodMother.this.setSummonCooldown(MathHelper.secondsToTicks(1));
                AbstractBroodMother.this.setMultiLay(AbstractBroodMother.this.getMultiLay() - 1);
            } else {
                AbstractBroodMother.this.setSummonCooldown(MathHelper.secondsToTicks(7));
                AbstractBroodMother.this.setMultiLay(0);
            }
        }
    }

    public class WebGoal extends BroodMotherGoal {

        public boolean canUse() {
            return AbstractBroodMother.this.random.nextInt(16) == 0
                    && AbstractBroodMother.this.getTarget() != null
                    && AbstractBroodMother.this.getTarget().distanceTo(AbstractBroodMother.this) <= 16.0F
                    && AbstractBroodMother.this.hasLineOfSight(AbstractBroodMother.this.getTarget())
                    && AbstractBroodMother.this.getWebCooldown() < 1;
        }

        public boolean canContinueToUse() {
            if (AbstractBroodMother.this.getAttackTicks() <= 20) {
                return AbstractBroodMother.this.getAttackType() == WEB_ATTACK;
            }
            return false;
        }

        public void start() {
            AbstractBroodMother.this.setAnimationState(SHOOT);
            AbstractBroodMother.this.playSound(ModSounds.SPIDER_CALL.get(), AbstractBroodMother.this.getSoundVolume(), AbstractBroodMother.this.getVoicePitch());
            AbstractBroodMother.this.setAttackType(WEB_ATTACK);
        }

        public void tick() {
            AbstractBroodMother.this.getNavigation().stop();
            AbstractBroodMother.this.getMoveControl().strafe(0.0F, 0.0F);
            if (AbstractBroodMother.this.getTarget() != null) {
                AbstractBroodMother.this.getLookControl().setLookAt(AbstractBroodMother.this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            AbstractBroodMother.this.setAnimationState(0);
            AbstractBroodMother.this.setAttackTicks(0);
            AbstractBroodMother.this.setAttackType(0);
            AbstractBroodMother.this.setWebCooldown(200);
        }
    }

    public class ChargeGoal extends BroodMotherGoal {

        public boolean canUse() {
            return AbstractBroodMother.this.getTarget() != null
                    && AbstractBroodMother.this.getChargeCooldown() <= 0
                    && !AbstractBroodMother.this.isStaying()
                    && AbstractBroodMother.this.hasLineOfSight(AbstractBroodMother.this.getTarget())
                    && AbstractBroodMother.this.getTarget().hasEffect(GoetyEffects.TANGLED.get());
        }

        public boolean canContinueToUse() {
            if (AbstractBroodMother.this.getAttackTicks() <= 25 || (!AbstractBroodMother.this.onGround() && AbstractBroodMother.this.getAttackTicks() <= 30)) {
                return AbstractBroodMother.this.getAttackType() == CHARGE_ATTACK;
            }
            return false;
        }

        public void start() {
            AbstractBroodMother.this.setAnimationState(CHARGE);
            AbstractBroodMother.this.setAttackType(CHARGE_ATTACK);
        }

        public void tick() {
            AbstractBroodMother.this.getNavigation().stop();
            AbstractBroodMother.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        public void stop() {
            AbstractBroodMother.this.setAnimationState(0);
            AbstractBroodMother.this.setAttackTicks(0);
            AbstractBroodMother.this.setAttackType(0);
            AbstractBroodMother.this.setChargeCooldown(100);
        }
    }

    public class BackOffGoal extends BroodMotherGoal {

        public boolean canUse() {
            return AbstractBroodMother.this.getTarget() != null
                    && AbstractBroodMother.this.getRandom().nextInt(16) == 0
                    && AbstractBroodMother.this.getBackOffCooldown() <= 0
                    && !AbstractBroodMother.this.isStaying()
                    && AbstractBroodMother.this.getTarget().distanceTo(AbstractBroodMother.this) <= 10.0F;
        }

        public boolean canContinueToUse() {
            if (AbstractBroodMother.this.getAttackTicks() <= 25 || (!AbstractBroodMother.this.onGround() && AbstractBroodMother.this.getAttackTicks() <= 30)) {
                return AbstractBroodMother.this.getAttackType() == BACK_OFF_ATTACK;
            }
            return false;
        }

        public void start() {
            AbstractBroodMother.this.setAnimationState(BACK_OFF);
            AbstractBroodMother.this.setAttackType(BACK_OFF_ATTACK);
        }

        public void tick() {
            AbstractBroodMother.this.getNavigation().stop();
            AbstractBroodMother.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        public void stop() {
            AbstractBroodMother.this.setAnimationState(0);
            AbstractBroodMother.this.setAttackTicks(0);
            AbstractBroodMother.this.setAttackType(0);
            AbstractBroodMother.this.setBackOffCooldown(100);
        }
    }

}
