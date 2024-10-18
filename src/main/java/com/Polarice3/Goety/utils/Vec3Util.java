package com.Polarice3.Goety.utils;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class Vec3Util {
    public static final Vec3 xAxis = new Vec3(1.0D, 0.0D, 0.0D);
    public static final Vec3 yAxis = new Vec3(0.0D, 1.0D, 0.0D);
    public static final Vec3 zAxis = new Vec3(0.0D, 0.0D, 1.0D);
    public static final Vec3 unit = new Vec3(1.0D, 1.0D, 1.0D);

    public static Vec3 north(Vec3 vector3d){
        return relative(vector3d, Direction.NORTH);
    }

    public static Vec3 north(Vec3 vector3d, int distance){
        return relative(vector3d, Direction.NORTH, distance);
    }

    public static Vec3 south(Vec3 vector3d){
        return relative(vector3d, Direction.SOUTH);
    }

    public static Vec3 south(Vec3 vector3d, int distance){
        return relative(vector3d, Direction.SOUTH, distance);
    }

    public static Vec3 west(Vec3 vector3d){
        return relative(vector3d, Direction.WEST);
    }

    public static Vec3 west(Vec3 vector3d, int distance){
        return relative(vector3d, Direction.WEST, distance);
    }

    public static Vec3 east(Vec3 vector3d){
        return relative(vector3d, Direction.EAST);
    }

    public static Vec3 east(Vec3 vector3d, int distance){
        return relative(vector3d, Direction.EAST, distance);
    }

    public static Vec3 relative(Vec3 vector3d, Direction p_177972_1_) {
        return new Vec3(vector3d.x() + p_177972_1_.getStepX(), vector3d.y() + p_177972_1_.getStepY(), vector3d.z() + p_177972_1_.getStepZ());
    }

    public static Vec3 relative(Vec3 vector3d, Direction pDirection, int pDistance) {
        return pDistance == 0 ? vector3d : new Vec3(vector3d.x() + pDirection.getStepX() * pDistance, vector3d.y() + pDirection.getStepY() * pDistance, vector3d.z() + pDirection.getStepZ() * pDistance);
    }

    public static Vec3 subtract(Vec3 vec3, double amount){
        return vec3.subtract(amount, amount, amount);
    }

    public static Vec3 add(Vec3 vec3, double amount){
        return vec3.add(amount, amount, amount);
    }

    public static Vec3 readVec3(CompoundTag compoundTag) {
        return new Vec3(
                compoundTag.getDouble("X"),
                compoundTag.getDouble("Y"),
                compoundTag.getDouble("Z"));
    }

    public static CompoundTag writeVec3(Vec3 vec3) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putDouble("X", vec3.x);
        compoundtag.putDouble("Y", vec3.y);
        compoundtag.putDouble("Z", vec3.z);
        return compoundtag;
    }

    public static Vec3 coerceAtLeast(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.max(vec1.x(), vec2.x()), Math.max(vec1.y(), vec2.y()), Math.max(vec1.z(), vec2.z()));
    }

    public static Vec3 coerceAtMost(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.min(vec1.x(), vec2.x()), Math.min(vec1.y(), vec2.y()), Math.min(vec1.z(), vec2.z()));
    }

    public static Vec3 randVec(Supplier<Double> rand) {
        return new Vec3(rand.get() - 0.5D, rand.get() - 0.5D, rand.get() - 0.5D);
    }

    public static Vec3 randVec(RandomSource random) {
        return randVec(random::nextDouble);
    }
}
