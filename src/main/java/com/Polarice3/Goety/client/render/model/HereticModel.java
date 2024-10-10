package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.cultists.Heretic;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class HereticModel<T extends Heretic> extends HierarchicalModel<T> implements HeadedModel {
	private final ModelPart root;
	private final ModelPart heretic;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart arms;

	public HereticModel(ModelPart root) {
		this.root = root;
		this.heretic = root.getChild("heretic");
		this.head = this.heretic.getChild("head");
		this.nose = this.head.getChild("nose");
		this.body = this.heretic.getChild("body");
		this.arms = this.heretic.getChild("arms");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition heretic = partdefinition.addOrReplaceChild("heretic", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = heretic.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 24.0F, 0.2F));

		PartDefinition tip = hat.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(0, 60).addBox(-4.0F, 3.0F, 1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.51F)), PartPose.offsetAndRotation(0.0F, -37.0F, 5.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition body = heretic.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 18).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottom = robe.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(28, 44).addBox(-5.0F, 1.0F, -2.5F, 10.0F, 12.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(0, 52).addBox(-5.0F, 7.0F, 6.5F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 11.0F, -0.5F));

		PartDefinition arms = heretic.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 18).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(44, 18).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition drip = arms.addOrReplaceChild("drip", CubeListBuilder.create().texOffs(0, 20).addBox(4.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(48, 30).addBox(0.0F, -24.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 20).mirror().addBox(-8.0F, -24.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(48, 30).mirror().addBox(-4.0F, -24.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 26.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		float f = 0.01F * (float)(entity.getId() % 10);
		this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
		this.nose.yRot = 0.0F;
		this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
		if (entity.isChanting()) {
			this.nose.xRot = -0.9F;
		}
		float f4 = Math.min(limbSwingAmount / 0.3F, 1.0F);
		this.body.xRot = f4 * MathHelper.modelDegrees(7.5F);
		float f5 = entity.getCast(Minecraft.getInstance().getPartialTick());
		this.arms.xRot = (f5 * MathHelper.modelDegrees(-25.0F)) + MathHelper.modelDegrees(-45.0F);
	}

	public ModelPart getNose() {
		return this.nose;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}
}