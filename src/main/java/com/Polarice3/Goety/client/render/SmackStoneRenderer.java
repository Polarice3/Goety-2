package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SmackStoneModel;
import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.common.entities.projectiles.SmackStone;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SmackStoneRenderer extends EntityRenderer<SmackStone> {
   private static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/smack_stone.png");
   private static final ResourceLocation FIERY = Goety.location("textures/entity/projectiles/smack_stone_fiery.png");
   private static final ResourceLocation GLOW = Goety.location("textures/entity/projectiles/smack_stone_fiery_glow.png");
   private static final ResourceLocation TRAIL = Goety.location("textures/entity/projectiles/solid_trail.png");
   private final SmackStoneModel<SmackStone> model;

   public SmackStoneRenderer(EntityRendererProvider.Context p_174449_) {
      super(p_174449_);
      this.model = new SmackStoneModel<>(p_174449_.bakeLayer(ModModelLayer.SMACK_STONE));
   }

   public void render(SmackStone entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
      float f = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
      float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
      VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entityIn)));
      this.model.setupAnim(0.0F, f, f1);
      this.model.renderToBuffer(matrixStackIn, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
      VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderType.eyes(GLOW));
      this.model.setupAnim(0.0F, f, f1);
      this.model.renderToBuffer(matrixStackIn, vertexconsumer2, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
      matrixStackIn.popPose();

      double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
      double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
      double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
      matrixStackIn.pushPose();
      entityIn.trail.prepareRender(new Vec3(x, y + entityIn.getBbHeight() / 2, z), partialTicks);
      matrixStackIn.translate(-x, -y, -z);
      ColorUtil colorUtil = new ColorUtil(0x79553a);
      if (entityIn.getFiery() > 0) {
         colorUtil = new ColorUtil(0xca4e06);
      }
      TrailRenderer.render(entityIn.trail, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TRAIL)), matrixStackIn, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1, LightTexture.FULL_BRIGHT);
      matrixStackIn.popPose();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(SmackStone p_116482_) {
      if (p_116482_.getFiery() > 0) {
         return FIERY;
      }
      return TEXTURE;
   }
}