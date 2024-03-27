package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.illagers.Ripper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class RipperModel<T extends Ripper> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart lower_maw;
	private final ModelPart body;
	private final ModelPart upperBody;
	private final ModelPart right_frontleg;
	private final ModelPart left_frontleg;
	private final ModelPart right_backleg;
	private final ModelPart left_backleg;
	private final ModelPart tail;

	public RipperModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.lower_maw = this.head.getChild("maw").getChild("lower_maw");
		this.body = root.getChild("body");
		this.upperBody = root.getChild("upperBody");
		this.right_frontleg = root.getChild("right_frontleg");
		this.left_frontleg = root.getChild("left_frontleg");
		this.right_backleg = root.getChild("right_backleg");
		this.left_backleg = root.getChild("left_backleg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.5F, -6.0F));

		PartDefinition maw = head.addOrReplaceChild("maw", CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -10.5156F, -12.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 10.5F, 6.0F));

		PartDefinition lower_maw = maw.addOrReplaceChild("lower_maw", CubeListBuilder.create().texOffs(14, 10).addBox(-1.5F, -0.5F, -3.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offset(-1.0F, -8.0156F, -9.0F));

		PartDefinition ears = head.addOrReplaceChild("ears", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -1.0F));

		PartDefinition head_r1 = ears.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-0.25F, -0.75F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.5F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition head_r2 = ears.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(20, 0).addBox(-0.75F, -0.75F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 15).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition upperBody = partdefinition.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -3.0F, -3.5F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.5F, -3.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition right_frontleg = partdefinition.addOrReplaceChild("right_frontleg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 13).addBox(-1.5F, -4.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 16.0F, -4.0F));

		PartDefinition left_frontleg = partdefinition.addOrReplaceChild("left_frontleg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 13).addBox(-1.5F, -4.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 16.0F, -4.0F));

		PartDefinition right_backleg = partdefinition.addOrReplaceChild("right_backleg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 22).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 16.0F, 6.0F));

		PartDefinition left_backleg = partdefinition.addOrReplaceChild("left_backleg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 22).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 16.0F, 6.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(8, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 7.0F, 0.9599F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public void prepareMobModel(T p_104132_, float p_104133_, float p_104134_, float p_104135_) {
		int b = p_104132_.getBitingTick();
		if (b > 0){
			if (b > 5) {
				this.lower_maw.xRot = Mth.sin(((float)(-4 + b) - p_104135_) / 4.0F) * (float)Math.PI * 0.4F;
			} else {
				this.lower_maw.xRot = 0.15707964F * Mth.sin((float)Math.PI * ((float)b - p_104135_) / 10.0F);
			}
		} else {
			this.lower_maw.xRot = (float)Math.PI * 0.01F;
		}
		this.tail.yRot = Mth.cos(p_104133_ * 0.6662F) * 1.4F * p_104134_;

		this.body.xRot = ((float)Math.PI / 2F);
		this.right_backleg.xRot = Mth.cos(p_104133_ * 0.6662F) * 1.4F * p_104134_;
		this.left_backleg.xRot = Mth.cos(p_104133_ * 0.6662F + (float)Math.PI) * 1.4F * p_104134_;
		this.right_frontleg.xRot = Mth.cos(p_104133_ * 0.6662F + (float)Math.PI) * 1.4F * p_104134_;
		this.left_frontleg.xRot = Mth.cos(p_104133_ * 0.6662F) * 1.4F * p_104134_;

		this.upperBody.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.08F);
		this.body.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.16F);
		this.tail.zRot = p_104132_.getBodyRollAngle(p_104135_, -0.2F);
	}

	public void setupAnim(T p_104137_, float p_104138_, float p_104139_, float p_104140_, float p_104141_, float p_104142_) {
		this.head.xRot = p_104142_ * ((float)Math.PI / 180F);
		this.head.yRot = p_104141_ * ((float)Math.PI / 180F);
		this.tail.xRot = p_104140_;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	private float r = 1.0F;
	private float g = 1.0F;
	private float b = 1.0F;

	public void setColor(float p_102420_, float p_102421_, float p_102422_) {
		this.r = p_102420_;
		this.g = p_102421_;
		this.b = p_102422_;
	}

	public void renderToBuffer(PoseStack p_102424_, VertexConsumer p_102425_, int p_102426_, int p_102427_, float p_102428_, float p_102429_, float p_102430_, float p_102431_) {
		super.renderToBuffer(p_102424_, p_102425_, p_102426_, p_102427_, this.r * p_102428_, this.g * p_102429_, this.b * p_102430_, p_102431_);
	}
}