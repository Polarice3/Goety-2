package com.Polarice3.Goety.common.world;

import com.Polarice3.Goety.Goety;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Based of codes by @AlexModGuy
 */
public class ModMobSpawnStructureModifier implements StructureModifier {
    private static final RegistryObject<Codec<? extends StructureModifier >> SERIALIZER = RegistryObject.create(new ResourceLocation(Goety.MOD_ID, "mob_structure_spawns"), ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Goety.MOD_ID);

    public ModMobSpawnStructureModifier() {
    }

    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD) {
            ModLevelRegistry.addStructureSpawns(structure, builder);
        }
    }

    public Codec<? extends StructureModifier > codec() {
        return (Codec)SERIALIZER.get();
    }

    public static Codec<ModMobSpawnStructureModifier> makeCodec() {
        return Codec.unit(ModMobSpawnStructureModifier::new);
    }
}
