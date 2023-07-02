package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.BrewConfig;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.block.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class BrewEffects {
    private final Map<String, BrewEffect> effectIDs = Maps.newHashMap();
    private final Map<Item, BrewEffect> catalyst = Maps.newHashMap();
    /** Made for external recipe viewers like Patchouli */
    private final Map<String, ItemStack> catalystInverted = Maps.newHashMap();

    public BrewEffects(){
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
        this.register(new PotionBrewEffect(MobEffects.INVISIBILITY, BrewConfig.InvisibilityCost.get(), 3600), Items.GLASS_PANE);
        this.register(new PotionBrewEffect(MobEffects.JUMP, BrewConfig.JumpBoostCost.get(), 3600), Items.RABBIT_FOOT);
        this.register(new PotionBrewEffect(MobEffects.LEVITATION, BrewConfig.LevitationCost.get(), 2, 600), Items.SHULKER_SHELL);
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
        this.register(new PotionBrewEffect(MobEffects.WATER_BREATHING, BrewConfig.WaterBreathingCost.get(), 3600), Items.PUFFERFISH);
        this.register(new PotionBrewEffect(MobEffects.WEAKNESS, BrewConfig.WeaknessCost.get(), 1800), Items.FERMENTED_SPIDER_EYE);
        this.register(new PotionBrewEffect(MobEffects.WITHER, BrewConfig.WitherCost.get(), 1, 900), Items.WITHER_ROSE);

        //Goety
        this.register(new PotionBrewEffect(GoetyEffects.CLIMBING.get(), 25, 3600), ModItems.SPIDER_EGG.get());
        this.register(new PotionBrewEffect(GoetyEffects.PRESSURE.get(), 10, 1800), Items.MUSIC_DISC_CAT);
        this.register(new PotionBrewEffect(GoetyEffects.ENDER_GROUND.get(), 10, 900), Items.ENDER_PEARL);
        this.register(new PotionBrewEffect(GoetyEffects.FLAME_HANDS.get(), 25, 3600), Items.FLINT_AND_STEEL);
        this.register(new PotionBrewEffect(GoetyEffects.VENOMOUS_HANDS.get(), 25, 3600), ModItems.VENOMOUS_FANG.get());
        this.register(new PotionBrewEffect(GoetyEffects.FREEZING.get(), 25, 900), Items.POWDER_SNOW_BUCKET);
        this.register(new PotionBrewEffect(GoetyEffects.GOLD_TOUCHED.get(), 10, 1800), Items.GOLD_NUGGET);
        this.register(new PotionBrewEffect(GoetyEffects.NYCTOPHOBIA.get(), 50, 4, 1800), Items.SCULK_SHRIEKER);
        this.register(new PotionBrewEffect(GoetyEffects.SAPPED.get(), 25, 1800), ModItems.SAVAGE_TOOTH.get());
        this.register(new PotionBrewEffect(GoetyEffects.SUN_ALLERGY.get(), 50, 4, 3600), Items.SKELETON_SKULL);
        this.register(new PotionBrewEffect(GoetyEffects.TRIPPING.get(), 25, 1800), Items.CRACKED_STONE_BRICKS);
        this.register(new PotionBrewEffect(GoetyEffects.REPULSIVE.get(), 25, 1800), Items.PISTON);
        this.register(new PotionBrewEffect(GoetyEffects.PHOTOSYNTHESIS.get(), 25, 1800), Items.SUNFLOWER);
        this.register(new PotionBrewEffect(GoetyEffects.ARROWMANTIC.get(), 50, 900), Items.TARGET);
        this.register(new PotionBrewEffect(GoetyEffects.FLAMMABLE.get(), 50, 1800), Items.GRASS);
        this.register(new PotionBrewEffect(GoetyEffects.EXPLOSIVE.get(), 50, 2, 900), Items.CREEPER_HEAD);
        this.register(new PotionBrewEffect(GoetyEffects.SWIFT_SWIM.get(), 25, 3600), ModItems.FEET_OF_FROG.get());
        this.register(new PotionBrewEffect(GoetyEffects.FIERY_AURA.get(), 50, 2, 1800), Items.MAGMA_BLOCK);
        this.register(new PotionBrewEffect(GoetyEffects.FIRE_TRAIL.get(), 50, 4, 900), Items.LAVA_BUCKET);
        this.register(new PotionBrewEffect(GoetyEffects.PLUNGE.get(), 50, 4, 600), Items.ANVIL);

        //Brew
        this.register(new MossifyBlockEffect(), Items.MOSS_BLOCK);
        this.register(new ChopTreeBlockEffect(), Items.STONE_AXE);
        this.register(new HarvestBlockEffect(), Items.WOODEN_HOE);
        this.register(new ExtinguishBlockEffect(), Items.SEAGRASS);
        this.register(new SweetBerriedEffect(), Items.SWEET_BERRIES);
        this.register(new SnowBlockEffect(), Items.SNOW_BLOCK);
        this.register(new TransposeBrewEffect(), Items.POPPED_CHORUS_FRUIT);
        this.register(new FertilityBrewEffect(), Items.EGG);
        this.register(new DroughtBlockEffect(), Items.SPONGE);
        this.register(new LaunchBrewEffect(), Items.FIREWORK_ROCKET);
        this.register(new FlayingBrewEffect(), Items.LEATHER);
        this.register(new LeafShellBlockEffect(), Items.PEONY);
        //Buffed version of Vanilla effect
        this.register(new SaturationBrewEffect(BrewConfig.SaturationCost.get()), Items.RABBIT_STEW);
        this.register(new PruningBlockEffect(10), Items.STONE_HOE);
        this.register(new ShearBrewEffect(10), Items.SHEARS);
        this.register(new LoveBrewEffect(10), Items.COCOA_BEANS);
        this.register(new BlindJumpBrewEffect(10), Items.CHORUS_FRUIT);
        this.register(new GrowCactusBlockEffect(10), Items.CACTUS);
        this.register(new GrowCaveVinesBlockEffect(10), Items.GLOW_BERRIES);
        this.register(new FreezeBlockEffect(1), Items.PACKED_ICE);
        this.register(new ThornTrapBrewEffect(50), Items.ROSE_BUSH);
        this.register(new GrowBlockEffect(50), Items.BONE_MEAL);
        this.register(new WebbedBrewEffect(50, 1), ModBlocks.SPIDER_NEST.get().asItem());
        this.register(new BeesBrewEffect(50, 1), Items.BEE_NEST);
        this.register(new CombustBlockEffect(100, 1), Items.FIRE_CHARGE);
        this.register(new BatsBrewEffect(100, 2), Items.BEETROOT_SOUP);
        this.register(new FloodBlockEffect(100, 2), Items.WET_SPONGE);
        this.register(new StripBrewEffect(100, 2), Items.MANGROVE_ROOTS);
        this.register(new ExplodeBlockEffect(100, 4), Items.TNT);
        this.register(new PurifyBrewEffect("purify_debuff", 50, 2, MobEffectCategory.BENEFICIAL, 0x385858, true), ModItems.WARTFUL_EGG.get());
        this.register(new PurifyBrewEffect("purify_buff", 50, 2, MobEffectCategory.HARMFUL, 0x374a4a, false), ModItems.WARPED_WARTFUL_EGG.get());
        for (Item item : ForgeRegistries.ITEMS.getValues()){
            if (item instanceof BlockItem blockItem){
                if (blockItem.getBlock() instanceof InfestedBlock){
                    this.register(new InfestBlockEffect(1), blockItem);
                } else if (blockItem.getBlock() instanceof SaplingBlock saplingBlock){
                    this.register(new GrowTreeBlockEffect(blockItem.getBlock(), saplingBlock.treeGrower), blockItem);
                }
            } else if (item instanceof DyeItem dyeItem){
                this.register(new BrewColorEffect(dyeItem), dyeItem);
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

    public BrewEffect getEffectFromCatalyst(Item ingredient){
        return this.catalyst.get(ingredient);
    }

    public ItemStack getCatalystFromEffect(String string){
        return this.catalystInverted.get(string);
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
