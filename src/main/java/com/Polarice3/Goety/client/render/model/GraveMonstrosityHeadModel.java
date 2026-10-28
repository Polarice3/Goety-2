package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class GraveMonstrosityHeadModel extends SkullModelBase {
	private final ModelPart neck;

	public GraveMonstrosityHeadModel(ModelPart root) {
		this.neck = root.getChild("neck");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(136, 119).addBox(-13.0F, -14.0F, -8.0F, 26.0F, 23.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(-16, 208).addBox(-13.0F, 6.0F, -8.0F, 26.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));

		PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(182, 97).addBox(9.0F, 0.0F, -5.0F, 9.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(84, 158).addBox(-4.0F, -6.0F, -5.0F, 13.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(13.0F, -6.0F, -1.0F));

		PartDefinition horn_fire = left_horn.addOrReplaceChild("horn_fire", CubeListBuilder.create(), PartPose.offset(13.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = horn_fire.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(480, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = horn_fire.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(480, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(220, 97).addBox(-18.0F, -18.0F, -5.0F, 9.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(130, 158).addBox(-9.0F, -6.0F, -5.0F, 13.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, -6.0F, -1.0F));

		PartDefinition jaw = neck.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 157).addBox(-13.0F, -7.52F, -16.0F, 26.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(-16, 208).addBox(-13.0F, -2.52F, -16.0F, 26.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.48F, 8.0F));

		PartDefinition J_NeckSocket = neck.addOrReplaceChild("J_NeckSocket", CubeListBuilder.create(), PartPose.offset(0.0F, -18.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 512, 256);
	}

	public void setupAnim(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
		this.neck.yRot = p_225603_2_ * ((float)Math.PI / 180F);
		this.neck.xRot = p_225603_3_ * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.neck.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}