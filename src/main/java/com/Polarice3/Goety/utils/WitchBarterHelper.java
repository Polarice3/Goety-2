package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.witchbarter.IWitchBarter;
import com.Polarice3.Goety.common.capabilities.witchbarter.WBUpdatePacket;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterImp;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterProvider;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;

import javax.annotation.Nullable;

public class WitchBarterHelper {
    public static IWitchBarter getCapability(LivingEntity livingEntity) {
        return livingEntity.getCapability(WitchBarterProvider.CAPABILITY).orElse(new WitchBarterImp());
    }

    public static int getTimer(Raider witch){
        return getCapability(witch).getTimer();
    }

    public static void setTimer(Raider witch, int timer){
        getCapability(witch).setTimer(timer);
        sendWitchBarterUpdatePacket(witch);
    }

    public static void decreaseTimer(Raider witch){
        setTimer(witch, -1);
        sendWitchBarterUpdatePacket(witch);
    }

    @Nullable
    public static LivingEntity getTrader(Raider witch){
        return witch.level.getEntity(getCapability(witch).getTraderID()) instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    public static void setTrader(Raider witch, @Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            getCapability(witch).setTraderID(livingEntity.getId());
        } else {
            getCapability(witch).setTraderID(-1);
        }
        sendWitchBarterUpdatePacket(witch);
    }

    public static void sendWitchBarterUpdatePacket(Raider witch) {
        if (!witch.level.isClientSide()) {
            ModNetwork.sentToTrackingEntityAndPlayer(witch, new WBUpdatePacket(witch));
        }
    }
}
