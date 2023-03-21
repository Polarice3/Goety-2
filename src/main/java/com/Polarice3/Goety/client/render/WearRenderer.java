package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.GloveModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import javax.annotation.Nullable;

public record WearRenderer(ResourceLocation texture,
                           HumanoidModel<LivingEntity> model) implements ICurioRenderer {

    private ResourceLocation getTexture() {
        return texture;
    }

    private HumanoidModel<LivingEntity> getModel() {
        return model;
    }

    @Nullable
    public static WearRenderer getRenderer(ItemStack stack) {
        if (!stack.isEmpty()) {
            return (WearRenderer) CuriosRendererRegistry.getRenderer(stack.getItem()).orElse(null);
        }
        return null;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HumanoidModel<LivingEntity> model = getModel();

        model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followBodyRotations(slotContext.entity(), model);
        render(matrixStack, renderTypeBuffer, light);
    }

    private void render(PoseStack matrixStack, MultiBufferSource buffer, int light) {
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }

    public void renderFirstPersonArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side, boolean hasFoil) {
        if (!player.isSpectator()) {
            GloveModel model = (GloveModel) getModel();

            ModelPart arm = side == HumanoidArm.LEFT ? model.leftArm : model.rightArm;

            model.setAllVisible(false);
            arm.visible = true;

            model.crouching = false;
            model.attackTime = 0;
            model.swimAmount = 0;
            model.setupAnim(player, 0, 0, 0, 0, 0);
            arm.xRot = 0;

            renderFirstPersonArm(model, arm, matrixStack, buffer, light, hasFoil);
        }
    }

    private void renderFirstPersonArm(GloveModel model, ModelPart arm, PoseStack matrixStack, MultiBufferSource buffer, int light, boolean hasFoil) {
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, renderType, false, hasFoil);
        arm.render(matrixStack, builder, light, OverlayTexture.NO_OVERLAY);
    }
}
