package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.CryologerAnimations;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.CryologerServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundCryologer;
import com.Polarice3.Goety.common.entities.hostile.illagers.Cryologer;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;

public class CryologerModel<T extends Mob> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart illager;
    private final ModelPart upperBody;
	private final ModelPart head;
	private final ModelPart body;
    private final ModelPart cape;
	private final ModelPart arms;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
    private final ModelPart left_leg;

    public CryologerModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.upperBody = this.illager.getChild("upperBody");
		this.head = this.upperBody.getChild("head");
		this.body = this.upperBody.getChild("body");
		this.cape = this.upperBody.getChild("cape");
		this.arms = this.upperBody.getChild("arms");
		this.right_arm = this.upperBody.getChild("right_arm");
		this.left_arm = this.upperBody.getChild("left_arm");
		this.right_leg = this.illager.getChild("right_leg");
		this.left_leg = this.illager.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upperBody = illager.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offset(0.0F, -12.5F, 0.0F));

		PartDefinition head = upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.5F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(32, 0).addBox(-5.5F, -11.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offset(1.0F, 1.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, -4.1F));

		PartDefinition right_eye = eyebrows.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition left_eye = eyebrows.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition blinks = head.addOrReplaceChild("blinks", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_blink = blinks.addOrReplaceChild("right_blink", CubeListBuilder.create().texOffs(0, 2).addBox(-2.0F, 1.5F, -0.05F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -5.5F, -4.0F));

		PartDefinition left_blink = blinks.addOrReplaceChild("left_blink", CubeListBuilder.create().texOffs(0, 2).mirror().addBox(0.0F, 1.5F, -0.05F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, -5.5F, -4.0F));

		PartDefinition body = upperBody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.5F, 0.0F));

		PartDefinition clothes = body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cape = upperBody.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(68, 0).addBox(-4.5F, -1.0F, -0.5F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 3.5F));

		PartDefinition arms = upperBody.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.5F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition right_arm = upperBody.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -9.5F, 0.0F));

		PartDefinition left_arm = upperBody.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -9.5F, 0.0F));

		PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition right_boot = right_leg.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(80, 61).addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.1F))
				.texOffs(64, 55).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));

		PartDefinition left_boot = left_leg.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(64, 55).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F))
				.texOffs(80, 61).addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (this.riding){
			this.right_arm.xRot = (-(float)Math.PI / 5F);
			this.right_arm.yRot = 0.0F;
			this.right_arm.zRot = 0.0F;
			this.left_arm.xRot = (-(float)Math.PI / 5F);
			this.left_arm.yRot = 0.0F;
			this.left_arm.zRot = 0.0F;
			this.right_leg.xRot = -1.4137167F;
			this.right_leg.yRot = ((float)Math.PI / 10F);
			this.right_leg.zRot = 0.07853982F;
			this.left_leg.xRot = -1.4137167F;
			this.left_leg.yRot = (-(float)Math.PI / 10F);
			this.left_leg.zRot = -0.07853982F;
		} else {
			this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
			this.right_arm.yRot = 0.0F;
			this.right_arm.zRot = 0.0F;
			this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
			this.left_arm.yRot = 0.0F;
			this.left_arm.zRot = 0.0F;
			this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
			this.right_leg.yRot = 0.0F;
			this.right_leg.zRot = 0.0F;
			this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
			this.left_leg.yRot = 0.0F;
			this.left_leg.zRot = 0.0F;
		}
		float f = 1.0F;
		if (entity.getFallFlyingTicks() > 4) {
			f = (float)entity.getDeltaMovement().lengthSqr();
			f = f / 0.2F;
			f = f * f * f;
		}
		if (f < 1.0F) {
			f = 1.0F;
		}
		this.cape.xRot = MathHelper.modelDegrees(10.0F) + Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount / f);
		if (entity instanceof Cryologer cryologer) {
			this.animate(cryologer.idleAnimationState, CryologerAnimations.IDLE, ageInTicks);
			this.animate(cryologer.breathAnimationState, CryologerAnimations.BREATH, ageInTicks);
			this.animate(cryologer.cloudAnimationState, CryologerAnimations.HAIL, ageInTicks);
			this.animate(cryologer.wallAnimationState, CryologerAnimations.WALL, ageInTicks);
			this.animate(cryologer.chunkAnimationState, CryologerAnimations.CHUNK, ageInTicks);
			if (cryologer.getCurrentAnimation() == cryologer.getAnimationState("idle")) {
				this.right_arm.xRot = -0.75F;
				this.right_arm.zRot = 0.0F;
				this.right_arm.yRot = 0.0F;
				this.left_arm.xRot = -0.75F;
				this.left_arm.zRot = 0.0F;
				this.left_arm.yRot = 0.0F;
				this.arms.visible = true;
				this.right_arm.visible = false;
				this.left_arm.visible = false;
			} else {
				this.arms.visible = false;
				this.right_arm.visible = true;
				this.left_arm.visible = true;
			}
		} else if (entity instanceof CryologerServant cryologer) {
			this.animate(cryologer.idleAnimationState, CryologerAnimations.IDLE, ageInTicks);
			this.animate(cryologer.breathAnimationState, CryologerAnimations.BREATH, ageInTicks);
			this.animate(cryologer.cloudAnimationState, CryologerAnimations.HAIL, ageInTicks);
			this.animate(cryologer.wallAnimationState, CryologerAnimations.WALL, ageInTicks);
			this.animate(cryologer.chunkAnimationState, CryologerAnimations.CHUNK, ageInTicks);
			if (cryologer.getCurrentAnimation() == cryologer.getAnimationState("idle")) {
				this.right_arm.xRot = -0.75F;
				this.right_arm.zRot = 0.0F;
				this.right_arm.yRot = 0.0F;
				this.left_arm.xRot = -0.75F;
				this.left_arm.zRot = 0.0F;
				this.left_arm.yRot = 0.0F;
				this.arms.visible = true;
				this.right_arm.visible = false;
				this.left_arm.visible = false;
			} else {
				this.arms.visible = false;
				this.right_arm.visible = true;
				this.left_arm.visible = true;
			}
		} else if (entity instanceof BoundCryologer cryologer) {
			this.animate(cryologer.idleAnimationState, CryologerAnimations.IDLE, ageInTicks);
			this.animate(cryologer.breathAnimationState, CryologerAnimations.BREATH, ageInTicks);
			this.animate(cryologer.cloudAnimationState, CryologerAnimations.HAIL, ageInTicks);
			this.animate(cryologer.wallAnimationState, CryologerAnimations.WALL, ageInTicks);
			this.animate(cryologer.chunkAnimationState, CryologerAnimations.CHUNK, ageInTicks);
			if (cryologer.getCurrentAnimation() == cryologer.getAnimationState("idle")) {
				this.right_arm.xRot = -0.75F;
				this.right_arm.zRot = 0.0F;
				this.right_arm.yRot = 0.0F;
				this.left_arm.xRot = -0.75F;
				this.left_arm.zRot = 0.0F;
				this.left_arm.yRot = 0.0F;
				this.arms.visible = true;
				this.right_arm.visible = false;
				this.left_arm.visible = false;
			} else {
				this.arms.visible = false;
				this.right_arm.visible = true;
				this.left_arm.visible = true;
			}
		}
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private ModelPart getArm(HumanoidArm p_102923_) {
		return p_102923_ == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.right_arm, this.left_arm, this.right_leg, this.left_leg);
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		this.getArm(arm).translateAndRotate(poseStack);
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, -1.5F, 0.0F);
		poseStack.scale(1.05F, 1.05F, 1.05F);
	}

	@Override
	public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
	}

	@Override
	public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.scale(1.05F, 1.05F, 1.05F);
	}

	public Iterable<ModelPart> rightHandArmors() {
		return ImmutableList.of(this.right_arm);
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of(this.left_arm);
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.right_leg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.left_leg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}

}