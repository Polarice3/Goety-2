package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SoulBoltModel;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SoulBoltRenderer extends EntityRenderer<SoulBolt> {
   private static final ResourceLocation TRAIL = Goety.location("textures/entity/projectiles/trail.png");
   private final SoulBoltModel<SoulBolt> model;

   public SoulBoltRenderer(EntityRendererProvider.Context p_174449_) {
      super(p_174449_);
      this.model = new SoulBoltModel<>(p_174449_.bakeLayer(ModModelLayer.SOUL_BOLT));
   }

   protected int getBlockLightLevel(SoulBolt p_116491_, BlockPos p_116492_) {
      return 15;
   }

   public void render(SoulBolt entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.scale(-1.25F, -1.25F, 1.25F);
      float f = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
      float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
      VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.eyes(this.getTextureLocation(entityIn)));
      this.model.setupAnim(0.0F, f, f1);
      this.model.renderToBuffer(matrixStackIn, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
      matrixStackIn.popPose();
      if (entityIn.hasTrail()) {
         double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
         double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
         double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
         matrixStackIn.pushPose();
         matrixStackIn.translate(-x, -y, -z);
         ColorUtil colorUtil = new ColorUtil(0xdafdff);
         renderTrail(entityIn, partialTicks, matrixStackIn, bufferIn, colorUtil.red, colorUtil.green, colorUtil.blue, 0.6F, packedLightIn);
         matrixStackIn.popPose();
      }
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Based on codes from @AlexModGuy: <a href="https://github.com/AlexModGuy/AlexsCaves/blob/main/src/main/java/com/github/alexmodguy/alexscaves/client/render/entity/WaterBoltRenderer.java#L49">...</a>
    */
   private void renderTrail(SoulBolt entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, float red, float green, float blue, float alpha, int packedLightIn) {
      int samples = 0;
      int sampleSize = 1;
      double trailHeight = 0.25D;
      float trailZRot = 0;
      Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).zRot(trailZRot);
      Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).zRot(trailZRot);
      Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
      VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL));
      while (samples < sampleSize) {
         Vec3 sample = entityIn.getTrailPosition(samples + 8, partialTicks);
         Vec3 draw1 = drawFrom;

         PoseStack.Pose posestack$pose = matrixStackIn.last();
         Matrix4f matrix4f = posestack$pose.pose();
         Matrix3f matrix3f = posestack$pose.normal();
         float f2 = entityIn.tickCount % 8 / 8.0F;
         float f3 = f2 + 0.5F;
         vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f2, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
         vertexconsumer.vertex(matrix4f, (float) sample.x + (float) bottomAngleVec.x, (float) sample.y + (float) bottomAngleVec.y, (float) sample.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f3, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
         vertexconsumer.vertex(matrix4f, (float) sample.x + (float) topAngleVec.x, (float) sample.y + (float) topAngleVec.y, (float) sample.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f3, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
         vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f2, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
         samples++;
         drawFrom = sample;
      }
   }

   public ResourceLocation getTextureLocation(SoulBolt p_116482_) {
      return p_116482_.getResourceLocation();
   }
}