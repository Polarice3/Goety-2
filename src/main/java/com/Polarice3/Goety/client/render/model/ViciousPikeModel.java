package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.MinisterAnimations;
import com.Polarice3.Goety.common.entities.projectiles.ViciousPike;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ViciousPikeModel<T extends ViciousPike> extends HierarchicalModel<T> {
	private final ModelPart fakeBase;
	private final ModelPart base;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;

	public ViciousPikeModel(ModelPart root) {
		this.fakeBase = root.getChild("fakeBase");
		this.base = this.fakeBase.getChild("base");
		this.bone = this.base.getChild("bone");
		this.bone2 = this.bone.getChild("bone2");
		this.bone3 = this.bone2.getChild("bone3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fakeBase = partdefinition.addOrReplaceChild("fakeBase", CubeListBuilder.create().texOffs(-2, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = fakeBase.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone = base.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(32, 22).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(28, 38).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.mainAnimationState, MinisterAnimations.SPIKE, ageInTicks);
	}

	private void resetArmPoses() {
		this.fakeBase.y = 24.0F;
		this.base.y = 0.0F;
		this.bone.y = -12.0F;
		this.bone2.y = -10.0F;
		this.bone3.y = -10.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		fakeBase.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.fakeBase;
	}
}