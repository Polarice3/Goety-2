package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.block.ModISTER;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.block.*;
import com.Polarice3.Goety.common.world.features.trees.HauntedTree;
import com.Polarice3.Goety.common.world.features.trees.RottenTree;
import com.Polarice3.Goety.common.world.features.trees.WindsweptTree;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Goety.MOD_ID);
    public static final Map<ResourceLocation, BlockLootSetting> BLOCK_LOOT = new HashMap<>();

    public static void init(){
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> ARCA_BLOCK = register("arca", ArcaBlock::new);
    public static final RegistryObject<Block> CURSED_INFUSER = register("cursed_infuser", CursedInfuserBlock::new);
    public static final RegistryObject<Block> CURSED_CAGE_BLOCK = register("cursed_cage", CursedCageBlock::new);
    public static final RegistryObject<Block> DARK_ALTAR = register("dark_altar", DarkAltarBlock::new);
    public static final RegistryObject<Block> PEDESTAL = register("pedestal", PedestalBlock::new);
    public static final RegistryObject<Block> SOUL_ABSORBER = register("soul_absorber", SoulAbsorberBlock::new);
    public static final RegistryObject<Block> SOUL_MENDER = register("soul_mender", SoulMenderBlock::new);
    public static final RegistryObject<Block> ICE_BOUQUET_TRAP = register("ice_bouquet_trap", IceBouquetTrapBlock::new);
    public static final RegistryObject<Block> WIND_BLOWER = register("wind_blower", () -> new WindBlowerBlock(ShadeStoneProperties()));
    public static final RegistryObject<Block> MARBLE_WIND_BLOWER = register("marble_wind_blower", () -> new WindBlowerBlock(MarbleProperties()));
    public static final RegistryObject<Block> RESONANCE_CRYSTAL = register("resonance_crystal", ResonanceCrystalBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> SCULK_DEVOURER = enchantedRegister("sculk_devourer", SculkDevourerBlock::new);
    public static final RegistryObject<Block> SCULK_CONVERTER = enchantedRegister("sculk_converter", SculkConverterBlock::new);
    public static final RegistryObject<Block> SCULK_RELAY = register("sculk_relay", SculkRelayBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SCULK_GROWER = enchantedRegister("sculk_grower", SculkGrowerBlock::new);
    public static final RegistryObject<Block> SPIDER_NEST = register("spider_nest", SpiderNestBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> FORBIDDEN_GRASS = register("forbidden_grass", ForbiddenGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> HOOK_BELL = register("hook_bell", HookBellBlock::new);
    public static final RegistryObject<Block> NECRO_BRAZIER = register("necro_brazier", NecroBrazierBlock::new);
    public static final RegistryObject<Block> DARK_ANVIL = register("dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> CHIPPED_DARK_ANVIL = register("chipped_dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> DAMAGED_DARK_ANVIL = register("damaged_dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> SOUL_CANDLESTICK = register("soul_candlestick", SoulCandlestickBlock::new);
    public static final RegistryObject<Block> WITCH_POLE = register("witch_pole", WitchPoleBlock::new);
    public static final RegistryObject<Block> BREWING_CAULDRON = register("witch_cauldron", BrewCauldronBlock::new);
    public static final RegistryObject<Block> CRYSTAL_BALL = register("crystal_ball", CrystalBallBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> MAGIC_THORN = register("magic_thorn", MagicThornBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> HARDENED_LEAVES = register("hardened_leaves", ()
            -> new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(2.0F).randomTicks().sound(SoundType.GRASS)
                    .noOcclusion().isValidSpawn(ModBlocks::ocelotOrParrot).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never)),
            true, LootTableType.EMPTY);
    public static final RegistryObject<Block> OVERGROWN_ROOTS = register("overgrown_roots", ()
                    -> new MangroveRootsBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(2.0F).sound(SoundType.MANGROVE_ROOTS)),
            true);
    public static final RegistryObject<Block> CORPSE_BLOSSOM = register("corpse_blossom", ()
                    -> new CorpseBlossomBlock(BlockBehaviour.Properties.of(Material.PLANT)
                    .instabreak()
                    .noCollission()
                    .sound(SoundType.SPORE_BLOSSOM)),
            true);
    public static final RegistryObject<Block> HOLE = register("hole", HoleBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> PITHOS = register("pithos", PithosBlock::new);
    public static final RegistryObject<Block> NIGHT_BEACON = register("night_beacon", NightBeaconBlock::new, false);
    public static final RegistryObject<Block> TALL_SKULL_BLOCK = register("tall_skull", TallSkullBlock::new, false);
    public static final RegistryObject<Block> WALL_TALL_SKULL_BLOCK = register("wall_tall_skull", WallTallSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> REDSTONE_GOLEM_SKULL_BLOCK = register("redstone_golem_skull", RedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_REDSTONE_GOLEM_SKULL_BLOCK = register("wall_redstone_golem_skull", WallRedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);

    //Plants
    public static final RegistryObject<Block> SNAP_WARTS = register("snap_warts", SnapWartsBlock::new, false, LootTableType.EMPTY);

    //Deco
    public static final RegistryObject<Block> CURSED_METAL_BLOCK = register("cursed_metal_block", CursedMetalBlock::new);
    public static final RegistryObject<Block> DARK_METAL_BLOCK = register("dark_metal_block", DarkMetalBlock::new);

    public static final RegistryObject<Block> HAUNTED_GLASS = register("haunted_glass", () -> new HauntedGlassBlock(glassProperties(), true, false));
    public static final RegistryObject<Block> TINTED_HAUNTED_GLASS = register("haunted_glass_tinted", () -> new HauntedGlassBlock(tintedGlassProperties(), true, true));
    public static final RegistryObject<Block> HAUNTED_GLASS_MOB = register("haunted_glass_mob", () -> new HauntedGlassBlock(glassProperties(), false, false));
    public static final RegistryObject<Block> TINTED_HAUNTED_GLASS_MOB = register("haunted_glass_mob_tinted", () -> new HauntedGlassBlock(tintedGlassProperties(), false, true));

    public static final RegistryObject<Block> FREEZING_LAMP = register("freezing_lamp", FreezeLampBlock::new);

    public static final RegistryObject<Block> SHADE_BRAZIER = register("shade_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> STONE_BRAZIER = register("stone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BRICK_BRAZIER = register("brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> DEEPSLATE_BRAZIER = register("deepslate_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> NETHER_BRICK_BRAZIER = register("nether_brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BLACKSTONE_BRAZIER = register("blackstone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> MARBLE_BRAZIER = register("marble_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> SKULL_PILE = register("skull_pile", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).strength(2.0F).sound(SoundType.BONE_BLOCK)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CRYPT_URN = register("crypt_urn", UrnBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SOUL_LIGHT_BLOCK = register("soul_light", SoulLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> GLOW_LIGHT_BLOCK = register("glow_light", GlowLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> REINFORCED_REDSTONE_BLOCK = register("reinforced_redstone_block", () ->
            new PoweredBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.FIRE)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.METAL)
                    .isRedstoneConductor(ModBlocks::never)));

    public static final RegistryObject<Block> JADE_ORE = register("jade_ore", StoneOreBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> JADE_TILES = register("jade_tiles", JadeStoneBlock::new);
    public static final RegistryObject<Block> JADE_BLOCK = register("jade_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops().strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)));
    public static final RegistryObject<Block> JADE_PILLAR = register("jade_pillar",
            () -> new RotatedPillarBlock(JadeStoneProperties()));
    public static final RegistryObject<Block> JADE_STAIRS = registerStairs("jade_stairs", JADE_TILES);
    public static final RegistryObject<Block> JADE_SLAB = registerSlabs("jade_slab", JADE_TILES);

    //Haunted
    public static final RegistryObject<Block> HAUNTED_PLANKS = register("haunted_planks",
            () -> new Block(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_LOG = register("haunted_log", () -> log(MaterialColor.COLOR_GRAY, MaterialColor.COLOR_GRAY));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_LOG = register("stripped_haunted_log", () -> log(MaterialColor.COLOR_GRAY, MaterialColor.COLOR_GRAY));
    public static final RegistryObject<Block> HAUNTED_WOOD = register("haunted_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_WOOD = register("stripped_haunted_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_PRESSURE_PLATE = register("haunted_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_TRAPDOOR = register("haunted_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> HAUNTED_BUTTON = register("haunted_button",
            () -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_STAIRS = registerStairs("haunted_stairs",
            HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_SLAB = registerSlabs("haunted_slab",
            HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_FENCE_GATE = register("haunted_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_FENCE = register("haunted_fence",
            () -> new FenceBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_DOOR = register("haunted_door",
            () -> new DoorBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> HAUNTED_BOOKSHELF = register("haunted_bookshelf",
            () -> new BookshelfBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(1.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModChestBlock> HAUNTED_CHEST = isterRegister("haunted_chest", () -> new ModChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_HAUNTED_CHEST = isterRegister("trapped_haunted_chest", () -> new ModTrappedChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_SIGN = register("haunted_sign",
            () -> new ModStandSignBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(HAUNTED_SIGN.get()), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> sapling(new HauntedTree()));
    public static final RegistryObject<Block> POTTED_HAUNTED_SAPLING = register("potted_haunted_sapling", () ->
            new FlowerPotBlock(() -> (FlowerPotBlock) ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.FLOWER_POT).get(), ModBlocks.HAUNTED_SAPLING, Block.Properties.of(Material.PLANT).noOcclusion().instabreak()), false, LootTableType.DROP);
    public static final RegistryObject<Block> HAUNTED_LAMP = register("haunted_lamp",
            () -> new LampBlock(Block.Properties.copy(HAUNTED_WOOD.get())));
    public static final RegistryObject<Block> DARK_PRESSURE_PLATE = register("dark_pressure_plate",
            () -> new DarkPressurePlateBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> APPARITION_DOOR = register("apparition_door",
            () -> new ApparitionDoorBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    //Rotten
    public static final RegistryObject<Block> ROTTEN_PLANKS = register("rotten_planks",
            () -> new Block(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_LOG = register("rotten_log", () -> log(MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_LOG = register("stripped_rotten_log", () -> log(MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN));
    public static final RegistryObject<Block> ROTTEN_WOOD = register("rotten_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_WOOD = register("stripped_rotten_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_LEAVES = register("rotten_leaves", () -> leaves(SoundType.GRASS), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> ROTTEN_PRESSURE_PLATE = register("rotten_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_TRAPDOOR = register("rotten_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> ROTTEN_BUTTON = register("rotten_button",
            () -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_STAIRS = registerStairs("rotten_stairs",
            ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_SLAB = registerSlabs("rotten_slab",
            ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_FENCE_GATE = register("rotten_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_FENCE = register("rotten_fence",
            () -> new FenceBlock(Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_DOOR = register("rotten_door",
            () -> new DoorBlock(Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> ROTTEN_BOOKSHELF = register("rotten_bookshelf",
            () -> new BookshelfBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN).strength(1.5F).sound(SoundType.WOOD)), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> ROTTEN_CHEST = isterRegister("rotten_chest", () -> new ModChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_ROTTEN_CHEST = isterRegister("trapped_rotten_chest", () -> new ModTrappedChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_SIGN = register("rotten_sign",
            () -> new ModStandSignBlock(Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_WALL_SIGN = register("rotten_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of(Material.WOOD, ROTTEN_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(ROTTEN_SIGN.get()), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_SAPLING = register("rotten_sapling", () -> sapling(new RottenTree()));
    public static final RegistryObject<Block> POTTED_ROTTEN_SAPLING = register("potted_rotten_sapling", () ->
            new FlowerPotBlock(() -> (FlowerPotBlock) ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.FLOWER_POT).get(), ModBlocks.ROTTEN_SAPLING, Block.Properties.of(Material.PLANT).noOcclusion().instabreak()), false, LootTableType.EMPTY);

    //Windswept
    public static final RegistryObject<Block> WINDSWEPT_PLANKS = register("windswept_planks",
            () -> new Block(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> COMPACTED_WINDSWEPT_PLANKS = register("compacted_windswept_planks",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> COMPACTED_WINDSWEPT_SLAB = registerSlabs("compacted_windswept_slab",
            COMPACTED_WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> THATCHED_WINDSWEPT_PLANKS = register("thatched_windswept_planks",
            () -> new Block(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_LOG = register("windswept_log", () -> log(MaterialColor.COLOR_BROWN, MaterialColor.COLOR_BROWN));
    public static final RegistryObject<Block> STRIPPED_WINDSWEPT_LOG = register("stripped_windswept_log", () -> log(MaterialColor.COLOR_BROWN, MaterialColor.COLOR_BROWN));
    public static final RegistryObject<Block> WINDSWEPT_WOOD = register("windswept_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_WINDSWEPT_WOOD = register("stripped_windswept_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_LEAVES = register("windswept_leaves", () -> leaves(SoundType.GRASS), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> WINDSWEPT_PRESSURE_PLATE = register("windswept_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_TRAPDOOR = register("windswept_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> WINDSWEPT_BUTTON = register("windswept_button",
            () -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_STAIRS = registerStairs("windswept_stairs",
            WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_SLAB = registerSlabs("windswept_slab",
            WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_FENCE_GATE = register("windswept_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_FENCE = register("windswept_fence",
            () -> new FenceBlock(Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_DOOR = register("windswept_door",
            () -> new DoorBlock(Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> WINDSWEPT_BOOKSHELF = register("windswept_bookshelf",
            () -> new BookshelfBlock(Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(1.5F).sound(SoundType.WOOD)), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> WINDSWEPT_CHEST = isterRegister("windswept_chest", () -> new ModChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_WINDSWEPT_CHEST = isterRegister("trapped_windswept_chest", () -> new ModTrappedChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WINDSWEPT_SIGN = register("windswept_sign",
            () -> new ModStandSignBlock(Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_WALL_SIGN = register("windswept_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of(Material.WOOD, WINDSWEPT_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(WINDSWEPT_SIGN.get()), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_SAPLING = register("windswept_sapling", () -> sapling(new WindsweptTree()));
    public static final RegistryObject<Block> POTTED_WINDSWEPT_SAPLING = register("potted_windswept_sapling", () ->
            new FlowerPotBlock(() -> (FlowerPotBlock) ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.FLOWER_POT).get(), ModBlocks.WINDSWEPT_SAPLING, Block.Properties.of(Material.PLANT).noOcclusion().instabreak()), false, LootTableType.EMPTY);

    //Steep
    public static final RegistryObject<Block> STEEP_PLANKS = register("steep_planks",
            () -> new Block(Block.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_WOOD = register("steep_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_WOOD_SLAB = registerSlabs("steep_wood_slab",
            STEEP_WOOD);
    public static final RegistryObject<Block> STUDDED_STEEP_WOOD = register("studded_steep_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LINED_STEEP_WOOD = register("lined_steep_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RIMMED_STEEP_WOOD = register("rimmed_steep_wood",
            () -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_PRESSURE_PLATE = register("steep_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, STEEP_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_BUTTON = register("steep_button",
            () -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_STAIRS = registerStairs("steep_stairs",
            STEEP_PLANKS);
    public static final RegistryObject<Block> STEEP_SLAB = registerSlabs("steep_slab",
            STEEP_PLANKS);
    public static final RegistryObject<Block> STEEP_FENCE_GATE = register("steep_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of(Material.WOOD, STEEP_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_FENCE = register("steep_fence",
            () -> new FenceBlock(Block.Properties.of(Material.WOOD, STEEP_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STEEP_WALL_BLOCK = registerWalls("steep_wall", STEEP_WOOD);
    public static final RegistryObject<Block> STUDDED_STEEP_WALL_BLOCK = registerWalls("studded_steep_wall", STUDDED_STEEP_WOOD);
    public static final RegistryObject<Block> LINED_STEEP_WALL_BLOCK = registerWalls("lined_steep_wall", LINED_STEEP_WOOD);
    public static final RegistryObject<Block> RIMMED_STEEP_WALL_BLOCK = registerWalls("rimmed_steep_wall", RIMMED_STEEP_WOOD);
    public static final RegistryObject<Block> STEEP_LAMP = register("steep_lamp",
            () -> new LampBlock(Block.Properties.copy(STEEP_WOOD.get())));

    //Shade Stones
    public static final RegistryObject<Block> SHADE_STONE_BLOCK = register("shade_stone", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_BLOCK = register("shade_stone_polished", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_CHISELED_BLOCK = register("shade_stone_chiseled", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_BLOCK = register("shade_stone_bricks", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_BRICK_BLOCK = register("shade_bricks", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_TILES_BLOCK = register("shade_tiles", ShadeStoneBlock::new);

    //Crypt
    public static final RegistryObject<Block> CRYPT_STONE_BLOCK = register("crypt_stone", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_BLOCK = register("crypt_stone_polished", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_STONE_CHISELED_BLOCK = register("crypt_stone_chiseled", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_BRICKS_BLOCK = register("crypt_bricks", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_TILES_BLOCK = register("crypt_tiles", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_PLINTH_BLOCK = register("crypt_plinth", CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_PILLAR_BLOCK = register("crypt_pillar", () -> pillar(CryptStoneProperties()));
    public static final RegistryObject<Block> CRYPT_BOOKSHELF = register("crypt_bookshelf",
            () -> new BookshelfBlock(CryptStoneProperties(), 2.0F), true, LootTableType.EMPTY);

    //Marble
    public static final RegistryObject<Block> MARBLE_BLOCK = register("marble", MarbleBlock::new);
    public static final RegistryObject<Block> CRACKED_MARBLE_BLOCK = register("cracked_marble", MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_ARCH_MARBLE_BLOCK = register("gold_arch_marble", MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_BANDED_MARBLE_BLOCK = register("gold_banded_marble", MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_HOLDER_MARBLE_BLOCK = register("gold_holder_marble", MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_COVERED_MARBLE_BLOCK = register("gold_covered_marble", MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_PLATED_MARBLE_BLOCK = register("gold_plated_marble", MarbleBlock::new);

    public static final RegistryObject<Block> SLATE_MARBLE_BLOCK = register("slate_marble", SlateMarbleBlock::new);
    public static final RegistryObject<Block> WORN_SLATE_MARBLE_BLOCK = register("worn_slate_marble", SlateMarbleBlock::new);
    public static final RegistryObject<Block> WEATHERED_SLATE_MARBLE_BLOCK = register("weathered_slate_marble", SlateMarbleBlock::new);
    public static final RegistryObject<Block> WASHED_SLATE_MARBLE_BLOCK = register("washed_slate_marble",
            () -> new FacingBlock(BlockBehaviour.Properties.of(Material.STONE,(state) -> {
                return state.getValue(FacingBlock.FACING) == Direction.DOWN ? MaterialColor.TERRACOTTA_CYAN : MaterialColor.TERRACOTTA_WHITE;
            }).requiresCorrectToolForDrops()
            .strength(3.0F, 6.0F)
            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> SLATE_CONNECTED_MARBLE_BLOCK = register("slate_connected_marble", MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_PATTERNED_MARBLE_BLOCK = register("slate_patterned_marble", MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_CORNERED_MARBLE_BLOCK = register("slate_cornered_marble", MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_GLAZED_MARBLE_BLOCK = register("slate_glazed_marble", () -> new GlazedTerracottaBlock(MarbleProperties()));

    //Indented Gold
    public static final RegistryObject<Block> INDENTED_GOLD_BLOCK = register("indented_gold", IndentedGoldBlock::new);
    public static final RegistryObject<Block> CHISELED_INDENTED_GOLD_BLOCK = register("chiseled_indented_gold", IndentedGoldBlock::new);
    public static final RegistryObject<Block> CARVED_INDENTED_GOLD_BLOCK = register("carved_indented_gold", IndentedGoldBlock::new);

    //Highrock
    public static final RegistryObject<Block> HIGHROCK_BLOCK = register("highrock", HighrockBlock::new);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_BLOCK = register("polished_highrock", HighrockBlock::new);
    public static final RegistryObject<Block> HIGHROCK_BRICKS_BLOCK = register("highrock_bricks", HighrockBlock::new);
    public static final RegistryObject<Block> GOLD_INDENTED_HIGHROCK_BRICKS_BLOCK = register("gold_indented_highrock_bricks", HighrockBlock::new);
    public static final RegistryObject<Block> GOLD_CHISELED_HIGHROCK_BRICKS_BLOCK = register("gold_chiseled_highrock_bricks", HighrockBlock::new);

    //Snow Bricks
    public static final RegistryObject<Block> SNOW_BRICKS_BLOCK = register("snow_bricks", SnowBrickBlock::new);

    //Slabs
    public static final RegistryObject<Block> SHADE_STONE_SLAB_BLOCK = registerShadeSlabs("shade_stone_slab");
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_SLAB_BLOCK = registerShadeSlabs("shade_stone_polished_slab");
    public static final RegistryObject<Block> SHADE_STONE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_stone_bricks_slab");
    public static final RegistryObject<Block> SHADE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_bricks_slab");
    public static final RegistryObject<Block> SHADE_TILES_SLAB_BLOCK = registerShadeSlabs("shade_tiles_slab");

    public static final RegistryObject<Block> CRYPT_STONE_SLAB_BLOCK = registerCryptSlabs("crypt_stone_slab");
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_SLAB_BLOCK = registerCryptSlabs("crypt_stone_polished_slab");
    public static final RegistryObject<Block> CRYPT_BRICKS_SLAB_BLOCK = registerCryptSlabs("crypt_bricks_slab");
    public static final RegistryObject<Block> CRYPT_TILES_SLAB_BLOCK = registerCryptSlabs("crypt_tiles_slab");

    public static final RegistryObject<Block> MARBLE_SLAB = registerSlabs("marble_slab",
            MARBLE_BLOCK);
    public static final RegistryObject<Block> CRACKED_MARBLE_SLAB = registerSlabs("cracked_marble_slab",
            CRACKED_MARBLE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_MARBLE_SLAB = registerSlabs("smooth_marble_slab",
            MARBLE_BLOCK);
    public static final RegistryObject<Block> SLATE_MARBLE_SLAB = registerSlabs("slate_marble_slab",
            SLATE_MARBLE_BLOCK);

    public static final RegistryObject<Block> HIGHROCK_SLAB = registerSlabs("highrock_slab",
            HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_SLAB = registerSlabs("polished_highrock_slab",
            POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_SLAB = registerSlabs("highrock_brick_slab",
            HIGHROCK_BRICKS_BLOCK);

    public static final RegistryObject<Block> INDENTED_GOLD_SLAB = registerSlabs("indented_gold_slab",
            INDENTED_GOLD_BLOCK);

    public static final RegistryObject<Block> SNOW_BRICK_SLAB = registerSlabs("snow_brick_slab",
            SNOW_BRICKS_BLOCK);

    //Stairs
    public static final RegistryObject<Block> SHADE_STONE_STAIRS_BLOCK = registerStairs("shade_stone_stairs", SHADE_STONE_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_STAIRS_BLOCK = registerStairs("shade_stone_polished_stairs", SHADE_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_STAIRS_BLOCK = registerStairs("shade_stone_bricks_stairs", SHADE_STONE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_BRICK_STAIRS_BLOCK = registerStairs("shade_bricks_stairs", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_TILES_STAIRS_BLOCK = registerStairs("shade_tiles_stairs", SHADE_TILES_BLOCK);

    public static final RegistryObject<Block> CRYPT_STONE_STAIRS_BLOCK = registerStairs("crypt_stone_stairs", CRYPT_STONE_BLOCK);
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_STAIRS_BLOCK = registerStairs("crypt_stone_polished_stairs", CRYPT_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> CRYPT_BRICKS_STAIRS_BLOCK = registerStairs("crypt_bricks_stairs", CRYPT_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRYPT_TILES_STAIRS_BLOCK = registerStairs("crypt_tiles_stairs", CRYPT_TILES_BLOCK);

    public static final RegistryObject<Block> MARBLE_STAIRS_BLOCK = registerStairs("marble_stairs", MARBLE_BLOCK);
    public static final RegistryObject<Block> SLATE_MARBLE_STAIRS_BLOCK = registerStairs("slate_marble_stairs", SLATE_MARBLE_BLOCK);

    public static final RegistryObject<Block> HIGHROCK_STAIRS = registerStairs("highrock_stairs", HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_STAIRS = registerStairs("polished_highrock_stairs", POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_STAIRS = registerStairs("highrock_brick_stairs", HIGHROCK_BRICKS_BLOCK);

    public static final RegistryObject<Block> INDENTED_GOLD_STAIRS_BLOCK = registerStairs("indented_gold_stairs", INDENTED_GOLD_BLOCK);

    public static final RegistryObject<Block> SNOW_BRICK_STAIRS_BLOCK = registerStairs("snow_brick_stairs", SNOW_BRICKS_BLOCK);

    //Walls
    public static final RegistryObject<Block> SHADE_BRICK_WALL_BLOCK = registerWalls("shade_bricks_wall", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_WALL_BLOCK = registerWalls("shade_stone_bricks_wall", SHADE_STONE_BRICK_BLOCK);

    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_WALL_BLOCK = registerWalls("crypt_stone_polished_wall", CRYPT_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> CRYPT_BRICKS_WALL_BLOCK = registerWalls("crypt_bricks_wall", CRYPT_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRYPT_TILES_WALL_BLOCK = registerWalls("crypt_tiles_wall", CRYPT_TILES_BLOCK);

    public static final RegistryObject<Block> HIGHROCK_WALL_BLOCK = registerWalls("highrock_wall", HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_WALL_BLOCK = registerWalls("polished_highrock_wall", POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_WALL_BLOCK = registerWalls("highrock_brick_wall", HIGHROCK_BRICKS_BLOCK);

    public static final RegistryObject<Block> INDENTED_GOLD_WALL_BLOCK = registerWalls("indented_gold_wall", INDENTED_GOLD_BLOCK);

    public static final RegistryObject<Block> SNOW_BRICK_WALL_BLOCK = registerWalls("snow_brick_wall", SNOW_BRICKS_BLOCK);

    public static final RegistryObject<Block> CURSED_BARS_BLOCK = register("cursed_bars",
            () -> new IronBarsBlock(Block.Properties.of(Material.METAL, MaterialColor.NONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    //Custom Items
    public static final RegistryObject<Item> RESONANCE_CRYSTAL_ITEM = ModItems.ITEMS.register("resonance_crystal",
            ResonanceBlockItem::new);
    public static final RegistryObject<Item> NIGHT_BEACON_ITEM = ModItems.ITEMS.register("night_beacon",
            () -> new BlockItem(ModBlocks.NIGHT_BEACON.get(), (new Item.Properties().tab(Goety.TAB).fireResistant())));
    public static final RegistryObject<Item> HAUNTED_SIGN_ITEM = ModItems.ITEMS.register("haunted_sign",
            () -> new SignItem(new Item.Properties().tab(Goety.TAB).stacksTo(16), HAUNTED_SIGN.get(), HAUNTED_WALL_SIGN.get()));
    public static final RegistryObject<Item> ROTTEN_SIGN_ITEM = ModItems.ITEMS.register("rotten_sign",
            () -> new SignItem(new Item.Properties().tab(Goety.TAB).stacksTo(16), ROTTEN_SIGN.get(), ROTTEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> WINDSWEPT_SIGN_ITEM = ModItems.ITEMS.register("windswept_sign",
            () -> new SignItem(new Item.Properties().tab(Goety.TAB).stacksTo(16), WINDSWEPT_SIGN.get(), WINDSWEPT_WALL_SIGN.get()));
    public static final RegistryObject<Item> TALL_SKULL_ITEM = ModItems.ITEMS.register("tall_skull",
            () -> new TallSkullItem(ModBlocks.TALL_SKULL_BLOCK.get(), ModBlocks.WALL_TALL_SKULL_BLOCK.get(), (new Item.Properties()).tab(Goety.TAB).rarity(Rarity.UNCOMMON)){
                @Override
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(new IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            return new ModISTER();
                        }
                    });
                }
            });
    public static final RegistryObject<Item> REDSTONE_GOLEM_SKULL_ITEM = ModItems.ITEMS.register("redstone_golem_skull",
            () -> new RedstoneGolemSkullItem((new Item.Properties()).tab(Goety.TAB).rarity(Rarity.UNCOMMON).fireResistant()){
                @Override
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(new IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            return new ModISTER();
                        }
                    });
                }
            });
    public static final RegistryObject<Item> SNAP_WARTS_ITEM = ModItems.ITEMS.register("snap_warts",
            () -> new ItemNameBlockItem(ModBlocks.SNAP_WARTS.get(), (new Item.Properties()).tab(Goety.TAB)));

    private static SaplingBlock sapling(AbstractTreeGrower tree){
        return new SaplingBlock(tree, Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }

    private static RotatedPillarBlock pillar(BlockBehaviour.Properties properties) {
        return new RotatedPillarBlock(properties);
    }

    private static RotatedPillarBlock log(MaterialColor pTopColor, MaterialColor pBarkColor) {
        return new RotatedPillarBlock(Block.Properties.of(Material.WOOD, (p_235431_2_) -> {
            return p_235431_2_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? pTopColor : pBarkColor;
        }).strength(2.0F).sound(SoundType.WOOD));
    }

    public static <T extends Block> RegistryObject<Block> registerShadeSlabs(final String string){
        return register(string, () -> new SlabBlock(ShadeStoneProperties()), true);
    }

    public static <T extends Block> RegistryObject<Block> registerCryptSlabs(final String string){
        return register(string, () -> new SlabBlock(CryptStoneProperties()), true);
    }

    public static <T extends Block> RegistryObject<Block> registerSlabs(final String string, final RegistryObject<T> block){
        return register(string, () -> new SlabBlock(Block.Properties.copy(block.get())), true);
    }

    public static <T extends Block> RegistryObject<Block> registerStairs(final String name, final RegistryObject<T> block){
        return register(name, () -> new StairBlock(() -> block.get().defaultBlockState(), Block.Properties.copy(block.get())));
    }

    public static <T extends Block> RegistryObject<Block> registerWalls(final String name, final RegistryObject<T> block){
        return register(name, () -> new WallBlock(Block.Properties.copy(block.get())));
    }

    public static <T extends Block> RegistryObject<T> register(final String string, final Supplier<? extends T> sup){
        return register(string, sup, true);
    }

    public static <T extends Block> RegistryObject<T> register(final String string, final Supplier<? extends T> sup, boolean blockItemDefault){
        return register(string, sup, blockItemDefault, LootTableType.DROP);
    }

    public static <T extends Block> RegistryObject<T> register(final String string, final Supplier<? extends T> sup, boolean blockItemDefault, LootTableType lootTableType) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new BlockLootSetting(blockItemDefault, lootTableType));
        if (blockItemDefault) {
            ModItems.ITEMS.register(string,
                    () -> new BlockItemBase(block.get()));
        }
        return block;
    }

    public static <T extends Block> RegistryObject<T> isterRegister(final String string, final Supplier<? extends T> sup){
        return isterRegister(string, sup, LootTableType.DROP);
    }

    public static <T extends Block> RegistryObject<T> isterRegister(final String string, final Supplier<? extends T> sup, LootTableType lootTableType) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new BlockLootSetting(false, lootTableType));
        ModItems.ITEMS.register(string,
                () -> new BlockISTERItem(block.get()));
        return block;
    }

    public static <T extends Block> RegistryObject<T> enchantedRegister(final String string, final Supplier<? extends T> sup) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new BlockLootSetting(false, LootTableType.EMPTY));
        ModItems.ITEMS.register(string,
                () -> new EnchantableBlockItem(block.get()));
        return block;
    }

    private static LeavesBlock leaves(SoundType p_152615_) {
        return new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(p_152615_).noOcclusion().isValidSpawn(ModBlocks::ocelotOrParrot).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never));
    }

    private static boolean ocelotOrParrot(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }

    public static boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos) {
        return false;
    }

    public static Boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> p_235427_3_) {
        return false;
    }

    public static BlockBehaviour.Properties ShadeStoneProperties(){
        return BlockBehaviour.Properties.of(Material.STONE)
                .strength(5.0F, 100.0F)
                .sound(SoundType.STONE);
    }

    public static BlockBehaviour.Properties CryptStoneProperties(){
        return BlockBehaviour.Properties.of(Material.STONE)
                .strength(50.0F, 1200.0F)
                .sound(SoundType.STONE);
    }

    public static class ShadeStoneBlock extends Block {

        public ShadeStoneBlock() {
            super(ShadeStoneProperties());
        }

    }

    public static class CryptStoneBlock extends Block {

        public CryptStoneBlock() {
            super(CryptStoneProperties());
        }

    }

    public static class CursedMetalBlock extends Block {

        public CursedMetalBlock() {
            super(Properties.of(Material.STONE)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            );
        }
    }

    public static class DarkMetalBlock extends Block {

        public DarkMetalBlock() {
            super(Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            );
        }
    }

    public static BlockBehaviour.Properties JadeStoneProperties(){
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE);
    }

    public static class JadeStoneBlock extends Block {

        public JadeStoneBlock() {
            super(JadeStoneProperties());
        }

    }

    public static BlockBehaviour.Properties MarbleProperties(){
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 6.0F)
                .sound(SoundType.STONE);
    }

    public static class MarbleBlock extends Block {

        public MarbleBlock() {
            super(MarbleProperties());
        }

    }

    public static BlockBehaviour.Properties SlateMarbleProperties(){
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_CYAN)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 6.0F)
                .sound(SoundType.STONE);
    }

    public static class SlateMarbleBlock extends Block {

        public SlateMarbleBlock() {
            super(SlateMarbleProperties());
        }

    }

    public static BlockBehaviour.Properties HighrockProperties(){
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    public static class HighrockBlock extends Block {

        public HighrockBlock() {
            super(HighrockProperties());
        }

    }

    public static BlockBehaviour.Properties IndentedGoldProperties(){
        return BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 6.0F)
                .sound(SoundType.METAL);
    }

    public static class IndentedGoldBlock extends Block {

        public IndentedGoldBlock() {
            super(IndentedGoldProperties());
        }

    }

    public static BlockBehaviour.Properties SnowBrickProperties(){
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    public static class SnowBrickBlock extends Block {

        public SnowBrickBlock() {
            super(SnowBrickProperties());
        }

    }

    public static class StoneOreBlock extends DropExperienceBlock{
        public StoneOreBlock(){
            super(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F),
                    UniformInt.of(3, 7));
        }
    }

    public static BlockBehaviour.Properties glassProperties(){
        return BlockBehaviour.Properties.of(Material.GLASS)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(ModBlocks::never)
                .isRedstoneConductor(ModBlocks::never)
                .isSuffocating(ModBlocks::never)
                .isViewBlocking(ModBlocks::never);
    }

    public static BlockBehaviour.Properties tintedGlassProperties(){
        return BlockBehaviour.Properties.copy(Blocks.GLASS)
                .color(MaterialColor.COLOR_GRAY)
                .noOcclusion()
                .isValidSpawn(ModBlocks::never)
                .isRedstoneConductor(ModBlocks::never)
                .isSuffocating(ModBlocks::never)
                .isViewBlocking(ModBlocks::never);
    }

    public static class FacingBlock extends DirectionalBlock{
        public FacingBlock(Properties p_52591_) {
            super(p_52591_);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
        }

        public BlockState getStateForPlacement(BlockPlaceContext p_56198_) {
            return this.defaultBlockState().setValue(FACING, p_56198_.getClickedFace());
        }

        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56249_) {
            p_56249_.add(FACING);
        }

        public BlockState rotate(BlockState p_56243_, Rotation p_56244_) {
            return p_56243_.setValue(FACING, p_56244_.rotate(p_56243_.getValue(FACING)));
        }

        public BlockState mirror(BlockState p_56240_, Mirror p_56241_) {
            return p_56240_.rotate(p_56241_.getRotation(p_56240_.getValue(FACING)));
        }
    }

    /**
     * Based on @klikli-dev's Block Loot Generator
     */
    public enum LootTableType {
        EMPTY,
        DROP
    }

    public static class BlockLootSetting {
        public boolean generateDefaultBlockItem;
        public LootTableType lootTableType;

        public BlockLootSetting(boolean generateDefaultBlockItem,
                                LootTableType lootTableType) {
            this.generateDefaultBlockItem = generateDefaultBlockItem;
            this.lootTableType = lootTableType;
        }
    }
}
