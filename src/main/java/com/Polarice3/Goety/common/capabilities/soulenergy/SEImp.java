package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.research.Research;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class SEImp implements ISoulEnergy{
    private boolean seActive;
    private int soulEnergy = 0;
    private int recoil = 0;
    private int restPeriod = 0;
    private boolean apostleWarned = false;
    private ResourceKey<Level> dimension = Level.OVERWORLD;
    private BlockPos ArcaBlock = null;
    private Set<UUID> grudgeList = new HashSet<>();
    private List<EntityType<?>> grudgeTypeList = new ArrayList<>();
    private Set<UUID> allyList = new HashSet<>();
    private List<EntityType<?>> allyTypeList = new ArrayList<>();
    private List<Research> researchList = new ArrayList<>();
    private FocusCooldown cooldowns = new FocusCooldown();
    @Nullable
    private Projectile grappling = null;
    private int bottling = 0;
    private int warding = 0;
    private int maxWarding = 0;
    private int ticksInAir = 0;
    private int airJumps = 0;
    private int airJumpCooldown = 0;
    private UUID cameraUUID = null;
    private BlockPos EndWalkPos = null;
    private ResourceKey<Level> EndWalkDim = null;

    @Override
    @Nullable
    public BlockPos getArcaBlock() {
        return this.ArcaBlock;
    }

    @Override
    public void setArcaBlock(@Nullable BlockPos blockPos) {
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
    public int getRecoil() {
        return this.recoil;
    }

    @Override
    public void setRecoil(int recoil) {
        this.recoil = recoil;
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
    public Set<UUID> allyList() {
        return this.allyList;
    }

    @Override
    public void addAlly(UUID uuid) {
        this.allyList.add(uuid);
    }

    @Override
    public void removeAlly(UUID uuid) {
        this.allyList.remove(uuid);
    }

    @Override
    public List<EntityType<?>> allyTypeList() {
        return this.allyTypeList;
    }

    @Override
    public void addAllyType(EntityType<?> entityType) {
        this.allyTypeList.add(entityType);
    }

    @Override
    public void removeAllyType(EntityType<?> entityType) {
        this.allyTypeList.remove(entityType);
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
    public FocusCooldown cooldowns() {
        return this.cooldowns;
    }

    @Override
    public void setCooldowns(FocusCooldown cooldowns){
        this.cooldowns = cooldowns;
    }

    @Nullable
    @Override
    public Projectile getGrappling() {
        return this.grappling;
    }

    public void setGrappling(@Nullable Projectile projectile){
        this.grappling = projectile;
    }

    @Override
    public int bottling(){
        return this.bottling;
    }

    @Override
    public void setBottling(int bottling){
        this.bottling = bottling;
    }

    @Override
    public int wardingLeft() {
        return this.warding;
    }

    @Override
    public int maxWarding() {
        return this.maxWarding;
    }

    @Override
    public void setWarding(int warding) {
        this.warding = warding;
    }

    @Override
    public void setMaxWarding(int warding) {
        this.maxWarding = warding;
    }

    @Override
    public int getTicksInAir() {
        return this.ticksInAir;
    }

    @Override
    public void setTicksInAir(int ticksInAir) {
        this.ticksInAir = ticksInAir;
    }

    @Override
    public int getAirJumps() {
        return this.airJumps;
    }

    @Override
    public void setAirJumps(int airJumps) {
        this.airJumps = airJumps;
    }

    @Override
    public int getAirJumpCooldown() {
        return this.airJumpCooldown;
    }

    @Override
    public void setAirJumpCooldown(int airJumpCooldown) {
        this.airJumpCooldown = airJumpCooldown;
    }

    @Override
    @Nullable
    public UUID getCameraUUID(){
        return this.cameraUUID;
    }

    @Override
    public void setCameraUUID(@Nullable UUID camera){
        this.cameraUUID = camera;
    }

    @Override
    @Nullable
    public BlockPos getEndWalkPos() {
        return this.EndWalkPos;
    }

    @Override
    public void setEndWalkPos(BlockPos blockPos) {
        this.EndWalkPos = blockPos;
    }

    @Override
    public ResourceKey<Level> getEndWalkDimension() {
        return this.EndWalkDim;
    }

    @Override
    public void setEndWalkDimension(ResourceKey<Level> dimension) {
        this.EndWalkDim = dimension;
    }
}
