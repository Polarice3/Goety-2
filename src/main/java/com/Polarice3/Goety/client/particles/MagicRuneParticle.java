package com.Polarice3.Goety.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class MagicRuneParticle extends NoRenderParticle {
    private static final float DEFAULT_R = 0x3D / 255.0F;
    private static final float DEFAULT_G = 0.0F;
    private static final float DEFAULT_B = 1.0F;
    private static final int DURATION = 10;
    private static final float RING_RADIUS = 0.25F;
    private static final float RING_HEIGHT = 0.75F;
    private static final int BURST_RUNES_TICK = 26;
    private static final int BURST_RUNES = 7;
    private static final float LOOP_RUNES_PER_TICK = 15.0F / 20.0F;
    private static final float SWIRL_MAX = 0.02F;
    private final float tintR;
    private final float tintG;
    private final float tintB;
    private float runeBudget;
    private final SpriteSet sprites;

    protected MagicRuneParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        boolean noTint = r <= 0.0F && g <= 0.0F && b <= 0.0F;
        this.tintR = noTint ? DEFAULT_R : r;
        this.tintG = noTint ? DEFAULT_G : g;
        this.tintB = noTint ? DEFAULT_B : b;
        this.lifetime = DURATION;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.runeBudget += LOOP_RUNES_PER_TICK;
        while (this.runeBudget >= 1.0F) {
            this.runeBudget -= 1.0F;
            this.spawnRune(false);
        }
        if (this.age == BURST_RUNES_TICK) {
            for (int i = 0; i < BURST_RUNES; i++) {
                this.spawnRune(true);
            }
        }
    }

    private void spawnRune(boolean burst) {
        float angle = this.random.nextFloat() * Mth.TWO_PI;
        double px = this.x + Mth.cos(angle) * RING_RADIUS;
        double py = this.y + this.random.nextFloat() * RING_HEIGHT;
        double pz = this.z + Mth.sin(angle) * RING_RADIUS;
        double up = burst ? this.randomBetween(0.75F, 1.0F) : this.randomBetween(0.25F, 0.5F);
        double out = burst ? up * 0.35D : 0.0D;
        float size = this.randomBetween(0.25F, 0.5F);
        int life = burst ? 25 + this.random.nextInt(10) : 30 + this.random.nextInt(15);
        engine().add(new IllagerRuneParticle(this.level, px, py, pz,
                Mth.cos(angle) * out, up, Mth.sin(angle) * out,
                this.x, this.z, this.randomSwirl(),
                this.tintR, this.tintG, this.tintB, this.runeSprite(), size, life));
    }

    private float randomSwirl() {
        return (this.random.nextFloat() * 2.0F - 1.0F) * SWIRL_MAX;
    }

    private float randomBetween(float min, float max) {
        return min + this.random.nextFloat() * (max - min);
    }

    private TextureAtlasSprite runeSprite() {
        return this.sprites.get(0, 1);
    }

    private static ParticleEngine engine() {
        return Minecraft.getInstance().particleEngine;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double r, double g, double b) {
            return new MagicRuneParticle(level, x, y, z, (float) r, (float) g, (float) b, this.sprites);
        }
    }
}
