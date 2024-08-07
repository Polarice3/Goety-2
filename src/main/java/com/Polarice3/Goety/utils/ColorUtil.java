package com.Polarice3.Goety.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.material.MaterialColor;

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
        this.red = (colorCode >> 16 & 255) / 255F;
        this.green = (colorCode >> 8 & 255) / 255F;
        this.blue = (colorCode & 255) / 255F;
        this.alpha = 1.0F;
    }

    public ColorUtil(MaterialColor mapColor){
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
}
