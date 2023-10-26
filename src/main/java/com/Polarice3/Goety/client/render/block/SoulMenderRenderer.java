package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.SoulMenderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SoulMenderRenderer implements BlockEntityRenderer<SoulMenderBlockEntity> {
    public SoulMenderRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    public void render(SoulMenderBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        ItemStack itemStack = pBlockEntity.getItem(0);
        Minecraft minecraft = Minecraft.getInstance();
        if (!itemStack.isEmpty()){
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.5F, 0.5F, 0.5F);
            pMatrixStack.scale(1.0F, 1.0F, 1.0F);
            assert minecraft.level != null;
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            minecraft.getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, pCombinedLight, pCombinedOverlay, pMatrixStack, pBuffer, pBlockEntity.getLevel(), 0);
            pMatrixStack.popPose();
        }
    }

}
