package com.Polarice3.Goety.common.entities.vehicle;

import com.Polarice3.Goety.common.entities.projectiles.SpellEntity;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Lists;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.List;

public class PioneerRock extends SpellEntity {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(PioneerRock.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(PioneerRock.class, EntityDataSerializers.FLOAT);
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;

    public PioneerRock(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.blocksBuilding = true;
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public boolean canCollideWith(Entity p_38376_) {
        return canVehicleCollide(this, p_38376_);
    }

    public static boolean canVehicleCollide(Entity p_38324_, Entity p_38325_) {
        return (p_38325_.canBeCollidedWith() || p_38325_.isPushable()) && !p_38324_.isPassengerOfSameVehicle(p_38325_);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    protected Vec3 getRelativePortalPosition(Direction.Axis p_38335_, BlockUtil.FoundRectangle p_38336_) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(p_38335_, p_38336_));
    }

    public void push(Entity p_38373_) {
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
    }

    public boolean hurt(DamageSource p_38319_, float p_38320_) {
        if (this.isInvulnerableTo(p_38319_)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + p_38320_ * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, p_38319_.getEntity());
            boolean flag = p_38319_.getEntity() instanceof Player && ((Player)p_38319_.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamage() > 40.0F) {
                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    public void lerpTo(double p_38299_, double p_38300_, double p_38301_, float p_38302_, float p_38303_, int p_38304_, boolean p_38305_) {
        this.lerpX = p_38299_;
        this.lerpY = p_38300_;
        this.lerpZ = p_38301_;
        this.lerpYRot = (double)p_38302_;
        this.lerpXRot = (double)p_38303_;
        this.lerpSteps = 10;
    }

    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    public void tick() {
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.tickLerp();
        if (this.isControlledByLocalInstance()) {
            if (this.level().isClientSide) {
                this.controlBoat();
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.checkInsideBlocks();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)d3 / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    private void controlBoat() {
        if (this.isVehicle()) {
            float f = 0.0F;
            if (this.inputLeft) {
                --this.deltaRotation;
            }

            if (this.inputRight) {
                ++this.deltaRotation;
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                f += 0.005F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.inputUp) {
                f += 0.04F;
            }

            if (this.inputDown) {
                f -= 0.005F;
            }

            this.setDeltaMovement(this.getDeltaMovement().add((double)(Mth.sin(-this.getYRot() * ((float)Math.PI / 180F)) * f), 0.0D, (double)(Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f)));
        }
    }

    protected float getSinglePassengerXOffset() {
        return 0.0F;
    }

    public boolean hasEnoughSpaceFor(Entity p_273171_) {
        return p_273171_.getBbWidth() < this.getBbWidth();
    }

    protected void positionRider(Entity p_289552_, Entity.MoveFunction p_289571_) {
        if (this.hasPassenger(p_289552_)) {
            float f = this.getSinglePassengerXOffset();
            float f1 = (float)((this.isRemoved() ? (double)0.01F : this.getPassengersRidingOffset()) + p_289552_.getMyRidingOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(p_289552_);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (p_289552_ instanceof Animal) {
                    f += 0.2F;
                }
            }

            Vec3 vec3 = (new Vec3((double)f, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
            p_289571_.accept(p_289552_, this.getX() + vec3.x, this.getY() + (double)f1, this.getZ() + vec3.z);
            p_289552_.setYRot(p_289552_.getYRot() + this.deltaRotation);
            p_289552_.setYHeadRot(p_289552_.getYHeadRot() + this.deltaRotation);
            this.clampRotation(p_289552_);
            if (p_289552_ instanceof Animal && this.getPassengers().size() == this.getMaxPassengers()) {
                int j = p_289552_.getId() % 2 == 0 ? 90 : 270;
                p_289552_.setYBodyRot(((Animal)p_289552_).yBodyRot + (float)j);
                p_289552_.setYHeadRot(p_289552_.getYHeadRot() + (float)j);
            }

        }
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity p_38357_) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector((double)(this.getBbWidth() * Mth.SQRT_OF_TWO), (double)p_38357_.getBbWidth(), p_38357_.getYRot());
        double d0 = this.getX() + vec3.x;
        double d1 = this.getZ() + vec3.z;
        BlockPos blockpos = BlockPos.containing(d0, this.getBoundingBox().maxY, d1);
        BlockPos blockpos1 = blockpos.below();
        if (!this.level().isWaterAt(blockpos1)) {
            List<Vec3> list = Lists.newArrayList();
            double d2 = this.level().getBlockFloorHeight(blockpos);
            if (DismountHelper.isBlockFloorValid(d2)) {
                list.add(new Vec3(d0, (double)blockpos.getY() + d2, d1));
            }

            double d3 = this.level().getBlockFloorHeight(blockpos1);
            if (DismountHelper.isBlockFloorValid(d3)) {
                list.add(new Vec3(d0, (double)blockpos1.getY() + d3, d1));
            }

            for(Pose pose : p_38357_.getDismountPoses()) {
                for(Vec3 vec31 : list) {
                    if (DismountHelper.canDismountTo(this.level(), vec31, p_38357_, pose)) {
                        p_38357_.setPose(pose);
                        return vec31;
                    }
                }
            }
        }

        return super.getDismountLocationForPassenger(p_38357_);
    }

    protected void clampRotation(Entity p_38322_) {
        p_38322_.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(p_38322_.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        p_38322_.yRotO += f1 - f;
        p_38322_.setYRot(p_38322_.getYRot() + f1 - f);
        p_38322_.setYHeadRot(p_38322_.getYRot());
    }

    public void onPassengerTurned(Entity p_38383_) {
        this.clampRotation(p_38383_);
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    public InteractionResult interact(Player p_38330_, InteractionHand p_38331_) {
        if (!p_38330_.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                if (this.getOwner() != null) {
                    if (this.getOwner() == p_38330_ || MobUtil.areAllies(this.getOwner(), p_38330_)) {
                        if (p_38330_.startRiding(this)) {
                            return InteractionResult.CONSUME;
                        }
                    }
                } else {
                    return p_38330_.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
                }
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void setDamage(float p_38312_) {
        this.entityData.set(DATA_ID_DAMAGE, p_38312_);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setHurtTime(int p_38355_) {
        this.entityData.set(DATA_ID_HURT, p_38355_);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    protected boolean canAddPassenger(Entity p_38390_) {
        return this.getPassengers().size() < this.getMaxPassengers();
    }

    protected int getMaxPassengers() {
        return 2;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        LivingEntity livingentity1;
        if (entity instanceof LivingEntity livingentity) {
            livingentity1 = livingentity;
        } else {
            livingentity1 = null;
        }

        return livingentity1;
    }

    public void setInput(boolean p_38343_, boolean p_38344_, boolean p_38345_, boolean p_38346_) {
        this.inputLeft = p_38343_;
        this.inputRight = p_38344_;
        this.inputUp = p_38345_;
        this.inputDown = p_38346_;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
}
