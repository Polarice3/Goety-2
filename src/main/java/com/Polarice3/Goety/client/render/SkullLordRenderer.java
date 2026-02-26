package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SkullLordModel;
import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SkullLordRenderer extends MobRenderer<SkullLord, SkullLordModel<SkullLord>> {
    public static final RenderType CONNECTION = RenderType.entityCutoutNoCull(Goety.location("textures/entity/skull_lord/skull_lord_connection.png"));
    private static final ResourceLocation LOCATION = Goety.location("textures/entity/skull_lord/skull_lord.png");
    private static final ResourceLocation VULNERABLE = Goety.location("textures/entity/skull_lord/skull_lord_vulnerable.png");
    private static final ResourceLocation CHARGE = Goety.location("textures/entity/skull_lord/skull_lord_charging.png");

    public SkullLordRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new SkullLordModel<>(p_174435_.bakeLayer(ModModelLayer.SKULL_LORD)), 0.5F);
    }

    @Override
    public void render(SkullLord entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        BoneLord boneLord = entity.getBoneLord();
        if (boneLord != null) {
            poseStack.pushPose();
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 start = new Vec3(
                    Mth.lerp(partialTicks, entity.xo, entity.getX()),
                    Mth.lerp(partialTicks, entity.yo, entity.getY()),
                    Mth.lerp(partialTicks, entity.zo, entity.getZ())
            );
            poseStack.translate(-start.x, -start.y, -start.z);
            Vec3 end = new Vec3(
                    Mth.lerp(partialTicks, boneLord.xo, boneLord.getX()),
                    Mth.lerp(partialTicks, boneLord.yo, boneLord.getY()) + boneLord.getEyeHeight(),
                    Mth.lerp(partialTicks, boneLord.zo, boneLord.getZ())
            );
            VertexConsumer vertexConsumer = buffer.getBuffer(CONNECTION);
            Vec3 offset = end.subtract(start);
            Vec3 sight = camPos.subtract(start).scale(-1);
            Vec3 sideOffset = offset.cross(sight).normalize();
            float age = entity.tickCount + partialTicks;
            float uOffset = age * 0.05f;
            PoseStack.Pose pose = poseStack.last();
            vertex(vertexConsumer, pose, start.add(sideOffset), uOffset, 0, packedLight);
            vertex(vertexConsumer, pose, start.add(sideOffset.scale(-1)), uOffset, 1, packedLight);
            vertex(vertexConsumer, pose, end.add(sideOffset.scale(-1)), (float) (offset.length() / 2) + uOffset, 1, packedLight);
            vertex(vertexConsumer, pose, end.add(sideOffset), (float) (offset.length() / 2) + uOffset, 0, packedLight);
            poseStack.popPose();
        }
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, float u, float v, int light) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z()).color(-1).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
    }

    protected int getBlockLightLevel(SkullLord pEntity, BlockPos pPos) {
        return 15;
    }

    protected float getWhiteOverlayProgress(SkullLord p_114043_, float p_114044_) {
        if (p_114043_.isShockWave()) {
            float f = p_114043_.getSwelling(p_114044_);
            return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
        } else {
            return super.getWhiteOverlayProgress(p_114043_, p_114044_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SkullLord pEntity) {
        if (pEntity.isCharging()){
            return CHARGE;
        } else {
            return pEntity.isInvulnerable() ? LOCATION: VULNERABLE;
        }
    }
}
