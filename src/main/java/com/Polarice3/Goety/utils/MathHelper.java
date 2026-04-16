package com.Polarice3.Goety.utils;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.atomic.AtomicReference;

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

    public static float lerpRotation(float p_37274_, float p_37275_) {
        while(p_37275_ - p_37274_ < -180.0F) {
            p_37274_ -= 360.0F;
        }

        while(p_37275_ - p_37274_ >= 180.0F) {
            p_37274_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_37274_, p_37275_);
    }

    public static float lerpAngleDegrees(float delta, float start, float end) {
        return start + delta * wrapDegrees(end - start);
    }

    public static double lerpAngleDegrees(double delta, double start, double end) {
        return start + delta * wrapDegrees(end - start);
    }

    public static double getLerpProgress(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public static float getLerpProgress(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < 1.0E-5F;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < 1.0E-5F;
    }

    public static float positionToPitch(Vec3 start, Vec3 end) {
        return positionToPitch(end.subtract(start));
    }

    public static float positionToYaw(Vec3 start, Vec3 end) {
        return positionToYaw(end.subtract(start));
    }

    public static float positionToPitch(Vec3 vec3) {
        return positionToPitch(vec3.x, vec3.y, vec3.z);
    }

    public static float positionToYaw(Vec3 vec3) {
        return positionToYaw(vec3.x, vec3.z);
    }

    public static float positionToPitch(double diffX, double diffY, double diffZ) {
        double horizontalDist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        return !(Math.abs(diffY) > (double) 1.0E-5F) && !(Math.abs(horizontalDist) > (double) 1.0E-5F) ? 0 : (float) ((Mth.atan2(diffY, horizontalDist) * Mth.RAD_TO_DEG));
    }

    public static float positionToYaw(double diffX, double diffZ) {
        return !(Math.abs(diffZ) > (double) 1.0E-5F) && !(Math.abs(diffX) > (double) 1.0E-5F) ? 0 : (float) (Mth.atan2(diffZ, diffX) * Mth.RAD_TO_DEG);
    }

    public static Vec3 rotationToPosition(float radius, float pitch, float yaw) {
        double endPosX = radius * Math.cos(yaw * Mth.DEG_TO_RAD) * Math.cos(pitch * Mth.DEG_TO_RAD);
        double endPosY = radius * Math.sin(pitch * Mth.DEG_TO_RAD);
        double endPosZ = radius * Math.sin(yaw * Mth.DEG_TO_RAD) * Math.cos(pitch * Mth.DEG_TO_RAD);
        return new Vec3(endPosX, endPosY, endPosZ);
    }

    public static Vec3 rotationToPosition(Vec3 startPos, float radius, float pitch, float yaw) {
        return startPos.add(rotationToPosition(radius, pitch, yaw));
    }

    //Stolen from @mehvahdjukaar codes: https://github.com/MehVahdJukaar/Moonlight/blob/1.20/common/src/main/java/net/mehvahdjukaar/moonlight/api/util/math/MthUtils.java
    public static VoxelShape rotateVoxelShape(VoxelShape source, Direction direction) {
        if (direction == Direction.NORTH) return source;
        AtomicReference<VoxelShape> newShape = new AtomicReference<>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Vec3 min = new Vec3(minX - 0.5, minY - 0.5, minZ - 0.5);
            Vec3 max = new Vec3(maxX - 0.5, maxY - 0.5, maxZ - 0.5);
            Vec3 v1 = Vec3Util.rotateVec3(min, direction);
            Vec3 v2 = Vec3Util.rotateVec3(max, direction);
            VoxelShape s = Shapes.create(0.5 + Math.min(v1.x, v2.x), 0.5 + Math.min(v1.y, v2.y), 0.5 + Math.min(v1.z, v2.z),
                    0.5 + Math.max(v1.x, v2.x), 0.5 + Math.max(v1.y, v2.y), 0.5 + Math.max(v1.z, v2.z));
            newShape.set(Shapes.or(newShape.get(), s));
        });
        return newShape.get();
    }

    public static VoxelShape moveVoxelShape(VoxelShape source, Vec3 v) {
        AtomicReference<VoxelShape> newShape = new AtomicReference<>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            VoxelShape s = Shapes.create(minX + v.x, minY + v.y, minZ + v.z,
                    maxX + v.x, maxY + v.y, maxZ + v.z);
            newShape.set(Shapes.or(newShape.get(), s));
        });
        return newShape.get();
    }
}
