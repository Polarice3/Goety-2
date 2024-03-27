package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.WhispererAnimations;
import com.Polarice3.Goety.common.entities.ally.Whisperer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WhispererModel<T extends Whisperer> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart whisperer;
	private final ModelPart head;

	public WhispererModel(ModelPart root) {
		this.root = root;
		this.whisperer = root.getChild("whisperer");
		this.head = this.whisperer.getChild("top_part").getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition whisperer = partdefinition.addOrReplaceChild("whisperer", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition top_part = whisperer.addOrReplaceChild("top_part", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition head = top_part.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

		PartDefinition lower_jaw = head.addOrReplaceChild("lower_jaw", CubeListBuilder.create().texOffs(25, 19).addBox(-5.5F, -6.0F, -10.0F, 11.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(32, 45).addBox(-5.5F, -1.0F, -10.0F, 11.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.5F));

		PartDefinition frill = lower_jaw.addOrReplaceChild("frill", CubeListBuilder.create().texOffs(9, 0).addBox(-9.5F, -27.0F, -9.0F, 19.0F, 0.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 28.0F, -4.5F));

		PartDefinition top_jaw = head.addOrReplaceChild("top_jaw", CubeListBuilder.create().texOffs(0, 35).addBox(-5.5F, -8.0F, -10.0F, 11.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(32, 45).addBox(-5.5F, -6.0F, -10.0F, 11.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.5F));

		PartDefinition flower = top_jaw.addOrReplaceChild("flower", CubeListBuilder.create(), PartPose.offset(0.0F, 28.0F, -4.5F));

		PartDefinition cube_r1 = flower.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 56).addBox(-2.5F, -4.0F, 0.0F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -36.0F, 1.5F, 0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r2 = flower.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 56).addBox(-2.5F, -4.0F, 0.0F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -36.0F, -2.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r3 = flower.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(50, 56).addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -36.0F, -0.5F, 0.0F, 0.0F, -0.5236F));

		PartDefinition cube_r4 = flower.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(42, 56).addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -36.0F, -0.5F, 0.0F, 0.0F, 0.5236F));

		PartDefinition body = top_part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -28.0F, -1.5F, 9.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(85, 48).addBox(-3.5F, -28.0F, -0.5F, 7.0F, 14.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 14.0F, 0.0F));

		PartDefinition right_arm = top_part.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -12.0F, 0.5F));

		PartDefinition upper_right = right_arm.addOrReplaceChild("upper_right", CubeListBuilder.create().texOffs(61, 43).addBox(-5.0F, 11.5F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(90, -6).addBox(-1.0F, -0.5F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(102, -6).addBox(-4.0F, -0.5F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(77, 48).addBox(-5.0F, -0.5F, -2.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(93, 13).mirror().addBox(-5.0F, -0.5F, 2.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(51, 19).addBox(-5.0F, 0.5F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -0.5F, 0.0F));

		PartDefinition bottom_right = right_arm.addOrReplaceChild("bottom_right", CubeListBuilder.create().texOffs(101, 13).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(97, 26).mirror().addBox(-2.5F, 0.0F, 2.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(101, 31).mirror().addBox(-1.5F, 0.0F, -3.0F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(107, 20).mirror().addBox(1.5F, 0.0F, -3.0F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(61, 55).mirror().addBox(-2.5F, 1.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(61, 49).mirror().addBox(-2.5F, 10.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, 12.0F, 0.0F));

		PartDefinition left_arm = top_part.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -12.0F, 0.5F));

		PartDefinition upper_left = left_arm.addOrReplaceChild("upper_left", CubeListBuilder.create().texOffs(61, 19).addBox(0.0F, 0.5F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(61, 25).addBox(0.0F, 11.5F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(66, -6).addBox(4.0F, -0.5F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(78, -6).addBox(1.0F, -0.5F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(77, 13).addBox(1.0F, -0.5F, -2.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(85, 13).mirror().addBox(1.0F, -0.5F, 2.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, -0.5F, 0.0F));

		PartDefinition bottom_left = left_arm.addOrReplaceChild("bottom_left", CubeListBuilder.create().texOffs(77, 26).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(87, 26).addBox(-2.5F, 0.0F, 2.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(77, 31).addBox(1.5F, 0.0F, -3.0F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(89, 31).addBox(-1.5F, 0.0F, -3.0F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(61, 31).addBox(-2.5F, 1.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(61, 37).addBox(-2.5F, 10.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 12.0F, 0.0F));

		PartDefinition right_leg = whisperer.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(13, 18).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.5F));

		PartDefinition right_foot = right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create(), PartPose.offset(3.5F, 14.3F, -0.5F));

		PartDefinition cube_r5 = right_foot.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(21, 1).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.5F, 0.0F, -1.5708F, 0.7854F));

		PartDefinition cube_r6 = right_foot.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(21, 2).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.0F, 0.5F, 0.0F, -1.5708F, -0.7854F));

		PartDefinition cube_r7 = right_foot.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(21, 3).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r8 = right_foot.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(21, 18).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition left_leg = whisperer.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(1, 18).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.5F));

		PartDefinition left_foot = left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create(), PartPose.offset(3.5F, 14.3F, -0.5F));

		PartDefinition cube_r9 = left_foot.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(21, 19).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.5F, 0.0F, -1.5708F, 0.7854F));

		PartDefinition cube_r10 = left_foot.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(9, 18).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.0F, 0.5F, 0.0F, -1.5708F, -0.7854F));

		PartDefinition cube_r11 = left_foot.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(9, 19).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r12 = left_foot.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(9, 20).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying() && !entity.isSummoning()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.idleAnimationState, WhispererAnimations.IDLE, ageInTicks);
		this.animate(entity.walkAnimationState, WhispererAnimations.WALK, ageInTicks, groundSpeed * 10);
		this.animate(entity.attackAnimationState, WhispererAnimations.ATTACK, ageInTicks);
		this.animate(entity.summonAnimationState, WhispererAnimations.SUMMON, ageInTicks);
		this.animate(entity.summonPoisonAnimationState, WhispererAnimations.SUMMON_POISON, ageInTicks);
		this.animate(entity.summonThornsAnimationState, WhispererAnimations.SUMMON_THORN, ageInTicks);
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