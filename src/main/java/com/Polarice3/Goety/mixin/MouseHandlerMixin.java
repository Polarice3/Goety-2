package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.client.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "turnPlayer",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),cancellable = true,locals = LocalCapture.CAPTURE_FAILHARD)
    private void goetyOnTurnPlayer(CallbackInfo ci, double d0, double d1, double d4, double d5, double d6, double d2, double d3, int i) {
        if (ClientEvents.handleKeyPress(this.minecraft.player)) {
            ci.cancel();
        }
    }
}
