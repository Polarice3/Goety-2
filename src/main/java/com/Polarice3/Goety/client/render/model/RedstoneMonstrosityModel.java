package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.RedstoneMonstrosityAnimations;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneMonstrosity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RedstoneMonstrosityModel<T extends RedstoneMonstrosity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart monstrosity;
	private final ModelPart head;
	private final ModelPart eyes;

	public RedstoneMonstrosityModel(ModelPart root) {
		this.root = root;
		this.monstrosity = root.getChild("monstrosity");
		this.head = this.monstrosity.getChild("upper").getChild("head");
		this.eyes = this.head.getChild("top").getChild("eyes");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition monstrosity = partdefinition.addOrReplaceChild("monstrosity", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upper = monstrosity.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -36.0F, 0.0F));

		PartDefinition body = upper.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 52).addBox(-37.0F, -57.0F, -14.5F, 74.0F, 57.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(179, 41).addBox(-14.0F, -51.0F, 15.5F, 28.0F, 16.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = upper.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -28.0F, -15.0F));

		PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -25.0F, -20.0F, 28.0F, 31.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition right_horn = top.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(196, 0).addBox(-7.25F, 0.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(262, 0).addBox(-7.25F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-26.75F, -23.5F, -8.5F));

		PartDefinition left_horn = top.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(306, 0).addBox(-1.75F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(26.75F, -23.5F, -8.5F));

		PartDefinition cube_r1 = left_horn.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(196, 0).addBox(-10.0F, -6.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.75F, 7.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eyes = top.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(0, 14).addBox(-14.0F, -6.0F, -20.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(10.25F, -6.0F, -20.5F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(-2.0F, -9.0F, -20.5F, 6.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottom = head.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(98, 0).addBox(-14.0F, 0.0F, -20.0F, 28.0F, 10.0F, 21.0F, new CubeDeformation(0.0F))
		.texOffs(77, 31).addBox(-14.0F, 7.0F, -20.0F, 28.0F, 0.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-38.0F, -44.0F, -0.5F));

		PartDefinition right_shoulder = right_arm.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(257, 28).addBox(-36.0F, -12.0F, -13.5F, 37.0F, 23.0F, 27.0F, new CubeDeformation(0.0F))
		.texOffs(358, 0).addBox(-19.0F, -35.0F, -13.5F, 20.0F, 23.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition right_middle = right_shoulder.addOrReplaceChild("right_middle", CubeListBuilder.create().texOffs(209, 78).addBox(-11.0F, 3.0F, -13.0F, 22.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-18.0F, 8.0F, 4.5F));

		PartDefinition right_hand = right_middle.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(182, 116).addBox(-14.5F, -1.0F, -13.5F, 29.0F, 20.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 26.0F, -4.5F));

		PartDefinition r_finger_s = right_hand.addOrReplaceChild("r_finger_s", CubeListBuilder.create().texOffs(182, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 18.0F, 4.0F));

		PartDefinition r_finger_n = right_hand.addOrReplaceChild("r_finger_n", CubeListBuilder.create().texOffs(182, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 18.0F, -5.0F));

		PartDefinition r_thumb = right_hand.addOrReplaceChild("r_thumb", CubeListBuilder.create().texOffs(198, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 18.0F, 1.0F));

		PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(38.0F, -44.0F, 0.0F));

		PartDefinition left_shoulder = left_arm.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(285, 78).addBox(-1.0F, -12.0F, -13.5F, 37.0F, 23.0F, 27.0F, new CubeDeformation(0.0F))
		.texOffs(386, 50).addBox(-1.0F, -35.0F, -13.5F, 20.0F, 23.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, -0.5F));

		PartDefinition left_middle = left_shoulder.addOrReplaceChild("left_middle", CubeListBuilder.create().texOffs(209, 78).mirror().addBox(-11.0F, 3.0F, -13.0F, 22.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(17.0F, 8.0F, 4.5F));

		PartDefinition left_hand = left_middle.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(294, 128).addBox(-14.5F, -1.0F, -13.5F, 29.0F, 20.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 26.0F, -4.5F));

		PartDefinition l_finger_s = left_hand.addOrReplaceChild("l_finger_s", CubeListBuilder.create().texOffs(182, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 18.0F, 4.0F));

		PartDefinition l_finger_n = left_hand.addOrReplaceChild("l_finger_n", CubeListBuilder.create().texOffs(182, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 18.0F, -5.0F));

		PartDefinition l_thumb = left_hand.addOrReplaceChild("l_thumb", CubeListBuilder.create().texOffs(198, 163).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 18.0F, 1.0F));

		PartDefinition pelvis = monstrosity.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 139).addBox(-14.0F, -5.5F, -10.5F, 28.0F, 11.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -30.5F, 0.5F));

		PartDefinition right_leg = monstrosity.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 171).addBox(-22.0F, 0.0F, -10.0F, 22.0F, 29.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -29.0F, 0.0F));

		PartDefinition left_leg = monstrosity.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(86, 171).addBox(0.0F, 0.0F, -10.0F, 22.0F, 29.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -29.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isSummoning() && !entity.isBelching() && !entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.activateAnimationState, RedstoneMonstrosityAnimations.ACTIVATE, ageInTicks);
		this.animate(entity.idleAnimationState, RedstoneMonstrosityAnimations.IDLE, ageInTicks);
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.walkAnimationState, RedstoneMonstrosityAnimations.WALK, ageInTicks, groundSpeed * 20);
		this.animate(entity.attackAnimationState, RedstoneMonstrosityAnimations.SMASH, ageInTicks);
		this.animate(entity.summonAnimationState, RedstoneMonstrosityAnimations.SUMMON, ageInTicks);
		this.animate(entity.sitAnimationState, RedstoneMonstrosityAnimations.SIT, ageInTicks);
		this.animate(entity.toSitAnimationState, RedstoneMonstrosityAnimations.TO_SIT, ageInTicks);
		this.animate(entity.toStandAnimationState, RedstoneMonstrosityAnimations.TO_STAND, ageInTicks);
		this.animate(entity.belchAnimationState, RedstoneMonstrosityAnimations.SPIT, ageInTicks);
		this.animate(entity.deathAnimationState, RedstoneMonstrosityAnimations.DEATH, ageInTicks);
		this.eyes.visible = entity.deathTime < 24;
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