package com.Polarice3.Goety;

import com.Polarice3.Goety.client.ClientProxy;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.CommonProxy;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModWoodType;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.effects.ModEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.Irk;
import com.Polarice3.Goety.common.entities.hostile.Wraith;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.servants.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModSpawnEggs;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.ritual.ModRituals;
import com.Polarice3.Goety.common.world.ModMobSpawnBiomeModifier;
import com.Polarice3.Goety.compat.curios.CuriosCompat;
import com.Polarice3.Goety.init.ModProxy;
import com.Polarice3.Goety.init.ModSounds;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
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
    public static final ModProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

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
        ModRituals.RITUALS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setupEntityAttributeCreation);
        modEventBus.addListener(this::enqueueIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.SPEC, "goety.toml");
        MainConfig.loadConfig(MainConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC, "goety-attributes.toml");
        AttributesConfig.loadConfig(AttributesConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpellConfig.SPEC, "goety-spells.toml");
        SpellConfig.loadConfig(SpellConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("goety-spells.toml").toString());

        final DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Goety.MOD_ID);
        biomeModifiers.register(modEventBus);
        biomeModifiers.register("mob_spawns", ModMobSpawnBiomeModifier::makeCodec);

        MinecraftForge.EVENT_BUS.register(this);
        ModItems.init();
        ModBlocks.init();
        ModRecipeSerializer.init();
        ModSpawnEggs.init();
        ModEffects.init();
        ModSounds.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.init();

        CuriosCompat.setup(event);
        event.enqueueWork(() -> {
            SpawnPlacement();
            DispenserBlock.registerBehavior(ModBlocks.TALL_SKULL_ITEM.get(), new OptionalDispenseItemBehavior() {
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    this.setSuccess(ArmorItem.dispenseArmor(source, stack));
                    return stack;
                }
            });
            AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.put(ModBlocks.HAUNTED_LOG.get(), ModBlocks.STRIPPED_HAUNTED_LOG.get());
            AxeItem.STRIPPABLES.put(ModBlocks.HAUNTED_WOOD.get(), ModBlocks.STRIPPED_HAUNTED_WOOD.get());
            WoodType.register(ModWoodType.HAUNTED);
        });
    }

    public static void SpawnPlacement(){
        SpawnPlacements.register(ModEntityType.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Owned::checkHostileSpawnRules);
    }

    private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(ModEntityType.APOSTLE.get(), Apostle.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), ZombieVillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinServant.setCustomAttributes().build());
        event.put(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinBruteServant.setCustomAttributes().build());
        event.put(ModEntityType.MALGHAST.get(), Malghast.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH.get(), Wraith.setCustomAttributes().build());
        event.put(ModEntityType.ALLY_VEX.get(), AllyVex.setCustomAttributes().build());
        event.put(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServant.setCustomAttributes().build());
        event.put(ModEntityType.HUSK_SERVANT.get(), HuskServant.setCustomAttributes().build());
        event.put(ModEntityType.DROWNED_SERVANT.get(), DrownedServant.setCustomAttributes().build());
        event.put(ModEntityType.SKELETON_SERVANT.get(), SkeletonServant.setCustomAttributes().build());
        event.put(ModEntityType.STRAY_SERVANT.get(), StrayServant.setCustomAttributes().build());
        event.put(ModEntityType.WRAITH_SERVANT.get(), WraithServant.setCustomAttributes().build());
        event.put(ModEntityType.DOPPELGANGER.get(), Doppelganger.setCustomAttributes().build());
        event.put(ModEntityType.VIZIER.get(), Vizier.setCustomAttributes().build());
        event.put(ModEntityType.IRK.get(), Irk.setCustomAttributes().build());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
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

    public static final CreativeModeTab TAB = new CreativeModeTab("goetyTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.TOTEM_OF_SOULS.get());
        }
    };
}
