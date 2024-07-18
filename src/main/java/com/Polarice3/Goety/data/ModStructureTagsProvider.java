package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.world.structures.ModStructures;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModStructureTagsProvider extends TagsProvider<Structure> {

    public ModStructureTagsProvider(DataGenerator p_126517_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, BuiltinRegistries.STRUCTURES, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    public void addTags() {
        this.tag(ModTags.Structures.WITHER_NECROMANCER_SPAWNS)
                .add(BuiltinStructures.FORTRESS)
                .addOptional(new ResourceLocation("betterfortresses", "better_fortresses"))
                .addOptionalTag(new ResourceLocation("morevillagers", "on_fortress_explorer_maps"));
        this.tag(ModTags.Structures.VIZIER_SPAWNS)
                .addTag(StructureTags.ON_WOODLAND_EXPLORER_MAPS)
                .addOptionalTag(new ResourceLocation("repurposed_structures", "collections/mansions"));
        this.tag(ModTags.Structures.CRONE_SPAWNS).add(ModStructures.BLIGHTED_SHACK_KEY);
        this.tag(ModTags.Structures.SKULL_LORD_SPAWNS).add(ModStructures.CRYPT_KEY);
        this.tag(ModTags.Structures.CRYPT).add(ModStructures.CRYPT_KEY);
        this.tag(ModTags.Structures.NECROMANCER_POWER).add(ModStructures.GRAVEYARD_KEY);
        this.tag(ModTags.Structures.CAN_SUMMON_BRUTES).add(BuiltinStructures.BASTION_REMNANT);
    }
}
