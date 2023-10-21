package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StunnedEvents {

    private static boolean isStunned(@Nullable LivingEntity entity) {
        return entity != null && entity.isAlive() && entity.hasEffect(GoetyEffects.STUNNED.get());
    }

    public static void cancelEvent(LivingEvent event){
        if (event.isCancelable() && isStunned(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void cancelPlayerAttack(AttackEntityEvent event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelBreakSpeed(PlayerEvent.BreakSpeed event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelActivateBlock(PlayerInteractEvent.RightClickBlock event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelInteract(PlayerInteractEvent.EntityInteract event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelUsingItem(LivingEntityUseItemEvent.Start event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelTickUsingItem(LivingEntityUseItemEvent.Tick event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void cancelPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        cancelEvent(event);
    }

    @SubscribeEvent
    public static void onLivingTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && isStunned(mob)) {
            event.setNewTarget(null);
        }
    }
}
