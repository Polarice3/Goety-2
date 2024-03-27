package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class InfernoModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart middle_rods;
	private final ModelPart extra_rods;
	private final ModelPart bottom_rod;
	private final ModelPart circle1;
	private final ModelPart circle2;

	public InfernoModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.middle_rods = root.getChild("middle_rods");
		this.extra_rods = root.getChild("extra_rods");
		this.bottom_rod = root.getChild("bottom_rod");
		this.circle1 = root.getChild("circle1");
		this.circle2 = root.getChild("circle2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Head_r1 = head2.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(58, 16).addBox(-1.0F, -9.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(5.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition Head_r2 = head2.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(58, 16).addBox(0.0F, -8.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5.5F, -1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition middle_rods = partdefinition.addOrReplaceChild("middle_rods", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition rod1 = middle_rods.addOrReplaceChild("rod1", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -3.0F, -1.0F));

		PartDefinition rod2 = middle_rods.addOrReplaceChild("rod2", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -3.0F, -1.0F));

		PartDefinition rod3 = middle_rods.addOrReplaceChild("rod3", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, 3.0F));

		PartDefinition rod4 = middle_rods.addOrReplaceChild("rod4", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, -5.0F));

		PartDefinition extra_rods = partdefinition.addOrReplaceChild("extra_rods", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition rod5 = extra_rods.addOrReplaceChild("rod5", CubeListBuilder.create().texOffs(8, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, -6.0F));

		PartDefinition rod6 = extra_rods.addOrReplaceChild("rod6", CubeListBuilder.create().texOffs(8, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, -6.0F));

		PartDefinition rod7 = extra_rods.addOrReplaceChild("rod7", CubeListBuilder.create().texOffs(8, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 4.0F));

		PartDefinition rod8 = extra_rods.addOrReplaceChild("rod8", CubeListBuilder.create().texOffs(8, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, 4.0F));

		PartDefinition bottom_rod = partdefinition.addOrReplaceChild("bottom_rod", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition circle1 = partdefinition.addOrReplaceChild("circle1", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -2.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition circle2 = partdefinition.addOrReplaceChild("circle2", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, -2.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float f0 = entity instanceof BlazeServant blaze && blaze.isCharged() ? 10.0F : 1.0F;
		float f = ageInTicks * (float)Math.PI * 0.03F;
		float f1 = (Mth.sin(ageInTicks / 5.0F) / 4.0F);
		float f2 = (Mth.cos(ageInTicks / 5.0F) / 4.0F);
		float f3 = (((float)Math.PI / 4F) + ageInTicks * (float)Math.PI * 0.03F);
		f *= f0;
		f3 *= f0;

		this.middle_rods.yRot = f;

		this.extra_rods.yRot = -f;

		this.circle2.xRot = -f1;
		this.circle2.yRot = -f;
		this.circle2.zRot = -f2;

		this.circle1.xRot = f1;
		this.circle1.yRot = f3;
		this.circle1.zRot = f2;

		this.bottom_rod.xRot = Math.min(limbSwingAmount / 0.3F, 1.0F);

		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}