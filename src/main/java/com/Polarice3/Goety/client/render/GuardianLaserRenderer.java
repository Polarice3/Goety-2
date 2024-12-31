package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GuardianLaserRenderer {
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public static void renderLaser(RenderLevelStageEvent event, Player player, float ticks) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        render(player, event.getPartialTick(), event.getPoseStack(), buffer);
    }

    public static void render(Player player, float partialTick, PoseStack poseStack, MultiBufferSource pBuffer) {
        Entity entity = MiscCapHelper.getClientTarget(player);
        if (entity instanceof LivingEntity livingentity) {
            int count = player.getUseItemRemainingTicks();
            int CastTime = player.getUseItem().getUseDuration() - count;
            float f = getAttackAnimationScale(CastTime, partialTick);
            float f1 = CastTime + partialTick;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = player.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.0F);
            Vec3 vec3 = getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, partialTick);
            Vec3 vec31 = getPosition(player, f3, partialTick);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + 1.0D);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (player.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            poseStack.popPose();
        }
    }

    public static float getAttackAnimationScale(float time, float p_32813_) {
        return (time + p_32813_) / (float) SpellConfig.PrismaBeamDuration.get();
    }

    private static Vec3 getPosition(LivingEntity livingEntity, double height, float partialTicks) {
        double d0 = Mth.lerp(partialTicks, livingEntity.xOld, livingEntity.getX());
        double d1 = Mth.lerp(partialTicks, livingEntity.yOld, livingEntity.getY()) + height;
        double d2 = Mth.lerp(partialTicks, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    private static void vertex(VertexConsumer p_253637_, Matrix4f p_253920_, Matrix3f p_253881_, float p_253994_, float p_254492_, float p_254474_, int p_254080_, int p_253655_, int p_254133_, float p_254233_, float p_253939_) {
        p_253637_.vertex(p_253920_, p_253994_, p_254492_, p_254474_).color(p_254080_, p_253655_, p_254133_, 255).uv(p_254233_, p_253939_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253881_, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
