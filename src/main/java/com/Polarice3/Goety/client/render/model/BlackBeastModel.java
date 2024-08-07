package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.BlackBeastAnimations;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Pose;

import java.util.List;

public class BlackBeastModel<T extends BlackBeast> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart stalker;
	private final ModelPart body;
	private final ModelPart pelvis;
	private final ModelPart upper;
	private final ModelPart torso;
	private final ModelPart neck;
	private final ModelPart head;
	private final List<ModelPart> parts;

	public BlackBeastModel(ModelPart root) {
		this.root = root;
		this.stalker = root.getChild("stalker");
		this.body = this.stalker.getChild("body");
		this.pelvis = this.body.getChild("pelvis");
		this.upper = this.pelvis.getChild("upper");
		this.torso = this.upper.getChild("torso");
		this.neck = this.torso.getChild("neck");
		this.head = this.neck.getChild("head");
		this.parts = root.getAllParts().filter((p_170824_) -> {
			return !p_170824_.isEmpty();
		}).collect(ImmutableList.toImmutableList());
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition stalker = partdefinition.addOrReplaceChild("stalker", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = stalker.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 3.0F));

		PartDefinition pelvis = body.addOrReplaceChild("pelvis", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 1.5F));

		PartDefinition cube_r1 = pelvis.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 36).addBox(-7.0F, -8.0F, -1.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 2.0F, -3.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition upper = pelvis.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, -1.0F));

		PartDefinition torso = upper.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(6.0F, 3.0F, -4.5F));

		PartDefinition actual_torso = torso.addOrReplaceChild("actual_torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r2 = actual_torso.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-13.0F, -10.0F, -1.0F, 14.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition fur = torso.addOrReplaceChild("fur", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.0F, -13.4F, 1.1F, 0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r3 = fur.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(78, 17).addBox(0.0F, -7.0F, -7.0F, 0.0F, 14.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition cube_r4 = fur.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(78, 43).addBox(-7.0F, -3.0F, 0.0F, 14.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.9853F, 2.359F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r5 = fur.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(78, 43).addBox(-7.0F, -3.0F, 0.0F, 14.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9853F, 1.359F, 0.6981F, 0.0F, 0.0F));

		PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(-4.0F, -7.0F, -1.0F));

		PartDefinition cube_r6 = neck.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(74, 49).addBox(-6.0F, -2.0F, -1.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, -5.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition cube_r7 = neck.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(54, 33).addBox(-5.0F, -8.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition neck_chain = neck.addOrReplaceChild("neck_chain", CubeListBuilder.create().texOffs(102, 23).addBox(-1.5F, 0.0718F, 0.1762F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.75F, -5.75F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -8.0F, -4.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(34, 1).addBox(-2.5F, -5.0F, -11.0F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -9.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(26, 0).mirror().addBox(-2.0F, -5.0F, -0.5F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.5F, -7.5F, -0.5F, -0.4363F, 0.0F, 0.4363F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, -5.0F, -0.5F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -7.5F, -0.5F, -0.4363F, 0.0F, -0.4363F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(51, 0).addBox(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));

		PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-7.0F, -8.0F, -7.0F));

		PartDefinition right_upper = right_arm.addOrReplaceChild("right_upper", CubeListBuilder.create().texOffs(48, 12).addBox(-7.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(74, 49).addBox(-7.5F, 4.0F, -3.5F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition arm_chain = right_upper.addOrReplaceChild("arm_chain", CubeListBuilder.create(), PartPose.offset(-4.1257F, 5.9357F, 3.5F));

		PartDefinition cube_r8 = arm_chain.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(102, 23).addBox(-1.3743F, -2.9357F, 0.0F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.75F, 1.25F, 0.4363F, 0.0F, 0.0F));

		PartDefinition right_hand = right_upper.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -4.2195F, -14.1338F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(106, 41).mirror().addBox(-4.5F, -4.7195F, -6.1338F, 9.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.5F, 1.0472F, 0.0F, 0.0F));

		PartDefinition r_thumb = right_hand.addOrReplaceChild("r_thumb", CubeListBuilder.create().texOffs(71, 9).addBox(-0.5F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.7195F, -11.6338F, 0.0F, 0.3491F, 0.0F));

		PartDefinition r_fingers = right_hand.addOrReplaceChild("r_fingers", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -0.2195F, -13.6338F, 0.0F, 0.7854F, 0.0F));

		PartDefinition r_back_finger = r_fingers.addOrReplaceChild("r_back_finger", CubeListBuilder.create().texOffs(72, 23).addBox(0.0F, -1.4695F, -1.1338F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.25F))
				.texOffs(86, 23).addBox(4.0F, -1.4695F, -1.1338F, 5.0F, 3.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 1.9695F, -0.3662F));

		PartDefinition r_front_finger = r_fingers.addOrReplaceChild("r_front_finger", CubeListBuilder.create().texOffs(72, 23).addBox(0.0F, -1.4695F, -1.1338F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.25F))
				.texOffs(86, 23).addBox(4.0F, -1.4695F, -1.1338F, 5.0F, 3.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -2.0305F, -0.3662F));

		PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(7.0F, -8.0F, -7.0F));

		PartDefinition left_upper = left_arm.addOrReplaceChild("left_upper", CubeListBuilder.create().texOffs(48, 12).mirror().addBox(1.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(74, 49).mirror().addBox(0.5F, 4.0F, -3.5F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition left_hand = left_upper.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(-4.0F, -4.2195F, -14.1338F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(106, 41).mirror().addBox(-4.5F, -4.7195F, -6.1338F, 9.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 12.0F, 0.5F, 1.0472F, 0.0F, 0.0F));

		PartDefinition l_thumb = left_hand.addOrReplaceChild("l_thumb", CubeListBuilder.create().texOffs(71, 9).mirror().addBox(-4.5F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -0.7195F, -11.6338F, 0.0F, -0.3491F, 0.0F));

		PartDefinition l_fingers = left_hand.addOrReplaceChild("l_fingers", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -0.2195F, -13.6338F, 0.0F, -0.7854F, 0.0F));

		PartDefinition l_back_finger = l_fingers.addOrReplaceChild("l_back_finger", CubeListBuilder.create().texOffs(72, 23).mirror().addBox(-4.0F, -1.4695F, -1.1338F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(86, 23).mirror().addBox(-9.0F, -1.4695F, -1.1338F, 5.0F, 3.0F, 3.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offset(0.0F, 1.9695F, -0.3662F));

		PartDefinition l_front_finger = l_fingers.addOrReplaceChild("l_front_finger", CubeListBuilder.create().texOffs(72, 23).mirror().addBox(-4.0F, -1.4695F, -1.1338F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(86, 23).mirror().addBox(-9.0F, -1.4695F, -1.1338F, 5.0F, 3.0F, 3.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offset(0.0F, -2.0305F, -0.3662F));

		PartDefinition tail = pelvis.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 1.0F));

		PartDefinition upper_tail = tail.addOrReplaceChild("upper_tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 1.0F));

		PartDefinition cube_r9 = upper_tail.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(46, 51).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition lower_tail = tail.addOrReplaceChild("lower_tail", CubeListBuilder.create(), PartPose.offset(-0.5F, 5.5F, 8.0F));

		PartDefinition cube_r10 = lower_tail.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(62, 52).addBox(-1.0F, -0.7321F, -1.2679F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 1.2654F, 0.0F, 0.0F));

		PartDefinition right_leg = stalker.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, -14.0F, 0.0F));

		PartDefinition right_thigh = right_leg.addOrReplaceChild("right_thigh", CubeListBuilder.create().texOffs(32, 36).addBox(-3.5F, -3.5F, -2.0F, 6.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition right_shin = right_leg.addOrReplaceChild("right_shin", CubeListBuilder.create().texOffs(30, 51).addBox(-2.0F, -3.0F, -1.0F, 5.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(102, 52).addBox(-2.5F, 0.0F, -1.5F, 6.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 7.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition right_toe = right_shin.addOrReplaceChild("right_toe", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -0.75F));

		PartDefinition cube_r11 = right_toe.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 52).addBox(-5.0F, -3.0F, -1.5F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 2.0F, -5.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition left_leg = stalker.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(4.0F, -14.0F, 0.0F));

		PartDefinition left_thigh = left_leg.addOrReplaceChild("left_thigh", CubeListBuilder.create().texOffs(32, 36).mirror().addBox(-2.5F, -3.5F, -2.0F, 6.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.5F, 1.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition left_shin = left_leg.addOrReplaceChild("left_shin", CubeListBuilder.create().texOffs(30, 51).addBox(-3.0F, -3.0F, -1.0F, 5.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(102, 52).addBox(-3.5F, 0.0F, -1.5F, 6.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition shin_chain = left_shin.addOrReplaceChild("shin_chain", CubeListBuilder.create(), PartPose.offset(-0.5F, 2.0F, 2.5F));

		PartDefinition cube_r12 = shin_chain.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(102, 23).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.75F, 1.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition left_toe = left_shin.addOrReplaceChild("left_toe", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -0.75F));

		PartDefinition cube_r13 = left_toe.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 52).addBox(-5.0F, -3.0F, -1.0F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 2.0F, -5.5F, -0.4363F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isSummoning() && !entity.isMeleeAttacking() && !entity.isDeadOrDying()){
			this.animateHeadLookTarget(entity, netHeadYaw, headPitch);
		}
		this.animate(entity.idleAnimationState, BlackBeastAnimations.IDLE, ageInTicks);
		if (entity.canAnimateMove()) {
			if (entity.isInWater()){
				this.animateWalk(BlackBeastAnimations.SWIM, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			} else if (entity.getPose() == Pose.CROUCHING){
				this.animateWalk(BlackBeastAnimations.CROUCH_WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			} else {
				this.animateWalk(BlackBeastAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
		this.animate(entity.attackAnimationState, BlackBeastAnimations.ATTACK, ageInTicks);
		this.animate(entity.roarAnimationState, BlackBeastAnimations.ROAR, ageInTicks);
		this.animate(entity.sitAnimationState, BlackBeastAnimations.SIT, ageInTicks);
		this.animate(entity.toSitAnimationState, BlackBeastAnimations.TO_SIT, ageInTicks);
		this.animate(entity.toStandAnimationState, BlackBeastAnimations.TO_STAND, ageInTicks);
		this.animate(entity.deathAnimationState, BlackBeastAnimations.DEATH, ageInTicks);
	}

	private void animateHeadLookTarget(T entity, float netHeadYaw, float headPitch) {
		this.head.zRot = entity.getHeadRollAngle(Minecraft.getInstance().getPartialTick());
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public ModelPart getRandomModelPart(RandomSource p_233439_) {
		return this.parts.get(p_233439_.nextInt(this.parts.size()));
	}
}