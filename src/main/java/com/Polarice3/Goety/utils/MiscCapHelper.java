package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.misc.IMisc;
import com.Polarice3.Goety.common.capabilities.misc.MiscCapUpdatePacket;
import com.Polarice3.Goety.common.capabilities.misc.MiscImp;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MiscCapHelper {
    public static IMisc getCapability(LivingEntity livingEntity) {
        return livingEntity.getCapability(MiscProvider.CAPABILITY).orElse(new MiscImp());
    }

    public static boolean isFreezing(LivingEntity livingEntity){
        return getCapability(livingEntity).freezeLevel() > 0;
    }

    public static int freezeLevel(LivingEntity livingEntity){
        return getCapability(livingEntity).freezeLevel();
    }

    public static void setFreezing(LivingEntity livingEntity, int freeze){
        getCapability(livingEntity).setFreezeLevel(freeze);
        if (!livingEntity.level.isClientSide){
            sendMiscUpdatePacket(livingEntity);
        }
    }

    public static CompoundTag save(CompoundTag tag, IMisc misc) {
        tag.putInt("freezeLevel", misc.freezeLevel());
        return tag;
    }

    public static IMisc load(CompoundTag tag, IMisc misc) {
        misc.setFreezeLevel(tag.getInt("freezeLevel"));
        return misc;
    }

    public static void sendMiscUpdatePacket(Player player, LivingEntity livingEntity) {
        ModNetwork.sendTo(player, new MiscCapUpdatePacket(livingEntity));
    }

    public static void sendMiscUpdatePacket(LivingEntity livingEntity) {
        ModNetwork.sendToALL(new MiscCapUpdatePacket(livingEntity));
    }
}
