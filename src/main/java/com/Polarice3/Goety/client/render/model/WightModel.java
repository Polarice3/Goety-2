package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.WightAnimations;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class WightModel<T extends Wight> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart wight;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart right_lower_leg;
	private final ModelPart left_lower_leg;

	public WightModel(ModelPart root) {
		this.root = root;
		this.wight = root.getChild("wight");
		this.body = this.wight.getChild("body");
		this.head = this.body.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.right_arm = this.body.getChild("right_arm");
		this.left_arm = this.body.getChild("left_arm");
		this.right_leg = this.wight.getChild("right_leg");
		this.left_leg = this.wight.getChild("left_leg");
		this.right_lower_leg = this.right_leg.getChild("right_lower_leg");
		this.left_lower_leg = this.left_leg.getChild("left_lower_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition wight = partdefinition.addOrReplaceChild("wight", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition body = wight.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 61).addBox(-5.0F, -8.0F, -3.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Body_r1 = body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(0, 45).addBox(-8.0F, -4.0F, -5.0F, 16.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition body_robe = body.addOrReplaceChild("body_robe", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.5F));

		PartDefinition Body_r2 = body_robe.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -6.0F, -5.0F, 16.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, -2.0F));

		PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -6.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition robe = top.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -6.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(0, 75).addBox(-6.0F, -8.0F, -8.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, 0.0F, -6.5F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 1.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 30).addBox(-3.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, 0.0F));

		PartDefinition right_lower = right_arm.addOrReplaceChild("right_lower", CubeListBuilder.create().texOffs(48, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(40, 22).addBox(-1.5F, 11.5F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(50, 18).addBox(-1.5F, 15.5F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 13.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 30).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, -10.0F, 0.0F));

		PartDefinition left_lower = left_arm.addOrReplaceChild("left_lower", CubeListBuilder.create().texOffs(48, 47).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(40, 22).mirror().addBox(-1.5F, 11.5F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(50, 18).mirror().addBox(-1.5F, 15.5F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition right_leg = wight.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 30).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition right_lower_leg = right_leg.addOrReplaceChild("right_lower_leg", CubeListBuilder.create().texOffs(56, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

		PartDefinition left_leg = wight.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(56, 30).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition left_lower_leg = left_leg.addOrReplaceChild("left_lower_leg", CubeListBuilder.create().texOffs(56, 47).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 15.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()) {
			boolean isCrouching = entity.getPose() == Pose.CROUCHING;
			if (isCrouching){
				this.animate(entity.idleAnimationState, WightAnimations.CROUCH, ageInTicks);
			} else {
				this.animate(entity.idleAnimationState, WightAnimations.IDLE, ageInTicks);
			}
			if (!entity.isMeleeAttacking()) {
				this.animateHeadLookTarget(netHeadYaw, headPitch);
				if (isCrouching) {
					this.head.xRot -= MathHelper.modelDegrees(75);
				}
				if (entity.isClimbing()){
					this.animateWalk(WightAnimations.CLIMB, limbSwing, limbSwingAmount, 2.5F, 1.0F);
				} else {
					this.animateWalk(WightAnimations.WALK, limbSwing, limbSwingAmount, 5.0F, 1.0F);
				}
			} else if (!isCrouching) {
				boolean flag = entity.getFallFlyingTicks() > 4;
				float f = 1.0F;
				if (flag) {
					f = (float)entity.getDeltaMovement().lengthSqr();
					f /= 0.2F;
					f *= f * f;
				}

				if (f < 1.0F) {
					f = 1.0F;
				}
				this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
				this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
				if (this.right_leg.xRot > 0.4F) {
					this.right_leg.xRot = 0.4F;
				}
				if (this.left_leg.xRot > 0.4F) {
					this.left_leg.xRot = 0.4F;
				}
				if (this.right_leg.xRot < -0.4F) {
					this.right_leg.xRot = -0.4F;
				}
				if (this.left_leg.xRot < -0.4F) {
					this.left_leg.xRot = -0.4F;
				}
			}
			if (isCrouching){
				this.animate(entity.attackAnimationState, WightAnimations.CROUCH_ATTACK, ageInTicks);
				this.animate(entity.smashAnimationState, WightAnimations.CROUCH_SMASH, ageInTicks);
			} else {
				this.animate(entity.attackAnimationState, WightAnimations.ATTACK, ageInTicks);
				this.animate(entity.smashAnimationState, WightAnimations.SMASH, ageInTicks);
			}
			this.animate(entity.unleashAnimationState, WightAnimations.UNLEASH, ageInTicks);
			this.animate(entity.summonAnimationState, WightAnimations.SUMMON, ageInTicks);
		} else {
			this.right_arm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
			this.left_arm.xRot = -Mth.cos(ageInTicks * 0.6662F) * 0.25F;
			this.right_arm.zRot = 2.3561945F;
			this.left_arm.zRot = -2.3561945F;
			this.right_leg.xRot = 0.0F;
			this.right_leg.yRot = 0.0F;
			this.right_leg.zRot = 0.0F;
			this.left_leg.xRot = 0.0F;
			this.left_leg.yRot = 0.0F;
			this.left_leg.zRot = 0.0F;
			this.right_lower_leg.xRot = 0.0F;
			this.right_lower_leg.yRot = 0.0F;
			this.right_lower_leg.zRot = 0.0F;
			this.left_lower_leg.xRot = 0.0F;
			this.left_lower_leg.yRot = 0.0F;
			this.left_lower_leg.zRot = 0.0F;
			this.head.xRot = -MathHelper.modelDegrees(45.0F);
			this.head.yRot = this.body.yRot;
			this.jaw.xRot = MathHelper.modelDegrees(25.0F);
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