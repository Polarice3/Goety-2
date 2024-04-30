package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TramplerModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Trampler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TramplerRenderer extends MobRenderer<Trampler, TramplerModel<Trampler>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/trampler.png");

    public TramplerRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new TramplerModel<>(p_174435_.bakeLayer(ModModelLayer.TRAMPLER)), 0.5F);
    }

    @Override
    protected void scale(Trampler p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(1.2F, 1.2F, 1.2F);
    }

    public ResourceLocation getTextureLocation(Trampler p_116292_) {
        return TEXTURE;
    }
}
