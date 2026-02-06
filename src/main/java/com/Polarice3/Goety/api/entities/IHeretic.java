package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;

import javax.annotation.Nullable;

public interface IHeretic {

    default boolean isChanting() {
        return false;
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
}
