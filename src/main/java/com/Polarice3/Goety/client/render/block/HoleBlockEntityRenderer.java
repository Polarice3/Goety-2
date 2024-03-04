package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.common.blocks.entities.HoleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class HoleBlockEntityRenderer<T extends HoleBlockEntity> implements BlockEntityRenderer<T> {

    public HoleBlockEntityRenderer(BlockEntityRendererProvider.Context p_173529_) {
    }

    @Override
    public void render(T entity, float p_112651_, PoseStack poseStack, MultiBufferSource bufferSource, int p_112654_, int p_112655_) {
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(entity, matrix4f, bufferSource.getBuffer(this.renderType()));
    }

    private void renderCube(T entity, Matrix4f matrix4f, VertexConsumer consumer) {
        this.renderFace(entity, matrix4f, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 0.9995F, 0.9995F, 0.9995F, 0.9995F, Direction.SOUTH);
        this.renderFace(entity, matrix4f, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0005F, 0.0005F, 0.0005F, 0.0005F, Direction.NORTH);
        this.renderFace(entity, matrix4f, consumer, 0.9995F, 0.9995F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(entity, matrix4f, consumer, 0.0005F, 0.0005F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(entity, matrix4f, consumer, 0.0F, 1.0F, 0.0005F, 0.0005F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(entity, matrix4f, consumer, 0.0F, 1.0F, 0.9995F, 0.9995F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(T entity, Matrix4f matrix4f, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction direction) {
        if (entity.shouldRenderFace(direction)) {
            consumer.vertex(matrix4f, x1, y1, z1).endVertex();
            consumer.vertex(matrix4f, x2, y1, z2).endVertex();
            consumer.vertex(matrix4f, x2, y2, z3).endVertex();
            consumer.vertex(matrix4f, x1, y2, z4).endVertex();
        }
    }

    protected RenderType renderType() {
        return ModRenderType.hole();
    }

    public int getViewDistance() {
        return 256;
    }
}
