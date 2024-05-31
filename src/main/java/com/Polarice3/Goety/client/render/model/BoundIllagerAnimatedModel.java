package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.BoundIllagerAnimations;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class BoundIllagerAnimatedModel<T extends AbstractBoundIllager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart arms;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart cape;

	public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
	public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;

	public BoundIllagerAnimatedModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.body = this.illager.getChild("body");
		this.head = this.body.getChild("head");
		this.arms = this.body.getChild("arms");
		this.RightArm = this.body.getChild("RightArm");
		this.LeftArm = this.body.getChild("LeftArm");
		this.cape = this.body.getChild("cape");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = illager.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(32, 0).addBox(-5.5F, -11.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offset(1.0F, 1.0F, 0.0F));

		PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition RightArm = body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition LeftArm = body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(68, 0).addBox(-4.5F, -1.0F, -0.5F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 3.5F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		if (this.riding) {
			this.RightArm.xRot = (-(float) Math.PI / 5F);
			this.RightArm.yRot = 0.0F;
			this.RightArm.zRot = 0.0F;
			this.LeftArm.xRot = (-(float) Math.PI / 5F);
        } else {
			this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
			this.RightArm.yRot = 0.0F;
			this.RightArm.zRot = 0.0F;
			this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
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
        this.LeftArm.yRot = 0.0F;
        this.LeftArm.zRot = 0.0F;
		if (entity instanceof BoundIceologer boundIceologer){
			this.animate(boundIceologer.chunkAnimationState, BoundIllagerAnimations.CHUNK, ageInTicks);
		}
        AbstractBoundIllager.BoundArmPose armPose = entity.getArmPose();
		boolean flag = armPose == AbstractBoundIllager.BoundArmPose.CROSSED;
		this.arms.visible = flag;
		this.LeftArm.visible = !flag;
		this.RightArm.visible = !flag;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private ModelPart getArm(HumanoidArm p_102923_) {
		return p_102923_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
	}

	@Override
	public void translateToHand(HumanoidArm p_102925_, PoseStack p_102926_) {
		this.illager.translateAndRotate(p_102926_);
		this.body.translateAndRotate(p_102926_);
		this.getArm(p_102925_).translateAndRotate(p_102926_);
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.body.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0, -0.1F, 0);
	}

	@Override
	public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, 0.0F, 0.0F);
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
		this.body.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.scale(1.05F, 1.05F, 1.05F);
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	public Iterable<ModelPart> rightHandArmors() {
		return ImmutableList.of(this.RightArm);
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of(this.LeftArm);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}
}