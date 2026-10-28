package com.Polarice3.Goety.client.particles;

public final class NecroVFXPalette {
    public static final int DARK_HEX = 0x1b3600;
    public static final int LIME_HEX = 0x9df431;
    public static final int GREEN_HEX = 0x0ab405;
    public static final int BRIGHT_HEX = 0x36dc0b;
    public static final int CHARTREUSE_HEX = 0x88ff00;

    public static final float[] DARK = rgb(DARK_HEX);
    public static final float[] LIME = rgb(LIME_HEX);
    public static final float[] GREEN = rgb(GREEN_HEX);
    public static final float[] BRIGHT = rgb(BRIGHT_HEX);
    public static final float[] CHARTREUSE = rgb(CHARTREUSE_HEX);

    public static final float[] LIME_HUE = hue(LIME_HEX);
    public static final float[] GREEN_HUE = hue(GREEN_HEX);
    public static final float[] BRIGHT_HUE = hue(BRIGHT_HEX);
    public static final float[] CHARTREUSE_HUE = hue(CHARTREUSE_HEX);

    private static final float[][] SMOKE = {GREEN, BRIGHT, LIME};

    private NecroVFXPalette() {
    }

    public static float[] rgb(int hex) {
        return new float[]{((hex >> 16) & 0xFF) / 255.0F, ((hex >> 8) & 0xFF) / 255.0F, (hex & 0xFF) / 255.0F};
    }

    public static float[] hue(int hex) {
        float[] c = rgb(hex);
        float m = Math.max(c[0], Math.max(c[1], c[2]));
        return m <= 0.0F ? c : new float[]{c[0] / m, c[1] / m, c[2] / m};
    }

    public static float[] lerp(float t, float[] a, float[] b) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        return new float[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t};
    }

    public static float[] greenHue(float t) {
        return lerp(t, GREEN_HUE, CHARTREUSE_HUE);
    }

    public static float[] smokeColour(int index) {
        return SMOKE[Math.floorMod(index, SMOKE.length)];
    }

    public static int smokeColours() {
        return SMOKE.length;
    }
}