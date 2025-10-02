package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class VindicatorChefModel<T extends LivingEntity> extends IllagerServantModel<T> {
	private final ModelPart chef_hat;

	public VindicatorChefModel(ModelPart root) {
		super(root);
		this.chef_hat = this.head.getChild("chef_hat");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = IllagerServantModel.createMesh();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.getChild("head");

		PartDefinition chef_hat = head.addOrReplaceChild("chef_hat", CubeListBuilder.create().texOffs(80, 0).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(80, 18).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.getChild("body");

		PartDefinition clothes = body.getChild("clothes");

		PartDefinition apron = clothes.addOrReplaceChild("apron", CubeListBuilder.create().texOffs(100, 31).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
		this.chef_hat.visible = headItem.isEmpty() || headItem.is(ItemTags.BANNERS);
	}
}