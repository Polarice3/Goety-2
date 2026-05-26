package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.ai.FloatAroundGoal;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MiniGhast extends Malghast {

    public MiniGhast(EntityType<? extends Malghast> type, Level worldIn) {
        super(type, worldIn);
    }

    public void addFlyingGoal(){
        this.goalSelector.addGoal(5, new FloatAroundGoal<>(this, 8.0F, 4, 0.25D));
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

    public void setGhastSpawn(){
        if (this.getTrueOwner() == null) {
            this.setBoundPos(this.blockPosition());
            this.setWandering(false);
            this.setStaying(false);
        }
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
                    fireball.setDamage(AttributesConfig.MiniGhastDamage.get().floatValue() + this.ghast.getFireBallDamage());
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
}
