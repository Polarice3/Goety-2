package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HarpoonModel;
import com.Polarice3.Goety.common.entities.projectiles.Harpoon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HarpoonRenderer extends EntityRenderer<Harpoon> {
   public static final ResourceLocation TEXTURE = Goety.location("textures/entity/projectiles/harpoon.png");
   private final HarpoonModel model;

   public HarpoonRenderer(EntityRendererProvider.Context p_174420_) {
      super(p_174420_);
      this.model = new HarpoonModel(p_174420_.bakeLayer(ModModelLayer.HARPOON));
   }

   public void render(Harpoon p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
      p_116114_.pushPose();
      p_116114_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot()) - 90.0F));
      p_116114_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot()) + 90.0F));
      float f9 = (float)p_116111_.shakeTime - p_116113_;
      if (f9 > 0.0F) {
         float f10 = -Mth.sin(f9 * 3.0F) * f9;
         p_116114_.mulPose(Axis.ZP.rotationDegrees(f10));
      }
      VertexConsumer vertexconsumer = p_116115_.getBuffer(this.model.renderType(this.getTextureLocation(p_116111_)));
      this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_116114_.popPose();
      super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
   }

   public ResourceLocation getTextureLocation(Harpoon p_116109_) {
      return TEXTURE;
   }
}