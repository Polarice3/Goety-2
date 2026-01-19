package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.IceologerServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.Polarice3.Goety.utils.MathHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

public class IceologerModel<T extends LivingEntity> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart upperBody;
    private final ModelPart rightLeg;
	private final ModelPart leftLeg;
    private final ModelPart body;
	private final ModelPart clothes;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart rightArm;
	private final ModelPart leftArm;

	public IceologerModel(ModelPart root) {
		this.root = root;
        this.illager = root.getChild("illager");
		this.rightLeg = this.illager.getChild("rightLeg");
		this.leftLeg = this.illager.getChild("leftLeg");
        this.upperBody = this.illager.getChild("upperBody");
		this.body = this.upperBody.getChild("body");
		this.clothes = this.body.getChild("clothes");
		this.head = this.upperBody.getChild("head");
		this.hat = this.head.getChild("hat");
		this.rightArm = this.upperBody.getChild("right_arm");
		this.leftArm = this.upperBody.getChild("left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightLeg = illager.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.97F, 12.0F, 0.5F, 0.1309F, 0.0F, 0.0524F));

		PartDefinition leftLeg = illager.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 12.0F, 0.2F, -0.0873F, 0.0F, -0.0873F));

		PartDefinition upperBody = illager.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.3491F, 0.0436F, 0.0175F));

		PartDefinition body = upperBody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition clothes = body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -12.0F, 0.0F, -0.1527F, 0.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.25F, -34.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(-0.01F)), PartPose.offset(-0.25F, 24.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition right_arm = upperBody.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.25F, -10.0F, 0.8F, 0.1745F, 0.2618F, -0.0436F));

		PartDefinition left_arm = upperBody.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.25F, -9.8F, 0.0F, -0.4363F, -0.0436F, -0.1745F));

		PartDefinition cape = upperBody.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(68, 0).addBox(-4.5F, -1.0F, 0.5F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 3.0F, 0.3054F, 0.1309F, -0.0873F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F) - MathHelper.modelDegrees(8.75F);
		if (entity instanceof IceologerServant servant){
			this.animate(servant.idleAnimationState, IDLE, ageInTicks);
			this.animate(servant.attackAnimationState, SPELL, ageInTicks);
			if (servant.getCurrentAnimation() != servant.getAnimationState("attack")){
				this.animateWalk(WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
		if (entity instanceof BoundIceologer servant){
			this.animate(servant.idleAnimationState, IDLE, ageInTicks);
			this.animate(servant.attackAnimationState, SPELL, ageInTicks);
			if (servant.getCurrentAnimation() != servant.getAnimationState("attack")){
				this.animateWalk(WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
		boolean flag2 = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
				|| entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
		this.clothes.visible = !flag2;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private ModelPart getArm(HumanoidArm p_102923_) {
		return p_102923_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	public ModelPart getHat() {
		return this.hat;
	}

	public ModelPart getHead() {
		return this.head;
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		this.getArm(arm).translateAndRotate(poseStack);
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, -1.5F, 0.0F);
		poseStack.scale(1.05F, 1.05F, 1.05F);
	}

	@Override
	public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
	}

	@Override
	public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.scale(1.05F, 1.05F, 1.05F);
	}

	public Iterable<ModelPart> rightHandArmors() {
		return ImmutableList.of(this.rightArm);
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of(this.leftArm);
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.rightLeg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.leftLeg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}

	public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(2.95F).looping()
			.addAnimation("upperBody", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.5F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.95F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.95F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.95F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.95F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("cape", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -10.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.95F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition SPELL = AnimationDefinition.Builder.withLength(3.3F)
			.addAnimation("upperBody", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(-30.0F, 12.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(-25.4724F, -6.509F, -6.8912F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(-40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(-42.5F, 10.0F, -3.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(27.5F, -10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(27.5F, -10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("upperBody", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(10.0303F, 12.497F, 0.2771F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(-2.5107F, -0.1631F, 7.4982F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(-9.14F, -4.7137F, -6.3135F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(-25.0F, 5.0F, -6.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(-25.0F, 5.0F, -6.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(-10.0F, -10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(-96.0295F, -11.9586F, 52.6054F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(-140.0F, -10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(-100.0F, -20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(-187.5F, -20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(-186.868F, -19.631F, -7.5175F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(-56.4138F, -7.1672F, -7.1198F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(-56.4138F, -7.1672F, -7.1198F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(-30.0F, 2.5F, 7.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(-72.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(-60.7838F, 8.6342F, 22.0555F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(-60.1585F, 6.8859F, 8.0535F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(-60.1585F, 6.8859F, 8.0535F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(-30.6164F, 12.4617F, 0.621F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(64.3836F, 12.4617F, 0.621F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("cape", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(7.4229F, -13.6127F, -1.3157F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(9.7873F, -4.4399F, 12.6903F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(14.7873F, -4.4399F, 12.6903F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(4.8658F, -4.7706F, 3.4965F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(12.3658F, -4.7706F, 3.4965F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.8F, KeyframeAnimations.degreeVec(12.3658F, -4.7706F, 3.4965F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("nose", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.05F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.15F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.45F).looping()
			.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.15F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.5F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("upperBody", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-7.5F, -5.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.5F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(-7.5F, -5.0F, -1.5F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("upperBody", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.5F, 5.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-1.0F, 3.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.5F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.0F, -0.5F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.25F, KeyframeAnimations.degreeVec(-1.0F, 3.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(-1.5F, 5.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.25F, -0.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 0.25F, -0.2F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.25F, -0.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(10.0F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(10.0F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(-7.5F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.85F, KeyframeAnimations.degreeVec(-10.0F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.05F, KeyframeAnimations.degreeVec(-10.0F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(10.0F, -15.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-18.0F, 2.5F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.55F, KeyframeAnimations.degreeVec(27.5F, 2.5F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.75F, KeyframeAnimations.degreeVec(45.0F, 2.5F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.9F, KeyframeAnimations.degreeVec(45.0F, 2.5F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(-18.0F, 2.5F, 10.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("cape", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -7.5F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.5F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.45F, KeyframeAnimations.degreeVec(0.0F, -7.5F, 5.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
}