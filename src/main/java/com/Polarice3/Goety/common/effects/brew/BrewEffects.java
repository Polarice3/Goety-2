package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.block.*;
import com.Polarice3.Goety.common.effects.brew.modifiers.BrewModifier;
import com.Polarice3.Goety.common.effects.brew.modifiers.CapacityModifier;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.BrewConfig;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class BrewEffects {
    public static BrewEffects INSTANCE = new BrewEffects();
    private final Map<String, BrewEffect> effectIDs = Maps.newHashMap();
    private final Map<Item, BrewEffect> catalyst = Maps.newHashMap();
    private final Map<EntityType<?>, BrewEffect> sacrifice = Maps.newHashMap();
    /** Made for external recipe viewers like Patchouli */
    private final Map<String, ItemStack> catalystInverted = Maps.newHashMap();
    private final Map<String, EntityType<?>> sacrificeInverted = Maps.newHashMap();
    private final Map<Item, BrewModifier> modifiers = Maps.newHashMap();

    public BrewEffects(){
        //Modifiers
        this.modifierRegister(new CapacityModifier(0), Items.NETHER_WART);
        this.modifierRegister(new CapacityModifier(1), Items.CRIMSON_FUNGUS);
        this.modifierRegister(new CapacityModifier(2), ModBlocks.SNAP_WARTS_ITEM.get());
        this.modifierRegister(new CapacityModifier(3), ModItems.MAGIC_EMERALD.get());
        this.modifierRegister(new CapacityModifier(4), ModItems.SOUL_EMERALD.get());
        this.modifierRegister(new CapacityModifier(5), ModItems.SOUL_RUBY.get());
        this.modifierRegister(new BrewModifier(BrewModifier.DURATION, 0), Items.REDSTONE);
        this.modifierRegister(new BrewModifier(BrewModifier.DURATION, 1), Items.PRISMARINE);
        this.modifierRegister(new BrewModifier(BrewModifier.DURATION, 2), Items.CHORUS_FLOWER);
        this.modifierRegister(new BrewModifier(BrewModifier.AMPLIFIER, 0), Items.GLOWSTONE_DUST);
        this.modifierRegister(new BrewModifier(BrewModifier.AMPLIFIER, 1), Items.BLAZE_ROD);
        this.modifierRegister(new BrewModifier(BrewModifier.AMPLIFIER, 2), ModItems.MYSTIC_CORE.get());
        this.modifierRegister(new BrewModifier(BrewModifier.AOE, 0), Items.CHARCOAL);
        this.modifierRegister(new BrewModifier(BrewModifier.AOE, 1), Items.FIREWORK_STAR);
        this.modifierRegister(new BrewModifier(BrewModifier.AOE, 2), ModBlocks.TALL_SKULL_ITEM.get());
        this.modifierRegister(new BrewModifier(BrewModifier.LINGER, 0), Items.HANGING_ROOTS);
        this.modifierRegister(new BrewModifier(BrewModifier.LINGER, 1), Items.BIG_DRIPLEAF);
        this.modifierRegister(new BrewModifier(BrewModifier.LINGER, 2), Items.SPORE_BLOSSOM);
        this.modifierRegister(new BrewModifier(BrewModifier.QUAFF, 0), Items.HONEY_BOTTLE);
        this.modifierRegister(new BrewModifier(BrewModifier.QUAFF, 1), Items.GLOW_LICHEN);
        this.modifierRegister(new BrewModifier(BrewModifier.QUAFF, 2), Items.TURTLE_EGG);
        this.modifierRegister(new BrewModifier(BrewModifier.VELOCITY, 0), Items.SNOWBALL);
        this.modifierRegister(new BrewModifier(BrewModifier.VELOCITY, 1), Items.BOW);
        this.modifierRegister(new BrewModifier(BrewModifier.VELOCITY, 2), Items.CROSSBOW);
        this.modifierRegister(new BrewModifier(BrewModifier.AQUATIC), Items.TUBE_CORAL);
        this.modifierRegister(new BrewModifier(BrewModifier.FIRE_PROOF), Items.NETHERITE_SCRAP);
        this.modifierRegister(new BrewModifier(BrewModifier.HIDDEN), Items.ENDER_EYE);
        this.modifierRegister(new BrewModifier(BrewModifier.SPLASH), Items.GUNPOWDER);
        this.modifierRegister(new BrewModifier(BrewModifier.LINGERING), Items.DRAGON_BREATH);
        this.modifierRegister(new BrewModifier(BrewModifier.GAS), ModItems.WIND_CORE.get());

        //Vanilla
        this.register(new PotionBrewEffect(MobEffects.ABSORPTION, BrewConfig.AbsorptionCost.get(), 1, 1800), Items.GOLDEN_APPLE);
        this.register(new PotionBrewEffect(MobEffects.BLINDNESS, BrewConfig.BlindnessCost.get(), 1800), Items.INK_SAC);
        this.register(new PotionBrewEffect(MobEffects.DARKNESS, BrewConfig.DarknessCost.get(), 1800), Items.SCULK);
        this.register(new PotionBrewEffect(MobEffects.FIRE_RESISTANCE, BrewConfig.FireResistanceCost.get(), 3600), Items.MAGMA_CREAM);
        this.register(new PotionBrewEffect(MobEffects.GLOWING, BrewConfig.GlowingCost.get(), 3600), Items.GLOW_INK_SAC);
        this.register(new PotionBrewEffect(MobEffects.DIG_SPEED, BrewConfig.HasteCost.get(), 3600), Items.COOKIE);
        this.register(new PotionBrewEffect(MobEffects.HUNGER, BrewConfig.HungerCost.get(), 1800), Items.ROTTEN_FLESH);
        this.register(new PotionBrewEffect(MobEffects.HARM, BrewConfig.HarmingCost.get(), 1), Items.BRICK);
        this.register(new PotionBrewEffect(MobEffects.HEAL, BrewConfig.HealingCost.get(), 1), Items.GLISTERING_MELON_SLICE);
        this.register(new PotionBrewEffect(MobEffects.HEALTH_BOOST, BrewConfig.HealthBoostCost.get(), 2, 1800), Items.ENCHANTED_GOLDEN_APPLE);
        this.register(new PotionBrewEffect(MobEffects.INVISIBILITY, BrewConfig.InvisibilityCost.get(), 3600), Items.GLASS_PANE);
        this.register(new PotionBrewEffect(MobEffects.JUMP, BrewConfig.JumpBoostCost.get(), 3600), Items.RABBIT_FOOT);
        this.register(new PotionBrewEffect(MobEffects.LEVITATION, BrewConfig.LevitationCost.get(), 2, 600), Items.SHULKER_SHELL);
        this.register(new PotionBrewEffect(MobEffects.LUCK, BrewConfig.LuckCost.get(), 3600), Items.NAUTILUS_SHELL);
        this.register(new PotionBrewEffect(MobEffects.DIG_SLOWDOWN, BrewConfig.MiningFatigueCost.get(), 1800), Items.PRISMARINE_SHARD);
        this.register(new PotionBrewEffect(MobEffects.CONFUSION, BrewConfig.NauseaCost.get(), 900), ModItems.REFUSE_BOTTLE.get());
        this.register(new PotionBrewEffect(MobEffects.NIGHT_VISION, BrewConfig.NightVisionCost.get(), 3600), Items.GOLDEN_CARROT);
        this.register(new PotionBrewEffect(MobEffects.POISON, BrewConfig.PoisonCost.get(), 900), Items.SPIDER_EYE);
        this.register(new PotionBrewEffect(MobEffects.REGENERATION, BrewConfig.RegenerationCost.get(), 900), Items.GHAST_TEAR);
        this.register(new PotionBrewEffect(MobEffects.DAMAGE_RESISTANCE, BrewConfig.ResistanceCost.get(), 2, 1800), Items.SCUTE);
        this.register(new PotionBrewEffect(MobEffects.SLOW_FALLING, BrewConfig.SlowFallingCost.get(), 1800), Items.PHANTOM_MEMBRANE);
        this.register(new PotionBrewEffect(MobEffects.MOVEMENT_SLOWDOWN, BrewConfig.SlownessCost.get(), 1800), Items.CHAIN);
        this.register(new PotionBrewEffect(MobEffects.MOVEMENT_SPEED, BrewConfig.SpeedCost.get(), 3600), Items.SUGAR);
        this.register(new PotionBrewEffect(MobEffects.DAMAGE_BOOST, BrewConfig.StrengthCost.get(), 3600), Items.BLAZE_POWDER);
        this.register(new PotionBrewEffect(MobEffects.UNLUCK, BrewConfig.UnluckCost.get(), 3600), Items.POISONOUS_POTATO);
        this.register(new PotionBrewEffect(MobEffects.WATER_BREATHING, BrewConfig.WaterBreathingCost.get(), 3600), Items.PUFFERFISH);
        this.register(new PotionBrewEffect(MobEffects.WEAKNESS, BrewConfig.WeaknessCost.get(), 1800), Items.FERMENTED_SPIDER_EYE);
        this.register(new PotionBrewEffect(MobEffects.WITHER, BrewConfig.WitherCost.get(), 1, 900), Items.WITHER_ROSE);

        //Goety
        this.register(new PotionBrewEffect(GoetyEffects.ARROWMANTIC.get(), BrewConfig.ArrowmanticCost.get(), 900), Items.TARGET);
        this.register(new PotionBrewEffect(GoetyEffects.BOTTLING.get(), BrewConfig.BottlingCost.get(), 4, 3600), ModItems.WITCH_HAT.get());
        this.register(new PotionBrewEffect(GoetyEffects.CLIMBING.get(), BrewConfig.ClimbingCost.get(), 3600), ModItems.SPIDER_EGG.get());
        this.register(new PotionBrewEffect(GoetyEffects.CORPSE_EATER.get(), BrewConfig.CorpseEaterCost.get(), 4, 3600), Items.ZOMBIE_HEAD);
        this.register(new PotionBrewEffect(GoetyEffects.CURSED.get(), BrewConfig.CursedCost.get(), 4, 600), ModItems.OCCULT_FABRIC.get());
        this.register(new PotionBrewEffect(GoetyEffects.ENDER_GROUND.get(), BrewConfig.EnderGroundCost.get(), 900), Items.ENDER_PEARL);
        this.register(new PotionBrewEffect(GoetyEffects.EVIL_EYE.get(), BrewConfig.EvilEyeCost.get(), 4, 3600), ModBlocks.FORBIDDEN_GRASS.get().asItem());
        this.register(new PotionBrewEffect(GoetyEffects.EXPLOSIVE.get(), BrewConfig.ExplosiveCost.get(), 2, 900), Items.CREEPER_HEAD);
        this.register(new PotionBrewEffect(GoetyEffects.FIERY_AURA.get(), BrewConfig.FieryAuraCost.get(), 2, 1800), Items.MAGMA_BLOCK);
        this.register(new PotionBrewEffect(GoetyEffects.FIRE_TRAIL.get(), BrewConfig.FireTrailCost.get(), 4, 900), Items.LAVA_BUCKET);
        this.register(new PotionBrewEffect(GoetyEffects.FLAME_HANDS.get(), BrewConfig.FlameHandsCost.get(), 3600), Items.FLINT_AND_STEEL);
        this.register(new PotionBrewEffect(GoetyEffects.FLAMMABLE.get(), BrewConfig.FlammableCost.get(), 1800), Items.GRASS);
        this.register(new PotionBrewEffect(GoetyEffects.FLIMSY.get(), BrewConfig.FlimsyCost.get(), 1800), Items.FEATHER);
        this.register(new PotionBrewEffect(GoetyEffects.FORTUNATE.get(), BrewConfig.FortunateCost.get(), 6, 1800), Items.DIAMOND);
        this.register(new PotionBrewEffect(GoetyEffects.FREEZING.get(), BrewConfig.FreezingCost.get(), 900), Items.POWDER_SNOW_BUCKET);
        this.register(new PotionBrewEffect(GoetyEffects.FROG_LEG.get(), BrewConfig.SwiftSwimCost.get(), 3600), ModItems.FEET_OF_FROG.get());
        this.register(new PotionBrewEffect(GoetyEffects.FROSTY_AURA.get(), BrewConfig.FrostyAuraCost.get(), 2, 1800), Items.BLUE_ICE);
        this.register(new PotionBrewEffect(GoetyEffects.GOLD_TOUCHED.get(), BrewConfig.GoldTouchedCost.get(), 1800), Items.GOLD_NUGGET);
        this.register(new PotionBrewEffect(GoetyEffects.INSIGHT.get(), BrewConfig.InsightCost.get(), 3600), Items.WRITABLE_BOOK);
        this.register(new PotionBrewEffect(GoetyEffects.NYCTOPHOBIA.get(), BrewConfig.NyctophobiaCost.get(), 4, 1800), Items.SCULK_SHRIEKER);
        this.register(new PotionBrewEffect(GoetyEffects.PHOTOSYNTHESIS.get(), BrewConfig.PhotosynthesisCost.get(), 1800), Items.SUNFLOWER);
        this.register(new PotionBrewEffect(GoetyEffects.PLUNGE.get(), BrewConfig.PlungeCost.get(), 4, 600), Items.ANVIL);
        this.register(new PotionBrewEffect(GoetyEffects.PRESSURE.get(), BrewConfig.PressureCost.get(), 1800), Items.SPYGLASS);
        this.register(new PotionBrewEffect(GoetyEffects.REPULSIVE.get(), BrewConfig.RepulsiveCost.get(), 1800), Items.PISTON);
        this.register(new PotionBrewEffect(GoetyEffects.SAPPED.get(), BrewConfig.SappedCost.get(), 1800), ModItems.SAVAGE_TOOTH.get());
        this.register(new PotionBrewEffect(GoetyEffects.SAVE_EFFECTS.get(), BrewConfig.SaveEffectsCost.get(), 8, 6000), Items.ECHO_SHARD);
        this.register(new PotionBrewEffect(GoetyEffects.STORMS_WRATH.get(), BrewConfig.StormsWrathCost.get(), 4, 3600), Items.LIGHTNING_ROD);
        this.register(new PotionBrewEffect(GoetyEffects.SUN_ALLERGY.get(), BrewConfig.SunAllergyCost.get(), 4, 3600), Items.SKELETON_SKULL);
        this.register(new PotionBrewEffect(GoetyEffects.SWIFT_SWIM.get(), BrewConfig.SwiftSwimCost.get(), 3600), ModItems.COOKED_FEET_OF_FROG.get());
        this.register(new PotionBrewEffect(GoetyEffects.TRIPPING.get(), BrewConfig.TrippingCost.get(), 1800), Items.CRACKED_STONE_BRICKS);
        this.register(new PotionBrewEffect(GoetyEffects.VENOMOUS_HANDS.get(), BrewConfig.VenomousHandsCost.get(), 3600), ModItems.VENOMOUS_FANG.get());
        this.register(new PotionBrewEffect(GoetyEffects.WILD_RAGE.get(), BrewConfig.WildRageCost.get(), 2, 900), ModItems.RAGING_MATTER.get());

        //Brew
        this.register(new BatsBrewEffect(100, 2), Items.BEETROOT_SOUP);
        this.register(new BeesBrewEffect(50, 1), Items.BEE_NEST);
        this.register(new BlindJumpBrewEffect(10), Items.CHORUS_FRUIT);
        this.register(new ChopTreeBlockEffect(), Items.STONE_AXE);
        this.register(new CombustBlockEffect(100, 1), Items.FIRE_CHARGE);
        this.register(new CorrosionBlockEffect(), Items.LILY_OF_THE_VALLEY);
        this.register(new DroughtBlockEffect(), Items.SPONGE);
        this.register(new ExplodeBlockEffect(100, 4), Items.TNT);
        this.register(new ExtinguishBlockEffect(), Items.SEAGRASS);
        this.register(new FertilityBrewEffect(), Items.EGG);
        this.register(new FlayingBrewEffect(), Items.LEATHER);
        this.register(new FloodBlockEffect(100, 2), Items.WET_SPONGE);
        this.register(new FreezeBlockEffect(10), Items.PACKED_ICE);
        this.register(new GrowBlockEffect(50), Items.BONE_MEAL);
        this.register(new GrowCactusBlockEffect(10), Items.CACTUS);
        this.register(new GrowCaveVinesBlockEffect(10), Items.GLOW_BERRIES);
        this.register(new HarvestBlockEffect(), Items.WOODEN_HOE);
        this.register(new LaunchBrewEffect(), Items.FIREWORK_ROCKET);
        this.register(new LeafShellBlockEffect(), Items.PEONY);
        this.register(new LoveBrewEffect(10), Items.COCOA_BEANS);
        this.register(new MossifyBlockEffect(), Items.MOSS_BLOCK);
        this.register(new PartLavaBlockEffect(), Items.CAULDRON);
        this.register(new PartWaterBlockEffect(), Items.BUCKET);
        this.register(new PulverizeBlockEffect(), Items.IRON_BLOCK);
        this.register(new PurifyBrewEffect("purify_debuff", 50, 2, MobEffectCategory.BENEFICIAL, 0x385858, true), ModItems.WARTFUL_EGG.get());
        this.register(new PurifyBrewEffect("purify_buff", 50, 2, MobEffectCategory.HARMFUL, 0x374a4a, false), ModItems.WARPED_WARTFUL_EGG.get());
        this.register(new PruningBlockEffect(10), Items.STONE_HOE);
        this.register(new RaiseDeadBrewEffect(50, 2), ModItems.CURSED_METAL_INGOT.get());
        //Buffed version of Vanilla effect
        this.register(new SaturationBrewEffect(BrewConfig.SaturationCost.get()), Items.RABBIT_STEW);
        this.register(new ShearBrewEffect(10), Items.SHEARS);
        this.register(new SnowBlockEffect(), Items.SNOW_BLOCK);
        this.register(new StripBrewEffect(100, 2), Items.MANGROVE_ROOTS);
        this.register(new SweetBerriedEffect(), Items.SWEET_BERRIES);
        this.register(new ThornTrapBrewEffect(50), Items.ROSE_BUSH);
        this.register(new TransposeBrewEffect(), Items.POPPED_CHORUS_FRUIT);
        this.register(new WebbedBrewEffect(50, 1), ModBlocks.SPIDER_NEST.get().asItem());
        for (Item item : ForgeRegistries.ITEMS.getValues()){
            if (item instanceof BlockItem blockItem){
                if (blockItem.getBlock() instanceof InfestedBlock){
                    this.register(new InfestBlockEffect(25), blockItem);
                } else if (blockItem.getBlock() instanceof SaplingBlock saplingBlock){
                    this.register(new GrowTreeBlockEffect(blockItem.getBlock(), saplingBlock.treeGrower), blockItem);
                }
            } else if (item instanceof DyeItem dyeItem){
                this.register(new BrewColorEffect(dyeItem), dyeItem);
            }
        }
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()){
            BrewEffect brewEffect = null;
            Item item = ForgeSpawnEggItem.fromEntityType(entityType);
            if (entityType.getDescriptionId().contains("endermite")){
                brewEffect = new PotionBrewEffect(GoetyEffects.ENDER_FLUX.get(), BrewConfig.EnderFluxCost.get(), 900);
            }
            if (entityType.getDescriptionId().contains("silverfish")){
                brewEffect = new InfestBlockEffect(25);
            }
            if (entityType == EntityType.SNOW_GOLEM){
                brewEffect = new PotionBrewEffect(GoetyEffects.SNOW_SKIN.get(), BrewConfig.SnowSkinCost.get(), 4, 1800);
            }
            if (entityType.is(ModTags.EntityTypes.VILLAGERS)){
                brewEffect = new PotionBrewEffect(MobEffects.REGENERATION, BrewConfig.RegenerationCost.get(), 1800);
            }
            if (brewEffect != null) {
                if (item != null) {
                    this.register(brewEffect, item);
                }
                this.register(brewEffect, entityType);
            }
        }
    }

    private void register(BrewEffect effect, Item ingredient) {
        if(!this.effectIDs.containsKey(effect.getEffectID())) {
            this.effectIDs.put(effect.getEffectID(), effect);
        }
        if (!this.catalyst.containsKey(ingredient)){
            this.catalyst.put(ingredient, effect);
        }
        if (!this.catalystInverted.containsKey(effect.getEffectID())){
            this.catalystInverted.put(effect.getEffectID(), new ItemStack(ingredient));
        }
    }

    private void register(BrewEffect effect, EntityType<?> sacrifice) {
        if(!this.effectIDs.containsKey(effect.getEffectID())) {
            this.effectIDs.put(effect.getEffectID(), effect);
        }
        if (!this.sacrifice.containsKey(sacrifice)){
            this.sacrifice.put(sacrifice, effect);
        }
        if (!this.sacrificeInverted.containsKey(effect.getEffectID())){
            this.sacrificeInverted.put(effect.getEffectID(), sacrifice);
        }
    }

    private void modifierRegister(BrewModifier modifier, Item ingredient){
        if (!this.modifiers.containsKey(ingredient)){
            this.modifiers.put(ingredient, modifier);
        }
    }

    public BrewEffect getEffectFromCatalyst(Item ingredient){
        return this.catalyst.get(ingredient);
    }

    public BrewEffect getEffectFromSacrifice(EntityType<?> sacrifice){
        return this.sacrifice.get(sacrifice);
    }

    public ItemStack getCatalystFromEffect(String string){
        return this.catalystInverted.get(string);
    }

    public EntityType<?> getSacrificeFromEffect(String string){
        return this.sacrificeInverted.get(string);
    }

    public BrewModifier getModifier(Item ingredient){
        return this.modifiers.get(ingredient);
    }

    @Nullable
    public BrewEffect getBrewEffect(String string){
        for (BrewEffect brewEffect : this.effectIDs.values()){
            if (brewEffect.getDescriptionId().equals(string)){
                return brewEffect;
            }
        }
        return this.effectIDs.get(string);
    }

    @Nullable
    public BrewEffect getBrewEffect(CompoundTag compoundTag){
        if (compoundTag.contains("BrewId")){
            return this.effectIDs.get(compoundTag.getString("BrewId"));
        } else {
            return null;
        }
    }
}
