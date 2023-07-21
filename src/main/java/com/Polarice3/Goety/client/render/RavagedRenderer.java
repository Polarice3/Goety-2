package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RavagedModel;
import com.Polarice3.Goety.common.entities.ally.Ravaged;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RavagedRenderer extends MobRenderer<Ravaged, RavagedModel<Ravaged>> {
   private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/ravager/ravaged.png");

   public RavagedRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new RavagedModel<>(p_174443_.bakeLayer(ModModelLayer.RAVAGED)), 1.0F);
   }

   public void render(Ravaged p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   protected void scale(Ravaged p_115681_, PoseStack p_115682_, float p_115683_) {
      int i = p_115681_.getRavagedSize();
      float f = 1.0F + (0.15F * i);
      p_115682_.scale(f, f, f);
   }

   public ResourceLocation getTextureLocation(Ravaged p_116410_) {
      return TEXTURE;
   }

   protected boolean isShaking(Ravaged p_174389_) {
      return p_174389_.isShaking();
   }

}