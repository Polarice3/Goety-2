package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.GnasherModel;
import com.Polarice3.Goety.common.entities.ally.Gnasher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GnasherRenderer extends MobRenderer<Gnasher, GnasherModel<Gnasher>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/gnasher/gnasher.png");

    public GnasherRenderer(EntityRendererProvider.Context p_174364_) {
        super(p_174364_, new GnasherModel<>(p_174364_.bakeLayer(ModModelLayer.GNASHER)), 0.5F);
    }

    public ResourceLocation getTextureLocation(Gnasher p_115826_) {
        return TEXTURE;
    }

    @Override
    protected void scale(Gnasher p_115314_, PoseStack p_115315_, float p_115316_) {
        int i = p_115314_.isUpgraded() ? 1 : 0;
        float f = 1.2F + 0.15F * (float)i;
        if (p_115314_.isBaby()) {
            f /= 2.0F;
        }
        p_115315_.scale(f, f, f);
    }

    protected void setupRotations(Gnasher p_115828_, PoseStack p_115829_, float p_115830_, float p_115831_, float p_115832_) {
        super.setupRotations(p_115828_, p_115829_, p_115830_, p_115831_, p_115832_);
        float f = 1.0F;
        float f1 = 1.0F;
        float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * p_115830_);
        p_115829_.mulPose(Axis.YP.rotationDegrees(f2));
        p_115829_.translate(0.0F, 0.0F, 0.0F);
    }
}
