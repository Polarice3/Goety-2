package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.layer.AllyVexBandsLayer;
import com.Polarice3.Goety.client.render.model.MinionModel;
import com.Polarice3.Goety.common.entities.ally.AllyVex;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
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
}
