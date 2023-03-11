package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpikeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart base;
    private final ModelPart upper_jaw;

    public SpikeModel(ModelPart root) {
        this.base = root.getChild("base");
        this.upper_jaw = root.getChild("upper_jaw");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 22.0F, -5.0F));

        PartDefinition upper_jaw = partdefinition.addOrReplaceChild("upper_jaw", CubeListBuilder.create().texOffs(46, 0).addBox(-2.5F, -7.0F, -2.0F, 5.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(56, 18).addBox(2.5F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition upper_jaw_r1 = upper_jaw.addOrReplaceChild("upper_jaw_r1", CubeListBuilder.create().texOffs(56, 18).addBox(2.5F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition upper_jaw_r2 = upper_jaw.addOrReplaceChild("upper_jaw_r2", CubeListBuilder.create().texOffs(56, 18).addBox(-1.5F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition upper_jaw_r3 = upper_jaw.addOrReplaceChild("upper_jaw_r3", CubeListBuilder.create().texOffs(56, 18).addBox(-1.5F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition upper_jaw_r4 = upper_jaw.addOrReplaceChild("upper_jaw_r4", CubeListBuilder.create().texOffs(46, 0).addBox(-2.5F, -7.0F, -2.0F, 5.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f1 = (limbSwing + Mth.sin(limbSwing * 2.7F)) * 0.6F * 12.0F;
        this.upper_jaw.yRot -= ageInTicks;
        this.upper_jaw.y = 18.0F - f1;
        this.base.y = 24.0F - f1;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upper_jaw.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.base;
    }
}
