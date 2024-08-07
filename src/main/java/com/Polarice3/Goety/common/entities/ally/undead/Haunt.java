package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ai.NeutralZombieAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Haunt extends Summoned {
    public Haunt(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new NeutralZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void followGoal(){
        this.goalSelector.addGoal(8, new FollowOwnerGoal<>(this, 1.0D, 20.0F, 2.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HauntHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.HauntArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.HauntFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.HauntDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HauntHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.HauntArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.HauntDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.HauntFollowRange.get());
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.9F));
            }
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.HAUNT_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.HAUNT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.HAUNT_HURT.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.HAUNT_FLY.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected float nextStep() {
        return this.moveDist + 2.0F;
    }

    public void lifeSpanDamage(){
        if (!this.level.isClientSide) {
            for (int i = 0; i < this.level.random.nextInt(12) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
            ServerParticleUtil.smokeParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getEyeY(), this.getZ(), this.level);
        }
        this.playSound(SoundEvents.SOUL_ESCAPE, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (entityIn instanceof Mob mob){
            if (mob.getTarget() != this){
                mob.setTarget(this);
            }
        }

        return super.doHurtTarget(entityIn);
    }

    @Override
    public void tick() {
        super.tick();
        this.setYHeadRot(this.getYRot());
    }

    public boolean isNoGravity() {
        return true;
    }

}
