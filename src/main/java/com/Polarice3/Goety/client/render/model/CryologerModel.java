package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.CryologerAnimations;
import com.Polarice3.Goety.common.entities.hostile.illagers.Cryologer;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CryologerModel<T extends Cryologer> extends HierarchicalModel<T> implements HeadedModel {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart cape;
	private final ModelPart arms;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart right_arm;
	private final ModelPart left_arm;

	public CryologerModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.cape = this.body.getChild("cape");
		this.arms = this.body.getChild("arms");
		this.right_arm = this.body.getChild("right_arm");
		this.left_arm = this.body.getChild("left_arm");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(32, 0).addBox(-5.5F, -11.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offset(1.0F, 1.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, -4.1F));

		PartDefinition right_eye = eyebrows.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition left_eye = eyebrows.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition blinks = head.addOrReplaceChild("blinks", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_blink = blinks.addOrReplaceChild("right_blink", CubeListBuilder.create().texOffs(0, 2).addBox(-2.0F, 1.5F, -0.05F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -5.5F, -4.0F));

		PartDefinition left_blink = blinks.addOrReplaceChild("left_blink", CubeListBuilder.create().texOffs(0, 2).mirror().addBox(0.0F, 1.5F, -0.05F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, -5.5F, -4.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -11.5F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-4.0F, -11.5F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 11.5F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(68, 0).addBox(-4.5F, -1.0F, -0.5F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 3.5F));

		PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.5F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -9.5F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -9.5F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

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
		this.animate(entity.idleAnimationState, CryologerAnimations.IDLE, ageInTicks);
		this.animate(entity.breathAnimationState, CryologerAnimations.BREATH, ageInTicks);
		this.animate(entity.cloudAnimationState, CryologerAnimations.HAIL, ageInTicks);
		this.animate(entity.wallAnimationState, CryologerAnimations.WALL, ageInTicks);
		if (entity.getCurrentAnimation() == entity.getAnimationState("idle")){
			this.arms.visible = true;
			this.right_arm.visible = false;
			this.left_arm.visible = false;
		} else {
			this.arms.visible = false;
			this.right_arm.visible = true;
			this.left_arm.visible = true;
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
}