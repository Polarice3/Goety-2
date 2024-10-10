package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

public class MaverickModel<T extends Maverick> extends CultistModel<T> {

	public MaverickModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = createMesh();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition head = partdefinition.getChild("head");
		PartDefinition nose = head.getChild("nose");
		nose.addOrReplaceChild("mole", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -2.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
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
	}

	public ModelPart getNose() {
		return this.nose;
	}
}