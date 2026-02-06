package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.MaverickServant;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Items;

public class MaverickServantModel<T extends MaverickServant> extends CultistServantModel<T> {
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

	public MaverickServantModel(ModelPart root) {
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

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.nose.setPos(0.0F, -2.0F, 0.0F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);

		CultistServant.CultistServantArmPose cultistArmPose = entity.getArmPose();
		if (cultistArmPose == CultistServant.CultistServantArmPose.ITEM && entity.getOffhandItem().is(Items.POTION)) {
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