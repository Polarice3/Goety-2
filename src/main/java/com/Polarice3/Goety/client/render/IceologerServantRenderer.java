package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.IceologerModel;
import com.Polarice3.Goety.common.entities.ally.illager.IceologerServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class IceologerServantRenderer<T extends IceologerServant> extends MobRenderer<T, IceologerModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/iceologer.png");
    protected static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/illager/iceologer_original.png");

    public IceologerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IceologerModel<>(renderManagerIn.bakeLayer(ModModelLayer.ICEOLOGER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.IceologerServantTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE;
        }
    }
}
