package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class OwnedFlying extends Summoned {

    protected OwnedFlying(EntityType<? extends OwnedFlying> type, Level worldIn) {
        super(type, worldIn);
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        } else {
            BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.isOnGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.isOnGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.isOnGround()? 0.1F * f1 : 0.02F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double)f));
        }

        this.calculateEntityAnimation(this, false);
    }

    public boolean onClimbable() {
        return false;
    }
}
