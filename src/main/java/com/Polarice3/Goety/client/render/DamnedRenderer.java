package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.DamnedModel;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DamnedRenderer<T extends Damned> extends MobRenderer<T, DamnedModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/damned.png");
    protected static final ResourceLocation TEXTURE_2 = Goety.location("textures/entity/cultist/damned_2.png");
    protected static final ResourceLocation SCREAM = Goety.location("textures/entity/cultist/damned_scream.png");
    protected static final ResourceLocation SCREAM_2 = Goety.location("textures/entity/cultist/damned_scream_2.png");
    public final DamnedModel<T> normalModel;
    public final DamnedModel<T> humanModel;

    public DamnedRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new DamnedModel<>(renderManagerIn.bakeLayer(ModModelLayer.DAMNED)), 0.5F);
        this.normalModel = this.getModel();
        this.humanModel = new DamnedModel<>(renderManagerIn.bakeLayer(ModModelLayer.DAMNED_HUMAN));
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isHuman()){
            this.model = this.humanModel;
        } else {
            this.model = normalModel;
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    protected int getBlockLightLevel(T p_114496_, BlockPos p_114497_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(T p_114482_) {
        if (p_114482_.isCharging()){
            if (p_114482_.isHuman()){
                return SCREAM_2;
            } else {
                return SCREAM;
            }
        }
        if (p_114482_.isHuman()){
            return TEXTURE_2;
        } else {
            return TEXTURE;
        }
    }
}
