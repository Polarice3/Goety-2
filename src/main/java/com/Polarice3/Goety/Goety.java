package com.Polarice3.Goety;

import com.Polarice3.Goety.client.ClientProxy;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.CommonProxy;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.blocks.*;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.fluids.ModFluids;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.ender.BlastlingServant;
import com.Polarice3.Goety.common.entities.ally.ender.SnarelingServant;
import com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant;
import com.Polarice3.Goety.common.entities.ally.golem.*;
import com.Polarice3.Goety.common.entities.ally.illager.*;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.HereticServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.MaverickServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WitchServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.*;
import com.Polarice3.Goety.common.entities.ally.spider.*;
import com.Polarice3.Goety.common.entities.ally.undead.*;
import com.Polarice3.Goety.common.entities.ally.undead.bound.*;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heretic;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.ender.Blastling;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.common.entities.hostile.ender.Snareling;
import com.Polarice3.Goety.common.entities.hostile.ender.Watchling;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.*;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModPotions;
import com.Polarice3.Goety.common.items.ModSpawnEggs;
import com.Polarice3.Goety.common.items.ServantSpawnEggs;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.ritual.ModRituals;
import com.Polarice3.Goety.common.world.ModMobSpawnBiomeModifier;
import com.Polarice3.Goety.common.world.ModMobSpawnStructureModifier;
import com.Polarice3.Goety.common.world.features.ModFeatures;
import com.Polarice3.Goety.common.world.features.trees.trunkplacers.ModTrunkPlacerTypes;
import com.Polarice3.Goety.common.world.placements.ModPlacementType;
import com.Polarice3.Goety.common.world.processors.ModProcessors;
import com.Polarice3.Goety.common.world.structures.ModStructureTypes;
import com.Polarice3.Goety.compat.OtherModCompat;
import com.Polarice3.Goety.config.*;
import com.Polarice3.Goety.init.*;
import com.Polarice3.Goety.mixin.FireBlockAccessor;
import com.Polarice3.Goety.utils.ModPotionUtil;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

@Mod(Goety.MOD_ID)
public class Goety {
    public static final String MOD_ID = "goety";
    public static final Logger LOGGER = LogUtils.getLogger();
    @SuppressWarnings("removal")
    public static ModProxy PROXY = net.minecraftforge.fml.DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    @SuppressWarnings("removal")
    public static SidedInit SIDED_INIT = net.minecraftforge.fml.DistExecutor.unsafeRunForDist(() -> ClientSideInit::new, () -> SidedInit::new);

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public Goety() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlockEntities.BLOCK_ENTITY.register(modEventBus);
        ModEntityType.ENTITY_TYPE.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModContainerType.CONTAINER_TYPE.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModLootModifier.GLOBAL_LOOT_MODIFIER.register(modEventBus);
        ModRituals.RITUALS.register(modEventBus);
        ModStructureTypes.STRUCTURE_TYPE.register(modEventBus);
        ModPlacementType.STRUCTURE_PLACEMENT_TYPE.register(modEventBus);
        ModProcessors.STRUCTURE_PROCESSOR.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityAttributeCreation);
        modEventBus.addListener(this::SpawnPlacementEvent);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(EventPriority.LOWEST, this::finalLoad);

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goety"), "goety");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.SPEC, "goety/goety.toml");
        MainConfig.loadConfig(MainConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC, "goety/goety-attributes.toml");
        AttributesConfig.loadConfig(AttributesConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpellConfig.SPEC, "goety/goety-spells.toml");
        SpellConfig.loadConfig(SpellConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety-spells.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BrewConfig.SPEC, "goety/goety-brews.toml");
        BrewConfig.loadConfig(BrewConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety-brews.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MobsConfig.SPEC, "goety/goety-mobs.toml");
        MobsConfig.loadConfig(MobsConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety-mobs.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ItemConfig.SPEC, "goety/goety-items.toml");
        ItemConfig.loadConfig(ItemConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety/goety-items.toml").toString());

        final DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Goety.MOD_ID);
        biomeModifiers.register(modEventBus);
        biomeModifiers.register("mob_spawns", ModMobSpawnBiomeModifier::makeCodec);
        final DeferredRegister<Codec<? extends StructureModifier>> structureModifiers = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Goety.MOD_ID);
        structureModifiers.register(modEventBus);
        structureModifiers.register("mob_structure_spawns", ModMobSpawnStructureModifier::makeCodec);

        MinecraftForge.EVENT_BUS.register(this);
        ModItems.init();
        ModAttributes.init();
        ModBlocks.init();
        ModFluids.init();
        ModRecipeSerializer.init();
        ModSpawnEggs.init();
        ServantSpawnEggs.init();
        GoetyEffects.init();
        ModPotions.init();
        ModPaintings.init();
        ModPotPatterns.init();
        ModBanners.init();
        ModSounds.init();
        ModCriteriaTriggers.init();
        SIDED_INIT.init();
    }

    public static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
        }
        if (!Files.isDirectory(dirPath))
        {
            LOGGER.debug(CORE, "Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.error(CORE, "Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.error(CORE, "Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            LOGGER.debug(CORE, "Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug(CORE, "Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.init();

        OtherModCompat.setup(event);
        event.enqueueWork(() -> {
            ModCauldronInteraction.init();
            DispenserBlock.registerBehavior(ModBlocks.TALL_SKULL_ITEM.get(), new OptionalDispenseItemBehavior() {
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    this.setSuccess(ArmorItem.dispenseArmor(source, stack));
                    return stack;
                }
            });
            DispenserBlock.registerBehavior(ModItems.OMINOUS_SADDLE.get(), new OptionalDispenseItemBehavior() {
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    boolean flag = false;
                    BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                    List<LivingEntity> list = source.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);
                    if (!list.isEmpty()) {
                        LivingEntity livingentity = list.get(0);
                        if (livingentity instanceof ModRavager ravager) {
                            if (!ravager.hasSaddle()){
                                ravager.equipSaddle(true);
                                flag = true;
                            }
                        }
                    }
                    this.setSuccess(flag);
                    return stack;
                }
            });
            DispenserBlock.registerBehavior(ModItems.ILL_BOMB.get(), new AbstractProjectileDispenseBehavior() {
                protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
                    return new IllBomb(p_123469_.x(), p_123469_.y(), p_123469_.z(), p_123468_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.SNAP_FUNGUS.get(), new AbstractProjectileDispenseBehavior() {
                protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
                    return new SnapFungus(p_123469_.x(), p_123469_.y(), p_123469_.z(), p_123468_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.BLAST_FUNGUS.get(), new AbstractProjectileDispenseBehavior() {
                protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
                    return new BlastFungus(p_123469_.x(), p_123469_.y(), p_123469_.z(), p_123468_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.BERSERK_FUNGUS.get(), new AbstractProjectileDispenseBehavior() {
                protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
                    return new BerserkFungus(p_123469_.x(), p_123469_.y(), p_123469_.z(), p_123468_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.SPLASH_BREW.get(), new DispenseItemBehavior() {
                public ItemStack dispense(BlockSource p_123491_, ItemStack p_123492_) {
                    return (new AbstractProjectileDispenseBehavior() {
                        protected Projectile getProjectile(Level p_123501_, Position p_123502_, ItemStack p_123503_) {
                            return Util.make(new ThrownBrew(p_123501_, p_123502_.x(), p_123502_.y(), p_123502_.z()), (p_123499_) -> {
                                p_123499_.setItem(p_123503_);
                            });
                        }

                        protected float getUncertainty() {
                            return super.getUncertainty() * 0.5F;
                        }

                        protected float getPower() {
                            return super.getPower() * 1.25F;
                        }
                    }).dispense(p_123491_, p_123492_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.LINGERING_BREW.get(), new DispenseItemBehavior() {
                public ItemStack dispense(BlockSource p_123507_, ItemStack p_123508_) {
                    return (new AbstractProjectileDispenseBehavior() {
                        protected Projectile getProjectile(Level p_123517_, Position p_123518_, ItemStack p_123519_) {
                            return Util.make(new ThrownBrew(p_123517_, p_123518_.x(), p_123518_.y(), p_123518_.z()), (p_123515_) -> {
                                p_123515_.setItem(p_123519_);
                            });
                        }

                        protected float getUncertainty() {
                            return super.getUncertainty() * 0.5F;
                        }

                        protected float getPower() {
                            return super.getPower() * 1.25F;
                        }
                    }).dispense(p_123507_, p_123508_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.GAS_BREW.get(), new DispenseItemBehavior() {
                public ItemStack dispense(BlockSource p_123507_, ItemStack p_123508_) {
                    return (new AbstractProjectileDispenseBehavior() {
                        protected Projectile getProjectile(Level p_123517_, Position p_123518_, ItemStack p_123519_) {
                            return Util.make(new ThrownBrew(p_123517_, p_123518_.x(), p_123518_.y(), p_123518_.z()), (p_123515_) -> {
                                p_123515_.setItem(p_123519_);
                            });
                        }

                        protected float getUncertainty() {
                            return super.getUncertainty() * 0.5F;
                        }

                        protected float getPower() {
                            return super.getPower() * 1.25F;
                        }
                    }).dispense(p_123507_, p_123508_);
                }
            });
            DispenserBlock.registerBehavior(ModItems.HAUNTED_ARMOR_STAND.get(), new DefaultDispenseItemBehavior() {
                public ItemStack execute(BlockSource p_123461_, ItemStack p_123462_) {
                    Direction direction = p_123461_.getBlockState().getValue(DispenserBlock.FACING);
                    BlockPos blockpos = p_123461_.getPos().relative(direction);
                    Level level = p_123461_.getLevel();
                    HauntedArmorStand armorstand = new HauntedArmorStand(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
                    EntityType.updateCustomEntityTag(level, (Player)null, armorstand, p_123462_.getTag());
                    armorstand.setYRot(direction.toYRot());
                    level.addFreshEntity(armorstand);
                    p_123462_.shrink(1);
                    return p_123462_;
                }
            });
            DispenserBlock.registerBehavior(ModItems.QUICK_GROWING_SEED.get(), new DefaultDispenseItemBehavior() {
                public ItemStack execute(BlockSource p_123461_, ItemStack p_123462_) {
                    Direction direction = p_123461_.getBlockState().getValue(DispenserBlock.FACING);
                    BlockPos blockpos = p_123461_.getPos().relative(direction);
                    Level level = p_123461_.getLevel();
                    AbstractVine vine = ModEntityType.QUICK_GROWING_VINE.get().create(level);
                    if (vine != null){
                        EntityType<?> entityType = vine.getVariant(null, level, blockpos);
                        if (entityType != null){
                            vine = (AbstractVine) entityType.create(level);
                        }
                        if (vine != null){
                            Vec3 vec3 = new Vec3((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
                            vine.setPos(vec3);
                            if (level instanceof ServerLevel serverLevel) {
                                vine.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            }
                            vine.setPerpetual(true);
                            if (level.addFreshEntity(vine)){
                                p_123462_.shrink(1);
                            }
                        }
                    }
                    return p_123462_;
                }
            });
            DispenserBlock.registerBehavior(ModItems.POISON_QUILL_SEED.get(), new DefaultDispenseItemBehavior() {
                public ItemStack execute(BlockSource p_123461_, ItemStack p_123462_) {
                    Direction direction = p_123461_.getBlockState().getValue(DispenserBlock.FACING);
                    BlockPos blockpos = p_123461_.getPos().relative(direction);
                    Level level = p_123461_.getLevel();
                    AbstractVine vine = ModEntityType.POISON_QUILL_VINE.get().create(level);
                    if (vine != null){
                        EntityType<?> entityType = vine.getVariant(null, level, blockpos);
                        if (entityType != null){
                            vine = (AbstractVine) entityType.create(level);
                        }
                        if (vine != null){
                            Vec3 vec3 = new Vec3((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
                            vine.setPos(vec3);
                            if (level instanceof ServerLevel serverLevel) {
                                vine.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                            }
                            vine.setPerpetual(true);
                            if (level.addFreshEntity(vine)){
                                p_123462_.shrink(1);
                            }
                        }
                    }
                    return p_123462_;
                }
            });
            DispenserBlock.registerBehavior(ModItems.VOID_BUCKET.get(), new DefaultDispenseItemBehavior() {
                private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

                public ItemStack execute(BlockSource p_123561_, ItemStack p_123562_) {
                    DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)p_123562_.getItem();
                    BlockPos blockpos = p_123561_.getPos().relative(p_123561_.getBlockState().getValue(DispenserBlock.FACING));
                    Level level = p_123561_.getLevel();
                    if (dispensiblecontaineritem.emptyContents((Player)null, level, blockpos, (BlockHitResult)null, p_123562_)) {
                        dispensiblecontaineritem.checkExtraContent((Player)null, level, p_123562_, blockpos);
                        return new ItemStack(Items.BUCKET);
                    } else {
                        return this.defaultDispenseItemBehavior.dispense(p_123561_, p_123562_);
                    }
                }
            });
            ModDispenserRegister.registerAlternativeDispenseBehavior(new ModDispenserRegister.AlternativeDispenseBehavior(
                    Goety.MOD_ID, Items.WATER_BUCKET,
                    (blockSource, itemStack) -> blockSource.getLevel().getBlockState(ModDispenserRegister.offsetPos(blockSource)).is(ModBlocks.BREWING_CAULDRON.get()),
                    new OptionalDispenseItemBehavior() {
                        protected ItemStack execute(BlockSource source, ItemStack stack) {
                            BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                            BlockState blockState = source.getLevel().getBlockState(blockpos);
                            if (blockState.is(ModBlocks.BREWING_CAULDRON.get())) {
                                if (blockState.getValue(BrewCauldronBlock.LEVEL) < 3) {
                                    this.setSuccess(source.getLevel().setBlockAndUpdate(blockpos, blockState.setValue(BrewCauldronBlock.LEVEL, 3)));
                                    return new ItemStack(Items.BUCKET);
                                }
                            }
                            return stack;
                        }
                    }));
            ModDispenserRegister.registerAlternativeDispenseBehavior(new ModDispenserRegister.AlternativeDispenseBehavior(
                    Goety.MOD_ID, Items.BUCKET,
                    (blockSource, itemStack) -> blockSource.getLevel().getBlockState(ModDispenserRegister.offsetPos(blockSource)).is(ModBlocks.BREWING_CAULDRON.get()),
                    new OptionalDispenseItemBehavior() {
                        protected ItemStack execute(BlockSource source, ItemStack stack) {
                            BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                            BlockState blockState = source.getLevel().getBlockState(blockpos);
                            if (blockState.is(ModBlocks.BREWING_CAULDRON.get())) {
                                if (blockState.getValue(BrewCauldronBlock.LEVEL) == 3) {
                                    if (source.getLevel().getBlockEntity(blockpos) instanceof BrewCauldronBlockEntity blockEntity){
                                        blockEntity.reset();
                                    }
                                    this.setSuccess(source.getLevel().setBlockAndUpdate(blockpos, blockState.setValue(BrewCauldronBlock.LEVEL, 0)));
                                    return new ItemStack(Items.WATER_BUCKET);
                                }
                            }
                            return stack;
                        }
                    }));
            AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.put(ModBlocks.HAUNTED_LOG.get(), ModBlocks.STRIPPED_HAUNTED_LOG.get());
            AxeItem.STRIPPABLES.put(ModBlocks.HAUNTED_WOOD.get(), ModBlocks.STRIPPED_HAUNTED_WOOD.get());
            AxeItem.STRIPPABLES.put(ModBlocks.ROTTEN_LOG.get(), ModBlocks.STRIPPED_ROTTEN_LOG.get());
            AxeItem.STRIPPABLES.put(ModBlocks.ROTTEN_WOOD.get(), ModBlocks.STRIPPED_ROTTEN_WOOD.get());
            AxeItem.STRIPPABLES.put(ModBlocks.WINDSWEPT_LOG.get(), ModBlocks.STRIPPED_WINDSWEPT_LOG.get());
            AxeItem.STRIPPABLES.put(ModBlocks.WINDSWEPT_WOOD.get(), ModBlocks.STRIPPED_WINDSWEPT_WOOD.get());
            AxeItem.STRIPPABLES.put(ModBlocks.PINE_LOG.get(), ModBlocks.STRIPPED_PINE_LOG.get());
            AxeItem.STRIPPABLES.put(ModBlocks.PINE_WOOD.get(), ModBlocks.STRIPPED_PINE_WOOD.get());
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.CHORUS_STALK.getId(), ModBlocks.POTTED_CHORUS_STALK);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.CHORUS_FERN.getId(), ModBlocks.POTTED_CHORUS_FERN);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.HAUNTED_SAPLING.getId(), ModBlocks.POTTED_HAUNTED_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.ROTTEN_SAPLING.getId(), ModBlocks.POTTED_ROTTEN_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WINDSWEPT_SAPLING.getId(), ModBlocks.POTTED_WINDSWEPT_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.PINE_SAPLING.getId(), ModBlocks.POTTED_PINE_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.CHORUS_SAPLING.getId(), ModBlocks.POTTED_CHORUS_SAPLING);
            WoodType.register(ModWoodType.HAUNTED);
            WoodType.register(ModWoodType.ROTTEN);
            WoodType.register(ModWoodType.WINDSWEPT);
            WoodType.register(ModWoodType.PINE);
            ModPotPatterns.addPatterns();
            addBrewingRecipes();

            FireBlockAccessor fireBlockAccessor = (FireBlockAccessor) Blocks.FIRE;

            Collection<Block> blocks = new ArrayList<>();
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
                if (block.defaultBlockState().ignitedByLava()){
                    blocks.add(block);
                }
            });
            for (Block block : blocks){
                if (block.defaultBlockState().ignitedByLava()){
                    if (!(block instanceof PressurePlateBlock) && !(block instanceof AbstractChestBlock<?>)
                    && !(block instanceof DoorBlock) && !(block instanceof SignBlock)
                    && !(block instanceof SpiderNestBlock) && !(block instanceof WitchPoleBlock)) {
                        fireBlockAccessor.callSetFlammable(block, 5, 20);
                    }
                }
            }
            ComposterBlock.COMPOSTABLES.put(ModBlocks.END_GROWTH_VINES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.HAUNTED_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.WINDSWEPT_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.PINE_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.WINDSWEPT_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.PINE_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_VINE.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_BLOSSOM_VINES_PRUNED.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ModItems.SNAP_FUNGUS.get(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.ROTTEN_SAPLING.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_SAPLING.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_SPROUT.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.END_GRASS_SPROUT.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_FERN_SPROUT.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.ROTTEN_LEAVES.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_LEAVES.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_BLOSSOM_LEAVES.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_STALK.get().asItem(), 0.85F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.END_GRASS.get().asItem(), 0.85F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_TALL_GRASS.get().asItem(), 0.85F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_FERN.get().asItem(), 0.85F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.LARGE_CHORUS_STALK.get().asItem(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.TALL_END_GRASS.get().asItem(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.LARGE_CHORUS_FERN.get().asItem(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModBlocks.CHORUS_BLOSSOM_VINES.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(ModItems.CHORUS_GROWTH.get(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLAST_FUNGUS.get(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModItems.BERSERK_FUNGUS.get(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModItems.QUICK_GROWING_SEED.get(), 1.0F);
            ComposterBlock.COMPOSTABLES.put(ModItems.POISON_QUILL_SEED.get(), 1.0F);
        });
    }

    private void finalLoad(FMLLoadCompleteEvent event){
        event.enqueueWork(() -> {
            ModDispenserRegister.getSortedAlternativeDispenseBehaviors().forEach(ModDispenserRegister.AlternativeDispenseBehavior::register);
            ModFluids.interactionInit();
        });
    }

    private static void addBrewingRecipes(){
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModItems.SNAP_FUNGUS.get().getDefaultInstance(), Ingredient.of(Items.LILY_OF_THE_VALLEY), new ItemStack(ModItems.BERSERK_FUNGUS.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setPotion(Potions.AWKWARD), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setSplashPotion(Potions.AWKWARD), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setSplashPotion(Potions.AWKWARD), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setPotion(ModPotions.CLIMBING.get()), Ingredient.of(Items.REDSTONE), ModPotionUtil.setPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get()), Ingredient.of(Items.REDSTONE), ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get()), Ingredient.of(Items.REDSTONE), ModPotionUtil.setLingeringPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setPotion(ModPotions.CLIMBING.get()), Ingredient.of(Items.GUNPOWDER), ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setPotion(ModPotions.LONG_CLIMBING.get()), Ingredient.of(Items.GUNPOWDER), ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get()), Ingredient.of(Items.DRAGON_BREATH), ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new ModPotionUtil(ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get()), Ingredient.of(Items.DRAGON_BREATH), ModPotionUtil.setLingeringPotion(ModPotions.LONG_CLIMBING.get())));
    }

    private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(ModEntityType.APOSTLE.get(), Apostle.setCustomAttributes().build());
        event.put(ModEntityType.OBSIDIAN_MONOLITH.get(), ObsidianMonolith.setCustomAttributes().build());
        event.put(ModEntityType.WARLOCK.get(), Warlock.setCustomAttributes().build());
        event.put(ModEntityType.WARTLING.get(), Wartling.setCustomAttributes().build());
        event.put(ModEntityType.HERETIC.get(), Heretic.setCustomAttributes().build());
        event.put(ModEntityType.MAVERICK.get(), Maverick.setCustomAttributes().build());
        event.put(ModEntityType.CRONE.get(), Crone.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinBruteServant.setCustomAttributes().build());
        event.put(ModEntityType.MALGHAST.get(), Malghast.setCustomAttributes().build());
        event.put(ModEntityType.INFERNO.get(), Inferno.setCustomAttributes().build());
        event.put(ModEntityType.DAMNED.get(), Damned.setCustomAttributes().build());
        event.put(ModEntityType.VAMPIRE_BAT.get(), VampireBat.setCustomAttributes().build());
        event.put(ModEntityType.HOSTILE_BLACK_WOLF.get(), HostileBlackWolf.setCustomAttributes().build());
        event.put(ModEntityType.FRAYED.get(), Frayed.setCustomAttributes().build());
        event.put(ModEntityType.RATTLED.get(), Rattled.setCustomAttributes().build());
        event.put(ModEntityType.REAPER.get(), Reaper.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH.get(), Wraith.setCustomAttributes().build());
        event.put(ModEntityType.BORDER_WRAITH.get(), BorderWraith.setCustomAttributes().build());
        event.put(ModEntityType.MUCK_WRAITH.get(), MuckWraith.setCustomAttributes().build());
        event.put(ModEntityType.CRYPT_SLIME.get(), CryptSlime.setCustomAttributes().build());
        event.put(ModEntityType.WEB_SPIDER.get(), WebSpider.setCustomAttributes().build());
        event.put(ModEntityType.ICY_SPIDER.get(), IcySpider.setCustomAttributes().build());
        event.put(ModEntityType.BONE_SPIDER.get(), BoneSpider.setCustomAttributes().build());
        event.put(ModEntityType.BROOD_MOTHER.get(), AbstractBroodMother.setCustomAttributes().build());
        event.put(ModEntityType.NECROMANCER.get(), HostileNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.CAIRN_NECROMANCER.get(), CairnNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.MOSSY_NECROMANCER.get(), MossyNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR.get(), HauntedArmor.setCustomAttributes().build());
        event.put(ModEntityType.WATCHLING.get(), Watchling.setCustomAttributes().build());
        event.put(ModEntityType.BLASTLING.get(), Blastling.setCustomAttributes().build());
        event.put(ModEntityType.SNARELING.get(), Snareling.setCustomAttributes().build());
        event.put(ModEntityType.ENDERSENT.get(), Endersent.setCustomAttributes().build());
        event.put(ModEntityType.ENDER_KEEPER.get(), EnderKeeper.setCustomAttributes().build());
        event.put(ModEntityType.VEX_SERVANT.get(), AllyVex.setCustomAttributes().build());
        event.put(ModEntityType.IRK_SERVANT.get(), AllyIrk.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), ZombieVillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.HUSK_SERVANT.get(), HuskServant.setCustomAttributes().build());
        event.put(ModEntityType.DROWNED_SERVANT.get(), DrownedServant.setCustomAttributes().build());
        event.put(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), FrozenZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), JungleZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.FRAYED_SERVANT.get(), FrayedServant.setCustomAttributes().build());
        event.put(ModEntityType.BLACKGUARD_SERVANT.get(), BlackguardServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_SERVANT.get(), SkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.STRAY_SERVANT.get(), StrayServant.setCustomAttributes().build());
        event.put(ModEntityType.WITHER_SKELETON_SERVANT.get(), WitherSkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.MOSSY_SKELETON_SERVANT.get(), MossySkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), SunkenSkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.RATTLED_SERVANT.get(), RattledServant.setCustomAttributes().build());
        event.put(ModEntityType.NECROMANCER_SERVANT.get(), NecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.CAIRN_NECROMANCER_SERVANT.get(), CairnNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.MOSSY_NECROMANCER_SERVANT.get(), MossyNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.DROWNED_NECROMANCER_SERVANT.get(), DrownedNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.WITHER_NECROMANCER_SERVANT.get(), WitherNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.REAPER_SERVANT.get(), ReaperServant.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH_SERVANT.get(), WraithServant.setCustomAttributes().build());
        event.put(ModEntityType.BORDER_WRAITH_SERVANT.get(), BorderWraithServant.setCustomAttributes().build());
        event.put(ModEntityType.MUCK_WRAITH_SERVANT.get(), MuckWraithServant.setCustomAttributes().build());
        event.put(ModEntityType.PHANTOM_SERVANT.get(), PhantomServant.setCustomAttributes().build());
        event.put(ModEntityType.VANGUARD_SERVANT.get(), VanguardServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), SkeletonPillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), ZombieVindicatorServant.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_EVOKER.get(), BoundEvoker.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_GEOMANCER.get(), BoundGeomancer.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_ICEOLOGER.get(), BoundIceologer.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_CRYOLOGER.get(), BoundCryologer.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_WIND_CALLER.get(), BoundWindCaller.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_STORM_CASTER.get(), BoundStormCaster.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), HauntedArmorServant.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_SKULL.get(), HauntedSkull.setCustomAttributes().build());
        event.put(ModEntityType.DOPPELGANGER.get(), Doppelganger.setCustomAttributes().build());
        event.put(ModEntityType.MINI_GHAST.get(), MiniGhast.setCustomAttributes().build());
        event.put(ModEntityType.GHAST_SERVANT.get(), GhastServant.setCustomAttributes().build());
        event.put(ModEntityType.BLAZE_SERVANT.get(), BlazeServant.setCustomAttributes().build());
        event.put(ModEntityType.WILDFIRE.get(), Wildfire.setCustomAttributes().build());
        event.put(ModEntityType.SLIME_SERVANT.get(), SlimeServant.setCustomAttributes().build());
        event.put(ModEntityType.MAGMA_CUBE_SERVANT.get(), MagmaCubeServant.setCustomAttributes().build());
        event.put(ModEntityType.CRYPT_SLIME_SERVANT.get(), CryptSlimeServant.setCustomAttributes().build());
        event.put(ModEntityType.TROPICAL_SLIME_SERVANT.get(), TropicalSlimeServant.setCustomAttributes().build());
        event.put(ModEntityType.SPIDER_SERVANT.get(), SpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.CAVE_SPIDER_SERVANT.get(), CaveSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.WEB_SPIDER_SERVANT.get(), WebSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.ICY_SPIDER_SERVANT.get(), IcySpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.BONE_SPIDER_SERVANT.get(), BoneSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.BROOD_MOTHER_SERVANT.get(), AbstractBroodMother.setCustomAttributes().build());
        event.put(ModEntityType.PRISONER.get(), Villager.createAttributes().build());
        event.put(ModEntityType.NEOLLAGER.get(), Neollager.setCustomAttributes().build());
        event.put(ModEntityType.PILLAGER_SERVANT.get(), PillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.PIKER_SERVANT.get(), PikerServant.setCustomAttributes().build());
        event.put(ModEntityType.SIGNALER_SERVANT.get(), SignalerServant.setCustomAttributes().build());
        event.put(ModEntityType.VINDICATOR_SERVANT.get(), VindicatorServant.setCustomAttributes().build());
        event.put(ModEntityType.VINDICATOR_CHEF_SERVANT.get(), VindicatorChefServant.setCustomAttributes().build());
        event.put(ModEntityType.MOUNTAINEER_SERVANT.get(), MountaineerServant.setCustomAttributes().build());
        event.put(ModEntityType.CRUSHER_SERVANT.get(), CrusherServant.setCustomAttributes().build());
        event.put(ModEntityType.EVOKER_SERVANT.get(), EvokerServant.setCustomAttributes().build());
        event.put(ModEntityType.GEOMANCER_SERVANT.get(), GeomancerServant.setCustomAttributes().build());
        event.put(ModEntityType.ICEOLOGER_SERVANT.get(), IceologerServant.setCustomAttributes().build());
        event.put(ModEntityType.CRYOLOGER_SERVANT.get(), CryologerServant.setCustomAttributes().build());
        event.put(ModEntityType.WIND_CALLER_SERVANT.get(), WindCallerServant.setCustomAttributes().build());
        event.put(ModEntityType.STORM_CASTER_SERVANT.get(), StormCasterServant.setCustomAttributes().build());
        event.put(ModEntityType.RIPPER_SERVANT.get(), RipperServant.setCustomAttributes().build());
        event.put(ModEntityType.TRAMPLER_SERVANT.get(), AllyTrampler.setCustomAttributes().build());
        event.put(ModEntityType.RAVAGED.get(), Ravaged.setCustomAttributes().build());
        event.put(ModEntityType.MOD_RAVAGER.get(), ModRavager.setCustomAttributes().build());
        event.put(ModEntityType.ARMORED_RAVAGER.get(), Ravager.createAttributes().build());
        event.put(ModEntityType.ZOMBIE_RAVAGER.get(), ZombieRavager.setCustomAttributes().build());
        event.put(ModEntityType.WITCH_SERVANT.get(), WitchServant.setCustomAttributes().build());
        event.put(ModEntityType.WARLOCK_SERVANT.get(), WarlockServant.setCustomAttributes().build());
        event.put(ModEntityType.HERETIC_SERVANT.get(), HereticServant.setCustomAttributes().build());
        event.put(ModEntityType.MAVERICK_SERVANT.get(), MaverickServant.setCustomAttributes().build());
        event.put(ModEntityType.BLACK_WOLF.get(), BlackWolf.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_WOLF.get(), SkeletonWolf.setCustomAttributes().build());
        event.put(ModEntityType.WINTER_WOLF.get(), WinterWolf.setCustomAttributes().build());
        event.put(ModEntityType.STORMHOUND.get(), Stormhound.setCustomAttributes().build());
        event.put(ModEntityType.HELLHOUND.get(), Hellhound.setCustomAttributes().build());
        event.put(ModEntityType.TWILIGHT_GOAT.get(), TwilightGoat.setCustomAttributes().build());
        event.put(ModEntityType.SNAPPER.get(), Snapper.setCustomAttributes().build());
        event.put(ModEntityType.GNASHER.get(), Gnasher.setCustomAttributes().build());
        event.put(ModEntityType.GUARDIAN_SERVANT.get(), GuardianServant.setCustomAttributes().build());
        event.put(ModEntityType.BEAR_SERVANT.get(), BearServant.setCustomAttributes().build());
        event.put(ModEntityType.POLAR_BEAR_SERVANT.get(), BearServant.setCustomAttributes().build());
        event.put(ModEntityType.HOGLIN_SERVANT.get(), HoglinServant.setCustomAttributes().build());
        event.put(ModEntityType.BLACK_BEAST.get(), BlackBeast.setCustomAttributes().build());
        event.put(ModEntityType.WHISPERER.get(), Whisperer.setCustomAttributes().build());
        event.put(ModEntityType.WAVEWHISPERER.get(), Wavewhisperer.setCustomAttributes().build());
        event.put(ModEntityType.LEAPLEAF.get(), Leapleaf.setCustomAttributes().build());
        event.put(ModEntityType.STONE_MINISTROSITY.get(), StoneMinistrosity.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_MINISTROSITY.get(), RedstoneMinistrosity.setCustomAttributes().build());
        event.put(ModEntityType.ICE_GOLEM.get(), IceGolem.setCustomAttributes().build());
        event.put(ModEntityType.SQUALL_GOLEM.get(), SquallGolem.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_GOLEM.get(), RedstoneGolem.setCustomAttributes().build());
        event.put(ModEntityType.GRAVE_GOLEM.get(), GraveGolem.setCustomAttributes().build());
        event.put(ModEntityType.HAUNT.get(), Haunt.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_MONSTROSITY.get(), RedstoneMonstrosity.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_CUBE.get(), RedstoneCube.setCustomAttributes().build());
        event.put(ModEntityType.WATCHLING_SERVANT.get(), WatchlingServant.setCustomAttributes().build());
        event.put(ModEntityType.BLASTLING_SERVANT.get(), BlastlingServant.setCustomAttributes().build());
        event.put(ModEntityType.SNARELING_SERVANT.get(), SnarelingServant.setCustomAttributes().build());
        event.put(ModEntityType.TOTEMIC_WALL.get(), TotemicWall.setCustomAttributes().build());
        event.put(ModEntityType.TOTEMIC_BOMB.get(), TotemicBomb.setCustomAttributes().build());
        event.put(ModEntityType.GLACIAL_WALL.get(), GlacialWall.setCustomAttributes().build());
        event.put(ModEntityType.QUICK_GROWING_VINE.get(), QuickGrowingVine.setCustomAttributes().build());
        event.put(ModEntityType.QUICK_GROWING_KELP.get(), QuickGrowingKelp.setCustomAttributes().build());
        event.put(ModEntityType.POISON_QUILL_VINE.get(), PoisonQuillVine.setCustomAttributes().build());
        event.put(ModEntityType.POISON_ANEMONE.get(), PoisonAnemone.setCustomAttributes().build());
        event.put(ModEntityType.SPIDER_EGG.get(), SpiderEgg.setCustomAttributes().build());
        event.put(ModEntityType.INSECT_SWARM.get(), InsectSwarm.setCustomAttributes().build());
        event.put(ModEntityType.BEAST_HEAD.get(), BeastHead.setCustomAttributes().build());
        event.put(ModEntityType.GULF_TENTACLE.get(), GulfTentacle.setCustomAttributes().build());
        event.put(ModEntityType.VOLCANO.get(), Volcano.setCustomAttributes().build());
        event.put(ModEntityType.SORCERER.get(), Sorcerer.setCustomAttributes().build());
        event.put(ModEntityType.ENVIOKER.get(), Envioker.setCustomAttributes().build());
        event.put(ModEntityType.TORMENTOR.get(), Tormentor.setCustomAttributes().build());
        event.put(ModEntityType.INQUILLAGER.get(), Inquillager.setCustomAttributes().build());
        event.put(ModEntityType.CONQUILLAGER.get(), Conquillager.setCustomAttributes().build());
        event.put(ModEntityType.PIKER.get(), Piker.setCustomAttributes().build());
        event.put(ModEntityType.RIPPER.get(), Ripper.setCustomAttributes().build());
        event.put(ModEntityType.TRAMPLER.get(), Trampler.setCustomAttributes().build());
        event.put(ModEntityType.CRUSHER.get(), Crusher.setCustomAttributes().build());
        event.put(ModEntityType.STORM_CASTER.get(), StormCaster.setCustomAttributes().build());
        event.put(ModEntityType.CRYOLOGER.get(), Cryologer.setCustomAttributes().build());
        event.put(ModEntityType.PREACHER.get(), Preacher.setCustomAttributes().build());
        event.put(ModEntityType.MINISTER.get(), Minister.setCustomAttributes().build());
        event.put(ModEntityType.HOSTILE_REDSTONE_GOLEM.get(), HostileRedstoneGolem.setCustomAttributes().build());
        event.put(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), HostileRedstoneMonstrosity.setCustomAttributes().build());
        event.put(ModEntityType.VIZIER.get(), Vizier.setCustomAttributes().build());
        event.put(ModEntityType.VIZIER_CLONE.get(), VizierClone.setCustomAttributes().build());
        event.put(ModEntityType.IRK.get(), Irk.setCustomAttributes().build());
        event.put(ModEntityType.WIGHT.get(), Wight.setCustomAttributes().build());
        event.put(ModEntityType.CARRION_MAGGOT.get(), CarrionMaggot.setCustomAttributes().build());
        event.put(ModEntityType.CARRION_FLY.get(), CarrionFly.setCustomAttributes().build());
        event.put(ModEntityType.SKULL_LORD.get(), SkullLord.setCustomAttributes().build());
        event.put(ModEntityType.BONE_LORD.get(), BoneLord.setCustomAttributes().build());
        event.put(ModEntityType.WITHER_NECROMANCER.get(), WitherNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.RAID_BOSS_SUMMON.get(), Monster.createMobAttributes().build());
        event.put(ModEntityType.SURVEY_EYE.get(), Mob.createMobAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    private void SpawnPlacementEvent(SpawnPlacementRegisterEvent event){
        event.register(ModEntityType.WARLOCK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.HERETIC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.MAVERICK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.OBSIDIAN_MONOLITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ObsidianMonolith::checkOMSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.HOSTILE_BLACK_WOLF.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkDayMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.FRAYED.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Frayed::checkFrayedSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.RATTLED.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Rattled::checkRattledSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.REAPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.BORDER_WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.MUCK_WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.CRYPT_SLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CryptSlime::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.WEB_SPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.ICY_SPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.BONE_SPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.NECROMANCER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.CAIRN_NECROMANCER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.MOSSY_NECROMANCER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.HAUNTED_ARMOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.WATCHLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.BLASTLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.SNARELING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.ENDERSENT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }

    @SuppressWarnings("all")
    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.BACK.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.NECKLACE.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.HANDS.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, () -> top.theillusivec4.curios.api.SlotTypePreset.RING.getMessageBuilder().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ModSaveInventory.resetInstance();
        ModSaveInventory.setInstance(event.getServer().overworld());
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        ModSaveInventory.resetInstance();
    }
}
