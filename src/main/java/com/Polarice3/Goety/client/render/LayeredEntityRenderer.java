package com.Polarice3.Goety.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.List;

public abstract class LayeredEntityRenderer<T extends Entity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected M model;
    protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

    public LayeredEntityRenderer(EntityRendererProvider.Context p_174289_, M p_174290_) {
        super(p_174289_);
        this.model = p_174290_;
    }

    public final boolean addLayer(RenderLayer<T, M> p_115327_) {
        return this.layers.add(p_115327_);
    }

    public void render(T p_115308_, float p_115309_, float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_) {
        p_115311_.pushPose();
        float f = Mth.rotlerp(p_115308_.yRotO, p_115308_.getYRot(), p_115310_);
        float f6 = Mth.lerp(p_115310_, p_115308_.xRotO, p_115308_.getXRot());
        float f7 = p_115308_.tickCount + p_115310_;
        for(RenderLayer<T, M> renderlayer : this.layers) {
            renderlayer.render(p_115311_, p_115312_, p_115313_, p_115308_, 0.0F, 0.0F, p_115310_, f7, f, f6);
        }
        p_115311_.popPose();
        super.render(p_115308_, p_115309_, p_115310_, p_115311_, p_115312_, p_115313_);
    }

    @Override
    public M getModel() {
        return this.model;
    }
}
