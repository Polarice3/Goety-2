package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.utils.MobUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class PlayerSoulShieldLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
    private final PlayerModel<T> model;

    public PlayerSoulShieldLayer(RenderLayerParent<T, M> p_i50926_1_, EntityModelSet p_174555_) {
        super(p_i50926_1_);
        this.model = new PlayerModel<>(p_174555_.bakeLayer(ModModelLayer.SOUL_SHIELD), false);
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (MobUtil.starAmuletActive(pLivingEntity)) {
            float f = (float) pLivingEntity.tickCount + pPartialTicks;
            PlayerModel<T> entitymodel = this.model;
            entitymodel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
            new ClientEvents().followBodyRotations(pLivingEntity, entitymodel);
            VertexConsumer ivertexbuilder = pBuffer.getBuffer(RenderType.energySwirl(POWER_LOCATION, this.xOffset(f), f * 0.01F));
            entitymodel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            entitymodel.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }

    protected float xOffset(float p_225634_1_) {
        return p_225634_1_ * 0.01F;
    }
}
