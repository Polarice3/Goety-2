package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.items.ModBoat;
import com.Polarice3.Goety.common.items.curios.HatItem;
import com.Polarice3.Goety.common.items.curios.RobeItem;
import com.Polarice3.Goety.common.items.curios.RingItem;
import com.Polarice3.Goety.common.items.magic.FocusBag;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModMathHelper;
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

    public static final RegistryObject<TotemOfSouls> TOTEM_OF_SOULS = ITEMS.register("totem_of_souls", TotemOfSouls::new);

    //Basic
    public static final RegistryObject<Item> SPENT_TOTEM = ITEMS.register("spent_totem", ItemBase::new);
    public static final RegistryObject<Item> CURSED_INGOT = ITEMS.register("cursed_ingot", ItemBase::new);
    public static final RegistryObject<Item> ECTOPLASM = ITEMS.register("ectoplasm", ItemBase::new);
    public static final RegistryObject<Item> DARK_FABRIC = ITEMS.register("dark_fabric", ItemBase::new);
    public static final RegistryObject<Item> MAGIC_FABRIC = ITEMS.register("magic_fabric", ItemBase::new);
    public static final RegistryObject<Item> OCCULT_FABRIC = ITEMS.register("occult_fabric", ItemBase::new);
    public static final RegistryObject<Item> SPIRIT_FABRIC = ITEMS.register("spirit_fabric", ItemBase::new);
    public static final RegistryObject<Item> SAVAGE_TOOTH = ITEMS.register("savage_tooth", ItemBase::new);
    public static final RegistryObject<Item> SOUL_RUBY = ITEMS.register("soul_ruby", ItemBase::new);
    public static final RegistryObject<Item> EMPTY_FOCUS = ITEMS.register("empty_focus", ItemBase::new);
    public static final RegistryObject<Item> ANIMATION_CORE = ITEMS.register("animation_core", ItemBase::new);
    public static final RegistryObject<Item> HUNGER_CORE = ITEMS.register("hunger_core", ItemBase::new);
    public static final RegistryObject<Item> WIND_CORE = ITEMS.register("wind_core", ItemBase::new);

    public static final RegistryObject<Item> DARK_SCROLL = ITEMS.register("dark_scroll", DarkScrollItem::new);
    public static final RegistryObject<Item> MAGIC_EMERALD = ITEMS.register("magic_emerald", () -> new SimpleFoiledItem(new Item.Properties().tab(Goety.TAB)));
    public static final RegistryObject<Item> SOUL_EMERALD = ITEMS.register("soul_emerald", () -> new SimpleFoiledItem(new Item.Properties().tab(Goety.TAB)));
    public static final RegistryObject<Item> UNHOLY_BLOOD = ITEMS.register("unholy_blood", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE).tab(Goety.TAB)));
    public static final RegistryObject<Item> SOUL_TRANSFER = ITEMS.register("soul_transfer", SoulTransferItem::new);
    public static final RegistryObject<Item> FLAME_CAPTURE = ITEMS.register("flame_capture", FlameCaptureItem::new);

    public static final RegistryObject<ModBoatItem> HAUNTED_BOAT = ITEMS.register("haunted_boat", () -> new ModBoatItem(ModBoat.Type.HAUNTED, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));

    //Curios
    public static final RegistryObject<Item> FOCUS_BAG = ITEMS.register("focus_bag", FocusBag::new);
    public static final RegistryObject<Item> RING_OF_WANT = ITEMS.register("ring_of_want", RingItem::new);
    public static final RegistryObject<Item> DARK_HAT = ITEMS.register("dark_hat", HatItem::new);
    public static final RegistryObject<Item> DARK_ROBE = ITEMS.register("dark_robe", RobeItem::new);
    public static final RegistryObject<Item> ILLUSION_HELM = ITEMS.register("illusion_helm", HatItem::new);
    public static final RegistryObject<Item> ILLUSION_ROBE = ITEMS.register("illusion_robe", RobeItem::new);

    //Focus
    public static final RegistryObject<Item> VEXING_FOCUS = ITEMS.register("vexing_focus", () -> new MagicFocus(SpellConfig.VexCost.get()));
    public static final RegistryObject<Item> BITING_FOCUS = ITEMS.register("biting_focus", () -> new MagicFocus(SpellConfig.FangCost.get()));
    public static final RegistryObject<Item> FEAST_FOCUS = ITEMS.register("feast_focus", () -> new MagicFocus(SpellConfig.FeastCost.get()));
    public static final RegistryObject<Item> ICEOLOGY_FOCUS = ITEMS.register("iceology_focus", () -> new MagicFocus(SpellConfig.IceChunkCost.get()));
    public static final RegistryObject<Item> ILLUSION_FOCUS = ITEMS.register("illusion_focus", () -> new MagicFocus(SpellConfig.IllusionCost.get()));
    public static final RegistryObject<Item> SOUL_BOLT_FOCUS = ITEMS.register("soul_bolt_focus", () -> new MagicFocus(SpellConfig.SoulBoltCost.get()));
    public static final RegistryObject<Item> SONIC_BOOM_FOCUS = ITEMS.register("sonic_boom_focus", () -> new MagicFocus(SpellConfig.SonicBoomCost.get()));
    public static final RegistryObject<Item> LAUNCH_FOCUS = ITEMS.register("launch_focus", () -> new MagicFocus(SpellConfig.LaunchCost.get()));
    public static final RegistryObject<Item> SOUL_LIGHT_FOCUS = ITEMS.register("soul_light_focus", () -> new MagicFocus(SpellConfig.SoulLightCost.get()));
    public static final RegistryObject<Item> GLOW_LIGHT_FOCUS = ITEMS.register("glow_light_focus", () -> new MagicFocus(SpellConfig.GlowLightCost.get()));
    public static final RegistryObject<Item> ROTTING_FOCUS = ITEMS.register("rotting_focus", () -> new MagicFocus(SpellConfig.ZombieCost.get()));
    public static final RegistryObject<Item> OSSEOUS_FOCUS = ITEMS.register("osseous_focus", () -> new MagicFocus(SpellConfig.SkeletonCost.get()));
    public static final RegistryObject<Item> SPOOKY_FOCUS = ITEMS.register("spooky_focus", () -> new MagicFocus(SpellConfig.WraithCost.get()));

    //Tools & Weapons
    public static final RegistryObject<Item> DARK_WAND = ITEMS.register("dark_wand", DarkWand::new);

    //Discs
    public static final RegistryObject<Item> MUSIC_DISC_VIZIER = ITEMS.register("music_disc_vizier", () -> new RecordItem(14, ModSounds.VIZIER_THEME, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE), (int) ModMathHelper.ticksToMinutes(1.33F)));
    public static final RegistryObject<Item> MUSIC_DISC_APOSTLE = ITEMS.register("music_disc_apostle", () -> new RecordItem(15, ModSounds.APOSTLE_THEME, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE),(int) ModMathHelper.ticksToMinutes(2.41F)));

    //Dummies
    public static final RegistryObject<Item> PEDESTAL_DUMMY = ITEMS.register("pedestal_dummy",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_NONE = ITEMS.register(
            "jei_dummy/none", () -> new DummyItem(new Item.Properties()));
    public static final RegistryObject<DummyItem> JEI_DUMMY_REQUIRE_SACRIFICE = ITEMS.register(
            "jei_dummy/sacrifice", () -> new DummyItem(new Item.Properties()));
}
