package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.ICharger;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.ChargeGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.TramplerArmorItem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CSetDeltaMovement;
import com.Polarice3.Goety.common.network.client.CTramplerPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class AllyTrampler extends RaiderServant implements ICharger, IAutoRideable, PlayerRideableJumping {
    private static final UUID ARMOR_MODIFIER_UUID = ModUUIDUtil.createUUID("entity.goety.ally_trampler.armor");
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(AllyTrampler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(AllyTrampler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH = SynchedEntityData.defineId(AllyTrampler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(AllyTrampler.class, EntityDataSerializers.BOOLEAN);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected float playerJumpPendingScale;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;
    private int dashCooldown = 0;
    public int running = 0;

    public AllyTrampler(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new TramplerChargeGoal(this));
        this.goalSelector.addGoal(4, new TramplerMeleeAttackGoal());
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new RaiderWanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @SuppressWarnings("removal")
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TramplerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TramplerArmor.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TramplerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TramplerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TramplerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TramplerDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(DATA_CHARGING, false);
        this.entityData.define(AUTO_MODE, false);
        this.entityData.define(DASH, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("AutoMode", this.isAutonomous());
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        if(!itemStack.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            itemStack.save(compoundTag);
            pCompound.put("ArmorItem", compoundTag);
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
        if (pCompound.contains("ArmorItem")) {
            CompoundTag armorItem = pCompound.getCompound("ArmorItem");
            if (!armorItem.isEmpty()) {
                this.setArmorEquipment(ItemStack.of(armorItem), false);
            }
        }
    }

    protected float getWaterSlowDown() {
        return 0.98F;
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

    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.6D * 0.75D;
    }

    public void positionRider(Entity rider, Entity.MoveFunction p_19958_) {
        super.positionRider(rider, p_19958_);
        if (this.standAnimO > 0.0F) {
            float f3 = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float f = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            float f1 = 0.7F * this.standAnimO;
            float f2 = 0.15F * this.standAnimO;
            rider.setPos(this.getX() + (double)(f1 * f3), this.getY() + this.getPassengersRidingOffset() + rider.getMyRidingOffset() + (double)f2, this.getZ() - (double)(f1 * f));
        }
        if (rider instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
        }
    }

    public int getAmbientSoundInterval() {
        return 400;
    }

    public SoundEvent getAmbientSound() {
        return ModSounds.TRAMPLER_AMBIENT.get();
    }

    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.TRAMPLER_HURT.get();
    }

    public SoundEvent getDeathSound() {
        return ModSounds.TRAMPLER_DEATH.get();
    }

    protected void playStepSound(BlockPos p_30584_, BlockState p_30585_) {
        if (!p_30585_.liquid()) {
            BlockState blockstate = this.level().getBlockState(p_30584_.above());
            SoundType soundtype = p_30585_.getSoundType(level(), p_30584_, this);
            if (blockstate.is(Blocks.SNOW)) {
                soundtype = blockstate.getSoundType(level(), p_30584_, this);
            }

            if (this.isVehicle() && this.canGallop) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                    this.playGallopSound(soundtype);
                } else if (this.gallopSoundCounter <= 5) {
                    this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            } else if (this.isWoodSoundType(soundtype)) {
                this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            } else {
                this.playSound(SoundEvents.HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }

        }
    }

    private boolean isWoodSoundType(SoundType p_278280_) {
        return p_278280_ == SoundType.WOOD || p_278280_ == SoundType.NETHER_WOOD || p_278280_ == SoundType.STEM || p_278280_ == SoundType.CHERRY_WOOD || p_278280_ == SoundType.BAMBOO_WOOD;
    }

    protected void playGallopSound(SoundType p_30560_) {
        this.playSound(SoundEvents.HORSE_GALLOP, p_30560_.getVolume() * 0.15F, p_30560_.getPitch());
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

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    public void setArmorEquipment(ItemStack armor, boolean sound) {
        if (!this.level.isClientSide) {
            this.setItemSlot(EquipmentSlot.CHEST, armor);
            float chance = MobsConfig.PlayerRavagerArmorDrop.get() ? 2.0F : 0.0F;
            this.setDropChance(EquipmentSlot.CHEST, chance);
            this.updateArmor();
            if (sound) {
                this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
            }
        }
    }

    public void updateArmor(){
        AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
        if (attribute != null) {
            attribute.removeModifier(ARMOR_MODIFIER_UUID);
            if (this.isArmor(this.getArmor())) {
                int i = ((TramplerArmorItem) this.getArmor().getItem()).getProtection();
                if (i != 0) {
                    attribute.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Ravager armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

    public boolean isArmor(ItemStack p_30731_) {
        return p_30731_.getItem() instanceof TramplerArmorItem;
    }

    public void tick() {
        super.tick();
        if (this.isHostile()) {
            if (MobsConfig.TramplerHostileConvert.get()) {
                if (this.tickCount % 20 == 0) {
                    this.convertTo(ModEntityType.TRAMPLER.get(), false);
                }
            }
        }
        if (this.isDashing() && this.dashCooldown < 180 && (this.onGround() || this.isInWater() || this.isPassenger())) {
            this.setDashing(false);
            if (this.level.isClientSide){
                ModNetwork.sendToServer(new CTramplerPacket(this.getId(), 2));
            } else {
                this.level.broadcastEntityEvent(this, (byte) 5);
            }
        }

        if (this.dashCooldown > 0) {
            --this.dashCooldown;
        }

        if (this.level.isClientSide) {
            if (this.running > 0 && this.tickCount % 2 == 0){
                this.running = 0;
            }
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            this.standAnimO = this.standAnim;
            this.mouthAnimO = this.mouthAnim;
            if (this.isStanding()) {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
                this.standAnim += (1.0F - this.standAnim) * 0.4F + 0.05F;
                if (this.standAnim > 1.0F) {
                    this.standAnim = 1.0F;
                }
                this.mouthAnim += (1.0F - this.mouthAnim) * 0.7F + 0.05F;
                if (this.mouthAnim > 1.0F) {
                    this.mouthAnim = 1.0F;
                }
            } else {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
                this.standAnim += (0.8F * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6F - 0.05F;
                if (this.standAnim < 0.0F) {
                    this.standAnim = 0.0F;
                }
                this.mouthAnim += (0.0F - this.mouthAnim) * 0.7F - 0.05F;
                if (this.mouthAnim < 0.0F) {
                    this.mouthAnim = 0.0F;
                }
            }
        } else {
            if (this.running > 0 && this.tickCount % 2 == 0){
                this.running = 0;
            }
            if (this.isDashing()){
                for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0F),
                        selector -> !MobUtil.areAllies(this, selector)
                                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(selector))){
                    if (this.doHurtTarget(livingEntity)) {
                        break;
                    }
                }
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            AttributeInstance instance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (instance != null) {
                double d0 = this.getTarget() != null || this.getFirstPassenger() instanceof Player ? 0.45D : 0.35D;
                double d1 = instance.getBaseValue();
                instance.setBaseValue(Mth.lerp(0.1D, d1, d0));
            }

            if (this.isCharging() || this.isDashing()){
                this.walkAnimation.setSpeed(this.walkAnimation.speed() + 0.8F);
            }

            if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                boolean flag = false;
                AABB aabb = this.getBoundingBox().inflate(0.2D);

                for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (block instanceof CropBlock) {
                        flag = this.level.destroyBlock(blockpos, true, this) || flag;
                    }
                }

                if (!flag && this.onGround()) {
                    this.jumpFromGround();
                }
            }

        }
    }

    public EntityDimensions getDimensions(Pose p_29531_) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;
            return super.getDimensions(p_29531_).scale(1.0F, f1);
        } else {
            return super.getDimensions(p_29531_);
        }
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean p_29568_) {
        this.entityData.set(DATA_STANDING_ID, p_29568_);
    }

    public float getStandingAnimationScale(float p_29570_) {
        return Mth.lerp(p_29570_, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    public float getMouthAnim(float p_30534_) {
        return Mth.lerp(p_30534_, this.mouthAnimO, this.mouthAnim);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.TRAMPLER_CELEBRATE.get();
    }

    @Override
    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        if (this.isVehicle()) {
            if (p_37849_.getEntity() != null && this.getControllingPassenger() != null) {
                if (p_37849_.getEntity() == this.getControllingPassenger()) {
                    return false;
                }
            }
        }
        return super.hurt(p_37849_, p_37850_);
    }

    @Override
    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    @Override
    public void setCharging(boolean flag) {
        this.entityData.set(DATA_CHARGING, flag);
    }

    protected void blockedByShield(LivingEntity p_33361_) {
        if (this.isCharging() || this.isDashing()) {
            this.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), 100, 0, false, false));
            p_33361_.hurtMarked = true;
        }
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
            if (this.isVehicle() && !this.isStanding() && !this.isCharging() && rider instanceof Player player && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = this.getRiddenSpeed(player);
                if (this.running > 0){
                    speed = 0.0F;
                }
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

    @Override
    public boolean canUpdateMove() {
        return !(this.getControllingPassenger() instanceof Mob);
    }

    @Override
    public void onPlayerJump(int p_21696_) {
        if (p_21696_ < 0) {
            p_21696_ = 0;
        } else {
            this.walkAnimation.setSpeed(this.walkAnimation.speed() + 0.8F);
        }

        if (p_21696_ >= 90) {
            this.playerJumpPendingScale = 1.0F;
        } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)p_21696_ / 90.0F;
        }
    }

    public boolean canSprint() {
        return true;
    }

    protected void tickRidden(Player p_278233_, Vec3 p_275693_) {
        super.tickRidden(p_278233_, p_275693_);
        Vec2 vec2 = this.getRiddenRotation(p_278233_);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (p_278233_.isLocalPlayer()) {
            if (this.dashCooldown <= 0) {
                if (p_278233_ instanceof LocalPlayer localPlayer) {
                    if (localPlayer.input.jumping) {
                        this.walkAnimation.setSpeed(this.walkAnimation.speed() + 0.8F);
                        ++this.running;
                        ModNetwork.sendToServer(new CTramplerPacket(this.getId(), 0));
                    }
                }
            }
            if (p_275693_.z <= 0.0D) {
                this.gallopSoundCounter = 0;
            }

            if (this.onGround()) {
                if (this.playerJumpPendingScale > 0.0F) {
                    this.executeRidersJump(this.playerJumpPendingScale, p_275693_);
                }

                this.playerJumpPendingScale = 0.0F;
            }
        }

    }

    protected float getRiddenSpeed(Player p_278241_) {
        float f = p_278241_.isSprinting() ? 0.1F : 0.0F;
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) + f;
    }

    protected Vec2 getRiddenRotation(LivingEntity p_275502_) {
        return new Vec2(p_275502_.getXRot() * 0.5F, p_275502_.getYRot());
    }

    protected Vec3 getRiddenInput(Player p_278278_, Vec3 p_275506_) {
        if ((this.onGround() && this.playerJumpPendingScale == 0.0F) || this.running > 0) {
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

    protected void executeRidersJump(float p_251967_, Vec3 p_275627_) {
        Vec3 vec3 = this.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)(22.2222F * p_251967_) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor());
        Vec3 vec31 = this.getDeltaMovement().add(vec3);
        this.dashCooldown = 200;
        this.setDashing(true);
        if (this.level.isClientSide) {
            ModNetwork.sendToServer(new CTramplerPacket(this.getId(), 1));
            ModNetwork.sendToServer(new CSetDeltaMovement(this.getId(), vec31.x, vec31.y, vec31.z));
        } else {
            this.level.broadcastEntityEvent(this, (byte) 4);
            this.addDeltaMovement(vec3);
        }
        this.hasImpulse = true;
    }

    public boolean isDashing() {
        return this.entityData.get(DASH);
    }

    public void setDashing(boolean p_251380_) {
        this.entityData.set(DASH, p_251380_);
    }

    @Override
    public boolean canJump() {
        return !this.isCharging() && !this.isStanding();
    }

    @Override
    public void handleStartJump(int p_21695_) {
        this.setDashing(true);
        if (this.level.isClientSide){
            ModNetwork.sendToServer(new CTramplerPacket(this.getId(), 1));
        } else {
            this.level.broadcastEntityEvent(this, (byte) 4);
        }
    }

    @Override
    public void handleStopJump() {

    }

    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.setDashing(true);
        } else if (p_21375_ == 5){
            this.setDashing(false);
        }
        super.handleEntityEvent(p_21375_);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.level.isClientSide) {
            if (pPlayer == this.getTrueOwner()) {
                if (!pPlayer.isCrouching()) {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer){
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)){
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                } else if (this.isArmor(pPlayer.getItemInHand(pHand))) {
                    if (!this.getArmor().isEmpty()) {
                        if (this.spawnAtLocation(this.getArmor()) != null) {
                            this.setArmorEquipment(pPlayer.getMainHandItem().copy(), true);
                            if (!pPlayer.getAbilities().instabuild) {
                                pPlayer.getMainHandItem().shrink(1);
                            }
                        }
                    } else {
                        this.setArmorEquipment(pPlayer.getMainHandItem().copy(), true);
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                    }
                    EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                    return InteractionResult.SUCCESS;
                } else if (this.isFood(pPlayer.getItemInHand(pHand)) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = pPlayer.getMainHandItem().getFoodProperties(this);
                    if (foodProperties != null) {
                        this.heal((float) foodProperties.getNutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), 0.5F);
                        this.gameEvent(GameEvent.EAT, this);
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.PASS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        FoodProperties foodProperties = p_30440_.getFoodProperties(this);
        return item.isEdible() && foodProperties != null && foodProperties.isMeat();
    }

    class TramplerMeleeAttackGoal extends MeleeAttackGoal {
        public TramplerMeleeAttackGoal() {
            super(AllyTrampler.this, 1.25D, true);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !AllyTrampler.this.isCharging();
        }

        protected void checkAndPerformAttack(LivingEntity p_29589_, double p_29590_) {
            double d0 = this.getAttackReachSqr(p_29589_);
            if (p_29590_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_29589_);
                AllyTrampler.this.setStanding(false);
            } else if (p_29590_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    AllyTrampler.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    AllyTrampler.this.setStanding(true);
                }
            } else {
                this.resetAttackCooldown();
                AllyTrampler.this.setStanding(false);
            }

        }

        public void stop() {
            AllyTrampler.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_29587_) {
            return (double)(4.0F + p_29587_.getBbWidth());
        }
    }

    public static class TramplerChargeGoal extends ChargeGoal {

        public TramplerChargeGoal(PathfinderMob mob) {
            super(mob, 1.2F, 4.0D, 32.0D, 5, 200);
        }

        @Override
        public boolean canUse() {
            if (this.charger instanceof AllyTrampler trampler){
                if (trampler.getControllingPassenger() instanceof Player){
                    return false;
                }
            }
            return super.canUse();
        }

        public double getAttackReachSqr(LivingEntity target) {
            return 6.0F + target.getBbWidth();
        }
    }
}
