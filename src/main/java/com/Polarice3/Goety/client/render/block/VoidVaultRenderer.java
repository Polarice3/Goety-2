package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.VoidVaultBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.void_vault.VoidVaultClientData;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidVaultRenderer implements BlockEntityRenderer<VoidVaultBlockEntity> {
    private final ItemRenderer itemRenderer;

    public VoidVaultRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(VoidVaultBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        if (VoidVaultBlockEntity.Client.hasDisplayItem(pBlockEntity.getSharedData())) {
            Level level = pBlockEntity.getLevel();
            if (level != null) {
                ItemStack itemStack = pBlockEntity.getSharedData().getDisplayItem();
                if (!itemStack.isEmpty()) {
                    VoidVaultClientData vaultClientData = pBlockEntity.getClientData();
                    renderDisplayItem(pPartialTicks, level, pMatrixStack, pBuffer, pCombinedLight, itemStack.copyWithCount(1), this.itemRenderer, vaultClientData.getPreviousDisplayRotation(), vaultClientData.getDisplayRotation(), level.getRandom());
                }
            }
        }
    }

    @VisibleForTesting
    static int getRenderedAmount(int stackSize) {
        if (stackSize <= 1) {
            return 1;
        } else if (stackSize <= 16) {
            return 2;
        } else if (stackSize <= 32) {
            return 3;
        } else {
            return stackSize <= 48 ? 4 : 5;
        }
    }

    public static void renderDisplayItem(float pPartialTicks, Level level, PoseStack poseStack, MultiBufferSource pBuffer, int light, ItemStack stack, ItemRenderer itemRenderer, float prevRotation, float rotation, RandomSource random) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.4F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(MathHelper.lerpAngleDegrees(pPartialTicks, prevRotation, rotation)));
        renderStack(itemRenderer, poseStack, pBuffer, light, stack, random, level);
        poseStack.popPose();
    }

    public static void renderStack(ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource pBuffer, int light, ItemStack stack, RandomSource random, Level world) {
        BakedModel bakedModel = itemRenderer.getModel(stack, world, null, 0);
        renderStack(itemRenderer, poseStack, pBuffer, light, stack, bakedModel, bakedModel.isGui3d(), random);
    }

    public static void renderStack(ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource pBuffer, int light, ItemStack stack, BakedModel model, boolean depth, RandomSource random) {
        int i = getRenderedAmount(stack.getCount());
        float f = model.getTransforms().ground.scale.x();
        float g = model.getTransforms().ground.scale.y();
        float h = model.getTransforms().ground.scale.z();
        if (!depth) {
            float j = -0.0F * (i - 1) * 0.5F * f;
            float k = -0.0F * (i - 1) * 0.5F * g;
            float l = -0.09375F * (i - 1) * 0.5F * h;
            poseStack.translate(j, k, l);
        }

        for (int m = 0; m < i; m++) {
            poseStack.pushPose();
            if (m > 0) {
                if (depth) {
                    float k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float n = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(k, l, n);
                } else {
                    float k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(k, l, 0.0F);
                }
            }

            itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, pBuffer, light, OverlayTexture.NO_OVERLAY, model);
            poseStack.popPose();
            if (!depth) {
                poseStack.translate(0.0F * f, 0.0F * g, 0.09375F * h);
            }
        }
    }

}
