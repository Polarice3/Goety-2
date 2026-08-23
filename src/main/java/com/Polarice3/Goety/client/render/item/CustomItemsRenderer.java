package com.Polarice3.Goety.client.render.item;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.client.render.model.NamelessStaffModel;
import com.Polarice3.Goety.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CustomItemsRenderer extends BlockEntityWithoutLevelRenderer {
    public static int ticksExisted = 0;
    private final NamelessStaffModel<?> staffModel;
    private static final ResourceLocation NAMELESS_STAFF_TEXTURE = Goety.location("textures/item/nameless_staff_model.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_TEXTURE = Goety.location("textures/item/nameless_staff_orb.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_MID_TEXTURE = Goety.location("textures/item/nameless_staff_orb_mid.png");
    private static final ResourceLocation NAMELESS_STAFF_ORB_CENTER_TEXTURE = Goety.location("textures/item/nameless_staff_orb_center.png");

    public CustomItemsRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public CustomItemsRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
        this.staffModel = new NamelessStaffModel<>(p_172551_.bakeLayer(ModModelLayer.NAMELESS_STAFF));
    }

    public static void incrementTick() {
        ++ticksExisted;
    }

    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float partialTick = Minecraft.getInstance().getPartialTick();

        int tick;
        Player player = Minecraft.getInstance().player;
        if (player != null && !Minecraft.getInstance().isPaused()) {
            tick = player.tickCount;
        } else {
            tick = ticksExisted;
        }

        if (itemStackIn.getItem() == ModItems.NAMELESS_STAFF.get()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(NAMELESS_STAFF_TEXTURE), false, itemStackIn.hasFoil());
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderType.eyes(NAMELESS_STAFF_ORB_TEXTURE));
            this.staffModel.animate(tick + partialTick);
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer2, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer3 = bufferIn.getBuffer(RenderType.eyes(NAMELESS_STAFF_ORB_MID_TEXTURE));
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer3, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer4 = bufferIn.getBuffer(ModRenderType.orbCenter(NAMELESS_STAFF_ORB_CENTER_TEXTURE));
            this.staffModel.renderToBuffer(matrixStackIn, vertexconsumer4, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
    }
}
