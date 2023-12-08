package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class MiniGhastModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart ghast;
	private final ModelPart[] tentacles = new ModelPart[9];

	public MiniGhastModel(ModelPart root) {
		this.root = root;
		this.ghast = this.root.getChild("ghast");
		for(int i = 0; i < this.tentacles.length; ++i) {
			this.tentacles[i] = this.ghast.getChild(createTentacleName(i));
		}
	}

	private static String createTentacleName(int p_170573_) {
		return "tentacle_" + p_170573_;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition ghast = partdefinition.addOrReplaceChild("ghast", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition tentacle_0 = ghast.addOrReplaceChild("tentacle_0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 5.0F, -3.0F));

		PartDefinition tentacle_1 = ghast.addOrReplaceChild("tentacle_1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, -3.0F));

		PartDefinition tentacle_2 = ghast.addOrReplaceChild("tentacle_2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 5.0F, -3.0F));

		PartDefinition tentacle_3 = ghast.addOrReplaceChild("tentacle_3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 5.0F, 0.0F));

		PartDefinition tentacle_4 = ghast.addOrReplaceChild("tentacle_4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, 0.0F));

		PartDefinition tentacle_5 = ghast.addOrReplaceChild("tentacle_5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 5.0F, 0.0F));

		PartDefinition tentacle_6 = ghast.addOrReplaceChild("tentacle_6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 5.0F, 3.0F));

		PartDefinition tentacle_7 = ghast.addOrReplaceChild("tentacle_7", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, 3.0F));

		PartDefinition tentacle_8 = ghast.addOrReplaceChild("tentacle_8", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 5.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 48, 24);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		for(int i = 0; i < this.tentacles.length; ++i) {
			this.tentacles[i].xRot = 0.2F * Mth.sin(ageInTicks * 0.3F + (float)i) + 0.4F;
		}
		this.ghast.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.ghast.xRot = headPitch * ((float)Math.PI / 180F);
	}

	public ModelPart root() {
		return this.root;
	}
}