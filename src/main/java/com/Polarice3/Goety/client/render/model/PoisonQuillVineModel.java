package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.PoisonQuillAnimations;
import com.Polarice3.Goety.common.entities.neutral.PoisonQuillVine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class PoisonQuillVineModel<T extends PoisonQuillVine> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart vine;

	public PoisonQuillVineModel(ModelPart root) {
		this.root = root;
		this.vine = root.getChild("vine");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition vine = partdefinition.addOrReplaceChild("vine", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bottom = vine.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 59).addBox(-8.0F, -24.0F, -5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(64, 59).mirror().addBox(-8.0F, -24.0F, 5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(-8, 83).addBox(-5.0F, -24.0F, -5.0F, 10.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bottom.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(96, 59).addBox(-8.0F, -24.0F, 5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 59).addBox(-8.0F, -24.0F, -5.0F, 16.0F, 24.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition inner = bottom.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 96).addBox(-4.0F, -24.0F, -4.0F, 8.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition middle = bottom.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(100, 25).addBox(-5.0F, -24.0F, -4.0F, 10.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(67, 15).addBox(-4.0F, -24.0F, -5.0F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(34, 25).addBox(-5.0F, -24.0F, 4.0F, 10.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(3, 15).addBox(4.0F, -24.0F, -5.0F, 0.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(-5, 84).addBox(-4.0F, -22.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition inner2 = middle.addOrReplaceChild("inner2", CubeListBuilder.create().texOffs(32, 100).addBox(-2.0F, -22.0F, -4.0F, 6.0F, 22.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 1.0F));

		PartDefinition top = middle.addOrReplaceChild("top", CubeListBuilder.create().texOffs(5, 1).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(101, -7).addBox(-3.0F, -25.0F, -4.0F, 0.0F, 24.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(69, 1).mirror().addBox(-4.0F, -24.0F, 3.0F, 8.0F, 24.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(37, -7).mirror().addBox(3.0F, -25.0F, -4.0F, 0.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(-2, 85).addBox(-3.0F, -22.0F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, 0.0F));

		PartDefinition inner3 = top.addOrReplaceChild("inner3", CubeListBuilder.create().texOffs(56, 98).addBox(0.0F, -48.0F, -4.0F, 4.0F, 26.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 22.0F, 2.0F));

		PartDefinition head = top.addOrReplaceChild("head", CubeListBuilder.create().texOffs(42, 98).addBox(-15.0F, 0.0F, -15.0F, 30.0F, 0.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -26.5F, 0.0F));

		PartDefinition quill = head.addOrReplaceChild("quill", CubeListBuilder.create().texOffs(132, 116).addBox(-1.0F, -82.0F, -2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 72.5F, 1.0F));

		PartDefinition cube_r2 = quill.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(140, 116).mirror().addBox(-2.0F, -84.0F, -1.0F, 5.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r3 = quill.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(140, 116).addBox(-3.0F, -84.0F, -1.0F, 5.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition petals = head.addOrReplaceChild("petals", CubeListBuilder.create(), PartPose.offset(0.0F, 72.5F, 0.0F));

		PartDefinition north_petal = petals.addOrReplaceChild("north_petal", CubeListBuilder.create(), PartPose.offset(0.5F, -73.0F, -7.5F));

		PartDefinition cube_r4 = north_petal.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(109, 89).mirror().addBox(-8.5F, -1.0F, 3.5F, 17.0F, 0.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition west_petal = petals.addOrReplaceChild("west_petal", CubeListBuilder.create(), PartPose.offset(7.5F, -73.0F, -0.5F));

		PartDefinition cube_r5 = west_petal.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(109, 89).mirror().addBox(-7.5F, -1.0F, 1.5F, 17.0F, 0.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition south_petal = petals.addOrReplaceChild("south_petal", CubeListBuilder.create().texOffs(109, 89).mirror().addBox(-8.5F, 0.0F, -0.5F, 17.0F, 0.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, -73.0F, 7.5F));

		PartDefinition east_petal = petals.addOrReplaceChild("east_petal", CubeListBuilder.create(), PartPose.offset(-7.5F, -73.0F, -0.5F));

		PartDefinition cube_r6 = east_petal.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(109, 89).mirror().addBox(-7.5F, -1.0F, 3.5F, 17.0F, 0.0F, 23.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 1.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaf6 = top.addOrReplaceChild("leaf6", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.75F, -17.0F, 2.0F, -2.2327F, -0.0707F, 2.2608F));

		PartDefinition bone10 = leaf6.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = bone10.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone11 = leaf6.addOrReplaceChild("bone11", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r8 = bone11.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf4 = middle.addOrReplaceChild("leaf4", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.75F, -19.0F, -3.0F, 0.3611F, 0.2058F, -1.3771F));

		PartDefinition bone6 = leaf4.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r9 = bone6.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone7 = leaf4.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r10 = bone7.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf5 = middle.addOrReplaceChild("leaf5", CubeListBuilder.create(), PartPose.offsetAndRotation(3.25F, -16.0F, 5.0F, -0.6365F, 0.1274F, 1.3836F));

		PartDefinition bone8 = leaf5.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r11 = bone8.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone9 = leaf5.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r12 = bone9.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf2 = bottom.addOrReplaceChild("leaf2", CubeListBuilder.create(), PartPose.offsetAndRotation(5.25F, -9.0F, -5.0F, 0.8346F, -0.8721F, 0.5435F));

		PartDefinition bone3 = leaf2.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r13 = bone3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone4 = leaf2.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r14 = bone4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf3 = bottom.addOrReplaceChild("leaf3", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.75F, -21.0F, 5.0F, -0.8784F, -0.3118F, -0.8562F));

		PartDefinition bone2 = leaf3.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r15 = bone2.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition bone5 = leaf3.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(1.75F, 0.0F, -0.25F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r16 = bone5.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(150, 124).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.vine.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.animate(entity.idleAnimationState, PoisonQuillAnimations.IDLE, ageInTicks);
		this.animate(entity.shootAnimationState, PoisonQuillAnimations.SHOOT, ageInTicks);
		this.animate(entity.burstAnimationState, PoisonQuillAnimations.BURST, ageInTicks);
		this.animate(entity.burrowAnimationState, PoisonQuillAnimations.BURROW, ageInTicks);
		this.animate(entity.holdAnimationState, PoisonQuillAnimations.HOLD, ageInTicks);
		this.animate(entity.openAnimationState, PoisonQuillAnimations.OPEN, ageInTicks);
		this.animate(entity.closeAnimationState, PoisonQuillAnimations.CLOSE, ageInTicks);
		this.animate(entity.targetAnimationState, PoisonQuillAnimations.TARGET, ageInTicks);
		this.animate(entity.docileAnimationState, PoisonQuillAnimations.DOCILE, ageInTicks);
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