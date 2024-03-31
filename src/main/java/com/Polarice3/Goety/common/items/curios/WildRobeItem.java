package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WildRobeItem extends SingleStackItem {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (CuriosFinder.hasWildRobe(livingEntity)){
                livingEntity.getActiveEffects().removeIf(effectInstance -> effectInstance.getEffect() == MobEffects.POISON
                || effectInstance.getEffect() == GoetyEffects.ACID_VENOM.get());
            }
        }
    }

    @SubscribeEvent
    public static void EffectApplyEvents(MobEffectEvent.Applicable event){
        LivingEntity livingEntity = event.getEntity();
        if (event.getEffectInstance().getEffect() == MobEffects.POISON
        || event.getEffectInstance().getEffect() == GoetyEffects.ACID_VENOM.get()) {
            if (CuriosFinder.hasWildRobe(livingEntity)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
