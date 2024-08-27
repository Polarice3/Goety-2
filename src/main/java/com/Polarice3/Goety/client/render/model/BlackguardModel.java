package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.BlackguardAnimations;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BlackguardModel<T extends BlackguardServant> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart blackguard;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart body;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart shield;
	private final ModelPart head;

	public BlackguardModel(ModelPart root) {
		this.root = root;
		this.blackguard = root.getChild("blackguard");
		this.right_leg = this.blackguard.getChild("right_leg");
		this.left_leg = this.blackguard.getChild("left_leg");
		this.body = this.blackguard.getChild("body");
		this.right_arm = this.body.getChild("right_arm");
		this.left_arm = this.body.getChild("left_arm");
		this.shield = this.left_arm.getChild("shield");
		this.head = this.body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition blackguard = partdefinition.addOrReplaceChild("blackguard", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition right_leg = blackguard.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 1.5F, 0.0873F, 0.6109F, 0.0436F));

		PartDefinition right_boot = right_leg.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(60, 15).addBox(-3.4F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(24, 0).addBox(-3.4F, -8.5F, -3.25F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.4F, 12.0F, 0.0F));

		PartDefinition left_leg = blackguard.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.9F, 0.0F, -3.0F, -0.0873F, -0.3491F, 0.0F));

		PartDefinition left_boot = left_leg.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(60, 15).addBox(0.75F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(24, 0).addBox(0.5F, -8.5F, -3.25F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.65F, 12.0F, 0.0F));

		PartDefinition body = blackguard.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition armor = body.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(24, 41).addBox(-2.5F, 0.0F, 0.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(-2.5F, -12.0F, -3.0F));

		PartDefinition upperBody_r1 = armor.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(56, 41).addBox(-3.0F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(56, 48).addBox(2.5F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.25F, -10.0F, 1.5F, 0.48F, 0.6981F, 0.6981F));

		PartDefinition right_gauntlet = right_arm.addOrReplaceChild("right_gauntlet", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, 0.0F));

		PartDefinition rightArm_r1 = right_gauntlet.addOrReplaceChild("rightArm_r1", CubeListBuilder.create().texOffs(48, 10).addBox(1.5F, 1.0F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition rightArm_r2 = right_gauntlet.addOrReplaceChild("rightArm_r2", CubeListBuilder.create().texOffs(62, 0).addBox(-3.0F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(62, 10).addBox(-3.0F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition rightArm_r3 = right_gauntlet.addOrReplaceChild("rightArm_r3", CubeListBuilder.create().texOffs(7, 36).addBox(5.0F, -2.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(48, 0).addBox(1.0F, -3.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition rightItem = right_arm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-1.0F, 7.0F, 1.0F));

		PartDefinition mace = rightItem.addOrReplaceChild("mace", CubeListBuilder.create().texOffs(0, 42).addBox(-9.0F, 7.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 45).addBox(-11.0F, -4.0F, -7.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 47).addBox(-9.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, -7.0F, 0.8802F, 0.1119F, 0.1343F));

		PartDefinition mace_r1 = mace.addOrReplaceChild("mace_r1", CubeListBuilder.create().texOffs(0, 47).addBox(5.0F, -5.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition mace_r2 = mace.addOrReplaceChild("mace_r2", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, 8.0F, 5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition mace_r3 = mace.addOrReplaceChild("mace_r3", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, -10.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(-1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-1.0F, -4.0F, 9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-1.0F, -3.0F, 8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-1.0F, -2.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 42).addBox(-1.0F, -1.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(-1.0F, 1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition mace_r4 = mace.addOrReplaceChild("mace_r4", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, 1.0F, -13.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition mace_r5 = mace.addOrReplaceChild("mace_r5", CubeListBuilder.create().texOffs(0, 47).addBox(-6.0F, -4.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition mace_r6 = mace.addOrReplaceChild("mace_r6", CubeListBuilder.create().texOffs(0, 55).addBox(-4.0F, -11.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 55).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition mace_r7 = mace.addOrReplaceChild("mace_r7", CubeListBuilder.create().texOffs(0, 38).addBox(0.0F, -10.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(0.0F, -9.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition mace_r8 = mace.addOrReplaceChild("mace_r8", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(0.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(0.0F, -7.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 42).addBox(0.0F, -4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 42).addBox(0.0F, -5.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, -11.0F, 1.5F, -1.0472F, 0.0F, 0.0F));

		PartDefinition left_gauntlet = left_arm.addOrReplaceChild("left_gauntlet", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -3.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(7, 36).addBox(5.0F, -2.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(48, 10).addBox(1.5F, 0.0F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

		PartDefinition leftArm_r1 = left_gauntlet.addOrReplaceChild("leftArm_r1", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(62, 0).addBox(-3.0F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition shield = left_arm.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(80, 0).addBox(-7.0F, -10.5F, -1.0F, 14.0F, 22.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 11.5F, -2.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(30, 22).addBox(-5.0F, -34.5F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(8, 32).mirror().addBox(5.0F, -33.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 32).addBox(6.0F, -36.5F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(8, 32).addBox(-8.0F, -33.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-8.0F, -36.5F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(18, 32).addBox(-2.0F, -37.5F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.idleAnimationState, BlackguardAnimations.IDLE, ageInTicks);
		this.animate(entity.standAnimationState, BlackguardAnimations.STAND, ageInTicks);
		this.animate(entity.attackAnimationState, BlackguardAnimations.ATTACK, ageInTicks);
		this.animateWalk(BlackguardAnimations.WALK, limbSwing, limbSwingAmount, 1.0F, 2.5F );
		this.shield.visible = entity.hasShield();
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