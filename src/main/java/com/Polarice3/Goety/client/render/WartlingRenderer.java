package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WartlingRenderer<T extends Wartling> extends MobRenderer<T, SpiderModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/wartling.png");

    public WartlingRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SpiderModel<>(renderManagerIn.bakeLayer(ModelLayers.SPIDER)), 0.8F * 0.4F);
    }

    protected void scale(T pLivingEntity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
    }

    protected float getFlipDegrees(T pLivingEntity) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
