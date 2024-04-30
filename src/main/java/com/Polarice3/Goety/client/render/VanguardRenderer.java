package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VanguardModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VanguardRenderer<T extends VanguardServant> extends MobRenderer<T, VanguardModel<T>> {
   private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/skeleton/vanguard.png");
   private static final ResourceLocation TEXTURE_2 = Goety.location("textures/entity/servants/skeleton/vanguard_original.png");

   public VanguardRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new VanguardModel<>(p_174443_.bakeLayer(ModModelLayer.VANGUARD)), 0.5F);
   }

   public void render(T p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   public ResourceLocation getTextureLocation(T p_116410_) {
      if (!MobsConfig.VanguardServantTexture.get()){
         return TEXTURE_2;
      }
      return TEXTURE;
   }
}