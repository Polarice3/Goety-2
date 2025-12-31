package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.misc.IMisc;
import com.Polarice3.Goety.common.capabilities.misc.MiscCapUpdatePacket;
import com.Polarice3.Goety.common.capabilities.misc.MiscImp;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
        getCapability(livingEntity).increaseAmbientSoundTime();
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void setAmbientSoundTime(LivingEntity livingEntity, int interval) {
        getCapability(livingEntity).setAmbientSoundTime(interval);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static int getClientTargetID(LivingEntity livingEntity){
        return getCapability(livingEntity).getClientTargetID();
    }

    public static void setClientTargetID(LivingEntity livingEntity, int clientTargetID){
        getCapability(livingEntity).setClientTargetID(clientTargetID);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    @Nullable
    public static Entity getClientTarget(LivingEntity livingEntity){
        return livingEntity.level.getEntity(getClientTargetID(livingEntity));
    }

    public static void setClientTarget(LivingEntity livingEntity, @Nullable Entity entity){
        if (entity == null){
            setClientTargetID(livingEntity, 0);
        } else {
            setClientTargetID(livingEntity, entity.getId());
        }
    }

    public static int getMobTargetID(LivingEntity livingEntity){
        return getCapability(livingEntity).getMobTargetID();
    }

    public static void setMobTargetID(LivingEntity livingEntity, int mob){
        getCapability(livingEntity).setMobTargetID(mob);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    @Nullable
    public static Entity getMobTarget(LivingEntity livingEntity){
        return livingEntity.level.getEntity(getMobTargetID(livingEntity));
    }

    public static void setMobTarget(LivingEntity livingEntity, @Nullable Entity entity){
        if (entity == null){
            setMobTargetID(livingEntity, 0);
        } else {
            setMobTargetID(livingEntity, entity.getId());
        }
    }

    public static void updateMobTarget(Mob mob){
        if (!mob.level.isClientSide) {
            if (getMobTarget(mob) != mob.getTarget()) {
                setMobTarget(mob, mob.getTarget());
            }
        }
    }

    public static int getNoHealTime(LivingEntity livingEntity){
        return getCapability(livingEntity).getNoHealTime();
    }

    public static void setNoHealTime(LivingEntity livingEntity, int seconds){
        getCapability(livingEntity).setNoHealTime(seconds);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static int getShakeTime(LivingEntity livingEntity){
        return getCapability(livingEntity).getShakeTime();
    }

    public static void setShakeTime(LivingEntity livingEntity, int ticks){
        getCapability(livingEntity).setShakeTime(ticks);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    @Nullable
    public static ResourceLocation getCustomSpinTexture(LivingEntity livingEntity){
        String string = getCapability(livingEntity).customSpinTexture();
        if (string.isEmpty()){
            return null;
        }
        return new ResourceLocation(getCapability(livingEntity).customSpinTexture());
    }

    public static void setCustomSpinTexture(LivingEntity livingEntity, @Nullable String string){
        if (string == null) {
            string = "";
        }
        getCapability(livingEntity).setCustomSpinTexture(string);
        MiscCapHelper.sendMiscUpdatePacket(livingEntity);
    }

    public static void clearGoals(GoalSelector goalSelector) {
        ArrayList<WrappedGoal> wrappedGoals = new ArrayList<>(goalSelector.getAvailableGoals());
        wrappedGoals.forEach(prioritizedGoal -> goalSelector.removeGoal(prioritizedGoal.getGoal()));
    }

    public static CompoundTag save(CompoundTag tag, IMisc misc) {
        tag.putInt("freezeLevel", misc.freezeLevel());
        tag.putInt("shields", misc.shieldsLeft());
        tag.putInt("shieldTime", misc.shieldTime());
        tag.putInt("shieldCool", misc.shieldCool());
        tag.putInt("ambientSoundTime", misc.ambientSoundTime());
        tag.putInt("clientTargetID", misc.getClientTargetID());
        tag.putInt("mobTargetID", misc.getMobTargetID());
        if (misc.getNoHealTime() > 0) {
            tag.putInt("noHealTime", misc.getNoHealTime());
        }
        tag.putInt("shakeTime", misc.getShakeTime());
        if (!misc.customSpinTexture().isEmpty()) {
            tag.putString("customSpinTexture", misc.customSpinTexture());
        }
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
        if (tag.contains("clientTargetID")){
            misc.setClientTargetID(tag.getInt("clientTargetID"));
        }
        if (tag.contains("mobTargetID")){
            misc.setMobTargetID(tag.getInt("mobTargetID"));
        }
        if (tag.contains("noHealTime")){
            misc.setNoHealTime(tag.getInt("noHealTime"));
        }
        if (tag.contains("shakeTime")){
            misc.setShakeTime(tag.getInt("shakeTime"));
        }
        if (tag.contains("customSpinTexture")) {
            misc.setCustomSpinTexture(tag.getString("customSpinTexture"));
        }
        return misc;
    }

    public static void sendMiscUpdatePacket(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide) {
            ModNetwork.sentToTrackingEntityAndPlayer(livingEntity, new MiscCapUpdatePacket(livingEntity));
        }
    }
}
