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
	private final ModelPart skull;
	private final ModelPart body;
	private final ModelPart cape;
	private final ModelPart cape1;
	private final ModelPart cape7;
	private final ModelPart cape8;
	private final ModelPart cape3;
	private final ModelPart cape2;
	private final ModelPart cape4;
	private final ModelPart cape5;
	private final ModelPart cape6;
	private final ModelPart right_arm;
	private final ModelPart righth_arm1;
	private final ModelPart doll;
	private final ModelPart doll_head;
	private final ModelPart doll1;
	private final ModelPart doll2;
	private final ModelPart doll3;
	private final ModelPart doll4;
	private final ModelPart right_shouldpad;
	private final ModelPart left_arm;
	private final ModelPart lefth_arm1;
	private final ModelPart knife;
	private final ModelPart left_shouldpad;
	private final ModelPart lower_body;
	public final List<String> allPartNames;

	public HeresiarchModel(ModelPart root) {
		this.root = root;
		this.witch = root.getChild("witch");
		this.upper_body = this.witch.getChild("upper_body");
		this.head = this.upper_body.getChild("head");
		this.nose = this.head.getChild("nose");
		this.skull = this.head.getChild("skull");
		this.body = this.upper_body.getChild("body");
		this.cape = this.body.getChild("cape");
		this.cape1 = this.cape.getChild("cape1");
		this.cape7 = this.cape1.getChild("cape7");
		this.cape8 = this.cape1.getChild("cape8");
		this.cape3 = this.cape1.getChild("cape3");
		this.cape2 = this.cape.getChild("cape2");
		this.cape4 = this.cape2.getChild("cape4");
		this.cape5 = this.cape2.getChild("cape5");
		this.cape6 = this.cape2.getChild("cape6");
		this.right_arm = this.upper_body.getChild("right_arm");
		this.righth_arm1 = this.right_arm.getChild("righth_arm1");
		this.doll = this.righth_arm1.getChild("doll");
		this.doll_head = this.doll.getChild("doll_head");
		this.doll1 = this.doll.getChild("doll1");
		this.doll2 = this.doll.getChild("doll2");
		this.doll3 = this.doll.getChild("doll3");
		this.doll4 = this.doll.getChild("doll4");
		this.right_shouldpad = this.right_arm.getChild("right_shouldpad");
		this.left_arm = this.upper_body.getChild("left_arm");
		this.lefth_arm1 = this.left_arm.getChild("lefth_arm1");
		this.knife = this.lefth_arm1.getChild("knife");
		this.left_shouldpad = this.left_arm.getChild("left_shouldpad");
		this.lower_body = this.witch.getChild("lower_body");
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

		PartDefinition witch = partdefinition.addOrReplaceChild("witch", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition upper_body = witch.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition head = upper_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(deformation))
				.texOffs(1, 103).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(deformation + 0.25F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(deformation))
				.texOffs(0, 0).addBox(0.0F, 1.0F, -2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation - 0.25F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition skull = head.addOrReplaceChild("skull", CubeListBuilder.create().texOffs(60, 31).addBox(-4.5F, -5.0F, -4.5F, 9.0F, 7.0F, 9.0F, new CubeDeformation(deformation + 0.2F))
				.texOffs(80, 47).addBox(-2.5F, -1.5F, -11.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(deformation + 0.2F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition skull_r1 = skull.addOrReplaceChild("skull_r1", CubeListBuilder.create().texOffs(60, 47).mirror().addBox(-0.5F, -12.5F, 0.0F, 10.0F, 13.0F, 0.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(5.0F, -3.0F, 1.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition skull_r2 = skull.addOrReplaceChild("skull_r2", CubeListBuilder.create().texOffs(60, 47).addBox(-9.5F, -12.5F, 0.0F, 10.0F, 13.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-5.0F, -3.0F, 1.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition body = upper_body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 11.0F, 6.0F, new CubeDeformation(deformation))
				.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 11.0F, 6.0F, new CubeDeformation(deformation + 0.25F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.1F));

		PartDefinition cape1 = cape.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(69, 0).addBox(-5.0F, 0.0F, -6.5F, 9.0F, 24.0F, 7.0F, new CubeDeformation(deformation)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition cape7 = cape1.addOrReplaceChild("cape7", CubeListBuilder.create().texOffs(56, 7).addBox(0.0F, -7.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(4.0F, 14.0F, -6.5F, 0.0F, 0.6545F, 0.0F));

		PartDefinition cape8 = cape1.addOrReplaceChild("cape8", CubeListBuilder.create().texOffs(56, 7).addBox(0.0F, -7.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(4.0F, 14.0F, 0.5F, 0.0F, -0.6545F, 0.0F));

		PartDefinition cape3 = cape1.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(56, 7).addBox(0.0F, -5.0F, 0.0F, 2.0F, 9.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-4.0F, 6.0F, 0.5F, 0.0F, -0.8727F, 0.0F));

		PartDefinition cape2 = cape.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(69, 0).mirror().addBox(-4.0F, 0.0F, -6.5F, 9.0F, 24.0F, 7.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition cape4 = cape2.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(56, 7).mirror().addBox(-2.0F, -5.0F, 0.0F, 2.0F, 9.0F, 0.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(4.0F, 6.0F, 0.5F, 0.0F, 0.8727F, 0.0F));

		PartDefinition cape5 = cape2.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(56, 7).mirror().addBox(-2.0F, -7.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(-4.0F, 14.0F, -6.5F, 0.0F, -0.6545F, 0.0F));

		PartDefinition cape6 = cape2.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(56, 7).mirror().addBox(-2.0F, -7.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offsetAndRotation(-4.0F, 14.0F, 0.5F, 0.0F, 0.6545F, 0.0F));

		PartDefinition right_arm = upper_body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition righth_arm1 = right_arm.addOrReplaceChild("righth_arm1", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition doll = righth_arm1.addOrReplaceChild("doll", CubeListBuilder.create().texOffs(61, 69).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(deformation - 0.25F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 1.5708F, 0.0F, -1.5708F));

		PartDefinition doll_head = doll.addOrReplaceChild("doll_head", CubeListBuilder.create().texOffs(60, 60).addBox(-2.0F, -3.0F, -1.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(deformation - 0.5F)), PartPose.offset(0.0F, -3.0F, -1.0F));

		PartDefinition doll1 = doll.addOrReplaceChild("doll1", CubeListBuilder.create().texOffs(80, 62).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-1.5F, -2.0F, 0.0F, 0.0F, 0.0F, -1.2654F));

		PartDefinition doll2 = doll.addOrReplaceChild("doll2", CubeListBuilder.create().texOffs(80, 62).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.5F, 2.0F, 0.0F, 0.0F, 0.0F, -2.3562F));

		PartDefinition doll3 = doll.addOrReplaceChild("doll3", CubeListBuilder.create().texOffs(80, 62).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(1.5F, -2.0F, -1.0F, 3.1416F, 0.0F, -1.8762F));

		PartDefinition doll4 = doll.addOrReplaceChild("doll4", CubeListBuilder.create().texOffs(80, 62).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 0.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.5F, 2.0F, -1.0F, 3.1416F, 0.0F, -0.7854F));

		PartDefinition right_shouldpad = right_arm.addOrReplaceChild("right_shouldpad", CubeListBuilder.create().texOffs(34, 38).mirror().addBox(-4.0F, -2.0F, -3.5F, 6.0F, 12.0F, 7.0F, new CubeDeformation(deformation + 0.35F)).mirror(false), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition left_arm = upper_body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition lefth_arm1 = left_arm.addOrReplaceChild("lefth_arm1", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(deformation)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition knife = lefth_arm1.addOrReplaceChild("knife", CubeListBuilder.create().texOffs(34, 110).addBox(-3.0F, -12.0F, -0.5F, 15.0F, 15.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(-3.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(-3.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(-2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(-1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(0.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(1.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(2.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(0.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(-1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 123).addBox(-1.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(-2.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(-1.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(0.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 125).addBox(1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(2.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(3.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 125).addBox(3.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 123).addBox(3.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 123).addBox(4.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(70, 123).addBox(4.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(5.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(6.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 123).addBox(6.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 123).addBox(6.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(7.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(8.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(9.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(9.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 123).addBox(9.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(82, 121).addBox(10.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(11.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(10.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 121).addBox(9.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 121).addBox(8.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(8.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(8.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(7.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 121).addBox(6.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 121).addBox(5.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(5.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(5.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(5.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(78, 121).addBox(4.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation))
				.texOffs(74, 121).addBox(3.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 10.0F, -0.5F, -1.5708F, -0.7854F, -1.5708F));

		PartDefinition left_shouldpad = left_arm.addOrReplaceChild("left_shouldpad", CubeListBuilder.create().texOffs(34, 38).addBox(-2.0F, -2.0F, -3.5F, 6.0F, 12.0F, 7.0F, new CubeDeformation(deformation + 0.35F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition lower_body = witch.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(28, 78).addBox(-4.0F, 0.5F, -3.0F, 8.0F, 14.0F, 6.0F, new CubeDeformation(deformation + 0.5F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition boby1_r1 = lower_body.addOrReplaceChild("boby1_r1", CubeListBuilder.create().texOffs(0, 76).addBox(-2.0F, -25.75F, -3.0F, 6.0F, 15.75F, 8.0F, new CubeDeformation(deformation - 0.5F)), PartPose.offsetAndRotation(-1.0F, 27.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition boby1_r2 = lower_body.addOrReplaceChild("boby1_r2", CubeListBuilder.create().texOffs(0, 78).addBox(-4.0F, -25.0F, -3.0F, 8.0F, 15.75F, 6.0F, new CubeDeformation(deformation - 0.1F)), PartPose.offsetAndRotation(0.0F, 25.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

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
			this.doll.visible = false;
			this.knife.visible = heresiarch.isCurrentAnimation(Heresiarch.MELEE);
		} else {
			this.disableItems();
		}
	}

	public void disableItems() {
		this.doll.visible = false;
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