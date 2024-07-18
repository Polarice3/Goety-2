package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.VineHook;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.common.items.magic.GrudgeGrimoire;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.TotemDeathPacket;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulEnergyEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Level world = player.level;
        ISoulEnergy soulEnergy = SEHelper.getCapability(player);
        SEHelper.getFocusCoolDown(player).tick(player, world);
        if (!soulEnergy.getSEActive() && soulEnergy.getSoulEnergy() > 0) {
            if (!world.isClientSide){
                player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_HUNGER.get(), 60));
                if (player.tickCount % 5 == 0) {
                    SEHelper.decreaseSESouls(player, 1);
                    SEHelper.sendSEUpdatePacket(player);
                }
            }
        }
        if (event.phase == TickEvent.Phase.END) {
            if (SEHelper.getRestPeriod(player) > 0) {
                SEHelper.decreaseRestPeriod(player, 1);
            }
            if (SEHelper.hasResearch(player, ResearchList.FORBIDDEN)){
                if (!SEHelper.hasResearch(player, ResearchList.BURIED)){
                    SEHelper.addResearch(player, ResearchList.BURIED);
                }
            }
        }
        if (soulEnergy.getArcaBlock() != null){
            if (soulEnergy.getArcaBlockDimension() == world.dimension()) {
                if (!world.isClientSide){
                    ServerLevel serverWorld = (ServerLevel) world;
                    BlockPos blockPos = soulEnergy.getArcaBlock();
                    BlockEntity tileEntity = world.getBlockEntity(blockPos);
                    if (tileEntity instanceof ArcaBlockEntity arcaTile) {
                        if (arcaTile.getPlayer() == player) {
                            RandomSource pRand = world.random;
                            if (pRand.nextInt(12) == 0) {
                                for (int i = 0; i < 3; ++i) {
                                    int j = pRand.nextInt(2) * 2 - 1;
                                    int k = pRand.nextInt(2) * 2 - 1;
                                    double d0 = (double) blockPos.getX() + 0.5D + 0.25D * (double) j;
                                    double d1 = (float) blockPos.getY() + pRand.nextFloat();
                                    double d2 = (double) blockPos.getZ() + 0.5D + 0.25D * (double) k;
                                    double d3 = pRand.nextFloat() * (float) j;
                                    double d4 = ((double) pRand.nextFloat() - 0.5D) * 0.125D;
                                    double d5 = pRand.nextFloat() * (float) k;
                                    serverWorld.sendParticles(ParticleTypes.ENCHANT, d0, d1, d2, 0, d3, d4, d5, 1.0F);
                                }
                            }
                            if (!soulEnergy.getSEActive()) {
                                soulEnergy.setSEActive(true);
                                SEHelper.sendSEUpdatePacket(player);
                            }
                        } else {
                            if (soulEnergy.getSEActive()) {
                                soulEnergy.setSEActive(false);
                                SEHelper.sendSEUpdatePacket(player);
                            }
                        }
                    } else {
                        if (soulEnergy.getSEActive()) {
                            soulEnergy.setSEActive(false);
                            SEHelper.sendSEUpdatePacket(player);
                        }
                    }
                }
            } else if (soulEnergy.getArcaBlockDimension() == null){
                soulEnergy.setArcaBlockDimension(world.dimension());
                SEHelper.sendSEUpdatePacket(player);
            }
        }
        if (!world.isClientSide) {
            if (soulEnergy.getCameraUUID() != null){
                Entity entity = EntityFinder.getEntityByUuiD(soulEnergy.getCameraUUID());
                if (entity == null || !entity.isAlive()
                        || entity.level.dimension() != player.level.dimension()
                        || player.isShiftKeyDown() || player.hurtTime > 0
                        || !player.isAlive()){
                    SEHelper.setCamera(player, null);
                }
            }
            soulEnergy.grudgeList().removeIf(uuid -> {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                return (entity instanceof Mob mob && (!mob.isAlive() || mob.isRemoved())) || entity == null;
            });
            soulEnergy.allyList().removeIf(uuid -> {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                return (entity instanceof Mob mob && (!mob.isAlive() || mob.isRemoved())) || entity == null;
            });
        }
        int s = soulEnergy.getSoulEnergy();
        if (s < 0){
            soulEnergy.setSoulEnergy(0);
        }
        if (soulEnergy.getGrappling() instanceof VineHook vineHook && vineHook.isAttached()) {
            if (vineHook.isStaff()) {
                player.resetFallDistance();
            }
            Vec3 vec3 = vineHook.position().subtract(player.getEyePosition());
            float f = vineHook.getLength();
            double d = vec3.length();
            if (d > (double)f) {
                double e = d / (double)f * 0.1D;
                player.setDeltaMovement(player.getDeltaMovement().add(vec3.scale(1.0 / d).multiply(e, e * 1.1, e)));
            }
        }
        if (!MobUtil.isSpellCasting(player) && soulEnergy.maxWarding() > 0){
            soulEnergy.setMaxWarding(0);
            soulEnergy.setWarding(0);
        }
    }

    public static boolean canWard(DamageSource damageSource){
        return !ModDamageSource.physicalAttacks(damageSource)
                && !(damageSource.getDirectEntity() instanceof AbstractArrow)
                && damageSource.getEntity() != null;
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player player){
            if (SEHelper.getWardingLeft(player) > 0){
                if (canWard(event.getSource())){
                    SEHelper.damageWarding(player, (int) event.getAmount());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player player){
            if (SEHelper.getWardingLeft(player) > 0){
                if (!canWard(event.getSource())){
                    event.setAmount(event.getAmount() * 0.96F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event){
        if (!event.getEntity().level.isClientSide) {
            SEHelper.setCamera(event.getEntity(), null);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeavesWorld(PlayerEvent.PlayerLoggedOutEvent event){
        if (!event.getEntity().level.isClientSide) {
            SEHelper.setCamera(event.getEntity(), null);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimensions(PlayerEvent.PlayerChangedDimensionEvent event){
        if (!event.getEntity().level.isClientSide) {
            SEHelper.setCamera(event.getEntity(), null);
        }
    }

    @SubscribeEvent
    public static void onPlayerStopTracking(PlayerEvent.StopTracking event){
        ISoulEnergy soulEnergy = SEHelper.getCapability(event.getEntity());
        if (!event.getEntity().level.isClientSide) {
            if (soulEnergy.getCameraUUID() != null){
                Entity entity = EntityFinder.getEntityByUuiD(soulEnergy.getCameraUUID());
                if (entity == event.getTarget()){
                    SEHelper.setCamera(event.getEntity(), null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingInteract(PlayerInteractEvent.EntityInteract event){
        if (!event.getLevel().isClientSide){
            if (event.getItemStack().getItem() instanceof GrudgeGrimoire) {
                if (event.getTarget() instanceof Merchant && event.getTarget() instanceof LivingEntity living) {
                    event.getItemStack().getItem().interactLivingEntity(event.getItemStack(), event.getEntity(), living, event.getHand());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        Entity projectile = event.getSource().getDirectEntity();
        Entity killed = event.getEntity();

        if (killed instanceof LivingEntity victim){
            if (killer instanceof Player player){
                if (!(player instanceof FakePlayer)){
                    if (projectile instanceof Fangs fangEntity && ((Fangs) projectile).isTotemSpawned()){
                        SEHelper.rawHandleKill(player, victim, fangEntity.getSoulEater(), event.getSource());
                    } else {
                        SEHelper.handleKill(player, victim, event.getSource());
                    }
                }
            }

            if (killer instanceof IOwned slayer){
                LivingEntity owner = slayer.getTrueOwner();
                if (owner != null){
                    if (owner instanceof IOwned ownedOwner){
                        if (ownedOwner.getTrueOwner() instanceof Player player){
                            if (CuriosFinder.hasDarkRobe(player) || CuriosFinder.hasUndeadSet(player) || ItemHelper.armorSet(owner, ModArmorMaterials.BLACK_IRON) || ItemHelper.armorSet(player, ModArmorMaterials.DARK)) {
                                if (!(player instanceof FakePlayer)) {
                                    SEHelper.handleKill(player, victim, event.getSource());
                                }
                            }
                        }
                    }
                    if (owner instanceof Player) {
                        if (CuriosFinder.hasDarkRobe(owner) || CuriosFinder.hasUndeadSet(owner) || ItemHelper.armorSet(owner, ModArmorMaterials.BLACK_IRON) || ItemHelper.armorSet(owner, ModArmorMaterials.DARK)) {
                            Player playerEntity = (Player) owner;
                            if (!(playerEntity instanceof FakePlayer)) {
                                SEHelper.handleKill(playerEntity, victim, event.getSource());
                            }
                        }
                    }
                }
            }

            if (victim != killer && killer instanceof LivingEntity) {
                if (CuriosFinder.hasCurio(victim, itemStack -> itemStack.getItem() instanceof ITotem)) {
                    ItemStack itemStack = CuriosFinder.findCurio(victim, itemStack1 -> itemStack1.getItem() instanceof ITotem);
                    ITotem.increaseSouls(itemStack, SEHelper.getSoulGiven(victim) * 2);
                }
            }

            if (!(victim instanceof Player) || !MainConfig.TotemUndying.get()) {
                if (victim.getMainHandItem().getItem() instanceof ITotem){
                    ItemStack itemStack = victim.getMainHandItem();
                    if (revive(itemStack, victim)) {
                        event.setCanceled(true);
                    }
                } else if (victim.getOffhandItem().getItem() instanceof ITotem) {
                    ItemStack itemStack = victim.getOffhandItem();
                    if (revive(itemStack, victim)) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        if (killed instanceof Player player){
            ISoulEnergy soulEnergy = SEHelper.getCapability(player);
            if (soulEnergy.getSEActive()){
                if (soulEnergy.getArcaBlock() != null) {
                    if (MainConfig.ArcaUndying.get()) {
                        if (!player.level.isClientSide) {
                            if (LichdomHelper.isLich(player)) {
                                SEHelper.teleportToArca(player);
                                player.setHealth(1.0F);
                                player.removeAllEffects();
                                if (soulEnergy.getSoulEnergy() > MainConfig.MaxSouls.get()) {
                                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                                } else {
                                    player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_HUNGER.get(), MathHelper.minutesToTicks(2), 4, false, false));
                                }
                                player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.WITHER_DEATH, 1.0F, 1.0F));
                                SEHelper.decreaseSESouls(player, MainConfig.MaxSouls.get());
                                SEHelper.sendSEUpdatePacket(player);
                                event.setCanceled(true);
                            } else if (soulEnergy.getSoulEnergy() > MainConfig.MaxSouls.get()) {
                                SEHelper.teleportToArca(player);
                                player.setHealth(1.0F);
                                player.removeAllEffects();
                                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                                player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.WITHER_DEATH, 1.0F, 1.0F));
                                SEHelper.decreaseSESouls(player, MainConfig.MaxSouls.get());
                                SEHelper.sendSEUpdatePacket(player);
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            } else if (ITotem.UndyingEffect(player)){
                if (!player.level.isClientSide) {
                    player.setHealth(1.0F);
                    player.removeAllEffects();
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                    ModNetwork.sendTo(player, new TotemDeathPacket(player.getUUID()));
                    ITotem.setSoulsamount(TotemFinder.FindTotem(player), 0);
                }
                event.setCanceled(true);
            }
            if (killer instanceof AbstractIllager){
                soulEnergy.setRestPeriod(soulEnergy.getRestPeriod() + MathHelper.minecraftDayToTicks(MobsConfig.IllagerAssaultRestDeath.get()));
            }
        }

    }

    public static boolean revive(ItemStack itemStack, LivingEntity victim){
        if (!itemStack.isEmpty()) {
            if (itemStack.getTag() != null) {
                if (itemStack.getTag().getInt(ITotem.SOULS_AMOUNT) == ITotem.MAX_SOULS) {
                    if (!victim.level.isClientSide) {
                        victim.setHealth(1.0F);
                        victim.removeAllEffects();
                        victim.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                        victim.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                        victim.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                        if (victim instanceof Player) {
                            ModNetwork.sendTo((Player) victim, new TotemDeathPacket(victim.getUUID()));
                        } else {
                            ServerLevel serverWorld = (ServerLevel) victim.level;
                            serverWorld.getChunkSource().broadcast(victim, new ClientboundEntityEventPacket(victim, (byte)35));
                        }
                        ITotem.setSoulsamount(itemStack, 0);
                        if (victim instanceof Mob){
                            itemStack.shrink(1);
                            victim.spawnAtLocation(new ItemStack(ModItems.SPENT_TOTEM.get()));
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
