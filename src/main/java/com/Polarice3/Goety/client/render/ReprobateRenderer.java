package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ReprobateModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Reprobate;
import com.Polarice3.Goety.init.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ReprobateRenderer<T extends Reprobate> extends MobRenderer<T, ReprobateModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/reprobate.png");

    public ReprobateRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new ReprobateModel<>(p_174304_.bakeLayer(ModModelLayer.REPROBATE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, p_174304_.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T heresiarch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (heresiarch.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY)) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, heresiarch, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
