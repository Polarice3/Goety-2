package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class ShieldDebrisModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart bolt;

	public ShieldDebrisModel(ModelPart root) {
		this.root = root;
		this.bolt = root.getChild("bolt");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bolt = partdefinition.addOrReplaceChild("bolt", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition cube_r1 = bolt.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
	}

	public void setupAnim(float p_103811_, float p_103812_, float p_103813_) {
		this.bolt.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.bolt.xRot = p_103813_ * ((float)Math.PI / 180F);
	}
}