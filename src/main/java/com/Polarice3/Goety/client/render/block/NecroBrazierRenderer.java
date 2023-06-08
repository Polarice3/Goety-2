package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.NecroBrazierBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class NecroBrazierRenderer implements BlockEntityRenderer<NecroBrazierBlockEntity> {
    public NecroBrazierRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    public void render(NecroBrazierBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        pMatrixStack.translate(0.5F, 0.5F, 0.5F);
        for (int i = 0; i < pBlockEntity.getContainer().getContainerSize(); i++) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            ItemStack stack = pBlockEntity.getContainer().getItem(i);
            if (!stack.isEmpty()) {
                minecraft.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, pCombinedLight, pCombinedOverlay, pMatrixStack, pBuffer, 0);
            }
            pMatrixStack.translate(0.4F, 0.0F, 0.0F);
            pMatrixStack.popPose();
        }
    }
}
