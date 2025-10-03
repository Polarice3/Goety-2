package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Based on @TeamTwilight's DamageTypeTagGenerator: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/data/tags/DamageTypeTagGenerator.java">...</a>
 */
public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.DAMAGE_TYPE, future, Goety.MOD_ID, helper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(ModDamageSource.DISMISSED);
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(ModDamageSource.PHOBIA,
                        ModDamageSource.DOOM,
                        ModDamageSource.HELLFIRE,
                        ModDamageSource.INDIRECT_HELLFIRE,
                        ModDamageSource.ACID,
                        ModDamageSource.SPIKE,
                        ModDamageSource.MAGIC_BOLT,
                        ModDamageSource.CHOKE,
                        ModDamageSource.VOIDED,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.DEATH);
        this.tag(DamageTypeTags.BYPASSES_SHIELD)
                .add(ModDamageSource.ICE_BOUQUET,
                        ModDamageSource.SPIKE,
                        ModDamageSource.HELLFIRE,
                        ModDamageSource.INDIRECT_HELLFIRE,
                        ModDamageSource.SOUL_LEECH,
                        ModDamageSource.LIFE_LEECH,
                        ModDamageSource.CHOKE,
                        ModDamageSource.VOIDED,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.DEATH);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(ModDamageSource.DOOM,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.VOIDED);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(ModDamageSource.DOOM,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.VOIDED);
        this.tag(DamageTypeTags.BYPASSES_EFFECTS)
                .add(ModDamageSource.DOOM,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.VOIDED);
        this.tag(DamageTypeTags.IS_PROJECTILE)
                .add(ModDamageSource.ICE_SPIKE,
                        ModDamageSource.NO_OWNER_MAGIC_FIREBALL,
                        ModDamageSource.MAGIC_FIREBALL);
        this.tag(DamageTypeTags.IS_FIRE)
                .add(ModDamageSource.BOILING,
                        ModDamageSource.FIRE_BREATH);
        this.tag(DamageTypeTags.IS_FREEZING)
                .add(ModDamageSource.DIRECT_FREEZE,
                        ModDamageSource.INDIRECT_FREEZE,
                        ModDamageSource.FROST_BREATH,
                        ModDamageSource.ICE_SPIKE,
                        ModDamageSource.ICE_BOUQUET);
        this.tag(DamageTypeTags.IS_EXPLOSION)
                .add(ModDamageSource.LOOT_EXPLODE,
                        ModDamageSource.LOOT_EXPLODE_OWNED);
        this.tag(DamageTypeTags.WITCH_RESISTANT_TO)
                .add(ModDamageSource.PHOBIA,
                        ModDamageSource.ICE_BOUQUET,
                        ModDamageSource.ACID,
                        ModDamageSource.SPIKE,
                        ModDamageSource.MAGIC_BOLT,
                        ModDamageSource.WIND_BLAST,
                        ModDamageSource.SOUL_LEECH,
                        ModDamageSource.LIFE_LEECH);
        this.tag(ModTags.DamageTypes.NO_KNOCKBACK)
                .add(ModDamageSource.ICE_BOUQUET,
                        ModDamageSource.ACID,
                        ModDamageSource.SPIKE,
                        ModDamageSource.HELLFIRE,
                        ModDamageSource.INDIRECT_HELLFIRE,
                        ModDamageSource.FIRE_BREATH,
                        ModDamageSource.MAGIC_FIRE,
                        ModDamageSource.FROST_BREATH,
                        ModDamageSource.MAGIC_BOLT,
                        ModDamageSource.SOUL_LEECH,
                        ModDamageSource.LIFE_LEECH,
                        ModDamageSource.CHOKE,
                        ModDamageSource.SWARM,
                        ModDamageSource.VOIDED,
                        ModDamageSource.DISMISSED,
                        ModDamageSource.DEATH);
        this.tag(ModTags.DamageTypes.PHYSICAL)
                .add(DamageTypes.PLAYER_ATTACK,
                        DamageTypes.MOB_ATTACK,
                        DamageTypes.MOB_ATTACK_NO_AGGRO,
                        DamageTypes.STING,
                        ModDamageSource.SUMMON);
        this.tag(ModTags.DamageTypes.FIRE_ATTACKS)
                .addTag(ModTags.DamageTypes.MAGIC_FIRE)
                .addTag(ModTags.DamageTypes.HELLFIRE)
                .add(DamageTypes.FIREBALL,
                        DamageTypes.UNATTRIBUTED_FIREBALL,
                        ModDamageSource.FIRE_BREATH);
        this.tag(ModTags.DamageTypes.FROST_ATTACKS)
                .add(ModDamageSource.FROST_BREATH,
                        ModDamageSource.ICE_BOUQUET,
                        ModDamageSource.ICE_SPIKE,
                        ModDamageSource.DIRECT_FREEZE,
                        ModDamageSource.INDIRECT_FREEZE);
        this.tag(ModTags.DamageTypes.SHOCK_ATTACKS)
                .add(DamageTypes.LIGHTNING_BOLT,
                        ModDamageSource.SHOCK,
                        ModDamageSource.DIRECT_SHOCK,
                        ModDamageSource.INDIRECT_SHOCK,
                        ModDamageSource.LIGHTNING);
        this.tag(ModTags.DamageTypes.WATER_ATTACKS)
                .add(ModDamageSource.BUBBLE_STREAM,
                        ModDamageSource.DRENCH,
                        ModDamageSource.DIRECT_DRENCH,
                        ModDamageSource.INDIRECT_DRENCH);
        this.tag(ModTags.DamageTypes.MAGIC_FIRE)
                .add(ModDamageSource.MAGIC_FIRE,
                        ModDamageSource.MAGIC_FIREBALL,
                        ModDamageSource.NO_OWNER_MAGIC_FIREBALL);
        this.tag(ModTags.DamageTypes.HELLFIRE)
                .add(ModDamageSource.HELLFIRE,
                        ModDamageSource.INDIRECT_HELLFIRE);
        this.tag(ModTags.DamageTypes.WANTING_DAMAGE)
                .addTag(DamageTypeTags.WITCH_RESISTANT_TO)
                .addTag(ModTags.DamageTypes.NO_KNOCKBACK)
                .addTag(ModTags.DamageTypes.FIRE_ATTACKS)
                .addTag(ModTags.DamageTypes.FROST_ATTACKS)
                .addTag(ModTags.DamageTypes.SHOCK_ATTACKS)
                .addTag(ModTags.DamageTypes.WATER_ATTACKS)
                .add(DamageTypes.WITHER_SKULL)
                .add(ModDamageSource.LOOT_EXPLODE,
                        ModDamageSource.LOOT_EXPLODE_OWNED);
    }
}
