package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class Specter extends Summoned {
    public Specter(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new SpecterMoveControl(this);
        this.noPhysics = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new SpecterAttackGoal(this));
        this.goalSelector.addGoal(5, new FlyTowardsTargetGoal(this));
        this.goalSelector.addGoal(6, new FlyingGoal(this));
        this.goalSelector.addGoal(7, new SpecterLookGoal(this));
    }

    public void followGoal(){
    }

    public void targetSelectGoal(){
        this.targetSelector.addGoal(1, new SummonTargetGoal(this, false, false));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    @Override
    public boolean isSteppingCarefully() {
        return true;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return super.canBeAffected(pPotioneffect) && pPotioneffect.getEffect() != MobEffects.WITHER;
    }

    public double getMeleeAttackRangeSqr(LivingEntity p_147273_) {
        return (double)(this.getBbWidth() * this.getBbWidth() + p_147273_.getBbWidth());
    }

    public static class SpecterMoveControl extends MoveControl {
        private final LivingEntity specter;
        private int coolDown;

        public SpecterMoveControl(Mob entity) {
            super(entity);
            this.specter = entity;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double dx = this.getWantedX() - this.specter.getX();
                double dy = this.getWantedY() - this.specter.getY();
                double dz = this.getWantedZ() - this.specter.getZ();
                double dist = dx * dx + dy * dy + dz * dz;

                if (this.coolDown-- <= 0) {
                    this.coolDown += this.specter.getRandom().nextInt(5) + 2;
                    dist = Mth.sqrt((float) dist);

                    this.specter.setDeltaMovement(this.specter.getDeltaMovement().add((dx / dist * 0.1D) * this.speedModifier, (dy / dist * 0.1D) * this.speedModifier, (dz / dist * 0.1D) * speedModifier));
                }
            }
        }
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
            BlockPos ground = BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround()) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double)f));
        }

        this.calculateEntityAnimation(false);
    }

    public boolean onClimbable() {
        return false;
    }

    public static class FlyTowardsTargetGoal extends Goal {
        private final Specter specter;

        public FlyTowardsTargetGoal(Specter specter) {
            this.specter = specter;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return specter.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = specter.getTarget();
            if (target != null) {
                specter.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.5F);
            }
        }
    }

    static class FlyingGoal extends Goal {
        private final Specter specter;

        public FlyingGoal(Specter specter) {
            this.specter = specter;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MoveControl moveControl = this.specter.getMoveControl();
            if (this.specter.getTarget() != null
                    || !this.specter.isWithinRestriction()) {
                return false;
            }
            double d0 = moveControl.getWantedX() - this.specter.getX();
            double d1 = moveControl.getWantedY() - this.specter.getY();
            double d2 = moveControl.getWantedZ() - this.specter.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.specter.getRandom();
            float distance = 16.0F;
            BlockPos blockPos = null;
            if (this.specter.getBoundPos() != null){
                blockPos = this.specter.getBoundPos();
            } else if (this.specter.getTrueOwner() != null){
                blockPos = this.specter.getTrueOwner().blockPosition().above(4);
            }

            if (blockPos != null) {
                if (this.specter.distanceToSqr(Vec3.atCenterOf(blockPos)) < Mth.square(distance)) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos);
                    double X = this.specter.getX() + vector3d.x * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Y = this.specter.getY() + vector3d.y * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Z = this.specter.getZ() + vector3d.z * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;

                    this.specter.getMoveControl().setWantedPosition(X, Y, Z, 0.25D);
                } else {
                    this.specter.getMoveControl().setWantedPosition(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, 0.25D);
                }
            } else {
                double d0 = this.specter.getX() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d1 = this.specter.getY() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d2 = this.specter.getZ() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                this.specter.getMoveControl().setWantedPosition(d0, d1, d2, 0.25D);
            }
        }
    }

    public static class SpecterLookGoal extends Goal {
        private final Specter specter;

        public SpecterLookGoal(Specter specter) {
            this.specter = specter;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.specter.getTarget() == null) {
                this.specter.setYRot(-((float) Mth.atan2(this.specter.getDeltaMovement().x(), this.specter.getDeltaMovement().z())) * (180F / (float) Math.PI));
                this.specter.setYBodyRot(this.specter.getYRot());
            } else {
                LivingEntity target = this.specter.getTarget();
                if (target.distanceToSqr(this.specter) < Mth.square(64)) {
                    double d1 = target.getX() - this.specter.getX();
                    double d2 = target.getZ() - this.specter.getZ();
                    this.specter.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.specter.setYBodyRot(this.specter.getYRot());
                }
            }
        }
    }

    public static class SpecterAttackGoal extends Goal {
        private final Mob mob;
        private int attackTick;

        public SpecterAttackGoal(Mob mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && this.mob.isWithinMeleeAttackRange(target);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            this.attackTick = 0;
        }

        @Override
        public void tick() {
            if (--this.attackTick <= 0) {
                LivingEntity livingentity = this.mob.getTarget();
                if (livingentity == null) {
                    this.stop();
                    return;
                }
                this.checkAndPerformAttack(livingentity);
            }
        }

        protected void checkAndPerformAttack(LivingEntity entity) {
            if (this.attackTick <= 0 && this.mob.isWithinMeleeAttackRange(entity) && this.mob.hasLineOfSight(entity)) {
                this.attackTick = this.adjustedTickDelay(20);
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(entity);
            }
        }
    }
}
