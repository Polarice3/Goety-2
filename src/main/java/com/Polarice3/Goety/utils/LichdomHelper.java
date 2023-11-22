package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.lichdom.ILichdom;
import com.Polarice3.Goety.common.capabilities.lichdom.LichImp;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.lichdom.LichUpdatePacket;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class LichdomHelper {
    public static ILichdom getCapability(Player player) {
        return player.getCapability(LichProvider.CAPABILITY).orElse(new LichImp());
    }

    public static boolean isLich(@Nullable Entity livingEntity) {
        if (livingEntity instanceof Player player){
            return isLich(player);
        } else {
            return false;
        }
    }

    public static boolean isLich(Player player) {
        return getCapability(player).getLichdom();
    }

    public static void sendLichUpdatePacket(Player player) {
        ModNetwork.sendTo(player, new LichUpdatePacket(player));
    }

    public static CompoundTag save(CompoundTag tag, ILichdom lichdom) {
        tag.putBoolean("lichdom", lichdom.getLichdom());
        return tag;
    }

    public static ILichdom load(CompoundTag tag, ILichdom lichdom) {
        lichdom.setLichdom(tag.getBoolean("lichdom"));
        return lichdom;
    }
}
