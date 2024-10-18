package com.Polarice3.Goety.client.particles;

import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.Vec3Util;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Stolen from @cerbon's Cerbon API codes:<a href="https://github.com/CERBON-MODS/CERBONs-API/blob/master/Common/src/main/java/com/cerbon/cerbons_api/api/general/particle/SimpleParticle.java">...</a>
 */
public class GeometricParticle extends TextureSheetParticle {
    private final ParticleContext particleContext;
    private final IParticleGeometry particleGeometry;
    private final boolean cycleSprites;

    private Function<GeometricParticle, Float> rotationOverride;
    private Function<GeometricParticle, Vec3> velocityOverride;
    private Function<GeometricParticle, Vec3> positionOverride;
    private Function<Float, Float> scaleOverride;
    private Function<Float, ColorUtil> colorOverride;
    private Function<Float, Integer> brightnessOverride;
    private Vec3 colorVariation = Vec3.ZERO;

    private float prevRotation = 0f;
    private float rotation = 0f;
    public float ageRatio = 1f;

    public GeometricParticle(ParticleContext particleContext, int particleAge, IParticleGeometry particleGeometry, boolean cycleSprites, boolean doCollision) {
        super(particleContext.level, particleContext.pos().x(), particleContext.pos().y(), particleContext.pos().z());
        this.particleContext = particleContext;
        this.lifetime = particleAge;
        this.particleGeometry = particleGeometry;
        this.cycleSprites = cycleSprites;
        this.hasPhysics = doCollision;
        this.xd = particleContext.vel().x();
        this.yd = particleContext.vel().y();
        this.zd = particleContext.vel().z();

        if (cycleSprites) {
            this.setSpriteFromAge(particleContext.spriteSet());
        } else {
            this.setSprite(particleContext.spriteSet().get(this.random));
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public @NotNull Vec3 getPos() {
        return new Vec3(x, y, z);
    }

    public int getAge() {
        return this.age;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isAlive()) return;

        if (this.cycleSprites) {
            this.setSpriteFromAge(this.particleContext.spriteSet());
        }
        this.ageRatio = (float) this.age / this.lifetime;
        this.setColorFromOverride(this.colorOverride, this.ageRatio);
        this.setScaleFromOverride(this.scaleOverride, this.ageRatio);
        this.setVelocityFromOverride(this.velocityOverride);
        this.setPositionFromOverride(this.positionOverride);
        this.setRotationFromOverride(this.rotationOverride);

    }

    private void setRotationFromOverride(Function<GeometricParticle, Float> rotationOverride) {
        if (rotationOverride == null) {
            return;
        }

        float rot = rotationOverride.apply(this);
        this.prevRotation = this.rotation;
        this.rotation = rot;
    }

    private void setVelocityFromOverride(Function<GeometricParticle, Vec3> velocityOverride) {
        if (velocityOverride == null) {
            return;
        }

        Vec3 velocity = velocityOverride.apply(this);
        this.xd = velocity.x();
        this.yd = velocity.y();
        this.zd = velocity.z();
    }

    private void setPositionFromOverride(Function<GeometricParticle, Vec3> positionOverride) {
        if (positionOverride == null) {
            return;
        }

        Vec3 pos = positionOverride.apply(this);
        this.setPos(pos.x(), pos.y(), pos.z());
    }

    private void setScaleFromOverride(Function<Float, Float> scaleOverride, float ageRatio) {
        if (scaleOverride == null) return;

        this.quadSize = scaleOverride.apply(ageRatio);
        this.setSize(0.2f * this.quadSize, 0.2f * this.quadSize);
    }

    private void setColorFromOverride(Function<Float, ColorUtil> colorOverride, float ageRatio) {
        if (colorOverride == null) {
            return;
        }

        ColorUtil color = colorOverride.apply(ageRatio);
        Vec3 vec3 = new Vec3(color.red, color.green, color.blue);
        Vec3 variedColor = Vec3Util.coerceAtMost(Vec3Util.coerceAtLeast(vec3.add(colorVariation), Vec3.ZERO), Vec3Util.unit);
        this.setColor((float) variedColor.x(), (float) variedColor.y(), (float) variedColor.z());
    }

    public void setBrightnessOverride(Function<Float, Integer> override) {
        this.brightnessOverride = override;
    }

    public void setColorOverride(Function<Float, ColorUtil> override) {
        this.colorOverride = override;
        this.setColorFromOverride(override, 0.0F);
    }

    public void setScaleOverride(Function<Float, Float> override) {
        this.scaleOverride = override;
        this.setScaleFromOverride(override, 0.0F);
    }

    public void setColorVariation(double variation) {
        this.colorVariation = Vec3Util.randVec(this.random).scale(variation);
        this.setColorFromOverride(this.colorOverride, 0.0F);
    }

    public void setVelocityOverride(Function<GeometricParticle, Vec3> override) {
        this.velocityOverride = override;
    }

    public void setPositionOverride(Function<GeometricParticle, Vec3> override) {
        this.positionOverride = override;
    }

    public void setRotationOverride(Function<GeometricParticle, Float> override) {
        this.rotationOverride = override;

        if (this.rotationOverride != null) {
            this.rotation = this.rotationOverride.apply(this);
            this.prevRotation = this.rotationOverride.apply(this);
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return this.brightnessOverride != null ? this.brightnessOverride.apply(this.ageRatio) : super.getLightColor(partialTick);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTicks) {
        Vector3f[] vector3fs = this.particleGeometry.getGeometry(
                camera,
                partialTicks,
                this.xo, this.yo, this.zo,
                this.x, this.y, this.z,
                this.getQuadSize(partialTicks),
                Mth.lerp(partialTicks, this.prevRotation, this.rotation)
        );

        float l = this.getU0();
        float m = this.getU1();
        float n = this.getV0();
        float o = this.getV1();
        float p = getLightColor(partialTicks);

        vertexConsumer.vertex(
                vector3fs[0].x(), vector3fs[0].y(),
                vector3fs[0].z()
        ).uv(m, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2((int) p).endVertex();

        vertexConsumer.vertex(
                vector3fs[1].x(), vector3fs[1].y(),
                vector3fs[1].z()
        ).uv(m, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2((int) p).endVertex();

        vertexConsumer.vertex(
                vector3fs[2].x(), vector3fs[2].y(),
                vector3fs[2].z()
        ).uv(l, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2((int) p).endVertex();

        vertexConsumer.vertex(
                vector3fs[3].x(), vector3fs[3].y(),
                vector3fs[3].z()
        ).uv(l, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2((int) p).endVertex();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private final Function<ParticleContext, Particle> particleProvider;

        public Provider(SpriteSet spriteSet, Function<ParticleContext, Particle> particleProvider) {
            this.spriteSet = spriteSet;
            this.particleProvider = particleProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return particleProvider.apply(new ParticleContext(spriteSet, level, new Vec3(x, y, z), new Vec3(xSpeed, ySpeed, zSpeed), true));
        }
    }

    public record ParticleContext(
            SpriteSet spriteSet,
            ClientLevel level,
            Vec3 pos,
            Vec3 vel,
            Boolean cycleSprites
    ) {
    }

    @FunctionalInterface
    public interface IParticleGeometry {
        Vector3f[] getGeometry(
                Camera camera,
                float tickDelta,
                double prevPosX,
                double prevPosY,
                double prevPosZ,
                double x,
                double y,
                double z,
                float scale,
                float rotation
        );
    }

    public static class Geometries {

        public static Vector3f[] buildBillBoardGeometry(
                @NotNull Camera camera,
                float tickDelta,
                double prevPosX,
                double prevPosY,
                double prevPosZ,
                double x,
                double y,
                double z,
                float scale,
                float rotation
        ) {
            Quaternion quaternion = new Quaternion(camera.rotation());
            Vec3 vec3 = camera.getPosition();
            float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
            float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
            float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());

            Vector3f[] vector3fs = {
                    new Vector3f(-1.0f, -1.0f, 0.0f),
                    new Vector3f(-1.0f, 1.0f, 0.0f),
                    new Vector3f(1.0f, 1.0f, 0.0f),
                    new Vector3f(1.0f, -1.0f, 0.0f)
            };

            for (int k = 0; k <= 3; k++) {
                Vector3f vector3f2 = vector3fs[k];
                vector3f2.transform(Vector3f.ZP.rotationDegrees(rotation));
                vector3f2.transform(quaternion);
                vector3f2.mul(scale);
                vector3f2.add(f, g, h);
            }

            return vector3fs;
        }

        public static Vector3f @NotNull [] buildFlatGeometry(
                @NotNull Camera camera,
                float tickDelta,
                double prevPosX,
                double prevPosY,
                double prevPosZ,
                double x,
                double y,
                double z,
                float scale,
                float rotation
        ) {
            Vec3 vec3 = camera.getPosition();
            float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
            float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
            float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());

            Vector3f[] vector3fs = new Vector3f[] {
                    new Vector3f(-1.0f, 0.0f, -1.0f),
                    new Vector3f(-1.0f, 0.0f, 1.0f),
                    new Vector3f(1.0f, 0.0f, 1.0f),
                    new Vector3f(1.0f, 0.0f, -1.0f)
            };

            for (int k = 0; k <= 3; k++) {
                Vector3f vector3f2 = vector3fs[k];
                vector3f2.transform(Vector3f.YP.rotationDegrees(rotation));
                vector3f2.mul(scale);
                vector3f2.add(f, g, h);
            }

            return vector3fs;
        }
    }
}
