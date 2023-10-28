package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.Haunt;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class HauntModel<T extends Haunt> extends HierarchicalModel<T> {
	private final ModelPart haunt;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart tail;
	private final ModelPart tail1;

	public HauntModel(ModelPart root) {
		this.haunt = root.getChild("haunt");
		this.rightArm = this.haunt.getChild("right_arm");
		this.leftArm = this.haunt.getChild("left_arm");
		this.tail = this.haunt.getChild("tail");
		this.tail1 = this.tail.getChild("tail1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition haunt = partdefinition.addOrReplaceChild("haunt", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition body = haunt.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition tail = haunt.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 16).addBox(-2.5F, 3.0F, -2.5F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, 1.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition right_arm = haunt.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-2.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, -10.0F, 0.0F));

		PartDefinition left_arm = haunt.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -10.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float pLimbSwingAmount, float pAgeInTicks, float netHeadYaw, float headPitch) {
		float f = pAgeInTicks * 0.0025F;
		this.haunt.y = Mth.sin(f * 40.0F) + 12.0F;
		float f4 = Math.min(pLimbSwingAmount / 0.3F, 1.0F);
		this.haunt.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.haunt.xRot = headPitch * ((float)Math.PI / 180F);
		this.tail.xRot = f4 * MathHelper.modelDegrees(22.5F);
		this.tail.xRot += Mth.cos(pAgeInTicks * 0.09F) * 0.1F + 0.1F;
		this.tail1.xRot = f4 * MathHelper.modelDegrees(40.0F);
		this.tail1.xRot += Mth.cos(pAgeInTicks * 0.09F) * 0.1F + 0.1F;
		animateArms(this.leftArm, this.rightArm, this.isAggressive(entity), this.attackTime, pAgeInTicks);
	}

	public boolean isAggressive(T entityIn) {
		return entityIn.isAggressive();
	}

	public static void animateArms(ModelPart p_102103_, ModelPart p_102104_, boolean isAggressive, float p_102106_, float p_102107_) {
		float f = Mth.sin(p_102106_ * (float)Math.PI);
		float f1 = Mth.sin((1.0F - (1.0F - p_102106_) * (1.0F - p_102106_)) * (float)Math.PI);
		p_102104_.zRot = 0.0F;
		p_102103_.zRot = 0.0F;
		p_102104_.yRot = -(0.1F - f * 0.6F);
		p_102103_.yRot = 0.1F - f * 0.6F;
		float f2 = isAggressive ? -MathHelper.modelDegrees(120.0F) : -MathHelper.modelDegrees(45.0F);
		p_102104_.xRot = f2;
		p_102103_.xRot = f2;
		p_102104_.xRot += f * 1.2F - f1 * 0.4F;
		p_102103_.xRot += f * 1.2F - f1 * 0.4F;
		AnimationUtils.bobArms(p_102104_, p_102103_, p_102107_);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.haunt.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.haunt;
	}
}