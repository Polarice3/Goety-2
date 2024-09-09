package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.WildfireAnimations;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class WildfireModel<T extends Wildfire> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart wildfire;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart shields;
	private final ModelPart[] shieldParts = new ModelPart[4];

	public WildfireModel(ModelPart root) {
		this.root = root;
		this.wildfire = root.getChild("wildfire");
		this.body = this.wildfire.getChild("body");
		this.head = this.body.getChild("head");
		this.shields = this.wildfire.getChild("shields");
		for(int i = 0; i < this.shieldParts.length; ++i) {
			this.shieldParts[i] = this.shields.getChild(createShieldName(i));
		}
	}

	private static String createShieldName(int p_170573_) {
		return "shield" + p_170573_;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition wildfire = partdefinition.addOrReplaceChild("wildfire", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = wildfire.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -21.0F, -2.0F, 4.0F, 21.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 47).addBox(-4.0F, -8.95F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -21.0F, 0.1F));

		PartDefinition shields = wildfire.addOrReplaceChild("shields", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shield0 = shields.addOrReplaceChild("shield0", CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, -9.0F));

		PartDefinition shield1 = shields.addOrReplaceChild("shield1", CubeListBuilder.create(), PartPose.offset(9.0F, -22.0F, 0.0F));

		PartDefinition shield1_r1 = shield1.addOrReplaceChild("shield1_r1", CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, -22.0F, 8.0F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition shield2 = shields.addOrReplaceChild("shield2", CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, 9.0F));

		PartDefinition shield3 = shields.addOrReplaceChild("shield3", CubeListBuilder.create(), PartPose.offset(-9.0F, -22.0F, 0.0F));

		PartDefinition shield3_r1 = shield3.addOrReplaceChild("shield3_r1", CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, -22.0F, -10.0F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.spinAnimationState, WildfireAnimations.SPIN, ageInTicks);
		this.animate(entity.idleAnimationState, WildfireAnimations.IDLE, ageInTicks);
		this.animate(entity.shockwaveAnimationState, WildfireAnimations.SHOCKWAVE, ageInTicks);
		this.animate(entity.shootAnimationState, WildfireAnimations.SHOOT, ageInTicks);
		if (entity.canAnimateMove()) {
			this.animateWalk(WildfireAnimations.WALK, limbSwing, limbSwingAmount, 1.0F, 2.5F);
		}
		for (int i = 0; i < 4; ++i){
			this.shieldParts[i].visible = i < entity.getShields();
		}
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}