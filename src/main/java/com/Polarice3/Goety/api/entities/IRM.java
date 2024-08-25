package com.Polarice3.Goety.api.entities;

public interface IRM {
    float getBigGlow();

    float getMinorGlow();

    boolean isActivating();

    boolean isSummoning();

    boolean isBelching();

    boolean canAnimateMove();

    boolean isHostile();
}
