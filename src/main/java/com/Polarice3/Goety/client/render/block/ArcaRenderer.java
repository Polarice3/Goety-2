package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ArcaRenderer implements BlockEntityRenderer<ArcaBlockEntity> {
    public static final ResourceLocation GLASS_CAGE_TEXTURE = Goety.location("textures/entity/arca/cage.png");
    public static final ResourceLocation CORE_TEXTURE = Goety.location("textures/entity/arca/core.png");
    public static final ResourceLocation D_CORE_TEXTURE = Goety.location("textures/entity/arca/d_core.png");
    private static final RenderType GLASS_CAGE_RENDER = RenderType.entityCutoutNoCull(GLASS_CAGE_TEXTURE);
    private static final RenderType CORE_RENDER = RenderType.entityCutoutNoCull(CORE_TEXTURE);
    private static final RenderType D_CORE_RENDER = RenderType.entityCutoutNoCull(D_CORE_TEXTURE);
    private final ModelPart cage;
    private final ModelPart core;

    public ArcaRenderer(BlockEntityRendererProvider.Context p_i226009_1_) {
        ModelPart modelpart = p_i226009_1_.bakeLayer(ModBlockLayer.ARCA);
        this.cage = modelpart.getChild("cage");
        this.core = modelpart.getChild("core");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("cage", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public void render(ArcaBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        float f = (float)pBlockEntity.tickCount + pPartialTicks;
        float f1 = pBlockEntity.getActiveRotation(pPartialTicks) * (180F / (float)Math.PI);
        float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
        f2 = f2 * f2 + f2;
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5D, 0.4F + f2 * 0.1F, 0.5D);
        Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vector3f.normalize();
        pMatrixStack.mulPose(new Quaternion(vector3f, f1, true));
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(GLASS_CAGE_RENDER);
        this.cage.render(pMatrixStack, ivertexbuilder, pCombinedLight, pCombinedOverlay);
        pMatrixStack.popPose();
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5D, 0.4F + f2 * 0.1F, 0.5D);
        pMatrixStack.scale(0.5F, 0.5F, 0.5F);
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(3 * f));
        VertexConsumer ivertexbuilder2 = pBuffer.getBuffer(CORE_RENDER);
        this.core.render(pMatrixStack, ivertexbuilder2, pCombinedLight, pCombinedOverlay);
        pMatrixStack.popPose();
    }

}
