package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ReprobateModel;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.ReprobateServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ReprobateServantRenderer<T extends ReprobateServant> extends MobRenderer<T, ReprobateModel<T>> {
    private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/servants/cultist/reprobate.png");
    private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/cultist/reprobate.png");

    public ReprobateServantRenderer(EntityRendererProvider.Context p_174304_) {
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
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.ReprobateServantTexture.get()) {
            return ORIGINAL;
        }
        return WITCH_LOCATION;
    }
}
