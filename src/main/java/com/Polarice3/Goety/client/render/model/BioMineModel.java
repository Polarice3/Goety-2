package com.Polarice3.Goety.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BioMineModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart mine;
	private final ModelPart body;
	private final ModelPart kelp;
	private final ModelPart spikes;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart sides;

	public BioMineModel(ModelPart root) {
		this.mine = root.getChild("mine");
		this.body = this.mine.getChild("body");
		this.kelp = this.body.getChild("kelp");
		this.spikes = this.mine.getChild("spikes");
		this.top = this.spikes.getChild("top");
		this.bottom = this.spikes.getChild("bottom");
		this.sides = this.spikes.getChild("sides");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mine = partdefinition.addOrReplaceChild("mine", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition body = mine.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition kelp = body.addOrReplaceChild("kelp", CubeListBuilder.create().texOffs(16, 0).addBox(-8.0F, 8.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(16, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = kelp.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, -1.5708F, 0.0F, -1.5708F));

		PartDefinition cube_r2 = kelp.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 1.5708F));

		PartDefinition cube_r3 = kelp.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r4 = kelp.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition spikes = mine.addOrReplaceChild("spikes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition top = spikes.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(7.5F, -15.5F, 0.0F));

		PartDefinition cube_r5 = top.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

		PartDefinition cube_r6 = top.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -0.7854F));

		PartDefinition cube_r7 = top.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 0.0F, -7.5F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r8 = top.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 0.0F, 7.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition bottom = spikes.addOrReplaceChild("bottom", CubeListBuilder.create(), PartPose.offset(7.5F, -0.5F, 0.0F));

		PartDefinition cube_r9 = bottom.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 2.3562F));

		PartDefinition cube_r10 = bottom.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -2.3562F));

		PartDefinition cube_r11 = bottom.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 0.0F, -7.5F, 2.3562F, 0.0F, 0.0F));

		PartDefinition cube_r12 = bottom.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 0.0F, 7.5F, -2.3562F, 0.0F, 0.0F));

		PartDefinition sides = spikes.addOrReplaceChild("sides", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r13 = sides.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5F, -8.0F, -7.5F, 0.7854F, 0.0F, 1.5708F));

		PartDefinition cube_r14 = sides.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, -8.0F, -7.5F, 2.3562F, 0.0F, 1.5708F));

		PartDefinition cube_r15 = sides.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, -8.0F, 7.5F, -2.3562F, 0.0F, 1.5708F));

		PartDefinition cube_r16 = sides.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.5F, -8.0F, 7.5F, -0.7854F, 0.0F, 1.5708F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.mine.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.mine.xRot = headPitch * ((float)Math.PI / 180F);
	}

	public void setupAnim(float p_225603_2_, float p_225603_3_) {
		this.mine.yRot = p_225603_2_ * ((float)Math.PI / 180F);
		this.mine.xRot = p_225603_3_ * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.mine.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}