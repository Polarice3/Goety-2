package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;

/**
 * Based on AbstractTrailParticle code from L_Ender's Cataclysm:<a href="https://github.com/PandaLunatique/L_Ender-s-Cataclysm/blob/1142477a2f4159ce4f75a194c682000ded1369d5/src/main/java/com/github/L_Ender/cataclysm/client/particle/AbstractTrailParticle.java">...</a>;
 */
public abstract class WindTrailParticle extends Particle {
    private static final ResourceLocation TEXTURE = Goety.location("textures/particle/trail.png");
    public final Vec3[] trailPositions = new Vec3[64];
    public int trailPointer = -1;
    public float trailA = 1.0F;

    public WindTrailParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue) {
        super(world, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.rCol = red;
        this.gCol = green;
        this.bCol = blue;
    }

    public void tick() {
        this.trail();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd *= 0.99D;
        this.yd *= 0.99D;
        this.zd *= 0.99D;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double)this.gravity;
        }
    }

    public void trail() {
        Vec3 trailAt = new Vec3(this.x, this.y, this.z);
        if (this.trailPointer == -1) {
            Arrays.fill(this.trailPositions, trailAt);
        }

        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }

        this.trailPositions[this.trailPointer] = trailAt;
    }

    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        if (this.trailPointer > -1) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTexture()));
            Vec3 cameraPos = camera.getPosition();
            double x = Mth.lerp(partialTick, this.xo, this.x);
            double y = Mth.lerp(partialTick, this.yo, this.y);
            double z = Mth.lerp(partialTick, this.zo, this.z);
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            int samples = 0;
            Vec3 drawFrom = new Vec3(x, y, z);
            float zRot = this.getCameraRot(camera);
            Vec3 topAngleVec = (new Vec3(0.0, this.getTrailHeight() / 2.0D, 0.0)).zRot(zRot);
            Vec3 bottomAngleVec = (new Vec3(0.0, this.getTrailHeight() / -2.0D, 0.0)).zRot(zRot);

            while (samples < this.sampleSize()){
                Vec3 sample = this.getTrailPosition(samples * this.sampleStep(), partialTick);
                Vec3 draw1 = drawFrom;
                float u1 = (float)samples / (float)this.sampleSize();
                float u2 = u1 + 1.0F / (float)this.sampleSize();
                PoseStack.Pose last = poseStack.last();
                Matrix4f matrix4f = last.pose();
                Matrix3f matrix3f = last.normal();
                vertexConsumer.vertex(matrix4f, (float)draw1.x + (float)bottomAngleVec.x, (float)draw1.y + (float)bottomAngleVec.y, (float)draw1.z + (float)bottomAngleVec.z).color(this.rCol, this.gCol, this.bCol, this.trailA).uv(u1, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(this.getLightColor(partialTick)).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(matrix4f, (float)sample.x + (float)bottomAngleVec.x, (float)sample.y + (float)bottomAngleVec.y, (float)sample.z + (float)bottomAngleVec.z).color(this.rCol, this.gCol, this.bCol, this.trailA).uv(u2, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(this.getLightColor(partialTick)).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(matrix4f, (float)sample.x + (float)topAngleVec.x, (float)sample.y + (float)topAngleVec.y, (float)sample.z + (float)topAngleVec.z).color(this.rCol, this.gCol, this.bCol, this.trailA).uv(u2, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(this.getLightColor(partialTick)).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(matrix4f, (float)draw1.x + (float)topAngleVec.x, (float)draw1.y + (float)topAngleVec.y, (float)draw1.z + (float)topAngleVec.z).color(this.rCol, this.gCol, this.bCol, this.trailA).uv(u1, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(this.getLightColor(partialTick)).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                ++samples;
                drawFrom = sample;
            }

            bufferSource.endBatch();
            poseStack.popPose();
        }

    }

    public float getCameraRot(Camera camera) {
        return (float) (-(Math.PI / 180.0F) * camera.getXRot());
    }

    public float getTrailHeight() {
        return 0.5F;
    }

    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    public int sampleSize() {
        return 1;
    }

    public int sampleStep() {
        return 1;
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.removed) {
            partialTick = 1.0F;
        }

        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}
