package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.QuickGrowingVineAnimations;
import com.Polarice3.Goety.common.entities.neutral.QuickGrowingVine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class QuickGrowingVineModel<T extends QuickGrowingVine> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart vine;

	public QuickGrowingVineModel(ModelPart root) {
		this.root = root;
		this.vine = root.getChild("vine");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition vine = partdefinition.addOrReplaceChild("vine", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = vine.addOrReplaceChild("base", CubeListBuilder.create().texOffs(96, 49).addBox(-8.0F, -24.0F, -5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(64, 33).addBox(-5.0F, -24.0F, -8.0F, 0.0F, 24.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(32, 49).addBox(-8.0F, -24.0F, 5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 33).mirror().addBox(5.0F, -24.0F, -8.0F, 0.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(-8, 83).addBox(-5.0F, -24.1F, -5.0F, 10.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition inner = base.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 96).addBox(-4.0F, -24.0F, -4.0F, 8.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition middle = base.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(100, 25).addBox(-5.0F, -24.0F, -4.0F, 10.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(67, 15).addBox(-4.0F, -24.0F, -5.0F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(34, 25).addBox(-5.0F, -24.0F, 4.0F, 10.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(3, 15).addBox(4.0F, -24.0F, -5.0F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(-5, 84).addBox(-4.0F, -22.1F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition inner2 = middle.addOrReplaceChild("inner2", CubeListBuilder.create().texOffs(32, 100).addBox(-2.0F, -22.0F, -4.0F, 6.0F, 22.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 1.0F));

		PartDefinition top = middle.addOrReplaceChild("top", CubeListBuilder.create().texOffs(101, 1).addBox(-4.0F, -22.0F, -3.0F, 8.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(68, -7).addBox(-3.0F, -23.0F, -4.0F, 0.0F, 24.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(35, 1).addBox(-4.0F, -22.0F, 3.0F, 8.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(3, -7).addBox(3.0F, -23.0F, -4.0F, 0.0F, 24.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(-2, 85).addBox(-3.0F, -20.1F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition inner3 = top.addOrReplaceChild("inner3", CubeListBuilder.create().texOffs(56, 102).addBox(0.0F, -44.0F, -4.0F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 24.0F, 2.0F));

		PartDefinition leaf6 = top.addOrReplaceChild("leaf6", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -4.0F, -2.0F, 0.2929F, 0.3542F, -1.1793F));

		PartDefinition bone11 = leaf6.addOrReplaceChild("bone11", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bone11.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone12 = leaf6.addOrReplaceChild("bone12", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r2 = bone12.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf7 = top.addOrReplaceChild("leaf7", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -12.0F, -2.0F, 0.7731F, -0.0302F, 1.3656F));

		PartDefinition bone13 = leaf7.addOrReplaceChild("bone13", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = bone13.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone14 = leaf7.addOrReplaceChild("bone14", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r4 = bone14.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf8 = top.addOrReplaceChild("leaf8", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -17.0F, 3.0F, -0.9918F, -0.1356F, -0.8827F));

		PartDefinition bone15 = leaf8.addOrReplaceChild("bone15", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r5 = bone15.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone16 = leaf8.addOrReplaceChild("bone16", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r6 = bone16.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition top_leaves = top.addOrReplaceChild("top_leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition top_r1 = top_leaves.addOrReplaceChild("top_r1", CubeListBuilder.create().texOffs(72, 113).mirror().addBox(0.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, -28.0F, 2.0F, 1.1345F, 0.0F, -0.6109F));

		PartDefinition top_r2 = top_leaves.addOrReplaceChild("top_r2", CubeListBuilder.create().texOffs(72, 113).mirror().addBox(0.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, 0.8727F, 0.0F, -0.6109F));

		PartDefinition top_r3 = top_leaves.addOrReplaceChild("top_r3", CubeListBuilder.create().texOffs(72, 113).addBox(-5.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, -0.8727F, 0.0F, 0.6109F));

		PartDefinition top_r4 = top_leaves.addOrReplaceChild("top_r4", CubeListBuilder.create().texOffs(72, 113).addBox(-5.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, -1.5708F, -0.7854F, 0.7854F));

		PartDefinition top_r5 = top_leaves.addOrReplaceChild("top_r5", CubeListBuilder.create().texOffs(72, 113).addBox(-5.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -28.0F, 0.0F, -1.5708F, -0.5236F, 0.7854F));

		PartDefinition top_r6 = top_leaves.addOrReplaceChild("top_r6", CubeListBuilder.create().texOffs(72, 113).mirror().addBox(0.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, -1.5708F, 0.7854F, -0.7854F));

		PartDefinition top_r7 = top_leaves.addOrReplaceChild("top_r7", CubeListBuilder.create().texOffs(72, 113).addBox(-5.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, 0.8727F, 0.0F, 0.6109F));

		PartDefinition top_r8 = top_leaves.addOrReplaceChild("top_r8", CubeListBuilder.create().texOffs(72, 113).mirror().addBox(0.0F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, -0.8727F, 0.0F, -0.6109F));

		PartDefinition leaf5 = middle.addOrReplaceChild("leaf5", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -13.0F, 5.0F, -0.7974F, 0.4549F, 0.3625F));

		PartDefinition bone9 = leaf5.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = bone9.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone10 = leaf5.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r8 = bone10.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf2 = base.addOrReplaceChild("leaf2", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.75F, -18.0F, -4.0F, 0.9239F, 0.4703F, -0.3298F));

		PartDefinition bone3 = leaf2.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r9 = bone3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone4 = leaf2.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r10 = bone4.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf3 = base.addOrReplaceChild("leaf3", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0F, -25.0F, -5.0F, 0.7885F, -0.5194F, 0.9031F));

		PartDefinition bone5 = leaf3.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r11 = bone5.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone6 = leaf3.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r12 = bone6.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf4 = base.addOrReplaceChild("leaf4", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -13.0F, 6.0F, -0.7854F, 0.0F, 1.0036F));

		PartDefinition bone7 = leaf4.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r13 = bone7.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone8 = leaf4.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r14 = bone8.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(72, 118).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.idleAnimationState, QuickGrowingVineAnimations.IDLE, ageInTicks);
		this.animate(entity.burstAnimationState, QuickGrowingVineAnimations.BURST, ageInTicks);
		this.animate(entity.burrowAnimationState, QuickGrowingVineAnimations.BURROW, ageInTicks);
		this.animate(entity.holdAnimationState, QuickGrowingVineAnimations.HOLD, ageInTicks);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.vine.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}