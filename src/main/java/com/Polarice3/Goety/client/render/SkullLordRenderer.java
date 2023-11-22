package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SkullLordModel;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SkullLordRenderer extends MobRenderer<SkullLord, SkullLordModel<SkullLord>> {
    public static final ResourceLocation CONNECTION = Goety.location("textures/entity/skull_lord/skull_lord_connection.png");
    public static final ResourceLocation LASER_BEAM = Goety.location("textures/entity/skull_lord/skull_lord_laser.png");
    private static final ResourceLocation LOCATION = Goety.location("textures/entity/skull_lord/skull_lord.png");
    private static final ResourceLocation VULNERABLE = Goety.location("textures/entity/skull_lord/skull_lord_vulnerable.png");
    private static final ResourceLocation CHARGE = Goety.location("textures/entity/skull_lord/skull_lord_charging.png");
    private static final RenderType CONNECTION_RENDER_TYPE = RenderType.entityCutoutNoCull(CONNECTION);
    private static final RenderType LASER_RENDER_TYPE = RenderType.entityCutoutNoCull(LASER_BEAM);

    public SkullLordRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new SkullLordModel<>(p_174435_.bakeLayer(ModModelLayer.SKULL_LORD)), 0.5F);
    }

    protected int getBlockLightLevel(SkullLord pEntity, BlockPos pPos) {
        return 15;
    }

    public boolean shouldRender(SkullLord skullLord, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(skullLord, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (skullLord.getBoneLord() != null) {
                LivingEntity boneLord = skullLord.getBoneLord();
                if (boneLord != null) {
                    Vec3 vector3d = this.getPosition(boneLord, (double)boneLord.getBbHeight() * 0.75D, 1.0F);
                    Vec3 vector3d1 = this.getPosition(skullLord, (double)skullLord.getBbHeight() * 0.5D, 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
                }
            }
            if (skullLord.getLaser() != null){
                LivingEntity laser = skullLord.getLaser();
                if (laser != null) {
                    Vec3 vector3d = this.getPosition(laser, (double)laser.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vector3d1 = this.getPosition(skullLord, (double)skullLord.getBbHeight() * 0.5D, 1.0F);
                    return camera.isVisible(new AABB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
                }
            }

            return false;
        }
    }

    private Vec3 getPosition(LivingEntity pLivingEntity, double p_177110_2_, float p_177110_4_) {
        double d0 = Mth.lerp((double)p_177110_4_, pLivingEntity.xOld, pLivingEntity.getX());
        double d1 = Mth.lerp((double)p_177110_4_, pLivingEntity.yOld, pLivingEntity.getY()) + p_177110_2_;
        double d2 = Mth.lerp((double)p_177110_4_, pLivingEntity.zOld, pLivingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    public void render(SkullLord skullLord, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(skullLord, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
/*        BoneLord boneLord = skullLord.getBoneLord();
        SkullLaser skullLaser = skullLord.getLaser();
        if (boneLord != null && !boneLord.isDeadOrDying()) {
            float f1 = (float)skullLord.level.getGameTime() + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = skullLord.getBbHeight() * 0.5F;
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0D, (double)f3, 0.0D);
            Vec3 vec3 = this.getPosition(boneLord, (double)boneLord.getBbHeight() * 0.75D, partialTicks);
            Vec3 vec31 = this.getPosition(skullLord, (double)f3, partialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + 1.0D);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            float f7 = f1 * 0.05F * -1.5F;
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
            VertexConsumer vertexconsumer = bufferIn.getBuffer(CONNECTION_RENDER_TYPE);
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, f28, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f27, f20, f28, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f27, f22, f27, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, f27, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, f28, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f27, f24, f28, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f27, f26, f27, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, f27, f30);
            float f31 = 0.0F;
            if (skullLord.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, 0.5F, f31);
            matrixStackIn.popPose();
        } else if (skullLaser != null){
            float f1 = (float)skullLord.level.getGameTime() + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = skullLord.getEyeHeight();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0D, (double)f3, 0.0D);
            Vec3 vec3 = this.getPosition(skullLaser, (double)skullLaser.getBbHeight() * 0.5D, partialTicks);
            Vec3 vec31 = this.getPosition(skullLord, (double)f3, partialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + 1.0D);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            float f7 = f1 * 0.05F * -1.5F;
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
            VertexConsumer vertexconsumer = bufferIn.getBuffer(LASER_RENDER_TYPE);
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex2(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, f28, f30);
            vertex2(vertexconsumer, matrix4f, matrix3f, f19, f27, f20, f28, f29);
            vertex2(vertexconsumer, matrix4f, matrix3f, f21, f27, f22, f27, f29);
            vertex2(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, f27, f30);
            vertex2(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, f28, f30);
            vertex2(vertexconsumer, matrix4f, matrix3f, f23, f27, f24, f28, f29);
            vertex2(vertexconsumer, matrix4f, matrix3f, f25, f27, f26, f27, f29);
            vertex2(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, f27, f30);
            float f31 = 0.0F;
            if (skullLord.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex2(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, 0.5F, f31 + 0.5F);
            vertex2(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, 1.0F, f31 + 0.5F);
            vertex2(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, 1.0F, f31);
            vertex2(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, 0.5F, f31);
            matrixStackIn.popPose();
        }*/
    }

    private static void vertex(VertexConsumer p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.vertex(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(255, 255, 255, 255).uv(p_229108_9_, p_229108_10_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void vertex2(VertexConsumer p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.vertex(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(116, 241, 245, 255).uv(p_229108_9_, p_229108_10_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SkullLord pEntity) {
        if (pEntity.isCharging() || pEntity.getLaser() != null){
            return CHARGE;
        } else {
            return pEntity.isInvulnerable() ? LOCATION: VULNERABLE;
        }
    }
}
