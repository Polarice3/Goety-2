package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
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
            if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof WildRobeItem)){
                livingEntity.getActiveEffects().removeIf(effectInstance -> effectInstance.getEffect() == MobEffects.POISON);
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        if (event.getEntity().getMobType() == MobType.ARTHROPOD){
            if (CuriosFinder.hasCurio(event.getOriginalTarget(), item -> item.getItem() instanceof WildRobeItem)) {
                event.setNewTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void EffectApplyEvents(MobEffectEvent.Applicable event){
        LivingEntity livingEntity = event.getEntity();
        if (event.getEffectInstance().getEffect() == MobEffects.POISON) {
            if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof WildRobeItem)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
