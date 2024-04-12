package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.VineHook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class VineHookRenderer extends EntityRenderer<VineHook> {
    private static final ResourceLocation GRAPPLE_LOCATION = Goety.location("textures/entity/vine_grapple.png");
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/projectiles/grapple_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

    public VineHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(VineHook hook, float pYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Player player = hook.getPlayerOwner();
        if (player != null) {
            poseStack.pushPose();
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RENDER_TYPE);
            vertex0(vertexconsumer, matrix4f, matrix3f, pPackedLight, 0.0F, 0, 0, 1);
            vertex0(vertexconsumer, matrix4f, matrix3f, pPackedLight, 1.0F, 0, 1, 1);
            vertex0(vertexconsumer, matrix4f, matrix3f, pPackedLight, 1.0F, 1, 1, 0);
            vertex0(vertexconsumer, matrix4f, matrix3f, pPackedLight, 0.0F, 1, 0, 0);
            poseStack.popPose();
            Vec3 vec3 = getPlayerHandPos(player, pPartialTicks);
            Vec3 vec32 = new Vec3(
                    Mth.lerp((double)pPartialTicks, hook.xo, hook.getX()),
                    Mth.lerp((double)pPartialTicks, hook.yo, hook.getY()) + (double)hook.getEyeHeight(),
                    Mth.lerp((double)pPartialTicks, hook.zo, hook.getZ())
            );
            float f0 = (float)hook.tickCount + pPartialTicks;
            float f1 = f0 * 0.15F % 1.0F;
            Vec3 vec33 = vec3.subtract(vec32);
            float f2 = (float)(vec33.length() + 0.1);
            vec33 = vec33.normalize();
            float f3 = (float)Math.acos(vec33.y);
            float f4 = (float)Math.atan2(vec33.z, vec33.x);
            poseStack.mulPose(Axis.YP.rotationDegrees(((float) (Math.PI / 2) - f4) * (180.0F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(f3 * (180.0F / (float)Math.PI)));
            float f5 = f0 * 0.05F * -1.5F;
            float f6 = 0.2F;
            float f7 = Mth.cos(f5 + (float) Math.PI) * f6;
            float f8 = Mth.sin(f5 + (float) Math.PI) * f6;
            float f9 = Mth.cos(f5 + 0.0F) * f6;
            float f10 = Mth.sin(f5 + 0.0F) * f6;
            float f11 = Mth.cos(f5 + (float) (Math.PI / 2)) * f6;
            float f12 = Mth.sin(f5 + (float) (Math.PI / 2)) * f6;
            float f13 = Mth.cos(f5 + (float) (Math.PI * 3.0 / 2.0)) * f6;
            float f14 = Mth.sin(f5 + (float) (Math.PI * 3.0 / 2.0)) * f6;
            float f15 = -1.0F + f1;
            float f16 = f2 * 2.5F + f15;
            VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(GRAPPLE_LOCATION));
            PoseStack.Pose pose = poseStack.last();
            vertex(vertexConsumer, pose, f7, f2, f8, 0.4999F, f16);
            vertex(vertexConsumer, pose, f7, 0.0F, f8, 0.4999F, f15);
            vertex(vertexConsumer, pose, f9, 0.0F, f10, 0.0F, f15);
            vertex(vertexConsumer, pose, f9, f2, f10, 0.0F, f16);
            vertex(vertexConsumer, pose, f11, f2, f12, 0.4999F, f16);
            vertex(vertexConsumer, pose, f11, 0.0F, f12, 0.4999F, f15);
            vertex(vertexConsumer, pose, f13, 0.0F, f14, 0.0F, f15);
            vertex(vertexConsumer, pose, f13, f2, f14, 0.0F, f16);
            poseStack.popPose();
            super.render(hook, pYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
        }
    }

    private static void vertex0(VertexConsumer vertexConsumer, Matrix4f p_114713_, Matrix3f p_114714_, int packedLight, float p_114716_, int p_114717_, int uv0, int uv1) {
        vertexConsumer.vertex(p_114713_, p_114716_ - 0.5F, (float)p_114717_ - 0.5F, 0.0F)
                .color(128, 255, 128, 255)
                .uv((float)uv0, (float)uv1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(p_114714_, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static void vertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, float z, float uv0, float uv1) {
        vertexConsumer.vertex(pose.pose(), x, y, z)
                .color(128, 255, 128, 255)
                .uv(uv0, uv1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private Vec3 getPlayerHandPos(Player player, float pPartialTicks) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        float f = player.getAttackAnim(pPartialTicks);
        float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);

        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double d7 = 960.0D / (double) this.entityRenderDispatcher.options.fov().get();
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
            vec3 = vec3.scale(d7);
            vec3 = vec3.yRot(f1 * 0.5F);
            vec3 = vec3.xRot(-f1 * 0.7F);
            return player.getEyePosition(pPartialTicks).add(vec3);
        } else {
            float f2 = Mth.lerp(pPartialTicks, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
            double d0 = Mth.sin(f2);
            double d1 = Mth.cos(f2);
            double d2 = (double)i * 0.35D;
            double d3 = 0.8D;
            float f3 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(pPartialTicks).add(-d1 * d2 - d0 * d3, (double)f3 - 0.45F, -d0 * d2 + d1 * d3);
        }
    }

    public ResourceLocation getTextureLocation(VineHook vineHook) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
