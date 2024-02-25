package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.RavagerArmorLayer;
import com.Polarice3.Goety.client.render.model.ModRavagerModel;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieRavager;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class ZombieRavagerRenderer<T extends Mob & IRavager> extends MobRenderer<T, ModRavagerModel<T>> {
   private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/servants/ravager/zombie_saddled_ravager.png");
   private static final ResourceLocation UNARMORED_LOCATION = Goety.location("textures/entity/servants/ravager/zombie_ravager.png");

   public ZombieRavagerRenderer(EntityRendererProvider.Context p_174362_) {
      super(p_174362_, new ModRavagerModel<>(p_174362_.bakeLayer(ModModelLayer.RAVAGER)), 1.1F);
      this.addLayer(new RavagerArmorLayer<>(this, p_174362_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(T p_115811_) {
      if (p_115811_ instanceof ZombieRavager modRavager && modRavager.hasSaddle()){
         return TEXTURE_LOCATION;
      } else {
         return UNARMORED_LOCATION;
      }
   }

   protected boolean isShaking(T p_116561_) {
      if (p_116561_ instanceof ZombieRavager zombieRavager){
         return zombieRavager.isConverting() || super.isShaking(p_116561_);
      }
      return super.isShaking(p_116561_);
   }
}