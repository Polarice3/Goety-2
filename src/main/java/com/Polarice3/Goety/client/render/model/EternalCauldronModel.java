package com.Polarice3.Goety.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class EternalCauldronModel extends HumanoidModel<LivingEntity> {
	private final ModelPart cauldron;
	private final ModelPart bone2;
	private final ModelPart bone;

	public EternalCauldronModel(ModelPart root) {
		super(root);
		this.cauldron = this.body.getChild("cauldron");
		this.bone2 = this.cauldron.getChild("bone2");
		this.bone = this.bone2.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.5F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cauldron = body.addOrReplaceChild("cauldron", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 4.0F, 2.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(-4.0F, 3.0F, 3.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(40, 3).addBox(-4.0F, 2.0F, 3.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone2 = cauldron.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(30, 0).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-5.0F, -1.0F, 8.5F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 2.5F));

		PartDefinition bone = bone2.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-4.5F, 0.0F, 4.5F));

		PartDefinition Body_r1 = bone.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(30, 0).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition Body_r2 = bone.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(30, 0).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.bodyParts().forEach((modelPart -> modelPart.visible = !entityIn.isInvisible()));
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.bodyParts().forEach((modelPart -> modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)));
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body);
	}
}