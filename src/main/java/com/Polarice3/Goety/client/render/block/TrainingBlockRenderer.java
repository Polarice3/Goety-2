package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TrainingBlockRenderer<T extends TrainingBlockEntity> implements BlockEntityRenderer<T> {
    public TrainingBlockRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    @Override
    public void render(T pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        if (pBlockEntity.isShowArea()) {
            pMatrixStack.pushPose();
            pMatrixStack.translate(-0.0005D, -0.0005D, -0.0005D);
            pMatrixStack.scale(0.999F, 0.999F, 0.999F);
            int range = TrainingBlockEntity.RANGE;
            LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.lines()), new AABB(-range, -range, -range, range + 1, range + 1, range + 1), 0.25F, 0.25F, 1.0F, 1.0F);
            pMatrixStack.popPose();
        }
    }

    public boolean shouldRenderOffScreen(T p_112138_) {
        return true;
    }

    public boolean shouldRender(T p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
    }
}
