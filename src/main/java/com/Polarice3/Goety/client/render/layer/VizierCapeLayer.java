package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.model.VizierModel;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.utils.HolidayUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class VizierCapeLayer extends RenderLayer<Vizier, VizierModel> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/vizier.png");
    protected static final ResourceLocation CHRISTMAS = Goety.location("textures/entity/illagers/vizier_christmas.png");

    public VizierCapeLayer(RenderLayerParent<Vizier, VizierModel> p_117135_) {
        super(p_117135_);
    }

    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, Vizier pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
            if (itemstack.isEmpty()) {
                pMatrixStack.pushPose();
                pMatrixStack.translate(0.0D, 0.0D, 0.425D);
                double d0 = Mth.lerp((double)pPartialTicks, pLivingEntity.xCloakO, pLivingEntity.xCloak) - Mth.lerp((double)pPartialTicks, pLivingEntity.xo, pLivingEntity.getX());
                double d1 = Mth.lerp((double)pPartialTicks, pLivingEntity.yCloakO, pLivingEntity.yCloak) - Mth.lerp((double)pPartialTicks, pLivingEntity.yo, pLivingEntity.getY());
                double d2 = Mth.lerp((double)pPartialTicks, pLivingEntity.zCloakO, pLivingEntity.zCloak) - Mth.lerp((double)pPartialTicks, pLivingEntity.zo, pLivingEntity.getZ());
                float f = pLivingEntity.yBodyRotO + (pLivingEntity.yBodyRot - pLivingEntity.yBodyRotO);
                double d3 = (double)Mth.sin(f * ((float)Math.PI / 180F));
                double d4 = (double)(-Mth.cos(f * ((float)Math.PI / 180F)));
                float f1 = (float)d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = Mth.lerp(pPartialTicks, pLivingEntity.oBob, pLivingEntity.bob);
                f1 = f1 + Mth.sin(Mth.lerp(pPartialTicks, pLivingEntity.walkDistO, pLivingEntity.walkDist) * 6.0F) * 32.0F * f4;
                if (pLivingEntity.isCrouching()) {
                    f1 += 25.0F;
                }

                pMatrixStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                pMatrixStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
                pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
                VertexConsumer ivertexbuilder = pBuffer.getBuffer(RenderType.entityCutoutNoCull(this.getCapeTexture()));
                this.getParentModel().renderCape(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY);
                pMatrixStack.popPose();
            }
        }
    }

    public ResourceLocation getCapeTexture() {
        return HolidayUtil.isChristmasMonth() && MobsConfig.HolidaySkins.get() ? CHRISTMAS : TEXTURE;
    }
}
