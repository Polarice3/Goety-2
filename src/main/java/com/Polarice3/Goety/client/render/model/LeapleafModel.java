package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.LeapleafAnimations;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.utils.ModModelUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LeapleafModel<T extends Leapleaf> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;

	public LeapleafModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("body").getChild("main").getChild("chest").getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.4975F, 15.0F, 19.6288F));

		PartDefinition main = body.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 0.0F));

		PartDefinition chest = main.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 27).addBox(-10.0F, -18.0F, -7.0F, 19.0F, 18.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(164, 0).addBox(-10.0F, -18.0F, -7.0F, 19.0F, 18.0F, 15.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -9.0F, 0.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(-19, 0).addBox(-14.0F, -1.0F, -12.5F, 27.0F, 0.0F, 27.0F, new CubeDeformation(0.0F))
				.texOffs(42, 102).addBox(-15.5F, 0.0F, -12.0F, 30.0F, 0.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition petal_s = head.addOrReplaceChild("petal_s", CubeListBuilder.create().texOffs(84, 59).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.5F, 7.0F, 1.8326F, 0.0F, 0.0F));

		PartDefinition inner_s = petal_s.addOrReplaceChild("inner_s", CubeListBuilder.create().texOffs(87, 53).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 9.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition petal_n = head.addOrReplaceChild("petal_n", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -1.5F, -5.0F, -1.8326F, 0.0F, 0.0F));

		PartDefinition cube_r1 = petal_n.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(84, 59).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -3.1416F));

		PartDefinition inner_n = petal_n.addOrReplaceChild("inner_n", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r2 = inner_n.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(87, 53).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -3.1416F));

		PartDefinition petal_e = head.addOrReplaceChild("petal_e", CubeListBuilder.create(), PartPose.offsetAndRotation(5.5F, -1.5F, 1.0F, 0.0F, 0.0F, -1.8326F));

		PartDefinition cube_r3 = petal_e.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(84, 59).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition inner_e = petal_e.addOrReplaceChild("inner_e", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition cube_r4 = inner_e.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(87, 53).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition petal_w = head.addOrReplaceChild("petal_w", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.5F, -1.5F, 1.0F, 0.0F, 0.0F, 1.8326F));

		PartDefinition cube_r5 = petal_w.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(84, 59).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition inner_w = petal_w.addOrReplaceChild("inner_w", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition cube_r6 = inner_w.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(87, 53).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition pelvis = main.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(44, 60).addBox(-7.0F, -15.0F, -5.0F, 13.0F, 15.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(128, 72).addBox(-7.0F, -15.0F, -5.0F, 13.0F, 15.0F, 11.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 60).addBox(-11.0F, -3.0F, -6.0F, 11.0F, 18.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(117, 0).addBox(-11.0F, -3.0F, -6.0F, 11.0F, 18.0F, 11.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(-10.0F, -15.0F, -19.0F, -0.2182F, 0.0F, 0.1745F));

		PartDefinition right_wrist = right_arm.addOrReplaceChild("right_wrist", CubeListBuilder.create().texOffs(62, 0).addBox(-7.0F, -1.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(128, 29).addBox(-7.0F, -1.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(-0.5F))
				.texOffs(158, 33).addBox(-10.0F, 11.0F, -9.0F, 18.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 16.0F, -6.0F, 0.2182F, 0.0436F, -0.1745F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(84, 24).addBox(-1.0F, -3.0F, -6.0F, 11.0F, 18.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(117, 0).addBox(-1.0F, -3.0F, -6.0F, 11.0F, 18.0F, 11.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(10.0F, -15.0F, -19.0F, -0.2182F, 0.0F, -0.1745F));

		PartDefinition left_wrist = left_arm.addOrReplaceChild("left_wrist", CubeListBuilder.create().texOffs(80, 77).addBox(-6.0F, -1.0F, -7.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(128, 29).mirror().addBox(-6.0F, -1.0F, -7.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(-0.5F)).mirror(false)
				.texOffs(158, 51).addBox(-9.0F, 11.0F, -10.0F, 18.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 16.0F, -6.0F, 0.2182F, -0.0436F, 0.1745F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 89).addBox(-8.0F, -1.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(128, 53).addBox(-8.0F, -1.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(-0.5F)), PartPose.offset(-5.5025F, 16.0F, 19.6288F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 108).addBox(-3.0F, -1.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(128, 53).addBox(-3.0F, -1.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(-0.5F)), PartPose.offset(6.4975F, 16.0F, 19.6288F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying() && !entity.isResting()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (entity.canAnimateMove() && entity.isMoving()) {
			ModModelUtils.animateWalk(this, LeapleafAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
		} else {
			this.animate(entity.idleAnimationState, LeapleafAnimations.IDLE, ageInTicks);
		}
		this.animate(entity.smashAnimationState, LeapleafAnimations.SMASH, ageInTicks);
		this.animate(entity.chargeAnimationState, LeapleafAnimations.CHARGE, ageInTicks);
		this.animate(entity.leapAnimationState, LeapleafAnimations.LEAP, ageInTicks);
		this.animate(entity.restAnimationState, LeapleafAnimations.REST, ageInTicks);
		this.animate(entity.alertAnimationState, LeapleafAnimations.ALERT, ageInTicks);
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