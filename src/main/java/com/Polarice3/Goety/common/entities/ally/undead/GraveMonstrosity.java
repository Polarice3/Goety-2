package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.api.entities.IAnimated;
import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.IMonstrosity;
import com.Polarice3.Goety.api.entities.IPoise;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.NecroVFXPalette;
import com.Polarice3.Goety.client.particles.SoulSummonVFX;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.path.LargeMobNavigation;
import com.Polarice3.Goety.common.entities.ai.path.MonstrosityBodyRotControl;
import com.Polarice3.Goety.common.entities.ai.servant.ServantWanderGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Ripple;
import com.Polarice3.Goety.common.entities.projectiles.SoulBomb;
import com.Polarice3.Goety.common.entities.projectiles.SoulStar;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.entities.util.ChargeAura;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.block.GraveMonstrosityHeadItem;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class GraveMonstrosity extends AbstractGolemServant implements IAnimated, IPoise, IAutoRideable, IMonstrosity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(GraveMonstrosity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(GraveMonstrosity.class, EntityDataSerializers.BOOLEAN);
    public final ChargeAura chargeAura = new ChargeAura(ChargeAura.Settings.NECRO.scaled(1.2F));
    public static ColorUtil AURA_COLOR = new ColorUtil(NecroVFXPalette.DARK_HEX);
    public static String ACTIVATE = "activate";
    public static String IDLE = "idle";
    public static String SUMMON = "summon";
    public static String ATTACK_COMBO = "attack_combo";
    public static String ATTACK_COMBO_END = "attack_combo_end";
    public static String ATTACK_SWIPE = "attack_swipe";
    public static String CHARGE = "charge";
    public static String CHARGE_LOOP = "charge_loop";
    public static String CHARGE_PUNCH = "charge_punch";
    public static String CHARGE_END = "charge_end";
    public static String PROJECTILE = "projectile";
    public static String CALL = "call";
    public static String STAGGERED = "staggered";
    public static String DEATH = "death";
    public static float MCD2_COMBO_DAMAGE = 12.5F;
    public static float MCD2_SWIPE_DAMAGE = 20.0F;
    public static float MCD2_CHARGE_DAMAGE = 25.0F;
    public static float MCD2_CHARGE_END_DAMAGE = 35.0F;
    public static float MCD2_PROJ_DAMAGE = 20.0F; //From Redstone Monstrosity
    public static float MCD2_CALL_DAMAGE = 8.0F; //From other mob I can't remember lol
    public static int SUMMON_COOLDOWN = MathHelper.secondsToTicks(20);
    public static int CHARGE_COOLDOWN = MathHelper.secondsToTicks(6);
    public static int PROJECTILE_COOLDOWN = MathHelper.secondsToTicks(5);
    public static int CALL_COOLDOWN = MathHelper.secondsToTicks(17);
    private int cubeSummonVFXTicks;
    public int activateTick;
    public int attackTick;
    public int summonCount;
    public int summonMax;
    public int starCount;
    public Map<Integer, Vec3> belchPositions = new HashMap<>();
    public int chargeCool;
    public int summonCool;
    public int projectileCool;
    public int callCool;
    public int poise = 0;
    public int poiseCool = 0;
    public float getGlow;
    public float deathRotation = 0.0F;
    public int deathTime = 0;
    public boolean clientStop;
    private boolean chubu = false;
    public final SimpleContainer inventory = new SimpleContainer(100);
    public boolean hasInventory;
    public final AnimationState activateAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState attackSwipeAnimationState = new AnimationState();
    public final AnimationState attackComboAnimationState = new AnimationState();
    public final AnimationState attackComboEndAnimationState = new AnimationState();
    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState chargeLoopAnimationState = new AnimationState();
    public final AnimationState chargePunchAnimationState = new AnimationState();
    public final AnimationState chargeEndAnimationState = new AnimationState();
    public final AnimationState projectileAnimationState = new AnimationState();
    public final AnimationState callAnimationState = new AnimationState();
    public final AnimationState staggeredAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    public GraveMonstrosity(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.moveControl = new MobUtil.noSpinControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(9, new ServantWanderGoal<>(this, 1.0F));
        this.attackGoals();
    }

    public void attackGoals() {
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, IDLE, STAGGERED, IDLE, 78, 0, 0.0F, 0.0F){
            @Override
            public boolean canUse() {
                return this.entity.shouldStagger() && this.entity.getCurrentAnimation() == this.currentAttackType;
            }

            @Override
            public void start() {
                super.start();
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_STAGGER_START.get());
            }

            @Override
            public void stop() {
                this.entity.poise = 0;
                this.entity.poiseCool = 0;
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new MonstrosityAttackGoal(this, IDLE, SUMMON, IDLE, 86, 10, 0.0F, 40.0F){
            @Override
            public boolean canUse() {
                int i = this.entity.level.getEntitiesOfClass(Summoned.class, this.entity.getBoundingBox().inflate(32), summoned -> summoned.getTrueOwner() == this.entity || (this.entity.getTrueOwner() != null && summoned.getTrueOwner() == this.entity.getTrueOwner())).size();
                return super.canUse() && this.entity.summonCool <= 0 && i < 15;
            }

            @Override
            public void start() {
                super.start();
                this.entity.summonCount = 0;
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_SUMMON_START.get());
                this.entity.summonMax = this.entity.getRandom().nextIntBetweenInclusive(5, 7);
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.summonCount = 0;
                this.entity.summonCool = SUMMON_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(2, new MonstrosityAttackGoal(this, IDLE, ATTACK_COMBO, ATTACK_COMBO_END, 40, 40, 0.0F, 6.0F){
            @Override
            public boolean canUse() {
                if (this.entity.getTarget() != null) {
                    LivingEntity livingEntity = this.entity.getTarget();
                    if (livingEntity.getY() > this.entity.getY() + (2.5F * 1.2F)) {
                        return false;
                    }
                }
                return super.canUse();
            }

            @Override
            public void start() {
                super.start();
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_ATTACK_COMBO.get());
            }
        });
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, ATTACK_COMBO_END, ATTACK_COMBO_END, ATTACK_SWIPE, 22, 22, 0.0F, 0.0F){
            public boolean swipe = false;

            @Override
            public boolean canUse() {
                return this.entity.getCurrentAnimation() == this.currentAttackType;
            }

            @Override
            public boolean canContinueToUse() {
                int maxTick = this.attackMaxTick;
                LivingEntity target = entity.getTarget();
                if (target != null && target.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && this.entity.distanceTo(target) <= 10) {
                    maxTick = 10;
                    this.swipe = true;
                } else {
                    this.swipe = false;
                }
                return this.entity.getCurrentAnimation() == this.attackType && this.entity.attackTick <= maxTick;
            }

            @Override
            public void stop() {
                LivingEntity target = entity.getTarget();
                if (!this.swipe) {
                    this.entity.setAnimationState(IDLE);
                    if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                        this.entity.setTarget(null);
                    }
                    this.entity.getNavigation().stop();
                    if (this.entity.getTarget() == null) {
                        this.entity.setAggressive(false);
                    }
                } else {
                    super.stop();
                }
            }
        });
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, ATTACK_SWIPE, ATTACK_SWIPE, IDLE, 50, 17, 0.0F, 0.0F){
            @Override
            public boolean canUse() {
                return this.entity.getCurrentAnimation() == this.currentAttackType;
            }
        });
        this.chargeGoals();
        this.goalSelector.addGoal(4, new MonstrosityAttackGoal(this, IDLE, PROJECTILE, IDLE, 50, 17, 4.0F, 15.0F){
            @Override
            public boolean canUse() {
                if (this.entity.getTarget() != null) {
                    LivingEntity livingEntity = this.entity.getTarget();
                    if (livingEntity.getY() > this.entity.getY() + (2.5F * 1.2F)) {
                        return false;
                    }
                }
                return super.canUse() && this.entity.projectileCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_PRE_BELCH.get());
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.belchPositions.clear();
                this.entity.projectileCool = PROJECTILE_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(5, new MonstrosityAttackGoal(this, IDLE, CALL, IDLE, 77, 40, 0.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && this.entity.callCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                this.entity.starCount = 0;
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_CALL.get());
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.starCount = 0;
                this.entity.callCool = CALL_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(8, new ChaseGoal(this));
    }

    public void chargeGoals() {
        this.goalSelector.addGoal(3, new MonstrosityAttackGoal(this, IDLE, CHARGE, CHARGE_LOOP, 24, 10, 4.5F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && this.entity.chargeCool <= 0 && !this.entity.isStaying();
            }

            @Override
            public void start() {
                super.start();
                this.entity.chubu = false;
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_CHARGE_START.get());
            }
        });
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, CHARGE_LOOP, CHARGE_LOOP, CHARGE_PUNCH, 14, 0, 0.0F, 0.0F){
            public boolean punch = false;

            @Override
            public boolean canUse() {
                return this.entity.getCurrentAnimation() == this.currentAttackType;
            }

            @Override
            public boolean canContinueToUse() {
                LivingEntity target = entity.getTarget();
                this.punch = target != null && target.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && this.entity.distanceTo(target) <= 6.0F;
                if (this.entity.chubu) {
                    return false;
                }
                return super.canContinueToUse();
            }

            @Override
            public void stop() {
                LivingEntity target = entity.getTarget();
                if (!this.punch) {
                    this.entity.setAnimationState(CHARGE_END);
                    if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                        this.entity.setTarget(null);
                    }
                    this.entity.getNavigation().stop();
                    if (this.entity.getTarget() == null) {
                        this.entity.setAggressive(false);
                    }
                } else {
                    super.stop();
                }
            }

            @Override
            public void tick() {
                if (this.entity.onGround()){
                    Vec3 vector3d = this.entity.getDeltaMovement();
                    float f = this.entity.getYRot() * ((float)Math.PI / 180F);
                    Vec3 vector3d1 = new Vec3(-Mth.sin(f), this.entity.getDeltaMovement().y, Mth.cos(f)).scale(1.0D).add(vector3d.scale(0.5D));
                    this.entity.setDeltaMovement(vector3d1.x, this.entity.getDeltaMovement().y, vector3d1.z);
                }
            }
        });
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, CHARGE_PUNCH, CHARGE_PUNCH, IDLE, 87, 29, 0.0F, 0.0F){
            @Override
            public boolean canUse() {
                return this.entity.getCurrentAnimation() == this.currentAttackType;
            }

            @Override
            public void start() {
                super.start();
                this.entity.playSound(ModSounds.GRAVE_MONSTROSITY_CHARGE_END.get());
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.chargeCool = CHARGE_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(0, new MonstrosityAttackGoal(this, CHARGE_END, CHARGE_END, IDLE, 4, 0, 0.0F, 0.0F){
            @Override
            public boolean canUse() {
                return this.entity.getCurrentAnimation() == this.currentAttackType;
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.chargeCool = CHARGE_COOLDOWN;
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GraveMonstrosityHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.GraveMonstrosityArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 6.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GraveMonstrosityDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GraveMonstrosityFollowRange.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GraveMonstrosityHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GraveMonstrosityArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GraveMonstrosityDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.GraveMonstrosityFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(AUTO_MODE, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActivateTick", this.activateTick);
        pCompound.putInt("SummonCount", this.summonCount);
        pCompound.putInt("StarCount", this.starCount);
        pCompound.putInt("ChargeCool", this.chargeCool);
        pCompound.putInt("SummonCool", this.summonCool);
        pCompound.putInt("ProjectileCool", this.projectileCool);
        pCompound.putInt("CallCool", this.callCool);
        this.savePoiseData(pCompound);

        ListTag listnbt = new ListTag();

        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                listnbt.add(itemstack.save(new CompoundTag()));
            }
        }

        pCompound.put("Inventory", listnbt);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActivateTick")) {
            this.activateTick = pCompound.getInt("ActivateTick");
        }
        if (pCompound.contains("SummonCount")) {
            this.summonCount = pCompound.getInt("SummonCount");
        }
        if (pCompound.contains("StarCount")) {
            this.starCount = pCompound.getInt("StarCount");
        }
        if (pCompound.contains("ChargeCool")) {
            this.chargeCool = pCompound.getInt("ChargeCool");
        }
        if (pCompound.contains("SummonCool")) {
            this.summonCool = pCompound.getInt("SummonCool");
        }
        if (pCompound.contains("ProjectileCool")) {
            this.projectileCool = pCompound.getInt("ProjectileCool");
        }
        if (pCompound.contains("CallCool")) {
            this.callCool = pCompound.getInt("CallCool");
        }
        this.readPoiseData(pCompound);
        if (pCompound.contains("Inventory")) {
            ListTag listnbt = pCompound.getList("Inventory", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
                if (!itemstack.isEmpty()) {
                    this.inventory.addItem(itemstack);
                }
            }
        }
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof IMonstrosity;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.MonstrosityPlayerLimit.get();
    }

    public SlotAccess getSlot(int p_149743_) {
        int i = p_149743_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149743_);
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

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle()
                    && rider instanceof Player player
                    && !this.clientStopMoving()
                    && !this.isAutonomous()) {
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

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                if (this.clientStopMoving() && !this.isCharging()){
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
            return !this.isCurrentAnimation(IDLE);
        }
    }

    public boolean isCharging() {
        return this.isCurrentAnimation(CHARGE) || this.isCurrentAnimation(CHARGE_LOOP) || this.isCurrentAnimation(CHARGE_PUNCH) || this.isCurrentAnimation(CHARGE_END);
    }

    protected float nextStep() {
        return this.moveDist + 1.0F;
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new MonstrosityBodyRotControl(this);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new LargeMobNavigation(this, level);
    }

    @Override
    public void playAmbientSound() {
        if (!this.isActivating()) {
            super.playAmbientSound();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GRAVE_MONSTROSITY_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.GRAVE_MONSTROSITY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GRAVE_MONSTROSITY_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.GRAVE_MONSTROSITY_STEP.get(), 2.0F, 1.0F);
        CameraShake.cameraShake(this.level, this.position(), 20.0F, 0.03F, 0, 20);
    }

    @Override
    protected float getSoundVolume() {
        return 3.0F;
    }

    @Override
    public void playSound(SoundEvent p_216991_) {
        if (!this.isSilent()) {
            this.playSound(p_216991_, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED || pReason == MobSpawnType.COMMAND){
            this.setPose(Pose.EMERGING);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isActivating();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return !this.isActivating() && super.hasLineOfSight(p_149755_);
    }

    public int getMaxPoise() {
        return 1200;
    }

    public int getPoiseRecover() {
        return 10;
    }

    public int getPoise() {
        return this.poise;
    }

    public void setPoise(int poise) {
        this.poise = poise;
    }

    public int getPoiseCoolLength() {
        return MathHelper.secondsToTicks(5);
    }

    public int getPoiseCool() {
        return this.poiseCool;
    }

    public void setPoiseCool(int cool) {
        this.poiseCool = cool;
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow - 0.05F, 0, 1);
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
        if (this.canHurtRange(pSource) > AttributesConfig.GraveMonstrosityHurtRange.get() && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        boolean flag = super.hurt(pSource, pAmount);
        if (flag && !this.shouldStagger()) {
            this.addPoise(pSource, pAmount, AttributesConfig.GraveMonstrosityDamageCap.get().floatValue());
        }
        return flag;
    }

    protected void actuallyHurt(DamageSource source, float amount) {
        MobUtil.bossHurt(this, source, amount, AttributesConfig.GraveMonstrosityDamageCap.get().floatValue());
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }

        if (damageSource.is(ModTags.DamageTypes.SOUL)) {
            damage *= 0.65F;
        }

        if (damageSource.is(DamageTypeTags.IS_FIRE) || damageSource.is(ModTags.DamageTypes.FIRE_ATTACKS)) {
            damage *= 1.5F;
        }

        return damage;
    }

    public int getDeathTime(){
        return this.deathTime;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(10)) {
            this.spawnAnim();
            if (this.getTrueOwner() != null){
                ItemStack itemStack = new ItemStack(ModBlocks.GRAVE_MONSTROSITY_HEAD_BLOCK.get());
                GraveMonstrosityHeadItem.setOwner(this.getTrueOwner(), itemStack);
                if (this.getCustomName() != null){
                    GraveMonstrosityHeadItem.setCustomName(this.getCustomName().getString(), itemStack);
                }
                ItemEntity itemEntity = this.spawnAtLocation(itemStack);
                if (itemEntity != null){
                    itemEntity.setExtendedLifetime();
                }
            }
            this.dropInventory();
            this.remove(RemovalReason.KILLED);
        }
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
    }

    public void addDrops(Collection<ItemEntity> items) {
        List<ItemStack> drops = items.stream()
                .filter(Objects::nonNull)
                .map(ItemEntity::getItem)
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();
        for (ItemStack itemStack : drops){
            if (this.inventory.canAddItem(itemStack)) {
                this.inventory.addItem(itemStack);
            } else {
                ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack);
                itemEntity.setDefaultPickUpDelay();
                float f = this.random.nextFloat() * 0.5F;
                float f1 = this.random.nextFloat() * ((float)Math.PI * 2F);
                itemEntity.setDeltaMovement((double)(-Mth.sin(f1) * f), (double)0.2F, (double)(Mth.cos(f1) * f));
                this.level.addFreshEntity(itemEntity);
            }
        }
    }

    public void dropInventory(){
        this.dropInventory(this.getX(), this.getY(), this.getZ());
    }

    public void dropInventory(BlockPos blockPos){
        this.dropInventory(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public void dropInventory(double x, double y, double z){
        if (!this.inventory.isEmpty()){
            for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (itemstack != ItemStack.EMPTY){
                    ItemEntity itemEntity = new ItemEntity(this.level, x, y, z, itemstack);
                    itemEntity.setDefaultPickUpDelay();
                    if (this.level.addFreshEntity(itemEntity)){
                        this.inventory.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance instance) {
        if (instance.getEffect() == GoetyEffects.SOUL_BURN.get()) {
            return false;
        }
        return super.canBeAffected(instance);
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

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, ACTIVATE)){
            return 1;
        } else if (Objects.equals(animation, SUMMON)){
            return 2;
        } else if (Objects.equals(animation, ATTACK_COMBO)){
            return 3;
        } else if (Objects.equals(animation, ATTACK_COMBO_END)){
            return 4;
        } else if (Objects.equals(animation, ATTACK_SWIPE)){
            return 5;
        } else if (Objects.equals(animation, CHARGE)){
            return 6;
        } else if (Objects.equals(animation, CHARGE_LOOP)){
            return 7;
        } else if (Objects.equals(animation, CHARGE_PUNCH)){
            return 8;
        } else if (Objects.equals(animation, CHARGE_END)){
            return 9;
        } else if (Objects.equals(animation, PROJECTILE)){
            return 10;
        } else if (Objects.equals(animation, CALL)){
            return 11;
        } else if (Objects.equals(animation, STAGGERED)){
            return 12;
        } else if (Objects.equals(animation, DEATH)){
            return 13;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.activateAnimationState);
        list.add(this.idleAnimationState);
        list.add(this.summonAnimationState);
        list.add(this.attackComboAnimationState);
        list.add(this.attackComboEndAnimationState);
        list.add(this.attackSwipeAnimationState);
        list.add(this.chargeAnimationState);
        list.add(this.chargeLoopAnimationState);
        list.add(this.chargePunchAnimationState);
        list.add(this.chargeEndAnimationState);
        list.add(this.projectileAnimationState);
        list.add(this.callAnimationState);
        list.add(this.staggeredAnimationState);
        list.add(this.deathAnimationState);
        return list;
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public boolean canAnimateMove() {
        return this.isCurrentAnimation(IDLE);
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

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.activateAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.activateAnimationState);
                        break;
                    case 2:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 3:
                        this.attackComboAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackComboAnimationState);
                        break;
                    case 4:
                        this.attackComboEndAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackComboEndAnimationState);
                        break;
                    case 5:
                        this.attackSwipeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackSwipeAnimationState);
                        break;
                    case 6:
                        this.chargeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chargeAnimationState);
                        break;
                    case 7:
                        this.chargeLoopAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chargeLoopAnimationState);
                        break;
                    case 8:
                        this.chargePunchAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chargePunchAnimationState);
                        break;
                    case 9:
                        this.chargeEndAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.chargeEndAnimationState);
                        break;
                    case 10:
                        this.projectileAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.projectileAnimationState);
                        break;
                    case 11:
                        this.callAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.callAnimationState);
                        break;
                    case 12:
                        this.staggeredAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.staggeredAnimationState);
                        break;
                    case 13:
                        this.deathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    public boolean isAttacking() {
        return !this.isCurrentAnimation(IDLE) && !this.isActivating() && !this.isDeadOrDying();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDeadOrDying()){
            this.setAnimationState(DEATH);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)){
            ++this.activateTick;
            this.setAnimationState(ACTIVATE);
            if (this.activateTick == 1){
                this.playSound(ModSounds.MONSTROSITY_SPAWN.get(), 10.0F, 1.0F);
            }
            if (this.activateTick > 20) {
                if (this.level.isClientSide()) {
                    this.getGlow = 1.0F;
                }
            }
            if (this.activateTick == 46){
                this.playSound(ModSounds.GRAVE_MONSTROSITY_AWAKEN.get(), 10.0F, 1.0F);
                CameraShake.cameraShake(this.level, this.position(), 40.0F, 0.5F, 0, 20);
            }
            if (this.activateTick >= MathHelper.secondsToTicks(4.6F)){
                this.setPose(Pose.STANDING);
            }
        } else {
            if (this.isCurrentAnimation(ACTIVATE)) {
                this.setAnimationState(IDLE);
            }
        }
        if (this.level.isClientSide) {
            this.idleAnimationState.animateWhen(this.isCurrentAnimation(IDLE) && !this.walkAnimation.isMoving(), this.tickCount);
            this.chargeAura.tick(this, this.isCurrentAnimation(CHARGE_LOOP) && this.isAlive());
            this.glow();
            if (this.cubeSummonVFXTicks > 0) {
                int t = SoulSummonVFX.STREAM_TICKS - this.cubeSummonVFXTicks;
                if (t == 1) {
                    SoulSummonVFX.flash(this.level, this.position(), true);
                }
                if (t % 2 == 0) {
                    SoulSummonVFX.soul(this.level, this.position(), this.yBodyRot, 1.5D, true);
                }
                --this.cubeSummonVFXTicks;
            }
        }
        if (!this.level.isClientSide){
            if (this.isAlive() && !this.isActivating()) {
                if (!this.isCurrentAnimation(STAGGERED)) {
                    this.poiseTick();
                }
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32.0D), undead -> undead.getMobType() == MobType.UNDEAD || undead instanceof Player)) {
                    MiscCapHelper.setSunscreen(livingEntity, 20);
                }
                if (MobsConfig.MonstrosityLeafBreak.get()) {
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
                if (this.isAttacking()) {
                    ++this.attackTick;
                    this.level.broadcastEntityEvent(this, (byte) 6);
                } else {
                    this.attackTick = 0;
                    this.level.broadcastEntityEvent(this, (byte) 7);
                }
                if (this.summonCool > 0) {
                    --this.summonCool;
                }
                if (this.chargeCool > 0) {
                    --this.chargeCool;
                }
                if (this.projectileCool > 0) {
                    --this.projectileCool;
                }
                if (this.callCool > 0) {
                    --this.callCool;
                }
                this.attackAI();
            }
        }
    }

    public void attackAI() {
        if (this.level instanceof ServerLevel serverLevel) {
            //MATHING
            float comboDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float swipeDamage = comboDamage * 1.5F;
            float chargeDamage = comboDamage * 2.0F;
            float chargeEndDamage = comboDamage * 3.0F;
            float projDamage = comboDamage * 1.2F;
            float callDamage = comboDamage * 0.5F;

            float hpMultiply = AttributesConfig.GraveMonstrosityHPPercentDamage.get().floatValue();
            if (this.isCurrentAnimation(STAGGERED)) {
                this.getNavigation().stop();
                this.getMoveControl().strafe(0, 0);
                if (this.attackTick == 52) {
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_STAGGER_END.get());
                }
            }
            if (this.isCurrentAnimation(SUMMON)) {
                if (this.attackTick == 27) {
                    this.serverGlow();
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_SUMMON.get());
                    CameraShake.cameraShake(this.level, this.position(), 25.0F, 0.3F, 0, 20);
                    this.level.broadcastEntityEvent(this, (byte) 102);
                }
                if (this.attackTick >= 27) {
                    if (this.attackTick % 4 == 0 && this.summonCount < this.summonMax) {
                        GraveCube summoned = new GraveCube(ModEntityType.GRAVE_CUBE.get(), this.level);
                        BlockPos blockPos = this.blockPosition();
                        for (int i = 0; i < 16; ++i) {
                            blockPos = blockPos.offset(-16 + this.level.getRandom().nextInt(32), 0, -16 + this.level.random.nextInt(32));
                            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                            if (this.level.noCollision(summoned, summoned.getBoundingBox().move(vec3)) && BlockFinder.canSeeBlock(this, vec3)){
                                summoned.setTrueOwner(this);
                                summoned.moveTo(vec3);
                                MobUtil.moveDownToGround(summoned);
                                summoned.setPersistenceRequired();
                                summoned.setPlaySummonParticles(false);
                                summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                                summoned.setTarget(this.getTarget());
                                if (this.level.addFreshEntity(summoned)) {
                                    ++this.summonCount;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (this.attackTick == 55) {
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_SUMMON_END.get());
                }
            }
            if (this.isCurrentAnimation(ATTACK_COMBO)) {
                if (this.attackTick == 14 || this.attackTick == 35) {
                    this.serverGlow();
                    MobUtil.mcd2Dash(this, 50.0F);
                }
                if (this.attackTick == 18 || this.attackTick == 37) {
                    MobUtil.meleeConeAttack(this, 6.0F * 1.2F, 120.0F - 30.0F, 2.5F * 1.2F, comboDamage, hpMultiply, 100, this.getServantAttack(), 1000.0F, 20.0F, this::lessInvul);
                }
                if (this.attackTick == 29) {
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_ATTACK_COMBO_END.get());
                }
            }
            if (this.isCurrentAnimation(ATTACK_SWIPE)) {
                if (this.attackTick == 2) {
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_ATTACK_SWIPE.get());
                }
                if (this.attackTick == 22) {
                    this.serverGlow();
                    MobUtil.mcd2Dash(this, 50.0F);
                }
                if (this.attackTick == 23) {
                    MobUtil.meleeConeAttack(this, 8.0F * 1.2F, 60.0F, 2.5F * 1.2F, swipeDamage, hpMultiply, 100, this.getServantAttack(), 1600.0F, 30.0F, this::lessInvul);
                }
            }
            if (this.isCurrentAnimation(CHARGE_LOOP)) {
                if (this.horizontalCollision) {
                    this.chubu = true;
                }
                this.serverGlow();
                DamageSource damageSource = this.getServantAttack();
                for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.5F), target -> target != this && target.isAlive() && target.isAttackable() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target))) {
                    if (!MobUtil.areAllies(this, target) || target instanceof GraveCube) {
                        boolean blocked = target.isDamageSourceBlocked(damageSource);
                        if (target instanceof GraveCube) {
                            damageSource = this.damageSources().generic();
                        }
                        if (target.hurt(damageSource, chargeDamage + (target.getMaxHealth() * hpMultiply))) {
                            MobUtil.mcd2Knockback(this, target, 4000.0F, 20.0F);
                            if (target.isAlive()) {
                                this.chargeStun(target);
                            }
                        }
                        if (blocked) {
                            MobUtil.disableShield(target, 100);
                        }
                    }
                }
            }
            if (this.isCurrentAnimation(CHARGE_PUNCH)) {
                if (this.attackTick == 45) {
                    this.serverGlow();
                    MobUtil.mcd2Dash(this, 250.0F);
                }
                if (this.attackTick == 48) {
                    MobUtil.meleeConeAttack(this, 6.0F * 1.2F, 120.0F - 30.0F, (2.0F * 1.2F) + 3.0F, chargeEndDamage, hpMultiply, 100, this.getServantAttack(), 2000.0F, 35.0F, this::applySoulBurn);
                    Vec3 front = MobUtil.getFrontPos(this, 1.0D);
                    Ripple ripple = new Ripple(this.level, this, front.x, front.y + 1.0F, front.z, Ripple.Preset.NECRO_RIPPLE);
                    ripple = ripple.setSpeed(8.0F);
                    ripple = ripple.setEndRadius((6.0F * 1.2F) + 2.0F);
                    ripple = ripple.setFireSeconds(0);
                    ripple = ripple.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(5)));
                    ripple = ripple.setDamage(chargeEndDamage * 0.5F);
                    ripple = ripple.setPercentDamage(hpMultiply);
                    this.level.addFreshEntity(ripple);
                }
            }
            if (this.isCurrentAnimation(PROJECTILE)) {
                if (this.attackTick == 1) {
                    this.serverGlow();
                }
                if (this.attackTick == 17) {
                    this.belchPositions.put(1, this.position().add(MobUtil.calculateViewVector(0, this.getYRot() - 60.0F).scale(10.0D)));
                    this.belchPositions.put(2, this.position().add(MobUtil.calculateViewVector(0, this.getYRot()).scale(10.0D)));
                    this.belchPositions.put(3, this.position().add(MobUtil.calculateViewVector(0, this.getYRot() + 60.0F).scale(10.0D)));
                    MobUtil.instaLook(this, this.belchPositions.get(1));
                }
                if (this.attackTick == 18 || this.attackTick == 22 || this.attackTick == 26) {
                    int slot = this.attackTick == 18 ? 1 : this.attackTick == 22 ? 2 : 3;
                    Vec3 vec3 = this.belchPositions.getOrDefault(slot, this.position().add(this.getLookAngle().scale(10.0D)));
                    MobUtil.instaLook(this, vec3);
                    this.serverGlow();
                    SoulBomb soulBomb = this.getSoulBomb(vec3, projDamage);
                    this.level.addFreshEntity(soulBomb);
                    this.playSound(ModSounds.GRAVE_MONSTROSITY_BELCH.get());
                }
            }
            if (this.isCurrentAnimation(CALL)) {
                this.serverGlow();
                if (this.attackTick >= 16) {
                    if (this.attackTick % 4 == 0) {
                        this.serverSoul();
                    }
                }
                if (this.attackTick >= 24) {
                    if (this.attackTick % 4 == 0 && this.starCount < 10) {
                        SoulStar star = new SoulStar(this, this.getTarget(), this.level);
                        BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), star, this.level);
                        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                        star.setNecro(true);
                        star.setBaseDamage(callDamage);
                        star.setPos(vec3.subtract(0, 2, 0));
                        star.signalTo(vec3.add(0, 4, 0), 30);
                        star.life = 0;
                        if (this.level.addFreshEntity(star)) {
                            ++this.starCount;
                        }
                    }
                }
            }
        }
    }

    public void serverGlow() {
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 15);
        }
    }

    public void serverSoul() {
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 103);
        }
    }

    public void lessInvul(Entity entity) {
        entity.invulnerableTime = 10;
    }

    public void applySoulBurn(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(5), 0), this);
        }
    }

    public void chargeStun(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), 14, 0, false, false), this);
        }
    }

    @NotNull
    private SoulBomb getSoulBomb(Vec3 vec3, float damage) {
        SoulBomb bomb = new SoulBomb(this, this.level);
        double d1 = vec3.x - this.getX();
        double d2 = vec3.y - this.getY();
        double d3 = vec3.z - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        bomb.setNecro(true);
        bomb.moveTo(this.getX(), this.getY() + 4.5D, this.getZ());
        bomb.setDamage(damage);
        float velocity = 1.4F;
        this.shoot(bomb, d1, d2, d3, velocity);
        return bomb;
    }

    public void shoot(Projectile projectile, double xPower, double yPower, double zPower, float velocity) {
        Vec3 vec3 = (new Vec3(xPower, yPower, zPower)).normalize().scale(velocity);
        projectile.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        projectile.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        projectile.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        projectile.yRotO = projectile.getYRot();
        projectile.xRotO = projectile.getXRot();
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 6){
            this.clientStop = true;
        } else if (pId == 7){
            this.clientStop = false;
        } else if (pId == 15){
            this.getGlow = 1.0F;
        } else if (pId == 19) {
            if (!this.hasInventory) {
                this.hasInventory = true;
            }
        } else if (pId == 24) {
            if (this.hasInventory) {
                this.hasInventory = false;
            }
        } else if (pId == 102) {
            this.cubeSummonVFXTicks = SoulSummonVFX.STREAM_TICKS;
            SoulSummonVFX.burst(this.level, this.position(), this.yBodyRot, true);
        } else if (pId == 103) {
            SoulSummonVFX.soul(this.level, this.position(), this.yBodyRot, 1.0D, true);
            SoulSummonVFX.spark(this.level, this.position(), this.getRandom(), true);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE_BLOCK && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.heal((this.getMaxHealth() / 4.0F) / 8.0F);
                    this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 0.5F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
                            double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
                            double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (item instanceof ShovelItem && !this.inventory.isEmpty()){
                    this.playSound(SoundEvents.SHOVEL_FLATTEN, 1.0F, 1.0F);
                    this.dropInventory(pPlayer.blockPosition());
                    return InteractionResult.SUCCESS;
                } else {
                    if (pPlayer.getMainHandItem().is(ModItems.REPAIR_PUTTY.get()) && pPlayer.getOffhandItem().is(ModTags.Items.HAMMERS)) {
                        return InteractionResult.PASS;
                    }
                    //Don't care, let them ride in the air lol
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
        return InteractionResult.PASS;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        AABB box = super.getBoundingBoxForCulling();
        return this.chargeAura.isVisible() ? box.inflate(this.chargeAura.settings.depth() + 1.0D) : box;
    }

    public static class MonstrosityAttackGoal extends Goal {
        protected final GraveMonstrosity entity;
        protected final int currentAttackType;
        protected final int attackType;
        protected final int attackEndType;
        protected final int attackMaxTick;
        protected final int attackSeeTick;
        protected final float attackOuterRange;
        protected final float attackRange;

        public MonstrosityAttackGoal(GraveMonstrosity entity, int currentAttackType, int attackType, int attackEndType, int attackMaxTick, int attackSeeTick, float attackOuterRange, float attackRange) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
            this.currentAttackType = currentAttackType;
            this.attackType = attackType;
            this.attackEndType = attackEndType;
            this.attackMaxTick = attackMaxTick;
            this.attackSeeTick = attackSeeTick;
            this.attackOuterRange = attackOuterRange;
            this.attackRange = attackRange;
        }

        public MonstrosityAttackGoal(GraveMonstrosity entity, String currentAttackType, String attackType, String attackEndType, int attackMaxTick, int attackSeeTick, float attackOuterRange, float attackRange) {
            this(entity, entity.getAnimationState(currentAttackType), entity.getAnimationState(attackType), entity.getAnimationState(attackEndType), attackMaxTick, attackSeeTick, attackOuterRange, attackRange);
        }

        public MonstrosityAttackGoal(GraveMonstrosity entity, String currentAttackType, String attackType, String attackEndType, int attackMaxTick, int attackSeeTick, float attackRange) {
            this(entity, entity.getAnimationState(currentAttackType), entity.getAnimationState(attackType), entity.getAnimationState(attackEndType), attackMaxTick, attackSeeTick, 0.0F, attackRange);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return target != null
                    && target.isAlive()
                    && (this.attackRange <= 0.0F || this.entity.distanceTo(target) < this.attackRange)
                    && (this.attackOuterRange <= 0.0F || this.entity.distanceTo(target) >= this.attackOuterRange)
                    && this.entity.getCurrentAnimation() == this.currentAttackType;
        }

        @Override
        public boolean canContinueToUse() {
            return this.entity.getCurrentAnimation() == this.attackType && this.entity.attackTick <= this.attackMaxTick;
        }

        @Override
        public void start() {
            this.entity.setAnimationState(this.attackType);
            this.entity.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.entity.setAnimationState(this.attackEndType);
            LivingEntity target = entity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                this.entity.setTarget(null);
            }
            this.entity.getNavigation().stop();
            if (this.entity.getTarget() == null) {
                this.entity.setAggressive(false);
            }
            this.entity.attackTick = 0;
        }

        public void tick() {
            LivingEntity target = this.entity.getTarget();
            if (this.entity.attackTick < this.attackSeeTick && target != null) {
                MobUtil.instaLook(this.entity, target);
            } else {
                this.entity.setYRot(this.entity.yRotO);
            }
            this.entity.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class ChaseGoal extends Goal {
        public GraveMonstrosity monstrosity;
        public Path path;
        public double pathedTargetX;
        public double pathedTargetY;
        public double pathedTargetZ;
        public int ticksUntilNextPathRecalculation;
        public int ticksUntilNextAttack;
        public long lastCanUseCheck;
        public int failedPathFindingPenalty = 0;
        public boolean canPenalize = false;

        public ChaseGoal(GraveMonstrosity monstrosity) {
            this.monstrosity = monstrosity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            long i = this.monstrosity.level().getGameTime();
            if (i - this.lastCanUseCheck < 20L) {
                return false;
            } else {
                this.lastCanUseCheck = i;
                LivingEntity livingentity = this.monstrosity.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    if (canPenalize) {
                        if (--this.ticksUntilNextPathRecalculation <= 0) {
                            this.path = this.monstrosity.getNavigation().createPath(livingentity, 0);
                            this.ticksUntilNextPathRecalculation = 4 + this.monstrosity.getRandom().nextInt(7);
                            return this.path != null;
                        } else {
                            return true;
                        }
                    }
                    this.path = this.monstrosity.getNavigation().createPath(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    } else {
                        return this.getAttackReachSqr(livingentity) >= this.monstrosity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                    }
                }
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.monstrosity.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!this.monstrosity.isWithinRestriction(livingentity.blockPosition())) {
                return false;
            } else {
                return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity);
            }
        }

        public void start() {
            this.monstrosity.getNavigation().moveTo(this.path, 1.0F);
            this.monstrosity.setAggressive(true);
            this.ticksUntilNextPathRecalculation = 0;
            this.ticksUntilNextAttack = 0;
        }

        public void stop() {
            LivingEntity livingentity = this.monstrosity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.monstrosity.setTarget(null);
            }

            this.monstrosity.setAggressive(false);
            this.monstrosity.getNavigation().stop();
        }

        public void tick() {
            LivingEntity livingentity = this.monstrosity.getTarget();
            if (livingentity != null) {
                this.monstrosity.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                double d0 = this.monstrosity.getPerceivedTargetDistanceSquareForMeleeAttack(livingentity);
                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                if ((this.monstrosity.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.monstrosity.getRandom().nextFloat() < 0.05F)) {
                    this.pathedTargetX = livingentity.getX();
                    this.pathedTargetY = livingentity.getY();
                    this.pathedTargetZ = livingentity.getZ();
                    this.ticksUntilNextPathRecalculation = 4 + this.monstrosity.getRandom().nextInt(7);
                    if (this.canPenalize) {
                        this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                        if (this.monstrosity.getNavigation().getPath() != null) {
                            net.minecraft.world.level.pathfinder.Node finalPathPoint = this.monstrosity.getNavigation().getPath().getEndNode();
                            if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                                failedPathFindingPenalty = 0;
                            else
                                failedPathFindingPenalty += 10;
                        } else {
                            failedPathFindingPenalty += 10;
                        }
                    }
                    if (d0 > 1024.0D) {
                        this.ticksUntilNextPathRecalculation += 10;
                    } else if (d0 > 256.0D) {
                        this.ticksUntilNextPathRecalculation += 5;
                    }

                    if (!this.monstrosity.getNavigation().moveTo(livingentity, 1.0F)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }

                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected double getAttackReachSqr(LivingEntity target) {
            return this.monstrosity.getBbWidth() * 2.0F * this.monstrosity.getBbWidth() * 2.0F + target.getBbWidth();
        }
    }
}
