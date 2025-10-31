package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Items;

public class MaverickModel<T extends Maverick> extends CultistModel<T> {
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart hat;
	private final ModelPart hat1;
	private final ModelPart body;
	private final ModelPart robe;
	private final ModelPart right_arm;
	private final ModelPart right_cloth;
	private final ModelPart sickle;
	private final ModelPart left_arm;
	private final ModelPart left_cloth;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public MaverickModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.nose = this.head.getChild("nose");
		this.hat = this.head.getChild("hat");
		this.hat1 = this.hat.getChild("hat1");
		this.body = root.getChild("body");
		this.robe = this.body.getChild("robe");
		this.right_arm = root.getChild("right_arm");
		this.right_cloth = this.right_arm.getChild("right_cloth");
		this.sickle = this.right_arm.getChild("sickle");
		this.left_arm = root.getChild("left_arm");
		this.left_cloth = this.left_arm.getChild("left_cloth");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = createMesh();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("clothes", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-3.5F, -10.5F, -5.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 1.0F));

		PartDefinition hat1 = hat.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(59, 2).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -10.5F, 3.5F, -0.6545F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(16, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.15F))
				.texOffs(84, 14).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 11.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition right_cloth = right_arm.addOrReplaceChild("right_cloth", CubeListBuilder.create().texOffs(44, 38).mirror().addBox(-9.0F, -24.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition sickle = right_arm.addOrReplaceChild("sickle", CubeListBuilder.create().texOffs(98, 49).addBox(-2.0F, -12.0F, -0.5F, 14.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(-2.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(5.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(3.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(3.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(6.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(7.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 43).addBox(11.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 43).addBox(11.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 43).addBox(10.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 43).addBox(9.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 43).addBox(8.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 43).addBox(4.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(-2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(-1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 47).addBox(0.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 47).addBox(1.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 45).addBox(0.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 45).addBox(1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(-1.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(0.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 47).addBox(1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(99, 47).addBox(2.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(103, 45).addBox(3.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(3.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(4.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(4.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(5.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(6.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(7.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(8.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(9.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(10.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(10.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(11.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(11.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(5.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(4.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(3.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(2.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(2.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(3.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(3.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(107, 43).addBox(3.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(111, 43).addBox(2.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 9.0F, -0.5F, 1.5708F, 0.7854F, 1.5708F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition left_cloth = left_arm.addOrReplaceChild("left_cloth", CubeListBuilder.create().texOffs(44, 38).addBox(3.0F, -24.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.nose.setPos(0.0F, -2.0F, 0.0F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);

		Cultist.CultistArmPose cultistArmPose = entity.getArmPose();
		if (cultistArmPose == Cultist.CultistArmPose.ITEM && entity.getOffhandItem().is(Items.POTION)) {
			this.nose.setPos(0.0F, 1.0F, -1.5F);
			this.nose.xRot = -0.9F;
		}
		boolean hasChest = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
		this.right_cloth.visible = !hasChest;
		this.left_cloth.visible = !hasChest;
		boolean flag2 = hasChest || entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
		this.robe.visible = !flag2;
		boolean flag3 = entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem;
		this.hat.visible = !flag3;
		this.sickle.visible = false;
	}

	public ModelPart getNose() {
		return this.nose;
	}
}