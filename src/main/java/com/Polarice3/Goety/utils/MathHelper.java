package com.Polarice3.Goety.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MathHelper extends Mth {
    public static int secondsToTicks(int pSeconds){
        return pSeconds * 20;
    }

    public static int secondsToTicks(float pSeconds){
        return (int) (pSeconds * 20);
    }

    public static int minutesToTicks(int pMinutes){
        return secondsToTicks(pMinutes * 60);
    }

    public static float minutesToTicks(float pMinutes){
        return secondsToTicks(pMinutes * 60);
    }

    public static int minecraftDayToTicks(int pDay){
        return pDay * 24000;
    }

    public static float minecraftDayToTicks(float pDay){
        return pDay * 24000;
    }

    public static float modelDegrees(float degree){
        return (float) ((degree * Math.PI)/180.0F); /* For opposite, it's (answer * 180) / PI*/
    }

    public static double rgbToSpeed(double colorCode){
        return colorCode/255.0D;
    }

    public static double[] rgbParticle(int colorCode){
        ColorUtil colorUtil = new ColorUtil(colorCode);
        return new double[]{colorUtil.red, colorUtil.green, colorUtil.blue};
    }

    public static float[] rgbFloat(int colorCode){
        ColorUtil colorUtil = new ColorUtil(colorCode);
        return new float[]{colorUtil.red, colorUtil.green, colorUtil.blue};
    }

    public static long setDayNumberAndTime(long day, long time){
        return day * 24000 + time;
    }

    public static long getNextDaysTime(Level world, long timeOfDay) {
        long dayTime = world.getDayTime();
        long newTime = (dayTime + 24000);
        newTime -= newTime % 24000;
        return newTime + timeOfDay;
    }
}
