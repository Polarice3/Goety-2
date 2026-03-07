package com.Polarice3.Goety.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.awt.*;

public class ColorUtil {
    public static ColorUtil WHITE = new ColorUtil(0xffffff);
    public static ColorUtil BLACK = new ColorUtil(0x000000);
    public float red;
    public float green;
    public float blue;
    public float alpha;

    public ColorUtil(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorUtil(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 1.0F;
    }

    public ColorUtil(int red, int green, int blue, float alpha) {
        this.red = red / 255.0F;
        this.green = green / 255.0F;
        this.blue = blue / 255.0F;
        this.alpha = alpha;
    }

    public ColorUtil(int colorCode){
        Color color = Color.decode(String.valueOf(colorCode));
        this.red = color.getRed() / 255.0F;
        this.green = color.getGreen() / 255.0F;
        this.blue = color.getBlue() / 255.0F;
        this.alpha = 1.0F;
    }

    public ColorUtil(MapColor mapColor){
        this(mapColor.col);
    }

    public ColorUtil(ChatFormatting format){
        this(format.getColor() != null ? format.getColor() : 0);
    }

    public float red() {
        return this.red;
    }

    public float green() {
        return this.green;
    }

    public float blue() {
        return this.blue;
    }

    public float alpha() {
        return this.alpha;
    }

    public int colorCode(float alpha) {
        return FastColor.ARGB32.color(Math.round(alpha * 255.0F), Math.round(red * 255.0F), Math.round(green * 255.0F), Math.round(blue * 255.0F));
    }

    public static class ARGB {

        public static int alpha(int i) {
            return i >>> 24;
        }

        public static int red(int i) {
            return i >> 16 & 0xFF;
        }

        public static int green(int i) {
            return i >> 8 & 0xFF;
        }

        public static int blue(int i) {
            return i & 0xFF;
        }

        public static int color(int alpha, int red, int green, int blue) {
            return alpha << 24 | red << 16 | green << 8 | blue;
        }

        public static int color(int red, int green, int blue) {
            return color(255, red, green, blue);
        }

        public static int color(Vec3 vec3) {
            return color(as8BitChannel((float)vec3.x()), as8BitChannel((float)vec3.y()), as8BitChannel((float)vec3.z()));
        }

        public static int multiply(int color, int alpha) {
            if (color == -1) {
                return alpha;
            } else {
                return alpha == -1 ? color : color(alpha(color) * alpha(alpha) / 255, red(color) * red(alpha) / 255, green(color) * green(alpha) / 255, blue(color) * blue(alpha) / 255);
            }
        }

        public static int scaleRGB(int color, float alpha) {
            return scaleRGB(color, alpha, alpha, alpha);
        }

        public static int scaleRGB(int color, float redAlpha, float greenAlpha, float blueAlpha) {
            return color(
                    alpha(color),
                    Mth.clamp(((int)((float)red(color) * redAlpha)), 0, 255),
                    Mth.clamp(((int)((float)green(color) * greenAlpha)), 0, 255),
                    Mth.clamp(((int)((float)blue(color) * blueAlpha)), 0, 255)
            );
        }

        public static int scaleRGB(int color, int alpha) {
            return color(
                    alpha(color),
                    Mth.clamp(red(color) * alpha / 255, 0, 255),
                    Mth.clamp(green(color) * alpha / 255, 0, 255),
                    Mth.clamp(blue(color) * alpha / 255, 0, 255)
            );
        }

        public static int greyscale(int color) {
            int j = (int)((float)red(color) * 0.3F + (float)green(color) * 0.59F + (float)blue(color) * 0.11F);
            return color(j, j, j);
        }

        public static int lerp(float partialTick, int colorFrom, int colorTo) {
            return FastColor.ARGB32.lerp(partialTick, colorFrom, colorTo);
        }

        public static int opaque(int color) {
            return FastColor.ABGR32.opaque(color);
        }

        public static int transparent(int color) {
            return FastColor.ABGR32.transparent(color);
        }

        public static int color(int alpha, int color) {
            return alpha << 24 | color & 16777215;
        }

        public static int white(float alpha) {
            return as8BitChannel(alpha) << 24 | 16777215;
        }

        public static int colorFromFloat(float alpha, float red, float green, float blue) {
            return color(as8BitChannel(alpha), as8BitChannel(red), as8BitChannel(green), as8BitChannel(blue));
        }

        public static Vector3f vector3fFromRGB24(int color) {
            float f = (float)red(color) / 255.0F;
            float g = (float)green(color) / 255.0F;
            float h = (float)blue(color) / 255.0F;
            return new Vector3f(f, g, h);
        }

        public static int average(int color, int color2) {
            return color((alpha(color) + alpha(color2)) / 2, (red(color) + red(color2)) / 2, (green(color) + green(color2)) / 2, (blue(color) + blue(color2)) / 2);
        }

        public static int as8BitChannel(float f) {
            return Mth.floor(f * 255.0F);
        }

        public static float alphaFloat(int i) {
            return from8BitChannel(alpha(i));
        }

        public static float redFloat(int i) {
            return from8BitChannel(red(i));
        }

        public static float greenFloat(int i) {
            return from8BitChannel(green(i));
        }

        public static float blueFloat(int i) {
            return from8BitChannel(blue(i));
        }

        private static float from8BitChannel(int i) {
            return (float)i / 255.0F;
        }

        public static int toABGR(int i) {
            return i & -16711936 | (i & 0xFF0000) >> 16 | (i & 0xFF) << 16;
        }

        public static int fromABGR(int i) {
            return toABGR(i);
        }
    }
}
