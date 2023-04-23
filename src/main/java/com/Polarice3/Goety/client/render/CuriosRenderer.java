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
        CuriosRendererRegistry.register(ModItems.DARK_HAT.get(), () -> new WearRenderer(Goety.location("textures/models/armor/dark_hat.png"), new DarkHatModel(bakeLayer(ModModelLayer.DARK_HAT))));
        CuriosRendererRegistry.register(ModItems.WITCH_HAT.get(), () -> new WearRenderer(Goety.location("textures/models/armor/witch_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register(ModItems.DARK_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/dark_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/illusion_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.WIND_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/wind_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.NECRO_CROWN.get(), () -> new WearRenderer(Goety.location("textures/models/armor/necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CROWN))));
        CuriosRendererRegistry.register(ModItems.NECRO_CAPE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/necro_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CROWN.get(), () -> new WearRenderer(Goety.location("textures/models/armor/nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NAMELESS_CROWN))));
        CuriosRendererRegistry.register(ModItems.NAMELESS_CAPE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/nameless_cape.png"), new NecroCapeModel<>(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register(ModItems.WITCH_ROBE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/witch_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.GRAVE_GLOVE.get(), () -> new WearRenderer(Goety.location("textures/models/armor/grave_glove.png"), new GloveModel(bakeLayer(ModModelLayer.GLOVE))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
