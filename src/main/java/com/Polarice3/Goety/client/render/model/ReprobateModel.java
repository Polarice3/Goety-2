package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.ReprobateAnimations;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.ReprobateServant;
import com.Polarice3.Goety.common.entities.hostile.cultists.Reprobate;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

public class ReprobateModel<T extends Entity> extends HierarchicalModel<T> implements ArmedModel {
	private final ModelPart root;
	private final ModelPart warlock;
	private final ModelPart upperBody;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart hat;
	private final ModelPart hat1;
	private final ModelPart body;
	private final ModelPart rightarm;
	private final ModelPart right_hand;
	private final ModelPart fragile;
	private final ModelPart barrel;
	private final ModelPart bottle_case;
	private final ModelPart bottle1;
	private final ModelPart bottle2;
	private final ModelPart bottle3;
	private final ModelPart bottle4;
	private final ModelPart bottle5;
	private final ModelPart bottle6;
	private final ModelPart leftarm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public ReprobateModel(ModelPart root) {
		this.root = root;
		this.warlock = root.getChild("warlock");
		this.upperBody = this.warlock.getChild("upperBody");
		this.head = this.upperBody.getChild("head");
		this.nose = this.head.getChild("nose");
		this.hat = this.head.getChild("hat");
		this.hat1 = this.hat.getChild("hat1");
		this.body = this.upperBody.getChild("body");
		this.rightarm = this.upperBody.getChild("rightarm");
		this.right_hand = this.rightarm.getChild("right_hand");
		this.fragile = this.rightarm.getChild("fragile");
		this.barrel = this.fragile.getChild("barrel");
		this.bottle_case = this.fragile.getChild("bottle_case");
		this.bottle1 = this.bottle_case.getChild("bottle1");
		this.bottle2 = this.bottle_case.getChild("bottle2");
		this.bottle3 = this.bottle_case.getChild("bottle3");
		this.bottle4 = this.bottle_case.getChild("bottle4");
		this.bottle5 = this.bottle_case.getChild("bottle5");
		this.bottle6 = this.bottle_case.getChild("bottle6");
		this.leftarm = this.upperBody.getChild("leftarm");
		this.rightLeg = this.warlock.getChild("rightLeg");
		this.leftLeg = this.warlock.getChild("leftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition warlock = partdefinition.addOrReplaceChild("warlock", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upperBody = warlock.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition head = upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, -12.0F, 0.0F, 0.0785F, 0.0F, 0.0175F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.0F, 2.0F, -2.9F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.5F, -10.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat1 = hat.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(59, 2).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.5F, 4.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition body = upperBody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0436F, 0.0873F, 0.0F));

		PartDefinition rightarm = upperBody.addOrReplaceChild("rightarm", CubeListBuilder.create(), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition right_hand = rightarm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, 1.0F, -0.2F, -3.1416F, 0.6458F, -0.9599F));

		PartDefinition rightArm_r1 = right_hand.addOrReplaceChild("rightArm_r1", CubeListBuilder.create().texOffs(36, 38).addBox(-3.0F, -24.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-1.0F, 21.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition fragile = rightarm.addOrReplaceChild("fragile", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -8.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition barrel = fragile.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(80, 38).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 14.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(88, 26).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(86, 32).addBox(-1.5F, -10.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = barrel.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(88, 26).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition bottle_case = fragile.addOrReplaceChild("bottle_case", CubeListBuilder.create().texOffs(88, 0).addBox(-4.0F, -2.0F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottle1 = bottle_case.addOrReplaceChild("bottle1", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition bottle2 = bottle_case.addOrReplaceChild("bottle2", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(4.0F, 1.0F, 0.0F));

		PartDefinition bottle3 = bottle_case.addOrReplaceChild("bottle3", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 4.0F));

		PartDefinition bottle4 = bottle_case.addOrReplaceChild("bottle4", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 8.0F));

		PartDefinition bottle5 = bottle_case.addOrReplaceChild("bottle5", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 8.0F));

		PartDefinition bottle6 = bottle_case.addOrReplaceChild("bottle6", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 4.0F));

		PartDefinition leftarm = upperBody.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.2F, -9.4F, -0.7F, -0.1309F, -0.0087F, -0.2182F));

		PartDefinition rightArm_r2 = leftarm.addOrReplaceChild("rightArm_r2", CubeListBuilder.create().texOffs(36, 38).addBox(-3.0F, -24.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.0F, 21.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition rightLeg = warlock.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -12.0F, 0.0F, 0.0175F, 0.1745F, 0.0873F));

		PartDefinition leftLeg = warlock.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -12.0F, 0.0F, -0.0436F, -0.1309F, -0.0436F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
		if (entity instanceof Reprobate reprobate) {
			this.animate(reprobate.idleAnimationState, ReprobateAnimations.IDLE, ageInTicks);
			this.animate(reprobate.throwAnimationState, ReprobateAnimations.ATTACK, ageInTicks);

			if (reprobate.isCurrentAnimation(Reprobate.IDLE) || reprobate.isCurrentAnimation(Reprobate.INSPECT)) {
				if (reprobate.isSprinting()) {
					this.animateWalk(ReprobateAnimations.RUN, limbSwing, limbSwingAmount, 1.0F, 2.5F);
				} else {
					this.animateWalk(ReprobateAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
				}
			}

			if (reprobate.isCurrentAnimation(Reprobate.INSPECT)) {
				this.head.xRot = 0.5F;
				this.head.yRot = 0.0F;
				this.leftarm.yRot = 0.5F;
				this.leftarm.xRot = -0.9F;
			} else {
				this.animateHeadLookTarget(netHeadYaw, headPitch);
			}
			if (reprobate.isBarrel()) {
				this.barrel.visible = true;
				this.bottle_case.visible = false;
			} else {
				this.barrel.visible = false;
				this.bottle_case.visible = true;
			}
		} else if (entity instanceof ReprobateServant reprobate) {
			this.animate(reprobate.idleAnimationState, ReprobateAnimations.IDLE, ageInTicks);
			this.animate(reprobate.throwAnimationState, ReprobateAnimations.ATTACK, ageInTicks);

			if (reprobate.isCurrentAnimation(Reprobate.IDLE) || reprobate.isCurrentAnimation(Reprobate.INSPECT)) {
				if (reprobate.isSprinting()) {
					this.animateWalk(ReprobateAnimations.RUN, limbSwing, limbSwingAmount, 1.0F, 2.5F);
				} else {
					this.animateWalk(ReprobateAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
				}
			}

			if (reprobate.isCurrentAnimation(Reprobate.INSPECT)) {
				this.head.xRot = 0.5F;
				this.head.yRot = 0.0F;
				this.leftarm.yRot = 0.5F;
				this.leftarm.xRot = -0.9F;
			} else {
				this.animateHeadLookTarget(netHeadYaw, headPitch);
			}
			if (reprobate.isBarrel()) {
				this.barrel.visible = true;
				this.bottle_case.visible = false;
			} else {
				this.barrel.visible = false;
				this.bottle_case.visible = true;
			}
		} else {
			this.disableItems();
		}
	}

	public void disableItems() {
		this.barrel.visible = false;
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
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.warlock.translateAndRotate(matrixStackIn);
		this.upperBody.translateAndRotate(matrixStackIn);
		this.leftarm.translateAndRotate(matrixStackIn);
	}
}