package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.IceGolemAnimations;
import com.Polarice3.Goety.common.entities.ally.IceGolem;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class IceGolemModel<T extends IceGolem> extends HierarchicalModel<T> {
	private final ModelPart root;
    private final ModelPart head;

	public IceGolemModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("body").getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(45, 0).mirror().addBox(8.0F, -8.0F, -5.0F, 7.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(45, 0).addBox(-15.0F, -8.0F, -5.0F, 7.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 14).addBox(-8.0F, -9.0F, -7.0F, 16.0F, 20.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(58, 36).addBox(-4.5F, 11.0F, -3.0F, 9.0F, 4.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -7.0F));

		PartDefinition frost2 = head.addOrReplaceChild("frost2", CubeListBuilder.create().texOffs(45, 50).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition frost = body.addOrReplaceChild("frost", CubeListBuilder.create().texOffs(96, 51).addBox(-15.0F, -2.0F, -5.0F, 7.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(96, 51).mirror().addBox(8.0F, -2.0F, -5.0F, 7.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 47).addBox(-8.0F, -3.0F, -7.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-12.0F, -7.0F, 0.0F));

		PartDefinition upper_right = right_arm.addOrReplaceChild("upper_right", CubeListBuilder.create().texOffs(88, 24).mirror().addBox(-2.0F, -2.0F, -3.0F, 4.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 1.5F, 0.0F));

		PartDefinition bottom_right = right_arm.addOrReplaceChild("bottom_right", CubeListBuilder.create().texOffs(95, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 13.5F, 0.0F));

		PartDefinition frost3 = bottom_right.addOrReplaceChild("frost3", CubeListBuilder.create().texOffs(73, 48).mirror().addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(12.0F, -7.0F, 0.0F));

		PartDefinition upper_left = left_arm.addOrReplaceChild("upper_left", CubeListBuilder.create().texOffs(88, 24).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));

		PartDefinition bottom_left = left_arm.addOrReplaceChild("bottom_left", CubeListBuilder.create().texOffs(95, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.5F, 0.0F));

		PartDefinition frost4 = bottom_left.addOrReplaceChild("frost4", CubeListBuilder.create().texOffs(73, 48).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, 8.0F, 0.0F));

		PartDefinition upper_right_leg = right_leg.addOrReplaceChild("upper_right_leg", CubeListBuilder.create().texOffs(77, 0).mirror().addBox(-2.5F, 0.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lower_right_leg = right_leg.addOrReplaceChild("lower_right_leg", CubeListBuilder.create().texOffs(58, 19).mirror().addBox(-3.0F, 0.0F, -3.5F, 6.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-0.5F, 6.0F, -0.5F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(4.0F, 8.0F, 0.0F));

		PartDefinition upper_left_leg = left_leg.addOrReplaceChild("upper_left_leg", CubeListBuilder.create().texOffs(77, 0).addBox(-1.5F, 0.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lower_left_leg = left_leg.addOrReplaceChild("lower_left_leg", CubeListBuilder.create().texOffs(58, 19).addBox(-3.0F, 0.0F, -3.5F, 6.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 6.0F, -0.5F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}

		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.idleAnimationState, IceGolemAnimations.IDLE, ageInTicks);
		this.animate(entity.walkAnimationState, IceGolemAnimations.WALK, ageInTicks, groundSpeed * 25);
		this.animate(entity.attackAnimationState, IceGolemAnimations.ATTACK, ageInTicks);
		this.animate(entity.smashAnimationState, IceGolemAnimations.SMASH, ageInTicks);
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