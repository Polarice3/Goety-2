package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EndermiteServant extends Summoned {

   public EndermiteServant(EntityType<? extends EndermiteServant> p_32591_, Level p_32592_) {
      super(p_32591_, p_32592_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
   }

   protected float getStandingEyeHeight(Pose p_32604_, EntityDimensions p_32605_) {
      return 0.13F;
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return Monster.createMonsterAttributes()
              .add(Attributes.MAX_HEALTH, 8.0D)
              .add(Attributes.MOVEMENT_SPEED, 0.25D)
              .add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   @Override
   public int xpReward() {
      return 3;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_32615_) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos p_32607_, BlockState p_32608_) {
      this.playSound(SoundEvents.ENDERMITE_STEP, 0.15F, 1.0F);
   }

   public void tick() {
      this.yBodyRot = this.getYRot();
      super.tick();
   }

   public void setYBodyRot(float p_32621_) {
      this.setYRot(p_32621_);
      super.setYBodyRot(p_32621_);
   }

   public double getMyRidingOffset() {
      return 0.1D;
   }

   @Override
   public void lifeSpanDamage() {
      if (!this.level.isClientSide){
         for(int i = 0; i < this.level.random.nextInt(10) + 10; ++i) {
            ServerParticleUtil.smokeParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getEyeY(), this.getZ(), this.level);
         }
      }
      this.discard();
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      }

   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }
}