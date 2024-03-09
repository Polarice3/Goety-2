package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CryologerModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Cryologer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class CryologerRenderer<T extends Cryologer> extends MobRenderer<T, CryologerModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/cryologer.png");

    public CryologerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CryologerModel<>(renderManagerIn.bakeLayer(ModModelLayer.CRYOLOGER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
