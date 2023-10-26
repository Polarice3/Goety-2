package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlastFungusModel;
import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BlastFungusRenderer extends EntityRenderer<BlastFungus> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/blast_fungus.png");
    private final BlastFungusModel<BlastFungus> model;

    public BlastFungusRenderer(EntityRendererProvider.Context p_174426_) {
        super(p_174426_);
        this.model = new BlastFungusModel<>(p_174426_.bakeLayer(ModModelLayer.BLAST_FUNGUS));
        this.shadowRadius = 0.5F;
    }

    public void render(BlastFungus p_116177_, float p_116178_, float p_116179_, PoseStack p_116180_, MultiBufferSource p_116181_, int p_116182_) {
        p_116180_.pushPose();
        p_116180_.translate(0.0F, -1.25F, -0.25F);
        float f = Mth.rotLerp(p_116179_, p_116177_.yRotO, p_116177_.getYRot());
        float f1 = Mth.lerp(p_116179_, p_116177_.xRotO, p_116177_.getXRot());
        VertexConsumer vertexconsumer = p_116181_.getBuffer(this.model.renderType(this.getTextureLocation(p_116177_)));
        this.model.setupAnim(f, f1);
        this.model.renderToBuffer(p_116180_, vertexconsumer, p_116182_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        p_116180_.popPose();
        super.render(p_116177_, p_116178_, p_116179_, p_116180_, p_116181_, p_116182_);
    }

    @Override
    public ResourceLocation getTextureLocation(BlastFungus p_114482_) {
        return TEXTURE;
    }
}
