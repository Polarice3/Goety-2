package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BlastFungusModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart fungus;

	public BlastFungusModel(ModelPart root) {
		this.fungus = root.getChild("fungus");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fungus = partdefinition.addOrReplaceChild("fungus", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -5.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bits = fungus.addOrReplaceChild("bits", CubeListBuilder.create().texOffs(-1, 0).addBox(2.0F, -9.0F, -7.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(29, 0).addBox(-1.0F, -9.0F, -6.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-1.0F, -7.0F, -6.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-3.0F, -1.0F, -6.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-3.0F, 1.0F, -6.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(5.0F, 1.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(5.0F, -1.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(5.0F, -7.0F, 1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(5.0F, -9.0F, 1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(5.0F, -8.0F, -3.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-1.0F, -7.0F, 4.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(29, 0).addBox(-1.0F, -9.0F, 3.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-5.0F, -8.0F, 1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-5.0F, -9.0F, -3.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-5.0F, -7.0F, -3.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(-5.0F, 1.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-5.0F, -1.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 3).addBox(3.0F, 1.0F, 4.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(3.0F, -1.0F, 4.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bits.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -1.5F, 4.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bits.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(29, 0).addBox(-2.5F, 2.0F, -9.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -8.0F, 3.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r3 = bits.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(-1, 0).addBox(2.5F, -9.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(-1, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 1.5F, -5.0F, -1.5708F, 0.0F, -3.1416F));

		PartDefinition cube_r4 = bits.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 0).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, -1.0F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r5 = bits.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(29, 0).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -8.0F, -3.0F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r6 = bits.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -9.5F, 1.5F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r7 = bits.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(29, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -9.0F, 1.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r8 = bits.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 1.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -8.5F, 1.5F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r9 = bits.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(29, 0).addBox(-0.5F, 1.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -8.0F, 6.0F, 1.5708F, 3.1416F, 0.0F));

		PartDefinition cube_r10 = bits.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(-1, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -9.0F, 4.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r11 = bits.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -9.5F, -2.5F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r12 = bits.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(29, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -9.0F, -2.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r13 = bits.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -8.5F, -2.5F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r14 = bits.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(29, 0).addBox(0.0F, -1.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -8.0F, 1.0F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r15 = bits.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(29, 0).addBox(0.0F, -1.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, -1.0F, 1.5708F, -1.5708F, 0.0F));

		PartDefinition cube_r16 = bits.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(29, 0).addBox(-2.5F, 0.0F, -9.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(29, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -8.0F, -6.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r17 = bits.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -1.5F, -5.0F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void setupAnim(float p_103812_, float p_103813_) {
		this.fungus.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.fungus.xRot = p_103813_ * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		fungus.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}