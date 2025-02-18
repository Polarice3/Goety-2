package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpiderEggModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart egg;

	public SpiderEggModel(ModelPart root) {
		this.root = root;
		this.egg = root.getChild("egg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition egg = partdefinition.addOrReplaceChild("egg", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.0F, -7.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 16.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.walk(this.egg, 0.1F, 0.1F, true, 1, 0, ageInTicks, 1);
		this.flap(this.egg, 0.1F, 0.1F, false, 0, 0, ageInTicks, 1);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public void walk(ModelPart modelPart, float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount) {
		modelPart.xRot += this.calculateRotation(speed, degree, invert, offset, weight, walk, walkAmount);
	}

	public void flap(ModelPart modelPart, float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount) {
		modelPart.zRot += this.calculateRotation(speed, degree, invert, offset, weight, flap, flapAmount);
	}

	private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
		float movementScale = 1.0F;
		float rotation = (Mth.cos(f * (speed * movementScale) + offset) * (degree * movementScale) * f1) + (weight * f1);
		return invert ? -rotation : rotation;
	}
}