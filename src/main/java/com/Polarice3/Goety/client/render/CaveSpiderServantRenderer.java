package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.spider.CaveSpiderServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CaveSpiderServantRenderer extends SpiderServantRenderer<CaveSpiderServant> {
   private static final ResourceLocation CAVE_SPIDER_LOCATION = Goety.location("textures/entity/servants/spider/cave_spider_servant.png");

   public CaveSpiderServantRenderer(EntityRendererProvider.Context p_173946_) {
      super(p_173946_, ModelLayers.CAVE_SPIDER);
      this.shadowRadius *= 0.7F;
   }

   protected void scale(CaveSpiderServant p_113974_, PoseStack p_113975_, float p_113976_) {
      p_113975_.scale(0.7F, 0.7F, 0.7F);
   }

   public ResourceLocation getTextureLocation(CaveSpiderServant p_113972_) {
      return CAVE_SPIDER_LOCATION;
   }
}