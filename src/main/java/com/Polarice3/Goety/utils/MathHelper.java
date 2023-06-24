package com.Polarice3.Goety.utils;

import net.minecraft.util.Mth;

public class MathHelper extends Mth {
    public static int secondsToTicks(int pSeconds){
        return pSeconds * 20;
    }

    public static float secondsToTicks(float pSeconds){
        return pSeconds * 20;
    }

    public static int minutesToTicks(int pMinutes){
        return secondsToTicks(pMinutes * 60);
    }

    public static float minutesToTicks(float pMinutes){
        return secondsToTicks(pMinutes * 60);
    }

    public static float modelDegrees(float degree){
        return (float) ((degree * Math.PI)/180.0F); /* For opposite, it's (answer * 180) / PI*/
    }

    public static double rgbToSpeed(double colorCode){
        return colorCode/255.0D;
    }

    /* Test this */
    public static double[] rgbParticle(int colorCode){
        return new double[]{((colorCode >> 16) & 0xff) / 255F, ((colorCode >> 8) & 0xff) / 255f, (colorCode & 0xff) / 255f};
    }
}
