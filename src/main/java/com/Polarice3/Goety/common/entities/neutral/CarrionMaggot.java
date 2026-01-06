package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.RandomUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CarrionMaggot extends Summoned {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(CarrionMaggot.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> DATA_HOST = SynchedEntityData.defineId(CarrionMaggot.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_COCOON = SynchedEntityData.defineId(CarrionMaggot.class, EntityDataSerializers.BOOLEAN);
   public int cocoonTick;
   public AnimationState idleAnimationState = new AnimationState();
   public AnimationState emergeAnimationState = new AnimationState();

   public CarrionMaggot(EntityType<? extends CarrionMaggot> p_32591_, Level p_32592_) {
      super(p_32591_, p_32592_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new CocoonGoal());
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F){
         @Override
         public boolean canUse() {
            return super.canUse() && !CarrionMaggot.this.isCocoon();
         }

         @Override
         public boolean canContinueToUse() {
            return super.canContinueToUse() && !CarrionMaggot.this.isCocoon();
         }

         @Override
         public void start() {
            if (!CarrionMaggot.this.isCocoon()) {
               super.start();
            }
         }
      });
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(8, new WanderGoal<>(this, 1.0D, 10, 0.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
   }

   protected float getStandingEyeHeight(Pose p_32604_, EntityDimensions p_32605_) {
      return 0.13F;
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return Monster.createMonsterAttributes()
              .add(Attributes.MAX_HEALTH, AttributesConfig.CarrionMaggotHealth.get())
              .add(Attributes.MOVEMENT_SPEED, 0.25D)
              .add(Attributes.ARMOR, AttributesConfig.CarrionMaggotArmor.get())
              .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CarrionMaggotDamage.get());
   }

   @Override
   public void setConfigurableAttributes(){
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CarrionMaggotHealth.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CarrionMaggotArmor.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CarrionMaggotDamage.get());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_HOST, -1);
      this.entityData.define(DATA_COCOON, false);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Cocoon")) {
         this.setCocoon(compound.getBoolean("Cocoon"));
      }
      if (compound.contains("CocoonTick")) {
         this.cocoonTick = compound.getInt("CocoonTick");
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Cocoon", this.isCocoon());
      compound.putInt("CocoonTick", this.cocoonTick);
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
      if (pReason == MobSpawnType.SPAWN_EGG){
         this.setHostile(true);
      }
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   public void summonParticles(ServerLevel pLevel, MobSpawnType pReason) {
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      if (this.isPassenger()){
         if (source.is(DamageTypes.IN_WALL)){
            return false;
         }
      }

      if (this.isCocoon()){
         amount /= 1.5F;
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
         if (!this.isCocoon()) {
            if (this.getTrueOwner() instanceof Wight) {
               if (!this.isClimbing() && this.tickCount > 20) {
                  if (this.getTarget() != null && this.getTarget().isAlive()) {
                     if (!this.isPassenger() && !this.isDeadOrDying()) {
                        if (this.getTarget().getY() > this.getY() + 4.0F && this.tickCount % 100 == 0) {
                           this.setCocoon(true);
                           this.level.broadcastEntityEvent(this, (byte) 4);
                        }
                     }
                  }
               }
            }
         } else {
            this.setStaying(true);
            this.setWandering(false);
            this.setBoundPos(null);
            ++this.cocoonTick;
            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
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

   public boolean isCocoon(){
      return this.entityData.get(DATA_COCOON);
   }

   public void setCocoon(boolean cocoon){
      this.entityData.set(DATA_COCOON, cocoon);
   }

   public void setYBodyRot(float p_32621_) {
      this.setYRot(p_32621_);
      super.setYBodyRot(p_32621_);
   }

   public double getMyRidingOffset() {
      return 0.1D;
   }

   @Override
   public void handleEntityEvent(byte p_21375_) {
      if (p_21375_ == 4) {
         this.setCocoon(true);
         this.playSound(ModSounds.SPIDER_NEST_TRAIN.get(), 1.0F, 0.75F);
      } else {
         super.handleEntityEvent(p_21375_);
      }
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
            health.setBaseValue(AttributesConfig.CarrionMaggotHealth.get() * 1.33D);
            attack.setBaseValue(AttributesConfig.CarrionMaggotDamage.get() * 1.1D);
         } else {
            health.setBaseValue(AttributesConfig.CarrionMaggotHealth.get());
            attack.setBaseValue(AttributesConfig.CarrionMaggotDamage.get());
         }
      }
      this.setHealth(this.getMaxHealth());
   }

   @Override
   public void uncreditedKill(LivingEntity target) {
      if (!MobUtil.areAllies(this, target)) {
         int random = 3;
         if (target.getMaxHealth() >= 20.0F) {
            random = 1;
         }
         if (RandomUtil.nextInt(this.getRandom(), random) == 0) {
            this.setCocoon(true);
            this.heal(this.getMaxHealth());
         }
      }
   }

   @Override
   public boolean doHurtTarget(float amount, Entity target) {
      if (target instanceof LivingEntity livingTarget) {
         if (livingTarget.getMobType() == MobType.UNDEAD) {
            amount *= 2;
         }
      }
      return super.doHurtTarget(amount, target);
   }

   public boolean isFood(ItemStack p_30440_) {
      Item item = p_30440_.getItem();
      return item.isEdible() && p_30440_.getFoodProperties(this).isMeat();
   }

   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
         if (this.isFood(itemstack) && !this.isCocoon()) {
            FoodProperties foodProperties = itemstack.getFoodProperties(this);
            if (foodProperties != null){
               this.heal((float)foodProperties.getNutrition());
               if (!pPlayer.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               this.gameEvent(GameEvent.EAT, this);
               this.eat(this.level, itemstack);
               if (this.level instanceof ServerLevel serverLevel) {
                  for (int i = 0; i < 7; ++i) {
                     double d0 = this.random.nextGaussian() * 0.02D;
                     double d1 = this.random.nextGaussian() * 0.02D;
                     double d2 = this.random.nextGaussian() * 0.02D;
                     serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                  }
               }
               pPlayer.swing(pHand);
               this.setCocoon(true);
               return InteractionResult.SUCCESS;
            }
         }
      }
      return super.mobInteract(pPlayer, pHand);
   }

   class CocoonGoal extends Goal {
      public CocoonGoal() {
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
      }

      public boolean canUse() {
         return CarrionMaggot.this.isCocoon();
      }

      public boolean canContinueToUse() {
         return CarrionMaggot.this.cocoonTick <= 100;
      }

      public void tick() {
         CarrionMaggot.this.getNavigation().stop();
         CarrionMaggot.this.getMoveControl().strafe(0.0F, 0.0F);
      }

      public void stop() {
         CarrionFly carrionFly = new CarrionFly(ModEntityType.CARRION_FLY.get(), CarrionMaggot.this.level);
         carrionFly.setUpgraded(CarrionMaggot.this.isUpgraded());
         carrionFly.setPos(CarrionMaggot.this.position());
         if (CarrionMaggot.this.getTrueOwner() != null) {
            carrionFly.setTrueOwner(CarrionMaggot.this.getTrueOwner());
         } else {
            carrionFly.setHostile(CarrionMaggot.this.isHostile());
         }
         if (CarrionMaggot.this.getLifespan() > 0 && CarrionMaggot.this.hasLifespan()) {
            carrionFly.setLifespan(CarrionMaggot.this.getLifespan());
         }
         if (CarrionMaggot.this.hasCustomName()) {
            carrionFly.setCustomName(CarrionMaggot.this.getCustomName());
         }
         carrionFly.setNoAi(CarrionMaggot.this.isNoAi());
         if (CarrionMaggot.this.isPersistenceRequired()){
            carrionFly.setPersistenceRequired();
         }
         if (CarrionMaggot.this.level instanceof ServerLevel serverLevel){
            carrionFly.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(CarrionMaggot.this.blockPosition()), CarrionMaggot.this.getSpawnType() != null ? CarrionMaggot.this.getSpawnType() : MobSpawnType.CONVERSION, null, null);
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), CarrionMaggot.this);
         }
         if (CarrionMaggot.this.getTarget() != null && CarrionMaggot.this.getTarget().isAlive()){
            carrionFly.setTarget(CarrionMaggot.this.getTarget());
         }
         if (CarrionMaggot.this.level.addFreshEntity(carrionFly)) {
            CarrionMaggot.this.playSound(SoundEvents.SLIME_DEATH, 1.5F, 0.5F);
            CarrionMaggot.this.discard();
         }
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }
}