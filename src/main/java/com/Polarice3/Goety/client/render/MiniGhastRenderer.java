package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.MiniGhastModel;
import com.Polarice3.Goety.common.entities.ally.MiniGhast;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniGhastRenderer extends MobRenderer<MiniGhast, MiniGhastModel<MiniGhast>> {
    private static final ResourceLocation GHAST_LOCATION = Goety.location("textures/entity/servants/mini_ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = Goety.location("textures/entity/servants/mini_ghast_shooting.png");

    public MiniGhastRenderer(EntityRendererProvider.Context p_i50961_1_) {
        super(p_i50961_1_, new MiniGhastModel<>(p_i50961_1_.bakeLayer(ModModelLayer.MINI_GHAST)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MiniGhast pEntity) {
        if (pEntity.isCharging()){
            return GHAST_SHOOTING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    protected void scale(MiniGhast pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = pLivingEntity.getSwelling(pPartialTickTime);
        if (f < 0.0F) {
            f = 0.0F;
        }
        f = 1.0F / (f * f * f * f * f * 2.0F + 1.0F);
        float var5 = (3.0F + f) / 2.0F;
        float var6 = (3.0F + 1.0F / f) / 2.0F;
        pMatrixStack.scale(var6, var5, var6);
    }
}
