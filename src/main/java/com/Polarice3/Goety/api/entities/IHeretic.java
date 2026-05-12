package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IHeretic {

    default boolean isChanting() {
        return false;
    }

    default int getChantTimes() {
        return 0;
    }

    default void setChantTimes(int chantTimes) {
    }

    default void setCasting(boolean casting) {
    }

    default void setMonolith(@Nullable AbstractObsidianMonolith monolith) {
    }

    @Nullable
    default AbstractObsidianMonolith getMonolith(){
        return null;
    }

    default boolean isCasting() {
        return false;
    }

    default float getCast(float p_268054_) {
        return 0.0F;
    }

    default List<Vec3> getConvokePos() {
        return new ArrayList<>();
    }
}
