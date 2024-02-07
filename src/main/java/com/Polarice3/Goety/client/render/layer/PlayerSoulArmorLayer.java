package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.common.effects.GoetyEffects;
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

public class PlayerSoulArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation SOUL_ARMOR = new ResourceLocation(Goety.MOD_ID, "textures/entity/soul_armor.png");
    private final PlayerModel<T> model;

    public PlayerSoulArmorLayer(RenderLayerParent<T, M> p_i50926_1_, EntityModelSet p_174555_) {
        super(p_i50926_1_);
        this.model = new PlayerModel<>(p_174555_.bakeLayer(ModModelLayer.SOUL_ARMOR), false);
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.hasEffect(GoetyEffects.SOUL_ARMOR.get()) && pLivingEntity.hurtTime > 0) {
            float f = (float) pLivingEntity.tickCount + pPartialTicks;
            PlayerModel<T> entitymodel = this.model;
            entitymodel.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
            new ClientEvents().followBodyRotations(pLivingEntity, entitymodel);
            VertexConsumer ivertexbuilder = pBuffer.getBuffer(RenderType.energySwirl(SOUL_ARMOR, this.xOffset(f), f * 0.01F));
            entitymodel.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            entitymodel.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }

    protected float xOffset(float p_225634_1_) {
        return p_225634_1_ * 0.01F;
    }
}
