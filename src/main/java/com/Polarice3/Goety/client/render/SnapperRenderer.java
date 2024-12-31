package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SnapperModel;
import com.Polarice3.Goety.common.entities.ally.Snapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class SnapperRenderer extends MobRenderer<Snapper, SnapperModel<Snapper>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/snapper/snapper.png");

    public SnapperRenderer(EntityRendererProvider.Context p_174364_) {
        super(p_174364_, new SnapperModel<>(p_174364_.bakeLayer(ModModelLayer.SNAPPER)), 0.4F);
        this.addLayer(new SnapperEyesLayer<>(this));
    }

    public ResourceLocation getTextureLocation(Snapper p_115826_) {
        return TEXTURE;
    }

    @Override
    protected void scale(Snapper p_115314_, PoseStack p_115315_, float p_115316_) {
        int i = p_115314_.isUpgraded() ? 1 : 0;
        float f = 1.0F + 0.15F * (float)i;
        p_115315_.scale(f, f, f);
    }

    protected void setupRotations(Snapper p_115828_, PoseStack p_115829_, float p_115830_, float p_115831_, float p_115832_) {
        super.setupRotations(p_115828_, p_115829_, p_115830_, p_115831_, p_115832_);
        float f = 1.0F;
        float f1 = 1.0F;
        if (!p_115828_.isInWater()) {
            f = 1.3F;
            f1 = 1.7F;
        }

        float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * p_115830_);
        p_115829_.mulPose(Axis.YP.rotationDegrees(f2));
        p_115829_.translate(0.0F, 0.0F, 0.0F);
        if (!p_115828_.isInWater()) {
            p_115829_.translate(0.2F, 0.1F, 0.0F);
            p_115829_.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

    }

    public static class SnapperEyesLayer<T extends Mob, M extends SnapperModel<T>> extends EyesLayer<T, M> {
        private static final RenderType EYES = RenderType.eyes(Goety.location("textures/entity/servants/snapper/snapper_eyes.png"));

        public SnapperEyesLayer(RenderLayerParent<T, M> p_117507_) {
            super(p_117507_);
        }

        public RenderType renderType() {
            return EYES;
        }
    }
}
