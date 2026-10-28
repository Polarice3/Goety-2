package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.*;
import com.Polarice3.Goety.common.entities.ally.undead.GraveMonstrosity;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;

public class GraveMonstrosityModel<T extends GraveMonstrosity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart monstrosity;
	private final ModelPart hips;
	private final ModelPart body;
	private final ModelPart spine;
	private final ModelPart shoulders;
	private final ModelPart right_shoulder;
	private final ModelPart right_elbow;
	private final ModelPart right_wrist;
	private final ModelPart right_hand;
	private final ModelPart right_thumb;
	private final ModelPart right_front_finger;
	private final ModelPart right_back_finger;
	private final ModelPart left_shoulder;
	private final ModelPart left_elbow;
	private final ModelPart left_wrist;
	private final ModelPart left_hand;
	private final ModelPart left_thumb;
	private final ModelPart left_front_finger;
	private final ModelPart left_back_finger;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart left_horn;
	private final ModelPart horn_fire;
	private final ModelPart right_horn;
	private final ModelPart jaw;
	private final ModelPart left_eye;
	private final ModelPart center_eye;
	private final ModelPart right_eye;
	private final ModelPart J_NeckSocket;
	private final ModelPart back;
	private final ModelPart J_BackSocket_0;
	private final ModelPart J_BackSocket_1;
	private final ModelPart J_BackSocket_2;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public GraveMonstrosityModel(ModelPart root) {
		this.root = root;
		this.monstrosity = root.getChild("monstrosity");
		this.hips = this.monstrosity.getChild("hips");
		this.body = this.hips.getChild("body");
		this.spine = this.body.getChild("spine");
		this.shoulders = this.spine.getChild("shoulders");
		this.right_shoulder = this.shoulders.getChild("right_shoulder");
		this.right_elbow = this.right_shoulder.getChild("right_elbow");
		this.right_wrist = this.right_elbow.getChild("right_wrist");
		this.right_hand = this.right_wrist.getChild("right_hand");
		this.right_thumb = this.right_hand.getChild("right_thumb");
		this.right_front_finger = this.right_hand.getChild("right_front_finger");
		this.right_back_finger = this.right_hand.getChild("right_back_finger");
		this.left_shoulder = this.shoulders.getChild("left_shoulder");
		this.left_elbow = this.left_shoulder.getChild("left_elbow");
		this.left_wrist = this.left_elbow.getChild("left_wrist");
		this.left_hand = this.left_wrist.getChild("left_hand");
		this.left_thumb = this.left_hand.getChild("left_thumb");
		this.left_front_finger = this.left_hand.getChild("left_front_finger");
		this.left_back_finger = this.left_hand.getChild("left_back_finger");
		this.neck = this.shoulders.getChild("neck");
		this.head = this.neck.getChild("head");
		this.left_horn = this.head.getChild("left_horn");
		this.horn_fire = this.left_horn.getChild("horn_fire");
		this.right_horn = this.head.getChild("right_horn");
		this.jaw = this.neck.getChild("jaw");
		this.left_eye = this.neck.getChild("left_eye");
		this.center_eye = this.neck.getChild("center_eye");
		this.right_eye = this.neck.getChild("right_eye");
		this.J_NeckSocket = this.neck.getChild("J_NeckSocket");
		this.back = this.shoulders.getChild("back");
		this.J_BackSocket_0 = this.back.getChild("J_BackSocket_0");
		this.J_BackSocket_1 = this.back.getChild("J_BackSocket_1");
		this.J_BackSocket_2 = this.back.getChild("J_BackSocket_2");
		this.right_leg = this.hips.getChild("right_leg");
		this.left_leg = this.hips.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition monstrosity = partdefinition.addOrReplaceChild("monstrosity", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition hips = monstrosity.addOrReplaceChild("hips", CubeListBuilder.create(), PartPose.offset(0.0F, -17.0F, 0.0F));

		PartDefinition body = hips.addOrReplaceChild("body", CubeListBuilder.create().texOffs(326, 91).addBox(-16.0F, -8.0F, -7.0F, 32.0F, 9.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9941F, -0.1087F, 0.109F, 0.0F, 0.0F));

		PartDefinition spine = body.addOrReplaceChild("spine", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition shoulders = spine.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 0).addBox(-32.0F, 0.0F, -12.0F, 64.0F, 50.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -49.905F, -2.1751F, 0.0871F, 0.0F, 0.0F));

		PartDefinition right_shoulder = shoulders.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(158, 52).addBox(-16.0F, -2.0F, -12.0F, 32.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(270, 52).addBox(5.0F, -23.0F, -12.0F, 11.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(388, 0).addBox(-6.0F, -15.0F, 9.0F, 10.0F, 13.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(410, 0).addBox(-14.0F, -14.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(402, 14).addBox(-6.0F, -9.0F, -2.0F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(388, 14).addBox(-14.0F, -8.0F, 9.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-49.0001F, 10.0566F, 1.3052F, -0.2001F, -0.2327F, 0.1021F));

		PartDefinition right_elbow = right_shoulder.addOrReplaceChild("right_elbow", CubeListBuilder.create().texOffs(404, 52).addBox(-9.0F, -5.0F, -7.0F, 18.0F, 25.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -0.203F, 0.0647F, 0.0245F));

		PartDefinition right_wrist = right_elbow.addOrReplaceChild("right_wrist", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 15.0002F, -0.0001F, -0.2262F, 0.1452F, 0.0332F));

		PartDefinition right_hand = right_wrist.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(296, 0).addBox(-16.0F, -12.0F, -14.0F, 32.0F, 24.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition right_thumb = right_hand.addOrReplaceChild("right_thumb", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 11.0F, 0.0F, -0.1617F, 0.0399F, -0.1312F));

		PartDefinition right_front_finger = right_hand.addOrReplaceChild("right_front_finger", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, -5.0F, -0.0297F, -0.2826F, 0.2242F));

		PartDefinition right_back_finger = right_hand.addOrReplaceChild("right_back_finger", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 5.0F, -0.1743F, -0.0234F, -0.2639F));

		PartDefinition left_shoulder = shoulders.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(0, 74).addBox(-16.0F, -2.0001F, -12.0F, 32.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(112, 74).addBox(-16.0F, -23.0001F, -12.0F, 11.0F, 21.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(420, 91).addBox(-5.0F, -20.0001F, -9.0F, 17.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(48.9999F, 10.0573F, 1.3052F, -0.1915F, -0.0849F, 0.1512F));

		PartDefinition left_elbow = left_shoulder.addOrReplaceChild("left_elbow", CubeListBuilder.create().texOffs(340, 52).addBox(-9.0F, -5.0F, -7.0F, 18.0F, 25.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -0.5581F, -0.2707F, 0.0678F));

		PartDefinition left_wrist = left_elbow.addOrReplaceChild("left_wrist", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, -0.2468F, -0.0239F, -0.0301F));

		PartDefinition left_hand = left_wrist.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(176, 0).addBox(-16.0F, -12.0F, -14.0F, 32.0F, 24.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition left_thumb = left_hand.addOrReplaceChild("left_thumb", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 11.0F, 0.0F, 0.3022F, -0.3515F, -0.4412F));

		PartDefinition left_front_finger = left_hand.addOrReplaceChild("left_front_finger", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 11.0F, -5.0F, -0.0351F, 0.1355F, -0.0767F));

		PartDefinition left_back_finger = left_hand.addOrReplaceChild("left_back_finger", CubeListBuilder.create().texOffs(268, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 11.0F, 5.0F));

		PartDefinition neck = shoulders.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, -13.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(136, 119).addBox(-13.0F, -14.0F, -8.0F, 26.0F, 23.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(-16, 208).addBox(-13.0F, 6.0F, -8.0F, 26.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(182, 97).addBox(9.0F, 0.0F, -5.0F, 9.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(84, 158).addBox(-4.0F, -6.0F, -5.0F, 13.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(13.0F, -6.0F, -1.0F));

		PartDefinition horn_fire = left_horn.addOrReplaceChild("horn_fire", CubeListBuilder.create(), PartPose.offset(13.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = horn_fire.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(480, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = horn_fire.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(480, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(220, 97).addBox(-18.0F, -18.0F, -5.0F, 9.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(130, 158).addBox(-9.0F, -6.0F, -5.0F, 13.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, -6.0F, -1.0F));

		PartDefinition jaw = neck.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 157).addBox(-13.0F, -7.52F, -16.0F, 26.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(-16, 208).addBox(-13.0F, -2.52F, -16.0F, 26.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.5438F, -0.0001F, 0.3376F, -0.0524F, -0.1133F));

		PartDefinition left_eye = neck.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(12, 185).addBox(-2.0F, -3.0F, -0.1F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, 6.0F, -16.0F));

		PartDefinition center_eye = neck.addOrReplaceChild("center_eye", CubeListBuilder.create().texOffs(0, 185).addBox(-3.0F, -3.0F, -0.1F, 6.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -16.0F));

		PartDefinition right_eye = neck.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(20, 185).addBox(-2.0F, -3.0F, -0.1F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 6.0F, -16.0F));

		PartDefinition J_NeckSocket = neck.addOrReplaceChild("J_NeckSocket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition back = shoulders.addOrReplaceChild("back", CubeListBuilder.create().texOffs(258, 97).addBox(-13.0F, -8.0F, 0.0F, 26.0F, 20.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 12.0F));

		PartDefinition J_BackSocket_0 = back.addOrReplaceChild("J_BackSocket_0", CubeListBuilder.create(), PartPose.offsetAndRotation(8.0001F, -15.9999F, 4.32F, 0.0F, 0.0F, -1.5708F));

		PartDefinition J_BackSocket_1 = back.addOrReplaceChild("J_BackSocket_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0001F, -16.0F, 4.32F, 0.0F, 0.0F, -1.5708F));

		PartDefinition J_BackSocket_2 = back.addOrReplaceChild("J_BackSocket_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-7.9999F, -16.0001F, 4.32F, 0.0F, 0.0F, -1.5708F));

		PartDefinition right_leg = hips.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(68, 119).addBox(-8.52F, 0.0F, -8.0F, 17.0F, 21.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.48F, -4.0F, 0.0F, -0.0002F, 0.0F, 0.0F));

		PartDefinition left_leg = hips.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 119).addBox(-8.48F, 0.0F, -8.0F, 17.0F, 21.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.48F, -4.0F, 0.0F, -0.0004F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 256);
	}

	@Override
	public void setupAnim(GraveMonstrosity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		AnimationState animationState = new AnimationState();
		animationState.start(entity.tickCount);
		this.animate(animationState, SCALE, ageInTicks);
		if (entity.isCurrentAnimation(GraveMonstrosity.IDLE) && !entity.isDeadOrDying()) {
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (entity.canAnimateMove()) {
			this.animateWalk(NeoMonstrosityAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.activateAnimationState, NeoMonstrosityAnimations.ACTIVATE, ageInTicks);
		this.animate(entity.idleAnimationState, NeoMonstrosityAnimations.IDLE, ageInTicks);
		this.animate(entity.summonAnimationState, NeoMonstrosityAnimations.SUMMON, ageInTicks);
		this.animate(entity.attackComboAnimationState, GraveMonstrosityAnimations1.ATTACK_COMBO, ageInTicks);
		this.animate(entity.attackComboEndAnimationState, GraveMonstrosityAnimations1.ATTACK_COMBO_END, ageInTicks);
		this.animate(entity.attackSwipeAnimationState, GraveMonstrosityAnimations2.ATTACK_SWIPE, ageInTicks);
		this.animate(entity.chargeAnimationState, GraveMonstrosityAnimations3.CHARGE, ageInTicks);
		this.animate(entity.chargeLoopAnimationState, GraveMonstrosityAnimations3.CHARGE_LOOP, ageInTicks);
		this.animate(entity.chargePunchAnimationState, GraveMonstrosityAnimations3.CHARGE_PUNCH, ageInTicks);
		this.animate(entity.chargeEndAnimationState, GraveMonstrosityAnimations3.CHARGE_END, ageInTicks);
		this.animate(entity.projectileAnimationState, GraveMonstrosityAnimations2.BELCH, ageInTicks);
		this.animate(entity.callAnimationState, GraveMonstrosityAnimations2.CALL, ageInTicks);
		this.animate(entity.staggeredAnimationState, GraveMonstrosityAnimations2.STAGGERED, ageInTicks);
		this.animate(entity.deathAnimationState, NeoMonstrosityDeathAnimation.DEATH, ageInTicks);
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.neck.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.neck.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition SCALE = AnimationDefinition.Builder.withLength(0.0F).looping()
			.addAnimation("monstrosity", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.2F, 1.2F, 1.2F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}