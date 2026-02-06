package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ModWitchModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WitchServant;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;

public class WitchServantRenderer extends MobRenderer<WitchServant, ModWitchModel<WitchServant>> {
   private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/servants/cultist/witch.png");
   private static final ResourceLocation ORIGINAL = new ResourceLocation("textures/entity/witch.png");

   public WitchServantRenderer(EntityRendererProvider.Context renderManagerIn) {
      super(renderManagerIn, new ModWitchModel<>(renderManagerIn.bakeLayer(ModModelLayer.MOD_WITCH)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)), renderManagerIn.getModelManager()));
      this.addLayer(new ModWitchItemLayer<>(this, renderManagerIn.getItemInHandRenderer()));
   }

   public void render(WitchServant p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      this.model.setHoldingItem(!p_116412_.getMainHandItem().isEmpty()
              && (p_116412_.getMainHandItem().getItem() instanceof BrewItem
              || p_116412_.getMainHandItem().getItem() instanceof PotionItem));
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   protected void scale(WitchServant p_116419_, PoseStack p_116420_, float p_116421_) {
      float f = 0.9375F;
      p_116420_.scale(0.9375F, 0.9375F, 0.9375F);
   }

   public ResourceLocation getTextureLocation(WitchServant p_116410_) {
      if (p_116410_.isHostile() || !MobsConfig.WitchServantTexture.get()) {
         return ORIGINAL;
      }
      return WITCH_LOCATION;
   }

   public static class ModWitchItemLayer<T extends LivingEntity> extends CrossedArmsItemLayer<T, ModWitchModel<T>> {
      public ModWitchItemLayer(RenderLayerParent<T, ModWitchModel<T>> p_234926_, ItemInHandRenderer p_234927_) {
         super(p_234926_, p_234927_);
      }

      public void render(PoseStack p_117685_, MultiBufferSource p_117686_, int p_117687_, T p_117688_, float p_117689_, float p_117690_, float p_117691_, float p_117692_, float p_117693_, float p_117694_) {
         ItemStack itemstack = p_117688_.getMainHandItem();
         p_117685_.pushPose();
         if (itemstack.is(Items.POTION)) {
            this.getParentModel().getHead().translateAndRotate(p_117685_);
            this.getParentModel().getNose().translateAndRotate(p_117685_);
            p_117685_.translate(0.0625F, 0.25F, 0.0F);
            p_117685_.mulPose(Axis.ZP.rotationDegrees(180.0F));
            p_117685_.mulPose(Axis.XP.rotationDegrees(140.0F));
            p_117685_.mulPose(Axis.ZP.rotationDegrees(10.0F));
            p_117685_.translate(0.0F, -0.4F, 0.4F);
         }

         super.render(p_117685_, p_117686_, p_117687_, p_117688_, p_117689_, p_117690_, p_117691_, p_117692_, p_117693_, p_117694_);
         p_117685_.popPose();
      }
   }
}