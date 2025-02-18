package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SpiderEggModel;
import com.Polarice3.Goety.common.entities.neutral.SpiderEgg;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpiderEggRenderer extends MobRenderer<SpiderEgg, SpiderEggModel<SpiderEgg>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/spider_egg.png");
    private static final ResourceLocation HATCH = Goety.location("textures/entity/spider_egg_hatch.png");

    public SpiderEggRenderer(EntityRendererProvider.Context p_174452_) {
        super(p_174452_, new SpiderEggModel<>(p_174452_.bakeLayer(ModModelLayer.SPIDER_EGG)), 0.5F);
    }

    protected void scale(SpiderEgg p_114046_, PoseStack p_114047_, float p_114048_) {
        float f = p_114046_.getSwelling(p_114048_);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.1F) * f1;
        float f3 = f2 - 0.15F;
        p_114047_.scale(f3, f2, f3);
    }

    public ResourceLocation getTextureLocation(SpiderEgg p_116526_) {
        if (p_116526_.isHatching()){
            return HATCH;
        }
        return TEXTURES;
    }
}
