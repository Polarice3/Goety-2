package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VoidShockBombModel;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
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
import org.joml.Quaternionf;

public class VoidShockBombRenderer extends EntityRenderer<VoidShockBomb> {
    private static final ResourceLocation OUTER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_bomb_outer.png");
    private static final ResourceLocation INNER_TEXTURES = Goety.location("textures/entity/projectiles/void_shock_bomb_inner.png");
    private static final ResourceLocation TRAIL_TEXTURE = Goety.location("textures/entity/projectiles/trail.png");
    private final VoidShockBombModel<VoidShockBomb> model;
    private final RandomSource random = RandomSource.create();

    public VoidShockBombRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.model = new VoidShockBombModel<>(renderManagerIn.bakeLayer(ModModelLayer.VOID_SHOCK_BOMB));
    }

    @Override
    public void render(VoidShockBomb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.growTick <= 0) {
            poseStack.pushPose();
            poseStack.mulPose((new Quaternionf()).setAngleAxis(entityYaw * ((float) Math.PI / 180F), 0, -1.0F, 0));
            VertexConsumer VertexConsumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
            model.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, 0, 0);
            model.renderToBuffer(poseStack, VertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            VertexConsumer VertexConsumer2 = buffer.getBuffer(RenderType.eyes(OUTER_TEXTURES));
            model.renderToBuffer(poseStack, VertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.4F);
            poseStack.popPose();
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
            TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE)), poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, false, r * 1.2F, g * 1.2F, b * 1.2F, 1, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.mulPose((new Quaternionf()).setAngleAxis(entityYaw * ((float) Math.PI / 180F), 0, -1.0F, 0));
            float scale2 = entity.size;
            poseStack.translate(0, scale2 / 4.0D, 0);
            poseStack.scale(-scale2, -scale2, scale2);
            VertexConsumer VertexConsumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
            model.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, 0, 0);
            model.renderToBuffer(poseStack, VertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, entity.alpha);
            poseStack.popPose();
        }
    }

    protected int getBlockLightLevel(VoidShockBomb entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VoidShockBomb entity) {
        return INNER_TEXTURES;
    }

}
