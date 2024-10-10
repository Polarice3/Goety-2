package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class HellChantModel<T extends HellChant> extends EntityModel<T> {
	private final ModelPart scream;
	private HellChant entity;

	public HellChantModel(ModelPart root) {
		this.scream = root.getChild("scream");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("scream", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int pPackedLight, int pOverlay, float r, float g, float b, float a) {
		stack.pushPose();
		float progress = this.entity.getGrowProgress(Minecraft.getInstance().getPartialTick());
		float scale = 0.6F + progress;
		stack.scale(scale, scale, scale);
		this.scream.render(stack, consumer, pPackedLight, pOverlay, r, g, b, a - (0.25F * progress));
		stack.popPose();
	}

	@Override
	public void setupAnim(T scream, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.entity = scream;
		this.scream.xRot = (float) -Math.toRadians(Mth.lerp(Minecraft.getInstance().getPartialTick(), this.entity.xRotO, this.entity.getXRot()));
	}
}