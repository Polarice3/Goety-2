package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VoidShockModel;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.SoulStar;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SoulStarRenderer extends EntityRenderer<SoulStar> {
    private static final ResourceLocation OUTER_TEXTURES = Goety.location("textures/entity/projectiles/soul_star_outer.png");
    private static final ResourceLocation NECRO_TEXTURES = Goety.location("textures/entity/projectiles/soul_star_necro_outer.png");
    private static final ResourceLocation INNER_TEXTURES = Goety.location("textures/entity/projectiles/soul_star_inner.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/solid_trail.png");
    private static final ColorUtil TRAIL_COLOR = new ColorUtil(0x7cf2f5);
    private static final ColorUtil NECRO_COLOR = new ColorUtil(0x9df530);
    private final VoidShockModel<SoulStar> model;

    public SoulStarRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new VoidShockModel<>(renderManagerIn.bakeLayer(ModModelLayer.VOID_SHOCK));
    }

    @Override
    public void render(SoulStar entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        VertexConsumer VertexConsumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, f, f1);
        poseStack.translate(0.0F, 0.3F * 1.6F, 0.0F);
        poseStack.scale(1.6F, 1.6F, 1.6F);
        this.model.renderToBuffer(poseStack, VertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        ResourceLocation location = OUTER_TEXTURES;
        if (entity.isNecro()) {
            location = NECRO_TEXTURES;
        }
        VertexConsumer VertexConsumer2 = buffer.getBuffer(RenderType.eyes(location));
        this.model.renderToBuffer(poseStack, VertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.4F);
        poseStack.popPose();
        ColorUtil colorUtil = TRAIL_COLOR;
        if (entity.isNecro()) {
            colorUtil = NECRO_COLOR;
        }
        float r = colorUtil.red();
        float g = colorUtil.green();
        float b = colorUtil.blue();
        poseStack.pushPose();
        float x = (float) (Mth.lerp(partialTicks, entity.xOld, entity.getX()));
        float y = (float) (Mth.lerp(partialTicks, entity.yOld, entity.getY()));
        float z = (float) (Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
        entity.trail.prepareRender(new Vec3(x, y + entity.getBbHeight() / 2, z), partialTicks);
        poseStack.translate(-x, -y, -z);
        TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, r, g, b, 1, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    protected int getBlockLightLevel(SoulStar entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(SoulStar entity) {
        return INNER_TEXTURES;
    }

}
