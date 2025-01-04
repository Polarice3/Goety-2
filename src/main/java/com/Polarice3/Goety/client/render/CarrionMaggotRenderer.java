package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CarrionMaggotModel;
import com.Polarice3.Goety.common.entities.neutral.CarrionMaggot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CarrionMaggotRenderer extends MobRenderer<CarrionMaggot, CarrionMaggotModel<CarrionMaggot>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/wight/carrion_maggot.png");
   private static final ResourceLocation COCOON = Goety.location("textures/entity/wight/carrion_maggot_cocoon.png");

   public CarrionMaggotRenderer(EntityRendererProvider.Context p_173994_) {
      super(p_173994_, new CarrionMaggotModel<>(p_173994_.bakeLayer(ModModelLayer.MAGGOT)), 0.3F);
   }

   protected float getFlipDegrees(CarrionMaggot p_114352_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(CarrionMaggot p_114354_) {
      if (p_114354_.isCocoon()){
         return COCOON;
      }
      return TEXTURES;
   }
}