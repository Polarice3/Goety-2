package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VizierModel;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VizierAuraLayer extends EnergySwirlLayer<Vizier, VizierModel> {
    private static final ResourceLocation VIZIER_ARMOR = new ResourceLocation(Goety.MOD_ID, "textures/entity/illagers/vizierarmor.png");
    private final VizierModel model;

    public VizierAuraLayer(RenderLayerParent<Vizier, VizierModel> p_174554_, EntityModelSet p_174555_) {
        super(p_174554_);
        this.model = new VizierModel(p_174555_.bakeLayer(ModModelLayer.VIZIER_ARMOR));
    }

    protected float xOffset(float p_225634_1_) {
        return Mth.cos(p_225634_1_ * 0.02F) * 3.0F;
    }

    protected ResourceLocation getTextureLocation() {
        return VIZIER_ARMOR;
    }

    protected EntityModel<Vizier> model() {
        return this.model;
    }
}
