package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.LichCuriosLayer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Based and modified from @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/player/WerewolfPlayerBeastRenderer.java">...</a>
 */
public class LichModeRenderer extends LichPlayerRenderer<AbstractClientPlayer, LichModeModel<AbstractClientPlayer>> {
    public static ResourceLocation TEXTURE = Goety.location("textures/entity/lich.png");

    public LichModeRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModeModel<>(context.bakeLayer(ModModelLayer.LICH)));
        this.addLayer(new LichCuriosLayer<>(this));
    }

    public boolean renderPlayer(AbstractClientPlayer player, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (LichdomHelper.isInLichMode(player)){
            this.render(player, entityYaw, partialTicks, stack, buffer, packedLight);
            return true;
        }
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer p_114482_) {
        return TEXTURE;
    }
}
