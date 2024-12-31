package com.Polarice3.Goety.client.render.model;


import com.Polarice3.Goety.common.entities.util.TridentStorm;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class TridentStormModel<T extends TridentStorm> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart circle;
	private final ModelPart trident;

	public TridentStormModel(ModelPart root) {
		this.root = root;
		this.circle = this.root.getChild("circle");
		this.trident = this.root.getChild("trident");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition circle = partdefinition.addOrReplaceChild("circle", CubeListBuilder.create().texOffs(-64, 0).addBox(-32.0F, -2.0F, -32.0F, 64.0F, 0.0F, 64.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition trident = partdefinition.addOrReplaceChild("trident", CubeListBuilder.create().texOffs(0, 64).addBox(-0.5F, -28.0F, -0.5F, 1.0F, 31.0F, 1.0F, new CubeDeformation(0.01F))
				.texOffs(4, 64).addBox(-1.5F, -24.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 67).addBox(-2.5F, -27.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 67).addBox(1.5F, -27.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -214.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.mainAnimationState, SHOOT, ageInTicks);
		this.circle.visible = !entity.isActivated();
		this.trident.visible = entity.isActivated();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static final AnimationDefinition SHOOT = AnimationDefinition.Builder.withLength(0.25F)
			.addAnimation("trident", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -216.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}