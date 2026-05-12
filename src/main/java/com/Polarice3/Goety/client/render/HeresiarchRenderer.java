package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HeresiarchModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heresiarch;
import com.Polarice3.Goety.init.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HeresiarchRenderer<T extends Heresiarch> extends MobRenderer<T, HeresiarchModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/heresiarch.png");
    private static final ResourceLocation RUNE = Goety.location("textures/entity/cultist/spell_rune.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(RUNE);

    public HeresiarchRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new HeresiarchModel<>(p_174304_.bakeLayer(ModModelLayer.HERESIARCH)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, p_174304_.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T heresiarch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (heresiarch.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY)) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, heresiarch, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
        if (p_115455_.isCurrentAnimation(Heresiarch.BARRAGE)) {
            float age = p_115455_.tickCount + p_115457_;
            p_115458_.pushPose();
            p_115458_.scale(2.0F, 2.0F, 2.0F);
            p_115458_.translate(0.0D, 2.0D, 0.0D);
            p_115458_.mulPose(this.entityRenderDispatcher.cameraOrientation());
            p_115458_.mulPose(Axis.YP.rotationDegrees(180.0F));
            p_115458_.mulPose(Axis.ZP.rotationDegrees(age));
            PoseStack.Pose posestack$pose = p_115458_.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = p_115459_.getBuffer(RENDER_TYPE);
            vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 1.0F, 0, 1, 1);
            vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 1.0F, 1, 1, 0);
            vertex(vertexconsumer, matrix4f, matrix3f, LightTexture.FULL_BRIGHT, 0.0F, 1, 0, 0);
            p_115458_.popPose();
        }
    }

    private static void vertex(VertexConsumer p_254095_, Matrix4f p_254477_, Matrix3f p_253948_, int p_253829_, float p_253995_, int p_254031_, int p_253641_, int p_254243_) {
        p_254095_.vertex(p_254477_, p_253995_ - 0.5F, (float)p_254031_ - 0.5F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float)p_253641_, (float)p_254243_)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(p_253829_)
                .normal(p_253948_, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
