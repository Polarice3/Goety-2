package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ReprobateCarryModel;
import com.Polarice3.Goety.common.entities.projectiles.ReprobateCarry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;

public class ReprobateCarryRenderer<T extends Projectile> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/reprobate.png");
    public ReprobateCarryModel<T> model;
    public ReprobateCarryModel<T> potionCase;
    public ReprobateCarryModel<T> barrel;

    public ReprobateCarryRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.potionCase = new ReprobateCarryModel<>(p_174008_.bakeLayer(ModModelLayer.POTION_CASE));
        this.barrel = new ReprobateCarryModel<>(p_174008_.bakeLayer(ModModelLayer.POTION_BARREL));
        this.shadowRadius = 0.5F;
    }

    public void render(T pEntity, float pYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity instanceof ReprobateCarry carry) {
            if (carry.isBarrel()) {
                this.model = this.barrel;
            } else {
                this.model = this.potionCase;
            }
        } else {
            this.model = this.potionCase;
        }
        pPoseStack.pushPose();
        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0, -0.35F, 0.0F);
        this.scale(pEntity, pPoseStack, 1.0F);
        float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
        float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(pEntity)));
        this.model.setupAnim(f, f1, this.model == this.barrel);
        this.model.renderToBuffer(pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        pPoseStack.popPose();

        super.render(pEntity, pYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }

    protected void scale(T p_116294_, PoseStack p_116295_, float p_116296_) {
        p_116295_.scale(p_116296_, p_116296_, p_116296_);
    }

}
