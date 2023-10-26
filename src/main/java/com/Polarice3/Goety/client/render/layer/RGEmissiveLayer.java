package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.model.RedstoneGolemModel;
import com.Polarice3.Goety.common.entities.ally.RedstoneGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RGEmissiveLayer<T extends RedstoneGolem, M extends RedstoneGolemModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation texture;
    private final AlphaFunction<T> alphaFunction;
    private final DrawSelector<T, M> drawSelector;

    public RGEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, AlphaFunction<T> p_234887_, DrawSelector<T, M> p_234888_) {
        super(p_234885_);
        this.texture = p_234886_;
        this.alphaFunction = p_234887_;
        this.drawSelector = p_234888_;
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            this.onlyDrawSelectedParts();
            VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
            this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, this.alphaFunction.apply(entity, partialTicks, ageInTicks));
            this.resetDrawForAllParts();
        }
    }

    private void onlyDrawSelectedParts() {
        List<ModelPart> list = this.drawSelector.getPartsToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach((p_234918_) -> {
            p_234918_.skipDraw = true;
        });
        list.forEach((p_234916_) -> {
            p_234916_.skipDraw = false;
        });
    }

    private void resetDrawForAllParts() {
        this.getParentModel().root().getAllParts().forEach((p_234913_) -> {
            p_234913_.skipDraw = false;
        });
    }

    public interface AlphaFunction<T extends RedstoneGolem> {
        float apply(T p_234920_, float p_234921_, float p_234922_);
    }

    public interface DrawSelector<T extends RedstoneGolem, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M p_234924_);
    }
}
