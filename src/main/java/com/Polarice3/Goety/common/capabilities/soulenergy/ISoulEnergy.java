package com.Polarice3.Goety.common.capabilities.soulenergy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ISoulEnergy {
    BlockPos getArcaBlock();
    void setArcaBlock(BlockPos blockPos);
    ResourceKey<Level> getArcaBlockDimension();
    void setArcaBlockDimension(ResourceKey<Level> dimension);
    boolean getSEActive();
    void setSEActive(boolean seActive);
    int getSoulEnergy();
    void setSoulEnergy(int soulEnergy);
    boolean increaseSE(int increase);
    boolean decreaseSE(int decrease);
}
