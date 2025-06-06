package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRendererMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Inject(
            method = {"isShaking(Lnet/minecraft/world/entity/LivingEntity;)Z"},
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void isShaking(T p_115304_, CallbackInfoReturnable<Boolean> callback) {
        if (MiscCapHelper.getShakeTime(p_115304_) > 0) {
            callback.setReturnValue(true);
        }
    }
}
