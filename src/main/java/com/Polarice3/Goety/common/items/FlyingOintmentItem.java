package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class FlyingOintmentItem extends UnguentItem {
   public static List<MobEffectInstance> LIST = List.of(
           new MobEffectInstance(MobEffects.CONFUSION, MathHelper.secondsToTicks(30), 4),
           new MobEffectInstance(GoetyEffects.TRIPPING.get(), MathHelper.minutesToTicks(3), 0),
           new MobEffectInstance(GoetyEffects.FLIMSY.get(), MathHelper.minutesToTicks(3), 0),
           new MobEffectInstance(GoetyEffects.HYSTERIA.get(), MathHelper.minutesToTicks(1), 0)
   );

   public FlyingOintmentItem() {
      super(LIST);
   }
}