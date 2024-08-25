package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Based and modified from @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/core/ModPlayerRenderer.java">...</a>
 */
public class ModPlayerRenderer {

    private final LichModeRenderer renderer;

    public ModPlayerRenderer(EntityRendererProvider.Context context) {
        this.renderer = new LichModeRenderer(context);
    }

    public boolean renderPlayer(AbstractClientPlayer player, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (LichdomHelper.isLich(player) && LichdomHelper.isInLichMode(player)) {
            this.renderer.render(player, entityYaw, partialTicks, stack, buffer, packedLight);
            return true;
        }
        return false;
    }
}
