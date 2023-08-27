package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.NecromancerModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class AbstractNecromancerRenderer extends MobRenderer<AbstractNecromancer, NecromancerModel<AbstractNecromancer>> {
   private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer.png");

   public AbstractNecromancerRenderer(EntityRendererProvider.Context p_174382_) {
      super(p_174382_, new NecromancerModel<>(p_174382_.bakeLayer(ModModelLayer.NECROMANCER)), 0.5F);
//      this.addLayer(new NecromancerArmorLayer(this, p_174382_));
   }

   protected void scale(AbstractNecromancer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(1.25F, 1.25F, 1.25F);
   }

   public ResourceLocation getTextureLocation(AbstractNecromancer p_115941_) {
      return SKELETON_LOCATION;
   }

   public static class NecromancerEyesLayer<T extends AbstractNecromancer, M extends NecromancerModel<T>> extends EyesLayer<T, M> {
      private final ResourceLocation textures;

      public NecromancerEyesLayer(RenderLayerParent<T, M> p_i50919_1_, ResourceLocation textures) {
         super(p_i50919_1_);
         this.textures = textures;
      }

      @Override
      public RenderType renderType() {
         return RenderType.eyes(textures);
      }
   }

}