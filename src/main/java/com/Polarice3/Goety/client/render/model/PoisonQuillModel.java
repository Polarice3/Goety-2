package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class PoisonQuillModel extends Model {
    private final ModelPart root;

    public PoisonQuillModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition arrow = partdefinition.addOrReplaceChild("arrow", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -14.0F, -0.5F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition arrow_r1 = arrow.addOrReplaceChild("arrow_r1", CubeListBuilder.create().texOffs(4, 0).addBox(-2.5F, -8.5F, 0.0F, 5.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition arrow_r2 = arrow.addOrReplaceChild("arrow_r2", CubeListBuilder.create().texOffs(4, 0).addBox(-2.5F, -8.5F, 0.0F, 5.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
