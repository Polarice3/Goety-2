package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.SpellcasterIllager;

public class EnviokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T> {
   private static final ResourceLocation EVOKER_ILLAGER = Goety.location("textures/entity/illagers/envioker.png");

   public EnviokerRenderer(EntityRendererProvider.Context p_174108_) {
      super(p_174108_, new IllagerModel<>(p_174108_.bakeLayer(ModelLayers.EVOKER)), 0.5F);
      this.addLayer(new ItemInHandLayer<>(this, p_174108_.getItemInHandRenderer()) {
         public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED) {
               super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(T p_114541_) {
      return EVOKER_ILLAGER;
   }
}