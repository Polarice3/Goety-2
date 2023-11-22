package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Map;

public class ModTrimMaterials {
    public static final ResourceKey<TrimMaterial> CURSED = registerKey("cursed");
    public static final ResourceKey<TrimMaterial> DARK = registerKey("dark");
    public static final ResourceKey<TrimMaterial> ECTO = registerKey("ecto");
    public static final ResourceKey<TrimMaterial> JADE = registerKey("jade");

    public static void bootstrap(BootstapContext<TrimMaterial> context) {
        register(context, CURSED, ModItems.CURSED_METAL_INGOT.get(), Style.EMPTY.withColor(0x1c303a), 0.9F);
        register(context, DARK, ModItems.DARK_METAL_INGOT.get(), Style.EMPTY.withColor(0x1f1f1f), 0.3F);
        register(context, ECTO, ModItems.ECTOPLASM.get(), Style.EMPTY.withColor(0x00c2d6), 0.8F);
        register(context, JADE, ModItems.JADE.get(), Style.EMPTY.withColor(0x00fa68), 0.7F);
    }

    private static ResourceKey<TrimMaterial> registerKey(String name) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, Goety.location(name));
    }

    private static void register(BootstapContext<TrimMaterial> p_268176_, ResourceKey<TrimMaterial> p_268293_, Item p_268156_, Style p_268174_, float p_268274_) {
        register(p_268176_, p_268293_, p_268156_, p_268174_, p_268274_, Map.of());
    }

    private static void register(BootstapContext<TrimMaterial> p_268244_, ResourceKey<TrimMaterial> p_268139_, Item p_268311_, Style p_268232_, float p_268197_, Map<ArmorMaterials, String> p_268352_) {
        TrimMaterial trimmaterial = TrimMaterial.create(p_268139_.location().getPath(), p_268311_, p_268197_, Component.translatable(Util.makeDescriptionId("trim_material", p_268139_.location())).withStyle(p_268232_), p_268352_);
        p_268244_.register(p_268139_, trimmaterial);
    }
}
