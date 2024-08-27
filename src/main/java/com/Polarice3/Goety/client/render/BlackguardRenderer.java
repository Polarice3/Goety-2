package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BlackguardModel;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlackguardRenderer<T extends BlackguardServant> extends MobRenderer<T, BlackguardModel<T>> {
   private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie/blackguard.png");
   private static final ResourceLocation HOSTILE = Goety.location("textures/entity/servants/zombie/blackguard_hostile.png");

   public BlackguardRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new BlackguardModel<>(p_174443_.bakeLayer(ModModelLayer.BLACKGUARD)), 0.5F);
   }

   public ResourceLocation getTextureLocation(T p_116410_) {
      if (p_116410_.isHostile()){
         return HOSTILE;
      }
      return TEXTURE;
   }
}