package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MonolithModel<T extends AbstractMonolith> extends EntityModel<T> {
	private final ModelPart monolith;

	public MonolithModel(ModelPart root) {
		this.monolith = root.getChild("monolith");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition monolith = partdefinition.addOrReplaceChild("monolith", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -50.0F, -7.0F, 14.0F, 50.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.isEmerging()) {
			this.monolith.y = (AbstractMonolith.getEmergingTime()) - limbSwing;
		} else {
			this.monolith.y = 0;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		monolith.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}