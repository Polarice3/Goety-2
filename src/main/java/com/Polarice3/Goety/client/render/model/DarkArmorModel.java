package com.Polarice3.Goety.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class DarkArmorModel extends HumanoidModel<LivingEntity> {
	public boolean villager = false;
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart bottom;
	public final ModelPart cape;
	public final ModelPart rightArm;
	public final ModelPart leftArm;
	public final ModelPart rightLeg;
	public final ModelPart leftLeg;

	public DarkArmorModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.bottom = this.body.getChild("bottom");
		this.cape = this.body.getChild("cloth").getChild("cape");
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

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -4.5F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.25F))
				.texOffs(38, 0).addBox(-2.0F, -10.35F, -4.65F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition horns = head.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(116, 0).addBox(-8.25F, -11.2F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.15F))
				.texOffs(116, 0).mirror().addBox(5.25F, -11.2F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.15F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(91, 25).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.5F))
				.texOffs(29, 4).addBox(-2.0F, 8.0F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(30, 2).addBox(-1.0F, 12.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, -3.5F, -0.0436F, 0.0F, 0.0F));

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(33, 48).addBox(-5.0F, 0.0F, -2.0F, 10.0F, 11.0F, 5.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition breast = body.addOrReplaceChild("breast", CubeListBuilder.create().texOffs(16, 42).addBox(-2.0F, -3.0F, -1.5F, 9.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 3.0F, -3.5F));

		PartDefinition cloth = body.addOrReplaceChild("cloth", CubeListBuilder.create().texOffs(40, 25).addBox(-2.0F, -3.5F, -1.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(40, 25).addBox(5.0F, -3.5F, -1.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 3.0F, -2.5F));

		PartDefinition cape = cloth.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(64, 0).addBox(-4.5F, -1.0F, -0.5F, 9.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, 6.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cape1 = cape.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(64, 11).addBox(-4.5F, 0.0F, -0.5F, 9.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 9).mirror().addBox(-3.0F, -1.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
				.texOffs(32, 21).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(48, 39).addBox(-4.0F, 4.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(44, 19).addBox(-3.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition cube_r2 = right_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 4).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 4).addBox(-1.5F, -1.5F, -8.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 3.75F, 0.0F, 0.0F, -0.4363F));

		PartDefinition cube_r3 = right_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(65, 28).mirror().addBox(-2.5F, -2.0F, -3.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 9).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.4F))
				.texOffs(32, 21).mirror().addBox(-1.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false)
				.texOffs(48, 39).mirror().addBox(1.0F, 4.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(44, 19).mirror().addBox(-1.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.26F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition cube_r4 = left_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 4).mirror().addBox(-1.5F, -1.5F, -8.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.0F, 3.75F, 0.0F, 0.0F, 0.4363F));

		PartDefinition cube_r5 = left_arm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(65, 28).addBox(-4.5F, -2.0F, -3.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 44).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(17, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(0, 56).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 56).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(0, 44).mirror().addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(17, 50).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	public DarkArmorModel animate(LivingEntity entity){
		float partialTick = Minecraft.getInstance().getFrameTime();
		float limbSwingAmount = entity.walkAnimation.speed(partialTick);
		float limbSwing = entity.walkAnimation.position(partialTick);
		float f = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		float f1 = Mth.cos((float) (limbSwing * 0.6662F + Math.PI)) * 1.4F * limbSwingAmount;
		float f2 = Math.min(f, f1);
		this.cape.xRot += -f2 / 2.0F;
		return this;
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.villager) {
			this.head.y += -1.0F;
		}
		super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		if (this.villager) {
			this.head.y -= -1.0F;
		}
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
	}
}