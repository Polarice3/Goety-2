package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public class WebSpider extends Spider implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> WEB_SHOOTING = SynchedEntityData.defineId(WebSpider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(WebSpider.class, EntityDataSerializers.INT);
    public static AttributeModifier SHOOT_SPEED_MODIFIER = new AttributeModifier(UUID.fromString("b255663a-e3e3-4660-9ce2-1c76c5f98e72"), "Shooting speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);
    public static String IDLE = "idle";
    public static String SHOOT = "shoot";
    public boolean isFleeing;
    public boolean stopMoving;
    public int shootAnim;
    public AnimationState shootAnimationState = new AnimationState();

    public WebSpider(EntityType<? extends Spider> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WebShootGoal<>(this, 0.75D, 60, 15.0F));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !WebSpider.this.isWebShooting()
                        && WebSpider.this.getTarget() != null
                        && WebSpider.this.getTarget().hasEffect(GoetyEffects.TANGLED.get());
            }
        });
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SpiderTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new SpiderTargetGoal<>(this, IronGolem.class));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return SpiderServant.setCustomAttributes()
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WebSpiderServantDamage.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.WebSpiderServantHealth.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WebSpiderServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WebSpiderServantHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEB_SHOOTING, false);
        this.entityData.define(ANIM_STATE, 0);
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

    public static class WebShootGoal<T extends WebSpider> extends Goal {
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

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public WebSpider webSpider;

        public SpiderAttackGoal(WebSpider p_33822_) {
            super(p_33822_, 1.5F, true);
            this.webSpider = p_33822_;
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                    && !this.mob.isVehicle()
                    && !this.webSpider.isWebShooting()
                    && this.mob.getTarget() != null
                    && this.mob.getTarget().hasEffect(GoetyEffects.TANGLED.get());
        }

        public boolean canContinueToUse() {
            float f = this.mob.getLightLevelDependentMagicValue();
            if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget((LivingEntity)null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }

        protected double getAttackReachSqr(LivingEntity p_33825_) {
            return (double)(4.0F + p_33825_.getBbWidth());
        }
    }

    static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public SpiderTargetGoal(Spider p_33832_, Class<T> p_33833_) {
            super(p_33832_, p_33833_, true);
        }

        public boolean canUse() {
            float f = this.mob.getLightLevelDependentMagicValue();
            return f >= 0.5F ? false : super.canUse();
        }
    }
}
