package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.VoidShock;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class VoidShockModel<T extends VoidShock> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart bolt;

	public VoidShockModel(ModelPart root) {
		this.root = root;
		this.bolt = root.getChild("bolt");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bolt = partdefinition.addOrReplaceChild("bolt", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void setupAnim(T entity, float p_103812_, float p_103813_) {
		if (entity.hasTarget) {
			this.bolt.yRot = p_103812_ * ((float) Math.PI / 180F);
			this.bolt.xRot = p_103813_ * ((float) Math.PI / 180F);
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}