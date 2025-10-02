package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.phys.Vec3;

public class VoidShockBombModel<T extends VoidShockBomb> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart bolt;
	private final ModelPart outer;
	private final ModelPart inner;

	public VoidShockBombModel(ModelPart root) {
		this.root = root;
		this.bolt = root.getChild("bolt");
		this.outer = this.bolt.getChild("outer");
		this.inner = this.bolt.getChild("inner");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bolt = partdefinition.addOrReplaceChild("bolt", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition outer = bolt.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition inner = bolt.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 32).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float delta = ageInTicks - entity.tickCount;
		Vec3 prevV = new Vec3(entity.prevDeltaMovementX, entity.prevDeltaMovementY, entity.prevDeltaMovementZ);
		Vec3 dv = prevV.add(entity.getDeltaMovement().subtract(prevV).scale(delta));
		double d = Math.sqrt(dv.x * dv.x + dv.y * dv.y + dv.z * dv.z);
		if (d != 0) {
			double a = dv.y / d;
			a = Math.max(-10, Math.min(1, a));
			float pitch = -(float) Math.asin(a);
			this.bolt.xRot = pitch + (float)Math.PI / 2f;

		}
		this.inner.yRot = ageInTicks * 20 * ((float)Math.PI / 180F);
		this.inner.xRot = ageInTicks * 20  * ((float)Math.PI / 180F);
		this.inner.zRot = ageInTicks  * 20  * ((float)Math.PI / 180F);

		this.outer.yRot = ageInTicks * -10 * ((float)Math.PI / 180F);
		this.outer.xRot = ageInTicks * -10  * ((float)Math.PI / 180F);
		this.outer.zRot = ageInTicks  * -10  * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}