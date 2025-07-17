package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class VoidBottleItem extends Item {

   public VoidBottleItem() {
      super(new Properties().craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().alwaysEat().build()));
   }

   public ItemStack finishUsingItem(ItemStack p_41348_, Level p_41349_, LivingEntity p_41350_) {
      super.finishUsingItem(p_41348_, p_41349_, p_41350_);

      if (!p_41349_.isClientSide) {
         if (p_41350_.hurt(ModDamageSource.getDamageSource(p_41349_, ModDamageSource.VOIDED), p_41350_.getMaxHealth() * 0.03F)){
            p_41350_.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(5)));
         }
      }

      if (p_41348_.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (p_41350_ instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
             if (!player.getInventory().add(itemstack)) {
               player.drop(itemstack, false);
            }
         }

         return p_41348_;
      }
   }

   public int getUseDuration(ItemStack p_41360_) {
      return 40;
   }

   public UseAnim getUseAnimation(ItemStack p_41358_) {
      return UseAnim.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level p_41352_, Player p_41353_, InteractionHand p_41354_) {
      return ItemUtils.startUsingInstantly(p_41352_, p_41353_, p_41354_);
   }
}