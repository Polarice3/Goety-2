package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.LichdomHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow public abstract MobType getMobType();

    @Shadow public abstract float getMaxHealth();

    protected LivingEntityMixin(EntityType<? extends Entity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "isInvertedHealAndHarm", at = @At("HEAD"), cancellable = true)
    public void isInvertedHealAndHarm(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (LichdomHelper.isLich(this)) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void canAttack(LivingEntity target, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (MainConfig.LichUndeadFriends.get()) {
            if (this.getMobType() == MobType.UNDEAD || this.getType().is(ModTags.EntityTypes.LICH_NEUTRAL)) {
                if (LichdomHelper.isLich(target)) {
                    if (MainConfig.LichPowerfulFoes.get()) {
                        if (this.getMaxHealth() <= MainConfig.LichPowerfulFoesHealth.get()){
                            callbackInfoReturnable.setReturnValue(false);
                        }
                    } else {
                        callbackInfoReturnable.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    public void isSensitiveToWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (this.hasEffect(GoetyEffects.SNOW_SKIN.get())) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
    public void randomTeleport(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (this.hasEffect(GoetyEffects.ENDER_GROUND.get())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void jumpFromGround(CallbackInfo callbackInfo) {
        if (this.hasEffect(GoetyEffects.STUNNED.get()) || this.hasEffect(GoetyEffects.TANGLED.get())) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateInvisibilityStatus", at = @At(value = "TAIL"))
    public void updateInvisibilityStatus(CallbackInfo callbackInfo) {
        if (this.hasEffect(GoetyEffects.SHADOW_WALK.get())) {
            this.setInvisible(true);
        }
    }
}
