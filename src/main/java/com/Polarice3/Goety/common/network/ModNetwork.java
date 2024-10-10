package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.LichUpdatePacket;
import com.Polarice3.Goety.common.capabilities.misc.MiscCapUpdatePacket;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEUpdatePacket;
import com.Polarice3.Goety.common.capabilities.witchbarter.WBUpdatePacket;
import com.Polarice3.Goety.common.network.client.*;
import com.Polarice3.Goety.common.network.client.brew.CBrewBagKeyPacket;
import com.Polarice3.Goety.common.network.client.brew.CThrowBrewKeyPacket;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToBagPacket;
import com.Polarice3.Goety.common.network.client.focus.CAddFocusToInventoryPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusPacket;
import com.Polarice3.Goety.common.network.client.focus.CSwapFocusTwoPacket;
import com.Polarice3.Goety.common.network.server.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    public static int nextID() {
        return id++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Goety.MOD_ID, "channel"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), EntityUpdatePacket.class, EntityUpdatePacket::encode, EntityUpdatePacket::decode, EntityUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), SEUpdatePacket.class, SEUpdatePacket::encode, SEUpdatePacket::decode, SEUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), LichUpdatePacket.class, LichUpdatePacket::encode, LichUpdatePacket::decode, LichUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), MiscCapUpdatePacket.class, MiscCapUpdatePacket::encode, MiscCapUpdatePacket::decode, MiscCapUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), CWandKeyPacket.class, CWandKeyPacket::encode, CWandKeyPacket::decode, CWandKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CBagKeyPacket.class, CBagKeyPacket::encode, CBagKeyPacket::decode, CBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CStopAttackPacket.class, CStopAttackPacket::encode, CStopAttackPacket::decode, CStopAttackPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CWitchRobePacket.class, CWitchRobePacket::encode, CWitchRobePacket::decode, CWitchRobePacket::consume);
        INSTANCE.registerMessage(nextID(), CAddWitchFuelKeyPacket.class, CAddWitchFuelKeyPacket::encode, CAddWitchFuelKeyPacket::decode, CAddWitchFuelKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CAddCatalystKeyPacket.class, CAddCatalystKeyPacket::encode, CAddCatalystKeyPacket::decode, CAddCatalystKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CExtractPotionKeyPacket.class, CExtractPotionKeyPacket::encode, CExtractPotionKeyPacket::decode, CExtractPotionKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CRavagerRoarPacket.class, CRavagerRoarPacket::encode, CRavagerRoarPacket::decode, CRavagerRoarPacket::consume);
        INSTANCE.registerMessage(nextID(), CAutoRideablePacket.class, CAutoRideablePacket::encode, CAutoRideablePacket::decode, CAutoRideablePacket::consume);
        INSTANCE.registerMessage(nextID(), CScytheStrikePacket.class, CScytheStrikePacket::encode, CScytheStrikePacket::decode, CScytheStrikePacket::consume);
        INSTANCE.registerMessage(nextID(), CLichKissPacket.class, CLichKissPacket::encode, CLichKissPacket::decode, CLichKissPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CMagnetPacket.class, CMagnetPacket::encode, CMagnetPacket::decode, CMagnetPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSetLichMode.class, CSetLichMode::encode, CSetLichMode::decode, CSetLichMode::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSetLichNightVisionMode.class, CSetLichNightVisionMode::encode, CSetLichNightVisionMode::decode, CSetLichNightVisionMode::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CBeamPacket.class, CBeamPacket::encode, CBeamPacket::decode, CBeamPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CTargetPlayerPacket.class, CTargetPlayerPacket::encode, CTargetPlayerPacket::decode, CTargetPlayerPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSwapFocusPacket.class, CSwapFocusPacket::encode, CSwapFocusPacket::decode, CSwapFocusPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CSwapFocusTwoPacket.class, CSwapFocusTwoPacket::encode, CSwapFocusTwoPacket::decode, CSwapFocusTwoPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CAddFocusToBagPacket.class, CAddFocusToBagPacket::encode, CAddFocusToBagPacket::decode, CAddFocusToBagPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CAddFocusToInventoryPacket.class, CAddFocusToInventoryPacket::encode, CAddFocusToInventoryPacket::decode, CAddFocusToInventoryPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CBrewBagKeyPacket.class, CBrewBagKeyPacket::encode, CBrewBagKeyPacket::decode, CBrewBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CThrowBrewKeyPacket.class, CThrowBrewKeyPacket::encode, CThrowBrewKeyPacket::decode, CThrowBrewKeyPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CMultiJumpPacket.class, CMultiJumpPacket::encode, CMultiJumpPacket::decode, CMultiJumpPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), TotemDeathPacket.class, TotemDeathPacket::encode, TotemDeathPacket::decode, TotemDeathPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayPlayerSoundPacket.class, SPlayPlayerSoundPacket::encode, SPlayPlayerSoundPacket::decode, SPlayPlayerSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayWorldSoundPacket.class, SPlayWorldSoundPacket::encode, SPlayWorldSoundPacket::decode, SPlayWorldSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayEntitySoundPacket.class, SPlayEntitySoundPacket::encode, SPlayEntitySoundPacket::decode, SPlayEntitySoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayLoopSoundPacket.class, SPlayLoopSoundPacket::encode, SPlayLoopSoundPacket::decode, SPlayLoopSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SFungusExplosionPacket.class, SFungusExplosionPacket::encode, SFungusExplosionPacket::decode, SFungusExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SLootingExplosionPacket.class, SLootingExplosionPacket::encode, SLootingExplosionPacket::decode, SLootingExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SApostleSmitePacket.class, SApostleSmitePacket::encode, SApostleSmitePacket::decode, SApostleSmitePacket::consume);
        INSTANCE.registerMessage(nextID(), SRCGlowPacket.class, SRCGlowPacket::encode, SRCGlowPacket::decode, SRCGlowPacket::consume);
        INSTANCE.registerMessage(nextID(), SSoulExplodePacket.class, SSoulExplodePacket::encode, SSoulExplodePacket::decode, SSoulExplodePacket::consume);
        INSTANCE.registerMessage(nextID(), SAddBrewParticlesPacket.class, SAddBrewParticlesPacket::encode, SAddBrewParticlesPacket::decode, SAddBrewParticlesPacket::consume);
        INSTANCE.registerMessage(nextID(), SLightningPacket.class, SLightningPacket::encode, SLightningPacket::decode, SLightningPacket::consume);
        INSTANCE.registerMessage(nextID(), SThunderBoltPacket.class, SThunderBoltPacket::encode, SThunderBoltPacket::decode, SThunderBoltPacket::consume);
        INSTANCE.registerMessage(nextID(), SLightningBoltPacket.class, SLightningBoltPacket::encode, SLightningBoltPacket::decode, SLightningBoltPacket::consume);
        INSTANCE.registerMessage(nextID(), SSetPlayerOwnerPacket.class, SSetPlayerOwnerPacket::encode, SSetPlayerOwnerPacket::decode, SSetPlayerOwnerPacket::consume);
        INSTANCE.registerMessage(nextID(), SUpdateBossBar.class, SUpdateBossBar::encode, SUpdateBossBar::decode, SUpdateBossBar::consume);
        INSTANCE.registerMessage(nextID(), SFocusCooldownPacket.class, SFocusCooldownPacket::encode, SFocusCooldownPacket::decode, SFocusCooldownPacket::consume);
        INSTANCE.registerMessage(nextID(), SRemoveEffectPacket.class, SRemoveEffectPacket::encode, SRemoveEffectPacket::decode, SRemoveEffectPacket::consume);
        INSTANCE.registerMessage(nextID(), SPurifyEffectPacket.class, SPurifyEffectPacket::encode, SPurifyEffectPacket::decode, SPurifyEffectPacket::consume);
        INSTANCE.registerMessage(nextID(), WBUpdatePacket.class, WBUpdatePacket::encode, WBUpdatePacket::decode, WBUpdatePacket::consume);
    }

    public static <MSG> void sendTo(Player player, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
    }

    public static <MSG> void sendToServer(MSG msg) {
        ModNetwork.INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sentToTrackingChunk(LevelChunk chunk, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }

    public static <MSG> void sentToTrackingEntity(Entity entity, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    public static <MSG> void sentToTrackingEntityAndPlayer(Entity entity, MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static <MSG> void sendToALL(MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToClient(ServerPlayer player, MSG msg) {
        ModNetwork.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

}
