package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.lichdom.LichImp;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.lichdom.LichUpdatePacket;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class LichdomHelper {
    public static ILichdom getCapability(Player player) {
        return player.getCapability(LichProvider.CAPABILITY).orElse(new LichImp());
    }

    public static void setLich(Player player, boolean lich) {
        getCapability(player).setLichdom(lich);
        sendLichUpdatePacket(player);
    }

    public static boolean isLich(@Nullable Entity livingEntity) {
        if (livingEntity instanceof Player player){
            return isLich(player);
        } else {
            return false;
        }
    }

    public static boolean isInLichMode(LivingEntity livingEntity){
        if (livingEntity instanceof Player player){
            return isInLichMode(player);
        } else {
            return false;
        }
    }

    public static boolean isInLichMode(Player player){
        return getCapability(player).isLichMode();
    }

    public static void setLichMode(Player player, boolean lichMode){
        getCapability(player).setLichMode(lichMode);
        sendLichUpdatePacket(player);
    }

    public static boolean nightVision(Player player){
        return getCapability(player).nightVision();
    }

    public static void setNightVision(Player player, boolean nightVision){
        getCapability(player).setNightVision(nightVision);
        sendLichUpdatePacket(player);
    }

    public static int smited(Player player){
        return getCapability(player).smited();
    }

    public static void setSmited(Player player, int smited){
        getCapability(player).setSmited(smited);
        sendLichUpdatePacket(player);
    }

    public static int lichModeColor(Player player){
        return getCapability(player).lichModeColor();
    }

    public static int lichModeColor(LivingEntity livingEntity){
        if (livingEntity instanceof Player player){
            return lichModeColor(player);
        } else {
            return -1;
        }
    }

    public static void setLichModeColor(Player player, int colorCode){
        getCapability(player).setLichModeColor(colorCode);
        sendLichUpdatePacket(player);
    }

    public static boolean isLich(Player player) {
        return getCapability(player).getLichdom();
    }

    public static void sendLichUpdatePacket(Player player) {
        if (!player.level.isClientSide) {
            ModNetwork.sendToALL(new LichUpdatePacket(player));
        }
    }

    public static CompoundTag save(CompoundTag tag, ILichdom lichdom) {
        tag.putBoolean("lichdom", lichdom.getLichdom());
        tag.putBoolean("lichMode", lichdom.isLichMode());
        tag.putBoolean("nightVision", lichdom.nightVision());
        tag.putInt("smited", lichdom.smited());
        tag.putInt("LichModeColor", lichdom.lichModeColor());
        return tag;
    }

    public static ILichdom load(CompoundTag tag, ILichdom lichdom) {
        if (tag.contains("lichdom")) {
            lichdom.setLichdom(tag.getBoolean("lichdom"));
        }
        if (tag.contains("lichMode")) {
            lichdom.setLichMode(tag.getBoolean("lichMode"));
        }
        if (tag.contains("nightVision")) {
            lichdom.setNightVision(tag.getBoolean("nightVision"));
        }
        if (tag.contains("smited")) {
            lichdom.setSmited(tag.getInt("smited"));
        }
        if (tag.contains("LichModeColor")) {
            lichdom.setLichModeColor(tag.getInt("LichModeColor"));
        }
        return lichdom;
    }
}
