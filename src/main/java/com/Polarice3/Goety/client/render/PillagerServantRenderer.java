package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import com.Polarice3.Goety.common.entities.ally.illager.PillagerServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class PillagerServantRenderer<T extends PillagerServant> extends MobRenderer<T, IllagerServantModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/pillager.png");
    protected static final ResourceLocation ORIGINAL = new ResourceLocation("textures/entity/illager/pillager.png");

    public PillagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IllagerServantModel<>(renderManagerIn.bakeLayer(ModModelLayer.ILLAGER_SERVANT)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.PillagerServantTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE;
        }
    }
}
