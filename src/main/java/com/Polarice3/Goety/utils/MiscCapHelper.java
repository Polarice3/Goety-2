package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.misc.IMisc;
import com.Polarice3.Goety.common.capabilities.misc.MiscCapUpdatePacket;
import com.Polarice3.Goety.common.capabilities.misc.MiscImp;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
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

    public static int getShields(LivingEntity livingEntity){
        return getCapability(livingEntity).shieldsLeft();
    }

    public static void setShields(LivingEntity livingEntity, int amount){
        getCapability(livingEntity).setShields(amount);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void increaseShields(LivingEntity livingEntity){
        getCapability(livingEntity).increaseShields();
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void decreaseShields(LivingEntity livingEntity){
        getCapability(livingEntity).breakShield();
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
        if (!livingEntity.level.isClientSide){
            if (livingEntity instanceof Player player) {
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.SHIELD_BREAK, 1.0F, 1.0F));
            } else {
                livingEntity.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 1.0F);
            }
        }
    }

    public static int getShieldTime(LivingEntity livingEntity){
        return getCapability(livingEntity).shieldTime();
    }

    public static void setShieldTime(LivingEntity livingEntity, int time){
        getCapability(livingEntity).setShieldTime(time);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void decreaseShieldTime(LivingEntity livingEntity){
        getCapability(livingEntity).decreaseShieldTime();
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static int getShieldCool(LivingEntity livingEntity){
        return getCapability(livingEntity).shieldCool();
    }

    public static void setShieldCool(LivingEntity livingEntity, int cool){
        getCapability(livingEntity).setShieldCool(cool);
    }

    public static void decreaseShieldCool(LivingEntity livingEntity){
        getCapability(livingEntity).decreaseShieldCool();
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static int getAmbientSoundTime(LivingEntity livingEntity){
        return getCapability(livingEntity).ambientSoundTime();
    }

    public static void doAmbientSoundTime(LivingEntity livingEntity){
        getCapability(livingEntity).setAmbientSoundTime(getAmbientSoundTime(livingEntity) + 1);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void resetAmbientSoundTime(LivingEntity livingEntity, int interval) {
        getCapability(livingEntity).setAmbientSoundTime(getAmbientSoundTime(livingEntity) - interval);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static CompoundTag save(CompoundTag tag, IMisc misc) {
        tag.putInt("freezeLevel", misc.freezeLevel());
        tag.putInt("shields", misc.shieldsLeft());
        tag.putInt("shieldTime", misc.shieldTime());
        tag.putInt("shieldCool", misc.shieldCool());
        tag.putInt("ambientSoundTime", misc.ambientSoundTime());
        return tag;
    }

    public static IMisc load(CompoundTag tag, IMisc misc) {
        if (tag.contains("freezeLevel")) {
            misc.setFreezeLevel(tag.getInt("freezeLevel"));
        }
        if (tag.contains("shields")) {
            misc.setShields(tag.getInt("shields"));
        }
        if (tag.contains("shieldTime")) {
            misc.setShieldTime(tag.getInt("shieldTime"));
        }
        if (tag.contains("shieldCool")) {
            misc.setShieldCool(tag.getInt("shieldCool"));
        }
        if (tag.contains("ambientSoundTime")) {
            misc.setAmbientSoundTime(tag.getInt("ambientSoundTime"));
        }
        return misc;
    }

    public static void sendMiscUpdatePacket(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide) {
            ModNetwork.sentToTrackingEntityAndPlayer(livingEntity, new MiscCapUpdatePacket(livingEntity));
        }
    }
}
