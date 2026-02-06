package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class WarlockServantModel<T extends WarlockServant> extends CultistServantModel<T> {

	public WarlockServantModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.hat.visible = true;
		this.nose.setPos(0.0F, -2.0F, 0.0F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
	}

}