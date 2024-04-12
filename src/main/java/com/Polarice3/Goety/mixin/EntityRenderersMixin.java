package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.LichModeRenderer;
import com.Polarice3.Goety.utils.ClientUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "createPlayerRenderers(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void createLichRenderer(EntityRendererProvider.Context context, CallbackInfoReturnable<Map<String, EntityRenderer<? extends Player>>> cir, ImmutableMap.Builder<String, EntityRenderer<? extends Player>> builder) {
        if (ClientUtils.noLoadingExceptions()) {
            builder.put(Goety.MOD_ID + ":lich", new LichModeRenderer(context));
        }
    }
}
