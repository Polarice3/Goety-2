package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.BoneShard;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class BoneSpiderServant extends SpiderServant implements RangedAttackMob {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BoneSpiderServant.class, EntityDataSerializers.INT);
    public static String ATTACK = "attack";
    public int attackAnim;
    public AnimationState attackAnimationState = new AnimationState();

    public BoneSpiderServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ShootBoneGoal(this, 1.0D, 50, 16.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return SpiderServant.setCustomAttributes()
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoneSpiderServantDamage.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoneSpiderServantHealth.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoneSpiderServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BoneSpiderServantDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, ATTACK)){
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
                        this.attackAnimationState.stop();
                        break;
                    case 1:
                        this.attackAnimationState.start(this.tickCount);
                        break;
                }
            }
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BONE_SPIDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33814_) {
        return ModSounds.BONE_SPIDER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BONE_SPIDER_DEATH.get();
    }

    protected void playStepSound(BlockPos p_33804_, BlockState p_33805_) {
        this.playSound(ModSounds.BONE_SPIDER_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (this.getCurrentAnimation() == this.getAnimationState(ATTACK)){
                this.getNavigation().stop();
                if (modifiableattributeinstance != null) {
                    if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                        modifiableattributeinstance.removeModifier(STOP_MODIFIER);
                        modifiableattributeinstance.addTransientModifier(STOP_MODIFIER);
                    }
                }
            } else {
                if (modifiableattributeinstance != null) {
                    if (modifiableattributeinstance.hasModifier(STOP_MODIFIER)) {
                        modifiableattributeinstance.removeModifier(STOP_MODIFIER);
                    }
                }
            }
            if (this.attackAnim > 0) {
                --this.attackAnim;
            } else {
                this.setAnimationState("");
            }
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32259_, DifficultyInstance p_32260_, MobSpawnType p_32261_, @Nullable SpawnGroupData p_32262_, @Nullable CompoundTag p_32263_) {
        return p_32262_;
    }

    protected float getStandingEyeHeight(Pose p_32265_, EntityDimensions p_32266_) {
        return 0.45F;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float p_33318_) {
        BoneShard boneShard = new BoneShard(this, this.level);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - boneShard.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        boneShard.setBaseDamage(boneShard.getBaseDamage() + AttributesConfig.BoneSpiderServantRangeDamage.get());
        boneShard.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.level.addFreshEntity(boneShard);
    }

    static class ShootBoneGoal extends Goal {
        public BoneSpiderServant servant;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private final int attackInterval;
        private final float attackRadius;
        private final double attackRadiusSqr;

        public ShootBoneGoal(BoneSpiderServant mob, double speed, int attackInterval, float attackRadius) {
            this.servant = mob;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity living = this.servant.getTarget();
            if (living != null && living.isAlive()){
                this.target = living;
                return living.distanceTo(this.servant) >= 4.0F;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.servant.getCurrentAnimation() == this.servant.getAnimationState(ATTACK));
        }

        @Override
        public void start() {
            this.target = null;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target != null){
                double d0 = this.servant.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.servant.getSensing().hasLineOfSight(this.target);

                if (d0 <= this.attackRadiusSqr) {
                    this.servant.getNavigation().stop();
                    if (flag) {
                        --this.attackTime;
                    }
                } else {
                    this.servant.attackAnim = 0;
                    this.servant.setAnimationState("");
                    this.servant.getNavigation().moveTo(this.target, this.speedModifier);
                }
                this.servant.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

                if (this.attackTime == 20){
                    this.servant.setAnimationState(ATTACK);
                    this.servant.attackAnim = MathHelper.secondsToTicks(2) + 5;
                    this.servant.playSound(ModSounds.BONE_SPIDER_SPIT.get(), this.servant.getSoundVolume(), this.servant.getVoicePitch());
                }
                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float)Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.servant.performRangedAttack(this.target, f1);
                    this.attackTime = this.attackInterval;
                } else if (this.attackTime < 0) {
                    this.attackTime = this.attackInterval;
                }
            }
        }
    }
}
