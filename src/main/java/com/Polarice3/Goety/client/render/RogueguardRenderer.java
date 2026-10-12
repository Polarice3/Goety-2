package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RogueguardModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.RogueguardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RogueguardRenderer<T extends RogueguardServant> extends MobRenderer<T, RogueguardModel<T>> {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/skeleton/rogueguard.png");
   protected static final ResourceLocation HOSTILE = Goety.location("textures/entity/servants/skeleton/rogueguard_hostile.png");

   public RogueguardRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new RogueguardModel<>(p_174443_.bakeLayer(ModModelLayer.ROGUEGUARD)), 0.5F);
   }

   public ResourceLocation getTextureLocation(T p_116410_) {
      if (p_116410_.isHostile()) {
         return HOSTILE;
      }
      return TEXTURE;
   }
}