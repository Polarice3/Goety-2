package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.WebShot;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public class WebSpiderServant extends SpiderServant implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING = SynchedEntityData.defineId(WebSpiderServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(WebSpiderServant.class, EntityDataSerializers.INT);
    public static AttributeModifier SHOOT_SPEED_MODIFIER = new AttributeModifier(UUID.fromString("fb1bebf4-834b-49d6-9ac6-ddd64dd93332"), "Shooting speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);
    public static String IDLE = "idle";
    public static String SHOOT = "shoot";
    public boolean isFleeing;
    public boolean stopMoving;
    public int shootAnim;
    public AnimationState shootAnimationState = new AnimationState();

    public WebSpiderServant(EntityType<? extends SpiderServant> type, Level worldIn) {
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
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this, 1.5F){
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
        this.entityData.define(ANIM_STATE, 0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return SpiderServant.setCustomAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WebSpiderServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WebSpiderServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WebSpiderServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WebSpiderServantDamage.get());
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, SHOOT)){
            return 1;
        } else {
            return 0;
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        this.shootAnimationState.stop();
                        break;
                    case 1:
                        this.shootAnimationState.start(this.tickCount);
                        break;
                }
            }
        }
    }

    public void setWebShooting(boolean webShooting) {
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
                    this.stopMoving = false;
                    if (!this.getTarget().hasEffect(GoetyEffects.TANGLED.get())) {
                        if (this.distanceTo(this.getTarget()) <= 5.0F) {
                            this.isFleeing = true;
                            Vec3 vec3 = DefaultRandomPos.getPosAway(this, 7, 5, this.getTarget().position());
                            if (vec3 != null) {
                                this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.25F);
                            }
                        } else {
                            this.isFleeing = false;
                        }
                    } else {
                        this.isFleeing = false;
                        this.setClimbing(false);
                    }
                } else {
                    this.isFleeing = false;
                }
            } else {
                this.isFleeing = false;
                this.stopMoving = false;
            }
            AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (this.stopMoving) {
                if (modifiableattributeinstance != null) {
                    modifiableattributeinstance.removeModifier(SHOOT_SPEED_MODIFIER);
                    modifiableattributeinstance.addTransientModifier(SHOOT_SPEED_MODIFIER);
                }
            } else {
                if (modifiableattributeinstance != null) {
                    if (modifiableattributeinstance.hasModifier(SHOOT_SPEED_MODIFIER)) {
                        modifiableattributeinstance.removeModifier(SHOOT_SPEED_MODIFIER);
                    }
                }
            }
            if (this.shootAnim > 0) {
                --this.shootAnim;
            } else {
                this.setAnimationState(IDLE);
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
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null
                    && livingentity.isAlive()
                    && !this.mob.isFleeing) {
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
            this.mob.stopMoving = false;
            this.mob.setWebShooting(false);
            this.mob.setAnimationState(IDLE);
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
                    if (!this.mob.stopMoving) {
                        this.mob.playSound(ModSounds.SPIDER_CALL.get(), this.mob.getSoundVolume(), this.mob.getVoicePitch());
                    }
                    this.mob.stopMoving = true;
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                    this.mob.stopMoving = false;
                }

                if (this.mob.stopMoving) {
                    MobUtil.instaLook(this.mob, this.target);
                } else {
                    this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                }
                --this.attackTime;
                if (this.attackTime == this.attackInterval - MathHelper.secondsToTicks(0.75F)) {
                    if (!flag) {
                        return;
                    }

                    this.mob.setAnimationState(SHOOT);
                    this.mob.shootAnim = MathHelper.secondsToTicks(0.56F) + 5;
                } else if (this.attackTime == this.attackInterval - MathHelper.secondsToTicks(0.75F + 0.36F)) {
                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                } else if (this.attackTime <= 0) {
                    this.attackTime = this.attackInterval;
                }
            }

        }
    }
}
