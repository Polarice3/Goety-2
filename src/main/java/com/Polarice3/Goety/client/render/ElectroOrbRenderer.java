package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.ElectroOrb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ElectroOrbRenderer extends EntityRenderer<ElectroOrb> {
   private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/projectiles/electro_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);

   public ElectroOrbRenderer(EntityRendererProvider.Context context) {
      super(context);
   }

   protected int getBlockLightLevel(ElectroOrb entity, BlockPos blockPos) {
      return 15;
   }

   public void render(ElectroOrb entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      float f1 = Mth.sin(entity.tickCount / 5.0F) * 0.2F + 0.2F;
      poseStack.scale(1.0F + f1, 1.0F + f1, 1.0F + f1);
      poseStack.translate(0.0D, 0.25D, 0.0D);
      poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
      PoseStack.Pose posestack$pose = poseStack.last();
      Matrix4f matrix4f = posestack$pose.pose();
      Matrix3f matrix3f = posestack$pose.normal();
      VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
      float f2 = entity.tickCount % 16 / 16.0F;
      float f3 = f2 + 0.0625F;
      vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, -0.5F, f2, 1.0F);
      vertex(vertexconsumer, matrix4f, matrix3f, packedLight,  0.5F, -0.5F, f3, 1.0F);
      vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.5F, 0.5F, f3, 0.0F);
      vertex(vertexconsumer, matrix4f, matrix3f, packedLight, -0.5F, 0.5F, f2, 0.0F);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
   }

   private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float xPos, float yPos, float u, float v) {
      consumer.vertex(matrix4f, xPos, yPos , 0.0F)
              .color(255, 255, 255, 255)
              .uv(u, v)
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(packedLight)
              .normal(matrix3f, 0.0F, 1.0F, 0.0F)
              .endVertex();
   }

   public ResourceLocation getTextureLocation(ElectroOrb entity) {
      return TEXTURE_LOCATION;
   }
}