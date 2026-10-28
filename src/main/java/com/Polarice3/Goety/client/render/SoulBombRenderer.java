package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ScatterMineModel;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.SoulBomb;
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

public class SoulBombRenderer extends EntityRenderer<SoulBomb> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/soul_bomb.png");
    private static final ResourceLocation NECRO_TEXTURE = Goety.location("textures/entity/projectiles/soul_bomb_necro.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/trail.png");
    private static final ColorUtil TRAIL_COLOR = new ColorUtil(0x7cf2f5);
    private static final ColorUtil NECRO_COLOR = new ColorUtil(0x9df530);
    private final ScatterMineModel<SoulBomb> model;

    public SoulBombRenderer(EntityRendererProvider.Context p_174426_) {
        super(p_174426_);
        this.model = new ScatterMineModel<>(p_174426_.bakeLayer(ModModelLayer.SCATTER_MINE));
    }

    public void render(SoulBomb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, -1.25F, 0.0F);
        poseStack.scale(1.0F, 1.0F, 1.0F);
        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
        this.model.setupAnim(f, f1);
        this.model.renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
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
        TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, false, r, g, b, 1, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected int getBlockLightLevel(SoulBomb entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(SoulBomb p_114482_) {
        if (p_114482_.isNecro()) {
            return NECRO_TEXTURE;
        }
        return TEXTURE;
    }
}
