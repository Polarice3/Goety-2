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

public class CursedKnightArmorModel extends HumanoidModel<LivingEntity> {
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart right_arm;
	public final ModelPart left_arm;
	public final ModelPart pants;
	public final ModelPart right_leg;
	public final ModelPart left_leg;

	public CursedKnightArmorModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.right_arm = root.getChild("right_arm");
		this.left_arm = root.getChild("left_arm");
		this.pants = this.body.getChild("pants");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
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

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(0, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.5F))
				.texOffs(0, 16).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 6.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Head_r1 = head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(82, -8).addBox(0.0F, -40.0F, -1.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(50, 0).addBox(-4.5F, 0.0F, -3.5F, 9.0F, 6.0F, 7.0F, new CubeDeformation(0.1F))
		.texOffs(0, 38).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
		.texOffs(50, 13).addBox(-3.5F, 6.0F, -3.0F, 7.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 13).addBox(-3.5F, 6.0F, 2.0F, 7.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(32, 8).mirror().addBox(-3.0F, 6.0F, -2.0F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false)
				.texOffs(47, 23).addBox(-4.0F, 4.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(0, 54).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition cube_r1 = right_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(29, 16).mirror().addBox(-2.5F, -1.5F, -3.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -1.5F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(32, 8).addBox(-2.0F, 6.0F, -2.0F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(47, 23).addBox(3.0F, 4.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(0, 54).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition cube_r2 = left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(29, 16).addBox(-2.5F, -1.5F, -3.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(2.0F, -1.5F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create().texOffs(52, 27).addBox(-4.5F, -14.5F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(20, 50).mirror().addBox(-2.2F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(40, 35).mirror().addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(24, 35).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(20, 50).addBox(-1.8F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(40, 35).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(24, 35).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
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