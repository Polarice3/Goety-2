package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.PoisonQuillModel;
import com.Polarice3.Goety.common.entities.projectiles.PoisonQuill;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PoisonQuillRenderer extends EntityRenderer<PoisonQuill> {
   public static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/poison_quill.png");
   public static final ResourceLocation AQUA = Goety.location("textures/entity/projectiles/poison_quill_aqua.png");
   private final PoisonQuillModel model;

   public PoisonQuillRenderer(EntityRendererProvider.Context p_174420_) {
      super(p_174420_);
      this.model = new PoisonQuillModel(p_174420_.bakeLayer(ModModelLayer.POISON_QUILL));
   }

   public void render(PoisonQuill p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
      p_116114_.pushPose();
      p_116114_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot()) - 90.0F));
      p_116114_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot()) + 90.0F));
      float f9 = (float)p_116111_.shakeTime - p_116113_;
      if (f9 > 0.0F) {
         float f10 = -Mth.sin(f9 * 3.0F) * f9;
         p_116114_.mulPose(Axis.ZP.rotationDegrees(f10));
      }
      VertexConsumer vertexconsumer = p_116115_.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(p_116111_)));
      this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_116114_.popPose();
      super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
   }

   public ResourceLocation getTextureLocation(PoisonQuill p_116109_) {
      if (p_116109_.isAqua()){
         return AQUA;
      }
      return TEXTURE;
   }
}