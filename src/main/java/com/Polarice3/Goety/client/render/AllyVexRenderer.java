package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.MinionModel;
import com.Polarice3.Goety.common.entities.ally.AllyVex;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class AllyVexRenderer extends HumanoidMobRenderer<AllyVex, MinionModel<AllyVex>> {
    private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
    private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

    public AllyVexRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new MinionModel<>(p_174435_.bakeLayer(ModModelLayer.MINION)), 0.3F);
        this.addLayer(new AllyVexBandsLayer(this, p_174435_.getModelSet()));
    }

    protected int getBlockLightLevel(AllyVex p_116298_, BlockPos p_116299_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(AllyVex p_116292_) {
        return p_116292_.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
    }

    protected void scale(AllyVex p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(0.4F, 0.4F, 0.4F);
    }

    public static class AllyVexBandsLayer extends RenderLayer<AllyVex, MinionModel<AllyVex>> {
        private static final ResourceLocation TEXTURES = new ResourceLocation(Goety.MOD_ID, "textures/entity/servants/ally_vex_bands.png");
        private final MinionModel<AllyVex> layerModel;

        public AllyVexBandsLayer(RenderLayerParent<AllyVex, MinionModel<AllyVex>> p_i50919_1_, EntityModelSet p_174555_) {
            super(p_i50919_1_);
            this.layerModel = new MinionModel(p_174555_.bakeLayer(ModModelLayer.MINION));
        }

        @Override
        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, AllyVex pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (MobsConfig.VexTexture.get()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, pPartialTicks, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
