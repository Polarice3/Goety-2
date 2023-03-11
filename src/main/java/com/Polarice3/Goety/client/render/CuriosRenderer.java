package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.DarkHatModel;
import com.Polarice3.Goety.client.render.model.DarkRobeModel;
import com.Polarice3.Goety.client.render.model.HelmModel;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderer {

    public static void register() {
        CuriosRendererRegistry.register(ModItems.DARK_HAT.get(), () -> new DarkHatRenderer(new DarkHatModel(bakeLayer(ModModelLayer.DARK_HAT))));
        CuriosRendererRegistry.register(ModItems.DARK_ROBE.get(), () -> new DarkRobeRenderer(new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_HELM.get(), () -> new HelmRenderer(new HelmModel(bakeLayer(ModModelLayer.HELM))));
        CuriosRendererRegistry.register(ModItems.ILLUSION_ROBE.get(), () -> new DarkRobeRenderer(Goety.location("textures/models/armor/illusion_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }
}
