package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SlamParticle extends Particle {
    private static final int SAMPLES = 32;
    private static final float SAMPLE_STEP = Mth.TWO_PI / SAMPLES;

    private static final int HEIGHT_SAMPLES = 25;

    private float size;

    public SlamParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 camPos = camera.getPosition();
        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.translate(x - camPos.x, y - camPos.y, z - camPos.z);
        float smoothAge = (age + partialTicks);
        PoseStack.Pose pose = stack.last();
        if (smoothAge < lifetime) {
            float circleAlpha = Easing.IN_CUBIC.interpolate(Mth.clamp(smoothAge / (lifetime / 1.25F), 0, 1), 1, 0);
            float smallCircleAlpha = Easing.IN_QUINT.interpolate(Mth.clamp(smoothAge / lifetime, 0, 1), 1, 0);
            float sphereAlpha = smoothAge < lifetime / 5.0F
                    ? Easing.OUT_QUART.interpolate(smoothAge / (lifetime / 5.0F), 0, 1)
                    : Easing.OUT_CUBIC.interpolate(Mth.clamp((smoothAge - lifetime / 5.0F) / (lifetime / 5.0F), 0, 1), 1, 0);
            float radius = Easing.OUT_CUBIC.interpolate(smoothAge / lifetime, 0, size);
            float smallRadius = Easing.OUT_QUART.interpolate(smoothAge / lifetime, 0, size / 1.5F);
            float sphereRadius = Easing.OUT_SINE.interpolate(smoothAge / lifetime, size * 0.36F, size * 0.48F);
            float height = size * 0.06F;

            int baseColor = FastColor.ARGB32.color(Math.round(circleAlpha * 255), Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));
            int smallBaseColor = FastColor.ARGB32.color(Math.round(smallCircleAlpha * 255), Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));
            int topColor = FastColor.ARGB32.color(0, Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));
            int sphereColor = FastColor.ARGB32.color(Math.round(sphereAlpha * 255), Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.DRAGON_RAYS_QUADS);

            for (int i = 0; i < SAMPLES; i++) {
                float angle = i * SAMPLE_STEP;
                float x = Mth.cos(angle);
                float z = Mth.sin(angle);
                float nextX = Mth.cos(angle + SAMPLE_STEP);
                float nextZ = Mth.sin(angle + SAMPLE_STEP);

                consumer.vertex(pose.pose(), x * smallRadius, 0, z * smallRadius).color(smallBaseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * smallRadius, 0, nextZ * smallRadius).color(smallBaseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * smallRadius, height, nextZ * smallRadius).color(topColor).endVertex();
                consumer.vertex(pose.pose(), x * smallRadius, height, z * smallRadius).color(topColor).endVertex();

                consumer.vertex(pose.pose(), x * radius, 0, z * radius).color(baseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * radius, 0, nextZ * radius).color(baseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * radius, height, nextZ * radius).color(topColor).endVertex();
                consumer.vertex(pose.pose(), x * radius, height, z * radius).color(topColor).endVertex();

                if (sphereAlpha > 0) {
                    for (int j = 0; j < HEIGHT_SAMPLES; j++) {
                        float sphereHeight = sphereRadius * j / HEIGHT_SAMPLES;
                        float nextSphereHeight = sphereRadius * (j + 1) / HEIGHT_SAMPLES;
                        float currentRadius = Mth.sqrt(Math.max(sphereRadius * sphereRadius - sphereHeight * sphereHeight, 0));
                        float nextRadius = Mth.sqrt(Math.max(sphereRadius * sphereRadius - nextSphereHeight * nextSphereHeight, 0));
                        consumer.vertex(pose.pose(), x * currentRadius, sphereHeight, z * currentRadius).color(sphereColor).endVertex();
                        consumer.vertex(pose.pose(), nextX * currentRadius, sphereHeight, nextZ * currentRadius).color(sphereColor).endVertex();
                        consumer.vertex(pose.pose(), nextX * nextRadius, nextSphereHeight, nextZ * nextRadius).color(sphereColor).endVertex();
                        consumer.vertex(pose.pose(), x * nextRadius, nextSphereHeight, z * nextRadius).color(sphereColor).endVertex();
                    }
                }
            }
        }
        stack.popPose();
        bufferSource.endBatch();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider implements ParticleProvider<SlamParticleOption> {
        @Override
        public Particle createParticle(SlamParticleOption option, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SlamParticle particle = new SlamParticle(level, x, y, z);
            particle.rCol = option.getRed();
            particle.gCol = option.getGreen();
            particle.bCol = option.getBlue();
            particle.size = option.getSize();
            particle.lifetime = option.getLifespan();
            return particle;
        }
    }
}
