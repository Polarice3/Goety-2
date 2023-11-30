package com.Polarice3.Goety.client.render.model;


import com.Polarice3.Goety.client.render.animation.VanguardAnimations;
import com.Polarice3.Goety.common.entities.ally.VanguardServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VanguardModel<T extends VanguardServant> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightArm;
	private final ModelPart shield;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public VanguardModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.rightArm = this.body.getChild("right_arm");
		this.shield = this.rightArm.getChild("shield");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -10.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-1.0F, -12.0F, -6.0F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet2 = helmet.addOrReplaceChild("helmet2", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -10.0F, -6.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(58, 0).addBox(-1.0F, -10.0F, 5.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(40, 0).addBox(-1.0F, -10.0F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition armor = body.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(0, 50).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(36, 119).addBox(-4.0F, 9.0F, -2.5F, 8.0F, 3.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -8.0F, 0.0F));

		PartDefinition shield = right_arm.addOrReplaceChild("shield", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = shield.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 66).addBox(-1.0F, 13.0F, 12.0F, 16.0F, 21.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 87).addBox(-1.0F, 13.0F, 13.0F, 16.0F, 21.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 23.0F, -22.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition ridge = shield.addOrReplaceChild("ridge", CubeListBuilder.create().texOffs(0, 60).addBox(-5.0F, -0.5F, 3.625F, 0.0F, 1.0F, 10.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(-8.0F, -0.5F, 3.625F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 65).addBox(-8.0F, -0.5F, -1.375F, 0.0F, 1.0F, 5.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(-8.0F, -0.5F, -1.375F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 66).addBox(-6.0F, -0.5F, -5.375F, 0.0F, 1.0F, 4.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(-6.0F, -0.5F, -5.375F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 68).addBox(-3.0F, -0.5F, -7.375F, 0.0F, 1.0F, 2.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(-3.0F, -0.5F, -7.375F, 6.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 68).addBox(3.0F, -0.5F, -7.375F, 0.0F, 1.0F, 2.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(3.0F, -0.5F, -5.375F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 66).addBox(6.0F, -0.5F, -5.375F, 0.0F, 1.0F, 4.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(5.0F, -0.5F, -1.375F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 65).addBox(8.0F, -0.5F, -1.375F, 0.0F, 1.0F, 5.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(5.0F, -0.5F, 3.625F, 3.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 60).addBox(5.0F, -0.5F, 3.625F, 0.0F, 1.0F, 10.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 70).addBox(-5.0F, -0.5F, 13.625F, 10.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 10.5F, -1.625F));

		PartDefinition ridge2 = shield.addOrReplaceChild("ridge2", CubeListBuilder.create().texOffs(2, 75).addBox(-6.0F, -0.5F, 5.625F, 0.0F, 1.0F, 8.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 83).addBox(-7.0F, -0.5F, 5.625F, 1.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 72).addBox(-7.0F, -0.5F, -5.375F, 0.0F, 1.0F, 11.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 72).addBox(7.0F, -0.5F, -5.375F, 0.0F, 1.0F, 11.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 83).addBox(6.0F, -0.5F, 5.625F, 1.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(2, 75).addBox(6.0F, -0.5F, 5.625F, 0.0F, 1.0F, 8.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 83).addBox(-6.0F, -0.5F, 13.625F, 12.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 10.5F, -1.625F));

		PartDefinition top = ridge2.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 83).addBox(-11.5F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 81).addBox(-7.5F, -0.5F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 83).addBox(-7.5F, -0.5F, -2.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 81).addBox(-1.5F, -0.5F, -2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(-0.01F))
				.texOffs(0, 83).addBox(-1.5F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, new CubeDeformation(-0.01F)), PartPose.offset(4.5F, 0.0F, -5.375F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -8.0F, 0.0F));

		PartDefinition glaive = left_arm.addOrReplaceChild("glaive", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -3.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bits = glaive.addOrReplaceChild("bits", CubeListBuilder.create().texOffs(0, 91).addBox(0.0F, -16.0F, -15.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 91).addBox(0.0F, -15.0F, -14.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 91).addBox(0.0F, -14.0F, -13.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 91).addBox(0.0F, -13.0F, -12.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 91).addBox(0.0F, -12.0F, -11.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 91).addBox(0.0F, -11.0F, -10.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 91).addBox(0.0F, -10.0F, -9.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 91).addBox(0.0F, -9.0F, -8.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(16, 91).addBox(0.0F, -8.0F, -7.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 90).addBox(0.0F, -7.0F, -6.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 89).addBox(0.0F, -3.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 92).addBox(0.0F, -6.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 89).addBox(0.0F, -4.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(3, 88).addBox(0.0F, -3.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(3, 88).addBox(0.0F, -2.0F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 1.0F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 2.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(11, 88).addBox(0.0F, 3.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 88).addBox(0.0F, 4.0F, 4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 88).addBox(0.0F, 5.0F, 5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(15, 88).addBox(0.0F, 6.0F, 6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 7.0F, 7.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 8.0F, 8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 9.0F, 9.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(7, 88).addBox(0.0F, 10.0F, 10.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 84).addBox(0.0F, 11.0F, 11.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(6, 86).addBox(0.0F, 13.0F, 13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition leg_armor_R = right_leg.addOrReplaceChild("leg_armor_R", CubeListBuilder.create().texOffs(42, 49).addBox(-2.5F, -3.0F, -2.5F, 5.0F, 9.0F, 6.0F, new CubeDeformation(-0.49F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition leg_armor_L = left_leg.addOrReplaceChild("leg_armor_L", CubeListBuilder.create().texOffs(42, 31).addBox(-2.5F, -3.0F, -2.5F, 5.0F, 9.0F, 6.0F, new CubeDeformation(-0.49F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
			if (entity.isMeleeAttacking()) {
				this.animateWalk(limbSwing, limbSwingAmount);
			}
		}
		Vec3 velocity = entity.getDeltaMovement();
		float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		this.animate(entity.idleAnimationState, VanguardAnimations.IDLE, ageInTicks);
		if (this.riding){
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = ((float)Math.PI / 10F);
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = (-(float)Math.PI / 10F);
			this.leftLeg.zRot = -0.07853982F;
		} else {
			this.animate(entity.walkAnimationState, VanguardAnimations.WALK, ageInTicks, groundSpeed * 10);
		}
		this.animate(entity.attackAnimationState, VanguardAnimations.ATTACK, ageInTicks);
		this.animate(entity.stayingAnimationState, VanguardAnimations.STAYING, ageInTicks);
		this.shield.visible = entity.hasShield();
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	private void animateWalk(float limbSwing, float limbSwingAmount){
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}