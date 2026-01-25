package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FrayedServantRenderer extends ZombieServantRenderer {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie/frayed_servant.png");
   private static final ResourceLocation ZOMBIE_LOCATION = Goety.location("textures/entity/servants/zombie/frayed_hostile.png");

   public FrayedServantRenderer(EntityRendererProvider.Context p_174180_) {
      super(p_174180_);
   }

   @Override
   protected boolean isShaking(ZombieServant p_113773_) {
      return super.isShaking(p_113773_) || p_113773_.tickCount % 100 > 80;
   }

   public ResourceLocation getTextureLocation(ZombieServant p_114905_) {
      if (p_114905_.isHostile() || !MobsConfig.HuskServantTexture.get()){
         return ZOMBIE_LOCATION;
      } else {
         return TEXTURE;
      }
   }
}