package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gnasher extends AnimalSummon implements PlayerRideable, IAutoRideable {
    private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(Gnasher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(Gnasher.class, EntityDataSerializers.BOOLEAN);
    protected float prevFinAnimation;
    protected float finAnimation;
    protected float finSpeed;

    public Gnasher(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new GnasherMoveController(this);
        this.lookControl = new GnasherLookController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4F, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterWanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(2, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(5, new NaturalAttackGoal<>(this, AbstractFish.class, false));
        this.targetSelector.addGoal(6, new NaturalAttackGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GnasherHealth.get())
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D)
                .add(Attributes.ARMOR, AttributesConfig.GnasherArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GnasherDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GnasherHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GnasherArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GnasherDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOVING, false);
        this.entityData.define(AUTO_MODE, false);
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean canDrownInFluidType(FluidType type){
        return false;
    }

    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader p_32829_) {
        return p_32829_.isUnobstructed(this);
    }

    protected PathNavigation createNavigation(Level world) {
        return new WaterBoundPathNavigation(this, world);
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.dimensions.height * 0.5D;
    }

    protected float getStandingEyeHeight(Pose p_28352_, EntityDimensions p_28353_) {
        return 0.3F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            ServerParticleUtil.addParticlesAroundMiddleSelf(pLevel.getLevel(), ParticleTypes.LARGE_SMOKE, this);
            ColorUtil color = new ColorUtil(0);
            ServerParticleUtil.windParticle(pLevel.getLevel(), color, 1.0F, 0.0F, this.getId(), this.position());
        }
        return pSpawnData;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.SALMON_FLOP;
    }

    protected void handleAirSupply(int p_30344_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_30344_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }

    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(i);
    }

    //Based on @TeamAbnormal's tail animation codes:https://github.com/team-abnormals/upgrade-aquatic/blob/1.20.x/src/main/java/com/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher.java
    public void aiStep() {
        if (this.isAlive()) {
            if (this.level.isClientSide) {
                this.prevFinAnimation = this.finAnimation;

                if (!this.isInWater()) {
                    this.finSpeed = 0.875F;
                } else if (this.isMoving()) {
                    if (this.finSpeed < 0.5F) {
                        this.finSpeed = 0.875F;
                    } else {
                        this.finSpeed += (0.045F - this.finSpeed) * 0.05F;
                    }
                } else {
                    this.finSpeed += (0.01125F - this.finSpeed) * 0.05F;
                }
                this.finAnimation += this.finSpeed;
            }
            if (!this.isInWater() && this.onGround() && this.verticalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
                this.setOnGround(false);
                this.hasImpulse = true;
                this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
            }
        }

        super.aiStep();
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

    public boolean isMoving() {
        return this.entityData.get(MOVING);
    }

    private void setMoving(boolean moving) {
        this.entityData.set(MOVING, moving);
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putBoolean("IsMoving", this.isMoving());
        p_33353_.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        if (p_33344_.contains("IsMoving")) {
            this.setMoving(p_33344_.getBoolean("IsMoving"));
        }
        if (p_33344_.contains("AutoMode")) {
            this.setAutonomous(p_33344_.getBoolean("AutoMode"));
        }
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
            } else if (entity instanceof LivingEntity
                    && !this.isAutonomous()) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = this.getRiddenRotation(player);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;
        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }
        Vec3 vector3d = player.getLookAngle();
        double y = vector3d.y;
        if (!this.isInWater()) {
            y = 0.0D;
        }
        return new Vec3(f, y, f1);

    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    protected float getRiddenSpeed(@NotNull Player player) {
        float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.2F;
        if (this.isInWater()) {
            speed *= 1.0F;
        } else {
            speed *= 0.2F;
        }
        return speed;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.discard();
    }

    public float getFinAnimation(float ptc) {
        return Mth.lerp(ptc, this.prevFinAnimation, this.finAnimation);
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (itemstack.is(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
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
                        return InteractionResult.CONSUME;
                    }
                } else if (!pPlayer.isCrouching() && !this.isBaby()) {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.playSound(SoundEvents.PHANTOM_BITE, 1.0F, 0.75F);
            if (this.isUpgraded()){
                if (entityIn instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), MathHelper.secondsToTicks(5), 0), this);
                }
            }
        }
        return flag;
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance knockback = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (health != null && attack != null && armor != null && knockback != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.GnasherHealth.get() * 1.5D);
                armor.setBaseValue(AttributesConfig.GnasherArmor.get() + 2.0D);
                attack.setBaseValue(AttributesConfig.GnasherDamage.get() + 1.0D);
                knockback.setBaseValue(1.0D);
            } else {
                health.setBaseValue(AttributesConfig.GnasherHealth.get());
                armor.setBaseValue(AttributesConfig.GnasherArmor.get());
                attack.setBaseValue(AttributesConfig.GnasherDamage.get());
                knockback.setBaseValue(0.0D);
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    //Based on @AlexModGuy's codes:https://github.com/AlexModGuy/AlexsMobs/blob/1.20/src/main/java/com/github/alexthe666/alexsmobs/entity/ai/AquaticMoveController.java
    static class GnasherMoveController extends MoveControl {
        public Gnasher gnasher;

        public GnasherMoveController(Gnasher entity) {
            super(entity);
            this.gnasher = entity;
        }

        public void tick() {
            if (this.mob.isInWater() && !(this.gnasher.isFollowing() && this.gnasher.getTrueOwner() != null) && !this.gnasher.isStaying() && !this.gnasher.hasControllingPassenger()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                if (d3 < (double)1.0E-5F) {
                    this.mob.setSpeed(0.0F);
                } else {
                    d1 /= d3;
                    float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
                    this.mob.yBodyRot = this.mob.getYRot();
                    float f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) * 0.8F;
                    this.mob.setSpeed(Mth.lerp(0.125F, this.mob.getSpeed(), f1));
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, (double)this.mob.getSpeed() * d1 * 0.1D, 0.0D));
                }
                this.gnasher.setMoving(true);
            } else {
                this.mob.setSpeed(0.0F);
                this.gnasher.setMoving(false);
            }
        }
    }

    //Based on @TeamAbnormal's codes:https://github.com/team-abnormals/upgrade-aquatic/blob/1.20.x/src/main/java/com/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher.java#L676
    public static class GnasherLookController extends LookControl {

        public GnasherLookController(Gnasher gnasher) {
            super(gnasher);
        }

        public void tick() {
            if (this.lookAtCooldown > 0) {
                this.lookAtCooldown--;
                this.getYRotD().ifPresent(yRot -> this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, yRot, this.yMaxRotSpeed));
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), this.getXRotD().get(), this.xMaxRotAngle));
            } else {
                if (this.mob.getNavigation().isDone()) {
                    this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 5.0F));
                }
                this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
            }

            float wrappedDegrees = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
            float angleLimit = this.mob.getPassengers().isEmpty() ? 10.0F : 5.0F;
            if (wrappedDegrees < -angleLimit) {
                this.mob.yBodyRot -= 4.0F;
            } else if (wrappedDegrees > angleLimit) {
                this.mob.yBodyRot += 4.0F;
            }
        }
    }
}
