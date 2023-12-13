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
    private int restPeriod = 0;
    private boolean apostleWarned = false;
    private ResourceKey<Level> dimension = Level.OVERWORLD;
    private BlockPos ArcaBlock = new BlockPos(0, 0, 0);
    private Set<UUID> grudgeList = new HashSet<>();
    private List<EntityType<?>> grudgeTypeList = new ArrayList<>();
    private List<Research> researchList = new ArrayList<>();
    private Set<UUID> summonList = new HashSet<>();
    private FocusCooldown cooldowns = new FocusCooldown();
    private int shields = 0;
    private int shieldTime = 0;
    private int shieldCool = 0;

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
    public boolean apostleWarned() {
        return this.apostleWarned;
    }

    @Override
    public void setApostleWarned(boolean apostleWarned) {
        this.apostleWarned = apostleWarned;
    }

    @Override
    public int getRestPeriod() {
        return this.restPeriod;
    }

    @Override
    public void setRestPeriod(int restPeriod) {
        this.restPeriod = restPeriod;
    }

    @Override
    public boolean increaseRestPeriod(int increase) {
        this.restPeriod += increase;
        return true;
    }

    @Override
    public boolean decreaseRestPeriod(int decrease) {
        this.restPeriod = Math.max(this.restPeriod - decrease, 0);
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

    @Override
    public Set<UUID> summonList() {
        return this.summonList;
    }

    @Override
    public void addSummon(UUID uuid) {
        this.summonList.add(uuid);
    }

    @Override
    public void removeSummon(UUID uuid) {
        this.summonList.remove(uuid);
    }

    @Override
    public FocusCooldown cooldowns() {
        return this.cooldowns;
    }

    @Override
    public void setCooldowns(FocusCooldown cooldowns){
        this.cooldowns = cooldowns;
    }

    @Override
    public int shieldsLeft() {
        return this.shields;
    }

    @Override
    public void breakShield() {
        --this.shields;
    }

    @Override
    public void increaseShields() {
        ++this.shields;
    }

    @Override
    public void setShields(int amount) {
        this.shields = amount;
    }

    @Override
    public int shieldTime(){
        return this.shieldTime;
    }

    @Override
    public void setShieldTime(int time) {
        this.shieldTime = time;
    }

    @Override
    public void decreaseShieldTime() {
        --this.shieldTime;
    }

    @Override
    public int shieldCool() {
        return this.shieldCool;
    }

    @Override
    public void setShieldCool(int cool) {
        this.shieldCool = cool;
    }

    @Override
    public void decreaseShieldCool() {
        --this.shieldCool;
    }
}
