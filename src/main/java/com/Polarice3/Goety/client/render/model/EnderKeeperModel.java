package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.EnderKeeperAnimations;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.utils.ModelUtil;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;
import java.util.stream.Stream;

public class EnderKeeperModel<T extends EnderKeeper> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart sword;
    public final List<String> allPartNames;

	public EnderKeeperModel(ModelPart root) {
		this.root = root;
		ModelPart keeper = root.getChild("keeper");
		ModelPart main = keeper.getChild("main");
		this.head = main.getChild("head");
		this.sword = keeper.getChild("sword");
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

		PartDefinition keeper = partdefinition.addOrReplaceChild("keeper", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 0.0F));

		PartDefinition main = keeper.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = main.addOrReplaceChild("head", CubeListBuilder.create().texOffs(80, 54).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.25F));

		PartDefinition horns = helmet.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_horn = horns.addOrReplaceChild("right_horn", CubeListBuilder.create(), PartPose.offset(-4.5F, -8.0F, -0.5F));

        PartDefinition rh_lower = right_horn.addOrReplaceChild("rh_lower", CubeListBuilder.create().texOffs(150, 0).addBox(0.0F, -9.0F, 0.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(1.0F, 5.5F, 0.25F, 0.1745F, 0.0F, -0.8727F));

        PartDefinition rh_upper = rh_lower.addOrReplaceChild("rh_upper", CubeListBuilder.create().texOffs(96, 153).addBox(0.25F, -10.0F, 0.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.75F, -9.0F, 1.0F, 0.3054F, 0.0F, 1.2654F));

        PartDefinition head_r1 = rh_upper.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(160, 81).addBox(-3.0F, -8.0F, 0.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(4.25F, -10.0F, 0.0F, -0.3054F, 0.0F, -0.829F));

		PartDefinition left_horn = horns.addOrReplaceChild("left_horn", CubeListBuilder.create(), PartPose.offset(4.5F, -8.0F, -0.5F));

        PartDefinition lh_lower = left_horn.addOrReplaceChild("lh_lower", CubeListBuilder.create().texOffs(152, 15).addBox(-4.0F, -9.0F, 0.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-1.0F, 5.5F, 0.25F, 0.1745F, 0.0F, 0.8727F));

        PartDefinition lh_upper = lh_lower.addOrReplaceChild("lh_upper", CubeListBuilder.create().texOffs(46, 155).addBox(-4.25F, -10.0F, 0.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.75F, -9.0F, 1.0F, 0.3054F, 0.0F, -1.2654F));

        PartDefinition head_r2 = lh_upper.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(22, 161).addBox(0.0F, -8.0F, 0.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-4.25F, -10.0F, 0.0F, -0.3054F, 0.0F, 0.829F));

		PartDefinition helm = helmet.addOrReplaceChild("helm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -4.5F, -3.0F, -0.9599F, 0.0F, 0.0F));

        PartDefinition helm_rot = helm.addOrReplaceChild("helm_rot", CubeListBuilder.create().texOffs(52, 129).addBox(-1.5F, -4.0F, -4.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(0, 137).addBox(-1.5F, -10.0F, -4.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(76, 137).addBox(-1.5F, 2.0F, -4.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(126, 76).addBox(-5.5F, -4.0F, -4.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(126, 66).addBox(-9.5F, -4.0F, -4.5F, 8.0F, 4.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(162, 126).addBox(-5.5F, 0.0F, -4.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(92, 116).addBox(1.5F, -4.0F, 1.5F, 3.0F, 4.0F, 4.0F, new CubeDeformation(deformation))
                .texOffs(92, 125).addBox(-1.5F, -4.0F, 1.5F, 6.0F, 4.0F, 8.0F, new CubeDeformation(deformation))
                .texOffs(158, 114).addBox(1.5F, 0.0F, 1.5F, 3.0F, 2.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.7854F));

		PartDefinition upper_body = main.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = upper_body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(126, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(deformation + 0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lower = body.addOrReplaceChild("lower", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition body_r1 = lower.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(162, 91).addBox(-2.0F, -4.0F, -4.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, -2.5F, 2.75F, 0.7854F, 1.3526F, 1.5708F));

        PartDefinition body_r2 = lower.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(32, 161).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, -2.5F, -2.75F, 0.7854F, -1.3526F, 1.5708F));

		PartDefinition right_plate = lower.addOrReplaceChild("right_plate", CubeListBuilder.create(), PartPose.offset(-4.0F, -7.0F, 0.0F));

        PartDefinition rp_4 = right_plate.addOrReplaceChild("rp_4", CubeListBuilder.create().texOffs(100, 137).addBox(-0.25F, 9.75F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(140, 76).addBox(-0.25F, -0.25F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.25F, 0.25F, 0.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition rp_5 = right_plate.addOrReplaceChild("rp_5", CubeListBuilder.create().texOffs(140, 121).addBox(-3.75F, 0.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation))
                .texOffs(140, 135).addBox(0.25F, 0.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation))
                .texOffs(52, 141).addBox(0.25F, 7.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition left_plate = lower.addOrReplaceChild("left_plate", CubeListBuilder.create(), PartPose.offset(4.0F, -7.0F, 0.0F));

        PartDefinition lp_4 = left_plate.addOrReplaceChild("lp_4", CubeListBuilder.create().texOffs(142, 92).addBox(-3.75F, -0.25F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(deformation))
                .texOffs(120, 146).addBox(-3.75F, 9.75F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.25F, 0.25F, 0.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition lp_5 = left_plate.addOrReplaceChild("lp_5", CubeListBuilder.create().texOffs(24, 147).addBox(-4.25F, 0.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation))
                .texOffs(0, 149).addBox(-4.25F, 7.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation))
                .texOffs(74, 149).addBox(-0.25F, 0.0F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, 0.0F, 0.0F, -0.7418F));

		PartDefinition cloak = upper_body.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition middle_armor = cloak.addOrReplaceChild("middle_armor", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 0.0F));

		PartDefinition chestplate_body_plackart = middle_armor.addOrReplaceChild("chestplate_body_plackart", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 0.0F));

		PartDefinition chestplate_body_plackart_front = chestplate_body_plackart.addOrReplaceChild("chestplate_body_plackart_front", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

		PartDefinition chestplate_body_plackart_front_center = chestplate_body_plackart_front.addOrReplaceChild("chestplate_body_plackart_front_center", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.5F, -1.25F, -0.7854F, 0.0F, 0.0F));

        PartDefinition chestplate_body_plackart_front_center_x = chestplate_body_plackart_front_center.addOrReplaceChild("chestplate_body_plackart_front_center_x", CubeListBuilder.create().texOffs(154, 64).addBox(-4.0F, -4.0F, 0.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(deformation))
                .texOffs(162, 131).addBox(-7.0F, -4.0F, 0.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(162, 99).addBox(-4.0F, -7.0F, 0.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition chestplate_body_plackart_back = chestplate_body_plackart.addOrReplaceChild("chestplate_body_plackart_back", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 5.0F));

		PartDefinition chestplate_body_plackart_back_center = chestplate_body_plackart_back.addOrReplaceChild("chestplate_body_plackart_back_center", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 8.75F, -2.25F, -0.3054F, 0.0F, 0.0F));

        PartDefinition chestplate_body_plackart_back_center_x = chestplate_body_plackart_back_center.addOrReplaceChild("chestplate_body_plackart_back_center_x", CubeListBuilder.create().texOffs(76, 129).addBox(0.0F, -5.0F, -2.0F, 5.0F, 5.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(118, 162).addBox(5.0F, -5.0F, -2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(142, 114).addBox(0.0F, -8.0F, -2.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cape = middle_armor.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 75).addBox(-10.0F, 0.0F, -1.0F, 20.0F, 36.0F, 1.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 1.0F, 3.75F, 0.1309F, 0.0F, 0.0F));

		PartDefinition right_pauldron = cloak.addOrReplaceChild("right_pauldron", CubeListBuilder.create(), PartPose.offset(-4.0F, 2.0F, 0.0F));

		PartDefinition rp_0 = right_pauldron.addOrReplaceChild("rp_0", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition rp_1 = rp_0.addOrReplaceChild("rp_1", CubeListBuilder.create().texOffs(80, 30).addBox(-11.75F, -0.25F, -4.0F, 12.0F, 4.0F, 8.0F, new CubeDeformation(deformation))
                .texOffs(0, 0).addBox(-19.75F, 1.75F, -8.0F, 19.0F, 0.0F, 16.0F, new CubeDeformation(deformation))
                .texOffs(0, 112).addBox(-19.75F, -0.25F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.5F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition rp_2 = rp_0.addOrReplaceChild("rp_2", CubeListBuilder.create().texOffs(92, 70).addBox(-8.0F, 0.0F, -4.5F, 8.0F, 4.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(70, 0).addBox(-14.0F, 1.0F, -7.5F, 13.0F, 0.0F, 15.0F, new CubeDeformation(deformation))
                .texOffs(32, 116).addBox(-8.0F, -4.0F, -4.5F, 6.0F, 4.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(62, 116).addBox(-14.0F, 0.0F, -4.5F, 6.0F, 4.0F, 9.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition right_cloak = right_pauldron.addOrReplaceChild("right_cloak", CubeListBuilder.create().texOffs(0, 32).addBox(-12.5F, 0.25F, -3.5F, 13.0F, 36.0F, 7.0F, new CubeDeformation(deformation + 0.005F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition left_pauldron = cloak.addOrReplaceChild("left_pauldron", CubeListBuilder.create(), PartPose.offset(4.0F, 2.0F, 0.0F));

		PartDefinition lp_0 = left_pauldron.addOrReplaceChild("lp_0", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition lp_1 = lp_0.addOrReplaceChild("lp_1", CubeListBuilder.create().texOffs(80, 42).addBox(-0.25F, -0.25F, -4.0F, 12.0F, 4.0F, 8.0F, new CubeDeformation(deformation))
                .texOffs(112, 54).addBox(11.75F, -0.25F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(deformation))
                .texOffs(0, 16).addBox(0.75F, 1.75F, -8.0F, 19.0F, 0.0F, 16.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition lp_2 = lp_0.addOrReplaceChild("lp_2", CubeListBuilder.create().texOffs(106, 83).addBox(0.0F, 0.0F, -4.5F, 8.0F, 4.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(120, 30).addBox(8.0F, 0.0F, -4.5F, 6.0F, 4.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(0, 124).addBox(2.0F, -4.0F, -4.5F, 6.0F, 4.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(70, 15).addBox(1.0F, 1.0F, -7.5F, 13.0F, 0.0F, 15.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition left_cloak = left_pauldron.addOrReplaceChild("left_cloak", CubeListBuilder.create().texOffs(40, 32).addBox(-0.5F, 0.25F, -3.5F, 13.0F, 36.0F, 7.0F, new CubeDeformation(deformation + 0.005F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition sword = keeper.addOrReplaceChild("sword", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 17.0F, -4.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition sword_rot = sword.addOrReplaceChild("sword_rot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 8.0F, -19.0F, 1.5708F, 0.0F, -1.5708F));

        PartDefinition blade = sword_rot.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(106, 96).addBox(-4.0F, -29.0F, -23.5F, 8.0F, 28.0F, 1.0F, new CubeDeformation(deformation))
                .texOffs(30, 129).addBox(-4.5F, -1.0F, -24.0F, 9.0F, 16.0F, 2.0F, new CubeDeformation(deformation)), PartPose.offset(0.0F, -29.0F, 24.0F));

        PartDefinition edge = blade.addOrReplaceChild("edge", CubeListBuilder.create().texOffs(124, 121).addBox(-3.5F, -12.0F, -0.5F, 7.0F, 24.0F, 1.0F, new CubeDeformation(deformation + 0.005F)), PartPose.offset(0.0F, -41.0F, -23.0F));

		PartDefinition tip = edge.addOrReplaceChild("tip", CubeListBuilder.create(), PartPose.offset(11.0F, -3.5F, 0.0F));

        PartDefinition cube_r1 = tip.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 163).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(deformation + 0.007F)), PartPose.offsetAndRotation(-11.0F, -8.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition handle = sword_rot.addOrReplaceChild("handle", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 1.0F));

        PartDefinition guard = handle.addOrReplaceChild("guard", CubeListBuilder.create().texOffs(126, 16).addBox(-4.0F, -4.0F, -2.5F, 8.0F, 8.0F, 5.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r2 = guard.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(84, 163).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(deformation))
                .texOffs(72, 163).addBox(-2.5F, -2.5F, -6.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r3 = guard.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(140, 149).addBox(-8.0F, -16.0F, 0.0F, 10.0F, 16.0F, 0.0F, new CubeDeformation(deformation))
                .texOffs(124, 96).addBox(-1.5F, -16.0F, -4.5F, 0.0F, 16.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(60, 155).addBox(-3.0F, -10.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r4 = guard.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(120, 43).addBox(-16.0F, -8.0F, 0.0F, 16.0F, 10.0F, 0.0F, new CubeDeformation(deformation))
                .texOffs(42, 75).addBox(-16.0F, -1.5F, -4.5F, 16.0F, 0.0F, 9.0F, new CubeDeformation(deformation))
                .texOffs(142, 108).addBox(-10.0F, -3.0F, -1.5F, 10.0F, 3.0F, 3.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(2.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition hilt = handle.addOrReplaceChild("hilt", CubeListBuilder.create().texOffs(152, 30).addBox(5.5F, 5.0F, -3.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(deformation))
                .texOffs(152, 47).addBox(5.5F, 5.0F, -3.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(deformation + 0.25F)), PartPose.offset(-7.0F, -10.0F, 1.5F));

		PartDefinition pommel = handle.addOrReplaceChild("pommel", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r5 = pommel.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(160, 149).addBox(0.0F, 0.0F, 0.0F, 0.0F, 12.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 7.25F, -2.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r6 = pommel.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(110, 153).addBox(0.0F, 0.0F, -4.0F, 0.0F, 12.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 7.25F, 2.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r7 = pommel.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(162, 120).addBox(2.0F, -2.0F, -1.0F, 5.0F, 4.0F, 2.0F, new CubeDeformation(deformation))
                .texOffs(162, 138).addBox(-2.0F, 2.0F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(deformation))
                .texOffs(160, 73).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(deformation)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying() && !entity.isAttacking()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (!entity.isAttacking()){
			this.animateWalk(EnderKeeperAnimations.MOVE, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.introAnimationState, EnderKeeperAnimations.INTRO, ageInTicks);
		this.animate(entity.idleAnimationState, EnderKeeperAnimations.IDLE, ageInTicks);
		this.animate(entity.swingAnimationState, EnderKeeperAnimations.SWING, ageInTicks);
		this.animate(entity.swingComboAnimationState, EnderKeeperAnimations.SWING_COMBO, ageInTicks);
		this.animate(entity.swingComboTripleAnimationState, EnderKeeperAnimations.SWING_COMBO_TRIPLE, ageInTicks);
		this.animate(entity.rapidSwingAnimationState, EnderKeeperAnimations.RAPID_SWING, ageInTicks);
		this.animate(entity.chargeAnimationState, EnderKeeperAnimations.CHARGE, ageInTicks);
		this.animate(entity.spell1AnimationState, EnderKeeperAnimations.SPELL, ageInTicks);
		this.animate(entity.spell2AnimationState, EnderKeeperAnimations.SPELL2, ageInTicks);
		this.animate(entity.spell3AnimationState, EnderKeeperAnimations.SPELL3, ageInTicks);
		this.animate(entity.lifeStealAnimationState, EnderKeeperAnimations.LIFE_STEAL, ageInTicks);
		this.animate(entity.groundPoundAnimationState, EnderKeeperAnimations.GROUND_POUND, ageInTicks);
		this.animate(entity.groundPoundSpinAnimationState, EnderKeeperAnimations.GROUND_POUND_SP, ageInTicks);
		this.animate(entity.backAwayAnimationState, EnderKeeperAnimations.BACK_AWAY, ageInTicks);
		this.animate(entity.slice1AnimationState, EnderKeeperAnimations.SLICE, ageInTicks);
		this.animate(entity.slice2AnimationState, EnderKeeperAnimations.SLICE2, ageInTicks);
		this.animate(entity.deathAnimationState, EnderKeeperAnimations.DEATH, ageInTicks);
		if (entity.shakeSword > 0) {
			this.sword.xRot += (-0.5F + entity.getRandom().nextFloat()) / 2.0F * 5.0F * 0.017453292F;
			this.sword.yRot += (-0.5F + entity.getRandom().nextFloat()) / 2.0F * 5.0F * 0.017453292F;
			this.sword.zRot += (-0.5F + entity.getRandom().nextFloat()) / 2.0F * 5.0F * 0.017453292F;
		}
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