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

public class SmashParticle extends Particle {
    private static final int SAMPLES = 32;
    private static final float SAMPLE_STEP = Mth.TWO_PI / SAMPLES;
    private static final int HEIGHT_SAMPLES = 25;
    private float width;
    private float height;
    private float speed;

    public SmashParticle(ClientLevel level, double x, double y, double z) {
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
        float smoothAge = (this.age + this.speed + partialTicks);
        PoseStack.Pose pose = stack.last();
        if (smoothAge < this.lifetime) {
            float circleAlpha = Easing.IN_CUBIC.interpolate(Mth.clamp(smoothAge / (lifetime / 1.25F), 0, 1), 1, 0);
            float radius = Easing.OUT_CUBIC.interpolate(smoothAge / lifetime, 0, width);
            float height = this.height;

            int baseColor = FastColor.ARGB32.color(Math.round(circleAlpha * 255), Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));
            int topColor = FastColor.ARGB32.color(0, Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.DRAGON_RAYS_QUADS);

            for (int i = 0; i < SAMPLES; i++) {
                float angle = i * SAMPLE_STEP;
                float x = Mth.cos(angle);
                float z = Mth.sin(angle);
                float nextX = Mth.cos(angle + SAMPLE_STEP);
                float nextZ = Mth.sin(angle + SAMPLE_STEP);
                consumer.vertex(pose.pose(), x * radius, 0, z * radius).color(baseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * radius, 0, nextZ * radius).color(baseColor).endVertex();
                consumer.vertex(pose.pose(), nextX * radius, height, nextZ * radius).color(topColor).endVertex();
                consumer.vertex(pose.pose(), x * radius, height, z * radius).color(topColor).endVertex();
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

    public static class Provider implements ParticleProvider<SmashParticleOption> {
        @Override
        public Particle createParticle(SmashParticleOption option, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmashParticle particle = new SmashParticle(level, x, y, z);
            particle.rCol = option.getRed();
            particle.gCol = option.getGreen();
            particle.bCol = option.getBlue();
            particle.width = option.getWidth();
            particle.height = option.getHeight();
            particle.speed = option.getSpeed();
            particle.lifetime = option.getLifespan();
            return particle;
        }
    }
}
