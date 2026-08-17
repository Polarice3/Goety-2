package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.GraveGolemAnimations;
import com.Polarice3.Goety.common.entities.ally.undead.GraveGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class GraveGolemModel<T extends GraveGolem> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart golem;
	private final ModelPart upper;
	private final ModelPart body;
	private final ModelPart upper_body;
	private final ModelPart bars;
	private final ModelPart head;
	private final ModelPart rotated_head;
	private final ModelPart right_arm;
	private final ModelPart right_shoulder;
	private final ModelPart statue;
	private final ModelPart bone22;
	private final ModelPart bone23;
	private final ModelPart bone24;
	private final ModelPart bone25;
	private final ModelPart bone;
	private final ModelPart right_middle;
	private final ModelPart right_hand;
	private final ModelPart left_arm;
	private final ModelPart left_shoulder;
	private final ModelPart left_middle;
	private final ModelPart left_fist;
	private final ModelPart pelvis;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public GraveGolemModel(ModelPart root) {
		this.root = root;
		this.golem = root.getChild("golem");
		this.upper = this.golem.getChild("upper");
		this.body = this.upper.getChild("body");
		this.upper_body = this.body.getChild("upper_body");
		this.bars = this.upper_body.getChild("bars");
		this.head = this.upper_body.getChild("head");
		this.rotated_head = this.head.getChild("rotated_head");
		this.right_arm = this.upper.getChild("right_arm");
		this.right_shoulder = this.right_arm.getChild("right_shoulder");
		this.statue = this.right_shoulder.getChild("statue");
		this.bone22 = this.statue.getChild("bone22");
		this.bone23 = this.bone22.getChild("bone23");
		this.bone24 = this.statue.getChild("bone24");
		this.bone25 = this.bone24.getChild("bone25");
		this.bone = this.statue.getChild("bone");
		this.right_middle = this.right_shoulder.getChild("right_middle");
		this.right_hand = this.right_middle.getChild("right_hand");
		this.left_arm = this.upper.getChild("left_arm");
		this.left_shoulder = this.left_arm.getChild("left_shoulder");
		this.left_middle = this.left_shoulder.getChild("left_middle");
		this.left_fist = this.left_middle.getChild("left_fist");
		this.pelvis = this.golem.getChild("pelvis");
		this.right_leg = this.golem.getChild("right_leg");
		this.left_leg = this.golem.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition golem = partdefinition.addOrReplaceChild("golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upper = golem.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -25.5F, 0.0F));

		PartDefinition body = upper.addOrReplaceChild("body", CubeListBuilder.create().texOffs(96, 0).addBox(-7.5F, -9.0F, -8.0F, 16.0F, 18.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.5F, 0.0F));

		PartDefinition upper_body = body.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -8.1667F, -9.0F, 24.0F, 18.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(0, 88).addBox(-16.0F, -10.1667F, -9.0F, 4.0F, 20.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(64, 68).addBox(-16.0F, -10.1667F, -13.0F, 32.0F, 20.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(172, 46).addBox(-4.0F, -8.1667F, -17.0F, 8.0F, 16.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(64, 92).addBox(-16.0F, -10.1667F, 15.0F, 32.0F, 20.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 88).mirror().addBox(12.0F, -10.1667F, -9.0F, 4.0F, 20.0F, 24.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -12.8333F, -3.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition bars = upper_body.addOrReplaceChild("bars", CubeListBuilder.create().texOffs(104, 128).addBox(-14.5F, -63.0F, -12.0F, 30.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(104, 128).addBox(-14.5F, -63.0F, 16.0F, 30.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 48.8333F, 1.0F));

		PartDefinition cube_r1 = bars.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(104, 128).addBox(-15.0F, -2.0F, 0.0F, 30.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.5F, -61.0F, 2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = bars.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(104, 128).addBox(-15.0F, -2.0F, 0.0F, 30.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.5F, -61.0F, 2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-0.5F, -8.9167F, 1.0F));

		PartDefinition rotated_head = head.addOrReplaceChild("rotated_head", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -4.25F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(72, 13).addBox(-2.0F, 0.75F, -4.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, -30.0F, 0.0F));

		PartDefinition right_shoulder = right_arm.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(0, 42).addBox(-14.5F, -21.75F, -8.0F, 16.0F, 30.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-17.0F, 2.25F, 0.0F));

		PartDefinition statue = right_shoulder.addOrReplaceChild("statue", CubeListBuilder.create(), PartPose.offsetAndRotation(-10.0F, 4.25F, 0.5F, 0.0F, 1.5708F, -0.3491F));

		PartDefinition bone22 = statue.addOrReplaceChild("bone22", CubeListBuilder.create().texOffs(160, 0).addBox(-32.5F, -77.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(184, 2).addBox(-29.5F, -70.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(29.0F, 49.0F, 0.0F));

		PartDefinition bone23 = bone22.addOrReplaceChild("bone23", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone24 = statue.addOrReplaceChild("bone24", CubeListBuilder.create().texOffs(160, 18).addBox(-32.5F, -67.0F, -3.0F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(160, 28).addBox(-32.5F, -63.0F, -3.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(29.0F, 49.0F, 0.0F));

		PartDefinition bone25 = bone24.addOrReplaceChild("bone25", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arms_r1 = bone25.addOrReplaceChild("arms_r1", CubeListBuilder.create().texOffs(96, 34).addBox(-4.0F, -2.0F, -3.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(126, 46).addBox(-7.9F, -2.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(126, 34).addBox(3.9F, -2.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-28.5F, -62.0F, -3.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone = statue.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(192, 0).addBox(-35.5F, -55.0F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(29.0F, 49.0F, 0.0F));

		PartDefinition right_middle = right_shoulder.addOrReplaceChild("right_middle", CubeListBuilder.create().texOffs(136, 66).addBox(-3.0F, -1.5F, -6.5F, 12.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.5F, 9.75F, 0.0F));

		PartDefinition right_hand = right_middle.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(56, 116).addBox(-32.5F, -1.0F, -8.5F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(27.5F, 13.5F, 0.0F));

		PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, -30.0F, 1.0F));

		PartDefinition left_shoulder = left_arm.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(96, 148).addBox(16.5F, -6.75F, -9.0F, 14.0F, 24.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(64, 180).addBox(18.5F, -14.75F, -8.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(76, 180).addBox(21.5F, -10.75F, -3.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.75F, 0.0F));

		PartDefinition left_middle = left_shoulder.addOrReplaceChild("left_middle", CubeListBuilder.create().texOffs(0, 132).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(26.5F, 18.25F, -3.5F));

		PartDefinition left_fist = left_middle.addOrReplaceChild("left_fist", CubeListBuilder.create().texOffs(0, 166).addBox(18.5F, -1.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(66, 184).addBox(22.5F, 3.0F, -12.0F, 8.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(-26.5F, 22.0F, 0.0F));

		PartDefinition cube_r3 = left_fist.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(66, 184).addBox(-4.0F, -4.0F, -12.0F, 8.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.5F, 7.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r4 = left_fist.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(66, 184).addBox(-4.0F, -4.0F, -12.0F, 8.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(26.5F, 7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition pelvis = golem.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(64, 42).addBox(-10.5F, -4.25F, -10.5F, 22.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -23.25F, 1.0F));

		PartDefinition right_leg = golem.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(48, 148).mirror().addBox(-14.0F, 2.5F, -7.5F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -22.5F, 2.0F));

		PartDefinition left_leg = golem.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(48, 148).addBox(2.0F, 2.5F, -7.5F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.5F, 2.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.activateAnimationState, GraveGolemAnimations.AWAKE, ageInTicks);
		this.animate(entity.idleAnimationState, GraveGolemAnimations.IDLE, ageInTicks);
		if (entity.canAnimateMove()) {
			this.animateWalk(GraveGolemAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.attackAnimationState, GraveGolemAnimations.SMASH, ageInTicks);
		this.animate(entity.summonAnimationState, GraveGolemAnimations.SUMMON, ageInTicks);
		this.animate(entity.sitAnimationState, GraveGolemAnimations.SIT, ageInTicks);
		this.animate(entity.toSitAnimationState, GraveGolemAnimations.TO_SIT, ageInTicks);
		this.animate(entity.toStandAnimationState, GraveGolemAnimations.TO_STAND, ageInTicks);
		this.animate(entity.shootAnimationState, GraveGolemAnimations.SHOOT, ageInTicks);
		this.animate(entity.deathAnimationState, GraveGolemAnimations.DEATH, ageInTicks);
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