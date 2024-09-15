package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ai.MinionFollowGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.entities.projectiles.SoulBullet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AllyIrk extends Minion {
    private int shootTime;

    public AllyIrk(EntityType<? extends AllyIrk> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.shootTime = 0;
        this.navigation = this.createNavigation(p_i50190_2_);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public void tick() {
        super.tick();
        if (this.shootTime > 0){
            --this.shootTime;
        }
        if (this.getTrueOwner() != null){
            if (this.getTrueOwner().isDeadOrDying()){
                this.kill();
            }
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AllyIrk.OutofBoundsGoal());
        this.goalSelector.addGoal(2, new MinionFollowGoal(this, 0.5D, 6.0f, 3.0f, true));
        this.goalSelector.addGoal(4, new AllyIrk.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new AllyIrk.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    public void die(DamageSource cause) {
        if (cause.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.heal(2.0F);
        }
        super.die(cause);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.shootTime = compound.getInt("shootTime");

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("shootTime", this.shootTime);

    }

    public void setLimitedLife(int limitedLifeTicksIn) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VEX_HURT;
    }

    class OutofBoundsGoal extends Goal {
        public OutofBoundsGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return AllyIrk.this.isInWall() && !AllyIrk.this.getMoveControl().hasWanted();
        }

        public boolean canContinueToUse() {
            return AllyIrk.this.isInWall() && !AllyIrk.this.getMoveControl().hasWanted();
        }

        public void tick() {
            BlockPos.MutableBlockPos blockpos$mutable = AllyIrk.this.blockPosition().mutable();
            blockpos$mutable.setY(AllyIrk.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
            AllyIrk.this.getMoveControl().setWantedPosition(blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ(), 1.0F);
        }

    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (AllyIrk.this.getTarget() != null && !AllyIrk.this.getMoveControl().hasWanted()) {
                return !AllyIrk.this.getTarget().isAlliedTo(AllyIrk.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return AllyIrk.this.getMoveControl().hasWanted()
                    && AllyIrk.this.isCharging()
                    && AllyIrk.this.getTarget() != null
                    && AllyIrk.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = AllyIrk.this.getTarget();
            assert livingentity != null;
            Vec3 vector3d = livingentity.getEyePosition(1.0F);
            if (AllyIrk.this.distanceTo(livingentity) > 4.0F) {
                AllyIrk.this.getMoveControl().setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0F);
            }
        }

        public void stop() {
            AllyIrk.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = AllyIrk.this.getTarget();
            if (livingentity != null) {
                if (AllyIrk.this.shootTime == 10) {
                    double d1 = livingentity.getX() - AllyIrk.this.getX();
                    double d2 = livingentity.getY(0.5D) - AllyIrk.this.getY(0.5D);
                    double d3 = livingentity.getZ() - AllyIrk.this.getZ();
                    SoulBullet smallFireballEntity = new SoulBullet(AllyIrk.this.level, AllyIrk.this, d1, d2, d3);
                    smallFireballEntity.setPos(smallFireballEntity.getX(), AllyIrk.this.getY(0.5D), smallFireballEntity.getZ());
                    AllyIrk.this.level.addFreshEntity(smallFireballEntity);
                    AllyIrk.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 2.0F);
                }
                AllyIrk.this.setIsCharging(AllyIrk.this.shootTime <= 10);
                if (AllyIrk.this.shootTime == 0){
                    AllyIrk.this.shootTime = 20;
                } else {
                    Vec3 vector3d0 = livingentity.getEyePosition(1.0F);
                    Vec3 vector3d = AllyIrk.this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                    double d0 = vector3d.y;
                    if (AllyIrk.this.getY() < vector3d0.y) {
                        d0 = Math.max(0.0D, d0);
                        d0 = d0 + (0.3D - d0 * (double)0.6F);
                    }

                    vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                    Vec3 vector3d1 = new Vec3(vector3d0.x - AllyIrk.this.getX(), 0.0D, vector3d0.z - AllyIrk.this.getZ());
                    if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                        Vec3 vector3d2 = vector3d1.normalize();
                        vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
                    }
                    AllyIrk.this.setDeltaMovement(vector3d);
                }
                double d2 = AllyIrk.this.getTarget().getX() - AllyIrk.this.getX();
                double d1 = AllyIrk.this.getTarget().getZ() - AllyIrk.this.getZ();
                AllyIrk.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                AllyIrk.this.yBodyRot = AllyIrk.this.getYRot();
            } else {
                AllyIrk.this.setIsCharging(false);
            }
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !AllyIrk.this.getMoveControl().hasWanted()
                    && AllyIrk.this.random.nextInt(7) == 0
                    && !AllyIrk.this.isCharging();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = AllyIrk.this.blockPosition();

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(AllyIrk.this.random.nextInt(8) - 4, AllyIrk.this.random.nextInt(6) - 2, AllyIrk.this.random.nextInt(8) - 4);
                if (AllyIrk.this.level.isEmptyBlock(blockpos1)) {
                    AllyIrk.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (AllyIrk.this.getTarget() == null) {
                        AllyIrk.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
