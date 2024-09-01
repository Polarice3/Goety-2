package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
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
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class BlackWolf extends AnimalSummon{
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(BlackWolf.class, EntityDataSerializers.BOOLEAN);
    public static final Predicate<LivingEntity> PREY_SELECTOR = (p_289448_) -> {
        EntityType<?> entitytype = p_289448_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
    };
    private int invisibleCool;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;

    public BlackWolf(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F){
            @Override
            public boolean canUse() {
                return super.canUse() && !BlackWolf.this.isStaying();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !BlackWolf.this.isStaying();
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
        this.targetSelector.addGoal(5, new NaturalAttackGoal<>(this, Animal.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(6, new NaturalAttackGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NaturalAttackGoal<>(this, AbstractSkeleton.class, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.35F)
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlackWolfHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BlackWolfArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlackWolfDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BlackWolfHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BlackWolfArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BlackWolfDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_INTERESTED_ID, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("InvisibleCool")) {
            this.invisibleCool = compound.getInt("InvisibleCool");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("InvisibleCool", this.invisibleCool);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public void setIsInterested(boolean p_30445_) {
        this.entityData.set(DATA_INTERESTED_ID, p_30445_);
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.25F;
    }

    protected void playStepSound(BlockPos p_30415_, BlockState p_30416_) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
        this.playSound(SoundEvents.CHAIN_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    protected SoundEvent getHurtSound(DamageSource p_30424_) {
        return SoundEvents.WOLF_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() != null){
            ServerParticleUtil.addParticlesAroundMiddleSelf(pLevel.getLevel(), ParticleTypes.LARGE_SMOKE, this);
            ColorUtil color = new ColorUtil(0);
            ServerParticleUtil.windParticle(pLevel.getLevel(), color, 1.0F, 0.0F, this.getId(), this.position());
        }
        return pSpawnData;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.isOnGround()) {
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

            if (!this.level.isClientSide){
                if (this.invisibleCool > 0){
                    --this.invisibleCool;
                }
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
                            if (this.getTarget() instanceof Mob mob && (mob.getTarget() != this || mob.getTarget().isDeadOrDying())) {
                                mob.setTarget(this);
                            }
                        } else {
                            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MathHelper.secondsToTicks(30), 0, false, false));
                            this.invisibleCool = MathHelper.secondsToTicks(30);
                        }
                    }
                }
            }
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag){
            if (this.hasEffect(MobEffects.INVISIBILITY)){
                this.removeEffect(MobEffects.INVISIBILITY);
            }
        }
        return flag;
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

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.hasEffect(MobEffects.INVISIBILITY)){
                this.removeEffect(MobEffects.INVISIBILITY);
            }
            if (this.isUpgraded()){
                if (entityIn instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), MathHelper.secondsToTicks(5), 0), this);
                }
            }
        }
        return flag;
    }

    public void handleEntityEvent(byte p_30379_) {
        if (p_30379_ == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
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

    public boolean canFallInLove() {
        return false;
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.BlackWolfHealth.get() * 1.5D);
                armor.setBaseValue(AttributesConfig.BlackWolfArmor.get() + 1.0D);
                attack.setBaseValue(AttributesConfig.BlackWolfDamage.get() + 1.0D);
            } else {
                health.setBaseValue(AttributesConfig.BlackWolfHealth.get());
                armor.setBaseValue(AttributesConfig.BlackWolfArmor.get());
                attack.setBaseValue(AttributesConfig.BlackWolfDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    public static class BegGoal extends Goal {
        private final BlackWolf wolf;
        private final float lookDistance;
        private int lookTime;

        public BegGoal(BlackWolf p_25063_, float p_25064_) {
            this.wolf = p_25063_;
            this.lookDistance = p_25064_;
            this.setFlags(EnumSet.of(Flag.LOOK));
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
}
