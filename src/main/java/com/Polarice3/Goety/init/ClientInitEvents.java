package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusBagScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.SoulItemScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.WandandBagScreen;
import com.Polarice3.Goety.client.inventory.container.ModContainerType;
import com.Polarice3.Goety.client.particles.GlowingParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.NoneParticle;
import com.Polarice3.Goety.client.particles.WraithParticle;
import com.Polarice3.Goety.client.render.*;
import com.Polarice3.Goety.client.render.block.*;
import com.Polarice3.Goety.client.render.model.*;
import com.Polarice3.Goety.common.blocks.ModWoodType;
import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.vehicle.ModBoat;
import com.Polarice3.Goety.common.items.FlameCaptureItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInitEvents {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event){
        MenuScreens.register(ModContainerType.WAND.get(), SoulItemScreen::new);
        MenuScreens.register(ModContainerType.FOCUS_BAG.get(), FocusBagScreen::new);
        MenuScreens.register(ModContainerType.WAND_AND_BAG.get(), WandandBagScreen::new);
        CuriosRenderer.register();
        ModKeybindings.init();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(BossBarEvent::renderBossBar);
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModWoodType.HAUNTED);
        });
        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("souls"),
                (stack, world, living, seed) -> ((float) TotemOfSouls.currentSouls(stack)) / TotemOfSouls.MAX_SOULS);
        ItemProperties.register(ModItems.TOTEM_OF_SOULS.get(), new ResourceLocation("activated"),
                (stack, world, living, seed) -> TotemOfSouls.isActivated(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.FLAME_CAPTURE.get(), new ResourceLocation("capture"),
                (stack, world, living, seed) -> FlameCaptureItem.hasEntity(stack) ? 1.0F : 0.0F);
    }

    @SubscribeEvent
    public static void registerGUI(final RegisterGuiOverlaysEvent event){
        event.registerAboveAll("soul_energy_hud", SoulEnergyGui.OVERLAY);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModBlockLayer.ARCA, ArcaRenderer::createBodyLayer);
        event.registerLayerDefinition(ModBlockLayer.TALL_SKULL, TallSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SPIKE, SpikeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_BOUQUET, IceBouquetModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ICE_CHUNK, IceChunkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SOUL_BOLT, SoulBoltModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SUMMON_CIRCLE, SummonCircleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.FIRE_TORNADO, FireTornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.APOSTLE, ApostleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZOMBIE_VILLAGER_SERVANT, VillagerServantModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.SKELETON_VILLAGER_SERVANT, SkeletonVillagerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.ZPIGLIN_SERVANT, ZPiglinModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MALGHAST, ModGhastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WRAITH, WraithModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER, VizierModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.IRK, IrkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.MINION, MinionModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.HAUNTED_SKULL, HauntedSkullModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VIZIER_ARMOR, VizierModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_HAT, DarkHatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.WITCH_HAT, WitchHatModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.DARK_ROBE, DarkRobeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.NECRO_CROWN, NecroCapeModel::createHeadLayer);
        event.registerLayerDefinition(ModModelLayer.NECRO_CAPE, NecroCapeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.GLOVE, GloveModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_INNER, VillagerArmorModel::createInnerArmorLayer);
        event.registerLayerDefinition(ModModelLayer.VILLAGER_ARMOR_OUTER, VillagerArmorModel::createOuterArmorLayer);

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
        event.registerBlockEntityRenderer(ModBlockEntities.ICE_BOUQUET_TRAP.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCULK_DEVOURER.get(), ModBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TALL_SKULL.get(), TallSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SIGN_BLOCK_ENTITIES.get(), SignRenderer::new);
        event.registerEntityRenderer(ModEntityType.NETHER_METEOR.get(), NetherMeteorRenderer::new);
        event.registerEntityRenderer(ModEntityType.SWORD.get(), (rendererManager) -> new SwordProjectileRenderer<>(rendererManager, itemRenderer, 1.25F, true));
        event.registerEntityRenderer(ModEntityType.SCYTHE.get(), ScytheSlashRenderer::new);
        event.registerEntityRenderer(ModEntityType.GRAND_LAVABALL.get(), GrandLavaballRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.GLOW_LIGHT.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BULLET.get(), SoulBulletRenderer::new);
        event.registerEntityRenderer(ModEntityType.SOUL_BOLT.get(), SoulBoltRenderer::new);
        event.registerEntityRenderer(ModEntityType.FANG.get(), FangsRenderer::new);
        event.registerEntityRenderer(ModEntityType.SPIKE.get(), SpikeRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_BOUQUET.get(), IceBouquetRenderer::new);
        event.registerEntityRenderer(ModEntityType.ICE_CHUNK.get(), IceChunkRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_CIRCLE.get(), SummonCircleRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO.get(), FireTornadoRenderer::new);
        event.registerEntityRenderer(ModEntityType.MOD_BOAT.get(), (render) -> new ModBoatRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.MOD_CHEST_BOAT.get(), (render) -> new ModBoatRenderer(render, true));
        event.registerEntityRenderer(ModEntityType.APOSTLE.get(), ApostleRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), ZombieVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), ZPiglinRenderer::new);
        event.registerEntityRenderer(ModEntityType.MALGHAST.get(), MalghastRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH.get(), WraithRenderer::new);
        event.registerEntityRenderer(ModEntityType.ALLY_VEX.get(), AllyVexRenderer::new);
        event.registerEntityRenderer(ModEntityType.ZOMBIE_SERVANT.get(), ZombieServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HUSK_SERVANT.get(), HuskServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.DROWNED_SERVANT.get(), DrownedServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.SKELETON_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.STRAY_SERVANT.get(), SkeletonServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.WRAITH_SERVANT.get(), WraithServantRenderer::new);
        event.registerEntityRenderer(ModEntityType.HAUNTED_SKULL.get(), HauntedSkullRenderer::new);
        event.registerEntityRenderer(ModEntityType.DOPPELGANGER.get(), (render) -> new DoppelgangerRenderer(render, false));
        event.registerEntityRenderer(ModEntityType.VIZIER.get(), VizierRenderer::new);
        event.registerEntityRenderer(ModEntityType.IRK.get(), IrkRenderer::new);
        event.registerEntityRenderer(ModEntityType.ARROW_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_BLAST_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_RAIN_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.FIRE_TORNADO_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.LIGHTNING_TRAP.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.STORM_UTIL.get(), TrapRenderer::new);
        event.registerEntityRenderer(ModEntityType.SUMMON_APOSTLE.get(), TrapRenderer::new);
    }

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.register(ModParticleTypes.NONE.get(), NoneParticle.Provider::new);
        event.register(ModParticleTypes.TOTEM_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.WHITE_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.BULLET_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.GLOW_EFFECT.get(), SpellParticle.Provider::new);
        event.register(ModParticleTypes.LEECH.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.HEAL_EFFECT.get(), HeartParticle.Provider::new);
        event.register(ModParticleTypes.SOUL_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.register(ModParticleTypes.GLOW_LIGHT_EFFECT.get(), GlowingParticle.Provider::new);
        event.register(ModParticleTypes.BURNING.get(), FlameParticle.Provider::new);
        event.register(ModParticleTypes.CULT_SPELL.get(), SpellParticle.MobProvider::new);
        event.register(ModParticleTypes.CONFUSED.get(), HeartParticle.Provider::new);
        event.register(ModParticleTypes.WRAITH.get(), WraithParticle.Provider::new);
    }

}
