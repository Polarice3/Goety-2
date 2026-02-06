package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.api.entities.IHeretic;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HereticModel<T extends LivingEntity & IHeretic> extends HierarchicalModel<T> implements HeadedModel {
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

		PartDefinition heretic = partdefinition.addOrReplaceChild("heretic", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition head = heretic.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-3.5F, -10.5F, -5.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 1.0F));

		PartDefinition hat1 = hat.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(59, 2).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -10.5F, 3.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition body = heretic.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.15F))
				.texOffs(0, 38).addBox(-4.5F, -12.5F, -3.5F, 9.0F, 10.0F, 7.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(-3, 84).addBox(-6.5F, 12.9F, -5.5F, 13.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 55).addBox(-4.5F, 0.0F, -3.5F, 9.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 108).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(72, 63).addBox(-4.5F, -0.2F, -3.5F, 9.0F, 14.0F, 7.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body1_r1 = body1.addOrReplaceChild("body1_r1", CubeListBuilder.create().texOffs(72, 61).mirror().addBox(-2.5F, -13.25F, -3.5F, 7.0F, 14.0F, 9.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(1.0F, 14.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition body6 = body1.addOrReplaceChild("body6", CubeListBuilder.create().texOffs(100, 110).addBox(-4.5F, -12.0F, -5.5F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 12.0F, 7.5F, 0.3491F, 0.0F, 0.0F));

		PartDefinition tome = body.addOrReplaceChild("tome", CubeListBuilder.create().texOffs(68, 23).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(86, 23).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(68, 39).addBox(-1.0F, 1.0F, -2.0F, 2.0F, 8.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(5.5F, -2.0F, 0.0F, 0.0F, -0.0436F, -0.0873F));

		PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(96, 1).addBox(-0.75F, 0.25F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -3.0F, 3.25F, 0.1309F, 0.0F, 0.0F));

		PartDefinition body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(100, 1).addBox(-0.75F, 0.25F, 0.0F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition body4 = body.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(92, 1).addBox(-0.25F, 0.25F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -3.0F, 3.25F, 0.1309F, 0.0F, 0.0F));

		PartDefinition body5 = body4.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(104, 1).mirror().addBox(-0.25F, 0.25F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition arms = heretic.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(40, 34).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(32, 65).addBox(-4.5F, 2.0F, -2.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(51, 65).mirror().addBox(0.5F, 2.0F, -2.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.25F)).mirror(false)
				.texOffs(32, 42).mirror().addBox(-9.0F, -2.0F, -2.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)).mirror(false)
				.texOffs(32, 42).addBox(3.0F, -2.0F, -2.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.3F))
				.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, -3.0F, -0.7418F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
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