package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Copied from MobEffect. Made so that I don't have to make more icons for instant effects. */
public abstract class BrewEffect {
    private final MobEffectCategory category;
    private final int color;
    private final int soulCost;
    private final int capacityExtra;
    @Nullable
    private String descriptionId;
    /** Used String because it's easier than making a new Registry, lol */
    public String effectID;
    public int duration;

    public BrewEffect(String effectID, int soulCost, int capacityExtra, MobEffectCategory category, int color, boolean noExtra) {
        this.effectID = effectID;
        this.soulCost = soulCost;
        this.capacityExtra = capacityExtra;
        this.category = category;
        this.color = color;
    }

    public BrewEffect(String effectID, int soulCost, int capacityExtra, MobEffectCategory category, int color) {
        this.effectID = "effect.goety." + effectID;
        this.soulCost = soulCost;
        this.capacityExtra = capacityExtra;
        this.category = category;
        this.color = color;
    }

    public BrewEffect(String effectID, int soulCost, MobEffectCategory category, int color){
        this(effectID, soulCost, 0, category, color);
    }

    /** For registering Vanilla Effects */
    public BrewEffect(MobEffect mobEffect, int soulCost, int capacityExtra, MobEffectCategory category, int color){
        this.effectID = mobEffect.getDescriptionId();
        this.soulCost = soulCost;
        this.capacityExtra = capacityExtra;
        this.category = category;
        this.color = color;
    }

    public BrewEffect(MobEffect mobEffect, int soulCost, MobEffectCategory category, int color){
        this(mobEffect, soulCost, 0, category, color);
    }

    public BrewEffect(String effectID, MobEffectCategory category, int color){
        this(effectID, 25, category, color);
    }

    public BrewEffect(String effectID, MobEffectCategory category, int color, boolean noExtra){
        this(effectID, 25, 0, category, color, noExtra);
    }

    public String getEffectID(){
        return this.effectID;
    }

    public void applyEffectTick(@NotNull LivingEntity p_19467_, int p_19468_) {
    }

    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        this.applyEntityEffect(pLivingEntity, pSource, pIndirectSource, pAmplifier);
    }

    public void drinkBlockEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, int pAreaOfEffect){
        this.applyInstantenousEffect(pSource, pIndirectSource, pLivingEntity, pAmplifier, 0);
        this.applyBlockEffect(pLivingEntity.level, pLivingEntity.blockPosition(), pIndirectSource instanceof LivingEntity livingEntity ? livingEntity : pSource instanceof LivingEntity livingEntity ? livingEntity : null, pAmplifier, pAreaOfEffect);
    }

    public boolean isInstantenous() {
        return false;
    }

    public boolean canLinger(){
        return false;
    }

    public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
        return false;
    }

    public void applyDirectionalBlockEffect(Level pLevel, BlockPos pPos, Direction pDirection, LivingEntity pSource, int pAmplifier, int pAreaOfEffect){

    }

    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect){
    }

    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pDuration, int pAmplifier, int pAreaOfEffect){
        this.applyBlockEffect(pLevel, pPos, pSource, pAmplifier, pAreaOfEffect);
    }

    public int getSoulCost(){
        return this.soulCost;
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, int pAmplifier){
        this.applyEntityEffect(pTarget, pSource, pSource, pAmplifier);
    }

    public void applyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource, int pAmplifier){
    }

    public List<BlockPos> getTreePos(BlockPos initial, int x, int y, int z){
        List<BlockPos> result = new ArrayList<>();
        for (int i = -x; i <= x; ++i) {
            for (int j = -(y / 2); j <= y; ++j) {
                for (int k = -z; k <= z; ++k) {
                    BlockPos blockpos1 = initial.offset(i, j, k);
                    result.add(blockpos1);
                }
            }
        }
        return result;
    }

    public List<BlockPos> getCubePos(BlockPos initial, int range){
        List<BlockPos> result = new ArrayList<>();
        for (int i = -range; i <= range; ++i) {
            for (int j = -range; j <= range; ++j) {
                for (int k = -range; k <= range; ++k) {
                    BlockPos blockpos1 = initial.offset(i, j, k);
                    result.add(blockpos1);
                }
            }
        }
        return result;
    }

    public List<BlockPos> getSpherePos(BlockPos initial, int range){
        List<BlockPos> result = new ArrayList<>();
        if (range == 1){
            result.add(initial);
        } else {
            int rangeSqr = range * range;

            for (int i = -range; i <= range; ++i) {
                for (int j = -range; j <= range; ++j) {
                    for (int k = -range; k <= range; ++k) {
                        BlockPos blockpos1 = initial.offset(i, j, k);
                        if (blockpos1.distSqr(initial) < (rangeSqr - 1)) {
                            result.add(blockpos1);
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<BlockPos> getHollowSphere(BlockPos initial, int range){
        List<BlockPos> result = new ArrayList<>();
        if (range > 1){
            int rangeSqr = range * range;

            for (int i = -range; i <= range; ++i) {
                for (int j = -range; j <= range; ++j) {
                    for (int k = -range; k <= range; ++k) {
                        BlockPos blockpos1 = initial.offset(i, j, k);
                        if (blockpos1.distSqr(initial) < (rangeSqr - 1)) {
                            result.add(blockpos1);
                        }
                    }
                }
            }

            int range2 = range - 1;
            int range2Sqr = range2 * range2;
            for (int i = -range2; i <= range2; ++i) {
                for (int j = -range2; j <= range2; ++j) {
                    for (int k = -range2; k <= range2; ++k) {
                        BlockPos blockpos1 = initial.offset(i, j, k);
                        if (blockpos1.distSqr(initial) < (range2Sqr - 1)) {
                            result.remove(blockpos1);
                        }
                    }
                }
            }
        } else {
            result.add(initial);
        }
        return result;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = this.effectID;
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }

    public MobEffectCategory getCategory() {
        return this.category;
    }

    public int getCapacityExtra(){
        return this.capacityExtra;
    }

    public int getColor() {
        return this.color;
    }

    public int getDuration() {
        return duration;
    }
}
