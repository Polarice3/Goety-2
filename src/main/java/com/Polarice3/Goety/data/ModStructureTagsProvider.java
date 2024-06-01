package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.world.structures.ModStructures;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

public class ModStructureTagsProvider extends TagsProvider<Structure> {

    public ModStructureTagsProvider(PackOutput p_256522_, CompletableFuture<HolderLookup.Provider> p_256661_, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(p_256522_, Registries.STRUCTURE, p_256661_, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_255894_) {
        this.tag(ModTags.Structures.WITHER_NECROMANCER_SPAWNS).add(BuiltinStructures.FORTRESS);
        this.tag(ModTags.Structures.WITHER_NECROMANCER_SPAWNS).addOptional(new ResourceLocation("betterfortresses", "better_fortresses"));
        this.tag(ModTags.Structures.WITHER_NECROMANCER_SPAWNS).addOptionalTag(new ResourceLocation("morevillagers", "on_fortress_explorer_maps"));
        this.tag(ModTags.Structures.VIZIER_SPAWNS).addTag(StructureTags.ON_WOODLAND_EXPLORER_MAPS);
        this.tag(ModTags.Structures.CRONE_SPAWNS).add(ModStructures.BLIGHTED_SHACK_KEY);
        this.tag(ModTags.Structures.SKULL_LORD_SPAWNS).add(ModStructures.CRYPT_KEY);
        this.tag(ModTags.Structures.CRYPT).add(ModStructures.CRYPT_KEY);
        this.tag(ModTags.Structures.NECROMANCER_POWER).add(ModStructures.GRAVEYARD_KEY);
    }
}
