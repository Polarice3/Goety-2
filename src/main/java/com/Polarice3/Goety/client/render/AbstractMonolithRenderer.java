package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.MonolithModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public abstract class AbstractMonolithRenderer extends EntityRenderer<AbstractMonolith> {
    private final MonolithModel<AbstractMonolith> model;

    public AbstractMonolithRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new MonolithModel<>(p_i47208_1_.bakeLayer(ModModelLayer.MONOLITH));
    }

    public void render(AbstractMonolith pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        float f = Math.min(AbstractMonolith.getEmergingTime(), pEntity.getAge());
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, 0.0D, 0.0D);
        pMatrixStack.scale(1.0F, 1.0F, 1.0F);
        this.model.setupAnim(pEntity, f, 0.0F, pPartialTicks, pEntity.getYRot(), pEntity.getXRot());
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity)));
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!pEntity.isEmerging() && !pEntity.isInvisible()) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(getActivatedTextureLocation());
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            AbstractMonolith.Crackiness irongolem$crackiness = pEntity.getCrackiness();
            if (irongolem$crackiness != AbstractMonolith.Crackiness.NONE) {
                ResourceLocation resourcelocation = resourceLocations().get(irongolem$crackiness);
                renderColoredCutoutModel(this.model, resourcelocation, pMatrixStack, pBuffer, pPackedLight, 1.0F, 1.0F, 1.0F);
            }
        }
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> p_117377_, ResourceLocation p_117378_, PoseStack p_117379_, MultiBufferSource p_117380_, int p_117381_, float p_117383_, float p_117384_, float p_117385_) {
        VertexConsumer vertexconsumer = p_117380_.getBuffer(RenderType.entityCutoutNoCull(p_117378_));
        p_117377_.renderToBuffer(p_117379_, vertexconsumer, p_117381_, OverlayTexture.NO_OVERLAY, p_117383_, p_117384_, p_117385_, 1.0F);
    }

    public abstract RenderType getActivatedTextureLocation();

    public abstract Map<AbstractMonolith.Crackiness, ResourceLocation> resourceLocations();
}
