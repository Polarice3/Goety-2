package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.PreacherModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Preacher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PreacherRenderer<T extends Preacher> extends MobRenderer<T, PreacherModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/preacher.png");

    public PreacherRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PreacherModel<>(renderManagerIn.bakeLayer(ModModelLayer.PREACHER)), 0.5F);
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
