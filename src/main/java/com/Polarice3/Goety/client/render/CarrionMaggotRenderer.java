package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CarrionMaggotModel;
import com.Polarice3.Goety.common.entities.neutral.CarrionMaggot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CarrionMaggotRenderer extends MobRenderer<CarrionMaggot, CarrionMaggotModel<CarrionMaggot>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/carrion_maggot.png");

   public CarrionMaggotRenderer(EntityRendererProvider.Context p_173994_) {
      super(p_173994_, new CarrionMaggotModel<>(p_173994_.bakeLayer(ModModelLayer.MAGGOT)), 0.3F);
   }

   protected float getFlipDegrees(CarrionMaggot p_114352_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(CarrionMaggot p_114354_) {
      return TEXTURES;
   }
}