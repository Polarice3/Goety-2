package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class BlazeServantRenderer extends MobRenderer<BlazeServant, BlazeModel<BlazeServant>> {
   private static final ResourceLocation BLAZE_LOCATION = Goety.location("textures/entity/servants/blaze_servant.png");
   private static final ResourceLocation ORIGINAL = new ResourceLocation("textures/entity/blaze.png");

   public BlazeServantRenderer(EntityRendererProvider.Context p_173933_) {
      super(p_173933_, new BlazeModel<>(p_173933_.bakeLayer(ModelLayers.BLAZE)), 0.5F);
   }

   protected int getBlockLightLevel(BlazeServant p_113910_, BlockPos p_113911_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(BlazeServant p_113908_) {
      if (MobsConfig.BlazeServantTexture.get()) {
         return ORIGINAL;
      }
      return BLAZE_LOCATION;
   }
}