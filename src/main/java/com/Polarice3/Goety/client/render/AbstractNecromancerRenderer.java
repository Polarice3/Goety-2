package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.NecroCapeModel;
import com.Polarice3.Goety.client.render.model.NecromancerModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class AbstractNecromancerRenderer extends HumanoidMobRenderer<AbstractNecromancer, NecromancerModel<AbstractNecromancer>> {
   private static final ResourceLocation SKELETON_LOCATION = Goety.location("textures/entity/necromancer.png");

   public AbstractNecromancerRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public AbstractNecromancerRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new NecromancerModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new NecromancerModel<>(p_174382_.bakeLayer(p_174384_)), new NecromancerModel<>(p_174382_.bakeLayer(p_174385_))));
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

   public static class NecroSetLayer<T extends AbstractNecromancer> extends RenderLayer<T, NecromancerModel<T>> {
      private final ResourceLocation textures;
      private final NecroCapeModel<T> layerModel;

      public NecroSetLayer(RenderLayerParent<T, NecromancerModel<T>> p_i50919_1_, EntityModelSet p_174555_, ResourceLocation textures) {
         super(p_i50919_1_);
         this.layerModel = new NecroCapeModel<>(p_174555_.bakeLayer(ModModelLayer.NECRO_SET));
         this.textures = textures;
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, textures, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
      }
   }

}