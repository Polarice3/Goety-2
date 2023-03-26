package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.common.entities.projectiles.GlowLight;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;

public class SoulBulletRenderer extends EntityRenderer<Projectile> {

    public SoulBulletRenderer(EntityRendererProvider.Context p_i47202_1_) {
        super(p_i47202_1_);
    }

    public void render(Projectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    protected int getBlockLightLevel(Projectile p_116491_, BlockPos p_116492_) {
        if (p_116491_ instanceof GlowLight) {
            return 15;
        }
        return 10;
    }

    public ResourceLocation getTextureLocation(Projectile pEntity) {
        return null;
    }
}
