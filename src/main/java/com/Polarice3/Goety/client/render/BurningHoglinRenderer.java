package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.BurningHoglin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class BurningHoglinRenderer extends MobRenderer<BurningHoglin, HoglinModel<BurningHoglin>> {
    private static final ResourceLocation HOGLIN_LOCATION = Goety.location("textures/entity/servants/burning_hoglin.png");

    public BurningHoglinRenderer(EntityRendererProvider.Context p_174165_) {
        super(p_174165_, new HoglinModel<>(p_174165_.bakeLayer(ModelLayers.HOGLIN)), 0.7F);
    }

    @Nullable
    protected RenderType getRenderType(BurningHoglin p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        ResourceLocation resourcelocation = this.getTextureLocation(p_115322_);
        if (p_115324_) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (p_115323_) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return p_115325_ ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    protected void scale(BurningHoglin p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(0.75F, 0.75F, 0.75F);
    }

    public ResourceLocation getTextureLocation(BurningHoglin p_114862_) {
        return HOGLIN_LOCATION;
    }
}
