package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Monster {

    protected WitherBossMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Shadow
    public abstract int getInvulnerableTicks();

    @Shadow
    public abstract boolean isPowered();

    @Shadow
    private int destroyBlocksTick;

    @Shadow
    @Final
    private int[] idleHeadUpdates;

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    private void hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (damageSource.getEntity() instanceof IOwned owned && owned instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.UNDEAD) {
            if (this.isInvulnerableTo(damageSource)) {
                callbackInfo.setReturnValue(false);
                callbackInfo.cancel();
            } else {
                if (this.getInvulnerableTicks() > 0 && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    callbackInfo.setReturnValue(false);
                    callbackInfo.cancel();
                } else {
                    Entity entity1;
                    if (this.isPowered()) {
                        entity1 = damageSource.getDirectEntity();
                        if (entity1 instanceof AbstractArrow) {
                            callbackInfo.setReturnValue(false);
                            callbackInfo.cancel();
                        }
                    }
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for(int i = 0; i < this.idleHeadUpdates.length; ++i) {
                        this.idleHeadUpdates[i] += 3;
                    }

                    callbackInfo.setReturnValue(super.hurt(damageSource, amount));
                    callbackInfo.cancel();
                }
            }
        }
    }
}
