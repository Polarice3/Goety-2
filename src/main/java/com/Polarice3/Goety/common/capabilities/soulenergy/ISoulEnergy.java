package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.research.Research;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    boolean apostleWarned();
    void setApostleWarned(boolean apostleWarned);
    int getRestPeriod();
    void setRestPeriod(int restPeriod);
    boolean increaseRestPeriod(int increase);
    boolean decreaseRestPeriod(int decrease);
    Set<UUID> grudgeList();
    void addGrudge(UUID uuid);
    void removeGrudge(UUID uuid);
    List<EntityType<?>> grudgeTypeList();
    void addGrudgeType(EntityType<?> entityType);
    void removeGrudgeType(EntityType<?> entityType);
    List<Research> getResearch();
    void addResearch(Research research);
    void removeResearch(Research research);
    Set<UUID> summonList();
    void addSummon(UUID uuid);
    void removeSummon(UUID uuid);
    FocusCooldown cooldowns();
    void setCooldowns(FocusCooldown cooldowns);
    int shieldsLeft();
    void breakShield();
    void increaseShields();
    void setShields(int amount);
    int shieldTime();
    void setShieldTime(int time);
    void decreaseShieldTime();
    int shieldCool();
    void setShieldCool(int cool);
    void decreaseShieldCool();
    @Nullable
    BlockPos getEndWalkPos();
    void setEndWalkPos(BlockPos blockPos);
    @Nullable
    ResourceKey<Level> getEndWalkDimension();
    void setEndWalkDimension(ResourceKey<Level> dimension);
}
