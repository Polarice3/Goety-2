package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.DrownedServantModel;
import com.Polarice3.Goety.common.entities.ally.DrownedServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class DrownedServantOuterLayer<T extends DrownedServant> extends RenderLayer<T, DrownedServantModel<T>> {
   private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = Goety.location("textures/entity/servants/drowned_servant_outer_layer.png");
   private final DrownedServantModel<T> model;

   public DrownedServantOuterLayer(RenderLayerParent<T, DrownedServantModel<T>> p_174490_, EntityModelSet p_174491_) {
      super(p_174490_);
      this.model = new DrownedServantModel<>(p_174491_.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
   }

   public void render(PoseStack p_116924_, MultiBufferSource p_116925_, int p_116926_, T p_116927_, float p_116928_, float p_116929_, float p_116930_, float p_116931_, float p_116932_, float p_116933_) {
      coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, p_116924_, p_116925_, p_116926_, p_116927_, p_116928_, p_116929_, p_116931_, p_116932_, p_116933_, p_116930_, 1.0F, 1.0F, 1.0F);
   }
}