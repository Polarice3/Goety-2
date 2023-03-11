package com.Polarice3.Goety.common.entities.neutral;

import net.minecraft.world.entity.Entity;

public interface IBreathing {

    boolean isBreathing();

    void setSpewing(boolean flag);

    void doSpewing(Entity target);
}
