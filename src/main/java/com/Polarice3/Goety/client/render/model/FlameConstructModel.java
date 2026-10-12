package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.FlameConstructAnimations;
import com.Polarice3.Goety.common.entities.ally.golem.FlameConstruct;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;

public class FlameConstructModel<T extends Mob> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart right_eye;
	private final ModelPart right_iris;
	private final ModelPart left_eye;
	private final ModelPart left_iris;
	private final ModelPart mouth;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart fire;
	private final ModelPart middle;
	private final ModelPart right_front_leg;
	private final ModelPart right_back_leg;
	private final ModelPart left_front_leg;
	private final ModelPart left_back_leg;

	public FlameConstructModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.right_eye = this.body.getChild("right_eye");
		this.right_iris = this.right_eye.getChild("right_iris");
		this.left_eye = this.body.getChild("left_eye");
		this.left_iris = this.left_eye.getChild("left_iris");
		this.mouth = this.body.getChild("mouth");
		this.head = this.body.getChild("head");
		this.hat = this.body.getChild("hat");
		this.fire = this.hat.getChild("fire");
		this.middle = this.fire.getChild("middle");
		this.right_front_leg = root.getChild("right_front_leg");
		this.right_back_leg = root.getChild("right_back_leg");
		this.left_front_leg = root.getChild("left_front_leg");
		this.left_back_leg = root.getChild("left_back_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 46).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition right_eye = body.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 1).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -7.0F, -5.025F));

		PartDefinition right_iris = right_eye.addOrReplaceChild("right_iris", CubeListBuilder.create().texOffs(2, 0).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.025F));

		PartDefinition left_eye = body.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 1).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -7.0F, -5.025F));

		PartDefinition left_iris = left_eye.addOrReplaceChild("left_iris", CubeListBuilder.create().texOffs(2, 0).mirror().addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, -0.025F));

		PartDefinition mouth = body.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -5.025F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 8).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition hat = body.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.01F, 0.0F));

		PartDefinition fire = hat.addOrReplaceChild("fire", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.0F, -8.0F, 8.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = fire.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -8.0F, 15.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.0F, -8.0F, -1.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition middle = fire.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -16.0F, -4.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.0F, -16.0F, 4.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r2 = middle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -8.0F, 15.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = middle.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -8.0F, -1.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 22.0F, -5.0F));

		PartDefinition right_back_leg = partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 22.0F, 5.0F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 22.0F, -5.0F));

		PartDefinition left_back_leg = partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 22.0F, 5.0F));

		return LayerDefinition.create(meshdefinition, 96, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		AnimationState animationState = new AnimationState();
		animationState.start(entity.tickCount);
		this.animate(animationState, SCALE, ageInTicks);
		if (entity instanceof FlameConstruct construct) {
			if (construct.getAllAnimations().stream().noneMatch(AnimationState::isStarted) && !construct.walkAnimation.isMoving()) {
				AnimationState fallback = new AnimationState();
				fallback.start(0);
				this.animate(fallback, construct.isHunkered() ? FlameConstructAnimations.HIDDEN_IDLE : FlameConstructAnimations.IDLE, ageInTicks);
			}
			if (!construct.isCurrentAnimation(FlameConstruct.BLAST)) {
				this.animateWalk(FlameConstructAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
			this.animate(construct.idleAnimationState, construct.isHunkered() ? FlameConstructAnimations.HIDDEN_IDLE : FlameConstructAnimations.IDLE, ageInTicks);
			this.animate(construct.breathAnimationState, FlameConstructAnimations.BREATH, ageInTicks);
			this.animate(construct.blastAnimationState, FlameConstructAnimations.BLAST, ageInTicks);
			this.animate(construct.hunkerDownAnimationState, FlameConstructAnimations.HUNKER_DOWN, ageInTicks);
			this.animate(construct.boredAnimationState, FlameConstructAnimations.BORED, ageInTicks);
			this.animate(construct.peekAnimationState, FlameConstructAnimations.PEEK, ageInTicks);
			this.animate(construct.standUpAnimationState, FlameConstructAnimations.STAND_UP, ageInTicks);
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition SCALE = AnimationDefinition.Builder.withLength(0.0F).looping()
			.addAnimation("fire", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("fire", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.7F, 0.7F, 0.7F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("middle", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.5F, 1.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}