package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VoidShockModel;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.VoidShock;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class VoidShockRenderer extends EntityRenderer<VoidShock> {
    private static final ResourceLocation OUTER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_outer.png");
    private static final ResourceLocation INNER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_inner.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/solid_trail.png");
    private final VoidShockModel<VoidShock> model;
    private final RandomSource random = RandomSource.create();

    public VoidShockRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new VoidShockModel<>(renderManagerIn.bakeLayer(ModModelLayer.VOID_SHOCK));
    }

    @Override
    public void render(VoidShock entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        VertexConsumer VertexConsumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, f, f1);
        poseStack.translate(0.0F, 0.3F, 0.0F);
        this.model.renderToBuffer(poseStack, VertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        VertexConsumer VertexConsumer2 = buffer.getBuffer(RenderType.eyes(OUTER_TEXTURES));
        this.model.renderToBuffer(poseStack, VertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.4F);
        poseStack.popPose();
        if (entity.hasTrail()) {
            float randomF = 0.04F;
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
            float r = colorUtil.red() + this.random.nextFloat() * randomF;
            float g = colorUtil.green() + this.random.nextFloat() * randomF;
            float b = colorUtil.blue() + this.random.nextFloat() * randomF;
            poseStack.pushPose();
            float x = (float) (Mth.lerp(partialTicks, entity.xOld, entity.getX()));
            float y = (float) (Mth.lerp(partialTicks, entity.yOld, entity.getY()));
            float z = (float) (Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
            entity.trail.prepareRender(new Vec3(x, y + entity.getBbHeight() / 2, z), partialTicks);
            poseStack.translate(-x, -y, -z);
            TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, r * 1.2F, g * 1.2F, b * 1.2F, 1, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }
    }

    protected int getBlockLightLevel(VoidShock entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VoidShock entity) {
        return INNER_TEXTURES;
    }

}
