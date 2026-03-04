package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.block.PlushieBlockEntityRenderer;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class PlushieCurioRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (renderLayerParent.getModel() instanceof HeadedModel headModel) {
            Item item = stack.getItem();
            if (!stack.isEmpty() && item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof PlushieBlock) {
                    matrixStack.pushPose();
                    headModel.getHead().translateAndRotate(matrixStack);
                    float size = 1.875F;
                    matrixStack.scale(size, -size, -size);
                    matrixStack.translate(-0.5D, 0.255D, -0.5D);
                    PlushieBlockEntityRenderer.renderItemPlushie(stack, block.defaultBlockState(), 180.0F, matrixStack, renderTypeBuffer, light);
                    matrixStack.popPose();

                }
            }
        }
    }
}
