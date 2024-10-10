package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HereticModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heretic;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class HereticRenderer extends MobRenderer<Heretic, HereticModel<Heretic>> {
   private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/cultist/heretic.png");

   public HereticRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new HereticModel<>(p_174443_.bakeLayer(ModModelLayer.HERETIC)), 0.5F);
      this.addLayer(new CrossedArmsItemLayer<>(this, p_174443_.getItemInHandRenderer()));
      this.addLayer(new CustomHeadLayer<>(this, p_174443_.getModelSet(), p_174443_.getItemInHandRenderer()));
   }

   public void render(Heretic p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   public ResourceLocation getTextureLocation(Heretic p_116410_) {
      return WITCH_LOCATION;
   }

   protected void scale(Heretic p_116419_, PoseStack p_116420_, float p_116421_) {
      float f = 0.9375F;
      p_116420_.scale(f, f, f);
   }
}