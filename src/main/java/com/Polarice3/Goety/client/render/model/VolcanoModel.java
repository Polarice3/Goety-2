package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class VolcanoModel<T extends AbstractMonolith> extends EntityModel<T> {
	private final ModelPart volcano;

	public VolcanoModel(ModelPart root) {
		this.volcano = root.getChild("volcano");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition volcano = partdefinition.addOrReplaceChild("volcano", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = volcano.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone = base.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -11.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition bone2 = base.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 11.0F));

		PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone3 = base.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(11.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = bone3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, -16).addBox(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bone4 = base.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(-11.0F, 0.0F, 0.0F));

		PartDefinition cube_r4 = bone4.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, -16).addBox(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition mid = volcano.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -11.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-12.0F, -7.0F, -12.0F, 24.0F, 4.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-16.0F, -3.0F, -16.0F, 32.0F, 3.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.isEmerging() || entity.isDescending()) {
			this.volcano.y = (AbstractMonolith.getEmergingTime()) - limbSwing;
		} else {
			this.volcano.y = 0;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		volcano.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}