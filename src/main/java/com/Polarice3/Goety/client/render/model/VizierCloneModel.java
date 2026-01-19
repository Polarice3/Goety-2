package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.servants.VizierClone;
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

public class VizierCloneModel extends HierarchicalModel<VizierClone> implements ArmedModel, HeadedModel {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart tail;
	private final ModelPart lower_tail;

	public VizierCloneModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.rightArm = this.body.getChild("RightArm");
		this.leftArm = this.body.getChild("LeftArm");
		this.rightWing = this.body.getChild("RightWing");
		this.leftWing = this.body.getChild("LeftWing");
		this.tail = this.body.getChild("tail");
		this.lower_tail = this.tail.getChild("lower_tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).mirror().addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 38).addBox(-4.5F, -0.25F, -3.5F, 9.0F, 23.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(61, 24).mirror().addBox(-4.0F, 2.75F, 3.6F, 8.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(44, 58).mirror().addBox(-4.0F, 12.0F, -3.0F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(63, 73).addBox(-5.5F, -3.0F, -5.0F, 11.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_eyebrow = head.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 2).addBox(-1.5F, -1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -5.0F, -4.5F));

		PartDefinition left_eyebrow = head.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(-0.5F, -1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -5.0F, -4.5F));

		PartDefinition right_eye = head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.5F, -4.05F));

		PartDefinition left_eye = head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -3.5F, -4.05F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Hat = head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(18, 85).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.075F))
				.texOffs(18, 68).addBox(-5.0F, -13.5F, -5.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.5F))
				.texOffs(18, 70).addBox(-2.0F, -12.3F, -6.2F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition feather = Hat.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(55, 3).addBox(-2.5F, -7.0F, 0.0F, 5.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(81, 3).addBox(-0.5F, -7.0F, 0.0F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -6.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition mustache = head.addOrReplaceChild("mustache", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition right_mustache = mustache.addOrReplaceChild("right_mustache", CubeListBuilder.create().texOffs(4, 5).addBox(-5.25F, -2.0F, -1.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 5).addBox(-4.25F, -1.0F, -1.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(-5.25F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(-4.25F, 1.0F, -1.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(-3.25F, 0.0F, -1.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(-1.25F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(-0.25F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(-2.25F, 1.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.75F, -4.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition left_mustache = mustache.addOrReplaceChild("left_mustache", CubeListBuilder.create().texOffs(0, 2).addBox(1.25F, 0.0F, -1.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(2.25F, 1.0F, -1.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 2).addBox(4.25F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 5).addBox(4.25F, -2.0F, -1.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 5).addBox(3.25F, -1.0F, -1.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(-0.75F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(0.25F, 0.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 6).addBox(1.25F, 1.0F, -1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.75F, -4.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition beard = mustache.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(33, 5).mirror().addBox(-4.0F, -34.0F, -4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.15F)).mirror(false), PartPose.offset(0.0F, 32.0F, -1.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 70).addBox(-6.0F, -0.25F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 70).mirror().addBox(-2.0F, -0.25F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition lower_tail = tail.addOrReplaceChild("lower_tail", CubeListBuilder.create().texOffs(112, 0).mirror().addBox(-4.0F, 1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition RightArm = body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(44, 42).addBox(-3.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(76, 41).addBox(-5.75F, -2.5F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.3F))
				.texOffs(60, 42).addBox(-3.5F, -1.55F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition LeftArm = body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(44, 42).mirror().addBox(-0.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(76, 41).mirror().addBox(-1.25F, -2.5F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.3F)).mirror(false)
				.texOffs(60, 42).mirror().addBox(-0.5F, -1.55F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition RightWing = body.addOrReplaceChild("RightWing", CubeListBuilder.create().texOffs(96, 28).addBox(-16.0F, -1.0F, 0.0F, 16.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 4.0F));

		PartDefinition LeftWing = body.addOrReplaceChild("LeftWing", CubeListBuilder.create().texOffs(96, 28).mirror().addBox(0.0F, -1.0F, 0.0F, 16.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(VizierClone entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);

		boolean flag = entity.getFallFlyingTicks() > 4;
		float f = 1.0F;
		if (flag) {
			f = (float)entity.getDeltaMovement().lengthSqr();
			f /= 0.2F;
			f *= f * f;
		}

		if (f < 1.0F) {
			f = 1.0F;
		}

		float f4 = Math.min(limbSwingAmount / 0.3F, 1.0F);
		this.tail.xRot = f4 * MathHelper.modelDegrees(22.5F);
		this.tail.xRot += Mth.cos(ageInTicks * 0.09F) * 0.1F + 0.1F;
		this.lower_tail.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
		this.lower_tail.xRot += ((float)Math.PI / 5F);

		this.rightWing.z = 2.0F;
		this.leftWing.z = 2.0F;
		this.rightWing.y = 1.0F;
		this.leftWing.y = 1.0F;
		this.rightWing.yRot = 0.47123894F + Mth.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
		this.leftWing.yRot = -this.rightWing.yRot;
		this.leftWing.zRot = -0.47123894F;
		this.leftWing.xRot = 0.47123894F;
		this.rightWing.xRot = 0.47123894F;
		this.rightWing.zRot = 0.47123894F;

		AbstractIllager.IllagerArmPose abstractillagerentity$armpose = entity.getArmPose();
		if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.NEUTRAL){
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;

			this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;

			this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
		} else if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.ATTACKING) {
			if (entity.getMainHandItem().isEmpty()) {
				this.rightArm.xRot = ((float)Math.PI * 1.5F);
				this.leftArm.xRot = ((float)Math.PI * 1.5F);
			} else if (entity.getMainArm() == HumanoidArm.RIGHT) {
				this.rightArm.xRot = 3.7699115F;
			} else {
				this.leftArm.xRot = 3.7699115F;
			}
		} else if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;

			this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
			this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;

			this.rightArm.zRot = 2.3561945F;
			this.leftArm.zRot = -2.3561945F;

			this.rightArm.yRot = 0.0F;
			this.leftArm.yRot = 0.0F;
		} else if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;

			this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
			this.rightArm.zRot = 2.670354F;
			this.rightArm.yRot = 0.0F;

			this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
			this.leftArm.zRot = -2.3561945F;
			this.leftArm.yRot = 0.0F;
		}
	}

	@Override
	public ModelPart root() {
		return root;
	}

	private ModelPart getArm(HumanoidArm p_191216_1_) {
		return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
		this.getArm(p_102108_).translateAndRotate(p_102109_);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}