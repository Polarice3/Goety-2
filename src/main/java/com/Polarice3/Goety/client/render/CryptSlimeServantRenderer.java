package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.CryptSlimeServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CryptSlimeServantRenderer extends MobRenderer<CryptSlimeServant, SlimeModel<CryptSlimeServant>> {
   private static final ResourceLocation SLIME_LOCATION = Goety.location("textures/entity/servants/slime/crypt_slime_servant.png");
   private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/crypt_slime.png");

   public CryptSlimeServantRenderer(EntityRendererProvider.Context p_174391_) {
      super(p_174391_, new SlimeModel<>(p_174391_.bakeLayer(ModelLayers.SLIME)), 0.25F);
      this.addLayer(new SlimeSecretLayer<>(this, p_174391_.getModelSet()));
      this.addLayer(new SlimeOuterLayer<>(this, p_174391_.getModelSet()));
   }

   public void render(CryptSlimeServant p_115976_, float p_115977_, float p_115978_, PoseStack p_115979_, MultiBufferSource p_115980_, int p_115981_) {
      this.shadowRadius = 0.25F * (float)p_115976_.getSize();
      super.render(p_115976_, p_115977_, p_115978_, p_115979_, p_115980_, p_115981_);
   }

   protected void scale(CryptSlimeServant p_115983_, PoseStack p_115984_, float p_115985_) {
      float f = 0.999F;
      p_115984_.scale(0.999F, 0.999F, 0.999F);
      p_115984_.translate(0.0D, (double)0.001F, 0.0D);
      float f1 = (float)p_115983_.getSize();
      float f2 = Mth.lerp(p_115985_, p_115983_.oSquish, p_115983_.squish) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      p_115984_.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   public ResourceLocation getTextureLocation(CryptSlimeServant p_115974_) {
      if (p_115974_.isHostile() || !MobsConfig.CryptSlimeServantTexture.get()){
         return ORIGINAL;
      }
      return SLIME_LOCATION;
   }

   public class SlimeSecretLayer<T extends CryptSlimeServant> extends RenderLayer<T, SlimeModel<T>> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/slime/slime_servant_secret.png");
      private final SlimeModel<T> layerModel;

      public SlimeSecretLayer(RenderLayerParent<T, SlimeModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new SlimeModel<>(p_174555_.bakeLayer(ModelLayers.SLIME));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (entitylivingbaseIn.isInterested()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}