package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.items.armor.BlackIronArmor;
import com.Polarice3.Goety.common.items.armor.CursedKnightArmor;
import com.Polarice3.Goety.common.items.armor.CursedPaladinArmor;
import com.Polarice3.Goety.common.items.armor.DarkArmor;
import com.Polarice3.Goety.common.items.block.HauntedArmorStandItem;
import com.Polarice3.Goety.common.items.block.HauntedPaintingItem;
import com.Polarice3.Goety.common.items.brew.BrewBag;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.brew.LingeringBrewItem;
import com.Polarice3.Goety.common.items.brew.SplashBrewItem;
import com.Polarice3.Goety.common.items.curios.*;
import com.Polarice3.Goety.common.items.equipment.*;
import com.Polarice3.Goety.common.items.magic.*;
import com.Polarice3.Goety.common.items.research.ExtraScroll;
import com.Polarice3.Goety.common.items.research.ForbiddenScroll;
import com.Polarice3.Goety.common.items.research.ResearchScroll;
import com.Polarice3.Goety.common.items.research.Scroll;
import com.Polarice3.Goety.common.magic.spells.*;
import com.Polarice3.Goety.common.magic.spells.frost.*;
import com.Polarice3.Goety.common.magic.spells.geomancy.*;
import com.Polarice3.Goety.common.magic.spells.necromancy.*;
import com.Polarice3.Goety.common.magic.spells.nether.*;
import com.Polarice3.Goety.common.magic.spells.storm.*;
import com.Polarice3.Goety.common.magic.spells.utility.GlowLightSpell;
import com.Polarice3.Goety.common.magic.spells.utility.SoulLightSpell;
import com.Polarice3.Goety.common.magic.spells.void_spells.*;
import com.Polarice3.Goety.common.magic.spells.wild.*;
import com.Polarice3.Goety.common.magic.spells.wind.*;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Goety.MOD_ID);

    public static void init(){
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<FullSpentTotem> TOTEM_OF_ROOTS = ITEMS.register("totem_of_roots", () -> new FullSpentTotem(Math.max(ITotem.MAX_SOULS / 100, 5)));
    public static final RegistryObject<TotemOfSouls> TOTEM_OF_SOULS = ITEMS.register("totem_of_souls", () -> new TotemOfSouls(ITotem.MAX_SOULS));

    //Basic
    public static final RegistryObject<Item> SPENT_TOTEM = ITEMS.register("spent_totem", ItemBase::new);
    public static final RegistryObject<Item> CURSED_METAL_INGOT = ITEMS.register("cursed_ingot", ItemBase::new);
    public static final RegistryObject<Item> DARK_METAL_INGOT = ITEMS.register("dark_ingot", ItemBase::new);
    public static final RegistryObject<Item> ECTOPLASM = ITEMS.register("ectoplasm", ItemBase::new);
    public static final RegistryObject<Item> DARK_FABRIC = ITEMS.register("dark_fabric", ItemBase::new);
    public static final RegistryObject<Item> MAGIC_FABRIC = ITEMS.register("magic_fabric", ItemBase::new);
    public static final RegistryObject<Item> OCCULT_FABRIC = ITEMS.register("occult_fabric", ItemBase::new);
    public static final RegistryObject<Item> SPIRIT_FABRIC = ITEMS.register("spirit_fabric", ItemBase::new);
    public static final RegistryObject<Item> GALE_FABRIC = ITEMS.register("gale_fabric", ItemBase::new);
    public static final RegistryObject<Item> CHILL_FABRIC = ITEMS.register("chill_fabric", ItemBase::new);
    public static final RegistryObject<Item> SAVAGE_TOOTH = ITEMS.register("savage_tooth", ItemBase::new);
    public static final RegistryObject<Item> JADE = ITEMS.register("jade", ItemBase::new);
    public static final RegistryObject<Item> SPIDER_EGG = ITEMS.register("spider_egg", ItemBase::new);
    public static final RegistryObject<Item> WARPED_WARTFUL_EGG = ITEMS.register("warped_wartful_egg", ItemBase::new);
    public static final RegistryObject<Item> VENOMOUS_FANG = ITEMS.register("venomous_fang", ItemBase::new);
    public static final RegistryObject<Item> RAGING_MATTER = ITEMS.register("raging_matter", ItemBase::new);
    public static final RegistryObject<Item> ICE_CUBE = ITEMS.register("ice_cube", ItemBase::new);
    public static final RegistryObject<Item> SOUL_RUBY = ITEMS.register("soul_ruby", ItemBase::new);
    public static final RegistryObject<Item> EMPTY_FOCUS = ITEMS.register("empty_focus", ItemBase::new);
    public static final RegistryObject<Item> ANIMATION_CORE = ITEMS.register("animation_core", AnimationCore::new);
    public static final RegistryObject<Item> HUNGER_CORE = ITEMS.register("hunger_core", ItemBase::new);
    public static final RegistryObject<Item> WIND_CORE = ITEMS.register("wind_core", ItemBase::new);
    public static final RegistryObject<Item> MYSTIC_CORE = ITEMS.register("mystic_core", ItemBase::new);
    public static final RegistryObject<Item> OMINOUS_ORB = ITEMS.register("ominous_orb", () -> new RepeatCraftItem(new Item.Properties()));
    public static final RegistryObject<Item> HEART_OF_THE_NIGHT = ITEMS.register("heart_of_the_night", ItemBase::new);
    public static final RegistryObject<Item> CAULDRON_LADLE = ITEMS.register("cauldron_ladle", ItemBase::new);
    public static final RegistryObject<Item> OMINOUS_SADDLE = ITEMS.register("ominous_saddle", ItemBase::new);
    public static final RegistryObject<Item> IRON_RAVAGER_ARMOR = ITEMS.register("iron_ravager_armor", () -> new RavagerArmorItem(7, "iron"));
    public static final RegistryObject<Item> GOLD_RAVAGER_ARMOR = ITEMS.register("gold_ravager_armor", () -> new RavagerArmorItem(11, "gold"));
    public static final RegistryObject<Item> DIAMOND_RAVAGER_ARMOR = ITEMS.register("diamond_ravager_armor", () -> new RavagerArmorItem(15, "diamond"));
    public static final RegistryObject<Item> NETHERITE_RAVAGER_ARMOR = ITEMS.register("netherite_ravager_armor", () -> new RavagerArmorItem(20, "netherite", new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<Item> WITHERED_MANUSCRIPT = ITEMS.register("withered_manuscript", () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> FORBIDDEN_PIECE = ITEMS.register("forbidden_piece", ItemBase::new);
    public static final RegistryObject<Item> FORBIDDEN_FRAGMENT = ITEMS.register("forbidden_fragment", ItemBase::new);

    public static final RegistryObject<Item> FEET_OF_FROG = ITEMS.register("feet_of_frog", () -> new Item(new Item.Properties().food(Foods.COD)));
    public static final RegistryObject<Item> COOKED_FEET_OF_FROG = ITEMS.register("cooked_feet_of_frog", () -> new Item(new Item.Properties().food(Foods.COOKED_COD)));

    public static final RegistryObject<Item> PHILOSOPHERS_STONE = ITEMS.register("philosophers_stone", PhilosophersStone::new);
    public static final RegistryObject<Item> DARK_SCROLL = ITEMS.register("dark_scroll", DarkScrollItem::new);
    public static final RegistryObject<Item> BLAZING_HORN = ITEMS.register("blazing_horn", BlazingHornItem::new);
    public static final RegistryObject<Item> MAGIC_EMERALD = ITEMS.register("magic_emerald", () -> new SimpleFoiledItem(new Item.Properties()));
    public static final RegistryObject<Item> SOUL_EMERALD = ITEMS.register("soul_emerald", () -> new SimpleFoiledItem(new Item.Properties()));
    public static final RegistryObject<Item> UNHOLY_BLOOD = ITEMS.register("unholy_blood", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SOUL_TRANSFER = ITEMS.register("soul_transfer", SoulTransferItem::new);
    public static final RegistryObject<Item> FLAME_CAPTURE = ITEMS.register("flame_capture", FlameCaptureItem::new);
    public static final RegistryObject<Item> SNAP_FUNGUS = ITEMS.register("snap_fungus", SnapFungusItem::new);
    public static final RegistryObject<Item> BLAST_FUNGUS = ITEMS.register("blast_fungus", BlastFungusItem::new);
    public static final RegistryObject<Item> BERSERK_FUNGUS = ITEMS.register("berserk_fungus", BerserkFungusItem::new);
    public static final RegistryObject<Item> WARTFUL_EGG = ITEMS.register("wartful_egg", WartlingEggItem::new);
    public static final RegistryObject<Item> REFUSE_BOTTLE = ITEMS.register("refuse_bottle", RefuseBottleItem::new);
    public static final RegistryObject<Item> ILL_BOMB = ITEMS.register("ill_bomb", IllBombItem::new);
    public static final RegistryObject<Item> SOUL_JAR = ITEMS.register("soul_jar", SoulJar::new);
    public static final RegistryObject<Item> TAGLOCK_KIT = ITEMS.register("taglock_kit", TaglockKit::new);
    public static final RegistryObject<Item> GRIMOIRE_OF_GRUDGES = ITEMS.register("grimoire_of_grudges", GrudgeGrimoire::new);
    public static final RegistryObject<Item> GRIMOIRE_OF_GOODWILL = ITEMS.register("grimoire_of_goodwill", GoodwillGrimoire::new);

    public static final RegistryObject<Item> RAVAGING_SCROLL = ITEMS.register("ravaging_scroll", () -> new Scroll(ResearchList.RAVAGING));
    public static final RegistryObject<Item> WARRED_SCROLL = ITEMS.register("warred_scroll", () -> new Scroll(ResearchList.WARRED));
    public static final RegistryObject<Item> BURIED_SCROLL = ITEMS.register("buried_scroll", () -> new Scroll(ResearchList.BURIED));
    public static final RegistryObject<Item> HAUNTING_SCROLL = ITEMS.register("haunting_scroll", () -> new Scroll(ResearchList.HAUNTING));
    public static final RegistryObject<Item> FRONT_SCROLL = ITEMS.register("front_scroll", () -> new Scroll(ResearchList.FRONT));
    public static final RegistryObject<Item> MISTRAL_SCROLL = ITEMS.register("mistral_scroll", () -> new Scroll(ResearchList.MISTRAL));
    public static final RegistryObject<Item> FLORAL_SCROLL = ITEMS.register("floral_scroll", () -> new Scroll(ResearchList.FLORAL));
    public static final RegistryObject<Item> BYGONE_SCROLL = ITEMS.register("bygone_scroll", () -> new Scroll(ResearchScroll.fireResistant(), ResearchList.BYGONE));
    public static final RegistryObject<Item> TERMINUS_SCROLL = ITEMS.register("terminus_scroll", () -> new ExtraScroll(ResearchScroll.fireResistant(), ResearchList.TERMINUS, ResearchList.WARRED));
    public static final RegistryObject<Item> FORBIDDEN_SCROLL = ITEMS.register("forbidden_scroll", ForbiddenScroll::new);

    public static final RegistryObject<Item> UNDEATH_POTION = ITEMS.register("undeath_potion", UndeathPotionItem::new);

    public static final RegistryObject<Item> BREW = ITEMS.register("brew", BrewItem::new);
    public static final RegistryObject<Item> SPLASH_BREW = ITEMS.register("splash_brew", SplashBrewItem::new);
    public static final RegistryObject<Item> LINGERING_BREW = ITEMS.register("lingering_brew", LingeringBrewItem::new);
    public static final RegistryObject<Item> GAS_BREW = ITEMS.register("gas_brew", SplashBrewItem::new);

    public static final RegistryObject<Item> TREASURE_POUCH = ITEMS.register("treasure_pouch", TreasurePouchItem::new);

    public static final RegistryObject<Item> HAUNTED_BOAT = ITEMS.register("haunted_boat", () -> new ModBoatItem(false, ModBoat.Type.HAUNTED, (new Item.Properties()).stacksTo(1)));
    public static final RegistryObject<Item> HAUNTED_CHEST_BOAT = ITEMS.register("haunted_chest_boat", () -> new ModBoatItem(true, ModBoat.Type.HAUNTED, (new Item.Properties()).stacksTo(1)));

    public static final RegistryObject<Item> ROTTEN_BOAT = ITEMS.register("rotten_boat", () -> new ModBoatItem(false, ModBoat.Type.ROTTEN, (new Item.Properties()).stacksTo(1)));
    public static final RegistryObject<Item> ROTTEN_CHEST_BOAT = ITEMS.register("rotten_chest_boat", () -> new ModBoatItem(true, ModBoat.Type.ROTTEN, (new Item.Properties()).stacksTo(1)));

    public static final RegistryObject<Item> WINDSWEPT_BOAT = ITEMS.register("windswept_boat", () -> new ModBoatItem(false, ModBoat.Type.WINDSWEPT, (new Item.Properties()).stacksTo(1)));
    public static final RegistryObject<Item> WINDSWEPT_CHEST_BOAT = ITEMS.register("windswept_chest_boat", () -> new ModBoatItem(true, ModBoat.Type.WINDSWEPT, (new Item.Properties()).stacksTo(1)));

    public static final RegistryObject<Item> HAUNTED_ARMOR_STAND = ITEMS.register("haunted_armor_stand", HauntedArmorStandItem::new);
    public static final RegistryObject<Item> HAUNTED_PAINTING = ITEMS.register("haunted_painting", HauntedPaintingItem::new);

    //Curios
    public static final RegistryObject<Item> FOCUS_BAG = ITEMS.register("focus_bag", FocusBag::new);
    public static final RegistryObject<Item> FOCUS_PACK = ITEMS.register("focus_pack", FocusPack::new);
    public static final RegistryObject<Item> BREW_BAG = ITEMS.register("brew_bag", BrewBag::new);
    public static final RegistryObject<Item> RING_OF_WANT = ITEMS.register("ring_of_want", RingItem::new);
    public static final RegistryObject<Item> RING_OF_FORCE = ITEMS.register("ring_of_force", RingItem::new);
    public static final RegistryObject<Item> RING_OF_THE_DRAGON = ITEMS.register("ring_of_the_dragon", RingItem::new);
    public static final RegistryObject<Item> PENDANT_OF_HUNGER = ITEMS.register("pendant_of_hunger", PendantOfHungerItem::new);
    public static final RegistryObject<Item> TARGETING_MONOCLE = ITEMS.register("targeting_monocle", SingleStackItem::new);
    public static final RegistryObject<Item> DARK_HAT = ITEMS.register("dark_hat", MagicHatItem::new);
    public static final RegistryObject<Item> GRAND_TURBAN = ITEMS.register("grand_turban", MagicHatItem::new);
    public static final RegistryObject<Item> NECRO_CROWN = ITEMS.register("necro_crown", NecroGarbs.NecroCrownItem::new);
    public static final RegistryObject<Item> NAMELESS_CROWN = ITEMS.register("nameless_crown", MagicHatItem::new);
    public static final RegistryObject<Item> AMETHYST_NECKLACE = ITEMS.register("amethyst_necklace", SingleStackItem::new);
    public static final RegistryObject<Item> WITCH_HAT = ITEMS.register("witch_hat", WitchHatItem::new);
    public static final RegistryObject<Item> WITCH_HAT_HEDGE = ITEMS.register("witch_hat_hedge", WitchHatItem::new);
    public static final RegistryObject<Item> CRONE_HAT = ITEMS.register("crone_hat", WitchHatItem::new);
    public static final RegistryObject<Item> DARK_ROBE = ITEMS.register("dark_robe", MagicRobeItem::new);
    public static final RegistryObject<Item> GRAND_ROBE = ITEMS.register("grand_robe", MagicRobeItem::new);
    public static final RegistryObject<Item> NECRO_CAPE = ITEMS.register("necro_cape", () -> new NecroGarbs.NecroCapeItem(false));
    public static final RegistryObject<Item> NAMELESS_CAPE = ITEMS.register("nameless_cape", () -> new NecroGarbs.NecroCapeItem(true));
    public static final RegistryObject<Item> ILLUSION_ROBE = ITEMS.register("illusion_robe", IllusionRobeItem::new);
    public static final RegistryObject<Item> ILLUSION_ROBE_MIRROR = ITEMS.register("illusion_robe_mirror", IllusionRobeItem::new);
    public static final RegistryObject<Item> FROST_ROBE = ITEMS.register("frost_robe", FrostRobeItem::new);
    public static final RegistryObject<Item> FROST_ROBE_CRYO = ITEMS.register("frost_robe_cryo", FrostRobeItem::new);
    public static final RegistryObject<Item> WIND_ROBE = ITEMS.register("wind_robe", WindyRobeItem::new);
    public static final RegistryObject<Item> STORM_ROBE = ITEMS.register("storm_robe", WindyRobeItem::new);
    public static final RegistryObject<Item> WILD_ROBE = ITEMS.register("wild_robe", WildRobeItem::new);
    public static final RegistryObject<Item> WITCH_ROBE = ITEMS.register("witch_robe", WitchRobeItem::new);
    public static final RegistryObject<Item> WITCH_ROBE_HEDGE = ITEMS.register("witch_robe_hedge", WitchRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_ROBE = ITEMS.register("warlock_robe", WarlockRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_ROBE_DARK = ITEMS.register("warlock_robe_dark", WarlockRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_SASH = ITEMS.register("warlock_sash", WarlockGarmentItem::new);
    public static final RegistryObject<Item> SEA_AMULET = ITEMS.register("sea_amulet", SeaAmuletItem::new);
    public static final RegistryObject<Item> FELINE_AMULET = ITEMS.register("feline_amulet", SingleStackItem::new);
    public static final RegistryObject<Item> ALARMING_CHARM = ITEMS.register("alarming_charm", SingleStackItem::new);
    public static final RegistryObject<Item> WAYFARERS_BELT = ITEMS.register("wayfarers_belt", WayfarersBeltItem::new);
    public static final RegistryObject<Item> SPITEFUL_BELT = ITEMS.register("spiteful_belt", SingleStackItem::new);
    public static final RegistryObject<Item> STAR_AMULET = ITEMS.register("star_amulet", SingleFoiledStackItem::new);
    public static final RegistryObject<Item> GRAVE_GLOVE = ITEMS.register("grave_glove", GloveItem::new);

    //Focus
    ///Magic
    public static final RegistryObject<Item> VEXING_FOCUS = ITEMS.register("vexing_focus", () -> new MagicFocus(new VexSpell()));
    public static final RegistryObject<Item> BITING_FOCUS = ITEMS.register("biting_focus", () -> new MagicFocus(new FangSpell()));
    public static final RegistryObject<Item> FEAST_FOCUS = ITEMS.register("feast_focus", () -> new MagicFocus(new FeastSpell()));
    public static final RegistryObject<Item> TEETH_FOCUS = ITEMS.register("teeth_focus", () -> new MagicFocus(new TeethSpell()));
    public static final RegistryObject<Item> ILLUSION_FOCUS = ITEMS.register("illusion_focus", () -> new MagicFocus(new IllusionSpell()));
    public static final RegistryObject<Item> FIRE_BREATH_FOCUS = ITEMS.register("fire_breath_focus", () -> new MagicFocus(new FireBreathSpell()));
    public static final RegistryObject<Item> SOUL_BOLT_FOCUS = ITEMS.register("soul_bolt_focus", () -> new MagicFocus(new SoulBoltSpell()));
    public static final RegistryObject<Item> MAGIC_BOLT_FOCUS = ITEMS.register("magic_bolt_focus", () -> new MagicFocus(new MagicBoltSpell()));
    public static final RegistryObject<Item> SWORD_FOCUS = ITEMS.register("sword_focus", () -> new MagicFocus(new SwordSpell()));
    public static final RegistryObject<Item> SOUL_LIGHT_FOCUS = ITEMS.register("soul_light_focus", () -> new MagicFocus(new SoulLightSpell()));
    public static final RegistryObject<Item> GLOW_LIGHT_FOCUS = ITEMS.register("glow_light_focus", () -> new MagicFocus(new GlowLightSpell()));
    public static final RegistryObject<Item> IRON_HIDE_FOCUS = ITEMS.register("iron_hide_focus", () -> new MagicFocus(new IronHideSpell()));
    public static final RegistryObject<Item> BULWARK_FOCUS = ITEMS.register("bulwark_focus", () -> new MagicFocus(new BulwarkSpell()));
    public static final RegistryObject<Item> SOUL_HEAL_FOCUS = ITEMS.register("soul_heal_focus", () -> new MagicFocus(new SoulHealSpell()));
    public static final RegistryObject<Item> SHOCKWAVE_FOCUS = ITEMS.register("shockwave_focus", () -> new MagicFocus(new ShockwaveSpell()));
    public static final RegistryObject<Item> TELEKINESIS_FOCUS = ITEMS.register("telekinesis_focus", () -> new MagicFocus(new TelekinesisSpell()));
    public static final RegistryObject<Item> COMMAND_FOCUS = ITEMS.register("command_focus", CommandFocus::new);
    public static final RegistryObject<Item> SONIC_BOOM_FOCUS = ITEMS.register("sonic_boom_focus", () -> new MagicFocus(new SonicBoomSpell()));
    public static final RegistryObject<Item> CORRUPTION_FOCUS = ITEMS.register("corruption_focus", () -> new MagicFocus(new CorruptedBeamSpell()));

    ///Necromancy
    public static final RegistryObject<Item> ROTTING_FOCUS = ITEMS.register("rotting_focus", () -> new MagicFocus(new ZombieSpell()));
    public static final RegistryObject<Item> OSSEOUS_FOCUS = ITEMS.register("osseous_focus", () -> new MagicFocus(new SkeletonSpell()));
    public static final RegistryObject<Item> SPOOKY_FOCUS = ITEMS.register("spooky_focus", () -> new MagicFocus(new WraithSpell()));
    public static final RegistryObject<Item> VANGUARD_FOCUS = ITEMS.register("vanguard_focus", () -> new MagicFocus(new VanguardSpell()));
    public static final RegistryObject<Item> SKULL_FOCUS = ITEMS.register("skull_focus", () -> new MagicFocus(new HauntedSkullSpell()));

    ///Geomancy
    public static final RegistryObject<Item> BARRICADE_FOCUS = ITEMS.register("barricade_focus", () -> new MagicFocus(new BarricadeSpell()));
    public static final RegistryObject<Item> QUAKING_FOCUS = ITEMS.register("quaking_focus", () -> new MagicFocus(new QuakingSpell()));
    public static final RegistryObject<Item> PULVERIZE_FOCUS = ITEMS.register("pulverize_focus", () -> new MagicFocus(new PulverizeSpell()));
    public static final RegistryObject<Item> ROTATION_FOCUS = ITEMS.register("rotation_focus", () -> new MagicFocus(new RotationSpell()));
    public static final RegistryObject<Item> SCATTER_FOCUS = ITEMS.register("scatter_focus", () -> new MagicFocus(new ScatterSpell()));
    public static final RegistryObject<Item> ERUPTION_FOCUS = ITEMS.register("eruption_focus", () -> new MagicFocus(new EruptionSpell()));

    ///Frost
    public static final RegistryObject<Item> FROST_BREATH_FOCUS = ITEMS.register("frost_breath_focus", () -> new MagicFocus(new FrostBreathSpell()));
    public static final RegistryObject<Item> ICE_SPIKE_FOCUS = ITEMS.register("ice_spike_focus", () -> new MagicFocus(new IceSpikeSpell()));
    public static final RegistryObject<Item> ICE_STORM_FOCUS = ITEMS.register("ice_storm_focus", () -> new MagicFocus(new IceStormSpell()));
    public static final RegistryObject<Item> HAIL_FOCUS = ITEMS.register("hail_focus", () -> new MagicFocus(new HailSpell()));
    public static final RegistryObject<Item> ICEOLOGY_FOCUS = ITEMS.register("iceology_focus", () -> new MagicFocus(new IceChunkSpell()));
    public static final RegistryObject<Item> FROST_NOVA_FOCUS = ITEMS.register("frost_nova_focus", () -> new MagicFocus(new FrostNovaSpell()));
    public static final RegistryObject<Item> FROSTBORN_FOCUS = ITEMS.register("frostborn_focus", () -> new MagicFocus(new IceGolemSpell()));

    ///Wild
    public static final RegistryObject<Item> SWARM_FOCUS = ITEMS.register("swarm_focus", () -> new MagicFocus(new SwarmSpell()));
    public static final RegistryObject<Item> GRAPPLE_FOCUS = ITEMS.register("grapple_focus", () -> new MagicFocus(new GrappleSpell()));
    public static final RegistryObject<Item> OVERGROWTH_FOCUS = ITEMS.register("overgrowth_focus", () -> new MagicFocus(new OvergrowthSpell()));
    public static final RegistryObject<Item> ENTANGLING_FOCUS = ITEMS.register("entangling_focus", () -> new MagicFocus(new EntanglingSpell()));
    public static final RegistryObject<Item> WHISPERING_FOCUS = ITEMS.register("whispering_focus", () -> new MagicFocus(new WhisperSpell()));

    ///Wind
    public static final RegistryObject<Item> LAUNCH_FOCUS = ITEMS.register("launch_focus", () -> new MagicFocus(new LaunchSpell()));
    public static final RegistryObject<Item> FLYING_FOCUS = ITEMS.register("flying_focus", () -> new MagicFocus(new FlyingSpell()));
    public static final RegistryObject<Item> CUSHION_FOCUS = ITEMS.register("cushion_focus", () -> new MagicFocus(new CushionSpell()));
    public static final RegistryObject<Item> WHIRLWIND_FOCUS = ITEMS.register("whirlwind_focus", () -> new MagicFocus(new WhirlwindSpell()));
    public static final RegistryObject<Item> CYCLONE_FOCUS = ITEMS.register("cyclone_focus", () -> new MagicFocus(new CycloneSpell()));
    public static final RegistryObject<Item> UPDRAFT_FOCUS = ITEMS.register("updraft_focus", () -> new MagicFocus(new UpdraftSpell()));
    public static final RegistryObject<Item> WIND_BLAST_FOCUS = ITEMS.register("wind_blast_focus", () -> new MagicFocus(new WindBlastSpell()));

    ///Storm
    public static final RegistryObject<Item> CHARGE_FOCUS = ITEMS.register("charge_focus", () -> new MagicFocus(new ChargeSpell()));
    public static final RegistryObject<Item> SHOCKING_FOCUS = ITEMS.register("shocking_focus", () -> new MagicFocus(new ShockingSpell()));
    public static final RegistryObject<Item> THUNDERBOLT_FOCUS = ITEMS.register("thunderbolt_focus", () -> new MagicFocus(new ThunderboltSpell()));
    public static final RegistryObject<Item> ELECTROCUTE_FOCUS = ITEMS.register("electrocute_focus", () -> new MagicFocus(new ElectroOrbSpell()));
    public static final RegistryObject<Item> MONSOON_FOCUS = ITEMS.register("monsoon_focus", () -> new MagicFocus(new MonsoonSpell()));
    public static final RegistryObject<Item> DISCHARGE_FOCUS = ITEMS.register("discharge_focus", () -> new MagicFocus(new DischargeSpell()));
    public static final RegistryObject<Item> LIGHTNING_FOCUS = ITEMS.register("lightning_focus", () -> new MagicFocus(new LightningSpell()));

    ///Nether
    public static final RegistryObject<Item> FIREBALL_FOCUS = ITEMS.register("fireball_focus", () -> new MagicFocus(new FireballSpell()));
    public static final RegistryObject<Item> LAVABALL_FOCUS = ITEMS.register("lavaball_focus", () -> new MagicFocus(new LavaballSpell()));
    public static final RegistryObject<Item> MAGMA_BOMB_FOCUS = ITEMS.register("magma_bomb_focus", () -> new MagicFocus(new MagmaSpell()));
    public static final RegistryObject<Item> FLAME_STRIKE_FOCUS = ITEMS.register("flame_strike_focus", () -> new MagicFocus(new FlameStrikeSpell()));
    public static final RegistryObject<Item> WITHER_SKULL_FOCUS = ITEMS.register("wither_skull_focus", () -> new MagicFocus(new WitherSkullSpell()));
    public static final RegistryObject<Item> GHASTLY_FOCUS = ITEMS.register("ghastly_focus", () -> new MagicFocus(new GhastSpell()));
    public static final RegistryObject<Item> BLAZING_FOCUS = ITEMS.register("blazing_focus", () -> new MagicFocus(new BlazeSpell()));

    ///Void
    public static final RegistryObject<Item> CALL_FOCUS = ITEMS.register("call_focus", CallFocus::new);
    public static final RegistryObject<Item> RECALL_FOCUS = ITEMS.register("recall_focus", RecallFocus::new);
    public static final RegistryObject<Item> ENDER_CHEST_FOCUS = ITEMS.register("ender_chest_focus", () -> new MagicFocus(new EnderChestSpell()));
    public static final RegistryObject<Item> END_WALK_FOCUS = ITEMS.register("end_walk_focus", () -> new MagicFocus(new EndWalkSpell()));
    public static final RegistryObject<Item> BLINK_FOCUS = ITEMS.register("blink_focus", () -> new MagicFocus(new BlinkSpell()));
    public static final RegistryObject<Item> BANISH_FOCUS = ITEMS.register("banish_focus", () -> new MagicFocus(new BanishSpell()));
    public static final RegistryObject<Item> TUNNEL_FOCUS = ITEMS.register("tunnel_focus", () -> new MagicFocus(new TunnelSpell()));
    public static final RegistryObject<Item> RUPTURE_FOCUS = ITEMS.register("rupture_focus", () -> new MagicFocus(new VoidRiftSpell()));

    //Armors
    public static final RegistryObject<Item> CURSED_KNIGHT_HELMET = ITEMS.register("cursed_knight_helmet", () -> new CursedKnightArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CURSED_KNIGHT_CHESTPLATE = ITEMS.register("cursed_knight_chestplate", () -> new CursedKnightArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> CURSED_KNIGHT_LEGGINGS = ITEMS.register("cursed_knight_leggings", () -> new CursedKnightArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> CURSED_KNIGHT_BOOTS = ITEMS.register("cursed_knight_boots", () -> new CursedKnightArmor(ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> CURSED_PALADIN_HELMET = ITEMS.register("cursed_paladin_helmet", () -> new CursedPaladinArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> CURSED_PALADIN_CHESTPLATE = ITEMS.register("cursed_paladin_chestplate", () -> new CursedPaladinArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> CURSED_PALADIN_LEGGINGS = ITEMS.register("cursed_paladin_leggings", () -> new CursedPaladinArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> CURSED_PALADIN_BOOTS = ITEMS.register("cursed_paladin_boots", () -> new CursedPaladinArmor(ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> BLACK_IRON_HELMET = ITEMS.register("black_iron_helmet", () -> new BlackIronArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> BLACK_IRON_CHESTPLATE = ITEMS.register("black_iron_chestplate", () -> new BlackIronArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> BLACK_IRON_LEGGINGS = ITEMS.register("black_iron_leggings", () -> new BlackIronArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> BLACK_IRON_BOOTS = ITEMS.register("black_iron_boots", () -> new BlackIronArmor(ArmorItem.Type.BOOTS));

    public static final RegistryObject<Item> DARK_HELMET = ITEMS.register("dark_helmet", () -> new DarkArmor(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> DARK_CHESTPLATE = ITEMS.register("dark_chestplate", () -> new DarkArmor(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> DARK_LEGGINGS = ITEMS.register("dark_leggings", () -> new DarkArmor(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> DARK_BOOTS = ITEMS.register("dark_boots", () -> new DarkArmor(ArmorItem.Type.BOOTS));

    //Tools & Weapons
    public static final RegistryObject<Item> DARK_WAND = ITEMS.register("dark_wand", DarkWand::new);
    public static final RegistryObject<Item> OMINOUS_STAFF = ITEMS.register("ominous_staff", () -> new DarkStaff(ItemConfig.OminousStaffDamage.get(), SpellType.ILL));
    public static final RegistryObject<Item> NECRO_STAFF = ITEMS.register("necro_staff", () -> new DarkStaff(ItemConfig.NecroStaffDamage.get(), SpellType.NECROMANCY));
    public static final RegistryObject<Item> WIND_STAFF = ITEMS.register("wind_staff", () -> new DarkStaff(ItemConfig.WindStaffDamage.get(), SpellType.WIND));
    public static final RegistryObject<Item> STORM_STAFF = ITEMS.register("storm_staff", () -> new DarkStaff(ItemConfig.StormStaffDamage.get(), SpellType.STORM));
    public static final RegistryObject<Item> FROST_STAFF = ITEMS.register("frost_staff", () -> new DarkStaff(ItemConfig.FrostStaffDamage.get(), SpellType.FROST));
    public static final RegistryObject<Item> WILD_STAFF = ITEMS.register("wild_staff", () -> new DarkStaff(ItemConfig.WildStaffDamage.get(), SpellType.WILD));
    public static final RegistryObject<Item> NETHER_STAFF = ITEMS.register("nether_staff", () -> new DarkStaff(DarkWand.wandProperties().fireResistant(), ItemConfig.NetherStaffDamage.get(), SpellType.NETHER));
    public static final RegistryObject<Item> NAMELESS_STAFF = ITEMS.register("nameless_staff", () -> new DarkStaff(ItemConfig.NamelessStaffDamage.get(), SpellType.NECROMANCY));
    public static final RegistryObject<Item> OMINOUS_SCYTHE = ITEMS.register("dark_scythe", DarkScytheItem::new);
    public static final RegistryObject<Item> DARK_METAL_SCYTHE = ITEMS.register("dark_metal_scythe", () -> new DarkScytheItem(ModTiers.DARK));
    public static final RegistryObject<Item> DEATH_SCYTHE = ITEMS.register("death_scythe", DeathScytheItem::new);
    public static final RegistryObject<Item> GREAT_HAMMER = ITEMS.register("great_hammer", HammerItem::new);
    public static final RegistryObject<Item> STORMLANDER = ITEMS.register("stormlander", StormlanderItem::new);
    public static final RegistryObject<Item> FANGED_DAGGER = ITEMS.register("fanged_dagger", FangedDaggerItem::new);
    public static final RegistryObject<Item> EERIE_PICKAXE = ITEMS.register("eerie_pickaxe", EeriePickaxeItem::new);
    public static final RegistryObject<Item> RAMPAGING_AXE = ITEMS.register("rampaging_axe", RampagingAxeItem::new);
    public static final RegistryObject<Item> GRAVEROBBER_SHOVEL = ITEMS.register("graverobber_shovel", GraverobberShovelItem::new);
    public static final RegistryObject<Item> HUNTERS_BOW = ITEMS.register("hunters_bow", HuntersBowItem::new);
    public static final RegistryObject<Item> PHILOSOPHERS_MACE = ITEMS.register("philosophers_mace", PhilosophersMaceItem::new);
    public static final RegistryObject<Item> DARK_SWORD = ITEMS.register("dark_sword", ModToolItems.DarkSwordItem::new);
    public static final RegistryObject<Item> DARK_SHOVEL = ITEMS.register("dark_shovel", ModToolItems.DarkShovelItem::new);
    public static final RegistryObject<Item> DARK_PICKAXE = ITEMS.register("dark_pickaxe", ModToolItems.DarkPickaxeItem::new);
    public static final RegistryObject<Item> DARK_AXE = ITEMS.register("dark_axe", ModToolItems.DarkAxeItem::new);
    public static final RegistryObject<Item> DARK_HOE = ITEMS.register("dark_hoe", ModToolItems.DarkHoeItem::new);
    public static final RegistryObject<Item> FELL_BLADE = ITEMS.register("fell_blade", () -> new SwordItem(ModTiers.SPECIAL, 3, -2.4F, new Item.Properties().durability(256)));
    public static final RegistryObject<Item> FROZEN_BLADE = ITEMS.register("frozen_blade", () -> new SwordItem(ModTiers.SPECIAL, 4, -2.4F, new Item.Properties()));

    //Sherds
    public static final RegistryObject<Item> CROSS_POTTERY_SHERD = ITEMS.register("cross_pottery_sherd", ItemBase::new);
    public static final RegistryObject<Item> DEAD_POTTERY_SHERD = ITEMS.register("dead_pottery_sherd", ItemBase::new);
    public static final RegistryObject<Item> HAUNT_POTTERY_SHERD = ITEMS.register("haunt_pottery_sherd", ItemBase::new);
    public static final RegistryObject<Item> NIGHT_POTTERY_SHERD = ITEMS.register("night_pottery_sherd", ItemBase::new);
    public static final RegistryObject<Item> SOUL_POTTERY_SHERD = ITEMS.register("soul_pottery_sherd", ItemBase::new);

    //Discs
    public static final RegistryObject<Item> MUSIC_DISC_VIZIER = ITEMS.register("music_disc_vizier", () -> new RecordItem(14, ModSounds.MUSIC_DISC_VIZIER, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE), 1860));
    public static final RegistryObject<Item> MUSIC_DISC_APOSTLE = ITEMS.register("music_disc_apostle", () -> new RecordItem(15, ModSounds.MUSIC_DISC_APOSTLE, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE),3220));

    //Dummies
    public static final RegistryObject<Item> PEDESTAL_DUMMY = ITEMS.register("pedestal_dummy",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_NONE = ITEMS.register(
            "jei_dummy/none", () -> new DummyItem(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_REQUIRE_SACRIFICE = ITEMS.register(
            "jei_dummy/sacrifice", () -> new DummyItem(new Item.Properties()));
    public static final RegistryObject<Item> BONE_SHARD = ITEMS.register("bone_shard",
            () -> new Item(new Item.Properties()));

    public static Item.Properties baseProperities(){
        return new Item.Properties();
    }

    public static boolean shouldSkipCreativeModTab(Item item) {
        return item == JEI_DUMMY_NONE.get()
                || item == JEI_DUMMY_REQUIRE_SACRIFICE.get()
                || item == PEDESTAL_DUMMY.get()
                || item == BONE_SHARD.get()
                || item instanceof TotemOfSouls
                || item instanceof BrewItem;
    }
}
