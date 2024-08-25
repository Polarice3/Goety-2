package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Goety.MOD_ID);

    public static final RegistryObject<BlockEntityType<ArcaBlockEntity>> ARCA = BLOCK_ENTITY.register("arca",
            () -> BlockEntityType.Builder.of(ArcaBlockEntity::new, ModBlocks.ARCA_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<CursedInfuserBlockEntity>> CURSED_INFUSER = BLOCK_ENTITY.register("cursed_infuser",
            () -> BlockEntityType.Builder.of(CursedInfuserBlockEntity::new, ModBlocks.CURSED_INFUSER.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrimInfuserBlockEntity>> GRIM_INFUSER = BLOCK_ENTITY.register("grim_infuser",
            () -> BlockEntityType.Builder.of(GrimInfuserBlockEntity::new, ModBlocks.GRIM_INFUSER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CursedCageBlockEntity>> CURSED_CAGE = BLOCK_ENTITY.register("cursed_cage",
            () -> BlockEntityType.Builder.of(CursedCageBlockEntity::new, ModBlocks.CURSED_CAGE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<DarkAltarBlockEntity>> DARK_ALTAR = BLOCK_ENTITY.register("dark_altar",
            () -> BlockEntityType.Builder.of(DarkAltarBlockEntity::new, ModBlocks.DARK_ALTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITY.register("pedestal",
            () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoulAbsorberBlockEntity>> SOUL_ABSORBER = BLOCK_ENTITY.register("soul_absorber",
            () -> BlockEntityType.Builder.of(SoulAbsorberBlockEntity::new, ModBlocks.SOUL_ABSORBER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoulMenderBlockEntity>> SOUL_MENDER = BLOCK_ENTITY.register("soul_mender",
            () -> BlockEntityType.Builder.of(SoulMenderBlockEntity::new, ModBlocks.SOUL_MENDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<IceBouquetTrapBlockEntity>> ICE_BOUQUET_TRAP = BLOCK_ENTITY.register("ice_bouquet_trap",
            () -> BlockEntityType.Builder.of(IceBouquetTrapBlockEntity::new, ModBlocks.ICE_BOUQUET_TRAP.get()).build(null));

    public static final RegistryObject<BlockEntityType<WindBlowerBlockEntity>> WIND_BLOWER = BLOCK_ENTITY.register("wind_blower",
            () -> BlockEntityType.Builder.of(WindBlowerBlockEntity::new, ModBlocks.WIND_BLOWER.get(), ModBlocks.MARBLE_WIND_BLOWER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResonanceCrystalBlockEntity>> RESONANCE_CRYSTAL = BLOCK_ENTITY.register("resonance_crystal",
            () -> BlockEntityType.Builder.of(ResonanceCrystalBlockEntity::new, ModBlocks.RESONANCE_CRYSTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<SculkDevourerBlockEntity>> SCULK_DEVOURER = BLOCK_ENTITY.register("sculk_devourer",
            () -> BlockEntityType.Builder.of(SculkDevourerBlockEntity::new, ModBlocks.SCULK_DEVOURER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SculkConverterBlockEntity>> SCULK_CONVERTER = BLOCK_ENTITY.register("sculk_converter",
            () -> BlockEntityType.Builder.of(SculkConverterBlockEntity::new, ModBlocks.SCULK_CONVERTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SculkGrowerBlockEntity>> SCULK_GROWER = BLOCK_ENTITY.register("sculk_grower",
            () -> BlockEntityType.Builder.of(SculkGrowerBlockEntity::new, ModBlocks.SCULK_GROWER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ForbiddenGrassBlockEntity>> FORBIDDEN_GRASS = BLOCK_ENTITY.register("forbidden_grass",
            () -> BlockEntityType.Builder.of(ForbiddenGrassBlockEntity::new, ModBlocks.FORBIDDEN_GRASS.get()).build(null));

    public static final RegistryObject<BlockEntityType<HookBellBlockEntity>> HOOK_BELL = BLOCK_ENTITY.register("hook_bell",
            () -> BlockEntityType.Builder.of(HookBellBlockEntity::new, ModBlocks.HOOK_BELL.get()).build(null));

    public static final RegistryObject<BlockEntityType<ShriekObeliskBlockEntity>> SHRIEKING_OBELISK = BLOCK_ENTITY.register("shriek_obelisk",
            () -> BlockEntityType.Builder.of(ShriekObeliskBlockEntity::new, ModBlocks.SHRIEKING_OBELISK.get()).build(null));

    public static final RegistryObject<BlockEntityType<NecroBrazierBlockEntity>> NECRO_BRAZIER = BLOCK_ENTITY.register("necro_brazier",
            () -> BlockEntityType.Builder.of(NecroBrazierBlockEntity::new, ModBlocks.NECRO_BRAZIER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoulCandlestickBlockEntity>> SOUL_CANDLESTICK = BLOCK_ENTITY.register("soul_candlestick",
            () -> BlockEntityType.Builder.of(SoulCandlestickBlockEntity::new, ModBlocks.SOUL_CANDLESTICK.get()).build(null));

    public static final RegistryObject<BlockEntityType<BrewCauldronBlockEntity>> BREWING_CAULDRON = BLOCK_ENTITY.register("witch_cauldron",
            () -> BlockEntityType.Builder.of(BrewCauldronBlockEntity::new, ModBlocks.BREWING_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<SpiderNestBlockEntity>> SPIDER_NEST = BLOCK_ENTITY.register("spider_nest",
            () -> BlockEntityType.Builder.of(SpiderNestBlockEntity::new, ModBlocks.SPIDER_NEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<GravestoneBlockEntity>> SHADE_GRAVESTONE = BLOCK_ENTITY.register("shade_gravestone",
            () -> BlockEntityType.Builder.of(GravestoneBlockEntity::new, ModBlocks.SHADE_GRAVESTONE.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlazingCageBlockEntity>> BLAZING_CAGE = BLOCK_ENTITY.register("blazing_cage",
            () -> BlockEntityType.Builder.of(BlazingCageBlockEntity::new, ModBlocks.BLAZING_CAGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<PithosBlockEntity>> PITHOS = BLOCK_ENTITY.register("pithos",
            () -> BlockEntityType.Builder.of(PithosBlockEntity::new, ModBlocks.PITHOS.get()).build(null));

    public static final RegistryObject<BlockEntityType<UrnBlockEntity>> CRYPT_URN = BLOCK_ENTITY.register("crypt_urn",
            () -> BlockEntityType.Builder.of(UrnBlockEntity::new, ModBlocks.CRYPT_URN.get()).build(null));

    public static final RegistryObject<BlockEntityType<HoleBlockEntity>> HOLE = BLOCK_ENTITY.register("hole",
            () -> BlockEntityType.Builder.of(HoleBlockEntity::new, ModBlocks.HOLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<PartLiquidBlockEntity>> PART_LIQUID = BLOCK_ENTITY.register("part_liquid",
            () -> BlockEntityType.Builder.of(PartLiquidBlockEntity::new, ModBlocks.PART_LIQUID.get()).build(null));

    public static final RegistryObject<BlockEntityType<NightBeaconBlockEntity>> NIGHT_BEACON = BLOCK_ENTITY.register("night_beacon",
            () -> BlockEntityType.Builder.of(NightBeaconBlockEntity::new, ModBlocks.NIGHT_BEACON.get()).build(null));

    public static final RegistryObject<BlockEntityType<MagicLightBlockEntity>> MAGIC_LIGHT = BLOCK_ENTITY.register("magic_light",
            () -> BlockEntityType.Builder.of(MagicLightBlockEntity::new, ModBlocks.SOUL_LIGHT_BLOCK.get(), ModBlocks.GLOW_LIGHT_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<TallSkullBlockEntity>> TALL_SKULL = BLOCK_ENTITY.register("tall_skull",
            () -> BlockEntityType.Builder.of(TallSkullBlockEntity::new, ModBlocks.TALL_SKULL_BLOCK.get(), ModBlocks.WALL_TALL_SKULL_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<RedstoneGolemSkullBlockEntity>> REDSTONE_GOLEM_SKULL = BLOCK_ENTITY.register("redstone_golem_skull",
            () -> BlockEntityType.Builder.of(RedstoneGolemSkullBlockEntity::new, ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_REDSTONE_GOLEM_SKULL_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<GraveGolemSkullBlockEntity>> GRAVE_GOLEM_SKULL = BLOCK_ENTITY.register("grave_golem_skull",
            () -> BlockEntityType.Builder.of(GraveGolemSkullBlockEntity::new, ModBlocks.GRAVE_GOLEM_SKULL_BLOCK.get(), ModBlocks.WALL_GRAVE_GOLEM_SKULL_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<RedstoneMonstrosityHeadBlockEntity>> REDSTONE_MONSTROSITY_HEAD = BLOCK_ENTITY.register("redstone_monstrosity_head",
            () -> BlockEntityType.Builder.of(RedstoneMonstrosityHeadBlockEntity::new, ModBlocks.REDSTONE_MONSTROSITY_HEAD_BLOCK.get(), ModBlocks.WALL_REDSTONE_MONSTROSITY_HEAD_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModChestBlockEntity>> MOD_CHEST = BLOCK_ENTITY.register("chest",
            () -> BlockEntityType.Builder.of(ModChestBlockEntity::new,
                    ModBlocks.HAUNTED_CHEST.get(), ModBlocks.ROTTEN_CHEST.get(),
                    ModBlocks.WINDSWEPT_CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModTrappedChestBlockEntity>> MOD_TRAPPED_CHEST = BLOCK_ENTITY.register("trapped_chest",
            () -> BlockEntityType.Builder.of(ModTrappedChestBlockEntity::new,
                    ModBlocks.TRAPPED_HAUNTED_CHEST.get(), ModBlocks.TRAPPED_ROTTEN_CHEST.get(),
                    ModBlocks.TRAPPED_WINDSWEPT_CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<LoftyChestBlockEntity>> LOFTY_CHEST = BLOCK_ENTITY.register("lofty_chest",
            () -> BlockEntityType.Builder.of(LoftyChestBlockEntity::new,
                    ModBlocks.LOFTY_CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> SIGN_BLOCK_ENTITIES = BLOCK_ENTITY.register("sign",
            () -> BlockEntityType.Builder.of(ModSignBlockEntity::new,
                    ModBlocks.HAUNTED_SIGN.get(), ModBlocks.HAUNTED_WALL_SIGN.get(),
                    ModBlocks.ROTTEN_SIGN.get(), ModBlocks.ROTTEN_WALL_SIGN.get(),
                    ModBlocks.WINDSWEPT_SIGN.get(), ModBlocks.WINDSWEPT_WALL_SIGN.get()).build(null));
}
