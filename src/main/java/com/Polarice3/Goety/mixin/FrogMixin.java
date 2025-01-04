package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frog.class)
public abstract class FrogMixin extends Animal {
    protected FrogMixin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Inject(
            method = {"canEat(Lnet/minecraft/world/entity/LivingEntity;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void canEat(LivingEntity p_218533_, CallbackInfoReturnable<Boolean> callbackInfoReturnable){
        if (p_218533_ instanceof SlimeServant slime) {
            if (slime.getSize() != 1) {
                callbackInfoReturnable.setReturnValue(false);
            }
        }
    }

}
