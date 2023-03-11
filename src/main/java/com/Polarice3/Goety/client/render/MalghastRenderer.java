package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ModGhastModel;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MalghastRenderer extends MobRenderer<Malghast, ModGhastModel<Malghast>> {
    private static final ResourceLocation GHAST_LOCATION = Goety.location("textures/entity/malghast/malghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = Goety.location("textures/entity/malghast/malghast_shooting.png");
    private static final ResourceLocation GHAST_SCREAMING_LOCATION = Goety.location("textures/entity/malghast/malghast_scream.png");

    public MalghastRenderer(EntityRendererProvider.Context p_i50961_1_) {
        super(p_i50961_1_, new ModGhastModel<>(p_i50961_1_.bakeLayer(ModModelLayer.MALGHAST)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Malghast pEntity) {
        if (pEntity.isCharging()){
            return GHAST_SHOOTING_LOCATION;
        } else if (pEntity.hurtTime > 0 || pEntity.deathTime > 0){
            return GHAST_SCREAMING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    protected void scale(Malghast pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = pLivingEntity.getSwelling(pPartialTickTime);
        float f1 = 2.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f) * f1;
        pMatrixStack.scale(f2, f2, f2);
    }
}
