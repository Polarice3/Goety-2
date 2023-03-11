package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class CursedCageRenderer implements BlockEntityRenderer<CursedCageBlockEntity> {
    public CursedCageRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    public void render(CursedCageBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        ItemStack itemStack = pBlockEntity.getItem();
        Minecraft minecraft = Minecraft.getInstance();
        if (!itemStack.isEmpty()){
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.5F, 0.25F, 0.5F);
            pMatrixStack.scale(1.0F, 1.0F, 1.0F);
            assert minecraft.level != null;
            if (pBlockEntity.getSpinning() > 0){
                pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(12 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            } else {
                pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            }
            minecraft.getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.GROUND, pCombinedLight, pCombinedOverlay, pMatrixStack, pBuffer, 0);
            pMatrixStack.popPose();
        }
    }

}
