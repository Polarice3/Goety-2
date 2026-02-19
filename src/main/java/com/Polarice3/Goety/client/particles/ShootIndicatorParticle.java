package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.common.entities.util.ShootIndicatorOwner;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShootIndicatorParticle extends Particle {
    private static final float MAX_WIDTH = 0.4F;

    private final int ownerId;
    private float width, oWidth;
    private Vec3 start, end;
    private float progress;
    private boolean updateStopped = false;

    protected ShootIndicatorParticle(ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz, int ownerId) {
        super(clientLevel, x, y, z);
        this.friction = 0;
        this.lifetime = 100;
        this.rCol = (float) dx;
        this.gCol = (float) dy;
        this.bCol = (float) dz;
        this.ownerId = ownerId;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        this.age++;
        if (this.start != null) {
            this.x = this.start.x();
            this.y = this.start.y();
            this.z = this.start.z();
        }
        this.oWidth = this.width;
        if (this.updateStopped) {
            this.width -= 0.05F;
        } else {
            this.width += 0.06F;
        }
        this.width = Mth.clamp(this.width, 0, MAX_WIDTH);
        if (this.oWidth <= 0 && this.width <= 0) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        if (this.level.getEntity(this.ownerId) instanceof ShootIndicatorOwner owner) {
            this.start = owner.getShootIndicatorStart(partialTick);
            this.updateStopped = !owner.shouldUpdateShootIndicator() && age >= 3;
            if (!this.updateStopped) {
                this.end = owner.getShootIndicatorEnd(partialTick);
                this.progress = owner.getShootIndicatorProgress(partialTick);
            }
        } else {
            this.updateStopped = true;
        }
        if (this.start != null && this.end != null && this.age >= 3) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer consumer = bufferSource.getBuffer(ModRenderType.DRAGON_RAYS_QUADS);
            Vec3 camPos = camera.getPosition();
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.translate(-camPos.x, -camPos.y, -camPos.z);
            double currentX = Mth.lerp(partialTick, this.xo, this.x);
            double currentY = Mth.lerp(partialTick, this.yo, this.y);
            double currentZ = Mth.lerp(partialTick, this.zo, this.z);
            Vec3 sight = camPos.subtract(currentX, currentY, currentZ).scale(-1);
            Vec3 offset = this.end.subtract(this.start);
            Vec3 sideOffset = offset.cross(sight).normalize().scale(Mth.lerp(partialTick, this.oWidth, this.width) / 2);
            PoseStack.Pose pose = stack.last();
            int startColor = FastColor.ARGB32.color(255, Math.round(this.rCol * 255), Math.round(this.gCol * 255), Math.round(this.bCol * 255));
            int endColor = FastColor.ARGB32.color(Math.round(Mth.lerp(this.progress, 0, 255)), Math.round(this.rCol * 255), Math.round(this.gCol * 255), Math.round(this.bCol * 255));
            vertex(consumer, pose, this.start.add(sideOffset), startColor);
            vertex(consumer, pose, this.start.add(sideOffset.scale(-1)), startColor);
            vertex(consumer, pose, this.end.add(sideOffset.scale(-1)), endColor);
            vertex(consumer, pose, this.end.add(sideOffset), endColor);
            stack.popPose();
            bufferSource.endBatch();
        }
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, int color) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z()).color(color).endVertex();
    }

    public static class Provider implements ParticleProvider<ShootIndicatorParticleOption> {
        @Override
        public Particle createParticle(ShootIndicatorParticleOption options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new ShootIndicatorParticle(level, x, y, z, dx, dy, dz, options.getOwnerId());
        }
    }
}
