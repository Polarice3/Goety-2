package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.HauntedArmorStandArmorModel;
import com.Polarice3.Goety.client.render.model.HauntedArmorStandModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;

import javax.annotation.Nullable;

public class HauntedArmorStandRenderer<T extends ArmorStand> extends LivingEntityRenderer<T, HauntedArmorStandArmorModel<T>> {
   public static final ResourceLocation LOCATION = Goety.location("textures/entity/haunted_armor_stand.png");

   public HauntedArmorStandRenderer(EntityRendererProvider.Context p_173915_) {
      super(p_173915_, new HauntedArmorStandModel<>(p_173915_.bakeLayer(ModModelLayer.HAUNTED_ARMOR_STAND)), 0.0F);
      this.addLayer(new HumanoidArmorLayer<>(this, new HauntedArmorStandArmorModel<>(p_173915_.bakeLayer(ModModelLayer.HAS_INNER)), new HauntedArmorStandArmorModel<>(p_173915_.bakeLayer(ModModelLayer.HAS_OUTER))));
      this.addLayer(new ItemInHandLayer<>(this, p_173915_.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer<>(this, p_173915_.getModelSet()));
      this.addLayer(new CustomHeadLayer<>(this, p_173915_.getModelSet(), p_173915_.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(T p_113798_) {
      return LOCATION;
   }

   protected void setupRotations(T p_113800_, PoseStack p_113801_, float p_113802_, float p_113803_, float p_113804_) {
      p_113801_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_113803_));
      float f = (float)(p_113800_.level.getGameTime() - p_113800_.lastHit) + p_113804_;
      if (f < 5.0F) {
         p_113801_.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f / 1.5F * (float)Math.PI) * 3.0F));
      }

   }

   protected boolean shouldShowName(T p_113815_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_113815_);
      float f = p_113815_.isCrouching() ? 32.0F : 64.0F;
      return d0 >= (double)(f * f) ? false : p_113815_.isCustomNameVisible();
   }

   @Nullable
   protected RenderType getRenderType(T p_113806_, boolean p_113807_, boolean p_113808_, boolean p_113809_) {
      if (!p_113806_.isMarker()) {
         return super.getRenderType(p_113806_, p_113807_, p_113808_, p_113809_);
      } else {
         ResourceLocation resourcelocation = this.getTextureLocation(p_113806_);
         if (p_113808_) {
            return RenderType.entityTranslucent(resourcelocation, false);
         } else {
            return p_113807_ ? RenderType.entityCutoutNoCull(resourcelocation, false) : null;
         }
      }
   }
}