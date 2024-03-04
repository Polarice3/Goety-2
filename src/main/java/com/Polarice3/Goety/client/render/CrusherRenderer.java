package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CrusherModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Crusher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class CrusherRenderer<T extends Crusher> extends MobRenderer<T, CrusherModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/crusher.png");
    protected static final ResourceLocation STORM = Goety.location("textures/entity/illagers/crusher_storm.png");

    public CrusherRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CrusherModel<>(renderManagerIn.bakeLayer(ModModelLayer.CRUSHER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = entity.isStorm() ? 1.25F : 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isStorm()){
            return STORM;
        }
        return TEXTURE;
    }
}
