package com.Polarice3.Goety.client.render.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BlockModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart block;

	public BlockModel(ModelPart root) {
		this.block = root.getChild("block");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition block = partdefinition.addOrReplaceChild("block", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -16.0F, -8.0F, 15.9F, 15.9F, 15.9F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		block.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}