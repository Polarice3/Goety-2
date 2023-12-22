package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPotPatterns {
    public static final DeferredRegister<String> POT_PATTERNS = DeferredRegister.create(Registries.DECORATED_POT_PATTERNS, Goety.MOD_ID);

    public static void init(){
        ModPotPatterns.POT_PATTERNS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<String> CROSS = create("cross_pottery_pattern");
    public static final RegistryObject<String> DEAD = create("dead_pottery_pattern");
    public static final RegistryObject<String> HAUNT = create("haunt_pottery_pattern");
    public static final RegistryObject<String> NIGHT = create("night_pottery_pattern");
    public static final RegistryObject<String> SOUL = create("soul_pottery_pattern");

    private static RegistryObject<String> create(String name) {
        return POT_PATTERNS.register(name, () -> Goety.MOD_ID + ":" + name);
    }

    public static void addPatterns() {
        ImmutableMap.Builder<Item, ResourceKey<String>> itemsToPot = new ImmutableMap.Builder<>();
        itemsToPot.putAll(DecoratedPotPatterns.ITEM_TO_POT_TEXTURE);
        itemsToPot.put(ModItems.CROSS_POTTERY_SHERD.get(), CROSS.getKey());
        itemsToPot.put(ModItems.DEAD_POTTERY_SHERD.get(), DEAD.getKey());
        itemsToPot.put(ModItems.HAUNT_POTTERY_SHERD.get(), HAUNT.getKey());
        itemsToPot.put(ModItems.NIGHT_POTTERY_SHERD.get(), NIGHT.getKey());
        itemsToPot.put(ModItems.SOUL_POTTERY_SHERD.get(), SOUL.getKey());
        DecoratedPotPatterns.ITEM_TO_POT_TEXTURE = itemsToPot.build();
    }
}
