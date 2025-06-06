package com.Polarice3.Goety.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ModFoodData {
    public int foodLevel = 20;
    public float saturationLevel;
    public float exhaustionLevel;
    public int tickTimer;
    public int lastFoodLevel = 20;
    public boolean shouldStarve = false;

    public ModFoodData(boolean shouldStarve) {
        this.saturationLevel = 5.0F;
        this.shouldStarve = shouldStarve;
    }

    public void eat(int nutrition, float saturation) {
        this.foodLevel = Math.min(nutrition + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + (float)nutrition * saturation * 2.0F, (float)this.foodLevel);
    }

    public void eat(ItemStack itemStack, @Nullable LivingEntity entity) {
        if (itemStack.isEdible()) {
            FoodProperties foodproperties = itemStack.getFoodProperties(entity);
            if (foodproperties != null) {
                this.eat(foodproperties.getNutrition(), foodproperties.getSaturationModifier());
            }
        }
    }

    public void tick(LivingEntity livingEntity) {
        boolean isHurt = livingEntity.getHealth() > 0.0F && livingEntity.getHealth() < livingEntity.getMaxHealth();
        Difficulty difficulty = livingEntity.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (this.saturationLevel > 0.0F && isHurt && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                livingEntity.heal(f / 6.0F);
                this.addExhaustion(f);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel >= 18 && isHurt) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                livingEntity.heal(1.0F);
                this.addExhaustion(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0 && this.shouldStarve) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (livingEntity.getHealth() > 10.0F || difficulty == Difficulty.HARD || livingEntity.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    livingEntity.hurt(livingEntity.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    public void readAdditionalSaveData(CompoundTag p_38716_) {
        if (p_38716_.contains("foodLevel", 99)) {
            this.foodLevel = p_38716_.getInt("foodLevel");
            this.tickTimer = p_38716_.getInt("foodTickTimer");
            this.saturationLevel = p_38716_.getFloat("foodSaturationLevel");
            this.exhaustionLevel = p_38716_.getFloat("foodExhaustionLevel");
            this.shouldStarve = p_38716_.getBoolean("shouldStarve");
        }

    }

    public void addAdditionalSaveData(CompoundTag p_38720_) {
        p_38720_.putInt("foodLevel", this.foodLevel);
        p_38720_.putInt("foodTickTimer", this.tickTimer);
        p_38720_.putFloat("foodSaturationLevel", this.saturationLevel);
        p_38720_.putFloat("foodExhaustionLevel", this.exhaustionLevel);
        p_38720_.putBoolean("shouldStarve", this.shouldStarve);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int getLastFoodLevel() {
        return this.lastFoodLevel;
    }

    public boolean needsFood() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float p_38704_) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + p_38704_, 40.0F);
    }

    public float getExhaustionLevel() {
        return this.exhaustionLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void setFoodLevel(int p_38706_) {
        this.foodLevel = p_38706_;
    }

    public void setSaturation(float p_38718_) {
        this.saturationLevel = p_38718_;
    }

    public void setExhaustion(float p_150379_) {
        this.exhaustionLevel = p_150379_;
    }

    public boolean isShouldStarve() {
        return this.shouldStarve;
    }

    public void setShouldStarve(boolean shouldStarve) {
        this.shouldStarve = shouldStarve;
    }
}
