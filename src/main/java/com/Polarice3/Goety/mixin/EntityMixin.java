package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(at = @At("HEAD"), method = "turn", ordinal = 0, argsOnly = true)
    private double I$XRotate(double value) {
        return this.goety2$playerPerspectiveValue(value);
    }

    @ModifyVariable(at = @At("HEAD"), method = "turn", ordinal = 1, argsOnly = true)
    private double I$YRotate(double value) {
        return this.goety2$playerPerspectiveValue(value);
    }

    @Unique
    private double goety2$playerPerspectiveValue(double value) {
        boolean flag = (Entity) (Object) this instanceof Player player && SEHelper.hasCamera(player);
        return flag ? 0 : value;
    }

    @Inject(
            method = {"canCollideWith(Lnet/minecraft/world/entity/Entity;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void canCollideWith(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if(other instanceof AbstractVine vine && vine.passableEntities((Entity)(Object)this)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    public void isSwimming(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity && livingEntity.hasEffect(GoetyEffects.PLUNGE.get())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

}
