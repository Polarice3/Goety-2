package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VizierCloneModel;
import com.Polarice3.Goety.common.entities.hostile.servants.VizierClone;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class VizierCloneRenderer extends MobRenderer<VizierClone, VizierCloneModel> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/vizier_clone.png");

    public VizierCloneRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VizierCloneModel(renderManagerIn.bakeLayer(ModModelLayer.VIZIER_CLONE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected int getBlockLightLevel(VizierClone p_114496_, BlockPos p_114497_) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VizierClone entity) {
        return TEXTURE;
    }
}
