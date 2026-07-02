package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HauntedBroomModel;
import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HauntedBroomRenderer<T extends HauntedBroom> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/haunted_broom.png");
    private final HauntedBroomModel<T> model;

    public HauntedBroomRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new HauntedBroomModel<>(context.bakeLayer(ModModelLayer.BROOM));
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T broomEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));

        float hurtTime = broomEntity.getHurtTime() - partialTicks;

        if (hurtTime > 0.0F) {
            float swingDegrees = Mth.sin(hurtTime) * hurtTime * 2.5F / 10.0F;
            poseStack.mulPose(Axis.XP.rotationDegrees(swingDegrees));
            poseStack.mulPose(Axis.ZP.rotationDegrees(swingDegrees));
        }

        ResourceLocation broomTexture = this.getTextureLocation(broomEntity);

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.model.setupAnim(broomEntity, partialTicks, 0.0F, broomEntity.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(this.model.renderType(broomTexture));
        this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(broomEntity, entityYaw, partialTicks, poseStack, multiBufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
