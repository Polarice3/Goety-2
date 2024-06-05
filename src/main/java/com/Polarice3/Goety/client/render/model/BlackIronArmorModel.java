package com.Polarice3.Goety.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class BlackIronArmorModel extends HumanoidModel<LivingEntity> {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public BlackIronArmorModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createInnerLayer(){
        return createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION);
    }

    public static LayerDefinition createOuterLayer(){
        return createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -4.5F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create().texOffs(36, 31).addBox(-4.0F, -14.0F, -3.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.65F)), PartPose.offset(0.0F, 24.0F, 1.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-4.0F, -3.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(38, 10).addBox(-3.0F, 1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(24, 19).addBox(-4.0F, 4.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition cube_r1 = right_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 21).mirror().addBox(-3.0F, -1.5F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -3.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(38, 10).addBox(-1.0F, 1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(24, 19).mirror().addBox(3.0F, 4.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition cube_r2 = left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(36, 21).addBox(-4.0F, -1.5F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.75F))
                .texOffs(20, 31).addBox(-2.1F, 1.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 35).mirror().addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.75F)).mirror(false)
                .texOffs(20, 31).addBox(-1.9F, 1.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bodyParts().forEach((modelPart -> modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)));
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.head, this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
    }
}
