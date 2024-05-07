package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ScatterMineModel;
import com.Polarice3.Goety.common.entities.projectiles.ScatterMine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ScatterMineRenderer<T extends ScatterMine> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/scatter_mine.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/projectiles/scatter_mine_glow.png");
    private static final ResourceLocation GLOW_SPELL = Goety.location("textures/entity/projectiles/scatter_mine_glow_spell.png");
    private final ScatterMineModel<T> model;

    public ScatterMineRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.model = new ScatterMineModel<>(p_i47208_1_.bakeLayer(ModModelLayer.SCATTER_MINE));
    }

    public void render(T pEntity, float entityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - pEntity.getYRot()));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
        float scale = pEntity.size;
        if (!pEntity.startShrink()){
            scale = 1.0F;
        }
        pMatrixStack.scale(-scale, -scale, scale);
        pMatrixStack.translate(0.0D, -1.45D, 0.0D);
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        ResourceLocation resourceLocation = pEntity.isSpell() ? GLOW_SPELL : GLOW;
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(resourceLocation));
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, pEntity.getGlow);
        pMatrixStack.popPose();
        super.render(pEntity, entityYaw, pPartialTicks, pMatrixStack, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        if (!pEntity.startGlow()){
            if (pEntity.isSpell()){
                return GLOW_SPELL;
            }
            return GLOW;
        }
        return TEXTURE;
    }
}
