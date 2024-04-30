package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.MagmaCubeServantModel;
import com.Polarice3.Goety.common.entities.ally.MagmaCubeServant;
import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MagmaCubeServantRenderer extends MobRenderer<MagmaCubeServant, MagmaCubeServantModel<MagmaCubeServant>> {
   private static final ResourceLocation MAGMACUBE_LOCATION = Goety.location("textures/entity/servants/slime/magma_cube_servant.png");
   private static final ResourceLocation ORIGINAL = new ResourceLocation("textures/entity/slime/magmacube.png");

   public MagmaCubeServantRenderer(EntityRendererProvider.Context p_174298_) {
      super(p_174298_, new MagmaCubeServantModel<>(p_174298_.bakeLayer(ModModelLayer.MAGMA_CUBE)), 0.25F);
      this.addLayer(new MCSecretLayer<>(this, p_174298_.getModelSet()));
   }

   protected int getBlockLightLevel(MagmaCubeServant p_115399_, BlockPos p_115400_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(MagmaCubeServant p_115393_) {
      if (p_115393_.isHostile() || !MobsConfig.MagmaCubeServantTexture.get()){
         return ORIGINAL;
      }
      return MAGMACUBE_LOCATION;
   }

   protected void scale(MagmaCubeServant p_115395_, PoseStack p_115396_, float p_115397_) {
      int i = p_115395_.getSize();
      float f = Mth.lerp(p_115397_, p_115395_.oSquish, p_115395_.squish) / ((float)i * 0.5F + 1.0F);
      float f1 = 1.0F / (f + 1.0F);
      p_115396_.scale(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
   }

   public class MCSecretLayer<T extends SlimeServant> extends RenderLayer<T, MagmaCubeServantModel<T>> {
      private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/slime/magma_cube_servant_secret.png");
      private final MagmaCubeServantModel<T> layerModel;

      public MCSecretLayer(RenderLayerParent<T, MagmaCubeServantModel<T>> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new MagmaCubeServantModel<>(p_174555_.bakeLayer(ModModelLayer.MAGMA_CUBE));
      }

      @Override
      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (entitylivingbaseIn.isInterested()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}