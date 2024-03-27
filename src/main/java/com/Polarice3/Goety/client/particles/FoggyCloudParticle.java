package com.Polarice3.Goety.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Based on Iron's Fog Particle: <a href="https://github.com/iron431/irons-spells-n-spellbooks/blob/1.19.2/src/main/java/io/redspace/ironsspellbooks/particle/FogParticle.java">...</a>
 */
public class FoggyCloudParticle extends GroundCircleParticle{
    private final int speed;

    public FoggyCloudParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xd, double yd, double zd, FoggyCloudParticleOption options) {
        super(pLevel, pX, pY, pZ, 0, 0, 0);
        float magnitude = 0.3F;
        this.xd = xd + (Math.random() * 2.0D - 1.0D) * magnitude;
        this.yd = yd + (Math.random() * 2.0D - 1.0D) * magnitude;
        this.zd = zd + (Math.random() * 2.0D - 1.0D) * magnitude;
        double d0 = (Math.random() + Math.random() + 1.0D) * magnitude * 0.3F;
        double d1 = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / d1 * d0 * magnitude;
        this.yd = this.yd / d1 * d0 * magnitude + magnitude * 0.25F;
        this.zd = this.zd / d1 * d0 * magnitude;

        this.quadSize = 1.5F * options.getSize();
        this.lifetime = pLevel.random.nextIntBetweenInclusive(60, 120);
        this.gravity = options.hasGravity() ? 0.1F : 0.0F;
        this.speed = options.getSpeed();

        float f = random.nextFloat() * 0.14F + 0.85F;
        this.rCol = options.getRed() * f;
        this.gCol = options.getGreen() * f;
        this.bCol = options.getBlue() * f;
        this.friction = 1;
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        return this.quadSize * (1.0F + Mth.clamp((this.age + pScaleFactor) / (float) this.lifetime * 0.75F, 0.0F, 1.0F)) * Mth.clamp(this.age / 5.0F, 0, 1);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.age++;
        this.age += this.speed;
        if (this.age >= this.lifetime) {
            this.remove();
        } else {
            if (this.gravity > 0.0F) {
                this.yd -= 0.04D * (double) this.gravity;
            }
            this.move(this.xd, this.yd, this.zd);
            this.yd *= 0.85F;
            this.xd *= 0.94F;
            this.zd *= 0.94F;
        }
    }

    private float noise(float offset) {
        return 10.0F * Mth.sin(offset * 0.01F);
    }

    @Override
    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        this.alpha = 1.0F - Mth.clamp(((float) this.age + p_233987_) / (float) this.lifetime, 0.0F, 1.0F);
        super.render(p_233985_, p_233986_, p_233987_);
    }

    public void makeCornerVertex(VertexConsumer pConsumer, Vector3f pVec3f, float p_233996_, float p_233997_, int p_233998_) {
        Vec3 wiggle = new Vec3(noise((float) (age + this.x)), noise((float) (age - this.x)), noise((float) (age + this.z))).scale(0.02F);
        pConsumer.vertex(pVec3f.x() + wiggle.x, pVec3f.y() + 0.08F + alpha * 0.125F, pVec3f.z() + wiggle.z).uv(p_233996_, p_233997_).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p_233998_).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z).above();
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    public static class Provider implements ParticleProvider<FoggyCloudParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        public Particle createParticle(FoggyCloudParticleOption options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            FoggyCloudParticle foggyCloudParticle = new FoggyCloudParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, options);
            foggyCloudParticle.pickSprite(this.sprite);
            foggyCloudParticle.setAlpha(1.0F);
            return foggyCloudParticle;
        }
    }
}
