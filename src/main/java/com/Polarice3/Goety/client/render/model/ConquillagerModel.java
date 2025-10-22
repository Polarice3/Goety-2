package com.Polarice3.Goety.client.render.model;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;

public class ConquillagerModel<T extends AbstractIllager> extends HumanoidModel<T> {
	public final ModelPart clothes;
	public final ModelPart hat2;

	public ConquillagerModel(ModelPart root) {
		super(root);
		this.hat2 = root.getChild("head").getChild("hat");
		this.clothes = root.getChild("clothes");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition leftbeard = head.addOrReplaceChild("leftbeard", CubeListBuilder.create().texOffs(32, 14).mirror().addBox(-0.25F, -0.85F, -1.25F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -3.0F, -3.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition rightbeard = head.addOrReplaceChild("rightbeard", CubeListBuilder.create().texOffs(32, 14).addBox(-4.75F, -0.85F, -1.25F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -3.0F, -3.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(96, 95).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.2F))
				.texOffs(96, 83).addBox(-4.0F, -1.95F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.45F))
				.texOffs(80, 110).addBox(-9.0F, 0.5F, -9.0F, 15.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0436F, 0.0F, -0.0436F));

		PartDefinition hat1 = hat.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(92, 31).addBox(0.0F, -6.0F, -9.0F, 0.0F, 6.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition hat2 = hat.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(94, 64).addBox(-2.5F, -6.0F, 0.0F, 5.0F, 6.0F, 12.0F, new CubeDeformation(0.25F))
				.texOffs(104, 45).addBox(0.0F, -6.25F, -0.25F, 0.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 2.0F, -0.3927F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition clothes = partdefinition.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(28, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 17.0F, 6.0F, new CubeDeformation(0.5F))
				.texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 17.0F, 6.0F, new CubeDeformation(0.3F))
				.texOffs(0, 62).addBox(-5.5F, -24.75F, -5.5F, 11.0F, 3.0F, 11.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(60, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(32, 0).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(60, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(32, 0).mirror().addBox(-1.0F, -4.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.1F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.95F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.clothes));
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		if (this.riding) {
			this.rightArm.xRot = (-(float)Math.PI / 5F);
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = (-(float)Math.PI / 5F);
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = ((float)Math.PI / 10F);
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = (-(float)Math.PI / 10F);
			this.leftLeg.zRot = -0.07853982F;
		} else {
			this.rightArm.y = 2.0F;
			this.leftArm.y = 2.0F;
			this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
			this.rightLeg.yRot = 0.0F;
			this.rightLeg.zRot = 0.0F;
			this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
			this.leftLeg.yRot = 0.0F;
			this.leftLeg.zRot = 0.0F;
		}

		AbstractIllager.IllagerArmPose abstractprotectorentity$armpose = entity.getArmPose();
		switch (abstractprotectorentity$armpose){
			case CROSSED:
				this.rightArm.xRot = 0;
				this.leftArm.xRot = 0;
				break;
			case ATTACKING:
				if (!entity.getMainHandItem().isEmpty() && !(entity.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
					AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entity, this.attackTime, ageInTicks);
				}
				break;
			case CROSSBOW_CHARGE:
				AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
				break;
			case CROSSBOW_HOLD:
				AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
				break;
			case SPELLCASTING:
				this.rightArm.z = 0.0F;
				this.rightArm.x = -5.0F;
				this.leftArm.z = 0.0F;
				this.leftArm.x = 5.0F;
				this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
				this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
				this.rightArm.zRot = 2.3561945F;
				this.leftArm.zRot = -2.3561945F;
				this.rightArm.yRot = 0.0F;
				this.leftArm.yRot = 0.0F;
		}
		boolean flag2 = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
				|| entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
		this.clothes.visible = !flag2;
		boolean flag3 = entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem;
		this.hat2.visible = !flag3;
	}

	public boolean isAggressive(T entityIn) {
		return entityIn.isAggressive();
	}

	public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		this.rightArmPose = ArmPose.EMPTY;
		this.leftArmPose = ArmPose.EMPTY;
		if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
			this.RightArmPoses(InteractionHand.MAIN_HAND, entityIn);
			this.LeftArmPoses(InteractionHand.OFF_HAND, entityIn);
		} else {
			this.RightArmPoses(InteractionHand.OFF_HAND, entityIn);
			this.LeftArmPoses(InteractionHand.MAIN_HAND, entityIn);
		}
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
	}

	private void RightArmPoses (InteractionHand hand, T entityIn){
		ItemStack itemstack = entityIn.getItemInHand(hand);
		UseAnim useAction = itemstack.getUseAnimation();
		if (entityIn.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED){
			this.rightArmPose = ArmPose.EMPTY;
			if (!itemstack.isEmpty()) {
				this.rightArmPose = ArmPose.ITEM;
			}
		}
	}

	private void LeftArmPoses (InteractionHand hand, T entityIn){
		ItemStack itemstack = entityIn.getItemInHand(hand);
		UseAnim useAction = itemstack.getUseAnimation();
		if (entityIn.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED){
			this.leftArmPose = ArmPose.EMPTY;
			if (!itemstack.isEmpty()) {
				this.leftArmPose = ArmPose.ITEM;
			}
		}
	}

	private ModelPart getthisArm(HumanoidArm p_191216_1_) {
		return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	public ModelPart getHead() {
		return this.head;
	}

	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.getthisArm(sideIn).translateAndRotate(matrixStackIn);
	}
}