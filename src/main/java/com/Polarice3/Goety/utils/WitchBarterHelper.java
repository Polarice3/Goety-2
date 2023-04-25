package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.capabilities.witchbarter.IWitchBarter;
import com.Polarice3.Goety.common.capabilities.witchbarter.WBUpdatePacket;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterImp;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterProvider;
import com.Polarice3.Goety.common.network.ModNetwork;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;

public class WitchBarterHelper {
    public static IWitchBarter getCapability(LivingEntity livingEntity) {
        return livingEntity.getCapability(WitchBarterProvider.CAPABILITY).orElse(new WitchBarterImp());
    }

    public static int getTimer(Raider witch){
        return getCapability(witch).getTimer();
    }

    public static void setTimer(Raider witch, int timer){
        getCapability(witch).setTimer(timer);
    }

    public static void decreaseTimer(Raider witch){
        setTimer(witch, -1);
    }

    public static LivingEntity getTrader(Raider witch){
        return getCapability(witch).getTrader();
    }

    public static void setTrader(Raider witch, LivingEntity livingEntity){
        getCapability(witch).setTrader(livingEntity);
    }

    public static void sendWitchBarterUpdatePacket(Player player, Raider livingEntity) {
        ModNetwork.sendTo(player, new WBUpdatePacket(livingEntity));
    }
}
