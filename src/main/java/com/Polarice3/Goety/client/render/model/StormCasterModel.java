package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.StormCasterAnimations;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.StormCasterServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundStormCaster;
import com.Polarice3.Goety.common.entities.hostile.illagers.StormCaster;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class StormCasterModel<T extends LivingEntity> extends HierarchicalModel<T> implements HeadedModel, HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart upperBody;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart hair;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final ModelPart right_eye;
	private final ModelPart right_pupil;
	private final ModelPart left_eye;
	private final ModelPart left_pupil;
	private final ModelPart eyebrows;
	private final ModelPart right_eyebrow;
	private final ModelPart left_eyebrow;
	private final ModelPart cape;
	private final ModelPart rightArm;
	private final ModelPart Staff;
	private final ModelPart handle;
	private final ModelPart group;
	private final ModelPart bits;
	private final ModelPart head2;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public StormCasterModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.upperBody = this.illager.getChild("upper_body");
		this.body = this.upperBody.getChild("body");
		this.head = this.upperBody.getChild("head");
		this.nose = this.head.getChild("nose");
		this.hair = this.head.getChild("hair");
		this.bone = this.hair.getChild("bone");
		this.bone2 = this.hair.getChild("bone2");
		this.bone3 = this.hair.getChild("bone3");
		this.right_eye = this.head.getChild("right_eye");
		this.right_pupil = this.right_eye.getChild("right_pupil");
		this.left_eye = this.head.getChild("left_eye");
		this.left_pupil = this.left_eye.getChild("left_pupil");
		this.eyebrows = this.head.getChild("eyebrows");
		this.right_eyebrow = this.eyebrows.getChild("right_eyebrow");
		this.left_eyebrow = this.eyebrows.getChild("left_eyebrow");
		this.cape = this.upperBody.getChild("cape");
		this.rightArm = this.upperBody.getChild("right_arm");
		this.Staff = this.rightArm.getChild("Staff");
		this.handle = this.Staff.getChild("handle");
		this.group = this.Staff.getChild("group");
		this.bits = this.group.getChild("bits");
		this.head2 = this.Staff.getChild("head2");
		this.leftArm = this.upperBody.getChild("left_arm");
		this.rightLeg = this.illager.getChild("right_leg");
		this.leftLeg = this.illager.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition upper_body = illager.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition body = upper_body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F))
		.texOffs(16, 20).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

		PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone = hair.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(46, -8).addBox(1.0F, -3.0F, -4.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -7.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bone2 = hair.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(46, -8).mirror().addBox(-1.0F, -3.0F, -4.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone3 = hair.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(46, -8).mirror().addBox(-1.0F, -3.0F, -4.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -7.0F, 8.0F, -1.5708F, -0.7854F, 1.5708F));

		PartDefinition right_eye = head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -0.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, -4.0F));

		PartDefinition right_pupil = right_eye.addOrReplaceChild("right_pupil", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, -1.0F, -0.02F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_eye = head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -0.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -3.0F, -4.0F));

		PartDefinition left_pupil = left_eye.addOrReplaceChild("left_pupil", CubeListBuilder.create().texOffs(4, 0).addBox(0.0F, -1.0F, -0.02F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -4.0F));

		PartDefinition right_eyebrow = eyebrows.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(0, 1).mirror().addBox(-1.5F, -1.0F, -0.3F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.35F, 0.1F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition left_eyebrow = eyebrows.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(0, 1).addBox(-1.5F, -1.0F, -0.3F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.35F, 0.1F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition cape = upper_body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(44, 8).addBox(-4.5F, -0.5F, 0.0F, 9.0F, 21.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 3.5F, 0.7418F, 0.0F, -0.1309F));

		PartDefinition right_arm = upper_body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -7.0F, -1.0F, -1.5272F, 0.0F, 0.0F));

		PartDefinition Staff = right_arm.addOrReplaceChild("Staff", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 9.0F, 10.0F, 1.5708F, 0.0F, -1.5708F));

		PartDefinition handle = Staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(28, 41).addBox(0.0F, -14.0F, 0.0F, 1.0F, 22.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition group = Staff.addOrReplaceChild("group", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bits = group.addOrReplaceChild("bits", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bits.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(44, 38).addBox(1.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 38).addBox(2.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 38).addBox(-3.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 38).addBox(-2.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(-2.0F, 4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(4.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 40).addBox(3.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 38).addBox(3.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(3.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(1.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 40).addBox(-1.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 38).addBox(0.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(1.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 40).addBox(2.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(2.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 39).addBox(3.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 38).addBox(3.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 39).addBox(4.0F, 4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F))
		.texOffs(28, 39).addBox(4.0F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 40).addBox(3.0F, 4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -20.0F, 0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition head2 = Staff.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(32, 38).addBox(-1.0F, -21.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = upper_body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.1309F, 0.0F, -0.1745F));

		PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.1466F, 0.0F, 0.0F));

		PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F) - MathHelper.modelDegrees(5);
		if (entity instanceof StormCaster caster) {
			this.animate(caster.idleAnimationState, StormCasterAnimations.IDLE, ageInTicks);
			this.animate(caster.shockAnimationState, StormCasterAnimations.SHOCK, ageInTicks);
			this.animate(caster.dischargeAnimationState, StormCasterAnimations.DISCHARGE, ageInTicks);
			this.animate(caster.cloudAnimationState, StormCasterAnimations.SUMMON_CLOUD, ageInTicks);
			if (!caster.isAttacking()){
				this.animateWalk(StormCasterAnimations.MOVE, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		} else if (entity instanceof StormCasterServant servant) {
			this.animate(servant.idleAnimationState, StormCasterAnimations.IDLE, ageInTicks);
			this.animate(servant.shockAnimationState, StormCasterAnimations.SHOCK, ageInTicks);
			this.animate(servant.dischargeAnimationState, StormCasterAnimations.DISCHARGE, ageInTicks);
			this.animate(servant.cloudAnimationState, StormCasterAnimations.SUMMON_CLOUD, ageInTicks);
			if (!servant.isAttacking()){
				this.animateWalk(StormCasterAnimations.MOVE, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		} else if (entity instanceof BoundStormCaster servant) {
			this.animate(servant.idleAnimationState, StormCasterAnimations.IDLE, ageInTicks);
			this.animate(servant.shockAnimationState, StormCasterAnimations.SHOCK, ageInTicks);
			this.animate(servant.dischargeAnimationState, StormCasterAnimations.DISCHARGE, ageInTicks);
			this.animate(servant.cloudAnimationState, StormCasterAnimations.SUMMON_CLOUD, ageInTicks);
			if (!servant.isAttacking()){
				this.animateWalk(StormCasterAnimations.MOVE, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
		ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
		this.hair.visible = headItem.isEmpty() || headItem.is(ItemTags.BANNERS);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public ModelPart getHead() {
		return this.head;
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0, 0.0F, 0);
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
		return ImmutableList.of(this.rightArm);
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of(this.leftArm);
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.rightLeg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.leftLeg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}

}