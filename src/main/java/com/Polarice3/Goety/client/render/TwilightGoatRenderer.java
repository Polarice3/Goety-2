package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TwilightGoatModel;
import com.Polarice3.Goety.common.entities.ally.TwilightGoat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TwilightGoatRenderer extends MobRenderer<TwilightGoat, TwilightGoatModel<TwilightGoat>> {
   private static final ResourceLocation GOAT_LOCATION = Goety.location("textures/entity/servants/twilight_goat/twilight_goat.png");

   public TwilightGoatRenderer(EntityRendererProvider.Context p_174153_) {
      super(p_174153_, new TwilightGoatModel<>(p_174153_.bakeLayer(ModelLayers.GOAT)), 0.7F);
   }

   @Override
   protected void scale(TwilightGoat p_115314_, PoseStack p_115315_, float p_115316_) {
      if (p_115314_.isUpgraded()){
         p_115315_.scale(1.25F, 1.25F, 1.25F);
      }
      super.scale(p_115314_, p_115315_, p_115316_);
   }

   public ResourceLocation getTextureLocation(TwilightGoat p_174157_) {
      return GOAT_LOCATION;
   }
}