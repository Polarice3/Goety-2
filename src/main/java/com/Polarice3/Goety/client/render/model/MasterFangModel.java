package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.MasterFangs;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MasterFangModel<T extends MasterFangs> extends HierarchicalModel<T> {
	private final ModelPart root;

	public MasterFangModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition teeth2 = base.addOrReplaceChild("teeth2", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 2.4972F));

		PartDefinition cube_r1 = teeth2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -3.9972F, 4.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, 3.1416F, -1.5708F, 0.0F));

		PartDefinition teeth1 = base.addOrReplaceChild("teeth1", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -2.5428F));

		PartDefinition cube_r2 = teeth1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, 0.0F, -3.9572F, 4.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.5708F, 3.1416F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.attackAnimationState, ATTACK, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(1.65F)
			.addAnimation("base", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, -0.0004F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.7F, KeyframeAnimations.degreeVec(0.0F, 0.4733F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.9247F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, -2.8532F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.85F, KeyframeAnimations.degreeVec(0.0F, 3.1584F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 4.7511F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, 5.2505F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -0.0019F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("base", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -26.8561F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.05F, KeyframeAnimations.posVec(0.0F, -3.4524F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.2F, KeyframeAnimations.posVec(0.0F, 3.5518F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 2.2867F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.posVec(0.0F, -0.0007F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.35F, KeyframeAnimations.posVec(0.0F, 3.8639F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.65F, KeyframeAnimations.posVec(0.0F, -26.8561F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("teeth2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-114.4718F, 0.0525F, -0.0815F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.15F, KeyframeAnimations.degreeVec(-111.5278F, 0.0386F, -0.0567F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(-105.349F, 0.0449F, -0.0589F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.7F, KeyframeAnimations.degreeVec(-114.7399F, 0.0477F, -0.0745F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(-103.3546F, 0.0521F, -0.0658F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.8F, KeyframeAnimations.degreeVec(-107.8768F, 0.0165F, -0.0226F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.85F, KeyframeAnimations.degreeVec(-115.7989F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(-116.2782F, -0.0256F, 0.0412F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.95F, KeyframeAnimations.degreeVec(-107.3295F, 0.0497F, -0.0676F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(-120.7894F, -0.0309F, 0.0544F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.05F, KeyframeAnimations.degreeVec(2.6584F, 0.043F, 0.001F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.1F, KeyframeAnimations.degreeVec(1.2431F, -0.0116F, -0.0001F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.15F, KeyframeAnimations.degreeVec(-1.5505F, 0.1296F, -0.0017F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.degreeVec(-1.8116F, 0.1018F, -0.0016F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(-0.9059F, 0.0858F, -0.0007F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(0.0F, 0.1532F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("teeth1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(114.9901F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.15F, KeyframeAnimations.degreeVec(111.9997F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(105.5619F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.7F, KeyframeAnimations.degreeVec(115.0077F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(103.7351F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.8F, KeyframeAnimations.degreeVec(108.2303F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.85F, KeyframeAnimations.degreeVec(116.1356F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(116.7062F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.95F, KeyframeAnimations.degreeVec(107.719F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(121.2461F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.05F, KeyframeAnimations.degreeVec(-2.6583F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.1F, KeyframeAnimations.degreeVec(-1.2552F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.15F, KeyframeAnimations.degreeVec(1.5307F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.degreeVec(1.8031F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.8952F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(-0.0125F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
}