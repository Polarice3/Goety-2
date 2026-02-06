package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.client.render.model.WarlockServantModel;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class WarlockServantRenderer extends MobRenderer<WarlockServant, WarlockServantModel<WarlockServant>> {
   private static final ResourceLocation WITCH_LOCATION = Goety.location("textures/entity/servants/cultist/warlock.png");
   private static final ResourceLocation ORIGINAL = Goety.location("textures/entity/cultist/warlock.png");

   public WarlockServantRenderer(EntityRendererProvider.Context renderManagerIn) {
      super(renderManagerIn, new WarlockServantModel<>(renderManagerIn.bakeLayer(ModModelLayer.WARLOCK)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)), renderManagerIn.getModelManager()));
      this.addLayer(new CrossedArmsItemLayer<>(this, renderManagerIn.getItemInHandRenderer()));
   }

   protected void scale(WarlockServant p_116419_, PoseStack p_116420_, float p_116421_) {
      float f = 0.9375F;
      p_116420_.scale(0.9375F, 0.9375F, 0.9375F);
   }

   public ResourceLocation getTextureLocation(WarlockServant p_116410_) {
      if (p_116410_.isHostile() || !MobsConfig.WarlockServantTexture.get()) {
         return ORIGINAL;
      }
      return WITCH_LOCATION;
   }

}