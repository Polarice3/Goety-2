package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.GulfTentacleModel;
import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GulfTentacleRenderer<T extends GulfTentacle> extends MobRenderer<T, GulfTentacleModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/gulf_tentacle.png");

    public GulfTentacleRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GulfTentacleModel<>(renderManagerIn.bakeLayer(ModModelLayer.GULF_TENTACLE)), 0.0F);
    }

    @Override
    public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        if (p_115455_.life > 1) {
            super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
        }
    }

    @Override
    protected void scale(T p_115314_, PoseStack p_115315_, float p_115316_) {
        float zSize = p_115314_.getRange() / 4.0F;
        p_115315_.scale(1.0F, 1.0F, zSize);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
