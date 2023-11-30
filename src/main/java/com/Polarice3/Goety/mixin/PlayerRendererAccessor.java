package com.Polarice3.Goety.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRenderer.class)
public interface PlayerRendererAccessor {
    @Invoker("setupRotations")
    void limbs_setupRotations(AbstractClientPlayer clientPlayer, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks);
}
