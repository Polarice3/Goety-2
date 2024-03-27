package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.InfernoModel;
import com.Polarice3.Goety.common.entities.hostile.servants.Inferno;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class InfernoRenderer extends MobRenderer<Inferno, InfernoModel<Inferno>> {
   private static final ResourceLocation BLAZE_LOCATION = Goety.location("textures/entity/servants/inferno.png");
   private static final ResourceLocation GRAND = Goety.location("textures/entity/servants/inferno_grand.png");

   public InfernoRenderer(EntityRendererProvider.Context p_173933_) {
      super(p_173933_, new InfernoModel<>(p_173933_.bakeLayer(ModModelLayer.INFERNO)), 0.5F);
      this.addLayer(new GlowLayer<>(this));
   }

   protected int getBlockLightLevel(Inferno p_113910_, BlockPos p_113911_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Inferno p_113908_) {
      if (p_113908_.isUpgraded()){
         return GRAND;
      } else {
         return BLAZE_LOCATION;
      }
   }

   public static class GlowLayer<T extends Inferno, M extends InfernoModel<T>> extends EyesLayer<T, M> {
      private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/servants/inferno_eyes.png"));

      public GlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
         super(p_i50919_1_);
      }

      public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
         if (!p_116986_.isInvisible()) {
            VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType());
            this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      @Override
      public RenderType renderType() {
         return RENDER_TYPE;
      }
   }
}