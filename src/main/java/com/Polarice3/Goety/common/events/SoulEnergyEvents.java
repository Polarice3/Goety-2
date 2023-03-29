package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.common.network.server.TotemDeathPacket;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TotemFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulEnergyEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Level world = player.level;
        ISoulEnergy soulEnergy = SEHelper.getCapability(player);
        if (!soulEnergy.getSEActive() && soulEnergy.getSoulEnergy() > 0) {
            if (!world.isClientSide){
                player.addEffect(new MobEffectInstance(ModEffects.SOUL_HUNGER.get(), 60));
                if (player.tickCount % 5 == 0) {
                    SEHelper.decreaseSESouls(player, 1);
                    SEHelper.sendSEUpdatePacket(player);
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
        int s = soulEnergy.getSoulEnergy();
        if (s < 0){
            soulEnergy.setSoulEnergy(0);
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
                        SEHelper.rawHandleKill(player, victim, fangEntity.getSoulEater());
                    } else {
                        SEHelper.handleKill(player, victim);
                    }
                }
            }

            if (killer instanceof IOwned slayer){
                LivingEntity owner = slayer.getTrueOwner();
                if (owner != null){
                    if (owner instanceof Player) {
                        if (CuriosFinder.hasDarkRobe(owner)) {
                            Player playerEntity = (Player) owner;
                            if (!(playerEntity instanceof FakePlayer)) {
                                SEHelper.handleKill(playerEntity, victim);
                            }
                        }
                    }
                }
            }

            if (!(victim instanceof Player) || !MainConfig.TotemUndying.get()) {
                if (victim.getMainHandItem().getItem() instanceof TotemOfSouls){
                    ItemStack itemStack = victim.getMainHandItem();
                    if (revive(itemStack, victim)) {
                        event.setCanceled(true);
                    }
                } else if (victim.getOffhandItem().getItem() instanceof TotemOfSouls) {
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
                                SEHelper.teleportDeathArca(player);
                                player.setHealth(1.0F);
                                player.removeAllEffects();
                                if (soulEnergy.getSoulEnergy() > MainConfig.MaxSouls.get()) {
                                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                                } else {
                                    player.addEffect(new MobEffectInstance(ModEffects.SOUL_HUNGER.get(), 12000, 4, false, false));
                                }
                                player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
                                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.WITHER_DEATH, 1.0F, 1.0F));
                                SEHelper.decreaseSESouls(player, MainConfig.MaxSouls.get());
                                SEHelper.sendSEUpdatePacket(player);
                                event.setCanceled(true);
                            } else if (soulEnergy.getSoulEnergy() > MainConfig.MaxSouls.get()) {
                                SEHelper.teleportDeathArca(player);
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
            } else if (TotemOfSouls.UndyingEffect(player)){
                if (!player.level.isClientSide) {
                    player.setHealth(1.0F);
                    player.removeAllEffects();
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                    ModNetwork.sendTo(player, new TotemDeathPacket(player.getUUID()));
                    TotemOfSouls.setSoulsamount(TotemFinder.FindTotem(player), 0);
                }
                event.setCanceled(true);
            }
        }

    }

    public static boolean revive(ItemStack itemStack, LivingEntity victim){
        if (!itemStack.isEmpty()) {
            if (itemStack.getTag() != null) {
                if (itemStack.getTag().getInt(TotemOfSouls.SOULS_AMOUNT) == TotemOfSouls.MAX_SOULS) {
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
                        TotemOfSouls.setSoulsamount(itemStack, 0);
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
