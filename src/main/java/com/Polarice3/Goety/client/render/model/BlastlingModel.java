package com.Polarice3.Goety.client.render.model;


import com.Polarice3.Goety.client.render.animation.BlastlingAnimations;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractBlastling;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;

public class BlastlingModel<T extends AbstractBlastling> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart watchling;
	private final ModelPart body;
	private final ModelPart head;

	public BlastlingModel(ModelPart root) {
		this.root = root;
		this.watchling = root.getChild("watchling");
		this.body = this.watchling.getChild("body");
		this.head = this.body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition watchling = partdefinition.addOrReplaceChild("watchling", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = watchling.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-6.0F, -11.5F, -3.0F, 12.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -25.5F, -2.0F, 0.3054F, -0.0436F, 0.0175F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.5F, 0.0F, -0.2269F, 0.0F, 0.0F));

		PartDefinition flames = head.addOrReplaceChild("flames", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition front = flames.addOrReplaceChild("front", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, -6.0F));

		PartDefinition cube_r1 = front.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-12.0F, -9.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -5.0F, 3.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition back = flames.addOrReplaceChild("back", CubeListBuilder.create().texOffs(96, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.5F));

		PartDefinition left = flames.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(4.5872F, 0.0038F, 1.0F));

		PartDefinition cube_r2 = left.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(96, -16).addBox(-0.6972F, -15.9696F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition right = flames.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offset(-4.5872F, 0.0038F, 1.0F));

		PartDefinition cube_r3 = right.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(96, -16).addBox(0.6972F, -15.9696F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition top = flames.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(0.0F, -8.5F, -1.0F));

		PartDefinition cube_r4 = top.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(80, 0).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition main_eyes = head.addOrReplaceChild("main_eyes", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 1).addBox(1.0F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, -6.025F));

		PartDefinition eyes = main_eyes.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0F, 3.5F, 6.025F));

		PartDefinition right_eye = eyes.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.05F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_eye = eyes.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.05F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(42, 22).addBox(-6.0F, -3.0F, -4.0F, 6.0F, 35.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -8.5F, 2.0F, -0.3491F, 0.0436F, -0.0262F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(68, 22).addBox(0.0F, -3.0F, -4.0F, 6.0F, 35.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -8.5F, 2.0F, -0.3927F, 0.0524F, -0.0087F));

		PartDefinition right_leg = watchling.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.0262F));

		PartDefinition left_leg = watchling.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -24.0F, 0.0F, -0.0436F, 0.0F, -0.0436F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		AnimationState animationState = new AnimationState();
		animationState.start(entity.tickCount);
		this.animate(animationState, BlastlingAnimations.SCALE, ageInTicks);
		this.animate(entity.idleAnimationState, BlastlingAnimations.CUSTOM_IDLE, ageInTicks);
		this.animateWalk(BlastlingAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		this.animate(entity.preAttackAnimationState, BlastlingAnimations.PRE_ATTACK, ageInTicks);
		this.animate(entity.attackAnimationState, BlastlingAnimations.SHOT, ageInTicks);
		this.animate(entity.postAttackAnimationState, BlastlingAnimations.POST_ATTACK, ageInTicks);
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