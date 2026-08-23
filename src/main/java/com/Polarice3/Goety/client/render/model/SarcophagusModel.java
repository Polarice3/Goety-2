package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;

public class SarcophagusModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart sarcophagus;
	private final ModelPart base;
	private final ModelPart tomb;
	public ModelPart cushion;
	private final ModelPart lid;

	public SarcophagusModel(ModelPart root) {
		this.root = root;
		this.sarcophagus = root.getChild("sarcophagus");
		this.base = this.sarcophagus.getChild("base");
		this.tomb = this.sarcophagus.getChild("tomb");
		this.cushion = this.sarcophagus.getChild("cushion");
		this.lid = this.sarcophagus.getChild("lid");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition sarcophagus = partdefinition.addOrReplaceChild("sarcophagus", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = sarcophagus.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -2.0F, -16.0F, 16.0F, 4.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 8.0F));

		PartDefinition tomb = sarcophagus.addOrReplaceChild("tomb", CubeListBuilder.create().texOffs(80, 71).addBox(6.0F, -4.5F, -15.0F, 1.0F, 9.0F, 30.0F, new CubeDeformation(0.0F))
				.texOffs(96, 39).addBox(-6.0F, -4.5F, -15.0F, 12.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(96, 49).addBox(-6.0F, -4.5F, 14.0F, 12.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(96, 0).addBox(-7.0F, -4.5F, -15.0F, 1.0F, 9.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 8.0F));

		PartDefinition cushion = sarcophagus.addOrReplaceChild("cushion", CubeListBuilder.create().texOffs(0, 71).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 1.0F, 28.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(5.0F, -12.0F, -5.0F, 1.0F, 7.0F, 26.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(-6.0F, -12.0F, -5.0F, 1.0F, 7.0F, 26.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(-6.0F, -12.0F, 21.0F, 12.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lid = sarcophagus.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, -1.5F, -16.0F, 16.0F, 3.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.5F, -8.0F, 8.0F, 0.0F, 0.0F, -1.1781F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void animate(AnimationState p_233382_, AnimationDefinition p_233383_, float p_233384_) {
		this.animate(p_233382_, p_233383_, p_233384_, 1.0F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}