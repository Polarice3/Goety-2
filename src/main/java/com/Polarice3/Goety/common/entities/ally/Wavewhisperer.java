package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class Wavewhisperer extends Whisperer{
    private boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public Wavewhisperer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxUpStep(1.0F);
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, worldIn);
        this.groundNavigation = new GroundPathNavigation(this, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0F));
        this.goalSelector.addGoal(1, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new GoToBeachGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new SwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
        this.goalSelector.addGoal(8, new WaterWanderGoal<>(this){
            @Override
            public boolean canUse() {
                return super.canUse() && Wavewhisperer.this.getTarget() == null;
            }
        });
    }

    protected void handleAirSupply(int p_30344_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_30344_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
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

    private boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else if (this.getTarget() != null && this.getTarget().isInWater()) {
            return true;
        } else {
            return this.getTrueOwner() != null && this.getTrueOwner().isInWater();
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

    protected boolean convertsInWater() {
        return false;
    }

    static class MoveHelperController extends MoveControl {
        private final Wavewhisperer wavewhisperer;

        public MoveHelperController(Wavewhisperer p_i48909_1_) {
            super(p_i48909_1_);
            this.wavewhisperer = p_i48909_1_;
        }

        public void tick() {
            LivingEntity livingentity = this.wavewhisperer.getTarget();
            LivingEntity owner = this.wavewhisperer.getTrueOwner();
            if (this.wavewhisperer.wantsToSwim() && this.wavewhisperer.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.wavewhisperer.getY() || this.wavewhisperer.searchingForLand) {
                    this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                } else if (owner != null && owner.getY() > this.wavewhisperer.getY()){
                    this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != Operation.MOVE_TO || this.wavewhisperer.getNavigation().isDone()) {
                    this.wavewhisperer.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.wavewhisperer.getX();
                double d1 = this.wantedY - this.wavewhisperer.getY();
                double d2 = this.wantedZ - this.wavewhisperer.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.wavewhisperer.setYRot(this.rotlerp(this.wavewhisperer.getYRot(), f, 90.0F));
                this.wavewhisperer.setYBodyRot(this.wavewhisperer.getYRot());
                float f1 = (float)(this.speedModifier * this.wavewhisperer.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.wavewhisperer.getSpeed(), f1);
                this.wavewhisperer.setSpeed(f2);
                this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                if (!this.wavewhisperer.onGround()) {
                    this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }

        }
    }

    public boolean canDrownInFluidType(FluidType type) {
        return type != ForgeMod.WATER_TYPE.get();
    }

    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WAVEWHISPERER_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.WAVEWHISPERER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WAVEWHISPERER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(ModSounds.WAVEWHISPERER_STEP.get(), 0.15F, 1.0F);
    }

    protected SoundEvent getAttackSound(){
        return ModSounds.WAVEWHISPERER_ATTACK.get();
    }

    protected SoundEvent getSummonSound(){
        return ModSounds.WAVEWHISPERER_SUMMON.get();
    }

    protected SoundEvent getSummonPoisonSound(){
        return ModSounds.WAVEWHISPERER_SUMMON_POISON.get();
    }

    protected SoundEvent getSummonThornsSound(){
        return ModSounds.WAVEWHISPERER_SUMMON_THORNS.get();
    }

    protected EntityType<? extends AbstractMonolith> getVines(){
        return ModEntityType.QUICK_GROWING_KELP.get();
    }

    protected EntityType<? extends AbstractMonolith> getPoison(){
        return ModEntityType.POISON_ANEMONE.get();
    }

    public void setSearchingForLand(boolean p_204713_1_) {
        this.searchingForLand = p_204713_1_;
    }

    static class SwimUpGoal extends Goal {
        private final Wavewhisperer whisperer;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public SwimUpGoal(Wavewhisperer p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
            this.whisperer = p_i48908_1_;
            this.speedModifier = p_i48908_2_;
            this.seaLevel = p_i48908_4_;
        }

        public boolean canUse() {
            if (this.whisperer.getTrueOwner() != null){
                if (this.whisperer.getTrueOwner().isUnderWater()){
                    return false;
                }
            } else {
                return false;
            }
            return this.whisperer.isInWater() && this.whisperer.getY() < (double)(this.seaLevel - 2);
        }

        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        public void tick() {
            if (this.whisperer.getY() < (double)(this.seaLevel - 1) && (this.whisperer.getNavigation().isDone() || this.whisperer.closeToNextPos())) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.whisperer, 4, 8, new Vec3(this.whisperer.getX(), (double)(this.seaLevel - 1), this.whisperer.getZ()), (double)((float)Math.PI / 2F));
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }

                this.whisperer.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }

        }

        public void start() {
            this.whisperer.setSearchingForLand(true);
            this.stuck = false;
        }

        public void stop() {
            this.whisperer.setSearchingForLand(false);
        }
    }

    static class GoToBeachGoal extends MoveToBlockGoal {
        private final Wavewhisperer wavewhisperer;

        public GoToBeachGoal(Wavewhisperer p_i48911_1_, double p_i48911_2_) {
            super(p_i48911_1_, p_i48911_2_, 8, 2);
            this.wavewhisperer = p_i48911_1_;
        }

        public boolean canUse() {
            if (this.wavewhisperer.getTrueOwner() != null){
                if (this.wavewhisperer.getTrueOwner().isUnderWater()){
                    return false;
                }
            } else {
                return false;
            }
            return super.canUse() && this.wavewhisperer.isInWater() && this.wavewhisperer.getY() >= (double)(this.wavewhisperer.level.getSeaLevel() - 3);
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            BlockPos blockpos = pPos.above();
            return pLevel.isEmptyBlock(blockpos) && pLevel.isEmptyBlock(blockpos.above()) && pLevel.getBlockState(pPos).entityCanStandOn(pLevel, pPos, this.wavewhisperer);
        }

        public void start() {
            this.wavewhisperer.setSearchingForLand(false);
            this.wavewhisperer.navigation = this.wavewhisperer.groundNavigation;
            super.start();
        }

        public void stop() {
            super.stop();
        }
    }
}
