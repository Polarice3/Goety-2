package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.IPersistDecorator;
import com.Polarice3.Goety.api.items.magic.ITotem;
import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.gui.overlay.CurrentFocusGui;
import com.Polarice3.Goety.client.gui.overlay.DreadOverlay;
import com.Polarice3.Goety.client.gui.overlay.RavagerRoarGui;
import com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui;
import com.Polarice3.Goety.client.gui.screen.inventory.*;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
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
import com.Polarice3.Goety.common.items.ArcaCompassItem;
import com.Polarice3.Goety.common.items.FlameCaptureItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.curios.OminousCharmItem;
import com.Polarice3.Goety.common.items.magic.*;
import com.Polarice3.Goety.common.items.revive.SoulJar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.ForgeRegistries;

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
        MenuScreens.register(ModContainerType.CRAFTING_FOCUS.get(), CraftingScreen::new);
        CuriosRenderer.register();
        ModKeybindings.init();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(BossBarEvent::renderBossBar);
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModWoodType.HAUNTED);
            Sheets.addWoodType(ModWoodType.ROTTEN);
            Sheets.addWoodType(ModWoodType.WINDSWEPT);
            Sheets.addWoodType(ModWoodType.PINE);

            ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("souls"),
                    (stack, world, living, seed) -> ((float) ITotem.currentSouls(stack)) / ITotem.maximumSouls(stack));
            ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("activated"),
                    (stack, world, living, seed) -> TotemOfSouls.isActivated(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.FLAME_CAPTURE.get(), new ResourceLocation("capture"),
                    (stack, world, living, seed) -> FlameCaptureItem.hasEntity(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.SOUL_JAR.get(), new ResourceLocation("type"),
                    (stack, world, living, seed) -> SoulJar.isDrowned(stack) ? 1.0F : SoulJar.isWither(stack) ? 2.0F : SoulJar.isCairn(stack) ? 3.0F : SoulJar.isMossy(stack) ? 4.0F : 0.0F);
            ItemProperties.register(ModItems.TAGLOCK_KIT.get(), new ResourceLocation("tagged"),
                    (stack, world, living, seed) -> TaglockKit.hasEntity(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.WAYSTONE.get(), new ResourceLocation("store"),
                    (stack, world, living, seed) -> WaystoneItem.hasBlock(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.TRANSFER_SCROLL.get(), new ResourceLocation("signed"),
                    (stack, world, living, seed) -> TransferScroll.hasSummon(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.ARCA_COMPASS.get(), new ResourceLocation("angle")
                    , new CompassItemPropertyFunction((p_234992_, p_234993_, p_234994_) -> {
                        return ArcaCompassItem.getArcaPosition(p_234993_.getOrCreateTag());
                    }));
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
            /*ItemProperties.register(ModItems.REVOLVER_CROSSBOW.get(), new ResourceLocation("pull")
                    , (stack, world, living, seed) -> {
                if (living == null) {
                    return 0.0F;
                } else {
                    return RevolverCrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getUseDuration() - living.getUseItemRemainingTicks()) / (float)RevolverCrossbowItem.getChargeDuration(stack);
                }
            });
            ItemProperties.register(ModItems.REVOLVER_CROSSBOW.get(), new ResourceLocation("pulling")
                    , (stack, world, living, seed) -> living != null && living.isUsingItem() && living.getUseItem() == stack && !RevolverCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.REVOLVER_CROSSBOW.get(), new ResourceLocation("charged")
                    , (stack, world, living, seed) -> RevolverCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.REVOLVER_CROSSBOW.get(), new ResourceLocation("firework")
                    , (stack, world, living, seed) -> RevolverCrossbowItem.isCharged(stack) && RevolverCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);*/
            ItemProperties.register(ModItems.CALL_FOCUS.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> CallFocus.hasSummon(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.TROOP_FOCUS.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> TroopFocus.hasSummonType(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.RECALL_FOCUS.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> RecallFocus.hasRecall(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.INFERNAL_TOME.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> living != null && living.isUsingItem() && (living.getUseItem() == stack || InfernalTome.isChanting(stack)) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.OMINOUS_CHARM.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> OminousCharmItem.hasOmen(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.COMMAND_HORN.get(), new ResourceLocation("mode")
                    , (stack, world, living, seed) -> {
                        if (CommandHorn.isWander(stack)) {
                            return 1.0F;
                        } else if (CommandHorn.isStandBy(stack)) {
                            return 2.0F;
                        } else if (CommandHorn.isGuard(stack)) {
                            return 3.0F;
                        } else if (CommandHorn.isFollow(stack)) {
                            return 4.0F;
                        }
                        return 0.0F;
                    });
            ItemProperties.register(ModItems.ESOTERIC_TESSERACT.get(), new ResourceLocation("active")
                    , (stack, world, living, seed) -> EsotericTesseract.getServantsInTesseract(stack) > 0 ? 1.0F : 0.0F);
        });
    }

    /*private static void copyOldArtIfMissing() {
        File dir = new File(".", "resourcepacks");
        File target = new File(dir, "Goety Old Textures.zip");

        if(!target.exists())
            try {
                dir.mkdirs();
                InputStream in = Goety.class.getResourceAsStream("/assets/goety/old_textures.zip");
                FileOutputStream out = new FileOutputStream(target);

                byte[] buf = new byte[16384];
                int len;
                if (in != null) {
                    while ((len = in.read(buf)) > 0)
                        out.write(buf, 0, len);

                    in.close();
                }
                out.close();
            } catch (IOException ignored) {

            }
    }*/

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
        event.registerAbove(VanillaGuiOverlay.PLAYER_LIST.id(), "static_overlay", DreadOverlay.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "soul_energy_hud", SoulEnergyGui.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "ravager_roar_hud", RavagerRoarGui.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "current_focus_hud", CurrentFocusGui.OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModBlockLayer.ARCA, ArcaRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.TALL_SKULL, TallSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.REDSTONE_GOLEM_SKULL, RedstoneGolemSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.GRAVE_GOLEM_SKULL, GraveGolemSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.REDSTONE_MONSTROSITY_HEAD, RedstoneMonstrosityHeadModel::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.LOFTY_CHEST, LoftyChestRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.BLACK_CRYSTAL, BlackCrystalRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.PLUSHIE, PlushieModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPIKE, SpikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HARPOON, HarpoonModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.POISON_QUILL, PoisonQuillModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_BOUQUET, IceBouquetModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_CHUNK, IceChunkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VICIOUS_TOOTH, ViciousToothModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VICIOUS_PIKE, ViciousPikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GULF_TENTACLE, GulfTentacleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.EARTH_FIST, EarthFistModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOLT, SoulBoltModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SHIELD_DEBRIS, ShieldDebrisModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HELL_BLAST, HellBlastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SCREAM, HellChantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VOID_SHOCK, VoidShockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VOID_SHOCK_BOMB, VoidShockBombModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SCATTER_MINE, ScatterMineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLAST_FUNGUS, BlastFungusModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WEB_SHOT, WebShotModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOMB, SoulBombModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUMMON_CIRCLE, SummonCircleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUMMON_CIRCLE_BOSS, SummonCircleBossModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ENTANGLE_VINES, EntangleVinesModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FIRE_TORNADO, CycloneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TIDAL_SURGE, TidalSurgeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MONOLITH, MonolithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.QUICK_GROWING_VINE, QuickGrowingVineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.POISON_QUILL_VINE, PoisonQuillVineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BIOMINE, BioMineModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPIDER_EGG, SpiderEggModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VOLCANO, VolcanoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TRIDENT_STORM, TridentStormModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLOCK, BlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WARLOCK, WarlockModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HERETIC, HereticModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MAVERICK, MaverickModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.CRONE, CroneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MOD_WITCH, ModWitchModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE, ApostleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE_SHADE, ApostleShadeRenderer.ApostleShadeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZOMBIE_VILLAGER_SERVANT, VillagerServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SKELETON_VILLAGER_SERVANT, SkeletonVillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BOUND_ILLAGER, BoundIllagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BOUND_ILLAGER_ANIMATED, BoundIllagerAnimatedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DAMNED, DamnedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DAMNED_HUMAN, DamnedModel::createHumanLayer);
        event.registerLayerDefinition(ModModelLayer.ILLAGER_SERVANT, IllagerServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VINDICATOR_CHEF, VindicatorChefModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MOUNTAINEER, MountaineerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GEOMANCER, GeomancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICEOLOGER, IceologerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WIND_CALLER, WindCallerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.PRISONER, PrisonerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGED, RavagedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER, ModRavagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.RAVAGER_ARMOR, ModRavagerModel::createArmorLayer);
        event.registerLayerDefinition(ModModelLayer.BLACK_WOLF, BlackWolfModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BEAR, BearServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SNAPPER, SnapperModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GNASHER, GnasherModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLACK_BEAST, BlackBeastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BEAST_HEAD, BeastHeadModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WHISPERER, WhispererModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.LEAPLEAF, LeapleafModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINISTROSITY, MinistrosityModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_GOLEM, IceGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SQUALL_GOLEM, SquallGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_GOLEM, RedstoneGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GRAVE_GOLEM, GraveGolemModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNT, HauntModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_MONSTROSITY, RedstoneMonstrosityModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REDSTONE_CUBE, RedstoneCubeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WATCHLING, WatchlingModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLASTLING, BlastlingModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SNARELING, SnarelingModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ENDERSENT, EndersentModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ENDER_KEEPER, EnderKeeperModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ENDER_KEEPER_SHADOW, EnderKeeperModel::createShadowLayer);
        event.registerLayerDefinition(ModModelLayer.ZPIGLIN_SERVANT, ZPiglinModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MALGHAST, ModGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.INFERNO, InfernoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WILDFIRE, WildfireModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINI_GHAST, MiniGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MAGMA_CUBE, MagmaCubeServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TROPICAL_SLIME_OUTER, TropicalSlimeModel::createOuterBodyLayer);
        event.registerLayerDefinition(ModModelLayer.TROPICAL_SLIME_INNER, TropicalSlimeModel::createInnerBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MOD_SPIDER, ModSpiderModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICY_SPIDER, ModSpiderModel::createIcyBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WEB_SPIDER, WebSpiderModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BROOD_MOTHER, BroodMotherModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPECTER, SpecterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.REAPER, ReaperModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WRAITH, WraithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUNKEN_SKELETON, SunkenSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECROMANCER, NecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DROWNED_NECROMANCER, DrownedNecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WITHER_NECROMANCER, WitherNecromancerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VANGUARD, VanguardModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.BLACKGUARD, BlackguardModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WIGHT, WightModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MAGGOT, CarrionMaggotModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FLY, CarrionFlyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SORCERER, SorcererModel::createBodyLayer);
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
        event.registerLayerDefinition(ModModelLayer.UNHOLY_HAT, UnholyHatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.IRON_CROWN, DarkHatModel::createIronCrownLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ROBE, DarkRobeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GRAND_ROBE, DarkRobeModel::createGrandRobeLayer);
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
        event.registerLayerDefinition(ModModelLayer.NAMELESS_STAFF, NamelessStaffModel::createBodyLayer);
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
        event.registerBlockEntityRenderer(ModBlockEntities.GRIM_INFUSER.get(), GrimInfuserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CURSED_CAGE.get(), CursedCageRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DARK_ALTAR.get(), DarkAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL.get(), PedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOUL_ABSORBER.get(), SoulAbsorberRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOUL_MENDER.get(), SoulMenderRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ICE_BOUQUET_TRAP.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WIND_BLOWER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESONANCE_CRYSTAL.get(), ResonanceCrystalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCULK_DEVOURER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FORBIDDEN_GRASS.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAGIC_LIGHT.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_BELL.get(), HookBellRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHRIEKING_OBELISK.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NECRO_BRAZIER.get(), NecroBrazierRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANIMATOR.get(), AnimatorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLACK_CRYSTAL.get(), BlackCrystalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BREWING_CAULDRON.get(), BrewCauldronRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HAUNTED_MIRROR.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HAUNTED_JUG.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SPIDER_NEST.get(), TrainingBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHADE_GRAVESTONE.get(), TrainingBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BLAZING_CAGE.get(), TrainingBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OMINOUS_PYRE.get(), BarracksBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OMINOUS_IDOL.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SPIDER_MOTHER_DEN.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_SPAWNER.get(), VoidSpawnerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_VAULT.get(), VoidVaultRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_FRAME.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_SHRINE.get(), VoidShrineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOLE.get(), HoleBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PART_LIQUID.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NIGHT_BEACON.get(), NightBeaconRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_BARREL.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TALL_SKULL.get(), TallSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_GOLEM_SKULL.get(), RedstoneGolemSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.GRAVE_GOLEM_SKULL.get(), GraveGolemSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_MONSTROSITY_HEAD.get(), RedstoneMonstrosityHeadBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_TRAPPED_CHEST.get(), ModChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRYPT_CHEST.get(), CryptChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.LOFTY_CHEST.get(), LoftyChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SIGN_BLOCK_ENTITIES.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HANGING_SIGN_BLOCK_ENTITIES.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntityType.NETHER_METEOR.get(), NetherMeteorRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_FIREBALL.get(), (rendererManager) -> new ModFireballRenderer<>(rendererManager, 0.75F, true));
        event.registerEntityRenderer(ModEntityType.LAVABALL.get(), (rendererManager) -> new ModFireballRenderer<>(rendererManager, 3.0F, true));
        event.registerEntityRenderer(ModEntityType.HELL_BOLT.get(), HellBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELL_BLAST.get(), HellBlastRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELL_CHANT.get(), HellChantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SWORD.get(), (rendererManager) -> new SwordProjectileRenderer<>(rendererManager, itemRenderer, 1.25F, true));
        event.registerEntityRenderer(ModEntityType.ICE_SPIKE.get(), IceSpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_SPEAR.get(), IceSpearRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_STORM.get(), IceStormRenderer::new);
        event.registerEntityRenderer(ModEntityType.GHOST_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntityType.RAIN_ARROW.get(), RainArrowRenderer::new);
        event.registerEntityRenderer(ModEntityType.DEATH_ARROW.get(), DeathArrowRenderer::new);
        event.registerEntityRenderer(ModEntityType.HARPOON.get(), HarpoonRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_QUILL.get(), PoisonQuillRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_SHARD.get(), (rendererManager) -> new BoneShardRenderer<>(rendererManager, itemRenderer));
        event.registerEntityRenderer(ModEntityType.BREW.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCYTHE.get(), ScytheSlashRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_DRAGON_FIREBALL.get(), ModDragonFireballRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL_SHOT.get(), HauntedSkullProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_WITHER_SKULL.get(), ModWitherSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLOW_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BULLET.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOLT.get(), SoulBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_BOLT.get(), PoisonBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.STEAM_MISSILE.get(), SteamMissileRenderer::new);
        event.registerEntityRenderer(ModEntityType.WITHER_BOLT.get(), WitherBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECRO_BOLT.get(), NecroBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_BOLT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SHIELD_DEBRIS.get(), ShieldDebrisRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOID_SHOCK.get(), VoidShockRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOID_SHOCK_BOMB.get(), VoidShockBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.FANG.get(), FangsRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIKE.get(), SpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ILL_BOMB.get(), IllBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYPTIC_EYE.get(), (rendererManager) -> new ThrownItemRenderer<>(rendererManager, 1.0F, true));
        event.registerEntityRenderer(ModEntityType.VOID_EYE.get(), (rendererManager) -> new ThrownItemRenderer<>(rendererManager, 1.0F, true));
        event.registerEntityRenderer(ModEntityType.FLYING_ITEM.get(), (rendererManager) -> new ThrownItemRenderer<>(rendererManager, 1.0F, true));
        event.registerEntityRenderer(ModEntityType.ELECTRO_ORB.get(), ElectroOrbRenderer::new);
        event.registerEntityRenderer(ModEntityType.SURGING_ORB.get(), SurgingOrbRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUNCY_BUBBLE.get(), BouncyBubbleRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_BOUQUET.get(), IceBouquetRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_FIRE.get(), MagicFireRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELLFIRE.get(), HellfireRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_CHUNK.get(), IceChunkRenderer::new);
        event.registerEntityRenderer(ModEntityType.VICIOUS_TOOTH.get(), ViciousToothRenderer::new);
        event.registerEntityRenderer(ModEntityType.VICIOUS_PIKE.get(), ViciousPikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.EARTH_FIST.get(), EarthFistRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLOSSOM_THORN.get(), BlossomThornRenderer::new);
        event.registerEntityRenderer(ModEntityType.CORRUPTED_BEAM.get(), CorruptedBeamRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCATTER_MINE.get(), ScatterMineRenderer::new);
        event.registerEntityRenderer(ModEntityType.SCATTER_BOMB.get(), ScatterBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOMB.get(), SoulBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNAP_FUNGUS.get(), SnapFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLAST_FUNGUS.get(), BlastFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.BERSERK_FUNGUS.get(), BerserkFungusRenderer::new);
        event.registerEntityRenderer(ModEntityType.PYROCLAST.get(), PyroclastRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGMA_BOMB.get(), MagmaBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLOSSOM_BALL.get(), BlossomBallRenderer::new);
        event.registerEntityRenderer(ModEntityType.WEB_SHOT.get(), WebShotRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNARELING_SHOT.get(), SnarelingShotRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENDER_GOO.get(), EnderGooRenderer::new);
        event.registerEntityRenderer(ModEntityType.TRIDENT_STORM.get(), TridentStormRenderer::new);
        event.registerEntityRenderer(ModEntityType.DELAYED_SUMMON.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_CIRCLE.get(), SummonCircleRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_CIRCLE_BOSS.get(), SummonCircleBossRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_FIERY.get(), SummonCircleVariantRenderer::new);
        event.registerEntityRenderer(ModEntityType.RAID_BOSS_SUMMON.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENTANGLE_VINES.get(), EntangleVinesRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIDER_WEB.get(), SpiderWebRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNARELING_GOOP.get(), SnarelingGoopRenderer::new);
        event.registerEntityRenderer(ModEntityType.OBSIDIAN_MONOLITH.get(), ObsidianMonolithRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_WALL.get(), TotemicWallRenderer::new);
        event.registerEntityRenderer(ModEntityType.TOTEMIC_BOMB.get(), TotemicBombRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLACIAL_WALL.get(), GlacialWallRenderer::new);
        event.registerEntityRenderer(ModEntityType.QUICK_GROWING_VINE.get(), QuickGrowingVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.QUICK_GROWING_KELP.get(), QuickGrowingVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_QUILL_VINE.get(), PoisonQuillVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.POISON_ANEMONE.get(), PoisonQuillVineRenderer::new);
        event.registerEntityRenderer(ModEntityType.BIOMINE.get(), BioMineRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIDER_EGG.get(), SpiderEggRenderer::new);
        event.registerEntityRenderer(ModEntityType.INSECT_SWARM.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.BEAST_HEAD.get(), BeastHeadRenderer::new);
        event.registerEntityRenderer(ModEntityType.GULF_TENTACLE.get(), GulfTentacleRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOLCANO.get(), VolcanoRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO.get(), FireTornadoRenderer::new);
        event.registerEntityRenderer(ModEntityType.CYCLONE.get(), CycloneRenderer::new);
        event.registerEntityRenderer(ModEntityType.TIDAL_SURGE.get(), TidalSurgeRenderer::new);
        event.registerEntityRenderer(ModEntityType.RAZOR_WIND.get(), RazorWindRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOID_SLASH.get(), VoidSlashRenderer::new);
        event.registerEntityRenderer(ModEntityType.FALLING_BLOCK.get(), ModFallingBlockRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_GAS.get(), BrewGasRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_BOAT.get(), (render) -> new ModBoatRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MOD_CHEST_BOAT.get(), (render) -> new ModBoatRenderer(render, true));
        event.registerEntityRenderer(ModEntityType.MOD_PAINTING.get(), HauntedPaintingRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR_STAND.get(), HauntedArmorStandRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARLOCK.get(), WarlockRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARTLING.get(), WartlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.HERETIC.get(), HereticRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAVERICK.get(), MaverickRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRONE.get(), CroneRenderer::new);
        event.registerEntityRenderer(ModEntityType.APOSTLE.get(), ApostleRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.MALGHAST.get(), MalghastRenderer::new);
        event.registerEntityRenderer(ModEntityType.INFERNO.get(), InfernoRenderer::new);
        event.registerEntityRenderer(ModEntityType.DAMNED.get(), DamnedRenderer::new);
        event.registerEntityRenderer(ModEntityType.VAMPIRE_BAT.get(), VampireBatRenderer::new);
        event.registerEntityRenderer(ModEntityType.HOSTILE_BLACK_WOLF.get(), BlackWolfRenderer::new);
        event.registerEntityRenderer(ModEntityType.FRAYED.get(), FrayedRenderer::new);
        event.registerEntityRenderer(ModEntityType.RATTLED.get(), RattledRenderer::new);
        event.registerEntityRenderer(ModEntityType.REAPER.get(), ReaperRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH.get(), WraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.BORDER_WRAITH.get(), BorderWraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.MUCK_WRAITH.get(), MuckWraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYPT_SLIME.get(), CryptSlimeRenderer::new);
        event.registerEntityRenderer(ModEntityType.WEB_SPIDER.get(), WebSpiderRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICY_SPIDER.get(), IcySpiderRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_SPIDER.get(), BoneSpiderRenderer::new);
        event.registerEntityRenderer(ModEntityType.BROOD_MOTHER.get(), BroodMotherRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECROMANCER.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAIRN_NECROMANCER.get(), AbstractCairnNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOSSY_NECROMANCER.get(), MossyNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.WATCHLING.get(), WatchlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLASTLING.get(), BlastlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNARELING.get(), SnarelingRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENDERSENT.get(), EndersentRenderer::new);
        event.registerEntityRenderer(ModEntityType.ENDER_KEEPER.get(), EnderKeeperRenderer::new);
        event.registerEntityRenderer(ModEntityType.VEX_SERVANT.get(), AllyVexRenderer::new);
        event.registerEntityRenderer(ModEntityType.IRK_SERVANT.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), ZombieVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HUSK_SERVANT.get(), HuskServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.DROWNED_SERVANT.get(), DrownedServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.FROZEN_ZOMBIE_SERVANT.get(), FrozenZombieRenderer::new);
        event.registerEntityRenderer(ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(), JungleZombieRenderer::new);
        event.registerEntityRenderer(ModEntityType.FRAYED_SERVANT.get(), FrayedServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLACKGUARD_SERVANT.get(), BlackguardRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.STRAY_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WITHER_SKELETON_SERVANT.get(), WitherSkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOSSY_SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), SunkenSkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.RATTLED_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.NECROMANCER_SERVANT.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAIRN_NECROMANCER_SERVANT.get(), AbstractCairnNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOSSY_NECROMANCER_SERVANT.get(), MossyNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.DROWNED_NECROMANCER_SERVANT.get(), DrownedNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.WITHER_NECROMANCER_SERVANT.get(), WitherNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.REAPER_SERVANT.get(), ReaperRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH_SERVANT.get(), WraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BORDER_WRAITH_SERVANT.get(), BorderWraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MUCK_WRAITH_SERVANT.get(), MuckWraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.PHANTOM_SERVANT.get(), PhantomServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.VANGUARD_SERVANT.get(), VanguardRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_PILLAGER_SERVANT.get(), SkeletonPillagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(), ZombieVindicatorRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_EVOKER.get(), BoundEvokerRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_GEOMANCER.get(), BoundGeomancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_ICEOLOGER.get(), BoundIceologerRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_CRYOLOGER.get(), BoundCryologerRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_WIND_CALLER.get(), BoundWindCallerRenderer::new);
        event.registerEntityRenderer(ModEntityType.BOUND_STORM_CASTER.get(), BoundStormCasterRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), HauntedArmorRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL.get(), HauntedSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.DOPPELGANGER.get(), (render) -> new DoppelgangerRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MINI_GHAST.get(), MiniGhastRenderer::new);
        event.registerEntityRenderer(ModEntityType.GHAST_SERVANT.get(), GhastServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLAZE_SERVANT.get(), BlazeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WILDFIRE.get(), WildfireRenderer::new);
        event.registerEntityRenderer(ModEntityType.SLIME_SERVANT.get(), SlimeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGMA_CUBE_SERVANT.get(), MagmaCubeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYPT_SLIME_SERVANT.get(), CryptSlimeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.TROPICAL_SLIME_SERVANT.get(), TropicalSlimeServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIDER_SERVANT.get(), SpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAVE_SPIDER_SERVANT.get(), CaveSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WEB_SPIDER_SERVANT.get(), WebSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICY_SPIDER_SERVANT.get(), IcySpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_SPIDER_SERVANT.get(), BoneSpiderServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BROOD_MOTHER_SERVANT.get(), BroodMotherRenderer::new);
        event.registerEntityRenderer(ModEntityType.PRISONER.get(), PrisonerRenderer::new);
        event.registerEntityRenderer(ModEntityType.NEOLLAGER.get(), NeollagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.PILLAGER_SERVANT.get(), PillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.PIKER_SERVANT.get(), PikerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SIGNALER_SERVANT.get(), SignalerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.VINDICATOR_SERVANT.get(), VindicatorServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.VINDICATOR_CHEF_SERVANT.get(), VindicatorChefServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOUNTAINEER_SERVANT.get(), MountaineerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRUSHER_SERVANT.get(), CrusherServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.EVOKER_SERVANT.get(), EvokerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.GEOMANCER_SERVANT.get(), GeomancerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICEOLOGER_SERVANT.get(), IceologerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.CRYOLOGER_SERVANT.get(), CryologerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WIND_CALLER_SERVANT.get(), WindCallerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_CASTER_SERVANT.get(), StormCasterServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.RIPPER_SERVANT.get(), RipperServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.TRAMPLER_SERVANT.get(), AllyTramplerRenderer::new);
        event.registerEntityRenderer(ModEntityType.RAVAGED.get(), RavagedRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARMORED_RAVAGER.get(), ModRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_RAVAGER.get(), ZombieRavagerRenderer::new);
        event.registerEntityRenderer(ModEntityType.WITCH_SERVANT.get(), WitchServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WARLOCK_SERVANT.get(), WarlockServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HERETIC_SERVANT.get(), HereticServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAVERICK_SERVANT.get(), MaverickServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLACK_WOLF.get(), BlackWolfRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_WOLF.get(), SkeletonWolfRenderer::new);
        event.registerEntityRenderer(ModEntityType.WINTER_WOLF.get(), WinterWolfRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORMHOUND.get(), StormhoundRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELLHOUND.get(), HellhoundRenderer::new);
        event.registerEntityRenderer(ModEntityType.TWILIGHT_GOAT.get(), TwilightGoatRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNAPPER.get(), SnapperRenderer::new);
        event.registerEntityRenderer(ModEntityType.GNASHER.get(), GnasherRenderer::new);
        event.registerEntityRenderer(ModEntityType.GUARDIAN_SERVANT.get(), GuardianServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BEAR_SERVANT.get(), BearServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.POLAR_BEAR_SERVANT.get(), BearServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HOGLIN_SERVANT.get(), HoglinServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLACK_BEAST.get(), BlackBeastRenderer::new);
        event.registerEntityRenderer(ModEntityType.WHISPERER.get(), WhispererRenderer::new);
        event.registerEntityRenderer(ModEntityType.WAVEWHISPERER.get(), WhispererRenderer::new);
        event.registerEntityRenderer(ModEntityType.LEAPLEAF.get(), LeapleafRenderer::new);
        event.registerEntityRenderer(ModEntityType.STONE_MINISTROSITY.get(), StoneMinistrosityRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_MINISTROSITY.get(), RedstoneMinistrosityRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_GOLEM.get(), IceGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.SQUALL_GOLEM.get(), SquallGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_GOLEM.get(), RedstoneGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.GRAVE_GOLEM.get(), GraveGolemRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNT.get(), HauntRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_MONSTROSITY.get(), RedstoneMonstrosityRenderer::new);
        event.registerEntityRenderer(ModEntityType.REDSTONE_CUBE.get(), RedstoneCubeRenderer::new);
        event.registerEntityRenderer(ModEntityType.WATCHLING_SERVANT.get(), WatchlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.BLASTLING_SERVANT.get(), BlastlingRenderer::new);
        event.registerEntityRenderer(ModEntityType.SNARELING_SERVANT.get(), SnarelingRenderer::new);
        event.registerEntityRenderer(ModEntityType.SORCERER.get(), SorcererRenderer::new);
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
        event.registerEntityRenderer(ModEntityType.HOSTILE_REDSTONE_MONSTROSITY.get(), RedstoneMonstrosityRenderer::new);
        event.registerEntityRenderer(ModEntityType.VIZIER.get(), VizierRenderer::new);
        event.registerEntityRenderer(ModEntityType.VIZIER_CLONE.get(), VizierCloneRenderer::new);
        event.registerEntityRenderer(ModEntityType.IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.WIGHT.get(), WightRenderer::new);
        event.registerEntityRenderer(ModEntityType.CARRION_MAGGOT.get(), CarrionMaggotRenderer::new);
        event.registerEntityRenderer(ModEntityType.CARRION_FLY.get(), CarrionFlyRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKULL_LORD.get(), SkullLordRenderer::new);
        event.registerEntityRenderer(ModEntityType.BONE_LORD.get(), BoneLordRenderer::new);
        event.registerEntityRenderer(ModEntityType.WITHER_NECROMANCER.get(), WitherNecromancerRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARROW_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_BLAST_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOID_LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.UPDRAFT_BLAST.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.CUSHION.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.MAGIC_GROUND.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.ACID_POOL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_PILLAR.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.VOID_RIFT.get(), VoidRiftRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_UTIL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_APOSTLE.get(), SummonApostleRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAIL_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.MONSOON_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.HELL_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPELL_LIGHTNING_BOLT.get(), SpellLightningBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLIED_EFFECT_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.BREW_EFFECT_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.DRAGON_BREATH_CLOUD.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.VINE_HOOK.get(), VineHookRenderer::new);
        event.registerEntityRenderer(ModEntityType.SURVEY_EYE.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.CAMERA_SHAKE.get(), TrapRenderer::new);
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
                                BiomeColors.getAverageWaterColor(lightReader, pos) :
                                -1, ModBlocks.HAUNTED_JUG.get());
        event.register(
                (state, lightReader, pos, color) ->
                        lightReader != null && pos != null ?
                                BiomeColors.getAverageFoliageColor(lightReader, pos) :
                                FoliageColor.getDefaultColor(), ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get(), ModBlocks.WINDSWEPT_LEAVES.get(), ModBlocks.PINE_LEAVES.get());
    }

    @SubscribeEvent
    public static void colorItem(RegisterColorHandlersEvent.Item event){
        event.register((itemStack, i) -> i > 0 ? -1 : PotionUtils.getColor(itemStack),
                ModItems.BREW.get(), ModItems.SPLASH_BREW.get(), ModItems.LINGERING_BREW.get(), ModItems.GAS_BREW.get());
        event.register((itemStack, i) -> 3694022, ModBlocks.HAUNTED_JUG.get());
        event.register((itemStack, i) -> {
            BlockState blockstate = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, i);
        }, ModBlocks.HARDENED_LEAVES.get(), ModBlocks.ROTTEN_LEAVES.get(), ModBlocks.WINDSWEPT_LEAVES.get(), ModBlocks.PINE_LEAVES.get());
    }

    @SubscribeEvent
    public static void modelBake(ModelEvent.ModifyBakingResult event) {
        List<Map.Entry<ResourceLocation, BakedModel>> models =  event.getModels().entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(Goety.MOD_ID)
                        && entry.getKey().getPath().contains("leaves")
                        && !entry.getKey().getPath().contains("mcd")
                        && !entry.getKey().getPath().contains("chorus")).toList();

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
    public static void registerItemDecorators(RegisterItemDecorationsEvent event) {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof IPersist) {
                event.register(item, new IPersistDecorator());
            }
        }
    }
}
