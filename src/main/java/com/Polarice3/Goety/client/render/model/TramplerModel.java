package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.illagers.Trampler;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class TramplerModel<T extends Trampler> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart trampler;
	protected final ModelPart body;
	protected final ModelPart headParts;
	protected final ModelPart mouth;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public TramplerModel(ModelPart root) {
		this.root = root;
		this.trampler = root.getChild("trampler");
		this.body = this.trampler.getChild("body");
		this.headParts = this.body.getChild("neck");
		this.mouth = this.headParts.getChild("head").getChild("mouth");
		this.rightFrontLeg = this.body.getChild("right_front_leg");
		this.leftFrontLeg = this.body.getChild("left_front_leg");
		this.rightHindLeg = this.trampler.getChild("right_back_leg");
		this.leftHindLeg = this.trampler.getChild("left_back_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition trampler = partdefinition.addOrReplaceChild("trampler", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = trampler.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -19.0F, 8.0F, 10.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 9.0F));

		PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 1.0F, -5.5F, 8.0F, 7.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -9.0F, -7.0F));

		PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(48, 8).mirror().addBox(-0.5F, 10.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(48, 0).mirror().addBox(-1.0F, 16.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, -7.0F, -18.0F));

		PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 8).mirror().addBox(-2.5F, 10.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(48, 0).mirror().addBox(-3.0F, 16.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, -7.0F, -18.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -17.0F));

		PartDefinition Neck_r1 = neck.addOrReplaceChild("Neck_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.6506F, -2.7679F, 0.7854F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -7.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(0, 14).addBox(-1.0F, 0.5F, -10.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -8.0F, -4.0F));

		PartDefinition Head_r1 = head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(32, 50).addBox(-1.0F, -8.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.5F))
				.texOffs(32, 50).addBox(10.0F, -8.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-5.0F, -3.0F, -4.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(26, 41).addBox(-5.0F, 1.0F, -8.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(1.0F, 1.0F, 0.0F));

		PartDefinition left_back_leg = trampler.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(76, 14).mirror().addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(60, 22).mirror().addBox(-0.5F, 10.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(60, 14).mirror().addBox(-1.0F, 16.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, -20.0F, 9.0F));

		PartDefinition right_back_leg = trampler.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(76, 14).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(60, 22).mirror().addBox(-2.5F, 10.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(60, 14).mirror().addBox(-3.0F, 16.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, -20.0F, 9.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

	public Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.headParts);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	public void prepareMobModel(T p_102780_, float p_102781_, float p_102782_, float p_102783_) {
		super.prepareMobModel(p_102780_, p_102781_, p_102782_, p_102783_);
		float f = Mth.rotLerp(p_102783_, p_102780_.yBodyRotO, p_102780_.yBodyRot);
		float f1 = Mth.rotLerp(p_102783_, p_102780_.yHeadRotO, p_102780_.yHeadRot);
		float f2 = Mth.lerp(p_102783_, p_102780_.xRotO, p_102780_.getXRot());
		float f3 = f1 - f;
		float f4 = f2 * ((float)Math.PI / 180F);
		if (f3 > 20.0F) {
			f3 = 20.0F;
		}

		if (f3 < -20.0F) {
			f3 = -20.0F;
		}

		if (p_102782_ > 0.2F) {
			f4 += Mth.cos(p_102781_ * 0.4F) * 0.15F * p_102782_;
		}

		float f5 = 0.0F;
		float f6 = p_102780_.getStandingAnimationScale(p_102783_);
		float f7 = 1.0F - f6;
		float f8 = p_102780_.getMouthAnim(p_102783_);
		float f9 = (float)p_102780_.tickCount + p_102783_;
		this.body.xRot = 0.0F;
		this.headParts.xRot = f4;
		this.headParts.yRot = f3 * ((float)Math.PI / 180F);
		float f10 = p_102780_.isInWater() ? 0.2F : 1.0F;
		float f11 = Mth.cos(f10 * p_102781_ * 0.6662F + (float)Math.PI);
		float f12 = f11 * 0.8F * p_102782_;
		float f13 = (1.0F - Math.max(f6, f5)) * (f4 + f8 * Mth.sin(f9) * 0.05F);
		this.headParts.xRot = f6 * (0.2617994F + f4) + f5 * (2.1816616F + Mth.sin(f9) * 0.05F) + f13;
		this.mouth.xRot = f8 * 0.5F;
		this.headParts.yRot = f6 * f3 * ((float)Math.PI / 180F) + (1.0F - Math.max(f6, f5)) * this.headParts.yRot;
		this.body.xRot = f6 * (-(float)Math.PI / 4F) + f7 * this.body.xRot;
		float f14 = 0.2617994F * f6;
		float f15 = Mth.cos(f9 * 0.6F + (float)Math.PI);
		float f16 = ((-(float)Math.PI / 3F) + f15) * f6 + f12 * f7;
		float f17 = ((-(float)Math.PI / 3F) - f15) * f6 - f12 * f7;
		this.leftHindLeg.xRot = f14 - f11 * 0.5F * p_102782_ * f7;
		this.rightHindLeg.xRot = f14 + f11 * 0.5F * p_102782_ * f7;
		this.leftFrontLeg.xRot = f16;
		this.rightFrontLeg.xRot = f17;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}