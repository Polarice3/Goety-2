package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.gui.overlay.RavagerRoarGui;
import com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui;
import com.Polarice3.Goety.client.gui.screen.inventory.DarkAnvilScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusBagScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.SoulItemScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.WandandBagScreen;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.client.render.*;
import com.Polarice3.Goety.client.render.block.*;
import com.Polarice3.Goety.client.render.layer.PlayerSoulArmorLayer;
import com.Polarice3.Goety.client.render.layer.PlayerSoulShieldLayer;
import com.Polarice3.Goety.client.render.layer.PlayerSpellShieldLayer;
import com.Polarice3.Goety.client.render.model.*;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModChestBlock;
import com.Polarice3.Goety.common.blocks.ModWoodType;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.items.FlameCaptureItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.RecallFocus;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitEvents {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event){
        MenuScreens.register(ModContainerType.WAND.get(), SoulItemScreen::new);
        MenuScreens.register(ModContainerType.FOCUS_BAG.get(), FocusBagScreen::new);
        MenuScreens.register(ModContainerType.WAND_AND_BAG.get(), WandandBagScreen::new);
        MenuScreens.register(ModContainerType.DARK_ANVIL.get(), DarkAnvilScreen::new);
        CuriosRenderer.register();
        ModKeybindings.init();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(BossBarEvent::renderBossBar);
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModWoodType.HAUNTED);
            Sheets.addWoodType(ModWoodType.ROTTEN);
        });

        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("souls"),
                (stack, world, living, seed) -> ((float) TotemOfSouls.currentSouls(stack)) / TotemOfSouls.maximumSouls(stack));
        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("activated"),
                (stack, world, living, seed) -> TotemOfSouls.isActivated(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.FLAME_CAPTURE.get(), new ResourceLocation("capture"),
                (stack, world, living, seed) -> FlameCaptureItem.hasEntity(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.HUNTERS_BOW.get(), new ResourceLocation("pull"),
                (stack, world, living, seed) -> {
                    if (living == null) {
                        return 0.0F;
                    } else {
                        return living.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration() - living.getUseItemRemainingTicks()) / 20;
                    }
                });
        ItemProperties.register(ModItems.HUNTERS_BOW.get(), new ResourceLocation("pulling")
                , (stack, world, living, seed) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.RECALL_FOCUS.get(), new ResourceLocation("active")
                , (stack, world, living, seed) -> RecallFocus.hasRecall(stack) ? 1.0F : 0.0F);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event){
        PlayerRenderer playerRenderer = event.getSkin("default");
        if (playerRenderer != null){
            playerRenderer.addLayer(new PlayerSoulArmorLayer(playerRenderer, event.getEntityModels()));
            playerRenderer.addLayer(new PlayerSoulShieldLayer(playerRenderer, event.getEntityModels()));
            playerRenderer.addLayer(new PlayerSpellShieldLayer(playerRenderer, event.getEntityModels()));
        }
    }

    @SubscribeEvent
    public static void registerGUI(final RegisterGuiOverlaysEvent event){
        event.registerAboveAll("soul_energy_hud", SoulEnergyGui.OVERLAY);
        event.registerAboveAll("ravager_roar_hud", RavagerRoarGui.OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModBlockLayer.ARCA, ArcaRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.TALL_SKULL, TallSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPIKE, SpikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_BOUQUET, IceBouquetModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_CHUNK, IceChunkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINISTER_TOOTH, MinisterToothModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOLT, SoulBoltModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLAST_FUNGUS, BlastFungusModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUMMON_CIRCLE, SummonCircleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FIRE_TORNADO, FireTornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MONOLITH, MonolithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLOCK, BlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WARLOCK, WarlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRONE, CroneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE, ApostleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZOMBIE_VILLAGER_SERVANT, VillagerServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SKELETON_VILLAGER_SERVANT, SkeletonVillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGED, RavagedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER, ModRavagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER_ARMOR, ModRavagerModel::createArmorLayer);
        event.registerLayerDefinition(ModModelLayer.ZPIGLIN_SERVANT, ZPiglinModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MALGHAST, ModGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WRAITH, WraithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECROMANCER, NecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TORMENTOR, TormentorModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.INQUILLAGER, InquillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CONQUILLAGER, ConquillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER, VizierModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.IRK, IrkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINION, MinionModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNTED_SKULL, HauntedSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SKULL_LORD, SkullLordModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER_ARMOR, VizierModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_HAT, DarkHatModel::createDarkHatLayer);
        event.registerLayerDefinition(ModModelLayer.GRAND_TURBAN, DarkHatModel::createGrandTurbanLayer);
        event.registerLayerDefinition(ModModelLayer.WITCH_HAT, WitchHatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRONE_HAT, WitchHatModel::createCroneLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ROBE, DarkRobeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECRO_CROWN, NecroCapeModel::createHeadLayer);
        event.registerLayerDefinition(ModModelLayer.NECRO_CAPE, NecroCapeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECRO_SET, NecroCapeModel::createNecromancerLayer);
        event.registerLayerDefinition(ModModelLayer.NAMELESS_CROWN, NecroCapeModel::createBigHeadLayer);
        event.registerLayerDefinition(ModModelLayer.NAMELESS_SET, NecroCapeModel::createNamelessLayer);
        event.registerLayerDefinition(ModModelLayer.GLOVE, GloveModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FOCUS_BAG, MiscCuriosModel::createFocusBagLayer);
        event.registerLayerDefinition(ModModelLayer.AMULET, MiscCuriosModel::createAmuletLayer);
        event.registerLayerDefinition(ModModelLayer.AMETHYST_NECKLACE, MiscCuriosModel::createAmethystNecklaceLayer);
        event.registerLayerDefinition(ModModelLayer.BELT, MiscCuriosModel::createBeltLayer);
        event.registerLayerDefinition(ModModelLayer.MONOCLE, MiscCuriosModel::createMonocleLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_INNER, VillagerArmorModel::createInnerArmorLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_OUTER, VillagerArmorModel::createOuterArmorLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_KNIGHT_ARMOR_INNER, CursedKnightArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_KNIGHT_ARMOR_OUTER, CursedKnightArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ARMOR_INNER, DarkArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ARMOR_OUTER, DarkArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_SHIELD, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.5F), false), 64, 64));
        event.registerLayerDefinition(ModModelLayer.SOUL_ARMOR, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.3F), false), 64, 64));

        LayerDefinition layerdefinition18 = BoatModel.createBodyModel(false);
        LayerDefinition layerdefinition19 = BoatModel.createBodyModel(true);

        for(ModBoat.Type boat$type : ModBoat.Type.values()) {
            event.registerLayerDefinition(ModModelLayer.createBoatModelName(boat$type), () -> layerdefinition18);
            event.registerLayerDefinition(ModModelLayer.createChestBoatModelName(boat$type), () ->  layerdefinition19);
        }
    }

    @SubscribeEvent
    public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        event.registerBlockEntityRenderer(ModBlockEntities.ARCA.get(), ArcaRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CURSED_INFUSER.get(), CursedInfuserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CURSED_CAGE.get(), CursedCageRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DARK_ALTAR.get(), DarkAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL.get(), PedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOUL_ABSORBER.get(), SoulAbsorberRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOUL_MENDER.get(), SoulMenderRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ICE_BOUQUET_TRAP.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCULK_DEVOURER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FORBIDDEN_GRASS.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAGIC_LIGHT.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_BELL.get(), HookBellRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NECRO_BRAZIER.get(), NecroBrazierRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BREWING_CAULDRON.get(), BrewCauldronRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NIGHT_BEACON.get(), NightBeaconRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TALL_SKULL.get(), TallSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_TRAPPED_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SIGN_BLOCK_ENTITIES.get(), SignRenderer::new);
        event.registerEntityRenderer(ModEntityType.NETHER_METEOR.get(), NetherMeteorRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_FIREBALL.get(),(rendererManager) -> new ThrownItemRenderer<>(rendererManager, 0.75F, true));
        event.registerEntityRenderer(ModEntityType.LAVABALL.get(),(rendererManager) -> new ThrownItemRenderer<>(rendererManager, 3.0F, true));
        event.registerEntityRenderer(ModEntityType.SWORD.get(), (rendererManager) -> new SwordProjectileRenderer<>(rendererManager, itemRenderer, 1.25F, true));
        event.registerEntityRenderer(ModEntityType.ICE_SPIKE.get(), IceSpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCYTHE.get(), ScytheSlashRenderer::new);
        event.registerEntityRenderer(ModEntityType.GRAND_LAVABALL.get(), GrandLavaballRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL_SHOT.get(), HauntedSkullProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLOW_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BULLET.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOLT.get(), SoulBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECRO_BOLT.get(), NecroBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.FANG.get(), FangsRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIKE.get(), SpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_BOUQUET.get(), IceBouquetRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_CHUNK.get(), IceChunkRenderer::new);
        event.registerEntityRenderer(ModEntityType.MINISTER_TOOTH.get(), MinisterToothRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNAP_FUNGUS.get(), SnapFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLAST_FUNGUS.get(), BlastFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BERSERK_FUNGUS.get(), BerserkFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_CIRCLE.get(), SummonCircleRenderer::new);
        event.registerEntityRenderer(ModEntityType.OBSIDIAN_MONOLITH.get(), ObsidianMonolithRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_WALL.get(), TotemicWallRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_BOMB.get(), TotemicBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO.get(), FireTornadoRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_GAS.get(), BrewGasRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_BOAT.get(), (render) -> new ModBoatRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MOD_CHEST_BOAT.get(), (render) -> new ModBoatRenderer(render, true));
        event.registerEntityRenderer(ModEntityType.WARLOCK.get(), WarlockRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARTLING.get(), WartlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRONE.get(), CroneRenderer::new);
        event.registerEntityRenderer(ModEntityType.APOSTLE.get(), ApostleRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), ZombieVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.MALGHAST.get(), MalghastRenderer::new);
        event.registerEntityRenderer(ModEntityType.VAMPIRE_BAT.get(), VampireBatRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH.get(), WraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.BORDER_WRAITH.get(), BorderWraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAIRN_NECROMANCER.get(), CairnNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLY_VEX.get(), AllyVexRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLY_IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HUSK_SERVANT.get(), HuskServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.DROWNED_SERVANT.get(), DrownedServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.STRAY_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECROMANCER_SERVANT.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH_SERVANT.get(), WraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL.get(), HauntedSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.DOPPELGANGER.get(), (render) -> new DoppelgangerRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.RAVAGED.get(), RavagedRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARMORED_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_RAVAGER.get(), ZombieRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENVIOKER.get(), EnviokerRenderer::new);
        event.registerEntityRenderer(ModEntityType.TORMENTOR.get(), TormentorRenderer::new);
        event.registerEntityRenderer(ModEntityType.INQUILLAGER.get(), InquillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.CONQUILLAGER.get(), ConquillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.VIZIER.get(), VizierRenderer::new);
        event.registerEntityRenderer(ModEntityType.IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKULL_LORD.get(), SkullLordRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_LORD.get(), BoneLordRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARROW_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_BLAST_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.UPDRAFT_BLAST.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_UTIL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_APOSTLE.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FROST_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LASER.get(), TrapRenderer::new);
    }

    @SubscribeEvent
    public static void colorBlock(RegisterColorHandlersEvent.Block event){
        event.register(
                (state, lightReader, pos, color) ->
                        lightReader != null && pos != null ?
                                Minecraft.getInstance().level != null
                                && Minecraft.getInstance().level.getBlockEntity(pos) instanceof BrewCauldronBlockEntity cauldronBlock
                                        ? cauldronBlock.getColor() :
                                BiomeColors.getAverageWaterColor(lightReader, pos) : -1, ModBlocks.BREWING_CAULDRON.get());
        event.register(
                (state, lightReader, pos, color) ->
                        lightReader != null && pos != null ?
                                BiomeColors.getAverageFoliageColor(lightReader, pos) :
                                FoliageColor.getDefaultColor(), ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get());
    }

    @SubscribeEvent
    public static void colorItem(RegisterColorHandlersEvent.Item event){
        event.register((itemStack, i) -> i > 0 ? -1 : PotionUtils.getColor(itemStack),
                ModItems.BREW.get(), ModItems.SPLASH_BREW.get(), ModItems.LINGERING_BREW.get(), ModItems.GAS_BREW.get());
        event.register((itemStack, i) -> {
            BlockState blockstate = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, i);
        }, ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get());
    }

    @SubscribeEvent
    public static void textureStitching(TextureStitchEvent.Pre event){
        if (event.getAtlas().location() == Sheets.CHEST_SHEET) {
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block ->
            {
                if (block instanceof ModChestBlock){
                    ModChestRenderer.stitchChests(event, block);
                }
            });
        }
    }

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.register(ModParticleTypes.NONE.get(), NoneParticle.Provider::new);
        event.register(ModParticleTypes.TOTEM_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.PLAGUE_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.WHITE_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.BULLET_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.NECRO_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.GLOW_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.WARLOCK.get(), SpellParticle.WitchProvider::new);
        event.register(ModParticleTypes.LEECH.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.ELECTRIC.get(), GlowParticle.ElectricSparkProvider::new);
        event.register(ModParticleTypes.BREW_BUBBLE.get(), BrewBubbleParticle.Provider::new);
        event.register(ModParticleTypes.WIND_BLAST.get(), SonicBoomParticle.Provider::new);
        event.register(ModParticleTypes.HEAL_EFFECT.get(), HeartParticle.Provider::new);
        event.register(ModParticleTypes.SOUL_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.register(ModParticleTypes.GLOW_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.register(ModParticleTypes.LASER_GATHER.get(), GatheringParticle.Provider::new);
        event.register(ModParticleTypes.BURNING.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.SOUL_EXPLODE_BITS.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.CULT_SPELL.get(), SpellParticle.MobProvider::new);
        event.register(ModParticleTypes.CONFUSED.get(), HeartParticle.Provider::new);
        event.register(ModParticleTypes.WRAITH.get(), WraithParticle.Provider::new);
        event.register(ModParticleTypes.WRAITH_BURST.get(), WraithParticle.Provider::new);
        event.register(ModParticleTypes.WRAITH_FIRE.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.BIG_FIRE.get(), FireParticle.Provider::new);
        event.register(ModParticleTypes.NECRO_FIRE.get(), FireParticle.Provider::new);
        event.register(ModParticleTypes.SMALL_NECRO_FIRE.get(), FireParticle.SmallProvider::new);
        event.register(ModParticleTypes.NECRO_FLAME.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.SPELL_CLOUD.get(), FireParticle.ColorProvider::new);
        event.register(ModParticleTypes.FUNGUS_EXPLOSION.get(), HugeExplosionParticle.Provider::new);
        event.register(ModParticleTypes.FUNGUS_EXPLOSION_EMITTER.get(), new HugeFungusExplosionSeedParticle.Provider());
        event.register(ModParticleTypes.SOUL_EXPLODE.get(), SoulExplodeParticle.Provider::new);
        event.register(ModParticleTypes.SUMMON.get(), SoulExplodeParticle.SummonProvider::new);
        event.register(ModParticleTypes.SHOCKWAVE.get(), ShockwaveParticle.Provider::new);
        event.register(ModParticleTypes.SOUL_SHOCKWAVE.get(), ShockwaveParticle.Provider::new);
        event.register(ModParticleTypes.PORTAL_SHOCKWAVE.get(), ShockwaveParticle.Provider::new);
        event.register(ModParticleTypes.SHOUT.get(), ShoutParticle.RedProvider::new);
        event.register(ModParticleTypes.SCULK_BUBBLE.get(), SculkBubbleParticle.Provider::new);
    }

}
