package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SkeletonWolf extends AnimalSummon {
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(SkeletonWolf.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(SkeletonWolf.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HOWLING = SynchedEntityData.defineId(SkeletonWolf.class, EntityDataSerializers.BOOLEAN);
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private int howlingCool;
    public boolean isSitting;
    public AnimationState howlAnimationState = new AnimationState();

    public SkeletonWolf(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new HowlGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F){
            @Override
            public boolean canUse() {
                return super.canUse() && !SkeletonWolf.this.isHowling() && !SkeletonWolf.this.isStaying();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !SkeletonWolf.this.isHowling() && !SkeletonWolf.this.isStaying();
            }
        });
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(6, new NaturalAttackGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.35F)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkeletonWolfHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SkeletonWolfArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkeletonWolfDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SkeletonWolfHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SkeletonWolfArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SkeletonWolfDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        this.entityData.define(DATA_INTERESTED_ID, false);
        this.entityData.define(DATA_HOWLING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HowlingCool")) {
            this.howlingCool = compound.getInt("HowlingCool");
        }
        if (compound.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
        }
        if (compound.contains("Sitting")) {
            this.isSitting = compound.getBoolean("Sitting");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HowlingCool", this.howlingCool);
        compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
        compound.putBoolean("Sitting", this.isSitting);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public void setIsInterested(boolean p_30445_) {
        this.entityData.set(DATA_INTERESTED_ID, p_30445_);
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    public void setIsHowling(boolean p_30445_) {
        this.entityData.set(DATA_HOWLING, p_30445_);
    }

    public boolean isHowling() {
        return this.entityData.get(DATA_HOWLING);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor p_30398_) {
        this.entityData.set(DATA_COLLAR_COLOR, p_30398_.getId());
    }

    protected void playStepSound(BlockPos p_30415_, BlockState p_30416_) {
        this.playSound(ModSounds.SKELETON_WOLF_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHowling()){
            super.playAmbientSound();
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.isAggressive()) {
            return ModSounds.SKELETON_WOLF_GROWL.get();
        } else if (this.random.nextInt(3) == 0) {
            return this.getTrueOwner() != null && this.getHealth() < this.getMaxHealth() / 2.0F ? ModSounds.SKELETON_WOLF_WHINE.get() : ModSounds.SKELETON_WOLF_PANT.get();
        } else {
            return ModSounds.SKELETON_WOLF_AMBIENT.get();
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_30424_) {
        return ModSounds.SKELETON_WOLF_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.SKELETON_WOLF_DEATH.get();
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean p_146756_) {
    }

    public boolean isSitting() {
        return this.isSitting;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && !this.isHowling() && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEvent(this, (byte)8);
        }
    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            if (this.isInterested()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }

            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level.isClientSide) {
                    this.level.broadcastEntityEvent(this, (byte)56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(ModSounds.SKELETON_WOLF_SHAKE.get(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
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

            if (!this.level.isClientSide){
                if (this.howlingCool > 0){
                    --this.howlingCool;
                }
                if (this.isStaying()){
                    this.isSitting = true;
                    this.level.broadcastEntityEvent(this, (byte) 9);
                } else {
                    this.isSitting = false;
                    this.level.broadcastEntityEvent(this, (byte) 10);
                }
                if (this.getTarget() != null){
                    this.setAggressive(true);
                    this.level.broadcastEntityEvent(this, (byte) 6);
                } else {
                    this.setAggressive(false);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                }
            }

        }
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

    public float getHeadRollAngle(float p_30449_) {
        return Mth.lerp(p_30449_, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
    }

    protected float getStandingEyeHeight(Pose p_30409_, EntityDimensions p_30410_) {
        return p_30410_.height * 0.8F;
    }

    public int getMaxHeadXRot() {
        return this.isStaying() ? 20 : super.getMaxHeadXRot();
    }

    public void handleEntityEvent(byte p_30379_) {
        if (p_30379_ == 4){
            this.howlAnimationState.startIfStopped(this.tickCount);
        } else if (p_30379_ == 5){
            this.howlAnimationState.stop();
        } else if (p_30379_ == 6){
            this.setAggressive(true);
        } else if (p_30379_ == 7){
            this.setAggressive(false);
        } else if (p_30379_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (p_30379_ == 9) {
            this.isSitting = true;
        } else if (p_30379_ == 10) {
            this.isSitting = false;
        } else if (p_30379_ == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(p_30379_);
        }

    }

    public float getTailAngle() {
        return (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI;
    }

    public boolean isFood(ItemStack p_30440_) {
        return p_30440_.is(Tags.Items.BONES);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
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
            } else {
                if (item instanceof DyeItem dyeitem) {
                    if (this.getTrueOwner() == pPlayer) {
                        DyeColor dyecolor = dyeitem.getDyeColor();
                        if (dyecolor != this.getCollarColor()) {
                            this.setCollarColor(dyecolor);
                            if (!pPlayer.getAbilities().instabuild) {
                                itemstack.shrink(1);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean canFallInLove() {
        return false;
    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    public static class BegGoal extends Goal {
        private final SkeletonWolf wolf;
        private final float lookDistance;
        private int lookTime;

        public BegGoal(SkeletonWolf p_25063_, float p_25064_) {
            this.wolf = p_25063_;
            this.lookDistance = p_25064_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.wolf.getTrueOwner() != null && this.playerHoldingInteresting(this.wolf.getTrueOwner());
        }

        public boolean canContinueToUse() {
            if (this.wolf.getTrueOwner() == null || !this.wolf.getTrueOwner().isAlive()) {
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

    public static class HowlGoal extends Goal {
        private final SkeletonWolf wolf;
        private int howlTime;

        public HowlGoal(SkeletonWolf p_25063_) {
            this.wolf = p_25063_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.wolf.getTarget() != null && this.wolf.getTarget().isAlive() && this.wolf.howlingCool <= 0 && this.wolf.getRandom().nextBoolean();
        }

        public boolean canContinueToUse() {
            return this.howlTime > 0;
        }

        public void start() {
            this.wolf.setIsHowling(true);
            this.wolf.getNavigation().stop();
            this.wolf.getMoveControl().strafe(0.0F, 0.0F);
            this.howlTime = MathHelper.secondsToTicks(3.25F);
            this.wolf.playSound(ModSounds.SKELETON_WOLF_HOWL.get(), 1.0F, 1.0F);
            this.wolf.level.broadcastEntityEvent(this.wolf, (byte) 4);
        }

        public void stop() {
            this.wolf.setIsHowling(false);
            this.wolf.level.broadcastEntityEvent(this.wolf, (byte) 5);
            this.wolf.howlingCool = 100;
        }

        public void tick() {
            --this.howlTime;
            this.wolf.getNavigation().stop();
            this.wolf.getMoveControl().strafe(0.0F, 0.0F);
            if (this.howlTime == MathHelper.secondsToTicks(3)){
                for (LivingEntity livingEntity : this.wolf.level.getEntitiesOfClass(LivingEntity.class, this.wolf.getBoundingBox().inflate(8.0D))){
                    if (livingEntity != this.wolf){
                        boolean flag = false;
                        if (this.wolf.isHostile()){
                            if (livingEntity instanceof AbstractSkeleton){
                                flag = true;
                            }
                        }
                        if (livingEntity instanceof AbstractSkeletonServant || livingEntity instanceof SkeletonWolf || livingEntity.getType().is(ModTags.EntityTypes.SKELETON_WOLF_BUFF)){
                            if (!(livingEntity instanceof DrownedNecromancer)) {
                                if (MobUtil.areAllies(this.wolf, livingEntity)) {
                                    flag = true;
                                }
                            }
                        }
                        if (flag){
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MathHelper.secondsToTicks(5)));
                        }
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
