package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRemoveEffectPacket;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StunnedEvents {

    private static boolean isStunned(@Nullable LivingEntity entity) {
        return entity != null && entity.isAlive() && (entity.hasEffect(GoetyEffects.STUNNED.get())
                || (entity instanceof Player player && SEHelper.hasCamera(player)));
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
            if (event.getTargetType() == MOB_TARGET) {
                event.setNewTarget(null);
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().hasEffect(GoetyEffects.TANGLED.get())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void PotionApplicationEvents(MobEffectEvent.Applicable event){
        if (event.getEffectInstance().getEffect() == GoetyEffects.STUNNED.get()
                || event.getEffectInstance().getEffect().getDescriptionId().contains("born_in_chaos_v1:stun")){
            if (event.getEntity().getType().is(ModTags.EntityTypes.UNSTUNNABLE)){
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event){
        if (!event.getEntity().level.isClientSide) {
            if (isStunned(event.getEntity())) {
                event.getEntity().removeEffect(GoetyEffects.STUNNED.get());
                event.getEntity().removeEffect(GoetyEffects.TANGLED.get());
                ModNetwork.sendToALL(new SRemoveEffectPacket(event.getEntity().getId(), MobEffect.getId(GoetyEffects.STUNNED.get())));
                ModNetwork.sendToALL(new SRemoveEffectPacket(event.getEntity().getId(), MobEffect.getId(GoetyEffects.TANGLED.get())));
            }
        }
    }
}
