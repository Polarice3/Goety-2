package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RipperServant extends AnimalRaiderServant {
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(RipperServant.class, EntityDataSerializers.INT);
    private static final String[] TAG_BABY_KILLER = new String[]{"Princess", "Cupcake"};
    private boolean isWet;
    private boolean isShaking;
    private boolean isBabyKiller;
    private float shakeAnim;
    private float shakeAnimO;
    private int bitingTick;

    public RipperServant(EntityType<? extends AnimalRaiderServant> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F){
            @Override
            public boolean canUse() {
                return super.canUse() && !RipperServant.this.isStaying();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !RipperServant.this.isStaying();
            }
        });
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, livingEntity -> this.isBabyKiller && livingEntity.isBaby()));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.RipperHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.RipperArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RipperDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_37870_) {
        super.addAdditionalSaveData(p_37870_);
        p_37870_.putInt("BitingTick", this.bitingTick);
        p_37870_.putInt("Size", this.getRipperSize());
        if (this.isBabyKiller) {
            p_37870_.putBoolean("BabyKiller", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_37862_) {
        super.readAdditionalSaveData(p_37862_);
        this.bitingTick = p_37862_.getInt("BitingTick");
        this.setRipperSize(p_37862_.getInt("Size"), false);
        if (p_37862_.contains("BabyKiller", 99)) {
            this.isBabyKiller = p_37862_.getBoolean("BabyKiller");
        }
    }

    public void setCustomName(@Nullable Component p_34096_) {
        super.setCustomName(p_34096_);
        if (p_34096_ != null) {
            for (String name : TAG_BABY_KILLER) {
                this.isBabyKiller = p_34096_.getString().contains(name);
            }
        }
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        float i = this.getRipperSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + (0.2F * i)) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    public void setRipperSize(int size, boolean health) {
        this.entityData.set(ID_SIZE, Mth.clamp(size, -16, 64));
        if (health) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void updateRipperSizeInfo() {
        this.refreshDimensions();
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            if (this.getRipperSize() < -1){
                attack.setBaseValue(0.5F);
            } else {
                attack.setBaseValue(AttributesConfig.RipperDamage.get() + this.getRipperSize());
            }
        }
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            if (this.getRipperSize() < 0){
                health.setBaseValue(AttributesConfig.RipperHealth.get() + (this.getRipperSize() * 4));
            } else {
                health.setBaseValue(AttributesConfig.RipperHealth.get() + (this.getRipperSize() * 2));
            }
        }
    }

    public int getRipperSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updateRipperSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    protected void playStepSound(BlockPos p_30415_, BlockState p_30416_) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        if (this.getTarget() != null) {
            return SoundEvents.WOLF_GROWL;
        } else {
            return SoundEvents.WOLF_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_30424_) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_HURT;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public float getVoicePitch() {
        return super.getVoicePitch() - 0.25F;
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()){
            if (this.bitingTick > 0) {
                --this.bitingTick;
            }
        }
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEvent(this, (byte)8);
        }
    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level.isClientSide) {
                    this.level.broadcastEntityEvent(this, (byte)56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_SHAKE);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float)this.getY();
                    int i = (int)(Mth.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for(int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)f1, (double)(f + 0.8F), this.getZ() + (double)f2, vec3.x, vec3.y, vec3.z);
                    }
                }
            }

        }
    }

    @Override
    public int agingRate() {
        return 2;
    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    public void die(DamageSource p_30384_) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(p_30384_);
    }

    public int getBitingTick() {
        return this.bitingTick;
    }

    public int attackTotalTick(){
        return 10;
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.bitingTick = attackTotalTick();
            this.level.broadcastEntityEvent(this, (byte)4);
            this.playSound(SoundEvents.FOX_BITE, this.getSoundVolume(), this.getVoicePitch());
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entityIn.setSecondsOnFire(2 * (int)f);
            }
        }

        return flag;
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float p_30447_) {
        return Math.min(0.5F + Mth.lerp(p_30447_, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
    }

    public float getBodyRollAngle(float p_30433_, float p_30434_) {
        float f = (Mth.lerp(p_30433_, this.shakeAnimO, this.shakeAnim) + p_30434_) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float)Math.PI) * Mth.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    public float getTailAngle() {
        if (this.getTarget() != null || this.getTrueOwner() == null) {
            return 1.5393804F;
        } else {
            return (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI;
        }
    }

    protected float getStandingEyeHeight(Pose p_30409_, EntityDimensions p_30410_) {
        return p_30410_.height * 0.8F;
    }

    public void handleEntityEvent(byte p_30379_) {
        if (p_30379_ == 4) {
            this.bitingTick = attackTotalTick();
            this.playSound(SoundEvents.FOX_BITE, this.getSoundVolume(), this.getVoicePitch() * 2.0F);
        } else if (p_30379_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (p_30379_ == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(p_30379_);
        }

    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34297_, DifficultyInstance p_34298_, MobSpawnType p_34299_, @Nullable SpawnGroupData p_34300_, @Nullable CompoundTag p_34301_) {
        p_34300_ = super.finalizeSpawn(p_34297_, p_34298_, p_34299_, p_34300_, p_34301_);
        float f = p_34298_.getSpecialMultiplier();
        this.handleAttributes(f);
        return p_34300_;
    }

    @Nullable
    @Override
    public AnimalRaiderServant getBreedOffspring(ServerLevel serverLevel, AnimalRaiderServant partner) {
        AnimalRaiderServant servant = super.getBreedOffspring(serverLevel, partner);
        if (servant instanceof RipperServant) {
            ForgeEventFactory.onFinalizeSpawn(servant, serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.BREEDING, null, null);
            servant.copyStance(partner);
        }
        return servant;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.WOLF_HOWL;
    }

    protected void handleAttributes(float p_34340_) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockResist = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (speed != null){
            speed.addPermanentModifier(new AttributeModifier("Random spawn speed bonus", this.random.nextDouble() * 0.05D, AttributeModifier.Operation.ADDITION));
        }
        if (knockResist != null){
            knockResist.addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05D, AttributeModifier.Operation.ADDITION));
        }

        if (this.random.nextFloat() < p_34340_ * 0.05F) {
            this.setRipperSize(2, true);
        } else {
            this.setRipperSize(this.random.nextIntBetweenInclusive(-1, 1), true);
        }
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
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
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }
}
