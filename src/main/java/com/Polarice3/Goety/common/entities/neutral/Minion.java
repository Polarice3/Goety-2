package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Minion extends Owned{
    public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(Minion.class, EntityDataSerializers.BYTE);
    @Nullable
    private BlockPos boundOrigin;

    public Minion(EntityType<? extends Minion> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.navigation = this.createNavigation(p_i50190_2_);
        this.moveControl = new MobUtil.MinionMoveControl(this);
    }

    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VEX_FLAGS, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag p_34008_) {
        super.readAdditionalSaveData(p_34008_);
        if (p_34008_.contains("BoundX")) {
            this.boundOrigin = new BlockPos(p_34008_.getInt("BoundX"), p_34008_.getInt("BoundY"), p_34008_.getInt("BoundZ"));
        }
    }

    public void addAdditionalSaveData(CompoundTag p_34015_) {
        super.addAdditionalSaveData(p_34015_);
        if (this.boundOrigin != null) {
            p_34015_.putInt("BoundX", this.boundOrigin.getX());
            p_34015_.putInt("BoundY", this.boundOrigin.getY());
            p_34015_.putInt("BoundZ", this.boundOrigin.getZ());
        }
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos p_34034_) {
        this.boundOrigin = p_34034_;
    }

    private boolean getVexFlag(int p_34011_) {
        int i = this.entityData.get(VEX_FLAGS);
        return (i & p_34011_) != 0;
    }

    private void setVexFlag(int p_33990_, boolean p_33991_) {
        int i = this.entityData.get(VEX_FLAGS);
        if (p_33991_) {
            i |= p_33990_;
        } else {
            i &= ~p_33990_;
        }

        this.entityData.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean p_34043_) {
        this.setVexFlag(1, p_34043_);
    }

    public void playChargeCry(){
        this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public class VexRandomMoveGoal extends Goal {
        public VexRandomMoveGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !Minion.this.getMoveControl().hasWanted() && Minion.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = Minion.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = Minion.this.blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(Minion.this.random.nextInt(15) - 7, Minion.this.random.nextInt(11) - 5, Minion.this.random.nextInt(15) - 7);
                if (Minion.this.level.isEmptyBlock(blockpos1)) {
                    Minion.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Minion.this.getTarget() == null) {
                        Minion.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    public class VexChargeAttackGoal extends Goal {
        public VexChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity livingentity = Minion.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && !Minion.this.getMoveControl().hasWanted() && Minion.this.random.nextInt(reducedTickDelay(7)) == 0) {
                return Minion.this.distanceToSqr(livingentity) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return Minion.this.getMoveControl().hasWanted() && Minion.this.isCharging() && Minion.this.getTarget() != null && Minion.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = Minion.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                Minion.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }

            Minion.this.setIsCharging(true);
            Minion.this.playChargeCry();
        }

        public void stop() {
            Minion.this.setIsCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = Minion.this.getTarget();
            if (livingentity != null) {
                if (Minion.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    Minion.this.doHurtTarget(livingentity);
                    Minion.this.setIsCharging(false);
                } else {
                    double d0 = Minion.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        Minion.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
                    }
                }

            }
        }
    }
}
