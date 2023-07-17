package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.ModRavagerModel;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.common.items.RavagerArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RavagerArmorLayer<T extends LivingEntity & IRavager> extends RenderLayer<T, ModRavagerModel<T>> {
   private final ModRavagerModel<T> model;

   public RavagerArmorLayer(RenderLayerParent<T, ModRavagerModel<T>> p_174496_, EntityModelSet p_174497_) {
      super(p_174496_);
      this.model = new ModRavagerModel<T>(p_174497_.bakeLayer(ModModelLayer.RAVAGER_ARMOR));
   }

   public void render(PoseStack p_117032_, MultiBufferSource p_117033_, int p_117034_, T p_117035_, float p_117036_, float p_117037_, float p_117038_, float p_117039_, float p_117040_, float p_117041_) {
      ItemStack itemstack = p_117035_.getArmor();
      if (itemstack.getItem() instanceof RavagerArmorItem ravagerArmorItem) {
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.prepareMobModel(p_117035_, p_117036_, p_117037_, p_117038_);
         this.model.setupAnim(p_117035_, p_117036_, p_117037_, p_117039_, p_117040_, p_117041_);

         VertexConsumer vertexconsumer = p_117033_.getBuffer(RenderType.entityCutoutNoCull(ravagerArmorItem.getTexture()));
         this.model.renderToBuffer(p_117032_, vertexconsumer, p_117034_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}