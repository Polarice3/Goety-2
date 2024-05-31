package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class ObsidianMonolithRenderer extends AbstractMonolithRenderer{
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/obsidian_monolith.png");
    private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/monolith/obsidian_monolith_glow.png"));
    private static final Map<AbstractMonolith.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(AbstractMonolith.Crackiness.LOW, Goety.location("textures/entity/monolith/obsidian_monolith_crack_1.png"), AbstractMonolith.Crackiness.MEDIUM, Goety.location("textures/entity/monolith/obsidian_monolith_crack_2.png"), AbstractMonolith.Crackiness.HIGH, Goety.location("textures/entity/monolith/obsidian_monolith_crack_3.png"));
    private static final ResourceLocation RESOURCE_LOCATION = Goety.location("textures/entity/monolith/obsidian_monolith_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(RESOURCE_LOCATION);

    public ObsidianMonolithRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
    }

    @Override
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        return RENDER_TYPE;
    }

    @Override
    public Map<AbstractMonolith.Crackiness, ResourceLocation> cracknessLocation() {
        return resourceLocations;
    }

    private Vec3 getPosition(LivingEntity p_114803_, double p_114804_, float p_114805_) {
        double d0 = Mth.lerp((double)p_114805_, p_114803_.xOld, p_114803_.getX());
        double d1 = Mth.lerp((double)p_114805_, p_114803_.yOld, p_114803_.getY()) + p_114804_;
        double d2 = Mth.lerp((double)p_114805_, p_114803_.zOld, p_114803_.getZ());
        return new Vec3(d0, d1, d2);
    }

    public void render(AbstractMonolith pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        LivingEntity livingentity = pEntity.getTrueOwner();
        if (livingentity != null && !pEntity.isEmerging()) {
            float f1 = pEntity.tickCount + pPartialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = pEntity.getEyeHeight();
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.0F, f3, 0.0F);
            Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, pPartialTicks);
            Vec3 vec31 = this.getPosition(pEntity, (double)f3, pPartialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + 1.0D);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            float f7 = f1 * 0.05F * -1.5F;
            int j = (0xe079fa >> 16 & 255) / 255;
            int k = (0xe079fa >> 8 & 255) / 255;
            int l = (0xe079fa & 255) / 255;
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * f10;
            float f12 = Mth.sin(f7 + 2.3561945F) * f10;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * f10;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * f10;
            float f15 = Mth.cos(f7 + 3.926991F) * f10;
            float f16 = Mth.sin(f7 + 3.926991F) * f10;
            float f17 = Mth.cos(f7 + 5.4977875F) * f10;
            float f18 = Mth.sin(f7 + 5.4977875F) * f10;
            float f19 = Mth.cos(f7 + (float)Math.PI) * f9;
            float f20 = Mth.sin(f7 + (float)Math.PI) * f9;
            float f21 = Mth.cos(f7 + 0.0F) * f9;
            float f22 = Mth.sin(f7 + 0.0F) * f9;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * f9;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * f9;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * f9;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * f9;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = pMatrixStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, f28, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f27, f20, j, k, l, f28, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f27, f22, j, k, l, f27, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, f27, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, f28, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f27, f24, j, k, l, f28, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f27, f26, j, k, l, f27, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, f27, f30);
            float f31 = 0.0F;
            if (pEntity.tickCount % 2 == 0) {
                f31 = 0.5F;
            }
            float f32 = 0.5F;
            float f33 = 1.0F;

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, f32, f31 + f32);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, f33, f31 + f32);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, f33, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, f32, f31);
            pMatrixStack.popPose();
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.vertex(matrix4f, x, y, z).color(red, green, blue, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }
}
