package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RattledRenderer extends SkeletonRenderer {
    private static final ResourceLocation RATTLED_ORIGINAL = Goety.location("textures/entity/servants/skeleton/rattled.png");

    public RattledRenderer(EntityRendererProvider.Context p_174409_) {
        super(p_174409_, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
        this.addLayer(new RattledClothingLayer<>(this, p_174409_.getModelSet()));
    }

    @Override
    protected boolean isShaking(AbstractSkeleton p_113773_) {
        return super.isShaking(p_113773_) || p_113773_.tickCount % 100 > 80;
    }

    public ResourceLocation getTextureLocation(AbstractSkeleton p_116458_) {
        return RATTLED_ORIGINAL;
    }

    public class RattledClothingLayer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation RATTLED_ORIGINAL = Goety.location("textures/entity/servants/skeleton/rattled_overlay.png");
        private final SkeletonModel<T> layerModel;

        public RattledClothingLayer(RenderLayerParent<T, M> p_174544_, EntityModelSet p_174545_) {
            super(p_174544_);
            this.layerModel = new SkeletonModel<>(p_174545_.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
        }

        public void render(PoseStack p_117553_, MultiBufferSource p_117554_, int p_117555_, T p_117556_, float p_117557_, float p_117558_, float p_117559_, float p_117560_, float p_117561_, float p_117562_) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, RATTLED_ORIGINAL, p_117553_, p_117554_, p_117555_, p_117556_, p_117557_, p_117558_, p_117560_, p_117561_, p_117562_, p_117559_, 1.0F, 1.0F, 1.0F);
        }
    }
}
