package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ModEntityTypeTagsProvider extends TagsProvider<EntityType<?>> {

    public ModEntityTypeTagsProvider(DataGenerator p_126517_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, Registry.ENTITY_TYPE, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModEntityType.WRAITH.get(),
                ModEntityType.WRAITH_SERVANT.get(),
                ModEntityType.BORDER_WRAITH.get(),
                ModEntityType.FROZEN_ZOMBIE_SERVANT.get(),
                ModEntityType.STRAY_SERVANT.get(),
                ModEntityType.CAIRN_NECROMANCER.get(),
                ModEntityType.HAUNTED_ARMOR.get(),
                ModEntityType.HAUNTED_ARMOR_SERVANT.get());
        this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(ModEntityType.MAGMA_CUBE_SERVANT.get());
        this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(ModEntityType.WRAITH.get(),
                ModEntityType.WRAITH_SERVANT.get(),
                ModEntityType.BORDER_WRAITH.get());
        this.tag(EntityTypeTags.RAIDERS).add(ModEntityType.ARMORED_RAVAGER.get(),
                ModEntityType.WARLOCK.get(),
                ModEntityType.CRONE.get(),
                ModEntityType.ENVIOKER.get(),
                ModEntityType.INQUILLAGER.get(),
                ModEntityType.CONQUILLAGER.get(),
                ModEntityType.PIKER.get(),
                ModEntityType.CRUSHER.get(),
                ModEntityType.STORM_CASTER.get(),
                ModEntityType.PREACHER.get(),
                ModEntityType.MINISTER.get(),
                ModEntityType.HOSTILE_REDSTONE_GOLEM.get(),
                ModEntityType.VIZIER.get());
        this.tag(EntityTypeTags.ARROWS).add(ModEntityType.DEATH_ARROW.get());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModEntityType.SOUL_BOLT.get(),
                ModEntityType.NECRO_BOLT.get(),
                ModEntityType.MAGIC_BOLT.get(),
                ModEntityType.ILL_BOMB.get(),
                ModEntityType.SCYTHE.get(),
                ModEntityType.SWORD.get(),
                ModEntityType.ICE_SPIKE.get(),
                ModEntityType.MOD_FIREBALL.get(),
                ModEntityType.LAVABALL.get(),
                ModEntityType.GRAND_LAVABALL.get(),
                ModEntityType.HAUNTED_SKULL_SHOT.get(),
                ModEntityType.MOD_WITHER_SKULL.get());
        this.tag(EntityTypeTags.FROG_FOOD).add(ModEntityType.SLIME_SERVANT.get(),
                ModEntityType.MAGMA_CUBE_SERVANT.get(),
                ModEntityType.CRYPT_SLIME.get());

        //Based on https://github.com/ochotonida/artifacts/blob/1.20.x/data/src/main/java/artifacts/data/providers/EntityTypeTags.java for max compat
        this.tag(ModTags.EntityTypes.CREEPERS).add(EntityType.CREEPER);

        List<String> creepers = Arrays.asList(
                "jungle_creeper",
                "bamboo_creeper",
                "desert_creeper",
                "badlands_creeper",
                "hills_creeper",
                "savannah_creeper",
                "mushroom_creeper",
                "swamp_creeper",
                "dripstone_creeper",
                "cave_creeper",
                "dark_oak_creeper",
                "spruce_creeper",
                "beach_creeper",
                "snowy_creeper"
        );
        for (String creeper : creepers) {
            this.tag(ModTags.EntityTypes.CREEPERS).addOptional(new ResourceLocation("creeperoverhaul", creeper));
        }
    }
}
