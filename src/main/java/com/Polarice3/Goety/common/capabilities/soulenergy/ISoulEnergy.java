package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.research.Research;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ISoulEnergy {
    @Nullable
    BlockPos getArcaBlock();
    void setArcaBlock(@Nullable BlockPos blockPos);
    ResourceKey<Level> getArcaBlockDimension();
    void setArcaBlockDimension(ResourceKey<Level> dimension);
    boolean getSEActive();
    void setSEActive(boolean seActive);
    int getSoulEnergy();
    void setSoulEnergy(int soulEnergy);
    boolean increaseSE(int increase);
    boolean decreaseSE(int decrease);
    int getRecoil();
    void setRecoil(int recoil);
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
    Set<UUID> allyList();
    void addAlly(UUID uuid);
    void removeAlly(UUID uuid);
    List<EntityType<?>> allyTypeList();
    void addAllyType(EntityType<?> entityType);
    void removeAllyType(EntityType<?> entityType);
    List<Research> getResearch();
    void addResearch(Research research);
    void removeResearch(Research research);
    FocusCooldown cooldowns();
    void setCooldowns(FocusCooldown cooldowns);
    @Nullable
    Projectile getGrappling();
    void setGrappling(@Nullable Projectile projectile);
    int bottling();
    void setBottling(int bottling);
    int wardingLeft();
    int maxWarding();
    void setWarding(int warding);
    void setMaxWarding(int warding);
    int getTicksInAir();
    int getAirJumps();
    int getAirJumpCooldown();
    void setTicksInAir(int tick);
    void setAirJumps(int airJumps);
    void setAirJumpCooldown(int cooldown);
    @Nullable
    UUID getCameraUUID();
    void setCameraUUID(@Nullable UUID camera);
    @Nullable
    BlockPos getEndWalkPos();
    void setEndWalkPos(BlockPos blockPos);
    @Nullable
    ResourceKey<Level> getEndWalkDimension();
    void setEndWalkDimension(ResourceKey<Level> dimension);
}
