package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ModSpiderModel;
import com.Polarice3.Goety.common.entities.ally.spider.BoneSpiderServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BoneSpiderServantRenderer<T extends BoneSpiderServant> extends MobRenderer<T, ModSpiderModel<T>> {
   private static final ResourceLocation BONE_SPIDER_LOCATION = Goety.location("textures/entity/servants/spider/bone_spider_servant.png");

   public BoneSpiderServantRenderer(EntityRendererProvider.Context p_173946_) {
      super(p_173946_, new ModSpiderModel<>(p_173946_.bakeLayer(ModModelLayer.MOD_SPIDER)), 0.8F * 0.75F);
      this.addLayer(new SpiderEyesLayer<>(this));
   }

   protected float getFlipDegrees(T p_116011_) {
      return 180.0F;
   }

   protected void scale(BoneSpiderServant p_113974_, PoseStack p_113975_, float p_113976_) {
      p_113975_.scale(0.75F, 0.75F, 0.75F);
   }

   public ResourceLocation getTextureLocation(BoneSpiderServant p_113972_) {
      return BONE_SPIDER_LOCATION;
   }

   public static class SpiderEyesLayer<T extends Entity, M extends ModSpiderModel<T>> extends EyesLayer<T, M> {
      private static final RenderType SPIDER_EYES = RenderType.eyes(Goety.location("textures/entity/servants/spider/bone_spider_servant_eyes.png"));

      public SpiderEyesLayer(RenderLayerParent<T, M> p_117507_) {
         super(p_117507_);
      }

      public RenderType renderType() {
         return SPIDER_EYES;
      }
   }
}