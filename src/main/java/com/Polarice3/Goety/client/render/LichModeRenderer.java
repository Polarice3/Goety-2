package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.LichCuriosLayer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Based and modified from @TeamLapen's Werewolf Rendering codes: <a href="https://github.com/TeamLapen/Werewolves/blob/1.20/src/main/java/de/teamlapen/werewolves/client/render/player/WerewolfPlayerBeastRenderer.java">...</a>
 */
public class LichModeRenderer extends LichPlayerRenderer<AbstractClientPlayer, LichModeModel<AbstractClientPlayer>> {
    public static ResourceLocation TEXTURE = Goety.location("textures/entity/lich.png");

    public LichModeRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModeModel<>(context.bakeLayer(ModModelLayer.LICH)));
        this.addLayer(new LichEyesLayer<>(this));
        this.addLayer(new LichCuriosLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer p_114482_) {
        return TEXTURE;
    }

    public static class LichEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M> {
        public static ResourceLocation EYES = Goety.location("textures/entity/lich_eyes.png");

        public LichEyesLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }
}
