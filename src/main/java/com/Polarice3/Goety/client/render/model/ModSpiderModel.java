package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.ModSpiderAnimations;
import com.Polarice3.Goety.common.entities.ally.spider.BoneSpiderServant;
import com.Polarice3.Goety.common.entities.ally.spider.WebSpiderServant;
import com.Polarice3.Goety.common.entities.hostile.BoneSpider;
import com.Polarice3.Goety.common.entities.hostile.WebSpider;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModSpiderModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart spider;
	private final ModelPart neck;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightMiddleHindLeg;
	private final ModelPart leftMiddleHindLeg;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public ModSpiderModel(ModelPart root) {
		this.root = root;
		this.spider = root.getChild("spider");
		this.neck = this.spider.getChild("neck");
		this.body = this.neck.getChild("body");
		this.head = this.spider.getChild("head");
		this.rightHindLeg = this.spider.getChild("right_legs").getChild("rightHindLeg");
		this.rightMiddleHindLeg = this.spider.getChild("right_legs").getChild("rightMiddleHindLeg");
		this.rightMiddleFrontLeg = this.spider.getChild("right_legs").getChild("rightMiddleFrontLeg");
		this.rightFrontLeg = this.spider.getChild("right_legs").getChild("rightFrontLeg");
		this.leftHindLeg = this.spider.getChild("left_legs").getChild("leftHindLeg");
		this.leftMiddleHindLeg = this.spider.getChild("left_legs").getChild("leftMiddleHindLeg");
		this.leftMiddleFrontLeg = this.spider.getChild("left_legs").getChild("leftMiddleFrontLeg");
		this.leftFrontLeg = this.spider.getChild("left_legs").getChild("leftFrontLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition spider = partdefinition.addOrReplaceChild("spider", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = spider.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -3.0F));

		PartDefinition neck = spider.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition body = neck.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition right_legs = spider.addOrReplaceChild("right_legs", CubeListBuilder.create(), PartPose.offset(-4.0F, -9.0F, 0.0F));

		PartDefinition rightHindLeg = right_legs.addOrReplaceChild("rightHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition rightMiddleHindLeg = right_legs.addOrReplaceChild("rightMiddleHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition rightMiddleFrontLeg = right_legs.addOrReplaceChild("rightMiddleFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightFrontLeg = right_legs.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition left_legs = spider.addOrReplaceChild("left_legs", CubeListBuilder.create(), PartPose.offset(4.0F, -9.0F, 0.0F));

		PartDefinition leftHindLeg = left_legs.addOrReplaceChild("leftHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition leftMiddleHindLeg = left_legs.addOrReplaceChild("leftMiddleHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition leftMiddleFrontLeg = left_legs.addOrReplaceChild("leftMiddleFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftFrontLeg = left_legs.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public static LayerDefinition createIcyBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition spider = partdefinition.addOrReplaceChild("spider", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = spider.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -3.0F));

		PartDefinition neck = spider.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition body = neck.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-4.0F, -3.0F, 1.0F, 8.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition right_legs = spider.addOrReplaceChild("right_legs", CubeListBuilder.create(), PartPose.offset(-4.0F, -9.0F, 0.0F));

		PartDefinition rightHindLeg = right_legs.addOrReplaceChild("rightHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition rightMiddleHindLeg = right_legs.addOrReplaceChild("rightMiddleHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition rightMiddleFrontLeg = right_legs.addOrReplaceChild("rightMiddleFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightFrontLeg = right_legs.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition left_legs = spider.addOrReplaceChild("left_legs", CubeListBuilder.create(), PartPose.offset(4.0F, -9.0F, 0.0F));

		PartDefinition leftHindLeg = left_legs.addOrReplaceChild("leftHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition leftMiddleHindLeg = left_legs.addOrReplaceChild("leftMiddleHindLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition leftMiddleFrontLeg = left_legs.addOrReplaceChild("leftMiddleFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftFrontLeg = left_legs.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		boolean flag = true;
		if (entity instanceof BoneSpiderServant boneSpiderServant){
			flag = !boneSpiderServant.attackAnimationState.isStarted();
			this.animate(boneSpiderServant.attackAnimationState, ModSpiderAnimations.BONE_SPIT, ageInTicks);
		}
		if (entity instanceof BoneSpider boneSpider){
			flag = !boneSpider.attackAnimationState.isStarted();
			this.animate(boneSpider.attackAnimationState, ModSpiderAnimations.BONE_SPIT, ageInTicks);
		}
		if (entity instanceof WebSpiderServant webSpiderServant){
			if (webSpiderServant.isWebShooting()) {
				this.body.xRot = ((float) Math.PI / 6F);
			} else {
				this.body.xRot = 0.0F;
			}
		}
		if (entity instanceof WebSpider webSpider){
			if (webSpider.isWebShooting()) {
				this.body.xRot = ((float) Math.PI / 6F);
			} else {
				this.body.xRot = 0.0F;
			}
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