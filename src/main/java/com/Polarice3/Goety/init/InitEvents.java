package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.misc.MiscProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.capabilities.witchbarter.WitchBarterProvider;
import com.Polarice3.Goety.common.commands.GoetyCommand;
import com.Polarice3.Goety.common.commands.LichCommand;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.listeners.IllagerAssaultListener;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InitEvents {

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        LichCommand.register(commandDispatcher);
        GoetyCommand.register(commandDispatcher);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity){
            event.addCapability(Goety.location("misc"), new MiscProvider());
        }
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Goety.MOD_ID, "soulenergy"), new SEProvider());
            event.addCapability(new ResourceLocation(Goety.MOD_ID, "lichdom"), new LichProvider());
        }
        if (event.getObject() instanceof Witch || event.getObject() instanceof Warlock || event.getObject() instanceof Crone){
            event.addCapability(Goety.location("witchbarter"), new WitchBarterProvider());
        }
    }

    @SubscribeEvent
    public static void registerListeners(AddReloadListenerEvent event) {
        event.addListener(new IllagerAssaultListener());
    }
}
