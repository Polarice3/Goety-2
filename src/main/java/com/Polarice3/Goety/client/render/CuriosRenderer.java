package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.*;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderer {

    public static void register() {
        CuriosRendererRegistry.register(ModItems.DARK_HAT.get(), () -> new WearRenderer(Goety.location("textures/models/curios/dark_hat.png"), new DarkHatModel(bakeLayer(ModModelLayer.DARK_HAT))));
        CuriosRendererRegistry.register(ModItems.GRAND_TURBAN.get(), () -> new WearRenderer(Goety.location("textures/models/curios/grand_turban.png"), new DarkHatModel(bakeLayer(ModModelLayer.GRAND_TURBAN))));
        CuriosRendererRegistry.register(ModItems.WITCH_HAT.get(), () -> new WearRenderer(Goety.location("textures/models/curios/witch_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register(ModItems.CRONE_HAT.get(), () -> new WearRenderer(Goety.location("textures/models/curios/crone_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.CRONE_HAT))));
        CuriosRendererRegistry.register(ModItems.DARK_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/dark_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.GRAND_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/grand_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_ROBE.get(), () -> new WearRenderer(Goety.location("textures/curios/armor/illusion_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.FROST_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/frost_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WIND_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/wind_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.NECRO_CROWN.get(), () -> new WearRenderer(Goety.location("textures/models/curios/necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CROWN))));
        CuriosRendererRegistry.register(ModItems.NECRO_CAPE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CROWN.get(), () -> new WearRenderer(Goety.location("textures/models/curios/nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NAMELESS_CROWN))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CAPE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.WITCH_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/witch_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WITCH_ROBE_HEDGE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/witch_robe_hedge.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/warlock_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_ROBE_DARK.get(), () -> new WearRenderer(Goety.location("textures/models/curios/warlock_robe_dark.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WARLOCK_SASH.get(), () -> new WearRenderer(Goety.location("textures/models/curios/warlock_sash.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.SEA_AMULET.get(), () -> new WearRenderer(Goety.location("textures/models/curios/sea_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register(ModItems.WAYFARERS_BELT.get(), () -> new WearRenderer(Goety.location("textures/models/curios/wayfarers_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register(ModItems.SPITEFUL_BELT.get(), () -> new WearRenderer(Goety.location("textures/models/curios/spiteful_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register(ModItems.FOCUS_BAG.get(), () -> new WearRenderer(Goety.location("textures/models/curios/focus_bag.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.FOCUS_BAG))));
        CuriosRendererRegistry.register(ModItems.AMETHYST_NECKLACE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/amethyst_necklace.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMETHYST_NECKLACE))));
        CuriosRendererRegistry.register(ModItems.TARGETING_MONOCLE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/targeting_monocle.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.MONOCLE))));
        CuriosRendererRegistry.register(ModItems.GRAVE_GLOVE.get(), () -> new WearRenderer(Goety.location("textures/models/curios/grave_glove.png"), new GloveModel(bakeLayer(ModModelLayer.GLOVE))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
