package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.EntangleVinesAnimations;
import com.Polarice3.Goety.common.entities.projectiles.EntangleVines;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class EntangleVinesModel<T extends EntangleVines> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart vine;
	private final ModelPart vine2;
	private final ModelPart vine3;
	private final ModelPart vine4;

	public EntangleVinesModel(ModelPart root) {
		this.root = root;
		this.vine = root.getChild("vine");
		this.vine2 = root.getChild("vine2");
		this.vine3 = root.getChild("vine3");
		this.vine4 = root.getChild("vine4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition vine = partdefinition.addOrReplaceChild("vine", CubeListBuilder.create().texOffs(-1, 15).addBox(-16.0F, 0.0F, -16.0F, 32.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(-15.0F, 0.0F, -15.0F, 30.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 13).addBox(-14.0F, 0.0F, -14.0F, 28.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 12).addBox(-13.0F, 0.0F, -13.0F, 26.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 11).addBox(-12.0F, 0.0F, -12.0F, 24.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 10).addBox(-11.0F, 0.0F, -11.0F, 22.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 9).addBox(-10.0F, 0.0F, -10.0F, 20.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 8).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 7).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 6).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 5).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 4).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(11, 3).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 2).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(13, 1).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition vine2 = partdefinition.addOrReplaceChild("vine2", CubeListBuilder.create().texOffs(-1, 16).addBox(-16.0F, 0.0F, -16.0F, 32.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 17).addBox(-15.0F, 0.0F, -15.0F, 30.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 18).addBox(-14.0F, 0.0F, -14.0F, 28.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 19).addBox(-13.0F, 0.0F, -13.0F, 26.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 20).addBox(-12.0F, 0.0F, -12.0F, 24.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 21).addBox(-11.0F, 0.0F, -11.0F, 22.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 22).addBox(-10.0F, 0.0F, -10.0F, 20.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 23).addBox(-9.0F, 0.0F, -9.0F, 18.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(7, 24).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 25).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 26).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 27).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(11, 28).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 29).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(13, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.5236F, 3.1416F, 0.0F));

		PartDefinition vine3 = partdefinition.addOrReplaceChild("vine3", CubeListBuilder.create().texOffs(-32, 32).addBox(-17.0F, 0.0F, -16.0F, 1.0F, 0.0F, 32.0F, new CubeDeformation(0.0F))
		.texOffs(-28, 33).addBox(-16.0F, 0.0F, -15.0F, 1.0F, 0.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(-24, 34).addBox(-15.0F, 0.0F, -14.0F, 1.0F, 0.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(-20, 35).addBox(-14.0F, 0.0F, -13.0F, 1.0F, 0.0F, 26.0F, new CubeDeformation(0.0F))
		.texOffs(-16, 36).addBox(-13.0F, 0.0F, -12.0F, 1.0F, 0.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(-12, 37).addBox(-12.0F, 0.0F, -11.0F, 1.0F, 0.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(-8, 38).addBox(-11.0F, 0.0F, -10.0F, 1.0F, 0.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(-4, 39).addBox(-10.0F, 0.0F, -9.0F, 1.0F, 0.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(0, 40).addBox(-9.0F, 0.0F, -8.0F, 1.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(4, 41).addBox(-8.0F, 0.0F, -7.0F, 1.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(8, 42).addBox(-7.0F, 0.0F, -6.0F, 1.0F, 0.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(12, 43).addBox(-6.0F, 0.0F, -5.0F, 1.0F, 0.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(16, 44).addBox(-5.0F, 0.0F, -4.0F, 1.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(20, 45).addBox(-4.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(24, 46).addBox(-3.0F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 47).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition vine4 = partdefinition.addOrReplaceChild("vine4", CubeListBuilder.create().texOffs(30, 33).addBox(-16.0F, 0.0F, -15.0F, 1.0F, 0.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(30, 32).addBox(-17.0F, 0.0F, -16.0F, 1.0F, 0.0F, 32.0F, new CubeDeformation(0.0F))
		.texOffs(30, 34).addBox(-15.0F, 0.0F, -14.0F, 1.0F, 0.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(30, 35).addBox(-14.0F, 0.0F, -13.0F, 1.0F, 0.0F, 26.0F, new CubeDeformation(0.0F))
		.texOffs(30, 36).addBox(-13.0F, 0.0F, -12.0F, 1.0F, 0.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(30, 37).addBox(-12.0F, 0.0F, -11.0F, 1.0F, 0.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(30, 38).addBox(-11.0F, 0.0F, -10.0F, 1.0F, 0.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(30, 39).addBox(-10.0F, 0.0F, -9.0F, 1.0F, 0.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(30, 40).addBox(-9.0F, 0.0F, -8.0F, 1.0F, 0.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(30, 41).addBox(-8.0F, 0.0F, -7.0F, 1.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(30, 42).addBox(-7.0F, 0.0F, -6.0F, 1.0F, 0.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(30, 43).addBox(-6.0F, 0.0F, -5.0F, 1.0F, 0.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(30, 44).addBox(-5.0F, 0.0F, -4.0F, 1.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(30, 45).addBox(-4.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(30, 46).addBox(-3.0F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(30, 47).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 3.1416F, 0.5236F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.hiddenAnimationState, EntangleVinesAnimations.HIDDEN, ageInTicks);
		this.animate(entity.holdAnimationState, EntangleVinesAnimations.HOLD, ageInTicks);
		this.animate(entity.burstAnimationState, EntangleVinesAnimations.BURST, ageInTicks);
		this.animate(entity.burrowAnimationState, EntangleVinesAnimations.BURROW, ageInTicks);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		vine.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		vine2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		vine3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		vine4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}