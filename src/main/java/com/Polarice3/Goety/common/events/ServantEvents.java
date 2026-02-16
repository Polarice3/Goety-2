package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedSkull;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.FrozenZombieServant;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.projectiles.ThrowableFungus;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServantEvents {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Mob mob){
            if (mob instanceof OwnableEntity ownable && mob.getTarget() != null) {
                if (SEHelper.isAlly(ownable.getOwner(), mob.getTarget())) {
                    mob.setTarget(null);
                }
            }
            if (mob instanceof IOwned && mob.getTarget() != null) {
                if (mob.getTarget().isDeadOrDying() || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(mob.getTarget())){
                    mob.setTarget(null);
                }
            }
            if (mob.getTarget() instanceof IOwned owned){
                if (mob.getType().is(ModTags.EntityTypes.IGNORE_SERVANTS)){
                    if (owned.getTrueOwner() != null){
                        if (mob.canAttack(owned.getTrueOwner()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(owned.getTrueOwner())) {
                            mob.setLastHurtByMob(owned.getTrueOwner());
                            mob.setTarget(owned.getTrueOwner());
                        }
                    }
                }
                if (mob.getTarget().isDeadOrDying() || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(mob.getTarget())){
                    mob.setTarget(null);
                }
            }
        }
        if (ServantUtil.notServantButOwned(livingEntity)){
            int i = MiscCapHelper.getNoHealTime(livingEntity);
            if (i > 0){
                MiscCapHelper.setNoHealTime(livingEntity, i - 1);
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalTarget();
        LivingEntity newTarget = event.getNewTarget();
        if (attacker instanceof Mob mobAttacker) {
            if (target instanceof Player) {
                if (mobAttacker.getLastHurtByMob() instanceof IOwned owned
                        && owned.getTrueOwner() == target
                        && !(mobAttacker instanceof Apostle)){
                    event.setNewTarget(mobAttacker.getLastHurtByMob());
                }
            }
            if (target instanceof OwnableEntity ownable) {
                if (attacker.getType().is(ModTags.EntityTypes.IGNORE_SERVANTS)) {
                    if (ownable.getOwner() != null) {
                        if (attacker.canAttack(ownable.getOwner()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(ownable.getOwner())) {
                            attacker.setLastHurtByMob(ownable.getOwner());
                            event.setNewTarget(ownable.getOwner());
                        }
                    }
                }
            }
            if (attacker instanceof IOwned owned && owned.getMasterOwner() instanceof Player){
                if (attacker.level.getServer() != null) {
                    if (!attacker.level.getServer().isPvpAllowed()) {
                        if (target instanceof Player
                                || (target instanceof IOwned owned1
                                && owned1.getMasterOwner() instanceof Player)) {
                            if (event.getTargetType() == MOB_TARGET) {
                                event.setNewTarget(null);
                            } else {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
            if (!(mobAttacker instanceof Enemy)
                    && target instanceof IOwned owned && owned instanceof Enemy && !owned.isHostile()
                    && target instanceof Mob mob && mob.getTarget() != mobAttacker
                    && !(mobAttacker instanceof OwnableEntity ownable && ownable.getOwner() != null && ((ownable.getOwner().getLastHurtByMob() == target) || (ownable.getOwner() instanceof Mob mob1 && mob1.getTarget() == target)))
                    && mobAttacker.getLastHurtByMob() != target){
                if (event.getTargetType() == MOB_TARGET) {
                    event.setNewTarget(null);
                } else {
                    event.setCanceled(true);
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
        if (attacker instanceof IOwned owned){
            if (MobsConfig.ServantsMasterImmune.get()){
                if (owned.getTrueOwner() == victim){
                    event.setCanceled(true);
                }
            }
            if (attacker instanceof Mob mob) {
                if (mob.getMainHandItem().getItem() instanceof AxeItem) {
                    if (victim.getType().is(ModTags.EntityTypes.BIC_SHIELDED_MOBS)) {
                        MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("born_in_chaos_v1", "block_break"));
                        if (mobEffect != null) {
                            victim.addEffect(new MobEffectInstance(mobEffect, 120, 0, false, false));
                            if (!victim.level.isClientSide()) {
                                victim.level.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.NEUTRAL, 0.2F, 1.0F);
                            } else {
                                victim.level.playLocalSound(victim.getX(), victim.getY(), victim.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.NEUTRAL, 0.2F, 1.0F, false);
                            }

                            if (victim.level instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(ParticleTypes.CRIT, victim.getX(), victim.getY(), victim.getZ(), 9, 0.6, 1.0, 0.6, 0.6);
                            }

                            if (victim.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                                victim.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                            }
                        }
                    }
                }
            }
        }
        if ((attacker instanceof IOwned owned
                && owned.getMasterOwner() instanceof Player)
                || attacker instanceof Player){
            if (attacker.level.getServer() != null) {
                if (!attacker.level.getServer().isPvpAllowed()) {
                    if (victim instanceof Player
                            || (victim instanceof IOwned owned1
                            && owned1.getMasterOwner() instanceof Player)) {
                        event.setCanceled(true);
                    }
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
        if (attacker instanceof FrozenZombieServant){
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(3)));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (MobsConfig.CompatMinionHeal.get()) {
            if (ServantUtil.notServantButOwned(target) && !target.getType().is(ModTags.EntityTypes.NO_HEAL_SERVANTS)) {
                MiscCapHelper.setNoHealTime(target, MathHelper.secondsToTicks(MobsConfig.ServantHealHalt.get()));
            }
        }
        if (attacker instanceof RaiderServant raider) {
            if (raider.isRaiding()) {
                if (raider.getTrueOwner() instanceof Player player) {
                    if (target instanceof Villager villager) {
                        if (villager.getPlayerReputation(player) > -200) {
                            villager.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
                        }
                    }
                }
            }
        }
        if (attacker instanceof Mob mob) {
            if (mob.getType().getDescriptionId().contains("nightmare_stalker")) {
                if (MobsConfig.CompatNightmareStalker.get()) {
                    if (target instanceof IOwned) {
                        if (target.hasEffect(MobEffects.WITHER)) {
                            MobEffectInstance instance = target.getEffect(MobEffects.WITHER);
                            if (instance != null) {
                                if (instance.getAmplifier() >= 2) {
                                    if (!target.level.isClientSide) {
                                        EffectsUtil.deamplifyEffect(target, MobEffects.WITHER, 2, 200);
                                    }
                                }
                            }
                        }
                        if (mob.hasEffect(MobEffects.REGENERATION)) {
                            MobEffectInstance instance = mob.getEffect(MobEffects.REGENERATION);
                            if (instance != null) {
                                if (instance.getAmplifier() >= 2) {
                                    if (!mob.level.isClientSide) {
                                        EffectsUtil.deamplifyEffect(mob, MobEffects.REGENERATION, 2, 60);
                                        mob.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 60, 0, false, false));
                                    }
                                }
                            }
                        }
                        if (mob.hasEffect(MobEffects.DAMAGE_BOOST)) {
                            if (!mob.level.isClientSide) {
                                mob.removeEffect(MobEffects.DAMAGE_BOOST);
                            }
                            event.setAmount((float) mob.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
                        }
                    }
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
    public static void DeathEvent(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (!killed.isRemoved()){
            IOwned owned = null;
            if (killer instanceof IOwned owned1){
                owned = owned1;
            } else if (killed.getLastHurtByMob() instanceof IOwned owned1) {
                owned = owned1;
            }
            if (owned != null) {
                if (owned.getMasterOwner() instanceof ServerPlayer serverPlayer) {
                    ModCriteriaTriggers.SERVANT_KILLED_ENTITY.trigger(serverPlayer, killed, event.getSource());
                }
                owned.uncreditedKill(killed);
            }
        }
        RaiderServant raider = null;
        if (killer instanceof RaiderServant servant) {
            raider = servant;
        } else if (MobUtil.getOwner(killer) instanceof RaiderServant servant) {
            raider = servant;
        }
        if (raider != null) {
            if (!raider.level.isClientSide) {
                if (raider.isCapturing()) {
                    RaiderServant leader = null;
                    if (raider.getLeader() != null) {
                        leader = raider.getLeader();
                    } else if (raider.isLeader()) {
                        leader = raider;
                    }
                    LivingEntity owner;
                    if (raider.getTrueOwner() != null) {
                        owner = raider.getTrueOwner();
                    } else {
                        owner = raider;
                    }
                    if (killed instanceof AbstractVillager villager && !villager.isBaby()) {
                        Prisoner prisoner = villager.convertTo(ModEntityType.PRISONER.get(), true);
                        if (prisoner != null) {
                            if (villager instanceof Villager villager1) {
                                prisoner.setVillagerData(villager1.getVillagerData());
                                prisoner.setGossips(villager1.getGossips().store(NbtOps.INSTANCE));
                                MobUtil.releaseAllPois(villager1);
                            }
                            prisoner.setTradeOffers(villager.getOffers().createTag());
                            prisoner.setVillagerXp(villager.getVillagerXp());
                            prisoner.setIsTrader(villager instanceof WanderingTrader);
                            if (leader != null) {
                                prisoner.setTrueOwner(raider.getTrueOwner());
                                prisoner.setLeader(leader);
                            } else {
                                prisoner.setTrueOwner(raider);
                            }
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, prisoner);
                            if (!prisoner.isSilent()) {
                                prisoner.playSound(SoundEvents.IRON_TRAPDOOR_CLOSE);
                            }
                        }
                    } else if (killed instanceof Prisoner prisoner) {
                        if (owner != null) {
                            if (prisoner.getTrueOwner() != owner) {
                                prisoner.setTrueOwner(owner);
                                if (leader != null) {
                                    prisoner.setLeader(leader);
                                }
                                if (!prisoner.isSilent()) {
                                    prisoner.playSound(SoundEvents.IRON_TRAPDOOR_CLOSE);
                                }
                                prisoner.heal(prisoner.getMaxHealth());
                                event.setCanceled(true);
                            }
                        }
                    }
                } else if (raider instanceof AbstractIllagerServant illager) {
                    if (illager.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                        if (killed instanceof AbstractVillager villager) {
                            int emeralds = illager.level.getRandom().nextIntBetweenInclusive(1, 3);
                            if (villager instanceof Villager villager1 && MobsConfig.IllagerServantLootVillagers.get()) {
                                emeralds += villager1.getVillagerData().getLevel() - 1;
                            } else if (villager instanceof WanderingTrader && MobsConfig.IllagerServantLootTraders.get()) {
                                emeralds *= 2;
                            } else {
                                emeralds = 0;
                            }
                            if (emeralds > 0) {
                                List<ItemStack> list = new ArrayList<>();
                                for (MerchantOffer offer : villager.getOffers()) {
                                    if (!offer.isOutOfStock()) {
                                        ItemStack itemStack = offer.assemble();
                                        if (!itemStack.isEmpty()) {
                                            if (itemStack.isStackable()) {
                                                itemStack.setCount(offer.getMaxUses() - offer.getUses());
                                            }
                                            list.add(itemStack);
                                        }
                                    }
                                }
                                if (!list.isEmpty()) {
                                    for (ItemStack itemStack : list) {
                                        if (illager.getInventory().canAddItem(itemStack)) {
                                            illager.getInventory().addItem(itemStack);
                                        } else {
                                            villager.spawnAtLocation(itemStack);
                                        }
                                    }
                                }
                                ItemStack itemStack = new ItemStack(Items.EMERALD, emeralds);
                                if (illager.getInventory().canAddItem(itemStack)) {
                                    illager.getInventory().addItem(itemStack);
                                } else {
                                    villager.spawnAtLocation(itemStack);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (killed instanceof IOwned owned) {
            if (!killed.level.isClientSide) {
                if (owned.canRevive(event.getSource())) {
                    killed.stopRiding();
                    owned.startRevival();
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void ChangeDimensions(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof IServant servant) {
            if (event.getDimension() != null) {
                servant.setBoundPos(null);
            }
        }
    }

    @SubscribeEvent
    public static void ServantProjectileImpact(ProjectileImpactEvent event){
        if (event.getProjectile().getOwner() instanceof FrozenZombieServant frozenZombieServant){
            if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult){
                Entity entity = entityHitResult.getEntity();
                if (MobUtil.areAllies(frozenZombieServant, entity)){
                    event.setCanceled(true);
                }
            }
            if (event.getProjectile() instanceof Snowball snowball){
                if (event.getRayTraceResult().getType() != HitResult.Type.MISS){
                    snowball.playSound(ModSounds.FROZEN_ZOMBIE_SNOWBALL.get());
                }
            }
        }
    }

    @SubscribeEvent
    public static void ExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if (event.getExplosion() != null) {
            if (event.getExplosion().getIndirectSourceEntity() != null) {
                if (event.getExplosion().getIndirectSourceEntity() instanceof Apostle) {
                    event.getAffectedEntities().removeIf(entity -> (MobUtil.getOwner(entity) instanceof Apostle) || (entity == event.getExplosion().getIndirectSourceEntity()));
                }
                if (event.getExplosion().getIndirectSourceEntity() instanceof IOwned sourceMob) {
                    if (sourceMob.getTrueOwner() instanceof Apostle) {
                        event.getAffectedEntities().removeIf(entity -> (MobUtil.getOwner(entity) instanceof Apostle) || entity == sourceMob.getTrueOwner());
                    }
                    if (sourceMob instanceof HauntedSkull){
                        event.getAffectedEntities().removeIf(entity ->
                                (MobUtil.getOwner(entity) != null && MobUtil.getOwner(entity) == sourceMob.getTrueOwner()
                                        || entity == sourceMob.getTrueOwner()));
                    }
                }
                if (event.getExplosion().getExploder() instanceof ThrowableFungus fungus){
                    event.getAffectedEntities().removeIf(entity ->
                            (MobUtil.getOwner(entity) != null && MobUtil.getOwner(entity) == fungus.getOwner()
                                    || entity instanceof AbstractHorse && fungus.getOwner() != null &&  ((AbstractHorse) entity).getOwnerUUID() == fungus.getOwner().getUUID()
                                    || entity == fungus.getOwner()
                                    || entity instanceof ThrowableFungus));
                }
            }
        }
    }

    @SubscribeEvent
    public static void DropEvents(LivingDropsEvent event){
        if (event.getEntity() != null) {
            LivingEntity victim = event.getEntity();
            if (!victim.level.isClientSide) {
                if (victim instanceof Mob) {
                    if (MobsConfig.IllagerServantCollectLoot.get()){
                        AbstractIllagerServant servant = null;
                        if (victim.getLastHurtByMob() instanceof IOwned minion){
                            if (minion.getTrueOwner() instanceof AbstractIllagerServant servant1) {
                                servant = servant1;
                            }
                        } else {
                            if (event.getSource() instanceof NoKnockBackDamageSource source) {
                                if (source.getOwner() instanceof IOwned minion) {
                                    if (minion.getTrueOwner() instanceof AbstractIllagerServant servant1) {
                                        servant = servant1;
                                    }
                                }
                            } else if (event.getSource().getEntity() instanceof IOwned minion) {
                                if (minion.getTrueOwner() instanceof AbstractIllagerServant servant1) {
                                    servant = servant1;
                                }
                            }
                        }
                        if (victim.getLastHurtByMob() instanceof AbstractIllagerServant servant1) {
                            servant = servant1;
                        } else {
                            if (event.getSource() instanceof NoKnockBackDamageSource source) {
                                if (source.getOwner() instanceof AbstractIllagerServant servant1) {
                                    servant = servant1;
                                }
                            } else if (event.getSource().getEntity() instanceof AbstractIllagerServant servant1) {
                                servant = servant1;
                            }
                        }
                        if (servant != null){
                            if (servant.getTrueOwner() != null) {
                                servant.addDrops(event.getDrops());
                                event.getDrops().clear();
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTotemUse(LivingUseTotemEvent event) {
        if (event.getSource().is(ModDamageSource.DISMISSED)) {
            event.setCanceled(true);
        }
    }
}
