package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CarrionMaggot extends Summoned {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(CarrionMaggot.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> DATA_HOST = SynchedEntityData.defineId(CarrionMaggot.class, EntityDataSerializers.INT);
   public AnimationState idleAnimationState = new AnimationState();
   public AnimationState emergeAnimationState = new AnimationState();

   public CarrionMaggot(EntityType<? extends CarrionMaggot> p_32591_, Level p_32592_) {
      super(p_32591_, p_32592_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
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

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_HOST, -1);
   }

   @Override
   protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
   }

   protected PathNavigation createNavigation(Level p_33802_) {
      return new WallClimberNavigation(this, p_33802_);
   }

   @Override
   public int xpReward() {
      return 3;
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.MAGGOT_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource p_32615_) {
      return ModSounds.MAGGOT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.MAGGOT_DEATH.get();
   }

   protected void playStepSound(BlockPos p_32607_, BlockState p_32608_) {
      this.playSound(ModSounds.MAGGOT_STEP.get(), 0.15F, 1.0F);
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket((LivingEntity)this, this.hasPose(Pose.EMERGING) ? 1 : 0);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
      super.recreateFromPacket(p_219420_);
      if (p_219420_.getData() == 1) {
         this.setPose(Pose.EMERGING);
      }
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
      if (pReason == MobSpawnType.MOB_SUMMONED){
         this.setPose(Pose.EMERGING);
      }
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      if (this.isPassenger()){
         if (source == DamageSource.IN_WALL){
            return false;
         }
      }

      return super.hurt(source, amount);
   }

   @Override
   public boolean doHurtTarget(Entity entityIn) {
      boolean flag = super.doHurtTarget(entityIn);

      if (flag){
         if (this.level.random.nextFloat() <= 0.25F + this.level.getCurrentDifficultyAt(this.blockPosition()).getSpecialMultiplier()){
            if (this.getVehicle() == null
                    && !entityIn.isVehicle()
                    && entityIn.isAlive()) {
               this.startRiding(entityIn);
               this.setHost(entityIn.getId());
            }
         }
      }

      return flag;
   }

   public void tick() {
      this.yBodyRot = this.getYRot();
      super.tick();
      if (this.hasPose(Pose.EMERGING)){
         this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
         this.emergeAnimationState.startIfStopped(this.tickCount);
         this.idleAnimationState.stop();
         if (this.tickCount >= 10){
            this.setPose(Pose.STANDING);
         }
      } else {
         this.idleAnimationState.startIfStopped(this.tickCount);
         this.emergeAnimationState.stop();
      }
      this.setClimbing(this.horizontalCollision);

      if (!this.level.isClientSide){
         if (this.getVehicle() != null
                 && this.getTarget() == this.getVehicle()
                 && !this.isDeadOrDying()
                 && this.tickCount % 20 == 0) {
            this.playSound(ModSounds.MAGGOT_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            this.doHurtTarget(this.getVehicle());
            if (this.level.random.nextFloat() <= 0.25F) {
               this.stopRiding();
               this.setHost(-1);
            }
         }
         if (this.getVehicle() == null && this.getHost() != -1) {
            this.setHost(-1);
         }
         if (this.getTrueOwner() != null) {
            if (this.getTrueOwner() instanceof Mob mobOwner) {
               if (mobOwner.isRemoved() || mobOwner.isDeadOrDying()) {
                  this.lifeSpanDamage();
               }
            }
         }
      } else if (this.getVehicle() == null && this.getHost() != -1) {
         Entity entity = this.level.getEntity(this.getHost());
         if (entity != null) {
            this.startRiding(entity);
         }
      } else if (this.getVehicle() != null && this.getHost() == -1) {
         this.stopRiding();
      }
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   public boolean isClimbing() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setClimbing(boolean p_33820_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_33820_) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, b0);
   }

   public int getHost() {
      return this.entityData.get(DATA_HOST);
   }

   public void setHost(int host) {
      this.entityData.set(DATA_HOST, host);
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
            ServerParticleUtil.smokeParticles(ParticleTypes.SMOKE, this.getX(), this.getEyeY(), this.getZ(), this.level);
         }
      }
      this.discard();
   }

   @Override
   public void setUpgraded(boolean upgraded) {
      super.setUpgraded(upgraded);
      AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
      AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (health != null && attack != null) {
         if (upgraded) {
            health.setBaseValue(8.0D * 1.33D);
            attack.setBaseValue(2.0D * 1.1D);
         } else {
            health.setBaseValue(8.0D);
            attack.setBaseValue(2.0D);
         }
      }
   }
}