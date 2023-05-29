package com.Polarice3.Goety.common.network;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.LichUpdatePacket;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEUpdatePacket;
import com.Polarice3.Goety.common.capabilities.witchbarter.WBUpdatePacket;
import com.Polarice3.Goety.common.network.client.*;
import com.Polarice3.Goety.common.network.server.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

        INSTANCE.registerMessage(nextID(), SEUpdatePacket.class, SEUpdatePacket::encode, SEUpdatePacket::decode, SEUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), LichUpdatePacket.class, LichUpdatePacket::encode, LichUpdatePacket::decode, LichUpdatePacket::consume);
        INSTANCE.registerMessage(nextID(), CWandKeyPacket.class, CWandKeyPacket::encode, CWandKeyPacket::decode, CWandKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CBagKeyPacket.class, CBagKeyPacket::encode, CBagKeyPacket::decode, CBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CStopAttackPacket.class, CStopAttackPacket::encode, CStopAttackPacket::decode, CStopAttackPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CWandAndBagKeyPacket.class, CWandAndBagKeyPacket::encode, CWandAndBagKeyPacket::decode, CWandAndBagKeyPacket::consume);
        INSTANCE.registerMessage(nextID(), CWitchRobePacket.class, CWitchRobePacket::encode, CWitchRobePacket::decode, CWitchRobePacket::consume);
        INSTANCE.registerMessage(nextID(), CScytheStrikePacket.class, CScytheStrikePacket::encode, CScytheStrikePacket::decode, CScytheStrikePacket::consume);
        INSTANCE.registerMessage(nextID(), CLichKissPacket.class, CLichKissPacket::encode, CLichKissPacket::decode, CLichKissPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), CMagnetPacket.class, CMagnetPacket::encode, CMagnetPacket::decode, CMagnetPacket::consume, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(nextID(), TotemDeathPacket.class, TotemDeathPacket::encode, TotemDeathPacket::decode, TotemDeathPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayPlayerSoundPacket.class, SPlayPlayerSoundPacket::encode, SPlayPlayerSoundPacket::decode, SPlayPlayerSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SPlayWorldSoundPacket.class, SPlayWorldSoundPacket::encode, SPlayWorldSoundPacket::decode, SPlayWorldSoundPacket::consume);
        INSTANCE.registerMessage(nextID(), SFungusExplosionPacket.class, SFungusExplosionPacket::encode, SFungusExplosionPacket::decode, SFungusExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SLootingExplosionPacket.class, SLootingExplosionPacket::encode, SLootingExplosionPacket::decode, SLootingExplosionPacket::consume);
        INSTANCE.registerMessage(nextID(), SApostleSmitePacket.class, SApostleSmitePacket::encode, SApostleSmitePacket::decode, SApostleSmitePacket::consume);
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

    public static <MSG> void sendToALL(MSG msg) {
        ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

}
