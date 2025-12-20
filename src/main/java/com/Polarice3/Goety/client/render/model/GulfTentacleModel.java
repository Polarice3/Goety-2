package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class GulfTentacleModel<T extends GulfTentacle> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart tentacle;
	private final ModelPart tentacle_mid;
	private final ModelPart tentacle_tip;

	public GulfTentacleModel(ModelPart root) {
		this.root = root;
		this.tentacle = root.getChild("tentacle");
		this.tentacle_mid = this.tentacle.getChild("tentacle_mid");
		this.tentacle_tip = this.tentacle_mid.getChild("tentacle_tip");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition tentacle = partdefinition.addOrReplaceChild("tentacle", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -16.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 22.0F, 8.0F));

		PartDefinition tentacle_mid = tentacle.addOrReplaceChild("tentacle_mid", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -2.0F, -16.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, -16.0F));

		PartDefinition tentacle_tip = tentacle_mid.addOrReplaceChild("tentacle_tip", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.0F, -16.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -16.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.tentacle.xRot = headPitch * (float) (Math.PI / 180F);
		this.tentacle.yRot = netHeadYaw * (float) (Math.PI / 180F);
		this.animate(entity.spawnAnimationState, ATTACK, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(1.0F)
			.addAnimation("tentacle", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-125.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("tentacle", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.scaleVec(0.5F, 0.5F, 0.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.45F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.7F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("tentacle_mid", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(152.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("tentacle_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-157.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
}