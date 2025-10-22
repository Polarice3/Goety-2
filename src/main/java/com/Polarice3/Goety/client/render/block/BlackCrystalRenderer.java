package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.entities.BlackCrystalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class BlackCrystalRenderer implements BlockEntityRenderer<BlackCrystalBlockEntity> {
    public static final ResourceLocation TEXTURE = Goety.location("textures/entity/black_crystal.png");
    private final ModelPart crystal;

    public BlackCrystalRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelpart = ctx.bakeLayer(ModBlockLayer.BLACK_CRYSTAL);
        this.crystal = modelpart.getChild("crystal");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition crystal = partdefinition.addOrReplaceChild("crystal", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(24, 9).addBox(-5.0F, -4.0F, -5.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(2.0F, -3.0F, -5.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 9).addBox(2.0F, -4.0F, 2.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-5.0F, -3.0F, 2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void render(@Nullable BlackCrystalBlockEntity pBlockEntity, float pPartialTicks, PoseStack pStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        pStack.pushPose();
        pStack.translate(0.0F, pBlockEntity == null ? 2.1F : 2.25F, 0.0F);
        pStack.scale(1.0F, -1.0F, 1.0F);

        pStack.pushPose();
        if (pBlockEntity != null) {
            float f = (float)pBlockEntity.tickCount + pPartialTicks;
            float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
            f2 = f2 * f2 + f2;
            pStack.translate(0.5F, 0.5F + (f2 * 0.1F), 0.5F);
        } else {
            pStack.translate(0.5F, 0.5F, 0.5F);
        }
        RenderType layer = RenderType.entityTranslucent(TEXTURE);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(layer);
        this.crystal.render(pStack, vertexConsumer, pLight, pOverlay);
        pStack.popPose();

        pStack.popPose();
    }
}
