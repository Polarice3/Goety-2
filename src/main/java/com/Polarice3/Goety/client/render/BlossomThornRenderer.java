package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.BlossomThorn;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.Vec3Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Function;

/**
 * Stolen and modified from @cerbon's codes: <a href="https://github.com/CERBON-MODS/Bosses-of-Mass-Destruction-FORGE/blob/1.20.1/src/main/java/com/cerbon/bosses_of_mass_destruction/entity/custom/void_blossom/VoidBlossomSpikeRenderer.java">...</a>
 * */
public class BlossomThornRenderer extends EntityRenderer<BlossomThorn> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/projectiles/blossom_thorn.png");

    public BlossomThornRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(BlossomThorn entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (!entity.sentTrapEvent){
            return;
        }
        this.renderBeam(entity, partialTicks, poseStack, buffer, RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
    }

    private void renderBeam(BlossomThorn blossomThorn, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, RenderType type){
        float numTextures = 8.0f;
        float lifeRatio = 2.0f;
        int maxAge = 20;
        int age = maxAge - blossomThorn.lifeTicks;
        float textureProgress = Math.max(0.0F, (((age + tickDelta) * lifeRatio) / maxAge) - lifeRatio + 1);
        if (textureProgress >= 1) {
            return;
        }

        float spikeHeight = blossomThorn.height;
        float textureRatio = 22.0F / 64.0F;
        float spikeWidth = textureRatio * spikeHeight * 0.5F;
        double upProgress = (Math.sin((Math.min((age + tickDelta) / (maxAge * 0.4D), 1.0D)) * Math.PI * 0.5D) - 1.0D) * spikeHeight;
        Function<Float, Float> texTransformer = textureMultiplier(1 / numTextures, (float) (Math.floor(textureProgress * numTextures) / numTextures));
        poseStack.pushPose();
        Vec3 offset = fromLerpedPosition(blossomThorn, 0.0D, tickDelta).subtract(blossomThorn.position());
        poseStack.translate(-offset.x, upProgress - offset.y, -offset.z);
        Vec3 bottomPos = blossomThorn.randVec.add(Vec3Util.yAxis.scale(spikeHeight)).normalize();
        float n = (float) Math.acos(bottomPos.y);
        float o = (float) Math.atan2(bottomPos.z, bottomPos.x);
        float pi = (float) Math.PI;
        float rTD = 57.295776F;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(((pi / 2.0F) - o) * rTD));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(n * rTD));
        float q = 0.0F;
        int red = (int) (ColorUtil.WHITE.red * 255);
        int green = (int) (ColorUtil.WHITE.green * 255);
        int blue = (int) (ColorUtil.WHITE.blue * 255);
        float af = Mth.cos(q + pi) * spikeWidth;
        float ag = Mth.sin(q + pi) * spikeWidth;
        float ah = Mth.cos(q + 0.0F) * spikeWidth;
        float ai = Mth.sin(q + 0.0F) * spikeWidth;
        float aj = Mth.cos(q + (pi / 2.0F)) * spikeWidth;
        float ak = Mth.sin(q + (pi / 2.0F)) * spikeWidth;
        float al = Mth.cos(q + 4.712389F) * spikeWidth;
        float am = Mth.sin(q + 4.712389F) * spikeWidth;
        VertexConsumer vertexConsumer= bufferSource.getBuffer(type);
        PoseStack.Pose entry = poseStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        float c0 = texTransformer.apply(0.4999F);
        float c2 = texTransformer.apply(0.0F);
        float c1 = texTransformer.apply(1.0F);
        vertex(vertexConsumer, matrix4f, matrix3f, af, spikeHeight, ag, red, green, blue, c0, 0.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0f, ag, red, green, blue, c0, 1.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0f, ai, red, green, blue, c2, 1.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, spikeHeight, ai, red, green, blue, c2, 0.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, spikeHeight, ak, red, green, blue, c1, 0.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0f, ak, red, green, blue, c1, 1.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0f, am, red, green, blue, c0, 1.0f);
        vertex(vertexConsumer, matrix4f, matrix3f, al, spikeHeight, am, red, green, blue, c0, 0.0f);
        poseStack.popPose();
    }

    public static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, float h, int red, int green, int blue, float l, float m) {
        vertexConsumer.vertex(matrix4f, f, g, h)
                .color(red, green, blue, 255)
                .uv(l, m)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728800)
                .normal(matrix3f, 0.0F, 0.0F, -1.0F)
                .endVertex();
    }

    public Function<Float, Float> textureMultiplier(Float multiplier, float adjustment) {
        return texCoord -> texCoord * multiplier + adjustment;
    }

    public static float randFloat(Random random, float range) {
        return (random.nextFloat() - 0.5F) * 2 * range;
    }

    public static Vec3 fromLerpedPosition(Entity entity, double yOffset, float delta) {
        double d = Mth.lerp(delta, entity.xOld, entity.getX());
        double e = Mth.lerp(delta, entity.yOld, entity.getY()) + yOffset;
        double f = Mth.lerp(delta, entity.zOld, entity.getZ());
        return new Vec3(d, e, f);
    }

    @Override
    public ResourceLocation getTextureLocation(BlossomThorn p_114482_) {
        return TEXTURE_LOCATION;
    }
}
