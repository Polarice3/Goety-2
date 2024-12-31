package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TropicalSlimeModel;
import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.common.entities.ally.TropicalSlimeServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class TropicalSlimeServantRenderer extends MobRenderer<TropicalSlimeServant, TropicalSlimeModel<TropicalSlimeServant>> {
   private static final ResourceLocation SLIME_LOCATION = Goety.location("textures/entity/servants/slime/tropical_slime_servant.png");
   private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/slime/tropical_slime.png");

   public TropicalSlimeServantRenderer(EntityRendererProvider.Context p_174391_) {
      super(p_174391_, new TropicalSlimeModel<>(p_174391_.bakeLayer(ModModelLayer.TROPICAL_SLIME_INNER)), 0.25F);
      this.addLayer(new TropicalSlimeSecretLayer<>(this, p_174391_.getModelSet()));
      this.addLayer(new TropicalSlimeFishLayer<>(this, p_174391_.getModelSet()));
      this.addLayer(new TropicalSlimeOuterLayer<>(this, p_174391_.getModelSet()));
   }

   public void render(TropicalSlimeServant p_115976_, float p_115977_, float p_115978_, PoseStack p_115979_, MultiBufferSource p_115980_, int p_115981_) {
      this.shadowRadius = 0.25F * (float)p_115976_.getSize();
      super.render(p_115976_, p_115977_, p_115978_, p_115979_, p_115980_, p_115981_);
   }

   protected void scale(TropicalSlimeServant p_115983_, PoseStack p_115984_, float p_115985_) {
      float f = 0.999F;
      p_115984_.scale(f, f, f);
      p_115984_.translate(0.0D, (double)0.001F, 0.0D);
      float f1 = (float)p_115983_.getSize();
      float f2 = Mth.lerp(p_115985_, p_115983_.oSquish, p_115983_.squish) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      p_115984_.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   public ResourceLocation getTextureLocation(TropicalSlimeServant p_115974_) {
      if (p_115974_.isHostile() || !MobsConfig.SlimeServantTexture.get()){
         return ORIGINAL;
      }
      return SLIME_LOCATION;
   }

   public static class TropicalSlimeSecretLayer<T extends SlimeServant> extends RenderLayer<T, TropicalSlimeModel<T>> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/slime/tropical_slime_servant_secret.png");
      private final TropicalSlimeModel<T> layerModel;

      public TropicalSlimeSecretLayer(RenderLayerParent<T, TropicalSlimeModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new TropicalSlimeModel<>(p_174555_.bakeLayer(ModModelLayer.TROPICAL_SLIME_INNER));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (entitylivingbaseIn.isInterested()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   public static class TropicalSlimeFishLayer<T extends TropicalSlimeServant> extends RenderLayer<T, TropicalSlimeModel<T>> {
      private final TropicalSlimeModel<T> layerModel;

      public TropicalSlimeFishLayer(RenderLayerParent<T, TropicalSlimeModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new TropicalSlimeModel<>(p_174555_.bakeLayer(ModModelLayer.TROPICAL_SLIME_INNER));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, entitylivingbaseIn.getResourceLocation(), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
      }
   }

   public static class TropicalSlimeOuterLayer<T extends LivingEntity> extends RenderLayer<T, TropicalSlimeModel<T>> {
      private final EntityModel<T> model;

      public TropicalSlimeOuterLayer(RenderLayerParent<T, TropicalSlimeModel<T>> p_174536_, EntityModelSet p_174537_) {
         super(p_174536_);
         this.model = new TropicalSlimeModel<>(p_174537_.bakeLayer(ModModelLayer.TROPICAL_SLIME_OUTER));
      }

      public void render(PoseStack p_117470_, MultiBufferSource p_117471_, int p_117472_, T p_117473_, float p_117474_, float p_117475_, float p_117476_, float p_117477_, float p_117478_, float p_117479_) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.shouldEntityAppearGlowing(p_117473_) && p_117473_.isInvisible();
         if (!p_117473_.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
               vertexconsumer = p_117471_.getBuffer(RenderType.outline(this.getTextureLocation(p_117473_)));
            } else {
               vertexconsumer = p_117471_.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(p_117473_)));
            }

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(p_117473_, p_117474_, p_117475_, p_117476_);
            this.model.setupAnim(p_117473_, p_117474_, p_117475_, p_117477_, p_117478_, p_117479_);
            this.model.renderToBuffer(p_117470_, vertexconsumer, p_117472_, LivingEntityRenderer.getOverlayCoords(p_117473_, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}