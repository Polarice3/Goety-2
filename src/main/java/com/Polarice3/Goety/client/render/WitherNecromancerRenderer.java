package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.WitherNecromancerModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class WitherNecromancerRenderer extends MobRenderer<AbstractWitherNecromancer, WitherNecromancerModel<AbstractWitherNecromancer>> {
   private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer/wither_necromancer.png");
   private static final ResourceLocation SERVANT_LOCATION = Goety.location("textures/entity/necromancer/wither_necromancer_servant.png");

   public WitherNecromancerRenderer(EntityRendererProvider.Context p_174382_) {
      super(p_174382_, new WitherNecromancerModel<>(p_174382_.bakeLayer(ModModelLayer.WITHER_NECROMANCER)), 0.5F);
      this.addLayer(new NecromancerEyesLayer<>(this));
   }

   protected void scale(AbstractWitherNecromancer necromancer, PoseStack matrixStackIn, float partialTickTime) {
      float original = 1.45F;
      float f1 = (float)necromancer.getNecroLevel();
      float size = original + Math.max(f1 * 0.15F, 0);
      matrixStackIn.scale(size, size, size);
   }

   public ResourceLocation getTextureLocation(AbstractWitherNecromancer p_115941_) {
      if (p_115941_.isHostile() || !MobsConfig.NecromancerServantTexture.get()){
         return SKELETON_LOCATION;
      } else {
         return SERVANT_LOCATION;
      }
   }

   public static class NecromancerEyesLayer<T extends AbstractWitherNecromancer, M extends WitherNecromancerModel<T>> extends EyesLayer<T, M> {
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