package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.NoKnockBackDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {

    protected EnderDragonMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"))
    private void hurt(EnderDragonPart part, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
        EnderDragon $this = (EnderDragon) (Object) this;
        if (SpellConfig.SpellDamageEnderDragon.get()) {
            if (damageSource instanceof NoKnockBackDamageSource damageSource1 && damageSource.getEntity() == null) {
                if (damageSource1.getOwner() != null) {
                    $this.hurt(part, new DamageSource(damageSource1.typeHolder(), damageSource1.getDirectAttacker(), damageSource1.getOwner()), amount);
                }
            }
        }
    }
}
