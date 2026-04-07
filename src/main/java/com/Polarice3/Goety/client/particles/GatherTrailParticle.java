package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GatherTrailParticle extends TextureSheetParticle {
    private final float red, green, blue;
    public final Vec3 fromPos;
    public final Vec3 toPos;

    protected GatherTrailParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, float red, float green, float blue, Vec3 end, SpriteSet spriteSet) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.fromPos = new Vec3(x, y, z);
        this.toPos = end;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.lifetime = Math.round(this.random.nextFloat() * 3.0F) + 5;
        this.pickSprite(spriteSet);
    }

    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);
        double currentX = Mth.lerp(partialTick, this.xo, this.x);
        double currentY = Mth.lerp(partialTick, this.yo, this.y);
        double currentZ = Mth.lerp(partialTick, this.zo, this.z);
        Vec3 sight = camPos.subtract(currentX, currentY, currentZ).scale(-1);
        Vec3 direction = toPos.subtract(fromPos);
        Vec3 start = new Vec3(currentX, currentY, currentZ).add(direction.scale(Easing.IN_OUT_QUAD.interpolate(Math.min(age + partialTick, lifetime) / lifetime, 0, 1)));
        Vec3 end = start.add(direction.scale(Easing.IN_OUT_QUAD.interpolate(Mth.abs(Math.min(age + partialTick, lifetime) / lifetime - 0.5f) * 2, 0.5F, 0)));
        if (end.distanceTo(fromPos) > direction.length()) {
            end = toPos;
        }
        Vec3 offset = end.subtract(start);
        Vec3 sideOffset = offset.cross(sight).normalize().scale(0.03);
        PoseStack.Pose pose = stack.last();
        float u0 = this.getU0();
        float u1 = Easing.IN_OUT_QUAD.interpolate(Mth.abs(Math.min(age + partialTick, lifetime) / lifetime - 0.5f) * 2, this.getU1(), this.getU0());
        float v0 = this.getV0();
        float v1 = this.getV1();
        vertex(consumer, pose, start.add(sideOffset), u0, v0, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, start.add(sideOffset.scale(-1)), u0, v1, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, end.add(sideOffset.scale(-1)), u1, v1, LightTexture.FULL_BRIGHT);
        vertex(consumer, pose, end.add(sideOffset), u1, v0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, float u, float v, int light) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z()).uv(u, v).color(red, green, blue, alpha).uv2(light).endVertex();
    }

    public static class Provider implements ParticleProvider<GatherTrailParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(GatherTrailParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new GatherTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType.getRed(), pType.getGreen(), pType.getBlue(), new Vec3(pType.getEndX(), pType.getEndY(), pType.getEndZ()), sprites);
        }
    }
}
