package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SunkenSkeletonModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SunkenSkeletonServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SunkenSkeletonServantRenderer extends HumanoidMobRenderer<SunkenSkeletonServant, SunkenSkeletonModel<SunkenSkeletonServant>> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/skeleton/sunken_skeleton_servant.png");
   private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/skeleton/sunken_skeleton.png");

   public SunkenSkeletonServantRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModModelLayer.SUNKEN_SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SunkenSkeletonServantRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new SunkenSkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SunkenSkeletonModel<>(p_174382_.bakeLayer(p_174384_)), new SunkenSkeletonModel<>(p_174382_.bakeLayer(p_174385_)), p_174382_.getModelManager()));
      this.addLayer(new SunkenSkeletonServantLayer<>(this, p_174382_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(SunkenSkeletonServant servant) {
      if (MobsConfig.SunkenSkeletonServantTexture.get() && !servant.isHostile()) {
         return TEXTURES;
      }
      return ORIGINAL;
   }

   protected void setupRotations(SunkenSkeletonServant p_114109_, PoseStack p_114110_, float p_114111_, float p_114112_, float p_114113_) {
      super.setupRotations(p_114109_, p_114110_, p_114111_, p_114112_, p_114113_);
      float f = p_114109_.getSwimAmount(p_114113_);
      if (f > 0.0F) {
         p_114110_.mulPose(Axis.XP.rotationDegrees(Mth.lerp(f, p_114109_.getXRot(), -10.0F - p_114109_.getXRot())));
      }

   }

   protected boolean isShaking(SunkenSkeletonServant p_174389_) {
      return p_174389_.isShaking();
   }

   public static class SunkenSkeletonServantLayer<T extends AbstractSkeletonServant, M extends EntityModel<T>> extends RenderLayer<T, M> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/skeleton/sunken_skeleton_servant_overlay.png");
      private final SunkenSkeletonModel<T> layerModel;

      public SunkenSkeletonServantLayer(RenderLayerParent<T, M> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new SunkenSkeletonModel<>(p_174555_.bakeLayer(ModModelLayer.SUNKEN_SKELETON));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (!entitylivingbaseIn.isHostile()) {
            if (MobsConfig.SunkenSkeletonServantTexture.get()) {
               coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
            }
         }
      }
   }
}