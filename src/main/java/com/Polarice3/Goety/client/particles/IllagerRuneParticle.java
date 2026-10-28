package com.Polarice3.Goety.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

public class IllagerRuneParticle extends VortexParticle {
    public static final float[] RUNE_ALPHA = { 0.000F, 0.074F, 0.259F, 0.500F, 0.741F, 0.926F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F };
    public static final float[] RUNE_HEAT = { 0.020F, 0.062F, 0.174F, 0.331F, 0.511F, 0.691F, 0.848F, 0.959F, 1.000F, 0.943F, 0.885F, 0.826F, 0.768F, 0.709F, 0.651F, 0.593F, 0.534F, 0.476F, 0.417F, 0.359F, 0.300F };
    public static final float[] RUNE_SIZE = { 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 1.000F, 0.980F, 0.917F, 0.799F, 0.615F, 0.352F, 0.000F };
    public static final int FRAMES = 41;
    private static final int GRID = 8;
    private final float baseSize;
    private final float runeU0;
    private final float runeU1;
    private final float runeV0;
    private final float runeV1;

    public IllagerRuneParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, double centerX, double centerZ, float swirl, float tintR, float tintG, float tintB, TextureAtlasSprite atlas, float size, int lifetime) {
        super(level, x, y, z, xd, yd, zd, centerX, centerZ, swirl, tintR, tintG, tintB, lifetime);
        this.setSprite(atlas);
        int frame = this.random.nextInt(FRAMES);
        int col = frame % GRID;
        int row = frame / GRID;
        float cell = 16.0F / GRID;
        this.runeU0 = atlas.getU(col * cell);
        this.runeU1 = atlas.getU((col + 1) * cell);
        this.runeV0 = atlas.getV(row * cell);
        this.runeV1 = atlas.getV((row + 1) * cell);
        this.baseSize = size;
        this.quadSize = size;
        this.updateLook(0.0F);
    }

    @Override
    protected void updateLook(float t) {
        this.applyHeat(sample(RUNE_HEAT, t), sample(RUNE_ALPHA, t));
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float t = (this.age + partialTicks) / (float) this.lifetime;
        return this.baseSize * sample(RUNE_SIZE, t);
    }

    @Override
    protected float getU0() {
        return this.runeU0;
    }

    @Override
    protected float getU1() {
        return this.runeU1;
    }

    @Override
    protected float getV0() {
        return this.runeV0;
    }

    @Override
    protected float getV1() {
        return this.runeV1;
    }

    public static float sample(float[] curve, float t) {
        float f = Mth.clamp(t, 0.0F, 1.0F) * (curve.length - 1);
        int i = Math.min((int) f, curve.length - 2);
        return Mth.lerp(f - i, curve[i], curve[i + 1]);
    }
}
