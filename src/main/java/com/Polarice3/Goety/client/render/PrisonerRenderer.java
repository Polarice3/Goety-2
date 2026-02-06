package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.PrisonerModel;
import com.Polarice3.Goety.common.entities.ally.illager.raider.Prisoner;
import com.Polarice3.Goety.utils.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class PrisonerRenderer extends MobRenderer<Prisoner, PrisonerModel<Prisoner>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");
   private static final ResourceLocation TRADER = new ResourceLocation("textures/entity/wandering_trader.png");
   private static final ResourceLocation CHAIN = Goety.location("textures/entity/servants/prisoner_chain.png");

   public PrisonerRenderer(EntityRendererProvider.Context p_174437_) {
      super(p_174437_, new PrisonerModel<>(p_174437_.bakeLayer(ModModelLayer.PRISONER)), 0.5F);
      this.addLayer(new CustomHeadLayer<>(this, p_174437_.getModelSet(), p_174437_.getItemInHandRenderer()));
      this.addLayer(new VillagerProfessionLayer<>(this, p_174437_.getResourceManager(), "villager"){
         @Override
         public void render(PoseStack p_117646_, MultiBufferSource p_117647_, int p_117648_, Prisoner p_117649_, float p_117650_, float p_117651_, float p_117652_, float p_117653_, float p_117654_, float p_117655_) {
            if (!p_117649_.isTrader()) {
               super.render(p_117646_, p_117647_, p_117648_, p_117649_, p_117650_, p_117651_, p_117652_, p_117653_, p_117654_, p_117655_);
            }
         }
      });
      this.addLayer(new ShacklesLayer<>(this, p_174437_.getModelSet()));
      this.addLayer(new PrisonerArmsItemLayer<>(this, p_174437_.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Prisoner p_116312_) {
      if (p_116312_.isTrader()) {
         return TRADER;
      }
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(Prisoner p_116314_, PoseStack p_116315_, float p_116316_) {
      float f = 0.9375F;
      if (p_116314_.isBaby() && !p_116314_.isTrader()) {
         f *= 0.5F;
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

      p_116315_.scale(f, f, f);
   }

   public boolean shouldRender(Prisoner p_115468_, Frustum p_115469_, double p_115470_, double p_115471_, double p_115472_) {
      if (super.shouldRender(p_115468_, p_115469_, p_115470_, p_115471_, p_115472_)) {
         return true;
      } else {
         Entity entity = p_115468_.getLeader();
         if (entity == null) {
            entity = p_115468_.getTrueOwner();
         }
         return entity != null && p_115469_.isVisible(entity.getBoundingBoxForCulling());
      }
   }

   public void render(Prisoner p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
      super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
      Entity entity = p_115455_.getLeader();
      if (entity == null) {
         entity = p_115455_.getTrueOwner();
      }
      if (entity != null && p_115455_.isChained && entity.distanceTo(p_115455_) <= 32.0F) {
         this.renderChain(p_115455_, p_115457_, p_115458_, p_115459_, entity);
      }
   }

   private <E extends Entity> void renderChain(Prisoner p_115462_, float p_115463_, PoseStack p_115464_, MultiBufferSource p_115465_, E p_115466_) {
      p_115464_.pushPose();
      Vec3 vec3 = p_115466_.getRopeHoldPosition(p_115463_);
      double d0 = (double)(Mth.lerp(p_115463_, p_115462_.yBodyRotO, p_115462_.yBodyRot) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
      Vec3 vec31 = p_115462_.getLeashOffset(p_115463_);
      double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
      double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
      double d3 = Mth.lerp(p_115463_, p_115462_.xo, p_115462_.getX()) + d1;
      double d4 = Mth.lerp(p_115463_, p_115462_.yo, p_115462_.getY()) + vec31.y;
      double d5 = Mth.lerp(p_115463_, p_115462_.zo, p_115462_.getZ()) + d2;
      p_115464_.translate(d1, vec31.y, d2);
      float f = (float)(vec3.x - d3);
      float f1 = (float)(vec3.y - d4);
      float f2 = (float)(vec3.z - d5);
      float f3 = 0.025F;
      VertexConsumer vertexconsumer = p_115465_.getBuffer(RenderType.leash());
      Matrix4f matrix4f = p_115464_.last().pose();
      float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
      float f5 = f2 * f4;
      float f6 = f * f4;
      BlockPos blockpos = BlockPos.containing(p_115462_.getEyePosition(p_115463_));
      BlockPos blockpos1 = BlockPos.containing(p_115466_.getEyePosition(p_115463_));
      int i = this.getBlockLightLevel(p_115462_, blockpos);
      int j = this.getBlockLightLevel(p_115462_, blockpos1);
      int k = p_115462_.level.getBrightness(LightLayer.SKY, blockpos);
      int l = p_115462_.level.getBrightness(LightLayer.SKY, blockpos1);

      for(int i1 = 0; i1 <= 24; ++i1) {
         addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
      }

      for(int j1 = 24; j1 >= 0; --j1) {
         addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
      }

      p_115464_.popPose();
   }

   private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_254405_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
      float f = (float)p_174321_ / 24.0F;
      int i = (int)Mth.lerp(f, (float)p_174313_, (float)p_174314_);
      int j = (int)Mth.lerp(f, (float)p_174315_, (float)p_174316_);
      int k = LightTexture.pack(i, j);
      ColorUtil light = new ColorUtil(0x93aead);
      ColorUtil dark = new ColorUtil(0x668785);
      float f2 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? dark.red() : light.red();
      float f3 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? dark.green() : light.green();
      float f4 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? dark.blue() : light.blue();
      float f5 = p_174310_ * f;
      float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
      float f7 = p_174312_ * f;
      p_174308_.vertex(p_254405_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
      p_174308_.vertex(p_254405_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
   }

   public static class ShacklesLayer<T extends Prisoner, M extends PrisonerModel<T>> extends RenderLayer<T, M> {
      private static final ResourceLocation SHACKLES = Goety.location("textures/entity/servants/prisoner_shackles.png");
      private final PrisonerModel<T> layerModel;

      public ShacklesLayer(RenderLayerParent<T, M> p_i50919_1_, EntityModelSet p_174555_) {
         super(p_i50919_1_);
         this.layerModel = new PrisonerModel<>(p_174555_.bakeLayer(ModModelLayer.PRISONER));
      }

      public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
         if (!entitylivingbaseIn.isInvisible()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, SHACKLES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   public static class PrisonerArmsItemLayer<T extends LivingEntity, M extends PrisonerModel<T>> extends RenderLayer<T, M> {
      private final ItemInHandRenderer itemInHandRenderer;

      public PrisonerArmsItemLayer(RenderLayerParent<T, M> p_234818_, ItemInHandRenderer p_234819_) {
         super(p_234818_);
         this.itemInHandRenderer = p_234819_;
      }

      public void render(PoseStack p_116699_, MultiBufferSource p_116700_, int p_116701_, T p_116702_, float p_116703_, float p_116704_, float p_116705_, float p_116706_, float p_116707_, float p_116708_) {
         p_116699_.pushPose();
         this.getParentModel().translateToHand(p_116699_);
         p_116699_.mulPose(Axis.XP.rotationDegrees(-90.0F));
         p_116699_.mulPose(Axis.YP.rotationDegrees(180.0F));
         p_116699_.translate(0.0F, 0.0F, -0.125F);
         ItemStack itemstack = p_116702_.getItemBySlot(EquipmentSlot.MAINHAND);
         this.itemInHandRenderer.renderItem(p_116702_, itemstack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, p_116699_, p_116700_, p_116701_);
         p_116699_.popPose();
      }
   }
}