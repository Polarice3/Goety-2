package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WebShotModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class WebShotRenderer<T extends Projectile> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/web_shot.png");
    public WebShotModel<T> model;

    public WebShotRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.model = new WebShotModel<>(p_174008_.bakeLayer(ModModelLayer.WEB_SHOT));
        this.shadowRadius = 0.5F;
    }

    public void render(T pEntity, float pYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0, pEntity.getBoundingBox().getYsize() * 0.5F, 0);
        this.scale(pEntity, pPoseStack, 1.0F);
        Vec3 vec3 = pEntity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(vec3.horizontalDistance(), vec3.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(vec3.z, vec3.x) * (double) (180.0F / (float) Math.PI)) + 90.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(pEntity)));
        this.model.renderToBuffer(pPoseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        pPoseStack.popPose();

        super.render(pEntity, pYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }

    protected void scale(T p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(p_116296_, p_116296_, p_116296_);
    }

}
