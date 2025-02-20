package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICharger;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.ChargeGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class TwilightGoat extends AnimalSummon implements ICharger {
    public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.scalable(0.9F, 1.3F).scale(0.7F);
    private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING_GOAT = SynchedEntityData.defineId(TwilightGoat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_LEFT_HORN = SynchedEntityData.defineId(TwilightGoat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_RIGHT_HORN = SynchedEntityData.defineId(TwilightGoat.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(TwilightGoat.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(600, 1200);
    private boolean isLoweringHead;
    private int lowerHeadTick;
    private int longJumpCool;

    public TwilightGoat(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new GoatChargeGoal(this, (p_287490_) -> {
            return p_287490_.isBaby() ? 1.0D : 2.5D;
        }, (p_149468_) -> {
            return p_149468_ instanceof TwilightGoat goat && goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT;
        }, (p_218772_) -> {
            return p_218772_ instanceof TwilightGoat goat && goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HORN_BREAK : SoundEvents.GOAT_HORN_BREAK;
        }));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TwilightGoatHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ARMOR, AttributesConfig.TwilightGoatArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TwilightGoatDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TwilightGoatHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TwilightGoatArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TwilightGoatDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_SCREAMING_GOAT, false);
        this.entityData.define(DATA_HAS_LEFT_HORN, true);
        this.entityData.define(DATA_HAS_RIGHT_HORN, true);
        this.entityData.define(DATA_CHARGING, false);
    }

    public void addAdditionalSaveData(CompoundTag p_149385_) {
        super.addAdditionalSaveData(p_149385_);
        p_149385_.putBoolean("IsScreamingGoat", this.isScreamingGoat());
        p_149385_.putBoolean("HasLeftHorn", this.hasLeftHorn());
        p_149385_.putBoolean("HasRightHorn", this.hasRightHorn());
    }

    public void readAdditionalSaveData(CompoundTag p_149373_) {
        super.readAdditionalSaveData(p_149373_);
        this.setScreamingGoat(p_149373_.getBoolean("IsScreamingGoat"));
        this.entityData.set(DATA_HAS_LEFT_HORN, p_149373_.getBoolean("HasLeftHorn"));
        this.entityData.set(DATA_HAS_RIGHT_HORN, p_149373_.getBoolean("HasRightHorn"));
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public boolean hasLeftHorn() {
        return this.entityData.get(DATA_HAS_LEFT_HORN);
    }

    public boolean hasRightHorn() {
        return this.entityData.get(DATA_HAS_RIGHT_HORN);
    }

    public void addHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, true);
        this.entityData.set(DATA_HAS_RIGHT_HORN, true);
    }

    public void removeHorns() {
        this.entityData.set(DATA_HAS_LEFT_HORN, false);
        this.entityData.set(DATA_HAS_RIGHT_HORN, false);
    }

    public boolean isScreamingGoat() {
        return this.entityData.get(DATA_IS_SCREAMING_GOAT);
    }

    public void setScreamingGoat(boolean p_149406_) {
        this.entityData.set(DATA_IS_SCREAMING_GOAT, p_149406_);
    }

    public float getRammingXHeadRot() {
        return (float)this.lowerHeadTick / 20.0F * 30.0F * ((float)Math.PI / 180F);
    }

    protected void ageBoundaryReached() {
        AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            if (this.isBaby()) {
                instance.setBaseValue(AttributesConfig.TwilightGoatDamage.get() / 2.0D);
                this.removeHorns();
            } else {
                instance.setBaseValue(AttributesConfig.TwilightGoatDamage.get());
                this.addHorns();
            }
        }

    }

    protected int calculateFallDamage(float p_149389_, float p_149390_) {
        return super.calculateFallDamage(p_149389_, p_149390_) - 10;
    }

    protected SoundEvent getAmbientSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_AMBIENT : SoundEvents.GOAT_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_149387_) {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HURT : SoundEvents.GOAT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_DEATH : SoundEvents.GOAT_DEATH;
    }

    protected void playStepSound(BlockPos p_149382_, BlockState p_149383_) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getMilkingSound() {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK;
    }

    public int getMaxHeadYRot() {
        return 15;
    }

    public void setYHeadRot(float p_149400_) {
        int i = this.getMaxHeadYRot();
        float f = Mth.degreesDifference(this.yBodyRot, p_149400_);
        float f1 = Mth.clamp(f, (float)(-i), (float)i);
        super.setYHeadRot(this.yBodyRot + f1);
    }

    public SoundEvent getEatingSound(ItemStack p_149394_) {
        return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_EAT : SoundEvents.GOAT_EAT;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_149365_, DifficultyInstance p_149366_, MobSpawnType p_149367_, @Nullable SpawnGroupData p_149368_, @Nullable CompoundTag p_149369_) {
        RandomSource randomsource = p_149365_.getRandom();
        this.setScreamingGoat(randomsource.nextDouble() < 0.02D);
        this.ageBoundaryReached();
        if (!this.isBaby() && (double)randomsource.nextFloat() < (double)0.1F) {
            EntityDataAccessor<Boolean> entitydataaccessor = randomsource.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
            this.entityData.set(entitydataaccessor, false);
        }

        return super.finalizeSpawn(p_149365_, p_149366_, p_149367_, p_149368_, p_149369_);
    }

    public float getScale() {
        return this.isUpgraded() ? 1.25F : super.getScale();
    }

    public EntityDimensions getDimensions(Pose p_149361_) {
        return p_149361_ == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(p_149361_);
    }

    public void handleEntityEvent(byte p_149356_) {
        if (p_149356_ == 58) {
            this.isLoweringHead = true;
        } else if (p_149356_ == 59) {
            this.isLoweringHead = false;
        } else {
            super.handleEntityEvent(p_149356_);
        }

    }

    public void aiStep() {
        if (this.isLoweringHead) {
            ++this.lowerHeadTick;
        } else {
            this.lowerHeadTick -= 2;
        }

        this.lowerHeadTick = Mth.clamp(this.lowerHeadTick, 0, 20);
        super.aiStep();

        if (this.longJumpCool > 0){
            --this.longJumpCool;
        }
    }

    public boolean dropHorn() {
        boolean flag = this.hasLeftHorn();
        boolean flag1 = this.hasRightHorn();
        if (!flag && !flag1) {
            return false;
        } else {
            EntityDataAccessor<Boolean> entitydataaccessor;
            if (!flag) {
                entitydataaccessor = DATA_HAS_RIGHT_HORN;
            } else if (!flag1) {
                entitydataaccessor = DATA_HAS_LEFT_HORN;
            } else {
                entitydataaccessor = this.random.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
            }

            this.entityData.set(entitydataaccessor, false);
            return true;
        }
    }

    @Override
    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    @Override
    public void setCharging(boolean flag) {
        this.entityData.set(DATA_CHARGING, flag);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.TwilightGoatHealth.get() * 1.5D);
                armor.setBaseValue(AttributesConfig.TwilightGoatArmor.get() + 1.0D);
                attack.setBaseValue(AttributesConfig.TwilightGoatDamage.get() + 1.0D);
            } else {
                health.setBaseValue(AttributesConfig.TwilightGoatHealth.get());
                armor.setBaseValue(AttributesConfig.TwilightGoatArmor.get());
                attack.setBaseValue(AttributesConfig.TwilightGoatDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
        this.refreshDimensions();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.isEdible() && itemstack.getFoodProperties(this) != null && this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
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
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (itemstack.is(Items.BUCKET) && !this.isBaby() && !this.limitedLifespan && this.limitedLifeTicks <= 0) {
                pPlayer.playSound(this.getMilkingSound(), 1.0F, 1.0F);
                ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, pPlayer, Items.MILK_BUCKET.getDefaultInstance());
                pPlayer.setItemInHand(pHand, itemstack1);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    public static class GoatChargeGoal extends ChargeGoal {
        private Vec3 ramDirection;
        private final ToDoubleFunction<PathfinderMob> getKnockbackForce;
        private final Function<PathfinderMob, SoundEvent> getImpactSound;
        private final Function<PathfinderMob, SoundEvent> getHornBreakSound;
        private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((p_289449_) -> {
            return p_289449_.level.getWorldBorder().isWithinBounds(p_289449_.getBoundingBox());
        });
        private static final UniformInt TIME_BETWEEN_RAMS = UniformInt.of(100, 300);

        public GoatChargeGoal(PathfinderMob mob, ToDoubleFunction<PathfinderMob> p_217345_, Function<PathfinderMob, SoundEvent> p_217346_, Function<PathfinderMob, SoundEvent> p_217347_) {
            super(mob, 3.0F, 4, 7, 1, TIME_BETWEEN_RAMS.sample(mob.getRandom()));
            this.getKnockbackForce = p_217345_;
            this.getImpactSound = p_217346_;
            this.getHornBreakSound = p_217347_;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.windup > -20 || !this.charger.getNavigation().isDone();
        }

        public void start() {
            this.windup = MathHelper.secondsToTicks(1) + this.charger.getRandom().nextInt(MathHelper.secondsToTicks(1));
            this.charger.level.broadcastEntityEvent(this.charger, (byte)58);
            BlockPos blockpos = this.charger.blockPosition();
            if (this.chargePos != null) {
                this.ramDirection = (new Vec3((double) blockpos.getX() - this.chargePos.x(), 0.0D, (double) blockpos.getZ() - this.chargePos.z())).normalize();
            }
        }

        @Override
        public void tick() {
            this.charger.getLookControl().setLookAt(this.chargePos.x(), this.chargePos.y() - 1, this.chargePos.z(), 10.0F, this.charger.getMaxHeadXRot());

            --this.windup;
            if (this.windup == 0){
                PathNavigation pathnavigation = this.charger.getNavigation();
                Path path = pathnavigation.createPath(BlockPos.containing(this.chargePos), 0);
                if (path != null && path.canReach()){
                    pathnavigation.moveTo(path, this.speed);
                } else {
                    pathnavigation.moveTo(this.chargePos.x(), this.chargePos.y(), this.chargePos.z(), this.speed);
                }
            }
            if (this.windup <= 0) {
                List<LivingEntity> list = this.charger.level.getNearbyEntities(LivingEntity.class, RAM_TARGET_CONDITIONS, this.charger, this.charger.getBoundingBox());
                list.removeIf(livingEntity -> MobUtil.areAllies(this.charger, livingEntity));
                if (!list.isEmpty()) {
                    LivingEntity livingentity = list.get(0);
                    DamageSource damageSource = this.charger instanceof IOwned owned && owned.getTrueOwner() != null ? ModDamageSource.summonAttack(this.charger, owned.getTrueOwner()) : this.charger.damageSources().mobAttack(this.charger);
                    livingentity.hurt(damageSource, (float)this.charger.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    int i = this.charger.hasEffect(MobEffects.MOVEMENT_SPEED) ? this.charger.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
                    int j = this.charger.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? this.charger.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
                    float f = 0.25F * (float)(i - j);
                    float f1 = Mth.clamp(this.charger.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
                    float f2 = livingentity.isDamageSourceBlocked(damageSource) ? 0.5F : 1.0F;
                    if (this.ramDirection != null) {
                        livingentity.knockback((double) (f2 * f1) * this.getKnockbackForce.applyAsDouble(this.charger), this.ramDirection.x(), this.ramDirection.z());
                    }
                    this.stop();
                    this.charger.level.playSound((Player)null, this.charger, this.getImpactSound.apply(this.charger), SoundSource.NEUTRAL, 1.0F, 1.0F);
                } else if (this.hasRammedHornBreakingBlock()) {
                    this.charger.level.playSound((Player)null, this.charger, this.getImpactSound.apply(this.charger), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    boolean flag = this.charger instanceof TwilightGoat goat && goat.dropHorn();
                    if (flag) {
                        this.charger.level.playSound((Player)null, this.charger, this.getHornBreakSound.apply(this.charger), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }

                    this.stop();
                } else {
                    boolean flag1 = this.chargePos == null || this.chargePos.closerThan(this.charger.position(), 0.25D);
                    if (flag1) {
                        this.stop();
                    }
                }
            } else {
                if (this.charger instanceof ICharger chargeMob) {
                    chargeMob.setCharging(true);
                }
            }
        }

        private boolean hasRammedHornBreakingBlock() {
            Vec3 vec3 = this.charger.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize();
            BlockPos blockpos = BlockPos.containing(this.charger.position().add(vec3));
            return this.charger.level.getBlockState(blockpos).is(BlockTags.SNAPS_GOAT_HORN) || this.charger.level.getBlockState(blockpos.above()).is(BlockTags.SNAPS_GOAT_HORN);
        }

        @Override
        public void stop() {
            this.charger.level.broadcastEntityEvent(this.charger, (byte)59);
            this.charger.getNavigation().stop();
            this.windup = 0;
            this.chargeTarget = null;
            this.coolDown = this.charger.tickCount + this.coolDownTotal;
            if (this.charger instanceof ICharger chargeMob) {
                chargeMob.setCharging(false);
            }
        }
    }
}
