package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.BroodMotherAnimations;
import com.Polarice3.Goety.common.entities.neutral.AbstractBroodMother;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BroodMotherModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart brood_mother;
	private final ModelPart head;
	private final ModelPart mouth_thingies;
	private final ModelPart leftMouth;
	private final ModelPart rightMouth;
	private final ModelPart body;
	private final ModelPart mid;
	private final ModelPart abdomen;
	private final ModelPart legs;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightMiddleHindLeg;
	private final ModelPart leftMiddleHindLeg;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public BroodMotherModel(ModelPart root) {
		this.root = root;
		this.brood_mother = root.getChild("brood_mother");
		this.body = this.brood_mother.getChild("body");
		this.head = this.body.getChild("head");
		this.mouth_thingies = this.head.getChild("mouth_thingies");
		this.leftMouth = this.mouth_thingies.getChild("left_mouth");
		this.rightMouth = this.mouth_thingies.getChild("right_mouth");
		this.mid = this.body.getChild("mid");
		this.abdomen = this.mid.getChild("abdomen");
		this.legs = this.brood_mother.getChild("legs");
		this.rightHindLeg = this.legs.getChild("right_hind_leg");
		this.leftHindLeg = this.legs.getChild("left_hind_leg");
		this.rightMiddleHindLeg = this.legs.getChild("right_middle_hind_leg");
		this.leftMiddleHindLeg = this.legs.getChild("left_middle_hind_leg");
		this.rightMiddleFrontLeg = this.legs.getChild("right_middle_front_leg");
		this.leftMiddleFrontLeg = this.legs.getChild("left_middle_front_leg");
		this.rightFrontLeg = this.legs.getChild("right_front_leg");
		this.leftFrontLeg = this.legs.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition brood_mother = partdefinition.addOrReplaceChild("brood_mother", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition body = brood_mother.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 26.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -16.0F, -14.0F, 18.0F, 16.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, -14.0F));

		PartDefinition mouth_thingies = head.addOrReplaceChild("mouth_thingies", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -14.0F));

		PartDefinition left_mouth = mouth_thingies.addOrReplaceChild("left_mouth", CubeListBuilder.create().texOffs(54, 0).mirror().addBox(-2.0F, -1.5F, -3.5F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 0.5F, 0.5F));

		PartDefinition right_mouth = mouth_thingies.addOrReplaceChild("right_mouth", CubeListBuilder.create().texOffs(54, 0).addBox(-3.0F, -1.5F, -3.5F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.5F, 0.5F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(0, 4).addBox(-10.0F, -14.0F, -15.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 4).addBox(6.0F, -14.0F, -15.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 4).addBox(1.0F, -12.0F, -16.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 4).addBox(-5.0F, -12.0F, -16.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-5.0F, -15.0F, -15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(3.0F, -15.0F, -15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(6.0F, -17.0F, -15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-8.0F, -17.0F, -15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));

		PartDefinition mid = body.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(78, 0).addBox(-8.0F, -5.0899F, -1.6671F, 16.0F, 14.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -26.0F, -11.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition abdomen = mid.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 34).addBox(-11.0F, -11.0F, 0.0F, 24.0F, 24.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.9101F, 19.3329F, -0.0873F, 0.0F, 0.0F));

		PartDefinition legs = brood_mother.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(8.0F, 2.0F, -9.5F));

		PartDefinition right_hind_leg = legs.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(-13.0F, 0.0F, 14.0F));

		PartDefinition right_hind_thigh = right_hind_leg.addOrReplaceChild("right_hind_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7053F, -0.2618F, -2.3562F));

		PartDefinition knee8 = right_hind_thigh.addOrReplaceChild("knee8", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition left_hind_leg = legs.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, 0.0F, 14.0F));

		PartDefinition left_hind_thigh = left_hind_leg.addOrReplaceChild("left_hind_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7053F, 0.2618F, 2.3562F));

		PartDefinition knee4 = left_hind_thigh.addOrReplaceChild("knee4", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition right_middle_hind_leg = legs.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 9.0F));

		PartDefinition right_middle_hind_thigh = right_middle_hind_leg.addOrReplaceChild("right_middle_hind_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 2.8798F, -0.1745F, -2.8798F));

		PartDefinition knee7 = right_middle_hind_thigh.addOrReplaceChild("knee7", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

		PartDefinition left_middle_hind_leg = legs.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 9.0F));

		PartDefinition left_middle_hind_thigh = left_middle_hind_leg.addOrReplaceChild("left_middle_hind_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 2.8798F, 0.1745F, 2.8798F));

		PartDefinition knee3 = left_middle_hind_thigh.addOrReplaceChild("knee3", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.8727F));

		PartDefinition right_middle_front_leg = legs.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 6.0F));

		PartDefinition right_middle_front_thigh = right_middle_front_leg.addOrReplaceChild("right_middle_front_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0873F, -2.7925F));

		PartDefinition knee6 = right_middle_front_thigh.addOrReplaceChild("knee6", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition left_middle_front_leg = legs.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 6.0F));

		PartDefinition left_middle_front_thigh = left_middle_front_leg.addOrReplaceChild("left_middle_front_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, -0.0873F, 2.7925F));

		PartDefinition knee2 = left_middle_front_thigh.addOrReplaceChild("knee2", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0472F));

		PartDefinition right_front_leg = legs.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(-16.0F, 0.0F, 0.0F));

		PartDefinition right_front_thigh = right_front_leg.addOrReplaceChild("right_front_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7053F, 0.6109F, -2.3562F));

		PartDefinition knee5 = right_front_thigh.addOrReplaceChild("knee5", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

		PartDefinition left_front_leg = legs.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_front_thigh = left_front_leg.addOrReplaceChild("left_front_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7053F, -0.6109F, 2.3562F));

		PartDefinition knee9 = left_front_thigh.addOrReplaceChild("knee9", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.4835F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof AbstractBroodMother abstractBroodMother) {
			this.animate(abstractBroodMother.attackAnimationState, BroodMotherAnimations.ATTACK, ageInTicks);
			this.animate(abstractBroodMother.shootAnimationState, BroodMotherAnimations.SHOOT, ageInTicks);
			this.animate(abstractBroodMother.layEggsAnimationState, BroodMotherAnimations.LAY_EGGS, ageInTicks);
			this.animate(abstractBroodMother.chargeAnimationState, BroodMotherAnimations.CHARGE, ageInTicks);
			this.animate(abstractBroodMother.backOffAnimationState, BroodMotherAnimations.BACK_OFF, ageInTicks);
			this.animate(abstractBroodMother.jumpAnimationState, BroodMotherAnimations.JUMP, ageInTicks);
			this.animate(abstractBroodMother.deathAnimationState, BroodMotherAnimations.DEATH, ageInTicks);
		}
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.rightMouth.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.rightMouth.yRot = 0.0F;
		this.rightMouth.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
		this.leftMouth.xRot = -this.rightMouth.xRot;
		this.leftMouth.yRot = -this.rightMouth.yRot;
		this.leftMouth.zRot = -this.rightMouth.zRot;
		float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
		float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f6 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
		float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f10 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		this.rightHindLeg.yRot += f3;
		this.leftHindLeg.yRot += -f3;
		this.rightMiddleHindLeg.yRot += f4;
		this.leftMiddleHindLeg.yRot += -f4;
		this.rightMiddleFrontLeg.yRot += f5;
		this.leftMiddleFrontLeg.yRot += -f5;
		this.rightFrontLeg.yRot += f6;
		this.leftFrontLeg.yRot += -f6;
		this.rightHindLeg.zRot += f7;
		this.leftHindLeg.zRot += -f7;
		this.rightMiddleHindLeg.zRot += f8;
		this.leftMiddleHindLeg.zRot += -f8;
		this.rightMiddleFrontLeg.zRot += f9;
		this.leftMiddleFrontLeg.zRot += -f9;
		this.rightFrontLeg.zRot += f10;
		this.leftFrontLeg.zRot += -f10;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}