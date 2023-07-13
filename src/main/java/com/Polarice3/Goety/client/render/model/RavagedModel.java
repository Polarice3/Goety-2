package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.Ravaged;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class RavagedModel<T extends Ravaged> extends HierarchicalModel<T> {
	private final ModelPart ghoul;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart body;

	public RavagedModel(ModelPart root) {
		this.ghoul = root.getChild("ghoul");
		this.head = this.ghoul.getChild("head");
		this.mouth = this.head.getChild("mouth");
		this.rightArm = this.ghoul.getChild("rightArm");
		this.leftArm = this.ghoul.getChild("leftArm");
		this.rightLeg = this.ghoul.getChild("rightLeg");
		this.leftLeg = this.ghoul.getChild("leftLeg");
		this.body = this.ghoul.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition ghoul = partdefinition.addOrReplaceChild("ghoul", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = ghoul.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -8.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -6.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -9.0F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 1.0F, -2.0F));

		PartDefinition body = ghoul.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 17).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 35).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -9.0F, 18.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition rightArm = ghoul.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(28, 35).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(44, 19).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, -4.0F));

		PartDefinition leftArm = ghoul.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(44, 35).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(44, 19).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -10.0F, -4.0F));

		PartDefinition rightLeg = ghoul.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -12.0F, 6.0F));

		PartDefinition leftLeg = ghoul.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, -12.0F, 6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.body.yRot = 0.0F;
		float f = 0.4F * limbSwingAmount;
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * f;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
		this.rightArm.xRot = -MathHelper.modelDegrees(15) + Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
		this.leftArm.xRot = -MathHelper.modelDegrees(15) + Mth.cos(limbSwing * 0.6662F) * f;
		this.rightArm.yRot = 0.0F;
		this.leftArm.yRot = 0.0F;
		this.rightArm.zRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.setupAttackAnimation(entity);
	}

	public void prepareMobModel(T pEntity, float limbSwing, float limbSwingAmount, float pPartialTick) {
		super.prepareMobModel(pEntity, limbSwing, limbSwingAmount, pPartialTick);
		int b = pEntity.getBitingTick();
		if (b > 0){
			if (b > 5) {
				this.mouth.xRot = Mth.sin(((float)(-4 + b) - pPartialTick) / 4.0F) * (float)Math.PI * 0.4F;
			} else {
				this.mouth.xRot = 0.15707964F * Mth.sin((float)Math.PI * ((float)b - pPartialTick) / 10.0F);
			}
		} else {
			this.mouth.xRot = (float)Math.PI * 0.01F;
		}
	}

	protected void setupAttackAnimation(T pEntity) {
		if (!(this.attackTime <= 0.0F)) {
			HumanoidArm handside = this.getAttackArm(pEntity);
			ModelPart modelrenderer = this.getArm(handside);
			float f = this.attackTime;
			this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
			if (handside == HumanoidArm.LEFT) {
				this.body.yRot *= -1.0F;
			}

			this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
			this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
			this.rightArm.yRot += this.body.yRot;
			this.leftArm.yRot += this.body.yRot;
			this.leftArm.xRot += this.body.yRot;
			f = 1.0F - this.attackTime;
			f = f * f;
			f = f * f;
			f = 1.0F - f;
			float f1 = Mth.sin(f * (float)Math.PI);
			float f2 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
			modelrenderer.xRot = (float)((double)modelrenderer.xRot - ((double)f1 * 1.2D + (double)f2));
			modelrenderer.yRot += this.body.yRot * 2.0F;
			modelrenderer.zRot += Mth.sin(this.attackTime * (float)Math.PI) * -0.4F;
		}
	}

	protected HumanoidArm getAttackArm(T pEntity) {
		HumanoidArm handside = pEntity.getMainArm();
		return pEntity.swingingArm == InteractionHand.MAIN_HAND ? handside : handside.getOpposite();
	}

	protected ModelPart getArm(HumanoidArm pSide) {
		return pSide == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		ghoul.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return ghoul;
	}
}