package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RipperModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Ripper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RipperRenderer extends MobRenderer<Ripper, RipperModel<Ripper>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/illagers/ripper.png");

    public RipperRenderer(EntityRendererProvider.Context p_174452_) {
        super(p_174452_, new RipperModel<>(p_174452_.bakeLayer(ModModelLayer.RIPPER)), 0.5F);
    }

    protected float getBob(Ripper p_116528_, float p_116529_) {
        return p_116528_.getTailAngle();
    }

    @Override
    protected void scale(Ripper p_115314_, PoseStack p_115315_, float p_115316_) {
        int i = p_115314_.getRipperSize();
        float f = 1.0F + 0.15F * (float)i;
        p_115315_.scale(f, f, f);
    }

    public void render(Ripper p_116531_, float p_116532_, float p_116533_, PoseStack p_116534_, MultiBufferSource p_116535_, int p_116536_) {
        if (p_116531_.isWet()) {
            float f = p_116531_.getWetShade(p_116533_);
            this.model.setColor(f, f, f);
        }

        super.render(p_116531_, p_116532_, p_116533_, p_116534_, p_116535_, p_116536_);
        if (p_116531_.isWet()) {
            this.model.setColor(1.0F, 1.0F, 1.0F);
        }

    }

    public ResourceLocation getTextureLocation(Ripper p_116526_) {
        return TEXTURES;
    }
}
