package com.Polarice3.Goety.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new ModBlockLootProvider(generator));
        generator.addProvider(event.includeServer(), new ModBlockModelProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModBlockStateProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModEntityTypeTagsProvider(generator, event.getExistingFileHelper()));
    }
}
