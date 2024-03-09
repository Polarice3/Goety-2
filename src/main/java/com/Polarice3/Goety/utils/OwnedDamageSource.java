package com.Polarice3.Goety.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class OwnedDamageSource extends EntityDamageSource {
   protected final LivingEntity owner;

   public OwnedDamageSource(String p_19394_, Entity entity, LivingEntity owner) {
      super(p_19394_, entity);
      this.owner = owner;
   }

   public LivingEntity getOwner() {
      return this.owner;
   }

   public Component getLocalizedDeathMessage(LivingEntity target) {
      ItemStack itemstack = this.entity instanceof LivingEntity
              ? ((LivingEntity)this.entity).getMainHandItem()
              : ItemStack.EMPTY;
      String s = "death.attack." + this.msgId;
      if (this.owner != null) {
         if (!itemstack.isEmpty() && itemstack.hasCustomHoverName()) {
            return Component.translatable(s + ".item", target.getDisplayName(), this.owner.getDisplayName(), this.entity.getDisplayName(), itemstack.getDisplayName());
         } else {
            return Component.translatable(s, target.getDisplayName(), this.owner.getDisplayName(), this.entity.getDisplayName());
         }
      } else {
         return super.getLocalizedDeathMessage(target);
      }
   }
}