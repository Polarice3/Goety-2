package com.Polarice3.Goety.client.render.model;// Made with Blockbench 4.10.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SummonCircleBossModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart summon;
	private final ModelPart base;
	private final ModelPart sides;

	public SummonCircleBossModel(ModelPart root) {
		this.summon = root.getChild("summon");
		this.base = this.summon.getChild("base");
		this.sides = this.summon.getChild("sides");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition summon = partdefinition.addOrReplaceChild("summon", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = summon.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = base.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -9.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition sides = summon.addOrReplaceChild("sides", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -5.0F, 8.0F, 16.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-8.0F, -5.0F, -8.0F, 16.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(8.0F, -5.0F, -8.0F, 0.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -5.0F, -8.0F, 0.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		summon.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return summon;
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

	}
}