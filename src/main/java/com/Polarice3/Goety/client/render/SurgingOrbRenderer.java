package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.SurgingOrb;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SurgingOrbRenderer extends EntityRenderer<SurgingOrb> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/projectiles/scatter_bolt.png");
    private static final ResourceLocation ORANGE = Goety.location("textures/entity/projectiles/scatter_bolt_orange.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/pointed_trail.png");

    public SurgingOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(SurgingOrb entity, BlockPos blockPos) {
        return 15;
    }

    public void render(SurgingOrb entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float age = entity.tickCount + partialTick;
        float clientScale = Mth.lerp(partialTick, entity.prevClientScale, entity.clientScale);
        float f1 = (Mth.sin(age / 5.0F) * 0.2F + 0.2F) * clientScale;
        poseStack.translate(0.0D, entity.getBbHeight() / 2, 0.0D);
        poseStack.scale(0.25F + f1, 0.25F + f1, 0.25F + f1);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        RenderType renderType = RenderType.entityTranslucent(this.getTextureLocation(entity));
        VertexConsumer vertexconsumer = buffer.getBuffer(renderType);
        float f2 = entity.tickCount % 16 / 16.0F;
        float f3 = f2 + 0.0625F;
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, -0.5F, f2, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.5F, -0.5F, f3, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.5F, 0.5F, f3, 0.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, 0.5F, f2, 0.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        poseStack.pushPose();
        float x = (float) (Mth.lerp(partialTick, entity.xOld, entity.getX()));
        float y = (float) (Mth.lerp(partialTick, entity.yOld, entity.getY()));
        float z = (float) (Mth.lerp(partialTick, entity.zOld, entity.getZ()));
        Matrix4f transform = new Matrix4f();
        transform.rotate(Axis.YP.rotationDegrees(-MathHelper.positionToYaw(entity.getDeltaMovement()) - 90));
        transform.rotate(Axis.XP.rotationDegrees(-MathHelper.positionToPitch(entity.getDeltaMovement())));
        Vector4f a = transform.transform(new Vector4f(Mth.cos(age / 4.0F) * 0.15F, Mth.sin(age / 4.0F) * 0.15F, 0.0F, 1.0F));
        Vector4f b = transform.transform(new Vector4f(Mth.cos(age / 4.0F + Mth.TWO_PI / 3) * 0.15F, Mth.sin(age / 4.0F + Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
        Vector4f c = transform.transform(new Vector4f(Mth.cos(age / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, Mth.sin(age / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
        entity.trailA.prepareRender(new Vec3(x, y, z).add(a.x(), a.y() + entity.getBbHeight() / 2, a.z()), partialTick);
        entity.trailB.prepareRender(new Vec3(x, y, z).add(b.x(), b.y() + entity.getBbHeight() / 2, b.z()), partialTick);
        entity.trailC.prepareRender(new Vec3(x, y, z).add(c.x(), c.y() + entity.getBbHeight() / 2, c.z()), partialTick);
        poseStack.translate(-x, -y, -z);
        ColorUtil colorUtil = entity.isOrange() ? new ColorUtil(ChatFormatting.GOLD) : new ColorUtil(0xa4ffff);
        TrailRenderer.render(entity.trailA, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red, colorUtil.green, colorUtil.blue, 1, LightTexture.FULL_BRIGHT);
        TrailRenderer.render(entity.trailB, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red, colorUtil.green, colorUtil.blue, 1, LightTexture.FULL_BRIGHT);
        TrailRenderer.render(entity.trailC, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red, colorUtil.green, colorUtil.blue, 1, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float xPos, float yPos, float u, float v) {
        consumer.vertex(matrix4f, xPos, yPos, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public ResourceLocation getTextureLocation(SurgingOrb entity) {
        if (entity.isOrange()) {
            return ORANGE;
        } else {
            return TEXTURE_LOCATION;
        }
    }
}