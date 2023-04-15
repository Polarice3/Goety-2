package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WarlockModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class WarlockRenderer extends MobRenderer<Warlock, WarlockModel<Warlock>> {
   private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/cultist/warlock.png");

   public WarlockRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new WarlockModel<>(p_174443_.bakeLayer(ModModelLayer.WARLOCK)), 0.5F);
      this.addLayer(new CrossedArmsItemLayer<>(this, p_174443_.getItemInHandRenderer()));
      this.addLayer(new CustomHeadLayer<>(this, p_174443_.getModelSet(), p_174443_.getItemInHandRenderer()));
   }

   public void render(Warlock p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   public ResourceLocation getTextureLocation(Warlock p_116410_) {
      return WITCH_LOCATION;
   }

   protected void scale(Warlock p_116419_, PoseStack p_116420_, float p_116421_) {
      float f = 0.9375F;
      p_116420_.scale(0.9375F, 0.9375F, 0.9375F);
   }
}