package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.MinisterAnimations;
import com.Polarice3.Goety.common.entities.hostile.illagers.Minister;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class MinisterModel<T extends Minister> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
	private final ModelPart root;
	private final ModelPart all;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart arms;
	private final ModelPart rightArm;
	private final ModelPart staff;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	public final ModelPart clothes;
	private final ModelPart cape;

	public MinisterModel(ModelPart root) {
		this.root = root;
		this.all = root.getChild("all");
		this.head = this.all.getChild("head");
		this.body = this.all.getChild("body");
		this.arms = this.all.getChild("arms");
		this.rightArm = this.all.getChild("right_arm");
		this.staff = this.rightArm.getChild("staff");
		this.leftArm = this.all.getChild("left_arm");
		this.rightLeg = this.all.getChild("right_leg");
		this.leftLeg = this.all.getChild("left_leg");
		this.clothes = this.body.getChild("clothes");
		this.cape = this.body.getChild("cape");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, -4.1F));

		PartDefinition right_eye = eyebrows.addOrReplaceChild("right_eye", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = right_eye.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -1.5F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition left_eye = eyebrows.addOrReplaceChild("left_eye", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r2 = left_eye.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-2.5F, -1.5F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition moustache = head.addOrReplaceChild("moustache", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = moustache.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 18).mirror().addBox(-2.5F, -1.0F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.5F, -3.5F, -4.1F, 0.0F, 0.0F, 0.1745F));

		PartDefinition cube_r4 = moustache.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(10, 18).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -3.5F, -4.1F, 0.0F, 0.0F, -0.1745F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 116).addBox(-4.0F, -1.25F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
				.texOffs(0, 104).addBox(-5.0F, -0.25F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -9.25F, 0.0F));

		PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 65).addBox(-5.0F, -14.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(44, 62).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 3.0F));

		PartDefinition clothes = body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F))
				.texOffs(18, 78).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition arms = all.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 78).addBox(4.5F, -2.5F, -2.5F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.5F))
				.texOffs(0, 78).mirror().addBox(-8.5F, -2.5F, -2.5F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(0.0F, 2.0F, 0.0F));

		PartDefinition right_arm = all.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 78).mirror().addBox(-3.5F, -2.5F, -2.5F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create().texOffs(60, 103).addBox(-0.5F, -8.125F, -0.5F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(48, 124).addBox(-1.5F, -9.125F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(48, 124).addBox(-1.5F, 15.375F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.25F))
				.texOffs(48, 118).addBox(-1.5F, -12.625F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-0.5F, 9.125F, -1.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition left_arm = all.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 78).addBox(-0.5F, -2.5F, -2.5F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_leg = all.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition left_leg = all.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateCape(entity, limbSwing, limbSwingAmount);
		if (!entity.isCasting() && !entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
			this.animateWalk(limbSwing, limbSwingAmount);
		}
		this.animate(entity.attackAnimationState, MinisterAnimations.SHOOT, ageInTicks);
		this.animate(entity.castAnimationState, MinisterAnimations.CAST_TEETH, ageInTicks);
		this.animate(entity.laughAnimationState, MinisterAnimations.LAUGH, ageInTicks);
		this.animate(entity.laughTargetAnimationState, MinisterAnimations.LAUGH_AT_YOU, ageInTicks);
		this.animate(entity.commandAnimationState, MinisterAnimations.COMMAND, ageInTicks);
		this.animate(entity.blockAnimationState, MinisterAnimations.BLOCK, ageInTicks);
		this.animate(entity.smashedAnimationState, MinisterAnimations.SMASHED, ageInTicks);
		this.animate(entity.speechAnimationState, MinisterAnimations.SPEECH, ageInTicks);
		this.animate(entity.deathAnimationState, MinisterAnimations.DEATH, ageInTicks);
		AbstractIllager.IllagerArmPose abstractprotectorentity$armpose = entity.getArmPose();
		boolean flag = abstractprotectorentity$armpose == AbstractIllager.IllagerArmPose.CROSSED;
		this.staff.visible = entity.hasStaff();
		this.arms.visible = flag;
		this.leftArm.visible = !flag;
		this.rightArm.visible = !flag;
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	private void animateWalk(float limbSwing, float limbSwingAmount) {
		this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.rightArm.yRot = 0.0F;
		this.rightArm.zRot = 0.0F;
		this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		this.leftArm.yRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.rightLeg.yRot = 0.0F;
		this.rightLeg.zRot = 0.0F;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
		this.leftLeg.yRot = 0.0F;
		this.leftLeg.zRot = 0.0F;
		this.resetArmPoses();
	}

	private void animateCape(T entity, float limbSwing, float limbSwingAmount){
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
	}

	private void resetArmPoses() {
		this.leftArm.x = 5.0F;
		this.leftArm.y = 2.0F;
		this.leftArm.z = 0.0F;
		this.leftArm.yRot = 0.0F;
		this.rightArm.x = -5.0F;
		this.rightArm.y = 2.0F;
		this.rightArm.z = 0.0F;
		this.rightArm.yRot = 0.0F;
		this.arms.y = 2.0F;
		this.arms.xRot = -0.75F;
	}

	public boolean isAggressive(T entityIn) {
		return entityIn.isAggressive();
	}

	private ModelPart getArm(HumanoidArm p_102923_) {
		return p_102923_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	public ModelPart getHead() {
		return this.head;
	}

	public void translateToHand(HumanoidArm p_102925_, PoseStack p_102926_) {
		this.getArm(p_102925_).translateAndRotate(p_102926_);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}