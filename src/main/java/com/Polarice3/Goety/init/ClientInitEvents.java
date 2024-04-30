package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.gui.overlay.CurrentFocusGui;
import com.Polarice3.Goety.client.gui.overlay.RavagerRoarGui;
import com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui;
import com.Polarice3.Goety.client.gui.screen.inventory.*;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.client.render.*;
import com.Polarice3.Goety.client.render.block.*;
import com.Polarice3.Goety.client.render.layer.*;
import com.Polarice3.Goety.client.render.model.*;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ModWoodType;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.items.FlameCaptureItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.CallFocus;
import com.Polarice3.Goety.common.items.magic.RecallFocus;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitEvents {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event){
        MenuScreens.register(ModContainerType.WAND.get(), SoulItemScreen::new);
        MenuScreens.register(ModContainerType.FOCUS_BAG.get(), FocusBagScreen::new);
        MenuScreens.register(ModContainerType.FOCUS_PACK.get(), FocusPackScreen::new);
        MenuScreens.register(ModContainerType.BREW_BAG.get(), BrewBagScreen::new);
        MenuScreens.register(ModContainerType.DARK_ANVIL.get(), DarkAnvilScreen::new);
        CuriosRenderer.register();
        ModKeybindings.init();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(BossBarEvent::renderBossBar);
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModWoodType.HAUNTED);
            Sheets.addWoodType(ModWoodType.ROTTEN);
            Sheets.addWoodType(ModWoodType.WINDSWEPT);
        });

        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("souls"),
                (stack, world, living, seed) -> ((float) ITotem.currentSouls(stack)) / ITotem.maximumSouls(stack));
        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("activated"),
                (stack, world, living, seed) -> TotemOfSouls.isActivated(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.FLAME_CAPTURE.get(), new ResourceLocation("capture"),
                (stack, world, living, seed) -> FlameCaptureItem.hasEntity(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.TAGLOCK_KIT.get(), new ResourceLocation("tagged"),
                (stack, world, living, seed) -> TaglockKit.hasEntity(stack) ? 1.0F : 0.0F);
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
        ItemProperties.register(ModItems.CALL_FOCUS.get(), new ResourceLocation("active")
                , (stack, world, living, seed) -> CallFocus.hasSummon(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.RECALL_FOCUS.get(), new ResourceLocation("active")
                , (stack, world, living, seed) -> RecallFocus.hasRecall(stack) ? 1.0F : 0.0F);
    }

    /**
     * Ripped from @TeamTwilight's AddLayer codes: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.x/src/main/java/twilightforest/client/TFClientSetup.java">...</a>
     */
    @Nullable
    private static Field fieldEntityRenderer;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void addLayers(EntityRenderersEvent.AddLayers event){
        if (fieldEntityRenderer == null) {
            try {
                fieldEntityRenderer = EntityRenderersEvent.AddLayers.class.getDeclaredField("renderers");
                fieldEntityRenderer.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (fieldEntityRenderer != null) {
            event.getSkins().forEach(skin -> {
                LivingEntityRenderer<Player, EntityModel<Player>> livingEntityRenderer = event.getSkin(skin);
                if (livingEntityRenderer != null) {
                    addPlayerLayers(livingEntityRenderer, event.getEntityModels());
                }
            });
            try {
                ((Map<EntityType<?>, EntityRenderer<?>>) fieldEntityRenderer.get(event)).values().stream().
                        filter(LivingEntityRenderer.class::isInstance).map(LivingEntityRenderer.class::cast).forEach(ClientInitEvents::addLivingLayer);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLivingLayer(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new FreezeLayer<>(renderer));
        renderer.addLayer(new MagicShieldLayer<>(renderer));
    }

    private static void addPlayerLayers(LivingEntityRenderer<Player, EntityModel<Player>> renderer, EntityModelSet entityModelSet) {
        renderer.addLayer(new FreezeLayer<>(renderer));
        renderer.addLayer(new MagicShieldLayer<>(renderer));
        renderer.addLayer(new PlayerSoulArmorLayer<>(renderer, entityModelSet));
        renderer.addLayer(new PlayerSoulShieldLayer<>(renderer, entityModelSet));
        renderer.addLayer(new PlayerSpellShieldLayer<>(renderer, entityModelSet));
    }

    @SubscribeEvent
    public static void registerGUI(final RegisterGuiOverlaysEvent event){
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "soul_energy_hud", SoulEnergyGui.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "ravager_roar_hud", RavagerRoarGui.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "current_focus_hud", CurrentFocusGui.OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModBlockLayer.ARCA, ArcaRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.TALL_SKULL, TallSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.REDSTONE_GOLEM_SKULL, RedstoneGolemSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPIKE, SpikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HARPOON, HarpoonModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.POISON_QUILL, PoisonQuillModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_BOUQUET, IceBouquetModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_CHUNK, IceChunkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VICIOUS_TOOTH, ViciousToothModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VICIOUS_PIKE, ViciousPikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOLT, SoulBoltModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HELL_BLAST, HellBlastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SCATTER_MINE, ScatterMineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLAST_FUNGUS, BlastFungusModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WEB_SHOT, WebShotModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOMB, SoulBombModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUMMON_CIRCLE, SummonCircleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ENTANGLE_VINES, EntangleVinesModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FIRE_TORNADO, FireTornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MONOLITH, MonolithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.QUICK_GROWING_VINE, QuickGrowingVineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.POISON_QUILL_VINE, PoisonQuillVineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VOLCANO, VolcanoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLOCK, BlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WARLOCK, WarlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRONE, CroneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE, ApostleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE_SHADE, ApostleShadeRenderer.ApostleShadeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZOMBIE_VILLAGER_SERVANT, VillagerServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SKELETON_VILLAGER_SERVANT, SkeletonVillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BOUND_ILLAGER, BoundIllagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGED, RavagedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER, ModRavagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER_ARMOR, ModRavagerModel::createArmorLayer);
        event.registerLayerDefinition(ModModelLayer.WHISPERER, WhispererModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.LEAPLEAF, LeapleafModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_GOLEM, IceGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SQUALL_GOLEM, SquallGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_GOLEM, RedstoneGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GRAVE_GOLEM, GraveGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNT, HauntModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_MONSTROSITY, RedstoneMonstrosityModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_CUBE, RedstoneCubeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZPIGLIN_SERVANT, ZPiglinModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MALGHAST, ModGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.INFERNO, InfernoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINI_GHAST, MiniGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MAGMA_CUBE, MagmaCubeServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MOD_SPIDER, ModSpiderModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WRAITH, WraithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUNKEN_SKELETON, SunkenSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECROMANCER, NecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WITHER_NECROMANCER, WitherNecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VANGUARD, VanguardModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TORMENTOR, TormentorModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.INQUILLAGER, InquillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CONQUILLAGER, ConquillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.PIKER, PikerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RIPPER, RipperModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TRAMPLER, TramplerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRUSHER, CrusherModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.STORM_CASTER, StormCasterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRYOLOGER, CryologerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.PREACHER, PreacherModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINISTER, MinisterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER, VizierModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER_CLONE, VizierCloneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.IRK, IrkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINION, MinionModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNTED_SKULL, HauntedSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNTED_SKULL_FIRELESS, HauntedSkullModel::createFirelessLayer);
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
        event.registerLayerDefinition(ModModelLayer.LICH, () -> LayerDefinition.create(LichModeModel.createMesh(CubeDeformation.NONE), 64, 64));
        event.registerLayerDefinition(ModModelLayer.GLOVE, GloveModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FOCUS_BAG, MiscCuriosModel::createFocusBagLayer);
        event.registerLayerDefinition(ModModelLayer.BREW_BAG, MiscCuriosModel::createBrewBagLayer);
        event.registerLayerDefinition(ModModelLayer.AMULET, MiscCuriosModel::createAmuletLayer);
        event.registerLayerDefinition(ModModelLayer.AMETHYST_NECKLACE, MiscCuriosModel::createAmethystNecklaceLayer);
        event.registerLayerDefinition(ModModelLayer.BELT, MiscCuriosModel::createBeltLayer);
        event.registerLayerDefinition(ModModelLayer.MONOCLE, MiscCuriosModel::createMonocleLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_INNER, VillagerArmorModel::createInnerArmorLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_OUTER, VillagerArmorModel::createOuterArmorLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_KNIGHT_ARMOR_INNER, CursedKnightArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_KNIGHT_ARMOR_OUTER, CursedKnightArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_PALADIN_ARMOR_INNER, CursedPaladinArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.CURSED_PALADIN_ARMOR_OUTER, CursedPaladinArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.BLACK_IRON_ARMOR_INNER, BlackIronArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.BLACK_IRON_ARMOR_OUTER, BlackIronArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ARMOR_INNER, DarkArmorModel::createInnerLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ARMOR_OUTER, DarkArmorModel::createOuterLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_SHIELD, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.5F), false), 64, 64));
        event.registerLayerDefinition(ModModelLayer.SOUL_ARMOR, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.3F), false), 64, 64));
        event.registerLayerDefinition(ModModelLayer.HAUNTED_ARMOR_STAND, HauntedArmorStandModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAS_INNER, () -> HauntedArmorStandArmorModel.createBodyLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ModModelLayer.HAS_OUTER, () -> HauntedArmorStandArmorModel.createBodyLayer(new CubeDeformation(1.0F)));
        event.registerLayerDefinition(ModModelLayer.SMALL_PAINTING, HauntedPaintingModel::createSmallFrameLayer);
        event.registerLayerDefinition(ModModelLayer.MEDIUM_PAINTING, HauntedPaintingModel::createMediumFrameLayer);
        event.registerLayerDefinition(ModModelLayer.LARGE_PAINTING, HauntedPaintingModel::createLargeFrameLayer);
        event.registerLayerDefinition(ModModelLayer.TALL_PAINTING, HauntedPaintingModel::createTallFrameLayer);
        event.registerLayerDefinition(ModModelLayer.WIDE_PAINTING, HauntedPaintingModel::createWideFrameLayer);

        for(ModBoat.Type boatType : ModBoat.Type.values()) {
            event.registerLayerDefinition(ModBoatRenderer.createBoatModelName(boatType), BoatModel::createBodyModel);
            event.registerLayerDefinition(ModBoatRenderer.createChestBoatModelName(boatType), ChestBoatModel::createBodyModel);
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
        event.registerBlockEntityRenderer(ModBlockEntities.WIND_BLOWER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESONANCE_CRYSTAL.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCULK_DEVOURER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FORBIDDEN_GRASS.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAGIC_LIGHT.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_BELL.get(), HookBellRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NECRO_BRAZIER.get(), NecroBrazierRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BREWING_CAULDRON.get(), BrewCauldronRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SPIDER_NEST.get(), TrainingBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOLE.get(), HoleBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NIGHT_BEACON.get(), NightBeaconRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TALL_SKULL.get(), TallSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_GOLEM_SKULL.get(), RedstoneGolemSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_TRAPPED_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SIGN_BLOCK_ENTITIES.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HANGING_SIGN_BLOCK_ENTITIES.get(), HangingSignRenderer::new);
        event.registerEntityRenderer(ModEntityType.NETHER_METEOR.get(), NetherMeteorRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_FIREBALL.get(),(rendererManager) -> new ThrownItemRenderer<>(rendererManager, 0.75F, true));
        event.registerEntityRenderer(ModEntityType.LAVABALL.get(),(rendererManager) -> new ThrownItemRenderer<>(rendererManager, 3.0F, true));
        event.registerEntityRenderer(ModEntityType.HELL_BOLT.get(), HellBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELL_BLAST.get(), HellBlastRenderer::new);
        event.registerEntityRenderer(ModEntityType.SWORD.get(), (rendererManager) -> new SwordProjectileRenderer<>(rendererManager, itemRenderer, 1.25F, true));
        event.registerEntityRenderer(ModEntityType.ICE_SPIKE.get(), IceSpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_SPEAR.get(), IceSpearRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_STORM.get(), IceStormRenderer::new);
        event.registerEntityRenderer(ModEntityType.GHOST_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntityType.DEATH_ARROW.get(), DeathArrowRenderer::new);
        event.registerEntityRenderer(ModEntityType.HARPOON.get(), HarpoonRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_QUILL.get(), PoisonQuillRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_SHARD.get(), (rendererManager) -> new BoneShardRenderer<>(rendererManager, itemRenderer));
        event.registerEntityRenderer(ModEntityType.BREW.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCYTHE.get(), ScytheSlashRenderer::new);
        event.registerEntityRenderer(ModEntityType.GRAND_LAVABALL.get(), GrandLavaballRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_DRAGON_FIREBALL.get(), ModDragonFireballRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL_SHOT.get(), HauntedSkullProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_WITHER_SKULL.get(), ModWitherSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLOW_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BULLET.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOLT.get(), SoulBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.STEAM_MISSILE.get(), SteamMissileRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECRO_BOLT.get(), NecroBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_BOLT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.FANG.get(), FangsRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIKE.get(), SpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ILL_BOMB.get(), IllBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.ELECTRO_ORB.get(), ElectroOrbRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_BOUQUET.get(), IceBouquetRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELLFIRE.get(), HellfireRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_CHUNK.get(), IceChunkRenderer::new);
        event.registerEntityRenderer(ModEntityType.VICIOUS_TOOTH.get(), ViciousToothRenderer::new);
        event.registerEntityRenderer(ModEntityType.VICIOUS_PIKE.get(), ViciousPikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.CORRUPTED_BEAM.get(), CorruptedBeamRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCATTER_MINE.get(), ScatterMineRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCATTER_BOMB.get(), ScatterBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOMB.get(), SoulBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNAP_FUNGUS.get(), SnapFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLAST_FUNGUS.get(), BlastFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BERSERK_FUNGUS.get(), BerserkFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.PYROCLAST.get(), PyroclastRenderer::new);
        event.registerEntityRenderer(ModEntityType.WEB_SHOT.get(), WebShotRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_CIRCLE.get(), SummonCircleRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_FIERY.get(), SummonCircleVariantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENTANGLE_VINES.get(), EntangleVinesRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIDER_WEB.get(), SpiderWebRenderer::new);
        event.registerEntityRenderer(ModEntityType.OBSIDIAN_MONOLITH.get(), ObsidianMonolithRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_WALL.get(), TotemicWallRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_BOMB.get(), TotemicBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLACIAL_WALL.get(), GlacialWallRenderer::new);
        event.registerEntityRenderer(ModEntityType.QUICK_GROWING_VINE.get(), QuickGrowingVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.QUICK_GROWING_KELP.get(), QuickGrowingVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_QUILL_VINE.get(), PoisonQuillVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_ANEMONE.get(), PoisonQuillVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.INSECT_SWARM.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOLCANO.get(), VolcanoRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO.get(), FireTornadoRenderer::new);
        event.registerEntityRenderer(ModEntityType.FALLING_BLOCK.get(), ModFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_GAS.get(), BrewGasRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_BOAT.get(), (render) -> new ModBoatRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MOD_CHEST_BOAT.get(), (render) -> new ModBoatRenderer(render, true));
        event.registerEntityRenderer(ModEntityType.MOD_PAINTING.get(), HauntedPaintingRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR_STAND.get(), HauntedArmorStandRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARLOCK.get(), WarlockRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARTLING.get(), WartlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRONE.get(), CroneRenderer::new);
        event.registerEntityRenderer(ModEntityType.APOSTLE.get(), ApostleRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.MALGHAST.get(), MalghastRenderer::new);
        event.registerEntityRenderer(ModEntityType.INFERNO.get(), InfernoRenderer::new);
        event.registerEntityRenderer(ModEntityType.VAMPIRE_BAT.get(), VampireBatRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH.get(), WraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.BORDER_WRAITH.get(), BorderWraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYPT_SLIME.get(), CryptSlimeRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAIRN_NECROMANCER.get(), CairnNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLY_VEX.get(), AllyVexRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLY_IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HUSK_SERVANT.get(), HuskServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.DROWNED_SERVANT.get(), DrownedServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), FrozenZombieRenderer::new);
        event.registerEntityRenderer(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), JungleZombieRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.STRAY_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOSSY_SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), SunkenSkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECROMANCER_SERVANT.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH_SERVANT.get(), WraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.VANGUARD_SERVANT.get(), VanguardRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_PILLAGER.get(), SkeletonPillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_VINDICATOR.get(), ZombieVindicatorRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_EVOKER.get(), BoundEvokerRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL.get(), HauntedSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.DOPPELGANGER.get(), (render) -> new DoppelgangerRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MINI_GHAST.get(), MiniGhastRenderer::new);
        event.registerEntityRenderer(ModEntityType.SLIME_SERVANT.get(), SlimeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGMA_CUBE_SERVANT.get(), MagmaCubeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIDER_SERVANT.get(), SpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAVE_SPIDER_SERVANT.get(), CaveSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WEB_SPIDER_SERVANT.get(), WebSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_SPIDER_SERVANT.get(), BoneSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.RAVAGED.get(), RavagedRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARMORED_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_RAVAGER.get(), ZombieRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.WHISPERER.get(), WhispererRenderer::new);
        event.registerEntityRenderer(ModEntityType.WAVEWHISPERER.get(), WhispererRenderer::new);
        event.registerEntityRenderer(ModEntityType.LEAPLEAF.get(), LeapleafRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_GOLEM.get(), IceGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.SQUALL_GOLEM.get(), SquallGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_GOLEM.get(), RedstoneGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.GRAVE_GOLEM.get(), GraveGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNT.get(), HauntRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_MONSTROSITY.get(), RedstoneMonstrosityRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_CUBE.get(), RedstoneCubeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENVIOKER.get(), EnviokerRenderer::new);
        event.registerEntityRenderer(ModEntityType.TORMENTOR.get(), TormentorRenderer::new);
        event.registerEntityRenderer(ModEntityType.INQUILLAGER.get(), InquillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.CONQUILLAGER.get(), ConquillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.PIKER.get(), PikerRenderer::new);
        event.registerEntityRenderer(ModEntityType.RIPPER.get(), RipperRenderer::new);
        event.registerEntityRenderer(ModEntityType.TRAMPLER.get(), TramplerRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRUSHER.get(), CrusherRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_CASTER.get(), StormCasterRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYOLOGER.get(), CryologerRenderer::new);
        event.registerEntityRenderer(ModEntityType.PREACHER.get(), PreacherRenderer::new);
        event.registerEntityRenderer(ModEntityType.MINISTER.get(), MinisterRenderer::new);
        event.registerEntityRenderer(ModEntityType.HOSTILE_REDSTONE_GOLEM.get(), HostileRedstoneGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.VIZIER.get(), VizierRenderer::new);
        event.registerEntityRenderer(ModEntityType.VIZIER_CLONE.get(), VizierCloneRenderer::new);
        event.registerEntityRenderer(ModEntityType.IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKULL_LORD.get(), SkullLordRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_LORD.get(), BoneLordRenderer::new);
//        event.registerEntityRenderer(ModEntityType.WITHER_NECROMANCER.get(), WitherNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARROW_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_BLAST_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.UPDRAFT_BLAST.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.CUSHION.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_GROUND.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.ACID_POOL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_UTIL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_APOSTLE.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAIL_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.MONSOON_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LASER.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.VINE_HOOK.get(), VineHookRenderer::new);
        event.registerEntityRenderer(ModEntityType.SURVEY_EYE.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAMERA_SHAKE.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.TUNNELING_FANG.get(), TrapRenderer::new);
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
                                FoliageColor.getDefaultColor(), ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get(), ModBlocks.WINDSWEPT_LEAVES.get());
    }

    @SubscribeEvent
    public static void colorItem(RegisterColorHandlersEvent.Item event){
        event.register((itemStack, i) -> i > 0 ? -1 : PotionUtils.getColor(itemStack),
                ModItems.BREW.get(), ModItems.SPLASH_BREW.get(), ModItems.LINGERING_BREW.get(), ModItems.GAS_BREW.get());
        event.register((itemStack, i) -> {
            BlockState blockstate = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, i);
        }, ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get(), ModBlocks.WINDSWEPT_LEAVES.get());
    }

    @SubscribeEvent
    public static void modelBake(ModelEvent.ModifyBakingResult event) {
        List<Map.Entry<ResourceLocation, BakedModel>> models =  event.getModels().entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(Goety.MOD_ID) && entry.getKey().getPath().contains("leaves") && !entry.getKey().getPath().contains("dark")).toList();

        models.forEach(entry -> event.getModels().put(entry.getKey(), new BakedLeavesModel(entry.getValue())));
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event){
        event.register(MagicShieldLayer.SHIELD);
    }

    @SubscribeEvent
    public static void registerRecipeBookCategory(RegisterRecipeBookCategoriesEvent event){
        event.registerRecipeCategoryFinder(ModRecipeSerializer.CURSED_INFUSER.get(), recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ModRecipeSerializer.SOUL_ABSORBER.get(), recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ModRecipeSerializer.RITUAL_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ModRecipeSerializer.BRAZIER_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ModRecipeSerializer.BREWING_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(ModRecipeSerializer.PULVERIZE_TYPE.get(), recipe -> RecipeBookCategories.UNKNOWN);
    }

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.NONE.get(), NoneParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TOTEM_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.PLAGUE_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WHITE_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BULLET_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.NECRO_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.GLOW_EFFECT.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LICH.get(), BigSpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WARLOCK.get(), SpellParticle.WitchProvider::new);
        event.registerSpriteSet(ModParticleTypes.BONE.get(), ShortFlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LEECH.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.ELECTRIC.get(), GlowParticle.ElectricSparkProvider::new);
        event.registerSpriteSet(ModParticleTypes.BIG_ELECTRIC.get(), BigElectricParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BREW_BUBBLE.get(), BrewBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WIND_BLAST.get(), SonicBoomParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.HEAL_EFFECT.get(), HeartParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.HEAL_EFFECT_2.get(), SoulExplodeParticle.SummonProvider::new);
        event.registerSpriteSet(ModParticleTypes.SOUL_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.GLOW_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LASER_GATHER.get(), GatheringParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.RESONANCE_GATHER.get(), GatheringParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BURNING.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SOUL_EXPLODE_BITS.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.CULT_SPELL.get(), SpellParticle.MobProvider::new);
        event.registerSpriteSet(ModParticleTypes.BIG_CULT_SPELL.get(), BigSpellParticle.MobProvider::new);
        event.registerSpriteSet(ModParticleTypes.CONFUSED.get(), HeartParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WRAITH.get(), WraithParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WRAITH_BURST.get(), WraithParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WRAITH_FIRE.get(), BreathParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BIG_FIRE.get(), FireParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BIG_FIRE_DROP.get(), FireParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BIG_FIRE_GROUND.get(), FireParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.NECRO_FIRE.get(), FireParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.NECRO_FIRE_DROP.get(), FireParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SMALL_NECRO_FIRE.get(), FireParticle.SmallProvider::new);
        event.registerSpriteSet(ModParticleTypes.NECRO_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.DRAGON_FLAME.get(), FireParticle.DragonProvider::new);
        event.registerSpriteSet(ModParticleTypes.DRAGON_FLAME_DROP.get(), FireParticle.EmberProvider::new);
        event.registerSpriteSet(ModParticleTypes.FROST.get(), FireParticle.FrostProvider::new);
        event.registerSpriteSet(ModParticleTypes.FROST_NOVA.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FLY.get(), FireParticle.FlyProvider::new);
        event.registerSpriteSet(ModParticleTypes.SPELL_CLOUD.get(), FireParticle.ColorProvider::new);
        event.registerSpriteSet(ModParticleTypes.FANG_RAIN.get(), WaterDropParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.REDSTONE_EXPLODE.get(), RedstoneExplodeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.MINE_PULSE.get(), PulsatingCircleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.ELECTRIC_EXPLODE.get(), ElectricExplosionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FAN_CLOUD.get(), FanCloudParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.MAGIC_BOLT.get(), RollingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.NECRO_BOLT.get(), RollingParticle.QuickProvider::new);
        event.registerSpriteSet(ModParticleTypes.STUN.get(), RollingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FUNGUS_EXPLOSION.get(), HugeExplosionParticle.Provider::new);
        event.registerSpecial(ModParticleTypes.FUNGUS_EXPLOSION_EMITTER.get(), new HugeFungusExplosionSeedParticle.Provider());
        event.registerSpriteSet(ModParticleTypes.SOUL_EXPLODE.get(), SoulExplodeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SUMMON.get(), SummonParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SPELL_SQUARE.get(), SpellSquareParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TRAIL.get(), TrailParticle.MobProvider::new);
        event.registerSpriteSet(ModParticleTypes.SPARKLE.get(), SparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.DUST_CLOUD.get(), DustCloudParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SHOCKWAVE.get(), ShockwaveParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.REVERSE_SHOCKWAVE.get(), ShockwaveParticle.ReverseProvider::new);
        event.registerSpriteSet(ModParticleTypes.CIRCLE_EXPLODE.get(), CircleExplodeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FOG_CLOUD.get(), FoggyCloudParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SOUL_HEAL.get(), RisingCircleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SCULK_BUBBLE.get(), SculkBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FAST_DUST.get(), FastFallDust.Provider::new);
    }

}
