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

    public static final RegistryObject<BlockEntityType<SculkDevourerBlockEntity>> SCULK_DEVOURER = BLOCK_ENTITY.register("sculk_devourer",
            () -> BlockEntityType.Builder.of(SculkDevourerBlockEntity::new, ModBlocks.SCULK_DEVOURER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ForbiddenGrassBlockEntity>> FORBIDDEN_GRASS = BLOCK_ENTITY.register("forbidden_grass",
            () -> BlockEntityType.Builder.of(ForbiddenGrassBlockEntity::new, ModBlocks.FORBIDDEN_GRASS.get()).build(null));

    public static final RegistryObject<BlockEntityType<TallSkullBlockEntity>> TALL_SKULL = BLOCK_ENTITY.register("tall_skull",
            () -> BlockEntityType.Builder.of(TallSkullBlockEntity::new, ModBlocks.TALL_SKULL_BLOCK.get(), ModBlocks.WALL_TALL_SKULL_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> SIGN_BLOCK_ENTITIES = BLOCK_ENTITY.register("sign",
            () -> BlockEntityType.Builder.of(ModSignBlockEntity::new,
                    ModBlocks.HAUNTED_SIGN.get(), ModBlocks.HAUNTED_WALL_SIGN.get()).build(null));
}
