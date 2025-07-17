package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.DrownedNecromancerAnimations;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class DrownedNecromancerModel<T extends DrownedNecromancer> extends HierarchicalModel<T> {
	public final ModelPart root;
	private final ModelPart skeleton;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart torso;
	private final ModelPart cape;
	private final ModelPart right_arm;
	private final ModelPart staff;
	private final ModelPart handle;
	private final ModelPart group;
	private final ModelPart staffhead;
	private final ModelPart right_pauldron;
	private final ModelPart left_arm;
	private final ModelPart left_pauldron;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public DrownedNecromancerModel(ModelPart root) {
		this.root = root;
		this.skeleton = root.getChild("skeleton");
		this.body = this.skeleton.getChild("body");
		this.head = this.body.getChild("head");
		this.hat = this.head.getChild("hat");
		this.torso = this.body.getChild("torso");
		this.cape = this.torso.getChild("cape");
		this.right_arm = this.body.getChild("right_arm");
		this.staff = this.right_arm.getChild("staff");
		this.handle = this.staff.getChild("handle");
		this.group = this.staff.getChild("group");
		this.staffhead = this.staff.getChild("staffhead");
		this.right_pauldron = this.right_arm.getChild("right_pauldron");
		this.left_arm = this.body.getChild("left_arm");
		this.left_pauldron = this.left_arm.getChild("left_pauldron");
		this.right_leg = this.skeleton.getChild("right_leg");
		this.left_leg = this.skeleton.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
				.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(28, 93).addBox(-6.0F, -5.75F, -4.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(1.0F, -4.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 32).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition cape = torso.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F, 0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 1.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 70).addBox(-2.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

		PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 5.5F, 1.4399F, 0.0F, 0.0F));

		PartDefinition handle = staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(60, 39).addBox(0.5F, -20.0F, -19.0F, 1.0F, 28.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));

		PartDefinition group = staff.addOrReplaceChild("group", CubeListBuilder.create().texOffs(56, 48).mirror().addBox(2.5F, -22.0F, -19.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(56, 48).addBox(-1.5F, -22.0F, -19.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(56, 56).addBox(-2.5F, -26.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(56, 56).addBox(3.5F, -26.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(56, 56).addBox(0.5F, -28.0F, -19.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));

		PartDefinition staffhead = staff.addOrReplaceChild("staffhead", CubeListBuilder.create().texOffs(48, 50).addBox(-0.5F, -23.0F, -20.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));

		PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -4.0F, -3.5F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 86).mirror().addBox(-1.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron", CubeListBuilder.create().texOffs(0, 59).mirror().addBox(-1.0F, -4.0F, -3.5F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition right_leg = skeleton.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(32, 112).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-2.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition left_leg = skeleton.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(48, 112).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(2.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			if (entity.cantDo > 0){
				this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
				this.head.xRot = 0.4F;
			} else {
				this.animateHeadLookTarget(netHeadYaw, headPitch);
			}
		}
		if (entity.isInWaterOrBubble()){
			this.animate(entity.idleAnimationState, DrownedNecromancerAnimations.IDLE_SWIM, ageInTicks);
		} else {
			this.animate(entity.idleAnimationState, DrownedNecromancerAnimations.IDLE, ageInTicks);
		}
		if (this.riding){
			this.right_leg.xRot = -1.4137167F;
			this.right_leg.yRot = ((float)Math.PI / 10F);
			this.right_leg.zRot = 0.07853982F;
			this.left_leg.xRot = -1.4137167F;
			this.left_leg.yRot = (-(float)Math.PI / 10F);
			this.left_leg.zRot = -0.07853982F;
		} else {
			if (entity.isInWaterOrBubble()){
				this.animateWalk(DrownedNecromancerAnimations.SWIM, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			} else {
				this.animateWalk(DrownedNecromancerAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
		this.animate(entity.attackAnimationState, DrownedNecromancerAnimations.SHOOT, ageInTicks, entity.getAttackSpeed());
		this.animate(entity.summonAnimationState, DrownedNecromancerAnimations.SUMMON, ageInTicks);
		this.animate(entity.spellAnimationState, DrownedNecromancerAnimations.SPELL, ageInTicks);
		this.animate(entity.stormAnimationState, DrownedNecromancerAnimations.STORM, ageInTicks);
		this.animate(entity.rapidAnimationState, DrownedNecromancerAnimations.RAPID, ageInTicks);
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