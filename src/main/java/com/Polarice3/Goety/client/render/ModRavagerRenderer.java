package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ModRavagerModel;
import com.Polarice3.Goety.common.entities.ally.ModRavager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ModRavagerRenderer extends MobRenderer<ModRavager, ModRavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");
   private static final ResourceLocation UNARMORED_LOCATION = Goety.location("textures/entity/servants/unarmored_ravager.png");

   public ModRavagerRenderer(EntityRendererProvider.Context p_174362_) {
      super(p_174362_, new ModRavagerModel(p_174362_.bakeLayer(ModModelLayer.RAVAGER)), 1.1F);
   }

   public ResourceLocation getTextureLocation(ModRavager p_115811_) {
      if (p_115811_.hasSaddle()){
         return TEXTURE_LOCATION;
      } else {
         return UNARMORED_LOCATION;
      }
   }
}