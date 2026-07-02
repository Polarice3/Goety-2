package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.HeresiarchAnimations;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heresiarch;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;
import java.util.stream.Stream;

public class HeresiarchModel<T extends Entity> extends HierarchicalModel<T> implements ArmedModel {
	private final ModelPart root;
	private final ModelPart witch;
	private final ModelPart upper_body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart knife;
	public final List<String> allPartNames;

	public HeresiarchModel(ModelPart root) {
		this.root = root;
		this.witch = root.getChild("witch");
		this.upper_body = this.witch.getChild("upper_body");
		this.head = this.upper_body.getChild("head");
		this.nose = this.head.getChild("nose");
		this.right_arm = this.upper_body.getChild("right_arm");
		this.left_arm = this.upper_body.getChild("left_arm");
		this.knife = this.left_arm.getChild("knife");
		this.allPartNames = Stream.concat(Stream.of("root"), ModelUtil.getAllPartNames(this.root)).toList();
	}

	public static LayerDefinition createBodyLayer() {
		return createBodyLayer(0);
	}

	public static LayerDefinition createShadowLayer() {
		return createBodyLayer(-0.05F);
	}

	public static LayerDefinition createBodyLayer(float deformation) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition witch = partdefinition.addOrReplaceChild("witch", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition upper_body = witch.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(74, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(deformation))
				.texOffs(32, 20).addBox(-4.5F, -10.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, -15.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(18, 97).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition skull = head.addOrReplaceChild("skull", CubeListBuilder.create().texOffs(32, 0).addBox(-5.0F, -2.75F, -5.5F, 10.0F, 9.0F, 11.0F, new CubeDeformation(deformation))
				.texOffs(68, 43).addBox(-3.0F, 1.25F, -16.5F, 6.0F, 5.0F, 11.0F, new CubeDeformation(deformation))
				.texOffs(92, 77).addBox(-3.0F, 3.25F, -16.5F, 6.0F, 0.0F, 11.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition skull1 = skull.addOrReplaceChild("skull1", CubeListBuilder.create().texOffs(32, 40).addBox(-17.5F, -15.75F, 0.5F, 18.0F, 19.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-5.0F, 0.0F, 2.0F, 0.0F, 0.3927F, 0.1309F));

		PartDefinition skull2 = skull.addOrReplaceChild("skull2", CubeListBuilder.create().texOffs(32, 40).mirror().addBox(-0.5F, -15.75F, 0.5F, 18.0F, 19.0F, 0.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(5.0F, 0.0F, 2.0F, 0.0F, -0.3927F, -0.1309F));

		PartDefinition body = upper_body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 97).addBox(-7.0F, 14.0F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(deformation + 0.2F))
				.texOffs(32, 96).addBox(-7.0F, 14.0F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(deformation))
				.texOffs(64, 59).addBox(-5.5F, -0.5F, -3.5F, 11.0F, 11.0F, 7.0F, new CubeDeformation(deformation + 0.25F))
				.texOffs(0, 36).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 18.0F, 6.0F, new CubeDeformation(deformation + 0.25F))
				.texOffs(0, 60).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 15.0F, 6.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, -15.0F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cape1 = cape.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 30.0F, 6.0F, new CubeDeformation(deformation)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = cape1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(56, 96).addBox(0.0F, -10.0F, -3.0F, 0.0F, 14.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-5.0F, 15.0F, 3.0F, 0.0F, -2.3562F, 0.0F));

		PartDefinition cube_r2 = cape1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(50, 96).addBox(0.0F, -10.0F, -3.0F, 0.0F, 23.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(5.0F, 15.0F, 3.0F, 0.0F, -2.3562F, 0.0F));

		PartDefinition cube_r3 = cape1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(50, 96).addBox(0.0F, -10.0F, -3.0F, 0.0F, 23.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(5.0F, 15.0F, -3.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cape2 = cape.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 0.0F, -3.0F, 10.0F, 30.0F, 6.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition cube_r4 = cape2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(56, 96).addBox(0.0F, -10.0F, -3.0F, 0.0F, 14.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(5.0F, 15.0F, 3.0F, 0.0F, 2.3562F, 0.0F));

		PartDefinition cube_r5 = cape2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(50, 96).mirror().addBox(0.0F, -10.0F, -3.0F, 0.0F, 23.0F, 3.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(-5.0F, 15.0F, 3.0F, 0.0F, 2.3562F, 0.0F));

		PartDefinition cube_r6 = cape2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(50, 96).mirror().addBox(0.0F, -10.0F, -3.0F, 0.0F, 23.0F, 3.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(-5.0F, 15.0F, -3.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition right_arm = upper_body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-6.0F, -13.0F, 0.0F));

		PartDefinition righth_arm1 = right_arm.addOrReplaceChild("righth_arm1", CubeListBuilder.create().texOffs(96, 18).addBox(-7.0F, -29.0F, -3.5F, 4.0F, 15.0F, 5.0F, new CubeDeformation(deformation))
				.texOffs(32, 81).addBox(-7.5F, -21.5F, -4.25F, 5.0F, 6.0F, 9.0F, new CubeDeformation(deformation)), PartPose.offset(4.0F, 27.0F, 1.0F));

		PartDefinition right_shouldpad = right_arm.addOrReplaceChild("right_shouldpad", CubeListBuilder.create().texOffs(92, 88).addBox(-10.25F, -27.25F, -4.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(deformation + 0.25F)), PartPose.offset(4.0F, 25.0F, 1.0F));

		PartDefinition left_arm = upper_body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(6.0F, -13.0F, 0.0F));

		PartDefinition lefth_arm1 = left_arm.addOrReplaceChild("lefth_arm1", CubeListBuilder.create().texOffs(96, 18).mirror().addBox(-7.0F, -29.0F, -3.5F, 4.0F, 15.0F, 5.0F, new CubeDeformation(deformation)).mirror(false)
				.texOffs(32, 81).mirror().addBox(-7.5F, -21.5F, -4.25F, 5.0F, 6.0F, 9.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offset(6.0F, 27.0F, 1.0F));

		PartDefinition left_shouldpad = left_arm.addOrReplaceChild("left_shouldpad", CubeListBuilder.create().texOffs(92, 88).mirror().addBox(-8.75F, -28.25F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(deformation + 0.25F)).mirror(false), PartPose.offset(7.0F, 26.0F, 0.0F));

		PartDefinition knife = left_arm.addOrReplaceChild("knife", CubeListBuilder.create().texOffs(0, 81).addBox(-3.0F, -12.0F, -0.5F, 15.0F, 15.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(-3.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(-3.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(-2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(-1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(0.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(1.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(2.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(0.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(-1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 18).addBox(-1.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(-2.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(-1.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(0.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 81).addBox(1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(2.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(3.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 18).addBox(3.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 18).addBox(3.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 18).addBox(4.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 83).addBox(4.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(5.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(6.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(86, 18).addBox(6.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(86, 18).addBox(6.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(7.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(8.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(9.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(9.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(86, 18).addBox(9.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 85).addBox(10.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(11.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(10.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 89).addBox(9.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 89).addBox(8.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(8.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(8.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(7.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 89).addBox(6.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 89).addBox(5.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(5.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(5.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(5.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 87).addBox(4.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(60, 89).addBox(3.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 13.0F, -0.5F, -1.5708F, -0.7854F, -1.5708F));

		PartDefinition lower_body = witch.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(32, 59).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 16.0F, 6.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition boby1_r1 = lower_body.addOrReplaceChild("boby1_r1", CubeListBuilder.create().texOffs(64, 77).addBox(-4.0F, -25.0F, -3.0F, 8.0F, 16.75F, 6.0F, new CubeDeformation(deformation + -0.1F)), PartPose.offsetAndRotation(0.0F, 25.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition boby1_r2 = lower_body.addOrReplaceChild("boby1_r2", CubeListBuilder.create().texOffs(68, 20).addBox(-2.0F, -26.25F, -3.0F, 6.0F, 15.75F, 8.0F, new CubeDeformation(deformation + -0.5F)), PartPose.offsetAndRotation(-1.0F, 27.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
		if (entity instanceof Heresiarch heresiarch) {
			this.animate(heresiarch.idleAnimationState, HeresiarchAnimations.IDLE, ageInTicks);
			this.animate(heresiarch.blessAnimationState, HeresiarchAnimations.BLESS, ageInTicks);
			this.animate(heresiarch.summonAnimationState, HeresiarchAnimations.SUMMON, ageInTicks);
			this.animate(heresiarch.blastAnimationState, HeresiarchAnimations.BLAST, ageInTicks);
			this.animate(heresiarch.shootAnimationState, HeresiarchAnimations.SHOOT, ageInTicks);
			this.animate(heresiarch.barrageAnimationState, HeresiarchAnimations.BARRAGE, ageInTicks);
			this.animate(heresiarch.meleeAnimationState, HeresiarchAnimations.MELEE, ageInTicks);
			this.animate(heresiarch.chantAnimationState, HeresiarchAnimations.CHANT, ageInTicks);

			if (heresiarch.isCurrentAnimation(Heresiarch.IDLE) || heresiarch.isCurrentAnimation(Heresiarch.INSPECT)) {
				this.animateWalk(HeresiarchAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}

			if (heresiarch.isCurrentAnimation(Heresiarch.INSPECT)) {
				this.head.xRot = 0.5F;
				this.head.yRot = 0.0F;
				if (heresiarch.isLeftHanded()) {
					this.left_arm.yRot = 0.5F;
					this.left_arm.xRot = -0.9F;
				} else {
					this.right_arm.yRot = -0.5F;
					this.right_arm.xRot = -0.9F;
				}
			} else {
				this.animateHeadLookTarget(netHeadYaw, headPitch);
			}
			this.knife.visible = heresiarch.isCurrentAnimation(Heresiarch.MELEE);
		} else {
			this.disableItems();
		}
	}

	public void disableItems() {
		this.knife.visible = false;
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private ModelPart getThisArm(HumanoidArm p_191216_1_) {
		return p_191216_1_ == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
	}

	@Override
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.witch.translateAndRotate(matrixStackIn);
		this.upper_body.translateAndRotate(matrixStackIn);
		this.getThisArm(sideIn).translateAndRotate(matrixStackIn);
	}
}