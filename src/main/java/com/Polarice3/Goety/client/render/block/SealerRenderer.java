package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ApparitionDoorBlock;
import com.Polarice3.Goety.common.blocks.CryptChestBlock;
import com.Polarice3.Goety.common.blocks.SealerBlock;
import com.Polarice3.Goety.common.blocks.entities.SealerBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SealerHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/SealerBlock.java
public class SealerRenderer implements BlockEntityRenderer<SealerBlockEntity> {
    private static final ResourceLocation SEAL = Goety.location("textures/block/sealer_seal.png");
    private static final float INFLATE = 0.005F;
    private static final float PULSE_PERIOD = 50.0F;

    public SealerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SealerBlockEntity sealer, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Level level = sealer.getLevel();
        if (level == null) {
            return;
        }
        ItemStack waystone = sealer.getWaystone();
        if (!waystone.isEmpty()) {
            BlockState state = sealer.getBlockState();
            boolean lit = state.hasProperty(SealerBlock.LIT) && state.getValue(SealerBlock.LIT);
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees((lit ? 12 : 3) * (level.getGameTime() % 360 + partialTicks)));
            Minecraft.getInstance().getItemRenderer().renderStatic(waystone, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, level, 0);
            poseStack.popPose();
        }
        BlockPos target = sealer.getSealedPos();
        if (target == null || !sealer.isSealing()) {
            return;
        }
        float pulse = 0.5F + 0.5F * Mth.sin((level.getGameTime() + partialTicks) * ((float) Math.PI * 2.0F / PULSE_PERIOD));
        int alpha = (int) (90.0F + 165.0F * pulse);
        BlockState targetState = level.getBlockState(target);
        if (targetState.getBlock() instanceof ApparitionDoorBlock) {
            for (BlockPos door : SealerHooks.connectedDoors(level, target)) {
                BlockState doorState = level.getBlockState(door);
                if (!doorState.getValue(ApparitionDoorBlock.VANISHED)) {
                    this.renderSeal(poseStack, buffer, level, door, doorState, door.subtract(sealer.getBlockPos()), alpha);
                }
            }
        } else if (!(targetState.getBlock() instanceof CryptChestBlock)) {
            this.renderSeal(poseStack, buffer, level, target, targetState, target.subtract(sealer.getBlockPos()), alpha);
        }
    }

    private void renderSeal(PoseStack poseStack, MultiBufferSource buffer, Level level, BlockPos pos, BlockState state, BlockPos offset, int alpha) {
        VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
        AABB box = shape.isEmpty() ? new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D) : shape.bounds();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(SEAL));
        poseStack.pushPose();
        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        float x0 = (float) box.minX - INFLATE;
        float y0 = (float) box.minY - INFLATE;
        float z0 = (float) box.minZ - INFLATE;
        float x1 = (float) box.maxX + INFLATE;
        float y1 = (float) box.maxY + INFLATE;
        float z1 = (float) box.maxZ + INFLATE;
        Matrix4f pose = poseStack.last().pose();
        int bright = LightTexture.FULL_BRIGHT;
        quad(consumer, poseStack, pose, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, 0, 1, 0, bright, alpha);
        quad(consumer, poseStack, pose, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, 0, -1, 0, bright, alpha);
        quad(consumer, poseStack, pose, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0, 0, 0, -1, bright, alpha);
        quad(consumer, poseStack, pose, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1, 0, 0, 1, bright, alpha);
        quad(consumer, poseStack, pose, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1, -1, 0, 0, bright, alpha);
        quad(consumer, poseStack, pose, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0, 1, 0, 0, bright, alpha);
        poseStack.popPose();
    }

    private static void quad(VertexConsumer consumer, PoseStack poseStack, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float nx, float ny, float nz, int light, int alpha) {
        vertex(consumer, poseStack, pose, x1, y1, z1, 0.0F, 0.0F, nx, ny, nz, light, alpha);
        vertex(consumer, poseStack, pose, x2, y2, z2, 0.0F, 1.0F, nx, ny, nz, light, alpha);
        vertex(consumer, poseStack, pose, x3, y3, z3, 1.0F, 1.0F, nx, ny, nz, light, alpha);
        vertex(consumer, poseStack, pose, x4, y4, z4, 1.0F, 0.0F, nx, ny, nz, light, alpha);
    }

    private static void vertex(VertexConsumer consumer, PoseStack poseStack, Matrix4f pose, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light, int alpha) {
        consumer.vertex(pose, x, y, z)
                .color(255, 255, 255, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(poseStack.last().normal(), nx, ny, nz)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(SealerBlockEntity sealer) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
