package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MiniGhast extends Malghast {
    public float fireBallDamage = AttributesConfig.MiniGhastDamage.get().floatValue();

    public MiniGhast(EntityType<? extends Malghast> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public void addFlyingGoal(){
        this.goalSelector.addGoal(5, new FlyingGoal(this));
    }

    public void addFireballGoal(){
        this.goalSelector.addGoal(7, new FireballAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MiniGhastHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.MiniGhastHealth.get());
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.8F;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    public float getVoicePitch() {
        return 1.5F;
    }

    public float getFireBallDamage(){
        return this.fireBallDamage;
    }

    public void setFireBallDamage(float damage){
        this.fireBallDamage = damage;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("FireballDamage", this.getFireBallDamage());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("FireballDamage")) {
            this.setFireBallDamage(pCompound.getFloat("FireballDamage"));
        }
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.playSound(ModSounds.GHAST_DISAPPEAR.get());
        this.discard();
    }

    static class FireballAttackGoal extends Goal {
        private final MiniGhast ghast;
        public int chargeTime;
        public boolean shotTimes;

        public FireballAttackGoal(MiniGhast p_i45837_1_) {
            this.ghast = p_i45837_1_;
        }

        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        public void start() {
            this.chargeTime = 0;
            this.shotTimes = true;
        }

        public void stop() {
            this.ghast.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = this.ghast.getTarget();
            float d0 = 64.0F;
            if (livingentity != null && livingentity.distanceToSqr(this.ghast) < Mth.square(d0) && this.ghast.hasLineOfSight(livingentity)) {
                Level world = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10) {
                    this.shotTimes = this.ghast.random.nextFloat() >= 0.25F;
                    if (!this.ghast.isSilent()) {
                        this.ghast.playSound(SoundEvents.GHAST_WARN, 2.0F, (this.ghast.random.nextFloat() - this.ghast.random.nextFloat()) * 0.2F + this.ghast.getVoicePitch());
                    }
                }

                if (this.chargeTime == 20) {
                    double d1 = 2.0D;
                    Vec3 vector3d = this.ghast.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.ghast.getX() + vector3d.x * d1);
                    double d3 = livingentity.getY(0.5D) - this.ghast.getY(0.5D);
                    double d4 = livingentity.getZ() - (this.ghast.getZ() + vector3d.z * d1);
                    if (!this.ghast.isSilent()) {
                        this.ghast.playSound(SoundEvents.GHAST_SHOOT, 2.0F, (this.ghast.random.nextFloat() - this.ghast.random.nextFloat()) * 0.2F + 1.0F);
                    }
                    ModFireball fireball = new ModFireball(world, this.ghast, d2, d3, d4);
                    double y = this.ghast.getY() <= livingentity.getEyeY() ? this.ghast.getY(0.5D) : this.ghast.getY();
                    fireball.setDangerous(false);
                    fireball.setDamage(this.ghast.getFireBallDamage());
                    fireball.setPos(this.ghast.getX() + vector3d.x * d1, y, fireball.getZ() + vector3d.z * d1);
                    world.addFreshEntity(fireball);
                    this.chargeTime = 0;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            this.ghast.setCharging(this.chargeTime > 10);
        }
    }

    static class FlyingGoal extends Goal {
        private final MiniGhast ghast;

        public FlyingGoal(MiniGhast p_i45836_1_) {
            this.ghast = p_i45836_1_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl moveControl = this.ghast.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            } else {
                double d0 = moveControl.getWantedX() - this.ghast.getX();
                double d1 = moveControl.getWantedY() - this.ghast.getY();
                double d2 = moveControl.getWantedZ() - this.ghast.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.ghast.getRandom();
            float distance = 8.0F;
            BlockPos blockPos = this.ghast.blockPosition();
            if (this.ghast.getTrueOwner() != null){
                blockPos = this.ghast.getTrueOwner().blockPosition().above(3);
            } else if (this.ghast.getTarget() != null){
                blockPos = this.ghast.getTarget().blockPosition().above(3);
            } else if (this.ghast.getBoundOrigin() != null){
                blockPos = this.ghast.getBoundOrigin();
            }

            if (blockPos != null) {
                if (this.ghast.distanceToSqr(Vec3.atCenterOf(blockPos)) < Mth.square(distance)) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos);
                    double X = this.ghast.getX() + vector3d.x * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Y = this.ghast.getY() + vector3d.y * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;
                    double Z = this.ghast.getZ() + vector3d.z * distance + (random.nextFloat() * 2.0F - 1.0F) * distance;

                    this.ghast.getMoveControl().setWantedPosition(X, Y, Z, 0.25D);
                } else {
                    this.ghast.getMoveControl().setWantedPosition(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, 0.25D);
                }
            } else {
                double d0 = this.ghast.getX() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d1 = this.ghast.getY() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                double d2 = this.ghast.getZ() + (random.nextFloat() * 2.0F - 1.0F) * distance;
                this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, 0.25D);
            }
        }
    }
}
