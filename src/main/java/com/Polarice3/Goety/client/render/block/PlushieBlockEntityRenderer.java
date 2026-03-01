package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.PlushieModel;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.common.blocks.entities.PlushieBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PlushieBlockEntityRenderer implements BlockEntityRenderer<PlushieBlockEntity> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/plushie/0.png");

    public PlushieBlockEntityRenderer(BlockEntityRendererProvider.Context p_i226015_1_) {
    }

    public void render(PlushieBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        BlockState blockstate = pBlockEntity.getBlockState();
        float f1 = 22.5F * blockstate.getValue(PlushieBlock.ROTATION);
        renderPlushie(pBlockEntity, pPartialTicks, f1, pMatrixStack, pBuffer, pCombinedLight);
    }

    public static void renderPlushie(PlushieBlockEntity pBlockEntity, float pPartialTicks, float rotateY, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pLight) {
        PlushieModel plushieModel = new PlushieModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModBlockLayer.PLUSHIE));
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5D, 0.0D, 0.5D);

        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        float f = pBlockEntity.getAnimation(pPartialTicks);
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(getTexture(pBlockEntity.getBlockState())));
        plushieModel.setupAnim(f, rotateY, 0.0F);
        plushieModel.renderToBuffer(pMatrixStack, consumer, pLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
    }

    public static void renderItemPlushie(ItemStack stack, BlockState blockState, float rotateY, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight) {
        PlushieModel plushieModel = new PlushieModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModBlockLayer.PLUSHIE));
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5D, 0.0D, 0.5D);

        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.scale(0.5F, 0.5F, 0.5F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, RenderType.entityTranslucent(getTexture(blockState)), true, stack.hasFoil());
        plushieModel.setupAnim(0, rotateY, 0.0F);
        plushieModel.renderToBuffer(pMatrixStack, vertexConsumer, pCombinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
    }

    public static ResourceLocation getTexture(BlockState blockState) {
        ResourceLocation texture = TEXTURE;
        if (blockState.getBlock() instanceof PlushieBlock) {
            if (blockState.getValue(PlushieBlock.TYPE) == 1) {
                texture = Goety.location("textures/entity/plushie/1.png");
            } else if (blockState.getValue(PlushieBlock.TYPE) == 2) {
                texture = Goety.location("textures/entity/plushie/2.png");
            } else if (blockState.getValue(PlushieBlock.TYPE) == 3) {
                texture = Goety.location("textures/entity/plushie/3.png");
            } else if (blockState.getValue(PlushieBlock.TYPE) == 4) {
                texture = Goety.location("textures/entity/plushie/4.png");
            } else if (blockState.getValue(PlushieBlock.TYPE) == 5) {
                texture = Goety.location("textures/entity/plushie/5.png");
            }
        }
        return texture;
    }
}
