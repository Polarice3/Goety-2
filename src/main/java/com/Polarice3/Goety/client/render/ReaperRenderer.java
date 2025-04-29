package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ReaperModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractReaper;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class ReaperRenderer<T extends AbstractReaper> extends MobRenderer<T, ReaperModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/specter/reaper.png");

    public ReaperRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new ReaperModel<>(renderManagerIn.bakeLayer(ModModelLayer.REAPER)), 0.0F);
        this.addLayer(new ReaperGlowLayer<>(this));
        this.addLayer(new ReaperBandsLayer<>(this, renderManagerIn.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public static class ReaperGlowLayer<T extends AbstractReaper, M extends ReaperModel<T>> extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = ModRenderType.wraith(Goety.location("textures/entity/specter/reaper_glow.png"));

        public ReaperGlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.deathTime < 12){
                super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }

    public static class ReaperBandsLayer<T extends AbstractReaper> extends RenderLayer<T, ReaperModel<T>> {
        private static final ResourceLocation TEXTURES = Goety.location("textures/entity/specter/reaper_servant_bands.png");
        private final ReaperModel<T> layerModel;

        public ReaperBandsLayer(RenderLayerParent<T, ReaperModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
            super(p_i50919_1_);
            this.layerModel = new ReaperModel<>(p_174555_.bakeLayer(ModModelLayer.REAPER));
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T wraith, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (MobsConfig.ReaperServantTexture.get() && !wraith.isHostile()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, wraith, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
