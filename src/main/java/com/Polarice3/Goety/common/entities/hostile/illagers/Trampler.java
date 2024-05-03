package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.api.entities.ICharger;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.ChargeGoal;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Trampler extends Raider implements ICharger, ICustomAttributes {
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(Trampler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(Trampler.class, EntityDataSerializers.BOOLEAN);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;

    public Trampler(EntityType<? extends Raider> p_37839_, Level p_37840_) {
        super(p_37839_, p_37840_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new TramplerChargeGoal(this));
        this.goalSelector.addGoal(4, new TramplerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
            return !p_199899_.isBaby();
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TramplerHealth.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TramplerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TramplerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TramplerDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(DATA_CHARGING, false);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
    }

    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.6D * 0.75D;
    }

    public void positionRider(Entity rider) {
        super.positionRider(rider);
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
        if (!p_30585_.getMaterial().isLiquid()) {
            BlockState blockstate = this.level.getBlockState(p_30584_.above());
            SoundType soundtype = p_30585_.getSoundType(level, p_30584_, this);
            if (blockstate.is(Blocks.SNOW)) {
                soundtype = blockstate.getSoundType(level, p_30584_, this);
            }

            if (this.isVehicle() && this.canGallop) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                    this.playGallopSound(soundtype);
                } else if (this.gallopSoundCounter <= 5) {
                    this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            } else if (soundtype == SoundType.WOOD) {
                this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            } else {
                this.playSound(SoundEvents.HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }

        }
    }

    protected void playGallopSound(SoundType p_30560_) {
        this.playSound(SoundEvents.HORSE_GALLOP, p_30560_.getVolume() * 0.15F, p_30560_.getPitch());
    }

    @Nullable
    public Entity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity != null && this.canBeControlledBy(entity) ? entity : null;
    }

    private boolean canBeControlledBy(Entity p_219063_) {
        return !this.isNoAi() && p_219063_ instanceof LivingEntity;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
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
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            AttributeInstance instance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (instance != null) {
                double d0 = this.getTarget() != null ? 0.45D : 0.35D;
                double d1 = instance.getBaseValue();
                instance.setBaseValue(Mth.lerp(0.1D, d1, d0));
            }

            if (this.isCharging()){
                this.animationSpeed += 0.8F;
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

                if (!flag && this.onGround) {
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
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public @NotNull SoundEvent getCelebrateSound() {
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
        if (this.isCharging()) {
            this.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), 100, 0, false, false));
            p_33361_.hurtMarked = true;
        }
    }

    class TramplerMeleeAttackGoal extends MeleeAttackGoal {
        public TramplerMeleeAttackGoal() {
            super(Trampler.this, 1.25D, true);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !Trampler.this.isCharging();
        }

        protected void checkAndPerformAttack(LivingEntity p_29589_, double p_29590_) {
            double d0 = this.getAttackReachSqr(p_29589_);
            if (p_29590_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_29589_);
                Trampler.this.setStanding(false);
            } else if (p_29590_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    Trampler.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    Trampler.this.setStanding(true);
                }
            } else {
                this.resetAttackCooldown();
                Trampler.this.setStanding(false);
            }

        }

        public void stop() {
            Trampler.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_29587_) {
            return (double)(4.0F + p_29587_.getBbWidth());
        }
    }

    public static class TramplerChargeGoal extends ChargeGoal{

        public TramplerChargeGoal(PathfinderMob mob) {
            super(mob, 1.2F, 4.0D, 32.0D, 5, 200);
        }

        public double getAttackReachSqr(LivingEntity target) {
            return 6.0F + target.getBbWidth();
        }
    }
}
