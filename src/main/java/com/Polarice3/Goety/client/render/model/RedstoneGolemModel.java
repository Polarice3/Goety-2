package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.RedstoneGolemAnimations;
import com.Polarice3.Goety.common.entities.ally.RedstoneGolem;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RedstoneGolemModel<T extends RedstoneGolem> extends HierarchicalModel<T> {
	private final ModelPart golem;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart chest;
	private final ModelPart pelvis;
	private final ModelPart core;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart lowerRight;
	private final ModelPart lowerLeft;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final List<ModelPart> glowParts;

	public RedstoneGolemModel(ModelPart root) {
		this.golem = root.getChild("golem");
		this.body = this.golem.getChild("body");
		this.chest = this.body.getChild("chest");
		this.pelvis = this.body.getChild("pelvis");
		this.core = this.body.getChild("core");
		this.head = this.body.getChild("head");
		this.rightArm = this.body.getChild("right_arm");
		this.leftArm = this.body.getChild("left_arm");
		this.lowerRight = this.rightArm.getChild("lower_right");
		this.lowerLeft = this.leftArm.getChild("lower_left");
		this.rightLeg = this.golem.getChild("right_leg");
		this.leftLeg = this.golem.getChild("left_leg");
		this.glowParts = ImmutableList.of(this.body, this.chest, this.pelvis, this.core, this.head, this.rightArm, this.leftArm, this.lowerRight, this.lowerLeft, this.rightLeg, this.leftLeg);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition golem = partdefinition.addOrReplaceChild("golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = golem.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, 2.0F));

		PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, -41.0F, -10.0F, 40.0F, 32.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pelvis = body.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(120, 36).addBox(-11.0F, -9.0F, -7.0F, 22.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition core = body.addOrReplaceChild("core", CubeListBuilder.create().texOffs(49, 90).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 6.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(124, 8).addBox(-8.0F, -8.0F, -12.0F, 16.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -33.0F, -10.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 52).addBox(-14.0F, -8.0F, -6.0F, 14.0F, 24.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-20.0F, -31.0F, 0.0F));

		PartDefinition lower_right = right_arm.addOrReplaceChild("lower_right", CubeListBuilder.create().texOffs(3, 88).addBox(-5.25F, -0.25F, -5.25F, 11.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.75F, 16.25F, -0.75F));

		PartDefinition right_hand = lower_right.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(97, 55).addBox(-4.0F, -3.0F, 2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(97, 55).addBox(-4.0F, -3.0F, -3.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(81, 57).addBox(1.0F, -2.0F, -3.5F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.75F, 21.75F, -1.25F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 52).mirror().addBox(0.0F, -8.0F, -6.0F, 14.0F, 24.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(20.0F, -31.0F, 0.0F));

		PartDefinition lower_left = left_arm.addOrReplaceChild("lower_left", CubeListBuilder.create().texOffs(3, 88).mirror().addBox(-6.5F, -0.25F, -5.25F, 11.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(11.5F, 16.25F, -0.75F));

		PartDefinition left_hand = lower_left.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(97, 55).mirror().addBox(2.0F, -3.0F, 1.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(97, 55).mirror().addBox(2.0F, -3.0F, -4.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(81, 57).mirror().addBox(-3.0F, -2.0F, -4.5F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.5F, 21.75F, -0.25F));

		PartDefinition right_leg = golem.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(113, 58).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, -20.0F, 2.0F));

		PartDefinition left_leg = golem.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(161, 58).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, -20.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isSummoning() && !entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (entity.isDeadOrDying()){
			this.golem.y = 32.0F;
		} else if (entity.isSitting()){
			this.golem.y = 37.0F;
		} else {
			this.golem.y = 24.0F;
		}
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.activateAnimationState, RedstoneGolemAnimations.ACTIVATE, ageInTicks);
		this.animate(entity.idleAnimationState, RedstoneGolemAnimations.IDLE, ageInTicks);
		this.animate(entity.noveltyAnimationState, RedstoneGolemAnimations.NOVELTY, ageInTicks);
		this.animate(entity.walkAnimationState, RedstoneGolemAnimations.WALK, ageInTicks, groundSpeed * 10);
		this.animate(entity.attackAnimationState, RedstoneGolemAnimations.ATTACK, ageInTicks);
		this.animate(entity.summonAnimationState, RedstoneGolemAnimations.SUMMON, ageInTicks);
		this.animate(entity.sitAnimationState, RedstoneGolemAnimations.SIT, ageInTicks);
		this.animate(entity.deathAnimationState, RedstoneGolemAnimations.DEATH, ageInTicks);
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		golem.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.golem;
	}

	public List<ModelPart> getGlowParts() {
		return this.glowParts;
	}
}