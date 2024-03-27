package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WitherNecromancerModel;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class WitherNecromancerRenderer extends MobRenderer<WitherNecromancer, WitherNecromancerModel<WitherNecromancer>> {
   private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer/wither_necromancer.png");

   public WitherNecromancerRenderer(EntityRendererProvider.Context p_174382_) {
      super(p_174382_, new WitherNecromancerModel<>(p_174382_.bakeLayer(ModModelLayer.WITHER_NECROMANCER)), 0.5F);
      this.addLayer(new NecromancerEyesLayer<>(this));
   }

   protected void scale(WitherNecromancer necromancer, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.25F, 1.25F, 1.25F);
   }

   public ResourceLocation getTextureLocation(WitherNecromancer p_115941_) {
      return SKELETON_LOCATION;
   }

   public static class NecromancerEyesLayer<T extends WitherNecromancer, M extends WitherNecromancerModel<T>> extends EyesLayer<T, M> {
      private static final ResourceLocation GLOW = Goety.location("textures/entity/necromancer/wither_necromancer_glow.png");

      public NecromancerEyesLayer(RenderLayerParent<T, M> p_i50919_1_) {
         super(p_i50919_1_);
      }

      @Override
      public RenderType renderType() {
         return RenderType.eyes(GLOW);
      }
   }

}