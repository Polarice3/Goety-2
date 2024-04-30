package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.WebShot;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class WebSpiderServant extends SpiderServant implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING = SynchedEntityData.defineId(WebSpiderServant.class, EntityDataSerializers.BOOLEAN);

    public WebSpiderServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new WebShootGoal<>(this, 0.75D, 60, 15.0F));
    }

    @Override
    public void attackGoal(){
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !WebSpiderServant.this.isWebShooting()
                        && WebSpiderServant.this.getTarget() != null
                        && WebSpiderServant.this.getTarget().hasEffect(GoetyEffects.TANGLED.get());
            }
        });
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this, 1.25F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !WebSpiderServant.this.isWebShooting()
                        && WebSpiderServant.this.getTarget() != null
                        && WebSpiderServant.this.getTarget().hasEffect(GoetyEffects.TANGLED.get());
            }
        });
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEB_SHOOTING, false);
    }

    public void setWebShooting(boolean webShooting) {
        this.playSound(ModSounds.SPIDER_CALL.get(), this.getSoundVolume(), this.getVoicePitch());
        this.entityData.set(WEB_SHOOTING, webShooting);
    }

    public boolean isWebShooting() {
        return this.entityData.get(WEB_SHOOTING);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            if (this.getTarget() != null){
                if (!this.isWebShooting()){
                    if (!this.getTarget().hasEffect(GoetyEffects.TANGLED.get())) {
                        if (this.distanceTo(this.getTarget()) <= 5.0F) {
                            Vec3 vec3 = DefaultRandomPos.getPosAway(this, 7, 5, this.getTarget().position());
                            if (vec3 != null) {
                                this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        WebShot snowball = new WebShot(this, this.level);
        Vec3 vec3 = p_33317_.getDeltaMovement();
        double d0 = p_33317_.getX() + vec3.x - this.getX();
        double d1 = p_33317_.getEyeY() - (double)1.1F - this.getY();
        double d2 = p_33317_.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        snowball.setXRot(snowball.getXRot() - -20.0F);
        snowball.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
        this.playSound(ModSounds.SPIDER_SPIT.get(), 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(snowball);
    }

    public static class WebShootGoal<T extends WebSpiderServant> extends Goal {
        private final T mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private final int attackInterval;
        private final float attackRadius;

        public WebShootGoal(T mob, double speed, int attackInterval, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !livingentity.hasEffect(GoetyEffects.TANGLED.get())
                        && this.mob.distanceTo(livingentity) > 4.0F;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            this.mob.setWebShooting(true);
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTime = -1;
            this.mob.setWebShooting(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

                if (this.mob.distanceTo(this.target) <= this.attackRadius) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (--this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = this.attackInterval;
                } else if (this.attackTime < 0) {
                    this.attackTime = this.attackInterval;
                }
            }

        }
    }
}
