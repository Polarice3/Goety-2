package com.Polarice3.Goety.common.effects.brew;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.block.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class BrewEffects {
    private final Map<String, BrewEffect> effectIDs = Maps.newHashMap();
    private final Map<Item, BrewEffect> catalyst = Maps.newHashMap();
    /** Made for external recipe viewers like Patchouli */
    private final Map<String, ItemStack> catalystInverted = Maps.newHashMap();

    public BrewEffects(){
        this.register(new PotionBrewEffect(MobEffects.ABSORPTION, 50, 1800), Items.GOLDEN_APPLE);
        this.register(new PotionBrewEffect(MobEffects.BLINDNESS, 10, 1800), Items.INK_SAC);
        this.register(new PotionBrewEffect(MobEffects.DARKNESS, 10, 1800), Items.SCULK_VEIN);
        this.register(new PotionBrewEffect(MobEffects.FIRE_RESISTANCE, 25, 3600), Items.MAGMA_CREAM);
        this.register(new PotionBrewEffect(MobEffects.GLOWING, 10, 3600), Items.GLOW_INK_SAC);
        this.register(new PotionBrewEffect(MobEffects.DIG_SPEED, 25, 3600), Items.COOKIE);
        this.register(new PotionBrewEffect(MobEffects.HUNGER, 10, 1800), Items.ROTTEN_FLESH);
        this.register(new PotionBrewEffect(MobEffects.HARM, 25, 1), Items.BRICK);
        this.register(new PotionBrewEffect(MobEffects.HEAL, 25, 1), Items.GLISTERING_MELON_SLICE);
        this.register(new PotionBrewEffect(MobEffects.INVISIBILITY, 25, 3600), Items.GLASS_PANE);
        this.register(new PotionBrewEffect(MobEffects.JUMP, 25, 3600), Items.RABBIT_FOOT);
        this.register(new PotionBrewEffect(MobEffects.LEVITATION, 25, 600), Items.SHULKER_SHELL);
        this.register(new PotionBrewEffect(MobEffects.DIG_SLOWDOWN, 10, 1800), Items.PRISMARINE_SHARD);
        this.register(new PotionBrewEffect(MobEffects.CONFUSION, 10, 900), ModItems.REFUSE_BOTTLE.get());
        this.register(new PotionBrewEffect(MobEffects.NIGHT_VISION, 25, 3600), Items.GOLDEN_CARROT);
        this.register(new PotionBrewEffect(MobEffects.POISON, 10, 900), Items.SPIDER_EYE);
        this.register(new PotionBrewEffect(MobEffects.REGENERATION, 25, 900), Items.GHAST_TEAR);
        this.register(new PotionBrewEffect(MobEffects.DAMAGE_RESISTANCE, 100, 1800), Items.SCUTE);
        this.register(new PotionBrewEffect(MobEffects.SATURATION, 25, 7), Items.RABBIT_STEW);
        this.register(new PotionBrewEffect(MobEffects.SLOW_FALLING, 25, 1800), Items.PHANTOM_MEMBRANE);
        this.register(new PotionBrewEffect(MobEffects.MOVEMENT_SLOWDOWN, 10, 1800), Items.CHAIN);
        this.register(new PotionBrewEffect(MobEffects.MOVEMENT_SPEED, 25, 3600), Items.SUGAR);
        this.register(new PotionBrewEffect(MobEffects.DAMAGE_BOOST, 25, 3600), Items.BLAZE_POWDER);
        this.register(new PotionBrewEffect(MobEffects.WATER_BREATHING, 25, 3600), Items.PUFFERFISH);
        this.register(new PotionBrewEffect(MobEffects.WEAKNESS, 25, 1800), Items.FERMENTED_SPIDER_EYE);
        this.register(new PotionBrewEffect(MobEffects.WITHER, 25, 900), Items.WITHER_ROSE);
        this.register(new PotionBrewEffect(GoetyEffects.CLIMBING.get(), 25, 3600), ModItems.SPIDER_EGG.get());
        this.register(new PotionBrewEffect(GoetyEffects.PRESSURE.get(), 10, 1800), Items.MUSIC_DISC_CAT);
        this.register(new PotionBrewEffect(GoetyEffects.ENDER_GROUND.get(), 10, 900), Items.ENDER_PEARL);
        this.register(new PotionBrewEffect(GoetyEffects.FLAME_HANDS.get(), 25, 3600), Items.FLINT_AND_STEEL);
        this.register(new PotionBrewEffect(GoetyEffects.VENOMOUS_HANDS.get(), 25, 3600), ModItems.VENOMOUS_FANG.get());
        this.register(new PotionBrewEffect(GoetyEffects.FREEZING.get(), 25, 900), Items.POWDER_SNOW_BUCKET);
        this.register(new PotionBrewEffect(GoetyEffects.GOLD_TOUCHED.get(), 10, 1800), Items.GOLD_NUGGET);
        this.register(new PotionBrewEffect(GoetyEffects.NYCTOPHOBIA.get(), 25, 1800), Items.SCULK);
        this.register(new PotionBrewEffect(GoetyEffects.SAPPED.get(), 25, 1800), ModItems.SAVAGE_TOOTH.get());
        this.register(new PotionBrewEffect(GoetyEffects.SUN_ALLERGY.get(), 25, 3600), Items.BONE);
        this.register(new PotionBrewEffect(GoetyEffects.TRIPPING.get(), 25, 1800), Items.CRACKED_STONE_BRICKS);
        this.register(new PotionBrewEffect(GoetyEffects.REPULSIVE.get(), 25, 1800), Items.PISTON);
        this.register(new PotionBrewEffect(GoetyEffects.ARROWMANTIC.get(), 50, 900), Items.TARGET);
        this.register(new PotionBrewEffect(GoetyEffects.EXPLOSIVE.get(), 50, 2, 900), Items.CREEPER_HEAD);
        this.register(new PotionBrewEffect(GoetyEffects.SWIFT_SWIM.get(), 25, 3600), ModItems.FEET_OF_FROG.get());
        this.register(new PotionBrewEffect(GoetyEffects.FIERY_AURA.get(), 50, 2, 1800), Items.LAVA_BUCKET);
        this.register(new MossifyBlockEffect(), Items.MOSS_BLOCK);
        this.register(new ChopTreeBlockEffect(), Items.STONE_AXE);
        this.register(new ExtinguishBlockEffect(), Items.SEAGRASS);
        this.register(new SweetBerriedEffect(), Items.SWEET_BERRIES);
        this.register(new SnowBlockEffect(), Items.SNOW_BLOCK);
        this.register(new TransposeBrewEffect(), Items.POPPED_CHORUS_FRUIT);
        this.register(new FertilityBrewEffect(), Items.EGG);
        this.register(new DroughtBlockEffect(), Items.SPONGE);
        this.register(new LaunchBrewEffect(), Items.FIREWORK_ROCKET);
        this.register(new FlayingBrewEffect(), Items.LEATHER);
        this.register(new PruningBlockEffect(10), Items.STONE_HOE);
        this.register(new ShearBrewEffect(10), Items.SHEARS);
        this.register(new LoveBrewEffect(10), Items.COCOA_BEANS);
        this.register(new BlindJumpBrewEffect(10), Items.CHORUS_FRUIT);
        this.register(new GrowCactusBlockEffect(10), Items.CACTUS);
        this.register(new GrowCaveVinesBlockEffect(10), Items.GLOW_BERRIES);
        this.register(new FreezeBlockEffect(1), Items.PACKED_ICE);
        this.register(new WebbedBrewEffect(50, 1), ModBlocks.SPIDER_NEST.get().asItem());
        this.register(new GrowBlockEffect(50), Items.BONE_MEAL);
        this.register(new BeesBrewEffect(50, 1), Items.BEE_NEST);
        this.register(new CombustBlockEffect(100, 1), Items.FIRE_CHARGE);
        this.register(new FloodBlockEffect(100, 2), Items.WET_SPONGE);
        this.register(new StripBrewEffect(100, 2), Items.MANGROVE_ROOTS);
        this.register(new ExplodeBlockEffect(100, 4), Items.TNT);
        this.register(new PurifyBrewEffect("purify_debuff", 50, 2, MobEffectCategory.BENEFICIAL, 0x385858, true), ModItems.WARTFUL_EGG.get());
        this.register(new PurifyBrewEffect("purify_buff", 50, 2, MobEffectCategory.HARMFUL, 0x374a4a, false), ModItems.WARPED_WARTFUL_EGG.get());
        for (Item item : ForgeRegistries.ITEMS.getValues()){
            if (item instanceof BlockItem blockItem){
                if (blockItem.getBlock() instanceof InfestedBlock){
                    this.register(new InfestBlockEffect(1), blockItem);
                }
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
