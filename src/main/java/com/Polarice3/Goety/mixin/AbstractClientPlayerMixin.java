package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.client.render.LichPlayerRenderer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    @Deprecated
    private AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(method = "getModelName", at = @At("HEAD"), cancellable = true)
    private void getLichModelName(CallbackInfoReturnable<String> cir) {
        LichPlayerRenderer.getLichRenderer(((AbstractClientPlayer)(Object) this)).ifPresent(cir::setReturnValue);
    }

}
