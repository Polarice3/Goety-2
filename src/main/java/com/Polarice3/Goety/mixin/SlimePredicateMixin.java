package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(SlimePredicate.class)
public class SlimePredicateMixin {

    @Final
    @Shadow private MinMaxBounds.Ints size;

    @Inject(method = "matches(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;)Z", at = @At("HEAD"), cancellable = true)
    public void matches(Entity p_223423_, ServerLevel p_223424_, @Nullable Vec3 p_223425_, CallbackInfoReturnable<Boolean> callbackInfoReturnable){
        if (p_223423_ instanceof SlimeServant slime) {
            callbackInfoReturnable.setReturnValue(this.size.matches(slime.getSize()));
        }
    }
}
