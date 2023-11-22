package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModEntityTypeTagsProvider extends TagsProvider<EntityType<?>> {

    public ModEntityTypeTagsProvider(DataGenerator p_126517_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, Registry.ENTITY_TYPE, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModEntityType.WRAITH.get(),
                ModEntityType.WRAITH_SERVANT.get(),
                ModEntityType.BORDER_WRAITH.get(),
                ModEntityType.STRAY_SERVANT.get(),
                ModEntityType.CAIRN_NECROMANCER.get(),
                ModEntityType.HAUNTED_ARMOR.get(),
                ModEntityType.HAUNTED_ARMOR_SERVANT.get());
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
                ModEntityType.PREACHER.get(),
                ModEntityType.MINISTER.get(),
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
    }
}
