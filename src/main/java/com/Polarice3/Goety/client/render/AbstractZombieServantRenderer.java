package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ZombieServantModel;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieServantRenderer<T extends ZombieServant, M extends ZombieServantModel<T>> extends HumanoidMobRenderer<T, M> {
   protected static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID, "textures/entity/servants/zombie_minion.png");

   protected AbstractZombieServantRenderer(EntityRendererProvider.Context p_173910_, M p_173911_, M p_173912_, M p_173913_) {
      super(p_173910_, p_173911_, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, p_173912_, p_173913_));
   }

   public ResourceLocation getTextureLocation(ZombieServant p_113771_) {
      return TEXTURE;
   }

   protected boolean isShaking(T p_113773_) {
      return super.isShaking(p_113773_) || p_113773_.isUnderWaterConverting();
   }
}