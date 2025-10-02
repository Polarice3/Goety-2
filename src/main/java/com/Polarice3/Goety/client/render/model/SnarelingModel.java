package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.SnarelingAnimations;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractSnareling;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SnarelingModel<T extends AbstractSnareling> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart snareling;
	private final ModelPart body;
	private final ModelPart upper;
	private final ModelPart mouth;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart upper_right;
	private final ModelPart lower_right;
	private final ModelPart left_arm;
	private final ModelPart upper_left;
	private final ModelPart lower_left;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public SnarelingModel(ModelPart root) {
		this.root = root;
		this.snareling = root.getChild("snareling");
		this.body = this.snareling.getChild("body");
		this.upper = this.body.getChild("upper");
		this.mouth = this.upper.getChild("mouth");
		this.top = this.mouth.getChild("top");
		this.bottom = this.mouth.getChild("bottom");
		this.head = this.upper.getChild("head");
		this.right_arm = this.body.getChild("right_arm");
		this.upper_right = this.right_arm.getChild("upper_right");
		this.lower_right = this.right_arm.getChild("lower_right");
		this.left_arm = this.body.getChild("left_arm");
		this.upper_left = this.left_arm.getChild("upper_left");
		this.lower_left = this.left_arm.getChild("lower_left");
		this.right_leg = this.snareling.getChild("right_leg");
		this.left_leg = this.snareling.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition snareling = partdefinition.addOrReplaceChild("snareling", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = snareling.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, -24.0F, -3.45F, -0.2182F, 0.0F, 0.0349F));

		PartDefinition upper = body.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(0, 15).addBox(-5.5F, -12.0F, -5.45F, 11.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(38, 14).addBox(-5.5F, -12.0F, -5.45F, 11.0F, 12.0F, 8.0F, new CubeDeformation(-0.01F)), PartPose.offset(-1.5F, 0.0F, 3.45F));

		PartDefinition sack = upper.addOrReplaceChild("sack", CubeListBuilder.create().texOffs(32, 0).addBox(-4.5F, -4.5F, 0.5F, 9.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.5F, 2.05F));

		PartDefinition mouth = upper.addOrReplaceChild("mouth", CubeListBuilder.create(), PartPose.offset(1.5F, 0.0F, -3.45F));

		PartDefinition top = mouth.addOrReplaceChild("top", CubeListBuilder.create().texOffs(38, 34).addBox(-4.5F, -1.5F, 0.0F, 9.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -7.75F, -2.025F));

		PartDefinition bottom = mouth.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(38, 37).addBox(-4.5F, -1.5F, 0.0F, 9.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -4.25F, -2.025F));

		PartDefinition head = upper.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -8.0F, -3.5F, 9.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -0.95F, -0.0436F, 0.0F, 0.0F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, -3.6F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-7.0F, -11.0F, 2.0F, 0.2182F, 0.0F, -0.0349F));

		PartDefinition upper_right = right_arm.addOrReplaceChild("upper_right", CubeListBuilder.create().texOffs(76, 0).addBox(-1.0F, -36.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 35.0F, 0.0F));

		PartDefinition lower_right = right_arm.addOrReplaceChild("lower_right", CubeListBuilder.create().texOffs(8, 35).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 34.0F, 1.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -11.0F, 2.0F, 0.192F, 0.0F, -0.0436F));

		PartDefinition upper_left = left_arm.addOrReplaceChild("upper_left", CubeListBuilder.create().texOffs(76, 0).addBox(-1.0F, -36.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 35.0F, 0.0F));

		PartDefinition lower_left = left_arm.addOrReplaceChild("lower_left", CubeListBuilder.create().texOffs(8, 35).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 34.0F, 1.0F));

		PartDefinition right_leg = snareling.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -24.0F, -1.45F, -0.3054F, 0.3054F, -0.0436F));

		PartDefinition left_leg = snareling.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -24.0F, -1.45F, -0.3054F, -0.3054F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		this.animate(entity.idleAnimationState, SnarelingAnimations.IDLE, ageInTicks);
		this.animateWalk(SnarelingAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
		this.animate(entity.attackAnimationState, SnarelingAnimations.MELEE, ageInTicks);
		this.animate(entity.shootAnimationState, SnarelingAnimations.SHOOT, ageInTicks);
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