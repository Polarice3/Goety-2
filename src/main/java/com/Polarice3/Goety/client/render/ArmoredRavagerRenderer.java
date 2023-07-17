package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.layer.RavagerArmorLayer;
import com.Polarice3.Goety.client.render.model.ModRavagerModel;
import com.Polarice3.Goety.common.entities.hostile.ArmoredRavager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArmoredRavagerRenderer extends MobRenderer<ArmoredRavager, ModRavagerModel<ArmoredRavager>> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public ArmoredRavagerRenderer(EntityRendererProvider.Context p_174362_) {
      super(p_174362_, new ModRavagerModel<>(p_174362_.bakeLayer(ModModelLayer.RAVAGER)), 1.1F);
      this.addLayer(new RavagerArmorLayer<>(this, p_174362_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(ArmoredRavager p_115811_) {
      return TEXTURE_LOCATION;
   }
}