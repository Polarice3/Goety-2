package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedSkull;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.projectiles.ThrowableFungus;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServantEvents {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Mob mob){
            if (mob.getTarget() instanceof IOwned){
                if (mob.getTarget().isDeadOrDying()){
                    mob.setTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalTarget();
        if (attacker instanceof Mob mobAttacker) {
            if (target instanceof Player) {
                if (mobAttacker.getLastHurtByMob() instanceof IOwned owned
                        && owned.getTrueOwner() == target
                        && !(mobAttacker instanceof Apostle)){
                    event.setNewTarget(mobAttacker.getLastHurtByMob());
                }
            }
        }
    }

    @SubscribeEvent
    public static void InteractEntityEvent(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        if (!event.getLevel().isClientSide) {
            if (event.getTarget().isVehicle() && player.isCrouching()){
                Entity entity = event.getTarget().getControllingPassenger();
                if (entity instanceof IServant summoned){
                    if (summoned.getTrueOwner() == player){
                        entity.stopRiding();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AttackEvent(LivingAttackEvent event){
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (MobsConfig.MinionsMasterImmune.get()){
            if (attacker instanceof IOwned owned){
                if (owned.getTrueOwner() == victim){
                    event.setCanceled(true);
                }
            }
        }
        if (MobsConfig.OwnerAttackCancel.get()){
            if (attacker != null) {
                if (victim instanceof IOwned owned) {
                    if (owned.getTrueOwner() == attacker) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerAttackEvent(AttackEntityEvent event){
        if (event.getTarget() instanceof IOwned iOwned){
            ItemStack itemStack = event.getEntity().getMainHandItem();
            if (iOwned.getTrueOwner() == event.getEntity() || (iOwned.getTrueOwner() instanceof IOwned owned && owned.getTrueOwner() == event.getEntity())) {
                if (MobsConfig.OwnerAttackCancel.get()) {
                    itemStack.getItem().onLeftClickEntity(itemStack, event.getEntity(), event.getTarget());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void DamageEvent(LivingDamageEvent event){
        LivingEntity target = event.getEntity();
        if (event.getSource().getEntity() instanceof IOwned summonedEntity){
            if (summonedEntity.getTrueOwner() != null){
                if (summonedEntity.getTrueOwner() == target){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            if (event.getExplosion().getIndirectSourceEntity() != null) {
                if (event.getExplosion().getIndirectSourceEntity() instanceof Apostle) {
                    event.getAffectedEntities().removeIf(entity -> (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() instanceof Apostle) || (entity == event.getExplosion().getIndirectSourceEntity()));
                }
                if (event.getExplosion().getIndirectSourceEntity() instanceof IOwned sourceMob) {
                    if (sourceMob.getTrueOwner() instanceof Apostle) {
                        event.getAffectedEntities().removeIf(entity -> (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() instanceof Apostle) || entity == sourceMob.getTrueOwner());
                    }
                    if (sourceMob instanceof HauntedSkull){
                        event.getAffectedEntities().removeIf(entity ->
                                (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == sourceMob.getTrueOwner()
                                        || entity instanceof OwnableEntity && ((OwnableEntity) entity).getOwner() == sourceMob.getTrueOwner()
                                        || entity == sourceMob.getTrueOwner()));
                    }
                }
                if (event.getExplosion().getExploder() instanceof ThrowableFungus fungus){
                    event.getAffectedEntities().removeIf(entity ->
                            (entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == fungus.getOwner()
                                    || entity instanceof OwnableEntity && ((OwnableEntity) entity).getOwner() == fungus.getOwner()
                                    || entity instanceof AbstractHorse && fungus.getOwner() != null &&  ((AbstractHorse) entity).getOwnerUUID() == fungus.getOwner().getUUID()
                                    || entity == fungus.getOwner()
                                    || entity instanceof ThrowableFungus));
                }
            }
        }
    }
}
