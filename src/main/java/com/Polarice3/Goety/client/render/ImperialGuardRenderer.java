package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ImperialGuardModel;
import com.Polarice3.Goety.common.entities.ally.illager.ImperialGuardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ImperialGuardRenderer<T extends ImperialGuardServant> extends MobRenderer<T, ImperialGuardModel<T>> {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/imperial_guard.png");
   protected static final ResourceLocation HOSTILE = Goety.location("textures/entity/illagers/imperial_guard.png");

   public ImperialGuardRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new ImperialGuardModel<>(p_174443_.bakeLayer(ModModelLayer.IMPERIAL_GUARD)), 0.5F);
   }

   public ResourceLocation getTextureLocation(T p_116410_) {
      if (p_116410_.isHostile()) {
         return HOSTILE;
      }
      return TEXTURE;
   }
}