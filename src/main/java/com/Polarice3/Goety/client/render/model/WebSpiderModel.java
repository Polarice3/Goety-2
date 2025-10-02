package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.WebSpiderAnimations;
import com.Polarice3.Goety.common.entities.ally.spider.WebSpiderServant;
import com.Polarice3.Goety.common.entities.hostile.WebSpider;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class WebSpiderModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart main_body;
	private final ModelPart head;
	private final ModelPart mid;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightMiddleHindLeg;
	private final ModelPart leftMiddleHindLeg;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart abdomen;

	public WebSpiderModel(ModelPart root) {
		this.root = root;
		this.main_body = root.getChild("main_body");
		this.head = this.main_body.getChild("head");
		this.mid = this.main_body.getChild("mid");
		this.rightHindLeg = this.mid.getChild("right_hind_leg");
		this.leftHindLeg = this.mid.getChild("left_hind_leg");
		this.rightMiddleHindLeg = this.mid.getChild("right_middle_hind_leg");
		this.leftMiddleHindLeg = this.mid.getChild("left_middle_hind_leg");
		this.rightMiddleFrontLeg = this.mid.getChild("right_middle_front_leg");
		this.leftMiddleFrontLeg = this.mid.getChild("left_middle_front_leg");
		this.rightFrontLeg = this.mid.getChild("right_front_leg");
		this.leftFrontLeg = this.mid.getChild("left_front_leg");
		this.abdomen = this.mid.getChild("abdomen");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main_body = partdefinition.addOrReplaceChild("main_body", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, -3.0F));

		PartDefinition head = main_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mid = main_body.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_hind_leg = mid.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 5.0F, 0.0F, 0.7854F, -0.7854F));

		PartDefinition left_hind_leg = mid.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 5.0F, 0.0F, -0.7854F, 0.7854F));

		PartDefinition right_middle_hind_leg = mid.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 4.0F, 0.0F, 0.2618F, -0.6109F));

		PartDefinition left_middle_hind_leg = mid.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 4.0F, 0.0F, -0.2618F, 0.6109F));

		PartDefinition right_middle_front_leg = mid.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 3.0F, 0.0F, -0.2618F, -0.6109F));

		PartDefinition left_middle_front_leg = mid.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 3.0F, 0.0F, 0.2618F, 0.6109F));

		PartDefinition right_front_leg = mid.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 2.0F, 0.0F, -0.7854F, -0.7854F));

		PartDefinition left_front_leg = mid.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 2.0F, 0.0F, 0.7854F, 0.7854F));

		PartDefinition abdomen = mid.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		boolean flag = true;
		if (entity instanceof WebSpider webSpider){
			flag = !webSpider.shootAnimationState.isStarted();
			this.animate(webSpider.shootAnimationState, WebSpiderAnimations.SHOOT, ageInTicks);
		}
		if (entity instanceof WebSpiderServant webSpiderServant){
			flag = !webSpiderServant.shootAnimationState.isStarted();
			this.animate(webSpiderServant.shootAnimationState, WebSpiderAnimations.SHOOT, ageInTicks);
		}
		if (flag) {
			this.rightHindLeg.zRot = (-(float) Math.PI / 4F);
			this.leftHindLeg.zRot = ((float) Math.PI / 4F);
			this.rightMiddleHindLeg.zRot = -0.58119464F;
			this.leftMiddleHindLeg.zRot = 0.58119464F;
			this.rightMiddleFrontLeg.zRot = -0.58119464F;
			this.leftMiddleFrontLeg.zRot = 0.58119464F;
			this.rightFrontLeg.zRot = (-(float) Math.PI / 4F);
			this.leftFrontLeg.zRot = ((float) Math.PI / 4F);
			this.rightHindLeg.yRot = ((float) Math.PI / 4F);
			this.leftHindLeg.yRot = (-(float) Math.PI / 4F);
			this.rightMiddleHindLeg.yRot = ((float) Math.PI / 8F);
			this.leftMiddleHindLeg.yRot = (-(float) Math.PI / 8F);
			this.rightMiddleFrontLeg.yRot = (-(float) Math.PI / 8F);
			this.leftMiddleFrontLeg.yRot = ((float) Math.PI / 8F);
			this.rightFrontLeg.yRot = (-(float) Math.PI / 4F);
			this.leftFrontLeg.yRot = ((float) Math.PI / 4F);
			float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
			float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
			float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
			float f6 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
			float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
			float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
			float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
			float f10 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
			this.rightHindLeg.yRot += f3;
			this.leftHindLeg.yRot += -f3;
			this.rightMiddleHindLeg.yRot += f4;
			this.leftMiddleHindLeg.yRot += -f4;
			this.rightMiddleFrontLeg.yRot += f5;
			this.leftMiddleFrontLeg.yRot += -f5;
			this.rightFrontLeg.yRot += f6;
			this.leftFrontLeg.yRot += -f6;
			this.rightHindLeg.zRot += f7;
			this.leftHindLeg.zRot += -f7;
			this.rightMiddleHindLeg.zRot += f8;
			this.leftMiddleHindLeg.zRot += -f8;
			this.rightMiddleFrontLeg.zRot += f9;
			this.leftMiddleFrontLeg.zRot += -f9;
			this.rightFrontLeg.zRot += f10;
			this.leftFrontLeg.zRot += -f10;
		}
	}

	public ModelPart root() {
		return this.root;
	}
}