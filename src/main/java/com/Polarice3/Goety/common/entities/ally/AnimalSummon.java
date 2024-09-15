package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AnimalSummon extends Summoned{
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(AnimalSummon.class, EntityDataSerializers.BOOLEAN);
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;
    private int inLove;
    @Nullable
    private UUID loveCause;

    public AnimalSummon(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY_ID, false);
    }

    public boolean canBreed() {
        return false;
    }

    public int getAge() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
        } else {
            return this.age;
        }
    }

    public void ageUp(int p_146741_, boolean p_146742_) {
        int i = this.getAge();
        i += p_146741_ * 20;
        if (i > 0) {
            i = 0;
        }

        int j = i - i;
        this.setAge(i);
        if (p_146742_) {
            this.forcedAge += j;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAge(this.forcedAge);
        }

    }

    public void ageUp(int p_146759_) {
        this.ageUp(p_146759_, false);
    }

    public void setAge(int p_146763_) {
        int i = this.getAge();
        this.age = p_146763_;
        if (i < 0 && p_146763_ >= 0 || i >= 0 && p_146763_ < 0) {
            this.entityData.set(DATA_BABY_ID, p_146763_ < 0);
            this.ageBoundaryReached();
        }

    }

    @Nullable
    public AnimalSummon getBreedOffspring(ServerLevel p_146743_, AnimalSummon p_146744_) {
        Entity entity = this.getType().create(p_146743_);

        if (entity instanceof AnimalSummon animalSummon){
            if (this.isUpgraded() || p_146744_.isUpgraded()){
                animalSummon.setUpgraded(true);
            }
            return animalSummon;
        }

        return null;
    }

    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        super.customServerAiStep();
    }

    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();
            if (i < 0) {
                ++i;
                this.setAge(i);
            } else if (i > 0) {
                --i;
                this.setAge(i);
            }
        }

        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

    }

    protected void ageBoundaryReached() {
        if (!this.isBaby() && this.isPassenger()) {
            Entity entity = this.getVehicle();
            if (entity instanceof Boat boat) {
                if (!boat.hasEnoughSpaceFor(this)) {
                    this.stopRiding();
                }
            }
        }
    }

    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public void setBaby(boolean p_146756_) {
        this.setAge(p_146756_ ? -24000 : 0);
    }

    public static int getSpeedUpSecondsWhenFeeding(int p_216968_) {
        return (int)((float)(p_216968_ / 20) * 0.1F);
    }

    public boolean hurt(DamageSource p_27567_, float p_27568_) {
        if (this.isInvulnerableTo(p_27567_)) {
            return false;
        } else {
            this.inLove = 0;
            return super.hurt(p_27567_, p_27568_);
        }
    }

    public float getWalkTargetValue(BlockPos p_27573_, LevelReader p_27574_) {
        return p_27574_.getBlockState(p_27573_.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : p_27574_.getPathfindingCostFromLightLevels(p_27573_);
    }

    public void addAdditionalSaveData(CompoundTag p_27587_) {
        super.addAdditionalSaveData(p_27587_);
        p_27587_.putInt("Age", this.getAge());
        p_27587_.putInt("ForcedAge", this.forcedAge);
        p_27587_.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            p_27587_.putUUID("LoveCause", this.loveCause);
        }

    }

    public double getMyRidingOffset() {
        return 0.14D;
    }

    public void readAdditionalSaveData(CompoundTag p_27576_) {
        super.readAdditionalSaveData(p_27576_);
        this.setAge(p_27576_.getInt("Age"));
        this.forcedAge = p_27576_.getInt("ForcedAge");
        this.inLove = p_27576_.getInt("InLove");
        this.loveCause = p_27576_.hasUUID("LoveCause") ? p_27576_.getUUID("LoveCause") : null;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_146754_) {
        if (DATA_BABY_ID.equals(p_146754_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_146754_);
    }

    public int getAmbientSoundInterval() {
        return 120;
    }

    public boolean removeWhenFarAway(double p_27598_) {
        return false;
    }

    public int getExperienceReward() {
        return 1 + this.level().random.nextInt(3);
    }

    public boolean isFood(ItemStack p_27600_) {
        return p_27600_.is(Items.WHEAT);
    }

    public InteractionResult mobInteract(Player p_27584_, InteractionHand p_27585_) {
        ItemStack itemstack = p_27584_.getItemInHand(p_27585_);
        if (this.getTrueOwner() != null && p_27584_ == this.getTrueOwner()) {
            if (this.isFood(itemstack)) {
                int i = this.getAge();
                if (!this.level.isClientSide && i == 0 && this.canFallInLove()) {
                    this.usePlayerItem(p_27584_, p_27585_, itemstack);
                    this.setInLove(p_27584_);
                    return InteractionResult.SUCCESS;
                }

                if (this.isBaby()) {
                    this.usePlayerItem(p_27584_, p_27585_, itemstack);
                    this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }

                if (this.level.isClientSide) {
                    return InteractionResult.CONSUME;
                }
            }
        }

        return super.mobInteract(p_27584_, p_27585_);
    }

    protected void usePlayerItem(Player p_148715_, InteractionHand p_148716_, ItemStack p_148717_) {
        if (!p_148715_.getAbilities().instabuild) {
            p_148717_.shrink(1);
        }

    }

    public boolean canFallInLove() {
        return this.inLove <= 0 && !this.limitedLifespan && this.limitedLifeTicks <= 0;
    }

    public void setInLove(@Nullable Player p_27596_) {
        if (!this.limitedLifespan && this.limitedLifeTicks <= 0) {
            this.inLove = 600;
            if (p_27596_ != null) {
                this.loveCause = p_27596_.getUUID();
            }

            this.level().broadcastEntityEvent(this, (byte) 18);
        }
    }

    public void setInLoveTime(int p_27602_) {
        this.inLove = p_27602_;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            Player player = this.level().getPlayerByUUID(this.loveCause);
            return player instanceof ServerPlayer ? (ServerPlayer)player : null;
        }
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(AnimalSummon p_27569_) {
        if (p_27569_ == this) {
            return false;
        } else if (p_27569_.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isInLove() && p_27569_.isInLove();
        }
    }

    public void spawnChildFromBreeding(ServerLevel p_27564_, AnimalSummon p_27565_) {
        AnimalSummon ageablemob = this.getBreedOffspring(p_27564_, p_27565_);
        if (ageablemob != null) {
            ageablemob.setBaby(true);
            ageablemob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            if (this.getTrueOwner() != null) {
                ageablemob.setTrueOwner(this.getTrueOwner());
            }
            this.finalizeSpawnChildFromBreeding(p_27564_, p_27565_, ageablemob);
            p_27564_.addFreshEntityWithPassengers(ageablemob);
        }
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel p_277963_, AnimalSummon p_277357_, @Nullable AnimalSummon p_277516_) {
        Optional.ofNullable(this.getLoveCause()).or(() -> {
            return Optional.ofNullable(p_277357_.getLoveCause());
        }).ifPresent((p_277486_) -> {
            p_277486_.awardStat(Stats.ANIMALS_BRED);
        });
        this.setAge(6000);
        p_277357_.setAge(6000);
        this.resetLove();
        p_277357_.resetLove();
        p_277963_.broadcastEntityEvent(this, (byte)18);
        if (p_277963_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            p_277963_.addFreshEntity(new ExperienceOrb(p_277963_, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    public void handleEntityEvent(byte p_27562_) {
        if (p_27562_ == 18) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(p_27562_);
        }

    }

    public static class BreedGoal extends Goal {
        private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
        protected final AnimalSummon animal;
        private final Class<? extends AnimalSummon> partnerClass;
        protected final Level level;
        @Nullable
        protected AnimalSummon partner;
        private int loveTime;
        private final double speedModifier;

        public BreedGoal(AnimalSummon p_25122_, double p_25123_) {
            this(p_25122_, p_25123_, p_25122_.getClass());
        }

        public BreedGoal(AnimalSummon p_25125_, double p_25126_, Class<? extends AnimalSummon> p_25127_) {
            this.animal = p_25125_;
            this.level = p_25125_.level();
            this.partnerClass = p_25127_;
            this.speedModifier = p_25126_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if (!this.animal.isInLove()) {
                return false;
            } else {
                this.partner = this.getFreePartner();
                return this.partner != null;
            }
        }

        public boolean canContinueToUse() {
            return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
        }

        public void stop() {
            this.partner = null;
            this.loveTime = 0;
        }

        public void tick() {
            this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
            this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
            ++this.loveTime;
            if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0D) {
                this.breed();
            }

        }

        @Nullable
        private AnimalSummon getFreePartner() {
            List<? extends AnimalSummon> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
            double d0 = Double.MAX_VALUE;
            AnimalSummon animal = null;

            for(AnimalSummon animal1 : list) {
                if (this.animal.canMate(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                    animal = animal1;
                    d0 = this.animal.distanceToSqr(animal1);
                }
            }

            return animal;
        }

        protected void breed() {
            this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
        }
    }
}
