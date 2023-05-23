package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.block.ModISTER;
import com.Polarice3.Goety.common.items.EnchantableBlockItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.TallSkullItem;
import com.Polarice3.Goety.common.world.features.trees.HauntedTree;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
    public static final RegistryObject<Block> SCULK_DEVOURER = enchantedRegister("sculk_devourer", SculkDevourerBlock::new);
    public static final RegistryObject<Block> SCULK_CONVERTER = enchantedRegister("sculk_converter", SculkConverterBlock::new);
    public static final RegistryObject<Block> SCULK_RELAY = register("sculk_relay", SculkRelayBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SCULK_GROWER = enchantedRegister("sculk_grower", SculkGrowerBlock::new);
    public static final RegistryObject<Block> SPIDER_NEST = register("spider_nest", SpiderNestBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> FORBIDDEN_GRASS = register("forbidden_grass", ForbiddenGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> TALL_SKULL_BLOCK = register("tall_skull", TallSkullBlock::new, false);
    public static final RegistryObject<Block> WALL_TALL_SKULL_BLOCK = register("wall_tall_skull", WallTallSkullBlock::new, false, LootTableType.EMPTY);

    //Plants
    public static final RegistryObject<Block> SNAP_WARTS = register("snap_warts", SnapWartsBlock::new, false, LootTableType.EMPTY);

    //Deco
    public static final RegistryObject<Block> CURSED_METAL_BLOCK = register("cursed_metal_block", CursedMetalBlock::new);
    public static final RegistryObject<Block> SOUL_LIGHT_BLOCK = register("soul_light", SoulLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> GLOW_LIGHT_BLOCK = register("glow_light", GlowLightBlock::new, false, LootTableType.EMPTY);

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
    public static final RegistryObject<Block> HAUNTED_SIGN = register("haunted_sign",
            () -> new ModStandSignBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign",
            () -> new ModWallSignBlock(Block.Properties.of(Material.WOOD, HAUNTED_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(HAUNTED_SIGN.get()), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> sapling(new HauntedTree()));

    //Shade Stones
    public static final RegistryObject<Block> SHADE_STONE_BLOCK = register("shade_stone", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_BLOCK = register("shade_stone_polished", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_CHISELED_BLOCK = register("shade_stone_chiseled", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_BLOCK = register("shade_stone_bricks", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_BRICK_BLOCK = register("shade_bricks", ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_TILES_BLOCK = register("shade_tiles", ShadeStoneBlock::new);

    //Slabs
    public static final RegistryObject<Block> SHADE_STONE_SLAB_BLOCK = registerShadeSlabs("shade_stone_slab");
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_SLAB_BLOCK = registerShadeSlabs("shade_stone_polished_slab");
    public static final RegistryObject<Block> SHADE_STONE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_stone_bricks_slab");
    public static final RegistryObject<Block> SHADE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_bricks_slab");
    public static final RegistryObject<Block> SHADE_TILES_SLAB_BLOCK = registerShadeSlabs("shade_tiles_slab");

    //Stairs
    public static final RegistryObject<Block> SHADE_STONE_STAIRS_BLOCK = registerStairs("shade_stone_stairs", SHADE_STONE_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_STAIRS_BLOCK = registerStairs("shade_stone_polished_stairs", SHADE_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_STAIRS_BLOCK = registerStairs("shade_stone_bricks_stairs", SHADE_STONE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_BRICK_STAIRS_BLOCK = registerStairs("shade_bricks_stairs", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_TILES_STAIRS_BLOCK = registerStairs("shade_tiles_stairs", SHADE_TILES_BLOCK);

    //Walls
    public static final RegistryObject<Block> SHADE_BRICK_WALL_BLOCK = registerWalls("shade_bricks_wall", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_WALL_BLOCK = registerWalls("shade_stone_bricks_wall", SHADE_STONE_BRICK_BLOCK);

    public static final RegistryObject<Block> CURSED_BARS_BLOCK = register("cursed_bars",
            () -> new IronBarsBlock(Block.Properties.of(Material.METAL, MaterialColor.NONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    //Custom Items
    public static final RegistryObject<Item> HAUNTED_SIGN_ITEM = ModItems.ITEMS.register("haunted_sign",
            () -> new SignItem(new Item.Properties().tab(Goety.TAB).stacksTo(16), HAUNTED_SIGN.get(), HAUNTED_WALL_SIGN.get()));
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
    public static final RegistryObject<Item> SNAP_WARTS_ITEM = ModItems.ITEMS.register("snap_warts",
            () -> new ItemNameBlockItem(ModBlocks.SNAP_WARTS.get(), (new Item.Properties()).tab(Goety.TAB)));

    private static SaplingBlock sapling(AbstractTreeGrower tree){
        return new SaplingBlock(tree, Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }

    private static RotatedPillarBlock log(MaterialColor pTopColor, MaterialColor pBarkColor) {
        return new RotatedPillarBlock(Block.Properties.of(Material.WOOD, (p_235431_2_) -> {
            return p_235431_2_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? pTopColor : pBarkColor;
        }).strength(2.0F).sound(SoundType.WOOD));
    }

    public static <T extends Block> RegistryObject<Block> registerShadeSlabs(final String string){
        return register(string, () -> new SlabBlock(ShadeStoneProperties()), true);
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

    public static <T extends Block> RegistryObject<T> enchantedRegister(final String string, final Supplier<? extends T> sup) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new BlockLootSetting(false, LootTableType.EMPTY));
        ModItems.ITEMS.register(string,
                () -> new EnchantableBlockItem(block.get()));
        return block;
    }

    private static boolean ocelotOrParrot(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }

    private static boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos) {
        return false;
    }

    public static Boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> p_235427_3_) {
        return false;
    }

    public static BlockBehaviour.Properties ShadeStoneProperties(){
        return BlockBehaviour.Properties.of(Material.STONE)
                .strength(5.0F, 9.0F)
                .sound(SoundType.STONE);
    }

    public static class ShadeStoneBlock extends Block {

        public ShadeStoneBlock() {
            super(ShadeStoneProperties());
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

    public static class StoneOreBlock extends DropExperienceBlock{
        public StoneOreBlock(){
            super(BlockBehaviour.Properties.of(Material.STONE)
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
