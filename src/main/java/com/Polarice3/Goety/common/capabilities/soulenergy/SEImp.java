package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.research.Research;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.*;

public class SEImp implements ISoulEnergy{
    private boolean seActive;
    private int soulEnergy = 0;
    private ResourceKey<Level> dimension = Level.OVERWORLD;
    private BlockPos ArcaBlock = new BlockPos(0, 0, 0);
    private Set<UUID> grudgeList = new HashSet<>();
    private List<EntityType<?>> grudgeTypeList = new ArrayList<>();
    private List<Research> researchList = new ArrayList<>();

    @Override
    public BlockPos getArcaBlock() {
        return this.ArcaBlock;
    }

    @Override
    public void setArcaBlock(BlockPos blockPos) {
        this.ArcaBlock = blockPos;
    }

    @Override
    public ResourceKey<Level> getArcaBlockDimension() {
        return this.dimension;
    }

    @Override
    public void setArcaBlockDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    @Override
    public boolean getSEActive() {
        return this.seActive;
    }

    @Override
    public void setSEActive(boolean seActive) {
        this.seActive = seActive;
    }

    @Override
    public int getSoulEnergy() {
        return this.soulEnergy;
    }

    @Override
    public void setSoulEnergy(int soulEnergy) {
        this.soulEnergy = soulEnergy;
    }

    @Override
    public boolean increaseSE(int increase) {
        if (this.soulEnergy >= MainConfig.MaxArcaSouls.get()) {
            return false;
        }
        this.soulEnergy = Math.min(this.soulEnergy + increase, MainConfig.MaxArcaSouls.get());
        return true;
    }

    @Override
    public boolean decreaseSE(int decrease) {
        if (this.soulEnergy <= 0) {
            return false;
        }
        this.soulEnergy = Math.max(this.soulEnergy - decrease, 0);
        return true;
    }

    @Override
    public Set<UUID> grudgeList() {
        return this.grudgeList;
    }

    @Override
    public void addGrudge(UUID uuid) {
        this.grudgeList.add(uuid);
    }

    @Override
    public void removeGrudge(UUID uuid) {
        this.grudgeList.remove(uuid);
    }

    @Override
    public List<EntityType<?>> grudgeTypeList() {
        return this.grudgeTypeList;
    }

    @Override
    public void addGrudgeType(EntityType<?> entityType) {
        this.grudgeTypeList.add(entityType);
    }

    @Override
    public void removeGrudgeType(EntityType<?> entityType) {
        this.grudgeTypeList.remove(entityType);
    }

    @Override
    public List<Research> getResearch() {
        return this.researchList;
    }

    @Override
    public void addResearch(Research research) {
        this.researchList.add(research);
    }

    @Override
    public void removeResearch(Research research) {
        this.researchList.remove(research);
    }
}
