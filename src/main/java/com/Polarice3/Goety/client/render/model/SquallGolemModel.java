package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.SquallGolemAnimations;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SquallGolemModel<T extends LivingEntity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart golem;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart pelvis;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightFan;
	private final ModelPart leftFan;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public SquallGolemModel(ModelPart root) {
		this.root = root;
		this.golem = root.getChild("golem");
		this.body = this.golem.getChild("body");
		this.pelvis = this.body.getChild("pelvis");
		this.head = this.body.getChild("head");
		this.rightArm = this.body.getChild("right_arm");
		this.leftArm = this.body.getChild("left_arm");
		this.rightFan = this.body.getChild("right_fan_base").getChild("right_fan");
		this.leftFan = this.body.getChild("left_fan_base").getChild("left_fan");
		this.rightLeg = this.golem.getChild("right_leg");
		this.leftLeg = this.golem.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition golem = partdefinition.addOrReplaceChild("golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = golem.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 23).addBox(-15.0F, -11.0F, -7.0F, 30.0F, 22.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -30.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(89, 0).addBox(-6.5F, 0.5F, -4.0F, 7.0F, 35.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(89, 43).addBox(-6.5F, 31.5F, 4.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-15.5F, -5.5F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(89, 53).addBox(-0.5F, 0.5F, -4.0F, 7.0F, 35.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(89, 96).addBox(-0.5F, 31.5F, 4.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(15.5F, -5.5F, 0.0F));

		PartDefinition right_fan_base = body.addOrReplaceChild("right_fan_base", CubeListBuilder.create().texOffs(42, 4).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.0F, -12.0F, -4.0F));

		PartDefinition right_fan = right_fan_base.addOrReplaceChild("right_fan", CubeListBuilder.create().texOffs(66, 11).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_blades = right_fan.addOrReplaceChild("right_blades", CubeListBuilder.create().texOffs(74, 0).mirror().addBox(-6.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(74, 9).addBox(1.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = right_blades.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(74, 9).addBox(1.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(74, 0).mirror().addBox(-6.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition left_fan_base = body.addOrReplaceChild("left_fan_base", CubeListBuilder.create().texOffs(58, 4).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(12.0F, -12.0F, -4.0F));

		PartDefinition left_fan = left_fan_base.addOrReplaceChild("left_fan", CubeListBuilder.create().texOffs(66, 11).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_blades = left_fan.addOrReplaceChild("left_blades", CubeListBuilder.create().texOffs(74, 9).mirror().addBox(-6.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(74, 0).addBox(1.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = left_blades.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(74, 9).addBox(1.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(74, 0).mirror().addBox(-6.0F, -11.0F, 0.0F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -10.0F, 16.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(75, 21).addBox(-1.0F, -6.0F, -12.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -7.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(52, 13).addBox(-2.0F, 3.0F, -13.0F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pelvis = body.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 59).addBox(-7.0F, 0.0F, -5.0F, 14.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition right_leg = golem.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 73).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 15.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -15.0F, 0.0F));

		PartDefinition left_leg = golem.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 73).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 15.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -15.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof SquallGolem squallGolem){
			if (squallGolem.isActivated() && !squallGolem.isDeadOrDying()){
				this.animateHeadLookTarget(netHeadYaw, headPitch);
			}
			Vec3 velocity = squallGolem.getDeltaMovement();
			float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
			this.animate(squallGolem.idleAnimationState, SquallGolemAnimations.IDLE, ageInTicks);
			this.animate(squallGolem.alertAnimationState, SquallGolemAnimations.ALERT, ageInTicks);
			this.animate(squallGolem.walkAnimationState, SquallGolemAnimations.WALK, ageInTicks, groundSpeed * 20);
			this.animate(squallGolem.attackAnimationState, SquallGolemAnimations.ATTACK, ageInTicks);
			this.animate(squallGolem.activateAnimationState, SquallGolemAnimations.ACTIVATE, ageInTicks);
			this.animate(squallGolem.deactivateAnimationState, SquallGolemAnimations.DEACTIVATE, ageInTicks);
			this.animate(squallGolem.offAnimationState, SquallGolemAnimations.OFF, ageInTicks);
			float speed = ageInTicks / 2.0F;
			if (!squallGolem.isActivated() && !squallGolem.isStartingUp()){
				speed = 0.0F;
			} else if (squallGolem.isAggressive()){
				speed = ageInTicks;
			}
			this.rightFan.yRot -= speed;
			this.leftFan.yRot = speed;
		}
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