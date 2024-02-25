package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.model.PlayerZombieModel;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.JungleZombieServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class JungleZombieRenderer extends HumanoidMobRenderer<JungleZombieServant, PlayerZombieModel<JungleZombieServant>> {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie/jungle_zombie_servant.png");
   private static final ResourceLocation ZOMBIE_LOCATION = Goety.location("textures/entity/servants/zombie/jungle_zombie_original.png");

   public JungleZombieRenderer(EntityRendererProvider.Context entityRendererManager) {
      super(entityRendererManager, new PlayerZombieModel<>(entityRendererManager.bakeLayer(ModelLayers.PLAYER)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(entityRendererManager.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(entityRendererManager.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
   }

   public ResourceLocation getTextureLocation(JungleZombieServant p_113771_) {
      if (p_113771_.isHostile() || !MobsConfig.JungleZombieServantTexture.get()){
         return ZOMBIE_LOCATION;
      } else {
         return TEXTURE;
      }
   }

   protected boolean isShaking(JungleZombieServant p_113773_) {
      return super.isShaking(p_113773_) || p_113773_.isUnderWaterConverting();
   }
}