package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class SpecterModel<T extends Mob> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart Ghost;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart body;

	public SpecterModel(ModelPart root) {
		this.root = root;
		this.Ghost = root.getChild("Ghost");
		this.head = this.Ghost.getChild("head");
		this.right_arm = this.Ghost.getChild("right_arm");
		this.left_arm = this.Ghost.getChild("left_arm");
		this.body = this.Ghost.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Ghost = partdefinition.addOrReplaceChild("Ghost", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = Ghost.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(40, 16).addBox(-4.0F, -8.0F, 5.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, -1.0F));

		PartDefinition right_arm = Ghost.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-6.0F, -22.0F, 0.0F));

		PartDefinition right_robe = right_arm.addOrReplaceChild("right_robe", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 34).addBox(-2.0F, -3.0F, 2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = Ghost.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(6.0F, -22.0F, 0.0F));

		PartDefinition left_robe = left_arm.addOrReplaceChild("left_robe", CubeListBuilder.create().texOffs(16, 16).mirror().addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(16, 34).addBox(-2.0F, -3.0F, 2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = Ghost.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -24.0F, 1.0F));

		PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(40, 28).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float f = ageInTicks * 0.0025F;
		this.Ghost.y = Mth.sin(f * 40.0F) + 24.0F;
		float f4 = Math.min(limbSwingAmount / 0.3F, 1.0F);
		this.body.xRot = f4 * MathHelper.modelDegrees(40.0F);
		this.body.xRot += Mth.cos(ageInTicks * 0.09F) * 0.1F + 0.1F;
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		animateArms(this.left_arm, this.right_arm, limbSwingAmount, ageInTicks);
	}

	public static void animateArms(ModelPart leftArm, ModelPart rightArm, float attackTime, float ageInTicks) {
		rightArm.zRot = 0.0F;
		leftArm.zRot = 0.0F;
		rightArm.yRot = -(0.1F - 0 * 0.6F);
		leftArm.yRot = 0.1F - 0 * 0.6F;
		float f2 = -MathHelper.modelDegrees(45.0F);
		rightArm.xRot = f2;
		leftArm.xRot = f2;
		AnimationUtils.bobArms(rightArm, leftArm, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}