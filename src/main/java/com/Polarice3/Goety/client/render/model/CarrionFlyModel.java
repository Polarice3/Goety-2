package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.CarrionFly;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CarrionFlyModel<T extends CarrionFly> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart fly;
    private final ModelPart rightWing;
	private final ModelPart leftWing;

    public CarrionFlyModel(ModelPart root) {
		this.root = root;
		this.fly = root.getChild("fly");
        ModelPart body = this.fly.getChild("body");
		this.rightWing = body.getChild("rightWing");
		this.leftWing = body.getChild("leftWing");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fly = partdefinition.addOrReplaceChild("fly", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition head = fly.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 5).addBox(-0.5F, 1.0F, -5.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -4.0F, -2.5F));

		PartDefinition body = fly.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -0.5F));

		PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 1.0F));

		PartDefinition back_r1 = legs.addOrReplaceChild("back_r1", CubeListBuilder.create().texOffs(4, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition mid_r1 = legs.addOrReplaceChild("mid_r1", CubeListBuilder.create().texOffs(4, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition front_r1 = legs.addOrReplaceChild("front_r1", CubeListBuilder.create().texOffs(4, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(-6, 20).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, -0.5F, 0.2618F, -0.2618F, 0.0F));

		PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(-6, 26).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, -0.5F, 0.2618F, 0.2618F, 0.0F));

		PartDefinition tail = fly.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 2).addBox(-2.5F, -2.0F, -0.25F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -4.0F, 1.5F, -0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.rightWing.xRot = 0.0F;
		boolean flag = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7D;
		if (flag) {
			this.rightWing.yRot = -0.2618F;
			this.rightWing.zRot = 0.0F;
			this.leftWing.xRot = 0.0F;
			this.leftWing.yRot = 0.2618F;
			this.leftWing.zRot = 0.0F;
		} else {
			float f = ageInTicks * 120.32113F * ((float)Math.PI / 180F);
			this.rightWing.yRot = 0.0F;
			this.rightWing.zRot = Mth.cos(f) * (float)Math.PI * 0.15F;
			this.leftWing.xRot = this.rightWing.xRot;
			this.leftWing.yRot = this.rightWing.yRot;
			this.leftWing.zRot = -this.rightWing.zRot;
		}
		float f = ageInTicks * 0.0025F;
		this.fly.y = Mth.sin(f * 40.0F) + 22.0F;

		this.fly.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.fly.xRot = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}