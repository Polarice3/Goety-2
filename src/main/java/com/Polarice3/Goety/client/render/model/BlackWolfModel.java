package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class BlackWolfModel<T extends BlackWolf> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart hound;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart upperBody;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;

	public BlackWolfModel(ModelPart root) {
		this.root = root;
		this.hound = root.getChild("hound");
		this.head = this.hound.getChild("head");
		this.body = this.hound.getChild("body");
		this.upperBody = this.hound.getChild("upperBody");
		this.rightHindLeg = this.hound.getChild("right_hind_leg");
		this.leftHindLeg = this.hound.getChild("left_hind_leg");
		this.rightFrontLeg = this.hound.getChild("right_front_leg");
		this.leftFrontLeg = this.hound.getChild("left_front_leg");
		this.tail = this.hound.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hound = partdefinition.addOrReplaceChild("hound", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = hound.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -2.0F, 7.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(58, 7).addBox(-1.5F, 3.0F, 1.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(56, 1).addBox(0.0F, -5.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 10).addBox(-2.0F, -0.0156F, -5.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 27).addBox(-2.0F, 2.9844F, -5.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -8.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, -2.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -3.5F, 0.5F, 0.0F, 0.0F, 0.4363F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -3.5F, 0.5F, 0.0F, 0.0F, -0.4363F));

		PartDefinition body = hound.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -4.0F, 6.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -9.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition upperBody = hound.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(22, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition upperBody_r1 = upperBody.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-4.0F, 0.0F, -0.5F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.75F, 3.55F, 0.4363F, 0.0F, 0.0F));

		PartDefinition upperBody_r2 = upperBody.addOrReplaceChild("upperBody_r2", CubeListBuilder.create().texOffs(46, 0).addBox(-4.0F, 0.0F, -0.5F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.75F, 3.55F, 0.4363F, 0.0F, 0.0F));

		PartDefinition right_hind_leg = hound.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -8.0F, 7.0F));

		PartDefinition left_hind_leg = hound.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -8.0F, 7.0F));

		PartDefinition right_front_leg = hound.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -8.0F, -4.0F));

		PartDefinition left_front_leg = hound.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -8.0F, -4.0F));

		PartDefinition tail = hound.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(8, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 8.0F, 0.9599F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public void prepareMobModel(T p_104132_, float p_104133_, float p_104134_, float p_104135_) {
		this.tail.yRot = 0.0F;

		if (p_104132_.isStaying()) {
			this.head.setPos(0.0F, -12.0F, -5.0F);
			this.upperBody.setPos(0.0F, -6.0F, 2.0F);
			this.upperBody.xRot = 0.8727F;
			this.body.setPos(1.0F, -7.0F, 3.0F);
			this.body.xRot = 0.4363F;
			this.tail.setPos(0.0F, -2.0F, 4.0F);
			this.rightHindLeg.setPos(-1.5F, -1.0F, 3.0F);
			this.rightHindLeg.xRot = -1.5708F;
			this.leftHindLeg.setPos(1.5F, -1.0F, 3.0F);
			this.leftHindLeg.xRot = -1.5708F;
			this.rightFrontLeg.xRot = -0.4363F;
			this.rightFrontLeg.setPos(-1.5F, -7.0F, -4.0F);
			this.leftFrontLeg.xRot = -0.4363F;
			this.leftFrontLeg.setPos(1.5F, -7.0F, -4.0F);
		} else {
			this.head.setPos(0.0F, -8.0F, -8.0F);
			this.body.setPos(1.0F, -9.0F, 2.0F);
			this.body.xRot = ((float)Math.PI / 2F);
			this.upperBody.setPos(0.0F, -9.0F, 2.0F);
			this.upperBody.xRot = this.body.xRot;
			this.tail.setPos(0.0F, -10.0F, 8.0F);
			this.rightHindLeg.setPos(-1.5F, -8.0F, 7.0F);
			this.leftHindLeg.setPos(1.5F, -8.0F, 7.0F);
			this.rightFrontLeg.setPos(-1.5F, -8.0F, -4.0F);
			this.leftFrontLeg.setPos(1.5F, -8.0F, -4.0F);
			this.rightHindLeg.xRot = Mth.cos(p_104133_ * 0.6662F) * 1.4F * p_104134_ * 0.5F;
			this.leftHindLeg.xRot = Mth.cos(p_104133_ * 0.6662F + (float)Math.PI) * 1.4F * p_104134_ * 0.5F;
			this.rightFrontLeg.xRot = Mth.cos(p_104133_ * 0.6662F + (float)Math.PI) * 1.4F * p_104134_ * 0.5F;
			this.leftFrontLeg.xRot = Mth.cos(p_104133_ * 0.6662F) * 1.4F * p_104134_ * 0.5F;
		}

		this.head.zRot = p_104132_.getHeadRollAngle(p_104135_) + p_104132_.getBodyRollAngle(p_104135_, 0.0F);
		this.upperBody.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.08F);
		this.body.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.16F);
		this.tail.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.2F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private float r = 1.0F;
	private float g = 1.0F;
	private float b = 1.0F;

	public void setColor(float p_102420_, float p_102421_, float p_102422_) {
		this.r = p_102420_;
		this.g = p_102421_;
		this.b = p_102422_;
	}

	public void renderToBuffer(PoseStack p_102424_, VertexConsumer p_102425_, int p_102426_, int p_102427_, float p_102428_, float p_102429_, float p_102430_, float p_102431_) {
		super.renderToBuffer(p_102424_, p_102425_, p_102426_, p_102427_, this.r * p_102428_, this.g * p_102429_, this.b * p_102430_, p_102431_);
	}
}