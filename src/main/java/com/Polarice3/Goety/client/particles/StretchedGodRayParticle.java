package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class StretchedGodRayParticle extends TextureSheetParticle {
    private final float halfLife;

    public StretchedGodRayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.lifetime = 20;
        this.halfLife = this.lifetime / 2.0F;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.quadSize = 1.0F;
        this.alpha = 0.0F;
        this.rCol = (float) xSpeed;
        this.gCol = (float) ySpeed;
        this.bCol = (float) zSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        float agePercent = (float) this.age / this.lifetime;
        float maxAlpha = 0.6F;
        if (agePercent <= 0.2F) {
            this.alpha = (agePercent / 0.2F) * maxAlpha;
        } else if (agePercent >= 0.7F) {
            this.alpha = maxAlpha * (1.0F - ((agePercent - 0.7F) / 0.3F));
        } else {
            this.alpha = maxAlpha;
        }
    }

    @Override
    protected int getLightColor(float light) {
        return LightTexture.FULL_BLOCK;
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
            float flashProgress = Easing.IN_OUT_SINE.calculate(1 - Math.abs(smoothAge - halfLife) / halfLife);
            float flashDistFromCenter = Mth.lerp(flashProgress, (smoothAge < halfLife ? 0.75F : 0.9F) * 0.5F, 0.65F * 0.5F);
            float flashHeight = 5.0F * flashProgress;

            int baseColor = FastColor.ARGB32.color(Math.round(Mth.lerp(flashProgress, 0.5F, 1.0F) * 255), Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));
            int topColor = FastColor.ARGB32.color(0, Math.round(rCol * 255), Math.round(gCol * 255), Math.round(bCol * 255));

            VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.DRAGON_RAYS_QUADS);

            consumer.vertex(pose.pose(), -flashDistFromCenter, 0, -flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), -flashDistFromCenter, 0, flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), -flashDistFromCenter, flashHeight, flashDistFromCenter).color(topColor).endVertex();
            consumer.vertex(pose.pose(), -flashDistFromCenter, flashHeight, -flashDistFromCenter).color(topColor).endVertex();

            consumer.vertex(pose.pose(), -flashDistFromCenter, 0, -flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, 0, -flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, flashHeight, -flashDistFromCenter).color(topColor).endVertex();
            consumer.vertex(pose.pose(), -flashDistFromCenter, flashHeight, -flashDistFromCenter).color(topColor).endVertex();

            consumer.vertex(pose.pose(), flashDistFromCenter, 0, -flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, 0, flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, flashHeight, flashDistFromCenter).color(topColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, flashHeight, -flashDistFromCenter).color(topColor).endVertex();

            consumer.vertex(pose.pose(), -flashDistFromCenter, 0, flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, 0, flashDistFromCenter).color(baseColor).endVertex();
            consumer.vertex(pose.pose(), flashDistFromCenter, flashHeight, flashDistFromCenter).color(topColor).endVertex();
            consumer.vertex(pose.pose(), -flashDistFromCenter, flashHeight, flashDistFromCenter).color(topColor).endVertex();
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
        return ModRenderType.PARTICLE_ADDITIVE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            StretchedGodRayParticle particle = new StretchedGodRayParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
