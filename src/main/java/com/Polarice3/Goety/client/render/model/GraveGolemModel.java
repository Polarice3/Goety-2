package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.GraveGolemAnimations;
import com.Polarice3.Goety.common.entities.ally.GraveGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GraveGolemModel<T extends GraveGolem> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart golem;
	private final ModelPart head;
	private final ModelPart headTop;
	private final ModelPart headJaw;
	private final ModelPart upper;
	private final ModelPart body;
	private final ModelPart graves;
	private final ModelPart leftGrave;
	private final ModelPart middleGrave;
	private final ModelPart rightGrave;
	private final ModelPart pelvis;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightShoulder;
	private final ModelPart leftShoulder;
	private final ModelPart rightMiddle;
	private final ModelPart leftMiddle;
	private final ModelPart rightHand;
	private final ModelPart leftHand;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public GraveGolemModel(ModelPart root) {
		this.root = root;
		this.golem = root.getChild("golem");
		this.upper = this.golem.getChild("upper");
		this.body = this.upper.getChild("body");
		this.graves = this.body.getChild("graves");
		this.leftGrave = this.graves.getChild("left_grave");
		this.middleGrave = this.graves.getChild("middle_grave");
		this.rightGrave = this.graves.getChild("right_grave");
		this.head = this.upper.getChild("head");
		this.headTop = this.head.getChild("top");
		this.headJaw = this.head.getChild("bottom");
		this.rightArm = this.upper.getChild("right_arm");
		this.leftArm = this.upper.getChild("left_arm");
		this.rightShoulder = this.rightArm.getChild("right_shoulder");
		this.leftShoulder = this.leftArm.getChild("left_shoulder");
		this.rightMiddle = this.rightShoulder.getChild("right_middle");
		this.leftMiddle = this.leftShoulder.getChild("left_middle");
		this.rightHand = this.rightMiddle.getChild("right_hand");
		this.leftHand = this.leftMiddle.getChild("left_hand");
		this.pelvis = this.golem.getChild("pelvis");
		this.rightLeg = this.golem.getChild("right_leg");
		this.leftLeg = this.golem.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition golem = partdefinition.addOrReplaceChild("golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upper = golem.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -25.5F, 0.0F));

		PartDefinition body = upper.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-22.5F, -60.0F, -12.0F, 45.0F, 33.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.5F, 0.0F));

		PartDefinition graves = body.addOrReplaceChild("graves", CubeListBuilder.create(), PartPose.offset(0.0F, -62.0F, 0.0F));

		PartDefinition left_grave = graves.addOrReplaceChild("left_grave", CubeListBuilder.create().texOffs(139, 16).addBox(-4.5F, -7.0F, 6.0F, 9.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(165, 0).addBox(-4.5F, 0.0F, -7.5F, 9.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(15.0F, 0.0F, 0.0F));

		PartDefinition middle_grave = graves.addOrReplaceChild("middle_grave", CubeListBuilder.create().texOffs(187, 14).addBox(-6.0F, -16.0F, 6.0F, 12.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(114, 0).addBox(-6.0F, 0.0F, -9.0F, 12.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition right_grave = graves.addOrReplaceChild("right_grave", CubeListBuilder.create().texOffs(163, 14).addBox(-4.5F, -10.0F, 6.0F, 9.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(165, 0).addBox(-4.5F, 0.0F, -7.5F, 9.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-15.0F, 0.0F, 0.0F));

		PartDefinition tomb = body.addOrReplaceChild("tomb", CubeListBuilder.create().texOffs(214, 29).addBox(-5.5F, -57.0F, 12.0F, 15.0F, 27.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(218, 62).addBox(-6.5F, -58.0F, 18.0F, 17.0F, 29.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition head = upper.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -19.5F, -10.5F));

		PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 57).addBox(-8.0F, -15.0F, -13.5F, 16.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottom = head.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(56, 57).addBox(-8.0F, 0.0F, -13.5F, 16.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-24.0F, -30.0F, 0.0F));

		PartDefinition right_shoulder = right_arm.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(80, 86).addBox(-20.5F, -6.75F, -9.0F, 22.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.25F, 0.0F));

		PartDefinition right_roof = right_shoulder.addOrReplaceChild("right_roof", CubeListBuilder.create(), PartPose.offset(-8.5F, -0.75F, -7.5F));

		PartDefinition cube_r1 = right_roof.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(160, 101).addBox(-13.0F, 0.0F, 10.5F, 24.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r2 = right_roof.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(160, 101).addBox(-13.0F, -11.0F, -0.5F, 24.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition right_middle = right_shoulder.addOrReplaceChild("right_middle", CubeListBuilder.create().texOffs(62, 73).addBox(1.0F, -1.5F, 1.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(62, 73).addBox(1.0F, -1.5F, -6.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(62, 73).addBox(-6.5F, -1.5F, -6.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(62, 73).addBox(-6.5F, -1.5F, 1.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.5F, 9.75F, 0.0F));

		PartDefinition right_hand = right_middle.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(138, 35).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 15.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.5F, 0.0F));

		PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(24.0F, -30.0F, 0.0F));

		PartDefinition left_shoulder = left_arm.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(0, 86).addBox(-1.5F, -6.75F, -9.0F, 22.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.25F, 0.0F));

		PartDefinition left_roof = left_shoulder.addOrReplaceChild("left_roof", CubeListBuilder.create(), PartPose.offset(10.5F, -0.75F, -7.5F));

		PartDefinition cube_r3 = left_roof.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(160, 101).addBox(-13.0F, 0.0F, 10.5F, 24.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r4 = left_roof.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(160, 101).addBox(-13.0F, -11.0F, -0.5F, 24.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition left_middle = left_shoulder.addOrReplaceChild("left_middle", CubeListBuilder.create().texOffs(62, 73).mirror().addBox(1.5F, -1.5F, 1.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(62, 73).mirror().addBox(1.5F, -1.5F, -6.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(62, 73).mirror().addBox(-6.0F, -1.5F, -6.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(62, 73).mirror().addBox(-6.0F, -1.5F, 1.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(10.5F, 9.75F, 0.0F));

		PartDefinition left_hand = left_middle.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(142, 68).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 15.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.5F, 0.0F));

		PartDefinition pelvis = golem.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(120, 122).addBox(-9.0F, -4.25F, -7.5F, 18.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -23.25F, 0.0F));

		PartDefinition right_leg = golem.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 122).addBox(-13.5F, 1.5F, -7.5F, 15.0F, 21.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, -22.5F, 0.0F));

		PartDefinition left_leg = golem.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 122).addBox(-1.5F, 1.5F, -7.5F, 15.0F, 21.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, -22.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.activateAnimationState, GraveGolemAnimations.ACTIVATE, ageInTicks);
		this.animate(entity.idleAnimationState, GraveGolemAnimations.IDLE, ageInTicks);
		if (entity.canAnimateMove()) {
			this.animateWalk(GraveGolemAnimations.WALK, limbSwing, limbSwingAmount, 6.0F, 20.0F);
		}
		this.animate(entity.attackAnimationState, GraveGolemAnimations.SMASH, ageInTicks);
		this.animate(entity.summonAnimationState, GraveGolemAnimations.SUMMON, ageInTicks);
		this.animate(entity.sitAnimationState, GraveGolemAnimations.SIT, ageInTicks);
		this.animate(entity.shootAnimationState, GraveGolemAnimations.SHOOT, ageInTicks);
		this.animate(entity.belchAnimationState, GraveGolemAnimations.SPIT, ageInTicks);
		this.animate(entity.deathAnimationState, GraveGolemAnimations.DEATH, ageInTicks);
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}