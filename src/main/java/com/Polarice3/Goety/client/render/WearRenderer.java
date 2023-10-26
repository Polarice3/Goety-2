package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.client.render.model.DarkRobeModel;
import com.Polarice3.Goety.client.render.model.GloveModel;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ElytraItem;
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

    public boolean hasCape(AbstractClientPlayer p_116618_){
        return p_116618_.isCapeLoaded() && !p_116618_.isInvisible() && p_116618_.isModelPartShown(PlayerModelPart.CAPE) && p_116618_.getCloakTextureLocation() != null;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity livingEntity = slotContext.entity();
        HumanoidModel<LivingEntity> model = getModel();

        model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followBodyRotations(slotContext.entity(), model);
        render(matrixStack, renderTypeBuffer, light);

        if (stack.getItem() == ModItems.GRAND_ROBE.get() || stack.getItem() == ModItems.FROST_ROBE.get() || stack.getItem() == ModItems.WIND_ROBE.get()){
            if (livingEntity instanceof AbstractClientPlayer p_116618_) {
                if (MainConfig.RobeCape.get()
                        && (CuriosFinder.hasCurio(p_116618_, ModItems.GRAND_ROBE.get()) || CuriosFinder.hasCurio(p_116618_, ModItems.FROST_ROBE.get()) || CuriosFinder.hasCurio(p_116618_, ModItems.WIND_ROBE.get()))
                        && !CuriosFinder.hasUndeadCape(p_116618_)
                        && !hasCape(p_116618_)
                        && !p_116618_.isInvisible()) {
                    ItemStack itemstack = p_116618_.getItemBySlot(EquipmentSlot.CHEST);
                    if (!(itemstack.getItem() instanceof ElytraItem)) {
                        matrixStack.pushPose();
                        matrixStack.translate(0.0D, -0.1D, 0.25D);
                        if (!itemstack.isEmpty()){
                            matrixStack.translate(0.0D, 0.0D, 0.025D);
                        }
                        double d0 = Mth.lerp((double) partialTicks, p_116618_.xCloakO, p_116618_.xCloak) - Mth.lerp((double) partialTicks, p_116618_.xo, p_116618_.getX());
                        double d1 = Mth.lerp((double) partialTicks, p_116618_.yCloakO, p_116618_.yCloak) - Mth.lerp((double) partialTicks, p_116618_.yo, p_116618_.getY());
                        double d2 = Mth.lerp((double) partialTicks, p_116618_.zCloakO, p_116618_.zCloak) - Mth.lerp((double) partialTicks, p_116618_.zo, p_116618_.getZ());
                        float f = p_116618_.yBodyRotO + (p_116618_.yBodyRot - p_116618_.yBodyRotO);
                        double d3 = (double) Mth.sin(f * ((float) Math.PI / 180F));
                        double d4 = (double) (-Mth.cos(f * ((float) Math.PI / 180F)));
                        float f1 = (float) d1 * 10.0F;
                        f1 = Mth.clamp(f1, -6.0F, 32.0F);
                        float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                        f2 = Mth.clamp(f2, 0.0F, 150.0F);
                        float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
                        f3 = Mth.clamp(f3, -20.0F, 20.0F);
                        if (f2 < 0.0F) {
                            f2 = 0.0F;
                        }
                        if (f2 > 80.0F){
                            matrixStack.translate(0.0D, 0.0D, -0.1D);
                        }

                        float f4 = Mth.lerp(partialTicks, p_116618_.oBob, p_116618_.bob);
                        f1 += Mth.sin(Mth.lerp(partialTicks, p_116618_.walkDistO, p_116618_.walkDist) * 6.0F) * 32.0F * f4;
                        if (p_116618_.isCrouching()) {
                            f1 += 25.0F;
                            matrixStack.translate(0.0D, 0.1D, 0.0D);
                        }

                        matrixStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                        matrixStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
                        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
                        VertexConsumer vertexconsumer = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(getTexture()));
                        new DarkRobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayer.DARK_ROBE)).renderCape(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
                        matrixStack.popPose();
                    }
                }
            }
        }
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
