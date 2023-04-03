package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HuskServantRenderer extends ZombieServantRenderer {
   protected static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID, "textures/entity/servants/husk_minion.png");

   public HuskServantRenderer(EntityRendererProvider.Context p_174180_) {
      super(p_174180_, ModelLayers.HUSK, ModelLayers.HUSK_INNER_ARMOR, ModelLayers.HUSK_OUTER_ARMOR);
   }

   protected void scale(ZombieServant p_114907_, PoseStack p_114908_, float p_114909_) {
      float f = 1.0625F;
      p_114908_.scale(1.0625F, 1.0625F, 1.0625F);
      super.scale(p_114907_, p_114908_, p_114909_);
   }

   public ResourceLocation getTextureLocation(ZombieServant p_114905_) {
      return TEXTURE;
   }
}