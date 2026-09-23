package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.client.render.animation.ImperialGuardAnimations;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.ImperialGuardServant;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Mob;

public class ImperialGuardModel<T extends Mob & IShielded> extends HierarchicalModel<T> implements HeadedModel, HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart guard;
	private final ModelPart upper_body;
	private final ModelPart head;
	private final ModelPart left_arm;
	private final ModelPart shield;

	public ImperialGuardModel(ModelPart root) {
		this.root = root;
		this.guard = root.getChild("guard");
		this.upper_body = this.guard.getChild("upper_body");
		this.head = this.upper_body.getChild("head");
		this.left_arm = this.upper_body.getChild("left_arm");
		this.shield = this.left_arm.getChild("shield");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition guard = partdefinition.addOrReplaceChild("guard", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition upper_body = guard.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = upper_body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(0, 70).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.4F))
				.texOffs(64, 0).addBox(-5.0F, -11.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.25F))
				.texOffs(64, 20).addBox(-6.0F, -4.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition right_arm = upper_body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 30).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F))
				.texOffs(45, 21).addBox(-3.0F, -3.25F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(-6.0F, -8.25F, 0.5F, -0.3333F, 0.3028F, 0.234F));

		PartDefinition right_item = right_arm.addOrReplaceChild("right_item", CubeListBuilder.create(), PartPose.offset(-0.5F, 9.0F, -2.0F));

		PartDefinition right_crossbow = right_item.addOrReplaceChild("right_crossbow", CubeListBuilder.create().texOffs(0, 101).addBox(-15.0F, -15.0F, -1.0F, 17.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -2.3562F));

		PartDefinition bits = right_crossbow.addOrReplaceChild("bits", CubeListBuilder.create().texOffs(0, 99).addBox(5.5F, 4.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 96).addBox(7.5F, 2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(6.5F, 1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(4.5F, 3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(3.5F, 2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(5.5F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(6, 99).addBox(1.5F, 1.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 96).addBox(4.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 94).addBox(3.5F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 94).addBox(0.5F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 99).addBox(1.5F, -3.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 96).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 94).addBox(-1.5F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 94).addBox(-2.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 94).addBox(0.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 94).addBox(1.5F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 94).addBox(2.5F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 94).addBox(-3.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-4.5F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-5.5F, 1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(3.5F, -7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(4.5F, -8.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 96).addBox(5.5F, -10.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 96).addBox(-8.5F, 0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 99).addBox(-7.5F, 2.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 99).addBox(3.5F, -11.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 94).addBox(2.5F, -10.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 94).addBox(-7.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 99).addBox(-0.5F, -9.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(36, 101).addBox(-6.5F, -3.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 94).addBox(-7.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 94).addBox(-1.5F, -10.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 94).addBox(-1.5F, -8.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 94).addBox(-3.5F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 94).addBox(-5.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(32, 99).addBox(-4.5F, -8.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(40, 101).addBox(-5.5F, -7.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-6.5F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-4.5F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-2.5F, -7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 94).addBox(-2.5F, -9.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(28, 94).addBox(3.5F, -9.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(32, 94).addBox(-6.5F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(-5.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(-4.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(-3.5F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(0.5F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 94).addBox(2.5F, -8.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(36, 88).addBox(1.5F, -7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, -3.5F, -0.5F));

		PartDefinition left_arm = upper_body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(44, 30).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(45, 21).mirror().addBox(-0.5F, -2.5F, -2.25F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false), PartPose.offsetAndRotation(5.5F, -9.0F, -1.0F, -1.1345F, 0.0F, 0.0F));

		PartDefinition left_item = left_arm.addOrReplaceChild("left_item", CubeListBuilder.create(), PartPose.offset(0.5F, 9.0F, -2.0F));

		PartDefinition shield = left_arm.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(93, 48).addBox(-7.0F, -10.5F, -0.5F, 14.0F, 22.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(115, 43).addBox(-7.0F, -13.5F, -0.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(115, 43).mirror().addBox(2.0F, -13.5F, -0.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 11.0F, -1.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition right_leg = guard.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(112, 112).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offsetAndRotation(-2.5F, 0.0F, -0.25F, -0.0436F, 0.2618F, 0.0F));

		PartDefinition leg1_r1 = right_leg.addOrReplaceChild("leg1_r1", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, 0.5F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-0.25F, -0.5F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition left_leg = guard.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(112, 112).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(2.0F, 0.0F, 1.0F, 0.0436F, -0.1309F, -0.2182F));

		PartDefinition leg1_r2 = left_leg.addOrReplaceChild("leg1_r2", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(-2.0F, 3.5F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.5F, -3.5F, 0.0F, 0.0F, 0.0F, -0.2182F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (entity instanceof ImperialGuardServant blackguard) {
			this.animate(blackguard.idleAnimationState, ImperialGuardAnimations.IDLE, ageInTicks);
			this.animate(blackguard.standAnimationState, ImperialGuardAnimations.STAND, ageInTicks);
			this.animate(blackguard.preAttackAnimationState, ImperialGuardAnimations.SHOOT_PRE, ageInTicks);
			this.animate(blackguard.attackAnimationState, ImperialGuardAnimations.SHOOT, ageInTicks);
		}
		this.animateWalk(ImperialGuardAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
		this.shield.visible = entity.hasShield();
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void translateToHead(ModelPart part, PoseStack poseStack) {
		this.guard.translateAndRotate(poseStack);
		this.upper_body.translateAndRotate(poseStack);
		part.translateAndRotate(poseStack);
		poseStack.translate(0, 0.0F, 0);
	}

	@Override
	public void translateToChest(ModelPart part, PoseStack poseStack) {
	}

	@Override
	public void translateToLeg(ModelPart part, PoseStack poseStack) {
	}

	@Override
	public void translateToArms(ModelPart part, PoseStack poseStack) {
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}
}