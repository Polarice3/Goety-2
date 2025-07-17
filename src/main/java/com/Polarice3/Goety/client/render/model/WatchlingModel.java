package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.WatchlingAnimations;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractWatchling;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class WatchlingModel<T extends AbstractWatchling> extends HierarchicalModel<T> {
	private final ModelPart root;
    private final ModelPart head;

	public WatchlingModel(ModelPart root) {
		this.root = root;
        ModelPart watchling = root.getChild("watchling");
        ModelPart body = watchling.getChild("body");
		this.head = body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition watchling = partdefinition.addOrReplaceChild("watchling", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = watchling.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.5F, -15.5F, -3.0F, 11.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.5F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition eyes2 = body.addOrReplaceChild("eyes2", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition eye9 = eyes2.addOrReplaceChild("eye9", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -4.0F, -3.1F));

		PartDefinition eye10 = eyes2.addOrReplaceChild("eye10", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.0F, -3.1F));

		PartDefinition eye11 = eyes2.addOrReplaceChild("eye11", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 4.0F, -3.1F));

		PartDefinition eye12 = eyes2.addOrReplaceChild("eye12", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 3.1F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eye13 = eyes2.addOrReplaceChild("eye13", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.0F, 3.1F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eye14 = eyes2.addOrReplaceChild("eye14", CubeListBuilder.create(), PartPose.offset(2.0F, 5.0F, 3.1F));

		PartDefinition cube_r1 = eye14.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(34, 0).addBox(-4.0F, -1.0F, -1.0F, 4.0F, 29.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -14.5F, -1.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 29).addBox(0.0F, -2.0F, -2.0F, 4.0F, 29.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -13.5F, 0.0F, -0.0436F, 0.0F, -0.0873F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -8.0F, -4.0F, 9.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.5F, 0.0F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eye1 = eyes.addOrReplaceChild("eye1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.5F, -4.1F));

		PartDefinition eye2 = eyes.addOrReplaceChild("eye2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.5F, -4.1F));

		PartDefinition eye3 = eyes.addOrReplaceChild("eye3", CubeListBuilder.create().texOffs(0, -3).addBox(0.0F, -0.5F, -1.5F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.6F, -6.5F, 2.5F));

		PartDefinition eye4 = eyes.addOrReplaceChild("eye4", CubeListBuilder.create().texOffs(0, -3).addBox(0.0F, -0.5F, -1.5F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.6F, -1.5F, -0.5F));

		PartDefinition eye5 = eyes.addOrReplaceChild("eye5", CubeListBuilder.create(), PartPose.offset(-3.0F, -5.5F, 4.1F));

		PartDefinition cube_r2 = eye5.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eye6 = eyes.addOrReplaceChild("eye6", CubeListBuilder.create(), PartPose.offset(3.0F, -2.5F, 4.1F));

		PartDefinition cube_r3 = eye6.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eye7 = eyes.addOrReplaceChild("eye7", CubeListBuilder.create(), PartPose.offset(4.6F, -6.5F, 0.5F));

		PartDefinition cube_r4 = eye7.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, -3).addBox(0.0F, -0.5F, -1.5F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eye8 = eyes.addOrReplaceChild("eye8", CubeListBuilder.create(), PartPose.offset(4.6F, -1.5F, 2.5F));

		PartDefinition cube_r5 = eye8.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, -3).addBox(0.0F, -0.5F, -1.5F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition right_leg = watchling.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -19.0F, 0.0F, -0.0436F, 0.0F, 0.0873F));

		PartDefinition left_leg = watchling.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 43).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -19.0F, 0.0F, 0.0436F, 0.0F, -0.0436F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.blinkAnimationState, WatchlingAnimations.BLINK, ageInTicks);
		this.animate(entity.idleAnimationState, WatchlingAnimations.IDLE, ageInTicks);
		if (entity.getCurrentAnimation() != entity.getAnimationState("teleport_in")) {
			this.animateWalk(WatchlingAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.attackAnimationState, WatchlingAnimations.SWING, ageInTicks);
		this.animate(entity.smashAnimationState, WatchlingAnimations.SMASH, ageInTicks);
		this.animate(entity.teleportOutAnimationState, WatchlingAnimations.TELEPORT_OUT, ageInTicks);
		this.animate(entity.teleportInAnimationState, WatchlingAnimations.TELEPORT_IN, ageInTicks);
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