package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Based on @TeamTwilight's DamageTypeTagGenerator: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/data/tags/DamageTypeTagGenerator.java">...</a>
 */
public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {
    public static final TagKey<DamageType> NO_KNOCKBACK = create("no_knockback");

    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.DAMAGE_TYPE, future, Goety.MOD_ID, helper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageSource.PHOBIA, ModDamageSource.DIRECT_FREEZE, ModDamageSource.INDIRECT_FREEZE, ModDamageSource.ICE_BOUQUET, ModDamageSource.MAGIC_BOLT, ModDamageSource.WIND_BLAST);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(ModDamageSource.ICE_SPIKE);
        this.tag(DamageTypeTags.IS_FIRE).add(ModDamageSource.BOILING, ModDamageSource.FIRE_BREATH);
        this.tag(DamageTypeTags.IS_FREEZING).add(ModDamageSource.DIRECT_FREEZE, ModDamageSource.INDIRECT_FREEZE, ModDamageSource.ICE_SPIKE, ModDamageSource.ICE_BOUQUET);
        this.tag(DamageTypeTags.WITCH_RESISTANT_TO).add(ModDamageSource.PHOBIA, ModDamageSource.ICE_BOUQUET, ModDamageSource.MAGIC_BOLT, ModDamageSource.WIND_BLAST, ModDamageSource.SOUL_LEECH);
        this.tag(NO_KNOCKBACK).add(ModDamageSource.ICE_BOUQUET, ModDamageSource.FIRE_BREATH, ModDamageSource.MAGIC_BOLT, ModDamageSource.SOUL_LEECH);
    }

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, Goety.location(name));
    }
}
