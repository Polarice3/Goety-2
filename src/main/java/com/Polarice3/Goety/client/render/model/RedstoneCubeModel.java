package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.RedstoneCubeAnimations;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneCube;
import com.Polarice3.Goety.utils.ModModelUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RedstoneCubeModel<T extends RedstoneCube> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart bone;

	public RedstoneCubeModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.idleAnimationState, RedstoneCubeAnimations.IDLE, ageInTicks);
		if (entity.canAnimateMove()) {
			ModModelUtils.animateWalk(this, RedstoneCubeAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
		this.animate(entity.attackAnimationState, RedstoneCubeAnimations.ATTACK, ageInTicks);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}