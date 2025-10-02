package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.CrusherModel;
import com.Polarice3.Goety.common.entities.ally.illager.CrusherServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrusherServantRenderer<T extends CrusherServant> extends MobRenderer<T, CrusherModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/crusher.png");
    protected static final ResourceLocation STORM = Goety.location("textures/entity/servants/illager/crusher_storm.png");
    protected static final ResourceLocation ORIGINAL = Goety.location("textures/entity/illagers/crusher.png");
    protected static final ResourceLocation ORIGINAL_STORM = Goety.location("textures/entity/illagers/crusher_storm.png");

    public CrusherServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CrusherModel<>(renderManagerIn.bakeLayer(ModModelLayer.CRUSHER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = entity.isStorm() ? 1.25F : 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.CrusherServantTexture.get()){
            if (entity.isStorm()){
                return ORIGINAL_STORM;
            }
            return ORIGINAL;
        } else {
            if (entity.isStorm()){
                return STORM;
            }
            return TEXTURE;
        }
    }
}
