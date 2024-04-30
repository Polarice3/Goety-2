package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DarkAltarRenderer implements BlockEntityRenderer<DarkAltarBlockEntity> {
    public DarkAltarRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    public void render(DarkAltarBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        pBlockEntity.itemStackHandler.ifPresent(handler -> {
            ItemStack stack = handler.getStackInSlot(0);
            Minecraft minecraft = Minecraft.getInstance();
            if (!stack.isEmpty()) {
                pMatrixStack.pushPose();
                pMatrixStack.translate(0.5F, 0.85F, 0.5F);
                pMatrixStack.scale(1.0F, 1.0F, 1.0F);
                assert minecraft.level != null;
                pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
                minecraft.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, pCombinedLight, pCombinedOverlay, pMatrixStack, pBuffer, 0);
                pMatrixStack.popPose();
            }
        });
        if (pBlockEntity.isShowArea()) {
            pMatrixStack.pushPose();
            pMatrixStack.translate(-0.0005D, -0.0005D, -0.0005D);
            pMatrixStack.scale(0.999F, 0.999F, 0.999F);
            int range = RitualRequirements.RANGE;
            LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.lines()), new AABB(-range, -range, -range, range + 1, range + 1, range + 1), 0.25F, 0.25F, 1.0F, 1.0F);
            pMatrixStack.popPose();
        }
    }

    public boolean shouldRenderOffScreen(DarkAltarBlockEntity p_112138_) {
        return true;
    }

    public boolean shouldRender(DarkAltarBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
    }
}
