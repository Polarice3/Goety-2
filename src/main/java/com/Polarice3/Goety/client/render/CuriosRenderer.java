package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.*;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderer {
    public static String folderPath = "textures/models/curios/";

    public static ResourceLocation render(String textureName){
        return Goety.location(folderPath + textureName);
    }

    public static void register() {
        CuriosRendererRegistry.register(ModItems.DARK_HAT.get(), () -> new WearRenderer(render("dark_hat.png"), new DarkHatModel(bakeLayer(ModModelLayer.DARK_HAT))));
        CuriosRendererRegistry.register(ModItems.GRAND_TURBAN.get(), () -> new WearRenderer(render("grand_turban.png"), new DarkHatModel(bakeLayer(ModModelLayer.GRAND_TURBAN))));
        CuriosRendererRegistry.register(ModItems.WITCH_HAT.get(), () -> new WearRenderer(render("witch_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register(ModItems.WITCH_HAT_HEDGE.get(), () -> new WearRenderer(render("witch_hat_hedge.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register(ModItems.CRONE_HAT.get(), () -> new WearRenderer(render("crone_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.CRONE_HAT))));
        CuriosRendererRegistry.register(ModItems.DARK_ROBE.get(), () -> new WearRenderer(render("dark_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.GRAND_ROBE.get(), () -> new WearRenderer(render("grand_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_ROBE.get(), () -> new WearRenderer(render("illusion_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_ROBE_MIRROR.get(), () -> new WearRenderer(render("illusion_robe_mirror.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.FROST_ROBE.get(), () -> new WearRenderer(render("frost_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.FROST_ROBE_CRYO.get(), () -> new WearRenderer(render("frost_robe_cryo.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WIND_ROBE.get(), () -> new WearRenderer(render("wind_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.STORM_ROBE.get(), () -> new WearRenderer(render("storm_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WILD_ROBE.get(), () -> new WearRenderer(render("wild_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.NECRO_CROWN.get(), () -> new WearRenderer(render("necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CROWN))));
        CuriosRendererRegistry.register(ModItems.NECRO_CAPE.get(), () -> new WearRenderer(render("necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CROWN.get(), () -> new WearRenderer(render("nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NAMELESS_CROWN))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CAPE.get(), () -> new WearRenderer(render("nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.WITCH_ROBE.get(), () -> new WearRenderer(render("witch_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WITCH_ROBE_HEDGE.get(), () -> new WearRenderer(render("witch_robe_hedge.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_ROBE.get(), () -> new WearRenderer(render("warlock_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_ROBE_DARK.get(), () -> new WearRenderer(render("warlock_robe_dark.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_SASH.get(), () -> new WearRenderer(render("warlock_sash.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.PENDANT_OF_HUNGER.get(), () -> new WearRenderer(render("pendant_of_hunger.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register(ModItems.SEA_AMULET.get(), () -> new WearRenderer(render("sea_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register(ModItems.STAR_AMULET.get(), () -> new WearRenderer(render("star_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));        CuriosRendererRegistry.register(ModItems.WAYFARERS_BELT.get(), () -> new WearRenderer(render("wayfarers_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register(ModItems.FELINE_AMULET.get(), () -> new WearRenderer(render("feline_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register(ModItems.SPITEFUL_BELT.get(), () -> new WearRenderer(render("spiteful_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register(ModItems.FOCUS_BAG.get(), () -> new WearRenderer(render("focus_bag.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.FOCUS_BAG))));
        CuriosRendererRegistry.register(ModItems.BREW_BAG.get(), () -> new WearRenderer(render("brew_bag.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BREW_BAG))));
        CuriosRendererRegistry.register(ModItems.AMETHYST_NECKLACE.get(), () -> new WearRenderer(render("amethyst_necklace.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMETHYST_NECKLACE))));
        CuriosRendererRegistry.register(ModItems.TARGETING_MONOCLE.get(), () -> new WearRenderer(render("targeting_monocle.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.MONOCLE))));
        CuriosRendererRegistry.register(ModItems.GRAVE_GLOVE.get(), () -> new WearRenderer(render("grave_glove.png"), new GloveModel(bakeLayer(ModModelLayer.GLOVE))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
