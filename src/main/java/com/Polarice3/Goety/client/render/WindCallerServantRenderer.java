package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.WindCallerModel;
import com.Polarice3.Goety.common.entities.ally.illager.WindCallerServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WindCallerServantRenderer<T extends WindCallerServant> extends MobRenderer<T, WindCallerModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/wind_caller.png");
    protected static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/illager/wind_caller_original.png");

    public WindCallerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new WindCallerModel<>(renderManagerIn.bakeLayer(ModModelLayer.WIND_CALLER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.WindCallerServantTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE;
        }
    }
}
