package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.client.render.layer.DrownedServantOuterLayer;
import com.Polarice3.Goety.client.render.model.DrownedServantModel;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.DrownedServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DrownedServantRenderer extends AbstractZombieServantRenderer<DrownedServant, DrownedServantModel<DrownedServant>> {
   protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie/drowned_servant.png");
   private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

   public DrownedServantRenderer(EntityRendererProvider.Context p_173964_) {
      super(p_173964_, new DrownedServantModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED)), new DrownedServantModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)), new DrownedServantModel<>(p_173964_.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)));
      this.addLayer(new DrownedServantOuterLayer<>(this, p_173964_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(DrownedServant p_114115_) {
      if (p_114115_.isHostile() || !MobsConfig.DrownedServantTexture.get()){
         return DROWNED_LOCATION;
      } else {
         return TEXTURE;
      }
   }

   protected void setupRotations(DrownedServant p_114109_, PoseStack p_114110_, float p_114111_, float p_114112_, float p_114113_) {
      super.setupRotations(p_114109_, p_114110_, p_114111_, p_114112_, p_114113_);
      float f = p_114109_.getSwimAmount(p_114113_);
      if (f > 0.0F) {
         p_114110_.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f, p_114109_.getXRot(), -10.0F - p_114109_.getXRot())));
      }

   }
}