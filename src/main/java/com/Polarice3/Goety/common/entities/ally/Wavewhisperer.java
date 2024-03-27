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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class Wavewhisperer extends Whisperer{
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public Wavewhisperer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, worldIn);
        this.groundNavigation = new GroundPathNavigation(this, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new GoToWaterGoal(this, 1.0F));
        this.goalSelector.addGoal(8, new WaterWanderGoal(this){
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
                this.hurt(DamageSource.DRY_OUT, 2.0F);
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

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
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
            if (this.wavewhisperer.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.wavewhisperer.getY()) {
                    this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                } else if (owner != null && owner.getY() > this.wavewhisperer.getY()){
                    this.wavewhisperer.setDeltaMovement(this.wavewhisperer.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.wavewhisperer.getNavigation().isDone()) {
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
                if (!this.wavewhisperer.onGround) {
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

    protected boolean isAffectedByFluids() {
        return false;
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

}
