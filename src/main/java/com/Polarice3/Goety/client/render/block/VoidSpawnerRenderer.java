package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.common.blocks.entities.VoidSpawnerBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawner;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawnerData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class VoidSpawnerRenderer implements BlockEntityRenderer<VoidSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public VoidSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    public void render(VoidSpawnerBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5F, 0.0F, 0.5F);
        Level level = pBlockEntity.getLevel();
        if (level != null) {
            VoidSpawner spawner = pBlockEntity.getVoidSpawner();
            VoidSpawnerData data = spawner.getData();
            Entity entity = data.getOrCreateDisplayEntity(spawner, level, spawner.getState());
            if (entity != null) {
                float f = 0.53125F;
                float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
                if ((double)f1 > 1.0D) {
                    f /= f1;
                }

                pMatrixStack.translate(0.0F, 0.4F, 0.0F);
                pMatrixStack.mulPose(Axis.YP.rotationDegrees((float) Mth.lerp((double)pPartialTicks, data.getOSpin(), data.getSpin()) * 10.0F));
                pMatrixStack.translate(0.0F, -0.2F, 0.0F);
                pMatrixStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
                pMatrixStack.scale(f, f, f);
                this.entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, pPartialTicks, pMatrixStack, pBuffer, pCombinedLight);
            }

        }
        pMatrixStack.popPose();
    }
}
