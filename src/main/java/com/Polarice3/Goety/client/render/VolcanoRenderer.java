package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VolcanoModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class VolcanoRenderer extends EntityRenderer<AbstractMonolith> {
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/volcano.png");
    private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/monolith/volcano_active.png"));
    private final VolcanoModel<AbstractMonolith> model;

    public VolcanoRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new VolcanoModel<>(p_i47208_1_.bakeLayer(ModModelLayer.VOLCANO));
    }

    public void render(AbstractMonolith pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        float f = Math.min(AbstractMonolith.getEmergingTime(), pEntity.getAge());
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(pEntity.getYRot()));
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, 0.0D, 0.0D);
        pMatrixStack.scale(1.0F, 1.0F, 1.0F);
        this.model.setupAnim(pEntity, f, 0.0F, pPartialTicks, pEntity.getYRot(), pEntity.getXRot());
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity)));
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!pEntity.isEmerging() && !pEntity.isInvisible()) {
            RenderType renderType = getActivatedTextureLocation(pEntity);
            if (renderType != null) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(renderType);
                this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Nullable
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        return RENDER_TYPE;
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }
}
