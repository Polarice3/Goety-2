package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RoyalGuardModel;
import com.Polarice3.Goety.common.entities.ally.illager.RoyalGuardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RoyalGuardRenderer<T extends RoyalGuardServant> extends MobRenderer<T, RoyalGuardModel<T>> {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/royal_guard.png");
   protected static final ResourceLocation HOSTILE = Goety.location("textures/entity/illagers/royal_guard.png");

   public RoyalGuardRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new RoyalGuardModel<>(p_174443_.bakeLayer(ModModelLayer.ROYAL_GUARD)), 0.5F);
   }

   public ResourceLocation getTextureLocation(T p_116410_) {
      if (p_116410_.isHostile()) {
         return HOSTILE;
      }
      return TEXTURE;
   }
}