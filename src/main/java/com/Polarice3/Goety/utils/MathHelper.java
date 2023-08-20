package com.Polarice3.Goety.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

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

    /* Test this */
    public static double[] rgbParticle(int colorCode){
        return new double[]{((colorCode >> 16) & 0xff) / 255F, ((colorCode >> 8) & 0xff) / 255f, (colorCode & 0xff) / 255f};
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
