package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.items.armor.CursedKnightArmor;
import com.Polarice3.Goety.common.items.armor.DarkArmor;
import com.Polarice3.Goety.common.items.brew.BrewApple;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.brew.LingeringBrewItem;
import com.Polarice3.Goety.common.items.brew.SplashBrewItem;
import com.Polarice3.Goety.common.items.curios.*;
import com.Polarice3.Goety.common.items.equipment.*;
import com.Polarice3.Goety.common.items.magic.*;
import com.Polarice3.Goety.common.items.research.ForbiddenScroll;
import com.Polarice3.Goety.common.items.research.Scroll;
import com.Polarice3.Goety.common.magic.spells.*;
import com.Polarice3.Goety.common.magic.spells.summon.*;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.entity.EquipmentSlot;
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

    public static final RegistryObject<Item> TOTEM_OF_ROOTS = ITEMS.register("totem_of_roots", TotemOfRoots::new);
    public static final RegistryObject<Item> TOTEM_OF_SOULS = ITEMS.register("totem_of_souls", TotemOfSouls::new);

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
    public static final RegistryObject<Item> SOUL_RUBY = ITEMS.register("soul_ruby", ItemBase::new);
    public static final RegistryObject<Item> EMPTY_FOCUS = ITEMS.register("empty_focus", ItemBase::new);
    public static final RegistryObject<Item> ANIMATION_CORE = ITEMS.register("animation_core", AnimationCore::new);
    public static final RegistryObject<Item> HUNGER_CORE = ITEMS.register("hunger_core", ItemBase::new);
    public static final RegistryObject<Item> WIND_CORE = ITEMS.register("wind_core", ItemBase::new);
    public static final RegistryObject<Item> MYSTIC_CORE = ITEMS.register("mystic_core", ItemBase::new);
    public static final RegistryObject<Item> OMINOUS_ORB = ITEMS.register("ominous_orb", RepeatCraftItem::new);
    public static final RegistryObject<Item> HEART_OF_THE_NIGHT = ITEMS.register("heart_of_the_night", ItemBase::new);
    public static final RegistryObject<Item> CAULDRON_LADLE = ITEMS.register("cauldron_ladle", ItemBase::new);
    public static final RegistryObject<Item> OMINOUS_SADDLE = ITEMS.register("ominous_saddle", ItemBase::new);
    public static final RegistryObject<Item> IRON_RAVAGER_ARMOR = ITEMS.register("iron_ravager_armor", () -> new RavagerArmorItem(7, "iron"));
    public static final RegistryObject<Item> GOLD_RAVAGER_ARMOR = ITEMS.register("gold_ravager_armor", () -> new RavagerArmorItem(11, "gold"));
    public static final RegistryObject<Item> DIAMOND_RAVAGER_ARMOR = ITEMS.register("diamond_ravager_armor", () -> new RavagerArmorItem(15, "diamond"));
    public static final RegistryObject<Item> NETHERITE_RAVAGER_ARMOR = ITEMS.register("netherite_ravager_armor", () -> new RavagerArmorItem(20, "netherite", new Item.Properties().stacksTo(1).tab(Goety.TAB).fireResistant()));
    public static final RegistryObject<Item> FORBIDDEN_PIECE = ITEMS.register("forbidden_piece", ItemBase::new);
    public static final RegistryObject<Item> FORBIDDEN_FRAGMENT = ITEMS.register("forbidden_fragment", ItemBase::new);

    public static final RegistryObject<Item> FEET_OF_FROG = ITEMS.register("feet_of_frog", () -> new Item(new Item.Properties().food(Foods.COD).tab(Goety.TAB)));
    public static final RegistryObject<Item> COOKED_FEET_OF_FROG = ITEMS.register("cooked_feet_of_frog", () -> new Item(new Item.Properties().food(Foods.COOKED_COD).tab(Goety.TAB)));

    public static final RegistryObject<Item> PHILOSOPHERS_STONE = ITEMS.register("philosophers_stone", PhilosophersStone::new);
    public static final RegistryObject<Item> DARK_SCROLL = ITEMS.register("dark_scroll", DarkScrollItem::new);
    public static final RegistryObject<Item> MAGIC_EMERALD = ITEMS.register("magic_emerald", () -> new SimpleFoiledItem(new Item.Properties().tab(Goety.TAB)));
    public static final RegistryObject<Item> SOUL_EMERALD = ITEMS.register("soul_emerald", () -> new SimpleFoiledItem(new Item.Properties().tab(Goety.TAB)));
    public static final RegistryObject<Item> UNHOLY_BLOOD = ITEMS.register("unholy_blood", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE).tab(Goety.TAB)));
    public static final RegistryObject<Item> SOUL_TRANSFER = ITEMS.register("soul_transfer", SoulTransferItem::new);
    public static final RegistryObject<Item> FLAME_CAPTURE = ITEMS.register("flame_capture", FlameCaptureItem::new);
    public static final RegistryObject<Item> SNAP_FUNGUS = ITEMS.register("snap_fungus", SnapFungusItem::new);
    public static final RegistryObject<Item> BLAST_FUNGUS = ITEMS.register("blast_fungus", BlastFungusItem::new);
    public static final RegistryObject<Item> BERSERK_FUNGUS = ITEMS.register("berserk_fungus", BerserkFungusItem::new);
    public static final RegistryObject<Item> WARTFUL_EGG = ITEMS.register("wartful_egg", WartlingEggItem::new);
    public static final RegistryObject<Item> REFUSE_BOTTLE = ITEMS.register("refuse_bottle", RefuseBottleItem::new);
    public static final RegistryObject<Item> ILL_BOMB = ITEMS.register("ill_bomb", IllBombItem::new);
    public static final RegistryObject<Item> GRIMOIRE_OF_GRUDGES = ITEMS.register("grimoire_of_grudges", GrudgeGrimoire::new);

    public static final RegistryObject<Item> RAVAGING_SCROLL = ITEMS.register("ravaging_scroll", () -> new Scroll(ResearchList.RAVAGING));
    public static final RegistryObject<Item> WARRED_SCROLL = ITEMS.register("warred_scroll", () -> new Scroll(ResearchList.WARRED));
    public static final RegistryObject<Item> FORBIDDEN_SCROLL = ITEMS.register("forbidden_scroll", ForbiddenScroll::new);

    public static final RegistryObject<Item> UNDEATH_POTION = ITEMS.register("undeath_potion", UndeathPotionItem::new);

    public static final RegistryObject<Item> BREW = ITEMS.register("brew", BrewItem::new);
    public static final RegistryObject<Item> SPLASH_BREW = ITEMS.register("splash_brew", SplashBrewItem::new);
    public static final RegistryObject<Item> LINGERING_BREW = ITEMS.register("lingering_brew", LingeringBrewItem::new);
    public static final RegistryObject<Item> GAS_BREW = ITEMS.register("gas_brew", SplashBrewItem::new);
    public static final RegistryObject<Item> BREW_APPLE = ITEMS.register("brew_apple", BrewApple::new);

    public static final RegistryObject<Item> HAUNTED_BOAT = ITEMS.register("haunted_boat", () -> new ModBoatItem(false, ModBoat.Type.HAUNTED, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));
    public static final RegistryObject<Item> HAUNTED_CHEST_BOAT = ITEMS.register("haunted_chest_boat", () -> new ModBoatItem(true, ModBoat.Type.HAUNTED, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));

    public static final RegistryObject<Item> ROTTEN_BOAT = ITEMS.register("rotten_boat", () -> new ModBoatItem(false, ModBoat.Type.ROTTEN, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));
    public static final RegistryObject<Item> ROTTEN_CHEST_BOAT = ITEMS.register("rotten_chest_boat", () -> new ModBoatItem(true, ModBoat.Type.ROTTEN, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));

    //Curios
    public static final RegistryObject<Item> FOCUS_BAG = ITEMS.register("focus_bag", FocusBag::new);
    public static final RegistryObject<Item> RING_OF_WANT = ITEMS.register("ring_of_want", RingItem::new);
    public static final RegistryObject<Item> PENDANT_OF_ROT = ITEMS.register("pendant_of_hunger", PendantOfHungerItem::new);
    public static final RegistryObject<Item> TARGETING_MONOCLE = ITEMS.register("targeting_monocle", SingleStackItem::new);
    public static final RegistryObject<Item> DARK_HAT = ITEMS.register("dark_hat", MagicHatItem::new);
    public static final RegistryObject<Item> GRAND_TURBAN = ITEMS.register("grand_turban", MagicHatItem::new);
    public static final RegistryObject<Item> NECRO_CROWN = ITEMS.register("necro_crown", MagicHatItem::new);
    public static final RegistryObject<Item> NAMELESS_CROWN = ITEMS.register("nameless_crown", MagicHatItem::new);
    public static final RegistryObject<Item> AMETHYST_NECKLACE = ITEMS.register("amethyst_necklace", SingleStackItem::new);
    public static final RegistryObject<Item> WITCH_HAT = ITEMS.register("witch_hat", SingleStackItem::new);
    public static final RegistryObject<Item> CRONE_HAT = ITEMS.register("crone_hat", SingleStackItem::new);
    public static final RegistryObject<Item> DARK_ROBE = ITEMS.register("dark_robe", MagicRobeItem::new);
    public static final RegistryObject<Item> GRAND_ROBE = ITEMS.register("grand_robe", MagicRobeItem::new);
    public static final RegistryObject<Item> NECRO_CAPE = ITEMS.register("necro_cape", SingleStackItem::new);
    public static final RegistryObject<Item> NAMELESS_CAPE = ITEMS.register("nameless_cape", SingleStackItem::new);
    public static final RegistryObject<Item> ILLUSION_ROBE = ITEMS.register("illusion_robe", SingleStackItem::new);
    public static final RegistryObject<Item> FROST_ROBE = ITEMS.register("frost_robe", SingleStackItem::new);
    public static final RegistryObject<Item> WIND_ROBE = ITEMS.register("wind_robe", SingleStackItem::new);
    public static final RegistryObject<Item> WITCH_ROBE = ITEMS.register("witch_robe", WitchRobeItem::new);
    public static final RegistryObject<Item> WITCH_ROBE_HEDGE = ITEMS.register("witch_robe_hedge", WitchRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_ROBE = ITEMS.register("warlock_robe", WarlockRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_ROBE_DARK = ITEMS.register("warlock_robe_dark", WarlockRobeItem::new);
    public static final RegistryObject<Item> WARLOCK_SASH = ITEMS.register("warlock_sash", WarlockGarmentItem::new);
    public static final RegistryObject<Item> SEA_AMULET = ITEMS.register("sea_amulet", SeaAmuletItem::new);
    public static final RegistryObject<Item> WARDING_CHARM = ITEMS.register("warding_charm", WardingCharmItem::new);
    public static final RegistryObject<Item> WAYFARERS_BELT = ITEMS.register("wayfarers_belt", WayfarersBeltItem::new);
    public static final RegistryObject<Item> SPITEFUL_BELT = ITEMS.register("spiteful_belt", SingleStackItem::new);
    public static final RegistryObject<Item> STAR_AMULET = ITEMS.register("star_amulet", SingleFoiledStackItem::new);
    public static final RegistryObject<Item> GRAVE_GLOVE = ITEMS.register("grave_glove", GloveItem::new);

    //Focus
    public static final RegistryObject<Item> VEXING_FOCUS = ITEMS.register("vexing_focus", () -> new MagicFocus(new VexSpell()));
    public static final RegistryObject<Item> BITING_FOCUS = ITEMS.register("biting_focus", () -> new MagicFocus(new FangSpell()));
    public static final RegistryObject<Item> FEAST_FOCUS = ITEMS.register("feast_focus", () -> new MagicFocus(new FeastSpell()));
    public static final RegistryObject<Item> TEETH_FOCUS = ITEMS.register("teeth_focus", () -> new MagicFocus(new TeethSpell()));
    public static final RegistryObject<Item> ICEOLOGY_FOCUS = ITEMS.register("iceology_focus", () -> new MagicFocus(new IceChunkSpell()));
    public static final RegistryObject<Item> BARRICADE_FOCUS = ITEMS.register("barricade_focus", () -> new MagicFocus(new BarricadeSpell()));
    public static final RegistryObject<Item> ILLUSION_FOCUS = ITEMS.register("illusion_focus", () -> new MagicFocus(new IllusionSpell()));
    public static final RegistryObject<Item> FIREBALL_FOCUS = ITEMS.register("fireball_focus", () -> new MagicFocus(new FireballSpell()));
    public static final RegistryObject<Item> LAVABALL_FOCUS = ITEMS.register("lavaball_focus", () -> new MagicFocus(new LavaballSpell()));
    public static final RegistryObject<Item> SOUL_BOLT_FOCUS = ITEMS.register("soul_bolt_focus", () -> new MagicFocus(new SoulBoltSpell()));
    public static final RegistryObject<Item> MAGIC_BOLT_FOCUS = ITEMS.register("magic_bolt_focus", () -> new MagicFocus(new MagicBoltSpell()));
    public static final RegistryObject<Item> SWORD_FOCUS = ITEMS.register("sword_focus", () -> new MagicFocus(new SwordSpell()));
    public static final RegistryObject<Item> ICE_SPIKE_FOCUS = ITEMS.register("ice_spike_focus", () -> new MagicFocus(new IceSpikeSpell()));
    public static final RegistryObject<Item> CHARGE_FOCUS = ITEMS.register("charge_focus", () -> new MagicFocus(new ChargeSpell()));
    public static final RegistryObject<Item> LIGHTNING_FOCUS = ITEMS.register("lightning_focus", () -> new MagicFocus(new LightningSpell()));
    public static final RegistryObject<Item> SHOCKWAVE_FOCUS = ITEMS.register("shockwave_focus", () -> new MagicFocus(new ShockwaveSpell()));
    public static final RegistryObject<Item> SONIC_BOOM_FOCUS = ITEMS.register("sonic_boom_focus", () -> new MagicFocus(new SonicBoomSpell()));
    public static final RegistryObject<Item> LAUNCH_FOCUS = ITEMS.register("launch_focus", () -> new MagicFocus(new LaunchSpell()));
    public static final RegistryObject<Item> FLYING_FOCUS = ITEMS.register("flying_focus", () -> new MagicFocus(new FlyingSpell()));
    public static final RegistryObject<Item> UPDRAFT_FOCUS = ITEMS.register("updraft_focus", () -> new MagicFocus(new UpdraftSpell()));
    public static final RegistryObject<Item> WIND_BLAST_FOCUS = ITEMS.register("wind_blast_focus", () -> new MagicFocus(new WindBlastSpell()));
    public static final RegistryObject<Item> SOUL_LIGHT_FOCUS = ITEMS.register("soul_light_focus", () -> new MagicFocus(new SoulLightSpell()));
    public static final RegistryObject<Item> GLOW_LIGHT_FOCUS = ITEMS.register("glow_light_focus", () -> new MagicFocus(new GlowLightSpell()));
    public static final RegistryObject<Item> ROTTING_FOCUS = ITEMS.register("rotting_focus", () -> new MagicFocus(new ZombieSpell()));
    public static final RegistryObject<Item> OSSEOUS_FOCUS = ITEMS.register("osseous_focus", () -> new MagicFocus(new SkeletonSpell()));
    public static final RegistryObject<Item> SPOOKY_FOCUS = ITEMS.register("spooky_focus", () -> new MagicFocus(new WraithSpell()));
    public static final RegistryObject<Item> SKULL_FOCUS = ITEMS.register("skull_focus", () -> new MagicFocus(new HauntedSkullSpell()));
    public static final RegistryObject<Item> CALL_FOCUS = ITEMS.register("call_focus", CallFocus::new);
    public static final RegistryObject<Item> RECALL_FOCUS = ITEMS.register("recall_focus", RecallFocus::new);
    public static final RegistryObject<Item> CORRUPTION_FOCUS = ITEMS.register("corruption_focus", () -> new MagicFocus(new CorruptedBeamSpell()));

    //Armors
    public static final RegistryObject<Item> CURSED_KNIGHT_HELMET = ITEMS.register("cursed_knight_helmet", () -> new CursedKnightArmor(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> CURSED_KNIGHT_CHESTPLATE = ITEMS.register("cursed_knight_chestplate", () -> new CursedKnightArmor(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> CURSED_KNIGHT_LEGGINGS = ITEMS.register("cursed_knight_leggings", () -> new CursedKnightArmor(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> CURSED_KNIGHT_BOOTS = ITEMS.register("cursed_knight_boots", () -> new CursedKnightArmor(EquipmentSlot.FEET));

    public static final RegistryObject<Item> DARK_HELMET = ITEMS.register("dark_helmet", () -> new DarkArmor(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DARK_CHESTPLATE = ITEMS.register("dark_chestplate", () -> new DarkArmor(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DARK_LEGGINGS = ITEMS.register("dark_leggings", () -> new DarkArmor(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DARK_BOOTS = ITEMS.register("dark_boots", () -> new DarkArmor(EquipmentSlot.FEET));

    //Tools & Weapons
    public static final RegistryObject<Item> DARK_WAND = ITEMS.register("dark_wand", DarkWand::new);
    public static final RegistryObject<Item> OMINOUS_STAFF = ITEMS.register("ominous_staff", () -> new DarkStaff(MainConfig.OminousStaffDamage.get()));
    public static final RegistryObject<Item> NECRO_STAFF = ITEMS.register("necro_staff", () -> new DarkStaff(MainConfig.NecroStaffDamage.get()));
    public static final RegistryObject<Item> WIND_STAFF = ITEMS.register("wind_staff", () -> new DarkStaff(MainConfig.WindStaffDamage.get()));
    public static final RegistryObject<Item> NAMELESS_STAFF = ITEMS.register("nameless_staff", () -> new DarkStaff(MainConfig.NamelessStaffDamage.get()));
    public static final RegistryObject<Item> DARK_SCYTHE = ITEMS.register("dark_scythe", DarkScytheItem::new);
    public static final RegistryObject<Item> DEATH_SCYTHE = ITEMS.register("death_scythe", DeathScytheItem::new);
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
    public static final RegistryObject<Item> FELL_BLADE = ITEMS.register("fell_blade", () -> new SwordItem(ModTiers.SPECIAL, 3, -2.4F, new Item.Properties().durability(256).tab(Goety.TAB)));

    //Discs
    public static final RegistryObject<Item> MUSIC_DISC_VIZIER = ITEMS.register("music_disc_vizier", () -> new RecordItem(14, ModSounds.MUSIC_DISC_VIZIER, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE), (int) MathHelper.minutesToTicks(1.33F)));
    public static final RegistryObject<Item> MUSIC_DISC_APOSTLE = ITEMS.register("music_disc_apostle", () -> new RecordItem(15, ModSounds.MUSIC_DISC_APOSTLE, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE),(int) MathHelper.minutesToTicks(2.41F)));

    //Dummies
    public static final RegistryObject<Item> PEDESTAL_DUMMY = ITEMS.register("pedestal_dummy",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_NONE = ITEMS.register(
            "jei_dummy/none", () -> new DummyItem(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_REQUIRE_SACRIFICE = ITEMS.register(
            "jei_dummy/sacrifice", () -> new DummyItem(new Item.Properties()));

    public static Item.Properties baseProperities(){
        return new Item.Properties().tab(Goety.TAB);
    }
}
