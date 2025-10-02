package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.EndersentAnimations;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;

public class EndersentModel<T extends Endersent> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart endersent;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart upperBody;
	private final ModelPart head;
	public final ModelPart ender_eye;
	private final ModelPart r_eye;
	private final ModelPart l_eye;
	private final ModelPart rightArm;
	private final ModelPart rightArmBone;
	private final ModelPart rightHand;
	private final ModelPart leftArm;
	private final ModelPart leftArmBone;
	private final ModelPart leftHand;

	public EndersentModel(ModelPart root) {
		this.root = root;
		this.endersent = root.getChild("endersent");
		this.rightLeg = this.endersent.getChild("rightLeg");
		this.leftLeg = this.endersent.getChild("leftLeg");
		this.upperBody = this.endersent.getChild("upperBody");
		this.head = this.upperBody.getChild("head");
		this.ender_eye = this.upperBody.getChild("ender_eye");
		this.r_eye = this.head.getChild("r_eye");
		this.l_eye = this.head.getChild("l_eye");
		this.rightArm = this.upperBody.getChild("rightArm");
		this.rightArmBone = this.rightArm.getChild("rightArmBone");
		this.rightHand = this.rightArmBone.getChild("rightHand");
		this.leftArm = this.upperBody.getChild("leftArm");
		this.leftArmBone = this.leftArm.getChild("leftArmBone");
		this.leftHand = this.leftArmBone.getChild("leftHand");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition endersent = partdefinition.addOrReplaceChild("endersent", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition rightLeg = endersent.addOrReplaceChild("rightLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.5F, -61.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition rightLeg_r1 = rightLeg.addOrReplaceChild("rightLeg_r1", CubeListBuilder.create().texOffs(50, 37).addBox(1.5F, -61.0F, 0.0F, 4.0F, 61.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 61.0F, 2.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition leftLeg = endersent.addOrReplaceChild("leftLeg", CubeListBuilder.create(), PartPose.offsetAndRotation(3.5F, -61.0F, 0.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition leftLeg_r1 = leftLeg.addOrReplaceChild("leftLeg_r1", CubeListBuilder.create().texOffs(50, 37).addBox(-5.5F, -61.0F, 0.0F, 4.0F, 61.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 61.0F, 2.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition upperBody = endersent.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(30, 0).addBox(-7.5F, -29.0F, -4.0F, 15.0F, 29.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -60.0F, 1.0F, 0.0175F, 0.0F, 0.0F));

		PartDefinition ender_eye = upperBody.addOrReplaceChild("ender_eye", CubeListBuilder.create().texOffs(102, 0).addBox(-6.5F, -6.5F, -1.01F, 13.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(94, 0).addBox(-1.5F, 5.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(96, 0).addBox(-3.5F, 4.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(96, 0).addBox(1.5F, 4.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(98, 0).addBox(3.5F, 3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(98, 0).addBox(-4.5F, 3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(98, 0).addBox(4.5F, -2.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 0).addBox(4.5F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 0).addBox(3.5F, -4.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(88, 0).addBox(1.5F, -5.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 0).addBox(-5.5F, -3.5F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 0).addBox(-5.5F, 1.5F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 0).addBox(-6.5F, -1.5F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(82, 0).addBox(-3.5F, -5.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(84, 0).addBox(-1.5F, -6.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(84, 0).addBox(-4.5F, -4.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(88, 0).addBox(-0.5F, -6.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(98, 0).addBox(4.5F, 1.5F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(98, 0).addBox(5.5F, -1.5F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.5F, -4.0F));

		PartDefinition head = upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -8.0F, -5.0F, 9.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -29.0F, -4.0F));

		PartDefinition r_eye = head.addOrReplaceChild("r_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 3.0F, -5.0F));

		PartDefinition l_eye = head.addOrReplaceChild("l_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 3.0F, -5.0F));

		PartDefinition rightArm = upperBody.addOrReplaceChild("rightArm", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.0F, -26.0F, 0.0F, 0.0436F, 0.0436F, 0.0436F));

		PartDefinition rightArm_r1 = rightArm.addOrReplaceChild("rightArm_r1", CubeListBuilder.create().texOffs(16, 20).addBox(-2.0F, -29.0F, 7.5F, 4.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 26.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition rightArmBone = rightArm.addOrReplaceChild("rightArmBone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 27.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition rightArmBone_r1 = rightArmBone.addOrReplaceChild("rightArmBone_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 42.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition rightHand = rightArmBone.addOrReplaceChild("rightHand", CubeListBuilder.create().texOffs(0, 53).addBox(-4.0F, -0.01F, -2.0F, 8.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 42.0F, 0.0F));

		PartDefinition leftArm = upperBody.addOrReplaceChild("leftArm", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0F, -26.0F, 0.0F, 0.0F, -0.1309F, -0.0873F));

		PartDefinition leftArm_r1 = leftArm.addOrReplaceChild("leftArm_r1", CubeListBuilder.create().texOffs(16, 20).addBox(-2.0F, -29.0F, -10.5F, 4.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 26.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition leftArmBone = leftArm.addOrReplaceChild("leftArmBone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 27.0F, 0.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition leftArmBone_r1 = leftArmBone.addOrReplaceChild("leftArmBone_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 42.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition leftHand = leftArmBone.addOrReplaceChild("leftHand", CubeListBuilder.create().texOffs(0, 53).addBox(-4.0F, -0.01F, -2.0F, 8.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 42.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		AnimationState animationState = new AnimationState();
		animationState.start(entity.tickCount);
		this.animate(animationState, EndersentAnimations.SCALE, ageInTicks);
		this.animate(entity.idleAnimationState, EndersentAnimations.IDLE, ageInTicks);
		if (!entity.isDeadlyEscape()
				&& !entity.teleportInAnimationState.isStarted()
				&& !entity.teleportOutAnimationState.isStarted()){
			this.animateWalk(EndersentAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.attackAnimationState, EndersentAnimations.ATTACK, ageInTicks);
		this.animate(entity.swipeAnimationState, EndersentAnimations.SWING, ageInTicks);
		this.animate(entity.deadlyEscapeAnimationState, EndersentAnimations.DEADLY_ESCAPE, ageInTicks);
		this.animate(entity.teleportInAnimationState, EndersentAnimations.TELEPORT_IN, ageInTicks);
		this.animate(entity.teleportOutAnimationState, EndersentAnimations.TELEPORT_OUT, ageInTicks);
		this.animate(entity.deathAnimationState, EndersentAnimations.DEATH, ageInTicks);
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