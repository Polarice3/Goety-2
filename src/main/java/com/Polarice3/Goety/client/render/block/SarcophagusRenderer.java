package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.animation.SarcophagusAnimations;
import com.Polarice3.Goety.client.render.model.SarcophagusModel;
import com.Polarice3.Goety.common.blocks.SarcophagusBlock;
import com.Polarice3.Goety.common.blocks.entities.SarcophagusBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SculpturedStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SarcophagusRenderer implements BlockEntityRenderer<SarcophagusBlockEntity> {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/sarcophagus/shade.png");
    public static final ResourceLocation CUSHION = Goety.location("textures/entity/sarcophagus/cushion.png");
    private final SarcophagusModel<?> model;

    public SarcophagusRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new SarcophagusModel<>(ctx.bakeLayer(ModModelLayer.SARCOPHAGUS));
    }

    @Override
    public void render(SarcophagusBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof SarcophagusBlock)) {
            return;
        }

        Direction facing = blockState.getValue(SarcophagusBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        switch (facing) {
            case SOUTH:
            default:
                break;
            case WEST:
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
            case EAST:
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                break;
            case NORTH:
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
        if (blockEntity.getBlockState().hasProperty(SarcophagusBlock.CUSHIONED)) {
            this.model.cushion.visible = blockEntity.getBlockState().getValue(SarcophagusBlock.CUSHIONED);
        }
        this.model.root().getAllParts().forEach(ModelPart::resetPose);
        float f7 = this.getBob(blockEntity.getLevel() != null ? blockEntity.getLevel() : null, partialTick);
        this.model.animate(blockEntity.openingState, SarcophagusAnimations.OPENING, f7);
        this.model.animate(blockEntity.closingState, SarcophagusAnimations.CLOSING, f7);
        this.model.animate(blockEntity.closedState, SarcophagusAnimations.CLOSED, f7);
        VertexConsumer vc = buffer.getBuffer(this.model.renderType(this.getTextureLocation(blockEntity)));
        this.model.renderToBuffer(poseStack, vc, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        if (blockEntity.getColor() != null) {
            VertexConsumer cushion = buffer.getBuffer(RenderType.entityCutoutNoCull(CUSHION));
            float[] color = blockEntity.getColor().getTextureDiffuseColors();
            this.model.renderToBuffer(poseStack, cushion, light, overlay, color[0], color[1], color[2], 1.0F);
        }
        poseStack.popPose();
    }

    protected float getBob(@Nullable Level level, float partialTick) {
        if (level == null) {
            return partialTick;
        }
        return level.getGameTime() + partialTick;
    }

    public ResourceLocation getTextureLocation(SarcophagusBlockEntity blockEntity) {
        if (blockEntity != null) {
            if (blockEntity.getBlockState().getBlock() instanceof SarcophagusBlock sarcophagusBlock) {
                ResourceLocation location = sarcophagusBlock.getTexture();
                if (location != null) {
                    return location;
                }
            }
        }
        return TEXTURE;
    }

    @Override
    public boolean shouldRenderOffScreen(SarcophagusBlockEntity blockEntity) {
        return true;
    }

    public boolean shouldRender(SculpturedStatueBlockEntity p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
    }
}
