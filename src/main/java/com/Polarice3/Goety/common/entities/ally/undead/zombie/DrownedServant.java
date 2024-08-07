package com.Polarice3.Goety.common.entities.ally.undead.zombie;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DrownedServant extends ZombieServant implements RangedAttackMob {
    private boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public DrownedServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, worldIn);
        this.groundNavigation = new GroundPathNavigation(this, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(2, new TridentAttackGoal(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(5, new GoToBeachGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new SwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
        this.goalSelector.addGoal(7, new WaterWanderGoal<>(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.DrownedServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DrownedServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.DrownedServantArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.DrownedServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.DrownedServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.DrownedServantDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }

    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    protected boolean convertsInWater() {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    private boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else if (this.getTarget() != null && this.getTarget().isInWater()) {
            return true;
        } else {
            return this.getTrueOwner() != null && this.getTrueOwner().isInWater();
        }
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }

    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            if (blockpos != null) {
                double d0 = this.distanceToSqr((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                return d0 < 4.0D;
            }
        }

        return false;
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        ThrownTrident tridententity = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - tridententity.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        tridententity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        ItemHelper.hurtAndBreak(this.getMainHandItem(), 1, this);
        this.level.addFreshEntity(tridententity);
    }

    public void setSearchingForLand(boolean p_204713_1_) {
        this.searchingForLand = p_204713_1_;
    }

    static class GoToBeachGoal extends MoveToBlockGoal {
        private final DrownedServant drowned;

        public GoToBeachGoal(DrownedServant p_i48911_1_, double p_i48911_2_) {
            super(p_i48911_1_, p_i48911_2_, 8, 2);
            this.drowned = p_i48911_1_;
        }

        public boolean canUse() {
            if (this.drowned.getTrueOwner() != null){
                if (this.drowned.getTrueOwner().isUnderWater()){
                    return false;
                }
            } else if (this.drowned.level.isDay()) {
                return false;
            }
            return super.canUse() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level.getSeaLevel() - 3);
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            BlockPos blockpos = pPos.above();
            return pLevel.isEmptyBlock(blockpos) && pLevel.isEmptyBlock(blockpos.above()) && pLevel.getBlockState(pPos).entityCanStandOn(pLevel, pPos, this.drowned);
        }

        public void start() {
            this.drowned.setSearchingForLand(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.start();
        }

        public void stop() {
            super.stop();
        }
    }

    static class MoveHelperController extends MoveControl {
        private final DrownedServant drowned;

        public MoveHelperController(DrownedServant p_i48909_1_) {
            super(p_i48909_1_);
            this.drowned = p_i48909_1_;
        }

        public void tick() {
            LivingEntity livingentity = this.drowned.getTarget();
            LivingEntity owner = this.drowned.getTrueOwner();
            if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                } else if (owner != null && owner.getY() > this.drowned.getY()){
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
                    this.drowned.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.drowned.getX();
                double d1 = this.wantedY - this.drowned.getY();
                double d2 = this.wantedZ - this.drowned.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.drowned.setYRot(this.rotlerp(this.drowned.getYRot(), f, 90.0F));
                this.drowned.setYBodyRot(this.drowned.getYRot());
                float f1 = (float)(this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.drowned.getSpeed(), f1);
                this.drowned.setSpeed(f2);
                this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }

        }
    }

    static class SwimUpGoal extends Goal {
        private final DrownedServant drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public SwimUpGoal(DrownedServant p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
            this.drowned = p_i48908_1_;
            this.speedModifier = p_i48908_2_;
            this.seaLevel = p_i48908_4_;
        }

        public boolean canUse() {
            if (this.drowned.getTrueOwner() != null){
                if (this.drowned.getTrueOwner().isUnderWater()){
                    return false;
                }
            } else if (this.drowned.level.isDay()) {
                return false;
            }
            return this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
        }

        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        public void tick() {
            if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), (double)(this.seaLevel - 1), this.drowned.getZ()), (double)((float)Math.PI / 2F));
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }

                this.drowned.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }

        }

        public void start() {
            this.drowned.setSearchingForLand(true);
            this.stuck = false;
        }

        public void stop() {
            this.drowned.setSearchingForLand(false);
        }
    }

    static class TridentAttackGoal extends RangedAttackGoal {
        private final DrownedServant drowned;

        public TridentAttackGoal(RangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
            super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
            this.drowned = (DrownedServant)p_i48907_1_;
        }

        public boolean canUse() {
            return super.canUse() && this.drowned.getMainHandItem().getItem() == Items.TRIDENT;
        }

        public void start() {
            super.start();
            this.drowned.setAggressive(true);
            this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
        }

        public void stop() {
            super.stop();
            this.drowned.stopUsingItem();
            this.drowned.setAggressive(false);
        }
    }
}
