package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.BoundIllagerAnimatedModel;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BoundIceologerRenderer<T extends BoundIceologer> extends MobRenderer<T, BoundIllagerAnimatedModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/bound_illager/bound_iceologer.png");
    protected static final ResourceLocation CASTING = Goety.location("textures/entity/servants/bound_illager/bound_iceologer_casting.png");

    public BoundIceologerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoundIllagerAnimatedModel<>(renderManagerIn.bakeLayer(ModModelLayer.BOUND_ILLAGER_ANIMATED)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }

    }

    public ResourceLocation getTextureLocation(T p_114482_) {
        if (p_114482_.isCastingSpell()){
            return CASTING;
        }
        return TEXTURE;
    }
}
