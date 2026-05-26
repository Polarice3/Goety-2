package com.Polarice3.Goety.client.render.model;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SpriteMobModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart sprite;

	public SpriteMobModel(ModelPart root) {
		this.root = root;
		this.sprite = root.getChild("sprite");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition sprite = partdefinition.addOrReplaceChild("sprite", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition inner = sprite.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -3.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.5F, 0.5F));

		PartDefinition outer = sprite.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(0, 12).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.sprite.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.sprite.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}