package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SmackStoneModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart rock;

	public SmackStoneModel(ModelPart root) {
		this.root = root;
		this.rock = root.getChild("rock");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rock = partdefinition.addOrReplaceChild("rock", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition bone = rock.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.5F));

		PartDefinition bone2 = rock.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition bone3 = rock.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bone4 = rock.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition bone5 = rock.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition bone6 = rock.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
	}

	public void setupAnim(float p_103811_, float p_103812_, float p_103813_) {
		this.rock.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.rock.xRot = p_103813_ * ((float)Math.PI / 180F);
	}
}