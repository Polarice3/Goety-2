package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.BroodMotherAnimations;
import com.Polarice3.Goety.common.entities.neutral.AbstractBroodMother;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BroodMotherModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart brood_mother;
	private final ModelPart body;
	private final ModelPart mid;
	private final ModelPart abdomen;
	private final ModelPart a_hair;
	private final ModelPart head;
	private final ModelPart hair;
	private final ModelPart mouth_thingies;
	private final ModelPart leftMouth;
	private final ModelPart rightMouth;
	private final ModelPart eyes;
	private final ModelPart legs;
	private final ModelPart rightHindLeg;
	private final ModelPart right_hind_thigh;
	private final ModelPart right_hind_hair;
	private final ModelPart bone8;
	private final ModelPart rhh_hair;
	private final ModelPart rhh_bottom;
	private final ModelPart rhv_hair;
	private final ModelPart rhv_bottom;
	private final ModelPart knee8;
	private final ModelPart knee8_hair;
	private final ModelPart k8_hair_bottom;
	private final ModelPart leftHindLeg;
	private final ModelPart left_hind_thigh;
	private final ModelPart left_hind_hair;
	private final ModelPart bone7;
	private final ModelPart lhh_hair;
	private final ModelPart lhh_bottom;
	private final ModelPart lhv_hair;
	private final ModelPart lhv_bottom;
	private final ModelPart knee4;
	private final ModelPart knee4_hair;
	private final ModelPart k4_hair_bottom;
	private final ModelPart rightMiddleHindLeg;
	private final ModelPart right_middle_hind_thigh;
	private final ModelPart right_middle_hind_hair;
	private final ModelPart bone6;
	private final ModelPart rmhh_hair;
	private final ModelPart rmhh_bottom;
	private final ModelPart rmhv_hair;
	private final ModelPart rmhv_bottom;
	private final ModelPart knee7;
	private final ModelPart knee7_hair;
	private final ModelPart k7_hair_bottom;
	private final ModelPart leftMiddleHindLeg;
	private final ModelPart left_middle_hind_thigh;
	private final ModelPart left_middle_hind_hair;
	private final ModelPart bone5;
	private final ModelPart lmhh_hair;
	private final ModelPart lmhh_bottom;
	private final ModelPart lmhv_hair;
	private final ModelPart lmhv_bottom;
	private final ModelPart knee3;
	private final ModelPart knee3_hair;
	private final ModelPart k3_hair_bottom;
	private final ModelPart rightMiddleFrontLeg;
	private final ModelPart right_middle_front_thigh;
	private final ModelPart right_middle_front_hair;
	private final ModelPart bone4;
	private final ModelPart rmfh_hair;
	private final ModelPart rmfh_bottom;
	private final ModelPart rmfv_hair;
	private final ModelPart rmfv_bottom;
	private final ModelPart knee6;
	private final ModelPart knee6_hair;
	private final ModelPart k6_hair_bottom;
	private final ModelPart leftMiddleFrontLeg;
	private final ModelPart left_middle_front_thigh;
	private final ModelPart left_middle_front_hair;
	private final ModelPart bone3;
	private final ModelPart lmfh_hair;
	private final ModelPart lmfh_bottom;
	private final ModelPart lmfv_hair;
	private final ModelPart lmfv_bottom;
	private final ModelPart knee2;
	private final ModelPart knee2_hair;
	private final ModelPart k2_hair_bottom;
	private final ModelPart rightFrontLeg;
	private final ModelPart right_front_thigh;
	private final ModelPart right_front_hair;
	private final ModelPart bone2;
	private final ModelPart rfh_hair;
	private final ModelPart rfh_bottom;
	private final ModelPart rfv_hair;
	private final ModelPart rfv_bottom;
	private final ModelPart knee5;
	private final ModelPart knee5_hair;
	private final ModelPart k5_hair_bottom;
	private final ModelPart leftFrontLeg;
	private final ModelPart left_front_thigh;
	private final ModelPart left_front_hair;
	private final ModelPart bone;
	private final ModelPart lfh_hair;
	private final ModelPart lfh_bottom;
	private final ModelPart lfv_hair;
	private final ModelPart lfv_bottom;
	private final ModelPart knee9;
	private final ModelPart knee9_hair;
	private final ModelPart k9_hair_bottom;

	public BroodMotherModel(ModelPart root) {
		this.root = root;
		this.brood_mother = root.getChild("brood_mother");
		this.body = this.brood_mother.getChild("body");
		this.mid = this.body.getChild("mid");
		this.abdomen = this.mid.getChild("abdomen");
		this.a_hair = this.abdomen.getChild("a_hair");
		this.head = this.mid.getChild("head");
		this.hair = this.head.getChild("hair");
		this.mouth_thingies = this.head.getChild("mouth_thingies");
		this.leftMouth = this.mouth_thingies.getChild("left_mouth");
		this.rightMouth = this.mouth_thingies.getChild("right_mouth");
		this.eyes = this.head.getChild("eyes");
		this.legs = this.brood_mother.getChild("legs");
		this.rightHindLeg = this.legs.getChild("right_hind_leg");
		this.right_hind_thigh = this.rightHindLeg.getChild("right_hind_thigh");
		this.right_hind_hair = this.right_hind_thigh.getChild("right_hind_hair");
		this.bone8 = this.right_hind_hair.getChild("bone8");
		this.rhh_hair = this.right_hind_hair.getChild("rhh_hair");
		this.rhh_bottom = this.rhh_hair.getChild("rhh_bottom");
		this.rhv_hair = this.right_hind_hair.getChild("rhv_hair");
		this.rhv_bottom = this.rhv_hair.getChild("rhv_bottom");
		this.knee8 = this.right_hind_thigh.getChild("knee8");
		this.knee8_hair = this.knee8.getChild("knee8_hair");
		this.k8_hair_bottom = this.knee8_hair.getChild("k8_hair_bottom");
		this.leftHindLeg = this.legs.getChild("left_hind_leg");
		this.left_hind_thigh = this.leftHindLeg.getChild("left_hind_thigh");
		this.left_hind_hair = this.left_hind_thigh.getChild("left_hind_hair");
		this.bone7 = this.left_hind_hair.getChild("bone7");
		this.lhh_hair = this.left_hind_hair.getChild("lhh_hair");
		this.lhh_bottom = this.lhh_hair.getChild("lhh_bottom");
		this.lhv_hair = this.left_hind_hair.getChild("lhv_hair");
		this.lhv_bottom = this.lhv_hair.getChild("lhv_bottom");
		this.knee4 = this.left_hind_thigh.getChild("knee4");
		this.knee4_hair = this.knee4.getChild("knee4_hair");
		this.k4_hair_bottom = this.knee4_hair.getChild("k4_hair_bottom");
		this.rightMiddleHindLeg = this.legs.getChild("right_middle_hind_leg");
		this.right_middle_hind_thigh = this.rightMiddleHindLeg.getChild("right_middle_hind_thigh");
		this.right_middle_hind_hair = this.right_middle_hind_thigh.getChild("right_middle_hind_hair");
		this.bone6 = this.right_middle_hind_hair.getChild("bone6");
		this.rmhh_hair = this.right_middle_hind_hair.getChild("rmhh_hair");
		this.rmhh_bottom = this.rmhh_hair.getChild("rmhh_bottom");
		this.rmhv_hair = this.right_middle_hind_hair.getChild("rmhv_hair");
		this.rmhv_bottom = this.rmhv_hair.getChild("rmhv_bottom");
		this.knee7 = this.right_middle_hind_thigh.getChild("knee7");
		this.knee7_hair = this.knee7.getChild("knee7_hair");
		this.k7_hair_bottom = this.knee7_hair.getChild("k7_hair_bottom");
		this.leftMiddleHindLeg = this.legs.getChild("left_middle_hind_leg");
		this.left_middle_hind_thigh = this.leftMiddleHindLeg.getChild("left_middle_hind_thigh");
		this.left_middle_hind_hair = this.left_middle_hind_thigh.getChild("left_middle_hind_hair");
		this.bone5 = this.left_middle_hind_hair.getChild("bone5");
		this.lmhh_hair = this.left_middle_hind_hair.getChild("lmhh_hair");
		this.lmhh_bottom = this.lmhh_hair.getChild("lmhh_bottom");
		this.lmhv_hair = this.left_middle_hind_hair.getChild("lmhv_hair");
		this.lmhv_bottom = this.lmhv_hair.getChild("lmhv_bottom");
		this.knee3 = this.left_middle_hind_thigh.getChild("knee3");
		this.knee3_hair = this.knee3.getChild("knee3_hair");
		this.k3_hair_bottom = this.knee3_hair.getChild("k3_hair_bottom");
		this.rightMiddleFrontLeg = this.legs.getChild("right_middle_front_leg");
		this.right_middle_front_thigh = this.rightMiddleFrontLeg.getChild("right_middle_front_thigh");
		this.right_middle_front_hair = this.right_middle_front_thigh.getChild("right_middle_front_hair");
		this.bone4 = this.right_middle_front_hair.getChild("bone4");
		this.rmfh_hair = this.right_middle_front_hair.getChild("rmfh_hair");
		this.rmfh_bottom = this.rmfh_hair.getChild("rmfh_bottom");
		this.rmfv_hair = this.right_middle_front_hair.getChild("rmfv_hair");
		this.rmfv_bottom = this.rmfv_hair.getChild("rmfv_bottom");
		this.knee6 = this.right_middle_front_thigh.getChild("knee6");
		this.knee6_hair = this.knee6.getChild("knee6_hair");
		this.k6_hair_bottom = this.knee6_hair.getChild("k6_hair_bottom");
		this.leftMiddleFrontLeg = this.legs.getChild("left_middle_front_leg");
		this.left_middle_front_thigh = this.leftMiddleFrontLeg.getChild("left_middle_front_thigh");
		this.left_middle_front_hair = this.left_middle_front_thigh.getChild("left_middle_front_hair");
		this.bone3 = this.left_middle_front_hair.getChild("bone3");
		this.lmfh_hair = this.left_middle_front_hair.getChild("lmfh_hair");
		this.lmfh_bottom = this.lmfh_hair.getChild("lmfh_bottom");
		this.lmfv_hair = this.left_middle_front_hair.getChild("lmfv_hair");
		this.lmfv_bottom = this.lmfv_hair.getChild("lmfv_bottom");
		this.knee2 = this.left_middle_front_thigh.getChild("knee2");
		this.knee2_hair = this.knee2.getChild("knee2_hair");
		this.k2_hair_bottom = this.knee2_hair.getChild("k2_hair_bottom");
		this.rightFrontLeg = this.legs.getChild("right_front_leg");
		this.right_front_thigh = this.rightFrontLeg.getChild("right_front_thigh");
		this.right_front_hair = this.right_front_thigh.getChild("right_front_hair");
		this.bone2 = this.right_front_hair.getChild("bone2");
		this.rfh_hair = this.right_front_hair.getChild("rfh_hair");
		this.rfh_bottom = this.rfh_hair.getChild("rfh_bottom");
		this.rfv_hair = this.right_front_hair.getChild("rfv_hair");
		this.rfv_bottom = this.rfv_hair.getChild("rfv_bottom");
		this.knee5 = this.right_front_thigh.getChild("knee5");
		this.knee5_hair = this.knee5.getChild("knee5_hair");
		this.k5_hair_bottom = this.knee5_hair.getChild("k5_hair_bottom");
		this.leftFrontLeg = this.legs.getChild("left_front_leg");
		this.left_front_thigh = this.leftFrontLeg.getChild("left_front_thigh");
		this.left_front_hair = this.left_front_thigh.getChild("left_front_hair");
		this.bone = this.left_front_hair.getChild("bone");
		this.lfh_hair = this.left_front_hair.getChild("lfh_hair");
		this.lfh_bottom = this.lfh_hair.getChild("lfh_bottom");
		this.lfv_hair = this.left_front_hair.getChild("lfv_hair");
		this.lfv_bottom = this.lfv_hair.getChild("lfv_bottom");
		this.knee9 = this.left_front_thigh.getChild("knee9");
		this.knee9_hair = this.knee9.getChild("knee9_hair");
		this.k9_hair_bottom = this.knee9_hair.getChild("k9_hair_bottom");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition brood_mother = partdefinition.addOrReplaceChild("brood_mother", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition body = brood_mother.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 26.0F, 0.0F));

		PartDefinition mid = body.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(78, 0).addBox(-8.0F, -5.0899F, -1.6671F, 16.0F, 14.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -26.0F, -11.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition abdomen = mid.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 34).addBox(-11.0F, -11.0F, 0.0F, 24.0F, 24.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.9101F, 19.3329F, -0.0873F, 0.0F, 0.0F));

		PartDefinition a_hair = abdomen.addOrReplaceChild("a_hair", CubeListBuilder.create().texOffs(122, 28).addBox(8.0F, -2.0F, -15.0F, 0.0F, 4.0F, 30.0F, new CubeDeformation(0.0F))
				.texOffs(122, 28).addBox(-8.0F, -2.0F, -15.0F, 0.0F, 4.0F, 30.0F, new CubeDeformation(0.0F))
				.texOffs(122, 32).addBox(-4.0F, -2.0F, -15.0F, 0.0F, 4.0F, 30.0F, new CubeDeformation(0.0F))
				.texOffs(122, 32).addBox(4.0F, -2.0F, -15.0F, 0.0F, 4.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -13.0F, 15.0F));

		PartDefinition head = mid.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -16.0F, -13.0F, 12.0F, 12.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -2.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(123, 52).addBox(5.0F, -2.0F, -8.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(123, 52).addBox(-5.0F, -2.0F, -8.0F, 0.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(123, 58).addBox(-2.0F, -2.0F, -7.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(123, 58).addBox(2.0F, -2.0F, -7.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -5.0F));

		PartDefinition mouth_thingies = head.addOrReplaceChild("mouth_thingies", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, -14.0F));

		PartDefinition left_mouth = mouth_thingies.addOrReplaceChild("left_mouth", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-2.0F, -1.5F, -3.5F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.0F, 0.5F, 0.5F));

		PartDefinition right_mouth = mouth_thingies.addOrReplaceChild("right_mouth", CubeListBuilder.create().texOffs(38, 0).addBox(-3.0F, -1.5F, -3.5F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.5F, 0.5F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(5.0F, 0.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-7.0F, 0.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-4.0F, 1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(2.0F, 1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, -13.0F));

		PartDefinition legs = brood_mother.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(8.0F, 2.0F, -9.5F));

		PartDefinition right_hind_leg = legs.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(-13.0F, 0.0F, 14.0F));

		PartDefinition right_hind_thigh = right_hind_leg.addOrReplaceChild("right_hind_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7053F, -0.2618F, -2.3562F));

		PartDefinition right_hind_hair = right_hind_thigh.addOrReplaceChild("right_hind_hair", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 1.0F));

		PartDefinition bone8 = right_hind_hair.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(118, 58).addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(118, 58).addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, 0.0F, 0.0F));

		PartDefinition rhh_hair = right_hind_hair.addOrReplaceChild("rhh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition rhh_bottom = rhh_hair.addOrReplaceChild("rhh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition rhv_hair = right_hind_hair.addOrReplaceChild("rhv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rhv_bottom = rhv_hair.addOrReplaceChild("rhv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee8 = right_hind_thigh.addOrReplaceChild("knee8", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition knee8_hair = knee8.addOrReplaceChild("knee8_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(25.0F, -1.0F, 1.0F));

		PartDefinition k8_hair_bottom = knee8_hair.addOrReplaceChild("k8_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition left_hind_leg = legs.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, 0.0F, 14.0F));

		PartDefinition left_hind_thigh = left_hind_leg.addOrReplaceChild("left_hind_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.7053F, 0.2618F, 2.3562F));

		PartDefinition left_hind_hair = left_hind_thigh.addOrReplaceChild("left_hind_hair", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 1.0F));

		PartDefinition bone7 = left_hind_hair.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-11.5F, 0.0F, 0.0F));

		PartDefinition lhh_hair = left_hind_hair.addOrReplaceChild("lhh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition lhh_bottom = lhh_hair.addOrReplaceChild("lhh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition lhv_hair = left_hind_hair.addOrReplaceChild("lhv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lhv_bottom = lhv_hair.addOrReplaceChild("lhv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee4 = left_hind_thigh.addOrReplaceChild("knee4", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition knee4_hair = knee4.addOrReplaceChild("knee4_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-25.0F, -1.0F, 1.0F));

		PartDefinition k4_hair_bottom = knee4_hair.addOrReplaceChild("k4_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition right_middle_hind_leg = legs.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 9.0F));

		PartDefinition right_middle_hind_thigh = right_middle_hind_leg.addOrReplaceChild("right_middle_hind_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 2.8798F, -0.1745F, -2.8798F));

		PartDefinition right_middle_hind_hair = right_middle_hind_thigh.addOrReplaceChild("right_middle_hind_hair", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 1.0F));

		PartDefinition bone6 = right_middle_hind_hair.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(118, 58).addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(118, 58).addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, 0.0F, 0.0F));

		PartDefinition rmhh_hair = right_middle_hind_hair.addOrReplaceChild("rmhh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition rmhh_bottom = rmhh_hair.addOrReplaceChild("rmhh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition rmhv_hair = right_middle_hind_hair.addOrReplaceChild("rmhv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rmhv_bottom = rmhv_hair.addOrReplaceChild("rmhv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee7 = right_middle_hind_thigh.addOrReplaceChild("knee7", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

		PartDefinition knee7_hair = knee7.addOrReplaceChild("knee7_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(25.0F, -1.0F, 1.0F));

		PartDefinition k7_hair_bottom = knee7_hair.addOrReplaceChild("k7_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition left_middle_hind_leg = legs.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 9.0F));

		PartDefinition left_middle_hind_thigh = left_middle_hind_leg.addOrReplaceChild("left_middle_hind_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 2.8798F, 0.1745F, 2.8798F));

		PartDefinition left_middle_hind_hair = left_middle_hind_thigh.addOrReplaceChild("left_middle_hind_hair", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 1.0F));

		PartDefinition bone5 = left_middle_hind_hair.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-11.5F, 0.0F, 0.0F));

		PartDefinition lmhh_hair = left_middle_hind_hair.addOrReplaceChild("lmhh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition lmhh_bottom = lmhh_hair.addOrReplaceChild("lmhh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition lmhv_hair = left_middle_hind_hair.addOrReplaceChild("lmhv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lmhv_bottom = lmhv_hair.addOrReplaceChild("lmhv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee3 = left_middle_hind_thigh.addOrReplaceChild("knee3", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.8727F));

		PartDefinition knee3_hair = knee3.addOrReplaceChild("knee3_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-25.0F, -1.0F, 1.0F));

		PartDefinition k3_hair_bottom = knee3_hair.addOrReplaceChild("k3_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition right_middle_front_leg = legs.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 6.0F));

		PartDefinition right_middle_front_thigh = right_middle_front_leg.addOrReplaceChild("right_middle_front_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, 0.0873F, -2.7925F));

		PartDefinition right_middle_front_hair = right_middle_front_thigh.addOrReplaceChild("right_middle_front_hair", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 1.0F));

		PartDefinition bone4 = right_middle_front_hair.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(118, 58).addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(118, 58).addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, 0.0F, 0.0F));

		PartDefinition rmfh_hair = right_middle_front_hair.addOrReplaceChild("rmfh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition rmfh_bottom = rmfh_hair.addOrReplaceChild("rmfh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition rmfv_hair = right_middle_front_hair.addOrReplaceChild("rmfv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rmfv_bottom = rmfv_hair.addOrReplaceChild("rmfv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, 0.0F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, -3.0F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 1.5F));

		PartDefinition knee6 = right_middle_front_thigh.addOrReplaceChild("knee6", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition knee6_hair = knee6.addOrReplaceChild("knee6_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(25.0F, -1.0F, 1.0F));

		PartDefinition k6_hair_bottom = knee6_hair.addOrReplaceChild("k6_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition left_middle_front_leg = legs.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 6.0F));

		PartDefinition left_middle_front_thigh = left_middle_front_leg.addOrReplaceChild("left_middle_front_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.8798F, -0.0873F, 2.7925F));

		PartDefinition left_middle_front_hair = left_middle_front_thigh.addOrReplaceChild("left_middle_front_hair", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 1.0F));

		PartDefinition bone3 = left_middle_front_hair.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, -1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, 1.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-11.5F, 0.0F, 0.0F));

		PartDefinition lmfh_hair = left_middle_front_hair.addOrReplaceChild("lmfh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition lmfh_bottom = lmfh_hair.addOrReplaceChild("lmfh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition lmfv_hair = left_middle_front_hair.addOrReplaceChild("lmfv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lmfv_bottom = lmfv_hair.addOrReplaceChild("lmfv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee2 = left_middle_front_thigh.addOrReplaceChild("knee2", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0472F));

		PartDefinition knee2_hair = knee2.addOrReplaceChild("knee2_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-25.0F, -1.0F, 1.0F));

		PartDefinition k2_hair_bottom = knee2_hair.addOrReplaceChild("k2_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition right_front_leg = legs.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(-16.0F, 0.0F, 0.0F));

		PartDefinition right_front_thigh = right_front_leg.addOrReplaceChild("right_front_thigh", CubeListBuilder.create().texOffs(78, 38).addBox(-2.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7053F, 0.6109F, -2.3562F));

		PartDefinition right_front_hair = right_front_thigh.addOrReplaceChild("right_front_hair", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 1.0F));

		PartDefinition bone2 = right_front_hair.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(118, 58).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(118, 58).addBox(-1.0F, -3.0F, 3.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, 0.0F, -1.5F));

		PartDefinition rfh_hair = right_front_hair.addOrReplaceChild("rfh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition rfh_bottom = rfh_hair.addOrReplaceChild("rfh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition rfv_hair = right_front_hair.addOrReplaceChild("rfv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rfv_bottom = rfv_hair.addOrReplaceChild("rfv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee5 = right_front_thigh.addOrReplaceChild("knee5", CubeListBuilder.create().texOffs(78, 48).addBox(-1.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

		PartDefinition knee5_hair = knee5.addOrReplaceChild("knee5_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(25.0F, -1.0F, 1.0F));

		PartDefinition k5_hair_bottom = knee5_hair.addOrReplaceChild("k5_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition left_front_leg = legs.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_front_thigh = left_front_leg.addOrReplaceChild("left_front_thigh", CubeListBuilder.create().texOffs(78, 38).mirror().addBox(-18.0F, -2.5F, -1.5F, 20.0F, 5.0F, 5.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7053F, -0.6109F, 2.3562F));

		PartDefinition left_front_hair = left_front_thigh.addOrReplaceChild("left_front_hair", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 1.0F));

		PartDefinition bone = left_front_hair.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, 0.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(118, 58).mirror().addBox(-1.0F, -3.0F, 3.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-11.5F, 0.0F, -1.5F));

		PartDefinition lfh_hair = left_front_hair.addOrReplaceChild("lfh_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition lfh_bottom = lfh_hair.addOrReplaceChild("lfh_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition lfv_hair = left_front_hair.addOrReplaceChild("lfv_hair", CubeListBuilder.create().texOffs(78, 58).addBox(-10.0F, -5.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 58).addBox(-10.0F, -5.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lfv_bottom = lfv_hair.addOrReplaceChild("lfv_bottom", CubeListBuilder.create().texOffs(78, 60).addBox(-10.0F, -1.0F, 1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 60).addBox(-10.0F, -1.0F, -1.5F, 20.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition knee9 = left_front_thigh.addOrReplaceChild("knee9", CubeListBuilder.create().texOffs(78, 48).mirror().addBox(-47.0F, 0.0F, -1.0F, 48.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-17.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.4835F));

		PartDefinition knee9_hair = knee9.addOrReplaceChild("knee9_hair", CubeListBuilder.create().texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 54).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-25.0F, -1.0F, 1.0F));

		PartDefinition k9_hair_bottom = knee9_hair.addOrReplaceChild("k9_hair_bottom", CubeListBuilder.create().texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, 1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(78, 56).mirror().addBox(-22.0F, -1.0F, -1.0F, 44.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof AbstractBroodMother abstractBroodMother) {
			this.animate(abstractBroodMother.attackAnimationState, BroodMotherAnimations.ATTACK, ageInTicks);
			this.animate(abstractBroodMother.shootAnimationState, BroodMotherAnimations.SHOOT, ageInTicks);
			this.animate(abstractBroodMother.layEggsAnimationState, BroodMotherAnimations.LAY_EGGS, ageInTicks);
			this.animate(abstractBroodMother.chargeAnimationState, BroodMotherAnimations.CHARGE, ageInTicks);
			this.animate(abstractBroodMother.backOffAnimationState, BroodMotherAnimations.BACK_OFF, ageInTicks);
			this.animate(abstractBroodMother.jumpAnimationState, BroodMotherAnimations.JUMP, ageInTicks);
			this.animate(abstractBroodMother.deathAnimationState, BroodMotherAnimations.DEATH, ageInTicks);
		}
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.rightMouth.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.rightMouth.yRot = 0.0F;
		this.rightMouth.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
		this.leftMouth.xRot = -this.rightMouth.xRot;
		this.leftMouth.yRot = -this.rightMouth.yRot;
		this.leftMouth.zRot = -this.rightMouth.zRot;
		float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
		float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f6 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
		float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
		float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f10 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		this.rightHindLeg.yRot += f3;
		this.leftHindLeg.yRot += -f3;
		this.rightMiddleHindLeg.yRot += f4;
		this.leftMiddleHindLeg.yRot += -f4;
		this.rightMiddleFrontLeg.yRot += f5;
		this.leftMiddleFrontLeg.yRot += -f5;
		this.rightFrontLeg.yRot += f6;
		this.leftFrontLeg.yRot += -f6;
		this.rightHindLeg.zRot += f7;
		this.leftHindLeg.zRot += -f7;
		this.rightMiddleHindLeg.zRot += f8;
		this.leftMiddleHindLeg.zRot += -f8;
		this.rightMiddleFrontLeg.zRot += f9;
		this.leftMiddleFrontLeg.zRot += -f9;
		this.rightFrontLeg.zRot += f10;
		this.leftFrontLeg.zRot += -f10;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}