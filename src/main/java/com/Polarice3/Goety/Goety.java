package com.Polarice3.Goety;

import com.Polarice3.Goety.client.ClientProxy;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.CommonProxy;
import com.Polarice3.Goety.common.advancements.ModCriteriaTriggers;
import com.Polarice3.Goety.common.blocks.BrewCauldronBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModWoodType;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.golem.*;
import com.Polarice3.Goety.common.entities.ally.spider.*;
import com.Polarice3.Goety.common.entities.ally.undead.*;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundEvoker;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.*;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import com.Polarice3.Goety.common.entities.hostile.*;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.entities.hostile.servants.*;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.SkullLaser;
import com.Polarice3.Goety.common.entities.util.TunnelingFang;
import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModPotions;
import com.Polarice3.Goety.common.items.ModSpawnEggs;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.ritual.ModRituals;
import com.Polarice3.Goety.common.world.ModMobSpawnBiomeModifier;
import com.Polarice3.Goety.common.world.processors.ModProcessors;
import com.Polarice3.Goety.common.world.structures.ModStructureTypes;
import com.Polarice3.Goety.compat.OtherModCompat;
import com.Polarice3.Goety.config.*;
import com.Polarice3.Goety.init.*;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(Goety.MOD_ID)
public class Goety {
    public static final String MOD_ID = "goety";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ModProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public Goety() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlockEntities.BLOCK_ENTITY.register(modEventBus);
        ModEntityType.ENTITY_TYPE.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModContainerType.CONTAINER_TYPE.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModLootModifier.GLOBAL_LOOT_MODIFIER.register(modEventBus);
        ModRituals.RITUALS.register(modEventBus);
        ModStructureTypes.STRUCTURE_TYPE.register(modEventBus);
        ModProcessors.STRUCTURE_PROCESSOR.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityAttributeCreation);
        modEventBus.addListener(this::SpawnPlacementEvent);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(EventPriority.LOWEST, this::finalLoad);

        FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goety"), "goety");
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

        MinecraftForge.EVENT_BUS.register(this);
        ModItems.init();
        ModBlocks.init();
        ModRecipeSerializer.init();
        ModSpawnEggs.init();
        GoetyEffects.init();
        ModPotions.init();
        ModPaintings.init();
        ModSounds.init();
        ModCriteriaTriggers.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.init();

        OtherModCompat.setup(event);
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(ModBlocks.TALL_SKULL_ITEM.get(), new OptionalDispenseItemBehavior() {
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    this.setSuccess(ArmorItem.dispenseArmor(source, stack));
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
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.HAUNTED_SAPLING.getId(), ModBlocks.POTTED_HAUNTED_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.ROTTEN_SAPLING.getId(), ModBlocks.POTTED_ROTTEN_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WINDSWEPT_SAPLING.getId(), ModBlocks.POTTED_WINDSWEPT_SAPLING);
            WoodType.register(ModWoodType.HAUNTED);
            WoodType.register(ModWoodType.ROTTEN);
            WoodType.register(ModWoodType.WINDSWEPT);
            RaidAdditions.addRaiders();
            addBrewingRecipes();
        });
    }

    private void finalLoad(FMLLoadCompleteEvent event){
        event.enqueueWork(() -> {
                ModDispenserRegister.getSortedAlternativeDispenseBehaviors().forEach(ModDispenserRegister.AlternativeDispenseBehavior::register);
        });
    }

    private static void addBrewingRecipes(){
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModItems.SNAP_FUNGUS.get()), Ingredient.of(Items.LILY_OF_THE_VALLEY), new ItemStack(ModItems.BERSERK_FUNGUS.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setPotion(Potions.AWKWARD)), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setSplashPotion(Potions.AWKWARD)), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setLingeringPotion(Potions.AWKWARD)), Ingredient.of(ModItems.SPIDER_EGG.get()), ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setPotion(ModPotions.CLIMBING.get())), Ingredient.of(Items.REDSTONE), ModPotionUtil.setPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())), Ingredient.of(Items.REDSTONE), ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get())), Ingredient.of(Items.REDSTONE), ModPotionUtil.setLingeringPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setPotion(ModPotions.CLIMBING.get())), Ingredient.of(Items.GUNPOWDER), ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setPotion(ModPotions.LONG_CLIMBING.get())), Ingredient.of(Items.GUNPOWDER), ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setSplashPotion(ModPotions.CLIMBING.get())), Ingredient.of(Items.DRAGON_BREATH), ModPotionUtil.setLingeringPotion(ModPotions.CLIMBING.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(ModPotionUtil.setSplashPotion(ModPotions.LONG_CLIMBING.get())), Ingredient.of(Items.DRAGON_BREATH), ModPotionUtil.setLingeringPotion(ModPotions.LONG_CLIMBING.get())));
    }

    private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(ModEntityType.APOSTLE.get(), Apostle.setCustomAttributes().build());
        event.put(ModEntityType.OBSIDIAN_MONOLITH.get(), ObsidianMonolith.setCustomAttributes().build());
        event.put(ModEntityType.WARLOCK.get(), Warlock.setCustomAttributes().build());
        event.put(ModEntityType.WARTLING.get(), Wartling.setCustomAttributes().build());
        event.put(ModEntityType.CRONE.get(), Crone.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinBruteServant.setCustomAttributes().build());
        event.put(ModEntityType.MALGHAST.get(), Malghast.setCustomAttributes().build());
        event.put(ModEntityType.INFERNO.get(), Inferno.setCustomAttributes().build());
        event.put(ModEntityType.DAMNED.get(), Damned.setCustomAttributes().build());
        event.put(ModEntityType.VAMPIRE_BAT.get(), VampireBat.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH.get(), Wraith.setCustomAttributes().build());
        event.put(ModEntityType.BORDER_WRAITH.get(), BorderWraith.setCustomAttributes().build());
        event.put(ModEntityType.CRYPT_SLIME.get(), CryptSlime.setCustomAttributes().build());
        event.put(ModEntityType.BONE_SPIDER.get(), BoneSpider.setCustomAttributes().build());
        event.put(ModEntityType.CAIRN_NECROMANCER.get(), CairnNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR.get(), HauntedArmor.setCustomAttributes().build());
        event.put(ModEntityType.ALLY_VEX.get(), AllyVex.setCustomAttributes().build());
        event.put(ModEntityType.ALLY_IRK.get(), AllyIrk.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.HUSK_SERVANT.get(), HuskServant.setCustomAttributes().build());
        event.put(ModEntityType.DROWNED_SERVANT.get(), DrownedServant.setCustomAttributes().build());
        event.put(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), FrozenZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), JungleZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_SERVANT.get(), SkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.STRAY_SERVANT.get(), StrayServant.setCustomAttributes().build());
        event.put(ModEntityType.WITHER_SKELETON_SERVANT.get(), WitherSkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.MOSSY_SKELETON_SERVANT.get(), MossySkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), SunkenSkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.NECROMANCER_SERVANT.get(), NecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.CAIRN_NECROMANCER_SERVANT.get(), CairnNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH_SERVANT.get(), WraithServant.setCustomAttributes().build());
        event.put(ModEntityType.BORDER_WRAITH_SERVANT.get(), BorderWraithServant.setCustomAttributes().build());
        event.put(ModEntityType.VANGUARD_SERVANT.get(), VanguardServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), SkeletonPillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), ZombieVindicatorServant.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_EVOKER.get(), BoundEvoker.setCustomAttributes().build());
        event.put(ModEntityType.BOUND_ICEOLOGER.get(), BoundIceologer.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), HauntedArmorServant.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_SKULL.get(), HauntedSkull.setCustomAttributes().build());
        event.put(ModEntityType.DOPPELGANGER.get(), Doppelganger.setCustomAttributes().build());
        event.put(ModEntityType.MINI_GHAST.get(), MiniGhast.setCustomAttributes().build());
        event.put(ModEntityType.GHAST_SERVANT.get(), GhastServant.setCustomAttributes().build());
        event.put(ModEntityType.BLAZE_SERVANT.get(), BlazeServant.setCustomAttributes().build());
        event.put(ModEntityType.SLIME_SERVANT.get(), SlimeServant.setCustomAttributes().build());
        event.put(ModEntityType.MAGMA_CUBE_SERVANT.get(), MagmaCubeServant.setCustomAttributes().build());
        event.put(ModEntityType.CRYPT_SLIME_SERVANT.get(), CryptSlimeServant.setCustomAttributes().build());
        event.put(ModEntityType.SPIDER_SERVANT.get(), SpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.CAVE_SPIDER_SERVANT.get(), CaveSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.WEB_SPIDER_SERVANT.get(), WebSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.ICY_SPIDER_SERVANT.get(), IcySpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.BONE_SPIDER_SERVANT.get(), BoneSpiderServant.setCustomAttributes().build());
        event.put(ModEntityType.RAVAGED.get(), Ravaged.setCustomAttributes().build());
        event.put(ModEntityType.MOD_RAVAGER.get(), ModRavager.setCustomAttributes().build());
        event.put(ModEntityType.ARMORED_RAVAGER.get(), Ravager.createAttributes().build());
        event.put(ModEntityType.ZOMBIE_RAVAGER.get(), ZombieRavager.setCustomAttributes().build());
        event.put(ModEntityType.BLACK_WOLF.get(), BlackWolf.setCustomAttributes().build());
        event.put(ModEntityType.BEAR_SERVANT.get(), BearServant.setCustomAttributes().build());
        event.put(ModEntityType.POLAR_BEAR_SERVANT.get(), BearServant.setCustomAttributes().build());
        event.put(ModEntityType.HOGLIN_SERVANT.get(), HoglinServant.setCustomAttributes().build());
        event.put(ModEntityType.BLACK_BEAST.get(), BlackBeast.setCustomAttributes().build());
        event.put(ModEntityType.WHISPERER.get(), Whisperer.setCustomAttributes().build());
        event.put(ModEntityType.WAVEWHISPERER.get(), Wavewhisperer.setCustomAttributes().build());
        event.put(ModEntityType.LEAPLEAF.get(), Leapleaf.setCustomAttributes().build());
        event.put(ModEntityType.ICE_GOLEM.get(), IceGolem.setCustomAttributes().build());
        event.put(ModEntityType.SQUALL_GOLEM.get(), SquallGolem.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_GOLEM.get(), RedstoneGolem.setCustomAttributes().build());
        event.put(ModEntityType.GRAVE_GOLEM.get(), GraveGolem.setCustomAttributes().build());
        event.put(ModEntityType.HAUNT.get(), Haunt.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_MONSTROSITY.get(), RedstoneMonstrosity.setCustomAttributes().build());
        event.put(ModEntityType.REDSTONE_CUBE.get(), RedstoneCube.setCustomAttributes().build());
        event.put(ModEntityType.TOTEMIC_WALL.get(), TotemicWall.setCustomAttributes().build());
        event.put(ModEntityType.TOTEMIC_BOMB.get(), TotemicBomb.setCustomAttributes().build());
        event.put(ModEntityType.GLACIAL_WALL.get(), GlacialWall.setCustomAttributes().build());
        event.put(ModEntityType.QUICK_GROWING_VINE.get(), QuickGrowingVine.setCustomAttributes().build());
        event.put(ModEntityType.QUICK_GROWING_KELP.get(), QuickGrowingKelp.setCustomAttributes().build());
        event.put(ModEntityType.POISON_QUILL_VINE.get(), PoisonQuillVine.setCustomAttributes().build());
        event.put(ModEntityType.POISON_ANEMONE.get(), PoisonAnemone.setCustomAttributes().build());
        event.put(ModEntityType.INSECT_SWARM.get(), InsectSwarm.setCustomAttributes().build());
        event.put(ModEntityType.VOLCANO.get(), Volcano.setCustomAttributes().build());
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
        event.put(ModEntityType.VIZIER.get(), Vizier.setCustomAttributes().build());
        event.put(ModEntityType.VIZIER_CLONE.get(), VizierClone.setCustomAttributes().build());
        event.put(ModEntityType.IRK.get(), Irk.setCustomAttributes().build());
        event.put(ModEntityType.SKULL_LORD.get(), SkullLord.setCustomAttributes().build());
        event.put(ModEntityType.BONE_LORD.get(), BoneLord.setCustomAttributes().build());
        event.put(ModEntityType.WITHER_NECROMANCER.get(), WitherNecromancer.setCustomAttributes().build());
        event.put(ModEntityType.LASER.get(), SkullLaser.setCustomAttributes().build());
        event.put(ModEntityType.SURVEY_EYE.get(), Mob.createMobAttributes().build());
        event.put(ModEntityType.TUNNELING_FANG.get(), TunnelingFang.setCustomAttributes().build());
        event.put(ModEntityType.HAUNTED_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    private void SpawnPlacementEvent(SpawnPlacementRegisterEvent event){
        event.register(ModEntityType.WARLOCK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.BORDER_WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.CRYPT_SLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CryptSlime::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.CAIRN_NECROMANCER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityType.HAUNTED_ARMOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HANDS.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().build());
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

    public static final CreativeModeTab TAB = new CreativeModeTab("goety") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.TOTEM_OF_SOULS.get());
        }
    };
}
