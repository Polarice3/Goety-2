package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LoftyChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURES = Goety.location("textures/entity/chest/lofty_chest.png");
   private final ModelPart lid;
   private final ModelPart bottom;
   private final ModelPart lock;

   public LoftyChestRenderer(BlockEntityRendererProvider.Context p_173607_) {
      ModelPart modelpart = p_173607_.bakeLayer(ModBlockLayer.LOFTY_CHEST);
      this.bottom = modelpart.getChild("bottom");
      this.lid = modelpart.getChild("lid");
      this.lock = modelpart.getChild("lock");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void render(T p_112363_, float p_112364_, PoseStack p_112365_, MultiBufferSource p_112366_, int p_112367_, int p_112368_) {
      Level level = p_112363_.getLevel();
      boolean flag = level != null;
      BlockState blockstate = flag ? p_112363_.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      Block block = blockstate.getBlock();
      if (block instanceof AbstractChestBlock<?> abstractchestblock) {
         p_112365_.pushPose();
         float f = blockstate.getValue(ChestBlock.FACING).toYRot();
         p_112365_.translate(0.5F, 0.5F, 0.5F);
         p_112365_.mulPose(Vector3f.YP.rotationDegrees(-f));
         p_112365_.translate(-0.5F, -0.5F, -0.5F);
         DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> neighborcombineresult;
         if (flag) {
            neighborcombineresult = abstractchestblock.combine(blockstate, level, p_112363_.getBlockPos(), true);
         } else {
            neighborcombineresult = DoubleBlockCombiner.Combiner::acceptNone;
         }

         float f1 = neighborcombineresult.apply(ChestBlock.opennessCombiner(p_112363_)).get(p_112364_);
         f1 = 1.0F - f1;
         f1 = 1.0F - f1 * f1 * f1;
         int i = neighborcombineresult.apply(new BrightnessCombiner<>()).applyAsInt(p_112367_);
         VertexConsumer vertexconsumer = p_112366_.getBuffer(RenderType.entityCutout(TEXTURES));
         this.render(p_112365_, vertexconsumer, this.lid, this.lock, this.bottom, f1, i, p_112368_);
         p_112365_.popPose();
      }
   }

   private void render(PoseStack p_112370_, VertexConsumer p_112371_, ModelPart p_112372_, ModelPart p_112373_, ModelPart p_112374_, float p_112375_, int p_112376_, int p_112377_) {
      p_112372_.xRot = -(p_112375_ * ((float)Math.PI / 2F));
      p_112373_.xRot = p_112372_.xRot;
      p_112372_.render(p_112370_, p_112371_, p_112376_, p_112377_);
      p_112373_.render(p_112370_, p_112371_, p_112376_, p_112377_);
      p_112374_.render(p_112370_, p_112371_, p_112376_, p_112377_);
   }
}
