package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class HauntedBroomModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;

	public HauntedBroomModel(ModelPart root) {
        this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition broom = partdefinition.addOrReplaceChild("broom", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition handle = broom.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -0.5F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(24, 13).addBox(-1.0F, -1.0F, 5.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition end = handle.addOrReplaceChild("end", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0F, -1.0F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -9.5F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cap = handle.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(18, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -13.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition wheat = broom.addOrReplaceChild("wheat", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 16.0F));

		PartDefinition bone = wheat.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(14, 25).addBox(-2.5F, -2.5F, 1.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 5.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float f = ageInTicks * 0.1F;
		if (entity.getPassengers().isEmpty()) {
			this.root.y = Mth.sin(f) * 1.8F;
		} else {
			this.root.y = 3.0F;
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}