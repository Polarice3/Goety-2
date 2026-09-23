package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HeavyArrowRenderer extends ArrowRenderer<HeavyArrow> {
    public static final ResourceLocation NORMAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation TIPPED_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/solid_trail.png");

    public HeavyArrowRenderer(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    @Override
    public void render(HeavyArrow entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.hasTrail()) {
            float x = (float) (Mth.lerp(partialTicks, entity.xOld, entity.getX()));
            float y = (float) (Mth.lerp(partialTicks, entity.yOld, entity.getY()));
            float z = (float) (Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
            poseStack.pushPose();
            entity.trail.prepareRender(new Vec3(x, y, z), partialTicks);
            poseStack.translate(-x, -y, -z);
            TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, false, 1.0F, 1.0F, 1.0F, 0.5F, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HeavyArrow p_116140_) {
        return p_116140_.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
    }
}
