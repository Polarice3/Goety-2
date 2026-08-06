package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.GuardianServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;

public class ElderGuardianServantRenderer extends GuardianServantRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");
   public static final ResourceLocation SERVANT_LOCATION = Goety.location("textures/entity/servants/elder_guardian_servant.png");

   public ElderGuardianServantRenderer(EntityRendererProvider.Context p_173966_) {
      super(p_173966_, 1.2F, ModelLayers.ELDER_GUARDIAN);
   }

   protected void scale(GuardianServant p_114129_, PoseStack p_114130_, float p_114131_) {
      p_114130_.scale(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }

   public ResourceLocation getTextureLocation(GuardianServant p_114127_) {
      if (p_114127_.isHostile() || !MobsConfig.GuardianServantTexture.get()){
         return GUARDIAN_ELDER_LOCATION;
      } else {
         return SERVANT_LOCATION;
      }
   }
}