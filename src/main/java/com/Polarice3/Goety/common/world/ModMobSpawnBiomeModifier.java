package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Based of codes by @AlexModGuy
 */
public class ModMobSpawnBiomeModifier implements BiomeModifier {
    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(Goety.MOD_ID, "mob_spawns"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Goety.MOD_ID);

    public ModMobSpawnBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            ModLevelRegistry.addBiomeSpawns(biome, builder);
        }
    }

    public Codec<? extends BiomeModifier> codec() {
        return (Codec)SERIALIZER.get();
    }

    public static Codec<ModMobSpawnBiomeModifier> makeCodec() {
        return Codec.unit(ModMobSpawnBiomeModifier::new);
    }
}
