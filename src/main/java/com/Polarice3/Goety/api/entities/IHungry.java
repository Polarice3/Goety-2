package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.utils.ModFoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

public interface IHungry {

    ModFoodData getFoodData();

    default void checkMovementStatistics(double x, double y, double z) {
        if (this instanceof LivingEntity livingEntity) {
            if (!livingEntity.isPassenger()) {
                double sqrt = Math.sqrt(x * x + y * y + z * z);
                if (livingEntity.isSwimming()) {
                    int i = Math.round((float) sqrt * 100.0F);
                    if (i > 0) {
                        this.causeFoodExhaustion(0.01F * (float) i * 0.01F);
                    }
                } else if (livingEntity.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
                    int j = Math.round((float) sqrt * 100.0F);
                    if (j > 0) {
                        this.causeFoodExhaustion(0.01F * (float) j * 0.01F);
                    }
                } else {
                    double sqrt1 = Math.sqrt(x * x + z * z);
                    if (livingEntity.isInWater()) {
                        int k = Math.round((float) sqrt1 * 100.0F);
                        if (k > 0) {
                            this.causeFoodExhaustion(0.01F * (float) k * 0.01F);
                        }
                    } else if (livingEntity.onGround()) {
                        int l = Math.round((float) sqrt1 * 100.0F);
                        if (l > 0) {
                            if (livingEntity.isSprinting()) {
                                this.causeFoodExhaustion(0.1F * (float) l * 0.01F);
                            } else if (livingEntity.isCrouching()) {
                                this.causeFoodExhaustion(0.0F * (float) l * 0.01F);
                            } else {
                                this.causeFoodExhaustion(0.0F * (float) l * 0.01F);
                            }
                        }
                    }
                }

            }
        }
    }

    default void causeFoodExhaustion(float p_36400_) {
        if (this instanceof LivingEntity livingEntity) {
            if (!livingEntity.level.isClientSide) {
                this.getFoodData().addExhaustion(p_36400_);
            }
        }
    }
}
