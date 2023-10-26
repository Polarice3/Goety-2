package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.IceChunkModel;
import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class IceChunkRenderer extends EntityRenderer<IceChunk> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/ice_chunk.png");
    private static final ResourceLocation SPAWN = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/ice_chunk_spawn.png");
    private final IceChunkModel<IceChunk> model;

    public IceChunkRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new IceChunkModel<>(renderManagerIn.bakeLayer(ModModelLayer.ICE_CHUNK));
    }

    public void render(IceChunk pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        float f;
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        if (pEntity.isStarting()){
            f = pEntity.hovering / 10.0F;
            ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(this.getTextureLocation(pEntity)));
            packedLightIn = 15728640;
        } else {
            f = 2.0F;
        }
        pMatrixStack.scale(-f, -f, f);
        pMatrixStack.translate(0.0D, -1.45D, 0.0D);
        this.model.setupAnim(pEntity, 0.0F, 0.0F, pPartialTicks, 0, 0);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        pMatrixStack.popPose();
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
    }

    protected int getBlockLightLevel(IceChunk iceChunk, BlockPos blockPos){
        if (iceChunk.isStarting()){
            return 15;
        } else {
            return super.getBlockLightLevel(iceChunk, blockPos);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(IceChunk pEntity) {
        if (pEntity.isStarting()){
            return SPAWN;
        }
        return TEXTURE;
    }
}
