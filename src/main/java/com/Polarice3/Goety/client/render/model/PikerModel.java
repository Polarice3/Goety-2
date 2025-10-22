package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.PikerAnimations;
import com.Polarice3.Goety.common.entities.ally.illager.PikerServant;
import com.Polarice3.Goety.common.entities.hostile.illagers.Piker;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class PikerModel<T extends Mob> extends HierarchicalModel<T> implements HeadedModel {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public PikerModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.head = this.illager.getChild("head");
		this.leftArm = this.illager.getChild("left_arm");
		this.rightLeg = this.illager.getChild("right_leg");
		this.leftLeg = this.illager.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = illager.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(60, 19).addBox(-4.5F, -5.75F, -4.55F, 4.0F, 7.0F, 8.0F, new CubeDeformation(-0.2F))
				.texOffs(60, 19).mirror().addBox(0.5F, -5.75F, -4.55F, 4.0F, 7.0F, 8.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition rightbeard = head.addOrReplaceChild("rightbeard", CubeListBuilder.create().texOffs(34, 2).addBox(-2.75F, -0.85F, -1.25F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -3.0F, -3.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition leftbeard = head.addOrReplaceChild("leftbeard", CubeListBuilder.create().texOffs(34, 2).mirror().addBox(-0.25F, -0.85F, -1.25F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -3.0F, -3.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(60, 50).addBox(-4.5F, -11.0F, -4.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(60, 34).addBox(-1.0F, -12.0F, -5.0F, 2.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet_r1 = helmet.addOrReplaceChild("helmet_r1", CubeListBuilder.create().texOffs(33, 1).mirror().addBox(0.0F, 0.0F, -1.5F, 7.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition helmet_r2 = helmet.addOrReplaceChild("helmet_r2", CubeListBuilder.create().texOffs(33, 1).addBox(-7.0F, 0.0F, -1.5F, 7.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition body = illager.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -7.5F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 47).mirror().addBox(-4.0F, 1.75F, -3.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.5F)).mirror(false)
				.texOffs(0, 64).addBox(-4.5F, -7.75F, -3.5F, 9.0F, 7.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 7.5F, 0.0F));

		PartDefinition upperBody_r1 = body.addOrReplaceChild("upperBody_r1", CubeListBuilder.create().texOffs(30, 46).addBox(-6.5F, -16.0F, 1.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 18.5F, -7.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition upperBody_r2 = body.addOrReplaceChild("upperBody_r2", CubeListBuilder.create().texOffs(30, 46).addBox(-6.5F, -16.0F, 1.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 18.5F, -3.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition bedroll = body.addOrReplaceChild("bedroll", CubeListBuilder.create().texOffs(60, 6).addBox(-7.0F, -1.5F, 0.0F, 14.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 3.0F));

		PartDefinition bedroll1 = bedroll.addOrReplaceChild("bedroll1", CubeListBuilder.create().texOffs(76, 14).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 4.0F));

		PartDefinition Scabbard = body.addOrReplaceChild("Scabbard", CubeListBuilder.create().texOffs(0, 105).addBox(1.5F, -12.5F, 0.0F, 11.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(3.5F, -2.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(2.5F, -3.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(1.5F, -4.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(3.5F, -6.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(4.5F, -7.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(6.5F, -9.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(7.5F, -10.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(9.5F, -12.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(10.5F, -12.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(11.5F, -12.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(11.5F, -11.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(11.5F, -10.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(9.5F, -8.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(8.5F, -7.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(6.5F, -5.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 75).addBox(5.5F, -4.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(4.5F, -3.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(7.5F, -6.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(10.5F, -9.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(8.5F, -11.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(5.5F, -8.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(2.5F, -5.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 2.0F, -8.0F, -1.5708F, -0.5236F, 1.5708F));

		PartDefinition Scabbard1 = Scabbard.addOrReplaceChild("Scabbard1", CubeListBuilder.create().texOffs(0, 117).addBox(-1.0F, 2.0F, -1.0F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(-1.0F, 10.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(3.0F, 4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(2.0F, 2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(3.0F, 3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(7.0F, 7.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(6.0F, 6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(-1.0F, 9.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(-1.0F, 8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 75).addBox(0.0F, 8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(1.0F, 7.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(2.0F, 6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(4.0F, 6.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(4.0F, 4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(5.0F, 5.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(3.0F, 5.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(2.0F, 3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(6.0F, 7.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(0.0F, 10.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 75).addBox(1.0F, 10.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 75).addBox(1.0F, 9.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(2.0F, 8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(3.0F, 7.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -7.5F, 1.0F));

		PartDefinition right_arm = illager.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(28, 54).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition rightPauldron = right_arm.addOrReplaceChild("rightPauldron", CubeListBuilder.create().texOffs(76, 64).addBox(-2.5F, -3.5F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(99, 66).addBox(1.5F, -4.5F, -3.5F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(99, 64).addBox(-3.5F, -1.5F, -3.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 64).addBox(-3.5F, -1.5F, 2.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 64).addBox(-3.5F, 1.5F, 2.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 67).addBox(1.5F, -0.5F, -3.5F, 1.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(99, 58).addBox(-3.5F, 1.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(99, 58).addBox(-3.5F, -1.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(99, 64).addBox(-3.5F, 1.5F, -3.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, -0.25F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition spear = right_arm.addOrReplaceChild("spear", CubeListBuilder.create(), PartPose.offset(-1.5F, 9.6884F, -2.3116F));

		PartDefinition Rotate = spear.addOrReplaceChild("Rotate", CubeListBuilder.create().texOffs(0, 77).addBox(-14.3116F, -12.6884F, -0.5F, 27.0F, 27.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(53, 67).addBox(-14.3116F, 13.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(53, 67).addBox(-7.3116F, 7.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(-8.3116F, 6.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(53, 67).addBox(-13.3116F, 13.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(53, 67).addBox(-14.3116F, 12.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(-14.3116F, 11.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(-13.3116F, 11.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(-12.3116F, 12.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(-12.3116F, 13.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-11.3116F, 11.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-10.3116F, 10.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-9.3116F, 9.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-8.3116F, 8.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-12.3116F, 10.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-11.3116F, 9.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 71).addBox(-10.3116F, 8.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 71).addBox(-9.3116F, 7.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 71).addBox(1.6884F, -3.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 71).addBox(0.6884F, -2.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(-0.3116F, -1.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(1.6884F, -1.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(2.6884F, -2.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 71).addBox(0.6884F, -0.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-0.3116F, 0.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-1.3116F, 1.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-2.3116F, 2.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-3.3116F, 3.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-4.3116F, 4.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-5.3116F, 5.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-6.3116F, 6.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-6.3116F, 4.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-7.3116F, 5.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(-4.3116F, 2.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(5.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 71).addBox(4.6884F, -4.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(4.6884F, -6.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(3.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(-1.3116F, -0.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(-2.3116F, 0.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(-3.3116F, 1.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 71).addBox(-5.3116F, 3.3116F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(1.6884F, -4.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 67).addBox(2.6884F, -4.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(3.6884F, -2.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(49, 67).addBox(4.6884F, -2.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(1.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(3.6884F, -3.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(3.6884F, -7.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(45, 67).addBox(4.6884F, -7.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(4.6884F, -8.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(4.6884F, -9.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(5.6884F, -9.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(6.6884F, -9.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(7.6884F, -10.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(8.6884F, -11.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(9.6884F, -11.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(10.6884F, -12.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(33, 67).addBox(11.6884F, -12.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(11.6884F, -11.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(10.6884F, -10.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(10.6884F, -9.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(9.6884F, -8.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(8.6884F, -7.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(37, 67).addBox(8.6884F, -6.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 67).addBox(8.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 67).addBox(7.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 67).addBox(6.6884F, -5.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(41, 67).addBox(6.6884F, -4.6884F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F, 1.5708F));

		PartDefinition left_arm = illager.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 38).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(44, 54).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-2.1F, 6.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.1F, 12.0F, 0.0F));

		PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(0, 38).mirror().addBox(-1.9F, 6.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(1.95F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof Piker piker) {
			if (!piker.isDeadOrDying()){
				this.animateHeadLookTarget(netHeadYaw, headPitch);
				if (piker.isMeleeAttacking()) {
					this.animateWalk(limbSwing, limbSwingAmount);
				}
			}
			Vec3 velocity = piker.getDeltaMovement();
			float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
			if (piker.aggressiveMode){
				this.animate(piker.idleAnimationState, PikerAnimations.IDLE, ageInTicks);
			} else {
				this.animate(piker.idleAnimationState, PikerAnimations.IDLE_PASSIVE, ageInTicks);
			}
			if (this.riding){
				this.rightLeg.xRot = -1.4137167F;
				this.rightLeg.yRot = ((float)Math.PI / 10F);
				this.rightLeg.zRot = 0.07853982F;
				this.leftLeg.xRot = -1.4137167F;
				this.leftLeg.yRot = (-(float)Math.PI / 10F);
				this.leftLeg.zRot = -0.07853982F;
			} else {
				if (piker.aggressiveMode){
					this.animate(piker.walkAnimationState, PikerAnimations.WALK, ageInTicks, groundSpeed * 10);
				} else {
					this.animate(piker.walkAnimationState, PikerAnimations.WALK_PASSIVE, ageInTicks);
					this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
					this.leftArm.yRot = 0.0F;
					this.leftArm.zRot = 0.0F;
					this.animateWalk(limbSwing, limbSwingAmount);
				}
			}
			this.animate(piker.attackAnimationState, PikerAnimations.ATTACK, ageInTicks);
		} else if (entity instanceof PikerServant piker) {
			if (!piker.isDeadOrDying()){
				this.animateHeadLookTarget(netHeadYaw, headPitch);
				if (piker.isMeleeAttacking()) {
					this.animateWalk(limbSwing, limbSwingAmount);
				}
			}
			Vec3 velocity = piker.getDeltaMovement();
			float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
			if (piker.aggressiveMode){
				this.animate(piker.idleAnimationState, PikerAnimations.IDLE, ageInTicks);
			} else {
				this.animate(piker.idleAnimationState, PikerAnimations.IDLE_PASSIVE, ageInTicks);
			}
			if (this.riding){
				this.rightLeg.xRot = -1.4137167F;
				this.rightLeg.yRot = ((float)Math.PI / 10F);
				this.rightLeg.zRot = 0.07853982F;
				this.leftLeg.xRot = -1.4137167F;
				this.leftLeg.yRot = (-(float)Math.PI / 10F);
				this.leftLeg.zRot = -0.07853982F;
			} else {
				if (piker.aggressiveMode){
					this.animate(piker.walkAnimationState, PikerAnimations.WALK, ageInTicks, groundSpeed * 10);
				} else {
					this.animate(piker.walkAnimationState, PikerAnimations.WALK_PASSIVE, ageInTicks);
					this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
					this.leftArm.yRot = 0.0F;
					this.leftArm.zRot = 0.0F;
					this.animateWalk(limbSwing, limbSwingAmount);
				}
			}
			this.animate(piker.attackAnimationState, PikerAnimations.ATTACK, ageInTicks);
		}
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	private void animateWalk(float limbSwing, float limbSwingAmount){
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
	}

	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}