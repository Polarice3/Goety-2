package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.MandalaBlock;
import com.Polarice3.Goety.common.blocks.entities.MandalaBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MandalaRenderer implements BlockEntityRenderer<MandalaBlockEntity> {
    private static final ResourceLocation TEXTURE = Goety.location("block/mandala");
    private static final float GAP = 0.002f;

    public MandalaRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(MandalaBlockEntity tile, float partialTick, PoseStack pose,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!tile.isAnchor()) {
            return;
        }

        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(MandalaBlock.FACING);

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(TEXTURE);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutoutMipped());

        pose.pushPose();

        switch (facing) {
            case UP -> {
                pose.translate(0, 1.0f / 16f + GAP, 0);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        1, 0, 0,
                        -3, 0, 0,
                        -3, 0, 4,
                        1, 0, 4,
                        0, 1, 0
                );
            }
            case DOWN -> {
                pose.translate(0, (1.0f - (1.0f / 16f)) + GAP, 0);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        1, 0, 0,
                        -3, 0, 0,
                        -3, 0, 4,
                        1, 0, 4,
                        0, -1, 0
                );
            }
            case NORTH -> {
                pose.translate(1.0f, 0, 1.0f - GAP);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        0, 0, 0,
                        -4, 0, 0,
                        -4, 4, 0,
                        0, 4, 0,
                        0, 0, -1
                );
            }
            case SOUTH -> {
                pose.translate(0, 0, GAP);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        0, 0, 0,
                        4, 0, 0,
                        4, 4, 0,
                        0, 4, 0,
                        0, 0, 1
                );
            }
            case EAST -> {
                pose.translate(GAP, 0, 1.0f);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        0, 0, 0,
                        0, 0, -4,
                        0, 4, -4,
                        0, 4, 0,
                        1, 0, 0
                );
            }
            case WEST -> {
                pose.translate(1.0f - GAP, 0, 0);
                drawQuad(pose, consumer, sprite, packedLight, packedOverlay,
                        0, 0, 0,
                        0, 0, 4,
                        0, 4, 4,
                        0, 4, 0,
                        -1, 0, 0
                );
            }
        }

        pose.popPose();
    }

    private void drawQuad(PoseStack pose, VertexConsumer consumer, TextureAtlasSprite sprite, int light, int overlay, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float nx, float ny, float nz) {

        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();

        float uA = sprite.getU0();
        float uB = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        vertex(consumer, m, n, x0, y0, z0, uA, v1, nx, ny, nz, light, overlay);
        vertex(consumer, m, n, x1, y1, z1, uB, v1, nx, ny, nz, light, overlay);
        vertex(consumer, m, n, x2, y2, z2, uB, v0, nx, ny, nz, light, overlay);
        vertex(consumer, m, n, x3, y3, z3, uA, v0, nx, ny, nz, light, overlay);

        vertex(consumer, m, n, x3, y3, z3, uA, v0, -nx, -ny, -nz, light, overlay);
        vertex(consumer, m, n, x2, y2, z2, uB, v0, -nx, -ny, -nz, light, overlay);
        vertex(consumer, m, n, x1, y1, z1, uB, v1, -nx, -ny, -nz, light, overlay);
        vertex(consumer, m, n, x0, y0, z0, uA, v1, -nx, -ny, -nz, light, overlay);
    }

    private void vertex(VertexConsumer consumer, Matrix4f m, Matrix3f n, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light, int overlay) {
        consumer.vertex(m, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(n, nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(MandalaBlockEntity tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
