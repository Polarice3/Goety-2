package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.LichCuriosLayer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Based and modified from @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/player/WerewolfPlayerBeastRenderer.java">...</a>
 */
public class LichModeRenderer extends LichPlayerRenderer<AbstractClientPlayer, LichModeModel<AbstractClientPlayer>> {
    public static ResourceLocation TEXTURE = Goety.location("textures/entity/lich.png");

    public LichModeRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModeModel<>(context.bakeLayer(ModModelLayer.LICH)));
        this.addLayer(new LichEyesLayer<>(this));
        this.addLayer(new LichCuriosLayer<>(this));
        this.layers.removeIf(layer -> layer.getClass().getName().startsWith("dev.tr7zw.skinlayers"));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer p_114482_) {
        return TEXTURE;
    }

    public static class LichEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M> {
        public static ResourceLocation EYES = Goety.location("textures/entity/lich_eyes.png");
        public static ResourceLocation CUSTOM = Goety.location("textures/entity/lich_eyes_custom.png");

        public LichEyesLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_ instanceof Player player && LichdomHelper.lichModeColor(player) > -1) {
                VertexConsumer vertexconsumer = p_116984_.getBuffer(RenderType.eyes(CUSTOM));
                ColorUtil colorUtil = new ColorUtil(LichdomHelper.lichModeColor(player));
                this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            } else {
                super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }
}
