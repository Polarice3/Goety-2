package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BroodMotherModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractBroodMother;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BroodMotherRenderer<T extends AbstractBroodMother> extends MobRenderer<T, BroodMotherModel<T>> {
   private static final ResourceLocation SPIDER_LOCATION = Goety.location("textures/entity/servants/spider/brood_mother_servant.png");
   private static final ResourceLocation HOSTILE_LOCATION = Goety.location("textures/entity/servants/spider/brood_mother.png");
   private static final ResourceLocation OLD_LOCATION = Goety.location("textures/entity/servants/spider/old/brood_mother_servant.png");
   private static final ResourceLocation OLD_HOSTILE_LOCATION = Goety.location("textures/entity/servants/spider/old/brood_mother.png");

   public BroodMotherRenderer(EntityRendererProvider.Context p_174403_) {
      super(p_174403_, new BroodMotherModel<>(p_174403_.bakeLayer(ModModelLayer.BROOD_MOTHER)), 1.6F);
      this.addLayer(new BroodMotherEyesLayer<>(this));
   }

   protected float getFlipDegrees(T p_116011_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(T p_116009_) {
      boolean old = MobsConfig.BroodMotherOldTexture.get();
      if (p_116009_.isHostile()){
         if (old){
            return OLD_HOSTILE_LOCATION;
         } else {
            return HOSTILE_LOCATION;
         }
      }
      if (old){
         return OLD_LOCATION;
      } else {
         return SPIDER_LOCATION;
      }
   }

   public static class BroodMotherEyesLayer<T extends Entity, M extends BroodMotherModel<T>> extends EyesLayer<T, M> {
      private static final RenderType SPIDER_EYES = RenderType.eyes(Goety.location("textures/entity/servants/spider/brood_mother_eyes.png"));

      public BroodMotherEyesLayer(RenderLayerParent<T, M> p_117507_) {
         super(p_117507_);
      }

      public RenderType renderType() {
         return SPIDER_EYES;
      }
   }
}