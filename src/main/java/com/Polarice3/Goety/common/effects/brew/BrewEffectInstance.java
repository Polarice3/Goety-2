package com.Polarice3.Goety.common.effects.brew;

import com.google.common.collect.ComparisonChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class BrewEffectInstance implements Comparable<BrewEffectInstance> {
   private final BrewEffect effect;
   int duration;
   private int amplifier;

   public BrewEffectInstance(BrewEffect brewEffect){
      this(brewEffect, 1, 0);
   }

   public BrewEffectInstance(BrewEffect p_19515_, int p_19516_) {
      this(p_19515_, p_19516_, 0);
   }

   public BrewEffectInstance(BrewEffect p_216887_, int duration, int amplifier) {
      this.effect = p_216887_;
      this.duration = duration;
      this.amplifier = amplifier;
   }

   public BrewEffectInstance(BrewEffectInstance p_19543_) {
      this.effect = p_19543_.effect;
      this.setDetailsFrom(p_19543_);
   }

   void setDetailsFrom(BrewEffectInstance p_19549_) {
      this.duration = p_19549_.duration;
      this.amplifier = p_19549_.amplifier;
   }

   public boolean update(BrewEffectInstance p_19559_) {

      int i = this.duration;
      boolean flag = false;
      if (p_19559_.amplifier > this.amplifier) {

         this.amplifier = p_19559_.amplifier;
         this.duration = p_19559_.duration;
         flag = true;
      } else if (p_19559_.duration > this.duration) {
         if (p_19559_.amplifier == this.amplifier) {
            this.duration = p_19559_.duration;
            flag = true;
         }
      }

      return flag;
   }

   public BrewEffect getEffect() {
      return this.effect;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean tick(LivingEntity p_19553_, Runnable p_19554_) {
      if (this.duration > 0) {
         if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(p_19553_);
         }

         this.tickDownDuration();
      }
      return this.duration > 0;
   }

   private int tickDownDuration() {
      return --this.duration;
   }

   public void applyEffect(LivingEntity p_19551_) {
      if (this.duration > 0) {
         this.effect.applyEffectTick(p_19551_, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getDescriptionId() + ", Duration: " + this.duration;
      }
      return s;
   }

   public boolean equals(Object p_19574_) {
      if (this == p_19574_) {
         return true;
      } else if (!(p_19574_ instanceof BrewEffectInstance brewEffectInstance)) {
         return false;
      } else {
         return this.duration == brewEffectInstance.duration && this.amplifier == brewEffectInstance.amplifier && this.effect.equals(brewEffectInstance.effect);
      }
   }

   public int hashCode() {
      int i = this.effect.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      return 31 * i;
   }

   public CompoundTag save(CompoundTag p_19556_) {
      p_19556_.putString("BrewId", this.effect.getEffectID());
      this.writeDetailsTo(p_19556_);
      return p_19556_;
   }

   private void writeDetailsTo(CompoundTag p_19568_) {
      p_19568_.putByte("Amplifier", (byte)this.getAmplifier());
      p_19568_.putInt("Duration", this.getDuration());
   }

   @Nullable
   public static BrewEffectInstance load(CompoundTag p_19561_) {
      BrewEffect mobeffect = new BrewEffects().getBrewEffect(p_19561_);
      return mobeffect == null ? null : loadSpecifiedEffect(mobeffect, p_19561_);
   }

   private static BrewEffectInstance loadSpecifiedEffect(BrewEffect p_19546_, CompoundTag p_19547_) {
      int i = p_19547_.getByte("Amplifier");
      int j = p_19547_.getInt("Duration");

      return new BrewEffectInstance(p_19546_, j, Math.max(0, i));
   }

   public int compareTo(BrewEffectInstance p_19566_) {
      int i = 32147;
      return ComparisonChain.start().compare(this.getDuration(), p_19566_.getDuration()).result();
   }
}
