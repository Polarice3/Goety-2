package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.client.render.animation.EnchanterAnimations;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.EnchanterServant;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;

public class EnchanterModel<T extends Mob & IEnchanter> extends HierarchicalModel<T> implements HierarchicalArmor {
	private final ModelPart root;
	private final ModelPart illager;
	private final ModelPart upperBody;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart hat;
	private final ModelPart body;
	private final ModelPart clothes;
	private final ModelPart cape;
	private final ModelPart arms;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart book;
	private final ModelPart book_left_cover;
	private final ModelPart book_left_pages;
	private final ModelPart book_right_cover;
	private final ModelPart book_right_pages;
	private final ModelPart book_page_1;
	private final ModelPart book_page_2;
	private final ModelPart book_page_3;
	private final ModelPart book_page_4;

	public EnchanterModel(ModelPart root) {
		this.root = root;
		this.illager = root.getChild("illager");
		this.upperBody = this.illager.getChild("upperBody");
		this.head = this.upperBody.getChild("head");
		this.nose = this.head.getChild("nose");
		this.hat = this.head.getChild("hat");
		this.body = this.upperBody.getChild("body");
		this.clothes = this.body.getChild("clothes");
		this.cape = this.upperBody.getChild("cape");
		this.arms = this.upperBody.getChild("arms");
		this.right_arm = this.upperBody.getChild("right_arm");
		this.left_arm = this.upperBody.getChild("left_arm");
		this.right_leg = this.illager.getChild("right_leg");
		this.left_leg = this.illager.getChild("left_leg");
		this.book = this.illager.getChild("book");
		this.book_left_cover = this.book.getChild("book_left_cover");
		this.book_left_pages = this.book_left_cover.getChild("book_left_pages");
		this.book_right_cover = this.book.getChild("book_right_cover");
		this.book_right_pages = this.book_right_cover.getChild("book_right_pages");
		this.book_page_1 = this.book.getChild("book_page_1");
		this.book_page_2 = this.book.getChild("book_page_2");
		this.book_page_3 = this.book.getChild("book_page_3");
		this.book_page_4 = this.book.getChild("book_page_4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upperBody = illager.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.4998F, -0.0128F, -0.0255F, 0.0F, 0.0F));

		PartDefinition head = upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.5006F, 0.0247F, 0.0452F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-6.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -6.0F, 0.0F));

		PartDefinition body = upperBody.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.5F, 0.0F));

		PartDefinition clothes = body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cape = upperBody.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(72, 0).addBox(-7.0F, -0.5F, -0.5F, 14.0F, 23.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.7735F, 3.2717F, 0.0916F, 0.0F, 0.0F));

		PartDefinition arms = upperBody.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.5F, -1.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition right_arm = upperBody.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -9.5F, 0.0F));

		PartDefinition left_arm = upperBody.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -9.5F, 0.0F));

		PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));

		PartDefinition book = illager.addOrReplaceChild("book", CubeListBuilder.create().texOffs(60, 24).addBox(-1.5F, -0.5F, -6.0F, 3.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -20.8927F, -6.5188F, 0.0F, 0.715F, 1.5708F));

		PartDefinition book_left_cover = book.addOrReplaceChild("book_left_cover", CubeListBuilder.create().texOffs(102, 0).addBox(0.0F, -8.0F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -0.5F, 0.0F));

		PartDefinition book_left_pages = book_left_cover.addOrReplaceChild("book_left_pages", CubeListBuilder.create().texOffs(76, 58).addBox(-0.5F, -7.0F, -5.0F, 0.5F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition book_right_cover = book.addOrReplaceChild("book_right_cover", CubeListBuilder.create().texOffs(102, 20).addBox(-1.0F, -8.0F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -0.5F, 0.0F));

		PartDefinition book_right_pages = book_right_cover.addOrReplaceChild("book_right_pages", CubeListBuilder.create().texOffs(97, 58).addBox(0.0F, -7.0F, -5.0F, 0.5F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition book_page_1 = book.addOrReplaceChild("book_page_1", CubeListBuilder.create().texOffs(64, 37).addBox(0.0F, -7.5F, -5.0F, 0.0F, 7.5F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition book_page_2 = book.addOrReplaceChild("book_page_2", CubeListBuilder.create().texOffs(84, 40).addBox(0.0F, -7.5F, -5.0F, 0.0F, 7.5F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition book_page_3 = book.addOrReplaceChild("book_page_3", CubeListBuilder.create().texOffs(104, 40).addBox(0.0F, -7.5F, -5.0F, 0.0F, 7.5F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition book_page_4 = book.addOrReplaceChild("book_page_4", CubeListBuilder.create().texOffs(56, 55).addBox(0.0F, -7.5F, -5.0F, 0.0F, 7.5F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()){
			this.animateHeadLookTarget(netHeadYaw, headPitch);
		}
		if (this.riding){
			this.right_leg.xRot = -1.4137167F;
			this.right_leg.yRot = ((float)Math.PI / 10F);
			this.right_leg.zRot = 0.07853982F;
			this.left_leg.xRot = -1.4137167F;
			this.left_leg.yRot = (-(float)Math.PI / 10F);
			this.left_leg.zRot = -0.07853982F;
		} else {
			this.animateWalk(EnchanterAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		if (entity instanceof EnchanterServant servant) {
			this.animate(servant.idleAnimationState, EnchanterAnimations.IDLE, ageInTicks);
			this.animate(servant.attackAnimationState, EnchanterAnimations.ATTACK, ageInTicks);
			this.animate(servant.enchantAnimationState, EnchanterAnimations.ENCHANT, ageInTicks);
			this.animate(servant.noveltyAnimationState, EnchanterAnimations.NOVELTY, ageInTicks);
		}
		if (!(entity.isCurrentAnimation(IEnchanter.ENCHANT) || entity.isCurrentAnimation(IEnchanter.NOVELTY))) {
			this.right_arm.xRot = -0.75F;
			this.left_arm.xRot = -0.75F;
		}
		this.right_arm.visible = entity.showArms();
		this.left_arm.visible = entity.showArms();
		boolean flag2 = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
		this.clothes.visible = !flag2;
	}

	private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.illager.translateAndRotate(poseStack);
		this.upperBody.translateAndRotate(poseStack);
		this.head.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(-(1F / 16F), (1F / 16F), 0);
		poseStack.scale(1.05F, 1.05F, 1.05F);
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
		return ImmutableList.of(this.right_arm);
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of(this.left_arm);
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.right_leg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.left_leg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.hat);
	}
}