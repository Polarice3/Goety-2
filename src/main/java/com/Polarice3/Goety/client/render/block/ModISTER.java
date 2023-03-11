package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.TallSkullBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ModISTER extends BlockEntityWithoutLevelRenderer {

    public ModISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pCamera, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        Item item = pStack.getItem();

        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof TallSkullBlock) {
                if(pCamera == ItemTransforms.TransformType.GUI) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.5F, 0.5F, 0.5F);
                    pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(30));
                    pMatrixStack.mulPose(Vector3f.YN.rotationDegrees(-45));
                    pMatrixStack.translate(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.translate(0.0F, 0.25F, 0.0F);
                    TallSkullBlockEntityRenderer.renderSkull(null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.popPose();

                } else {
                    TallSkullBlockEntityRenderer.renderSkull(null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            }
        }
    }
}
