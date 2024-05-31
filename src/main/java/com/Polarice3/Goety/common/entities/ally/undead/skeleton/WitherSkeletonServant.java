package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class WitherSkeletonServant extends AbstractSkeletonServant {
   public WitherSkeletonServant(EntityType<? extends WitherSkeletonServant> p_34166_, Level p_34167_) {
      super(p_34166_, p_34167_);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return Mob.createMobAttributes()
              .add(Attributes.MAX_HEALTH, AttributesConfig.WitherSkeletonServantHealth.get())
              .add(Attributes.ARMOR, AttributesConfig.WitherSkeletonServantArmor.get())
              .add(Attributes.MOVEMENT_SPEED, 0.25F)
              .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WitherSkeletonServantDamage.get());
   }

   public void setConfigurableAttributes(){
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WitherSkeletonServantHealth.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WitherSkeletonServantArmor.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WitherSkeletonServantDamage.get());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_34195_) {
      return SoundEvents.WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.WITHER_SKELETON_STEP;
   }

   protected void populateDefaultEquipmentSlots(RandomSource p_219154_, DifficultyInstance p_219155_) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   protected void populateDefaultEquipmentEnchantments(RandomSource p_219157_, DifficultyInstance p_219158_) {
   }

   protected float getStandingEyeHeight(Pose p_34186_, EntityDimensions p_34187_) {
      return 2.1F;
   }

   public boolean doHurtTarget(Entity p_34169_) {
      if (!super.doHurtTarget(p_34169_)) {
         return false;
      } else {
         if (p_34169_ instanceof LivingEntity) {
            ((LivingEntity)p_34169_).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
         }

         return true;
      }
   }

   protected AbstractArrow getMobArrow(ItemStack p_34189_, float p_34190_) {
      AbstractArrow abstractarrow = super.getMobArrow(p_34189_, p_34190_);
      abstractarrow.setSecondsOnFire(100);
      return abstractarrow;
   }

   public boolean canBeAffected(MobEffectInstance p_34192_) {
      return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
   }
}