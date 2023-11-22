package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.PikerAnimations;
import com.Polarice3.Goety.common.entities.hostile.illagers.Piker;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PikerModel<T extends Piker> extends HierarchicalModel<T> implements HeadedModel {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public PikerModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.head = this.illager.getChild("head");
		this.leftArm = this.illager.getChild("left_arm");
		this.rightLeg = this.illager.getChild("right_leg");
		this.leftLeg = this.illager.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = illager.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 62).addBox(-6.0F, -29.0F, -4.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(40, 62).addBox(-2.0F, -30.0F, -4.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 18.0F, -1.0F));

		PartDefinition bone = helmet.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-2.0F, 3.0F, -1.0F));

		PartDefinition head_r1 = bone.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(28, 43).addBox(0.5F, -3.5F, -4.0F, 0.0F, 8.0F, 10.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(4.0F, -27.5F, 1.0F, 0.0F, 0.0F, -1.0472F));

		PartDefinition bone2 = helmet.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -1.0F));

		PartDefinition head_r2 = bone2.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(28, 43).mirror().addBox(-0.5F, -3.5F, -4.0F, 0.0F, 8.0F, 10.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -27.5F, 1.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition body = illager.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -7.5F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-4.0F, -7.5F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 7.5F, 0.0F));

		PartDefinition right_arm = illager.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition spear = right_arm.addOrReplaceChild("spear", CubeListBuilder.create(), PartPose.offset(-0.5F, 10.0F, 2.0F));

		PartDefinition bits = spear.addOrReplaceChild("bits", CubeListBuilder.create().texOffs(0, 78).addBox(-0.5F, -18.0F, -18.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(2, 78).addBox(-0.5F, -17.0F, -17.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(4, 78).addBox(-0.5F, -16.0F, -17.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(9, 78).addBox(-0.5F, -15.0F, -16.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(10, 84).addBox(-0.5F, -14.0F, -16.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(20, 84).addBox(-0.5F, -13.0F, -15.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 83).addBox(-0.5F, -12.0F, -13.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 87).addBox(-0.5F, -12.0F, -15.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -8.0F, -9.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -9.0F, -10.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -10.0F, -11.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -11.0F, -12.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(21, 80).addBox(-0.5F, -7.0F, -8.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(29, 80).addBox(-0.5F, -6.0F, -7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(29, 80).addBox(-0.5F, -5.0F, -6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(30, 84).addBox(-0.5F, -4.0F, -5.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -3.0F, -4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(30, 84).addBox(-0.5F, 4.0F, 3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(29, 80).addBox(-0.5F, 3.0F, 2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(29, 80).addBox(-0.5F, 2.0F, 1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(21, 80).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 8.0F, 7.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 7.0F, 6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 6.0F, 5.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 5.0F, 4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 9.0F, 8.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 10.0F, 9.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 11.0F, 10.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(5, 84).addBox(-0.5F, 12.0F, 11.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(37, 80).addBox(-0.5F, 13.0F, 12.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 81).addBox(-0.5F, 14.0F, 13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition left_arm = illager.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
			if (entity.isMeleeAttacking()) {
				this.animateWalk(limbSwing, limbSwingAmount);
			}
		}
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		if (entity.aggressiveMode){
			this.animate(entity.idleAnimationState, PikerAnimations.IDLE, ageInTicks);
		} else {
			this.animate(entity.idleAnimationState, PikerAnimations.IDLE_PASSIVE, ageInTicks);
		}
		if (this.riding){
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = ((float)Math.PI / 10F);
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = (-(float)Math.PI / 10F);
			this.leftLeg.zRot = -0.07853982F;
		} else {
			if (entity.aggressiveMode){
				this.animate(entity.walkAnimationState, PikerAnimations.WALK, ageInTicks, groundSpeed * 10);
			} else {
				this.animate(entity.walkAnimationState, PikerAnimations.WALK_PASSIVE, ageInTicks);
				this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
				this.leftArm.yRot = 0.0F;
				this.leftArm.zRot = 0.0F;
				this.animateWalk(limbSwing, limbSwingAmount);
			}
		}
		this.animate(entity.attackAnimationState, PikerAnimations.ATTACK, ageInTicks);
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	private void animateWalk(float limbSwing, float limbSwingAmount){
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
	}

	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}