package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SoulBoltModel;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NecroBoltRenderer extends EntityRenderer<NecroBolt> {
   private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/necro_bolt.png");
   private final SoulBoltModel<NecroBolt> model;

   public NecroBoltRenderer(EntityRendererProvider.Context p_174449_) {
      super(p_174449_);
      this.model = new SoulBoltModel<>(p_174449_.bakeLayer(ModModelLayer.SOUL_BOLT));
   }

   protected int getBlockLightLevel(NecroBolt p_116491_, BlockPos p_116492_) {
      return 15;
   }

   public void render(NecroBolt p_116484_, float p_116485_, float p_116486_, PoseStack p_116487_, MultiBufferSource p_116488_, int p_116489_) {
      p_116487_.pushPose();
      p_116487_.scale(-2.5F, -2.5F, 2.5F);
      float f = Mth.rotlerp(p_116484_.yRotO, p_116484_.getYRot(), p_116486_);
      float f1 = Mth.lerp(p_116486_, p_116484_.xRotO, p_116484_.getXRot());
      VertexConsumer vertexconsumer = p_116488_.getBuffer(RenderType.eyes(this.getTextureLocation(p_116484_)));
      this.model.setupAnim(0.0F, f, f1);
      this.model.renderToBuffer(p_116487_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
      p_116487_.popPose();
      super.render(p_116484_, p_116485_, p_116486_, p_116487_, p_116488_, p_116489_);
   }

   public ResourceLocation getTextureLocation(NecroBolt p_116482_) {
      return TEXTURE;
   }
}