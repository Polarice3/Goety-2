package com.Polarice3.Goety.common.entities.util;

import net.minecraft.world.phys.Vec3;

public interface ShootIndicatorOwner {
    Vec3 getShootIndicatorStart(float partialTicks);

    Vec3 getShootIndicatorEnd(float partialTicks);

    float getShootIndicatorProgress(float partialTicks);

    boolean shouldUpdateShootIndicator();
}
