package com.Polarice3.Goety.utils;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class OwnedDamageSource extends DamageSource {
   protected final LivingEntity owner;

   public OwnedDamageSource(Holder<DamageType> pDamageType, Entity entity, LivingEntity owner) {
      super(pDamageType, entity);
      this.owner = owner;
   }

   public LivingEntity getOwner() {
      return this.owner;
   }

   public Component getLocalizedDeathMessage(LivingEntity target) {
      ItemStack itemstack = this.getDirectEntity() instanceof LivingEntity
              ? ((LivingEntity)this.getDirectEntity()).getMainHandItem()
              : ItemStack.EMPTY;
      String s = "death.attack." + this.getMsgId();
      if (this.owner != null && this.getDirectEntity() != null) {
         if (!itemstack.isEmpty() && itemstack.hasCustomHoverName()) {
            return Component.translatable(s + ".item", target.getDisplayName(), this.owner.getDisplayName(), this.getDirectEntity().getDisplayName(), itemstack.getDisplayName());
         } else {
            return Component.translatable(s, target.getDisplayName(), this.owner.getDisplayName(), this.getDirectEntity().getDisplayName());
         }
      } else {
         return super.getLocalizedDeathMessage(target);
      }
   }
}