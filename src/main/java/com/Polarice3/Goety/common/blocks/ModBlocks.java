package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.block.ModISTER;
import com.Polarice3.Goety.common.items.*;
import com.Polarice3.Goety.common.world.features.trees.HauntedTree;
import com.Polarice3.Goety.common.world.features.trees.RottenTree;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
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
    public static final RegistryObject<Block> WIND_BLOWER = register("wind_blower", WindBlowerBlock::new);
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
            -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(2.0F).randomTicks().sound(SoundType.GRASS)
                    .noOcclusion().isValidSpawn(ModBlocks::ocelotOrParrot).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never)),
            true, LootTableType.EMPTY);
    public static final RegistryObject<Block> PITHOS = register("pithos", PithosBlock::new);
    public static final RegistryObject<Block> NIGHT_BEACON = register("night_beacon", NightBeaconBlock::new);
    public static final RegistryObject<Block> TALL_SKULL_BLOCK = register("tall_skull", TallSkullBlock::new, false);
    public static final RegistryObject<Block> WALL_TALL_SKULL_BLOCK = register("wall_tall_skull", WallTallSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> REDSTONE_GOLEM_SKULL_BLOCK = register("redstone_golem_skull", RedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_REDSTONE_GOLEM_SKULL_BLOCK = register("wall_redstone_golem_skull", WallRedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);

    //Plants
    public static final RegistryObject<Block> SNAP_WARTS = register("snap_warts", SnapWartsBlock::new, false, LootTableType.EMPTY);

    //Deco
    public static final RegistryObject<Block> CURSED_METAL_BLOCK = register("cursed_metal_block", CursedMetalBlock::new);
    public static final RegistryObject<Block> DARK_METAL_BLOCK = register("dark_metal_block", DarkMetalBlock::new);
    public static final RegistryObject<Block> SHADE_BRAZIER = register("shade_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> STONE_BRAZIER = register("stone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BRICK_BRAZIER = register("brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> DEEPSLATE_BRAZIER = register("deepslate_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> NETHER_BRICK_BRAZIER = register("nether_brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BLACKSTONE_BRAZIER = register("blackstone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> SKULL_PILE = register("skull_pile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(2.0F).sound(SoundType.BONE_BLOCK).instrument(NoteBlockInstrument.BASEDRUM)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CRYPT_URN = register("crypt_urn", UrnBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SOUL_LIGHT_BLOCK = register("soul_light", SoulLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> GLOW_LIGHT_BLOCK = register("glow_light", GlowLightBlock::new, false, LootTableType.EMPTY);

    public static final RegistryObject<Block> JADE_ORE = register("jade_ore", StoneOreBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> JADE_TILES = register("jade_tiles", JadeStoneBlock::new);
    public static final RegistryObject<Block> JADE_BLOCK = register("jade_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops().strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)));
    public static final RegistryObject<Block> JADE_PILLAR = register("jade_pillar",
            () -> new RotatedPillarBlock(JadeStoneProperties()));
    public static final RegistryObject<Block> JADE_STAIRS = registerStairs("jade_stairs", JADE_TILES);
    public static final RegistryObject<Block> JADE_SLAB = registerSlabs("jade_slab", JADE_TILES);

    //Haunted
    public static final RegistryObject<Block> HAUNTED_PLANKS = register("haunted_planks",
            () -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_LOG = register("haunted_log", () -> fireProofLog(MapColor.COLOR_GRAY));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_LOG = register("stripped_haunted_log", () -> fireProofLog(MapColor.COLOR_GRAY));
    public static final RegistryObject<Block> HAUNTED_WOOD = register("haunted_wood",
            () -> new RotatedPillarBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_WOOD = register("stripped_haunted_wood",
            () -> new RotatedPillarBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_PRESSURE_PLATE = register("haunted_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_TRAPDOOR = register("haunted_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_BUTTON = register("haunted_button",
            () -> woodenButton(ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_STAIRS = registerStairs("haunted_stairs",
            HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_SLAB = registerSlabs("haunted_slab",
            HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_FENCE_GATE = register("haunted_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD), ModWoodType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_FENCE = register("haunted_fence",
            () -> new FenceBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_DOOR = register("haunted_door",
            () -> new DoorBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_BOOKSHELF = register("haunted_bookshelf",
            () -> new BookshelfBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(1.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModChestBlock> HAUNTED_CHEST = isterRegister("haunted_chest", () -> new ModChestBlock(Block.Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_HAUNTED_CHEST = isterRegister("trapped_haunted_chest", () -> new ModTrappedChestBlock(Block.Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> HAUNTED_SIGN = register("haunted_sign",
            () -> new ModStandSignBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of().mapColor(HAUNTED_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(HAUNTED_SIGN), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_HANGING_SIGN = register("haunted_hanging_sign",
            () -> new ModHangingSignBlock(BlockBehaviour.Properties.of().mapColor(HAUNTED_LOG.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_HANGING_SIGN = register("haunted_wall_hanging_sign",
            () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.of().mapColor(HAUNTED_LOG.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).lootFrom(HAUNTED_HANGING_SIGN), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> sapling(new HauntedTree()));
    public static final RegistryObject<Block> POTTED_HAUNTED_SAPLING = register("potted_haunted_sapling", () ->
            new FlowerPotBlock(() -> (FlowerPotBlock) ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.FLOWER_POT).get(), ModBlocks.HAUNTED_SAPLING, Block.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().instabreak()), false, LootTableType.DROP);

    //Rotten
    public static final RegistryObject<Block> ROTTEN_PLANKS = register("rotten_planks",
            () -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_LOG = register("rotten_log", () -> fireProofLog(MapColor.COLOR_GREEN));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_LOG = register("stripped_rotten_log", () -> fireProofLog(MapColor.COLOR_GREEN));
    public static final RegistryObject<Block> ROTTEN_WOOD = register("rotten_wood",
            () -> new RotatedPillarBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_WOOD = register("stripped_rotten_wood",
            () -> new RotatedPillarBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_LEAVES = register("rotten_leaves", () -> leaves(SoundType.GRASS), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> ROTTEN_PRESSURE_PLATE = register("rotten_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_TRAPDOOR = register("rotten_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_BUTTON = register("rotten_button",
            () -> woodenButton(ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_STAIRS = registerStairs("rotten_stairs",
            ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_SLAB = registerSlabs("rotten_slab",
            ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_FENCE_GATE = register("rotten_fence_gate",
            () -> new FenceGateBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD), ModWoodType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_FENCE = register("rotten_fence",
            () -> new FenceBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_DOOR = register("rotten_door",
            () -> new DoorBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_BOOKSHELF = register("rotten_bookshelf",
            () -> new BookshelfBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(1.5F).sound(SoundType.WOOD)), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> ROTTEN_CHEST = isterRegister("rotten_chest", () -> new ModChestBlock(Block.Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_ROTTEN_CHEST = isterRegister("trapped_rotten_chest", () -> new ModTrappedChestBlock(Block.Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ROTTEN_SIGN = register("rotten_sign",
            () -> new ModStandSignBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_WALL_SIGN = register("rotten_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of().mapColor(ROTTEN_PLANKS.get().defaultMapColor()).instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(ROTTEN_SIGN), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_HANGING_SIGN = register("rotten_hanging_sign",
            () -> new ModHangingSignBlock(BlockBehaviour.Properties.of().mapColor(ROTTEN_LOG.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_WALL_HANGING_SIGN = register("rotten_wall_hanging_sign",
            () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.of().mapColor(ROTTEN_LOG.get().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).lootFrom(ROTTEN_HANGING_SIGN), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_SAPLING = register("rotten_sapling", () -> sapling(new RottenTree()));
    public static final RegistryObject<Block> POTTED_ROTTEN_SAPLING = register("potted_rotten_sapling", () ->
            new FlowerPotBlock(() -> (FlowerPotBlock) ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.FLOWER_POT).get(), ModBlocks.ROTTEN_SAPLING, Block.Properties.of().pushReaction(PushReaction.DESTROY).noOcclusion().instabreak()), false, LootTableType.EMPTY);

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

    //Walls
    public static final RegistryObject<Block> SHADE_BRICK_WALL_BLOCK = registerWalls("shade_bricks_wall", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_WALL_BLOCK = registerWalls("shade_stone_bricks_wall", SHADE_STONE_BRICK_BLOCK);

    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_WALL_BLOCK = registerWalls("crypt_stone_polished_wall", CRYPT_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> CRYPT_BRICKS_WALL_BLOCK = registerWalls("crypt_bricks_wall", CRYPT_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRYPT_TILES_WALL_BLOCK = registerWalls("crypt_tiles_wall", CRYPT_TILES_BLOCK);

    public static final RegistryObject<Block> CURSED_BARS_BLOCK = register("cursed_bars",
            () -> new IronBarsBlock(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    //Custom Items
    public static final RegistryObject<Item> HAUNTED_SIGN_ITEM = ModItems.ITEMS.register("haunted_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), HAUNTED_SIGN.get(), HAUNTED_WALL_SIGN.get()));
    public static final RegistryObject<Item> HAUNTED_HANGING_SIGN_ITEM = ModItems.ITEMS.register("haunted_hanging_sign",
            () -> new HangingSignItem(HAUNTED_HANGING_SIGN.get(), HAUNTED_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ROTTEN_SIGN_ITEM = ModItems.ITEMS.register("rotten_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ROTTEN_SIGN.get(), ROTTEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> ROTTEN_HANGING_SIGN_ITEM = ModItems.ITEMS.register("rotten_hanging_sign",
            () -> new HangingSignItem(ROTTEN_HANGING_SIGN.get(), ROTTEN_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> TALL_SKULL_ITEM = ModItems.ITEMS.register("tall_skull",
            () -> new TallSkullItem(ModBlocks.TALL_SKULL_BLOCK.get(), ModBlocks.WALL_TALL_SKULL_BLOCK.get(), (new Item.Properties()).rarity(Rarity.UNCOMMON)){
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
            () -> new RedstoneGolemSkullItem((new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()){
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
            () -> new ItemNameBlockItem(ModBlocks.SNAP_WARTS.get(), (new Item.Properties())));

    private static SaplingBlock sapling(AbstractTreeGrower tree){
        return new SaplingBlock(tree, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    }

    private static RotatedPillarBlock pillar(BlockBehaviour.Properties properties) {
        return new RotatedPillarBlock(properties);
    }

    private static Block fireProofLog(MapColor p_285125_) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((p_152620_) -> {
            return p_285125_;
        }).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM));
    }

    private static RotatedPillarBlock log(MapColor p_285370_, MapColor p_285126_) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((p_152624_) -> {
            return p_152624_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_285370_ : p_285126_;
        }).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
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
        return new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(p_152615_).noOcclusion().isValidSpawn(ModBlocks::ocelotOrParrot).isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(ModBlocks::never));
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

    private static ButtonBlock woodenButton(BlockSetType p_278239_, FeatureFlag... p_278229_) {
        BlockBehaviour.Properties blockbehaviour$properties = BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
        if (p_278229_.length > 0) {
            blockbehaviour$properties = blockbehaviour$properties.requiredFeatures(p_278229_);
        }

        return new ButtonBlock(blockbehaviour$properties, p_278239_, 30, true);
    }

    public static BlockBehaviour.Properties ShadeStoneProperties(){
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 100.0F)
                .sound(SoundType.STONE);
    }

    public static BlockBehaviour.Properties CryptStoneProperties(){
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
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
            super(Properties.of()
                    .mapColor(MapColor.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            );
        }
    }

    public static class DarkMetalBlock extends Block {

        public DarkMetalBlock() {
            super(Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            );
        }
    }

    public static BlockBehaviour.Properties JadeStoneProperties(){
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE);
    }

    public static class JadeStoneBlock extends Block {

        public JadeStoneBlock() {
            super(JadeStoneProperties());
        }

    }

    public static class StoneOreBlock extends DropExperienceBlock{
        public StoneOreBlock(){
            super(Properties.of()
                    .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F),
                    UniformInt.of(3, 7));
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
