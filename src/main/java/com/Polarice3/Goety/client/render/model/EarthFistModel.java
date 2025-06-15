package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.EarthFist;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class EarthFistModel<T extends EarthFist> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart fist;

	public EarthFistModel(ModelPart root) {
		this.root = root;
		this.fist = root.getChild("fist");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fist = partdefinition.addOrReplaceChild("fist", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -32.0F, -12.0F, 24.0F, 32.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(-12.0F, -40.0F, -4.0F, 24.0F, 8.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 80).addBox(-4.0F, -40.0F, -12.0F, 16.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 96, 96);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.attackAnimationState, ATTACK, ageInTicks);
		this.animate(entity.retreatAnimationState, REMOVE, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(0.4F)
			.addAnimation("fist", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -40.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("fist", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition REMOVE = AnimationDefinition.Builder.withLength(0.4F)
			.addAnimation("fist", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F, -40.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("fist", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.2F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.4F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}