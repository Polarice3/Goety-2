package com.Polarice3.Goety.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class Vec3Util {
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
}
