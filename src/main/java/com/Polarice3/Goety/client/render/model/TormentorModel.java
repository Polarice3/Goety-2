package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.illagers.Tormentor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ArmorItem;

public class TormentorModel<T extends Tormentor> extends HumanoidModel<T> {
	public final ModelPart clothes;
	private final ModelPart RightWing;
	private final ModelPart LeftWing;

	public TormentorModel(ModelPart root) {
		super(root);
		this.clothes = this.body.getChild("clothes");
		this.RightWing = this.body.getChild("RightWing");
		this.LeftWing = this.body.getChild("LeftWing");
		this.leftLeg.visible = false;
		this.hat.visible = false;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-4.0F, 1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		body.addOrReplaceChild("RightWing", CubeListBuilder.create().texOffs(32, 0).addBox(-16.0F, -1.0F, 0.0F, 16.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 4.0F));

		body.addOrReplaceChild("LeftWing", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(0.0F, -1.0F, 0.0F, 16.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.RightWing, this.LeftWing, this.clothes));
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.rightArm.y = 2.0F;
		this.leftArm.y = 2.0F;
		this.rightLeg.xRot += ((float)Math.PI / 5F);
		this.RightWing.z = 2.0F;
		this.LeftWing.z = 2.0F;
		this.RightWing.y = 1.0F;
		this.LeftWing.y = 1.0F;
		this.RightWing.yRot = 0.47123894F + Mth.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
		this.LeftWing.yRot = -this.RightWing.yRot;
		this.LeftWing.zRot = -0.47123894F;
		this.LeftWing.xRot = 0.47123894F;
		this.RightWing.xRot = 0.47123894F;
		this.RightWing.zRot = 0.47123894F;

		if (entity.isCharging()) {
			if (entity.getMainHandItem().isEmpty()) {
				this.rightArm.xRot = ((float)Math.PI * 1.5F);
				this.leftArm.xRot = ((float)Math.PI * 1.5F);
			} else if (entity.getMainArm() == HumanoidArm.RIGHT) {
				this.rightArm.xRot = 3.7699115F;
			} else {
				this.leftArm.xRot = 3.7699115F;
			}
		}

		AbstractIllager.IllagerArmPose abstractillagerentity$armpose = entity.getArmPose();
		if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
			this.rightArm.zRot = 2.670354F;
			this.rightArm.yRot = 0.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;
			this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
			this.leftArm.zRot = -2.3561945F;
			this.leftArm.yRot = 0.0F;
		}

		boolean flag2 = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
				|| entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
		this.clothes.visible = !flag2;
	}
}