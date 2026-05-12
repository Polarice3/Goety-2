package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class ReprobateCarryModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart carry;

	public ReprobateCarryModel(ModelPart root) {
		this.carry = root.getChild("carry");
	}

	public static LayerDefinition createCaseLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition carry = partdefinition.addOrReplaceChild("carry", CubeListBuilder.create().texOffs(88, 0).addBox(-4.0F, -2.0F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottle1 = carry.addOrReplaceChild("bottle1", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition bottle2 = carry.addOrReplaceChild("bottle2", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(4.0F, 1.0F, 0.0F));

		PartDefinition bottle3 = carry.addOrReplaceChild("bottle3", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 4.0F));

		PartDefinition bottle4 = carry.addOrReplaceChild("bottle4", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 8.0F));

		PartDefinition bottle5 = carry.addOrReplaceChild("bottle5", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 8.0F));

		PartDefinition bottle6 = carry.addOrReplaceChild("bottle6", CubeListBuilder.create().texOffs(67, 27).addBox(-3.5F, -6.0F, -5.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 21).addBox(-3.0F, -7.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 52).addBox(-3.0F, -4.0F, -5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
		.texOffs(64, 38).addBox(-3.0F, -5.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	public static LayerDefinition createBarrelLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition carry = partdefinition.addOrReplaceChild("carry", CubeListBuilder.create().texOffs(80, 38).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 14.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(88, 26).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(86, 32).addBox(-1.5F, -10.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.75F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = carry.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(88, 26).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	public void setupAnim(float p_103812_, float p_103813_, boolean barrel) {
		this.carry.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.carry.xRot = p_103813_ * ((float)Math.PI / 180F);
		if (barrel) {
			this.carry.xRot -= 1.5708F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.carry.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}