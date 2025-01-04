package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CarrionFlyModel;
import com.Polarice3.Goety.common.entities.neutral.CarrionFly;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CarrionFlyRenderer extends MobRenderer<CarrionFly, CarrionFlyModel<CarrionFly>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/wight/carrion_fly.png");

   public CarrionFlyRenderer(EntityRendererProvider.Context p_173994_) {
      super(p_173994_, new CarrionFlyModel<>(p_173994_.bakeLayer(ModModelLayer.FLY)), 0.3F);
      this.addLayer(new FlyEyesLayer<>(this));
   }

   protected float getFlipDegrees(CarrionFly p_114352_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(CarrionFly p_114354_) {
      return TEXTURES;
   }

   public static class FlyEyesLayer<T extends CarrionFly, M extends CarrionFlyModel<T>> extends EyesLayer<T, M> {
      private static final RenderType EYES = RenderType.eyes(Goety.location("textures/entity/wight/carrion_fly_eyes.png"));

      public FlyEyesLayer(RenderLayerParent<T, M> p_117507_) {
         super(p_117507_);
      }

      public RenderType renderType() {
         return EYES;
      }
   }
}