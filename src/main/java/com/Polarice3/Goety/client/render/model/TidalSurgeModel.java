package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.AbstractWave;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.joml.Vector3f;

public class TidalSurgeModel<T extends AbstractWave> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart wave;
	private final ModelPart top;

	public TidalSurgeModel(ModelPart root) {
		this.root = root;
		this.wave = root.getChild("wave");
		this.top = this.wave.getChild("top");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition wave = partdefinition.addOrReplaceChild("wave", CubeListBuilder.create().texOffs(0, 25).addBox(-13.0F, -5.7F, 1.0F, 26.0F, 6.0F, 12.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition top = wave.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -8.0F, 0.0F, 26.0F, 8.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 1.0F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		float f = (float) ((Math.sin(ageInTicks * 0.1F) + 1.0F) * 0.2F);
		float waveScale = entity.getWaveScale();
		float stretch = Math.min(40.0F, ageInTicks) / 40.0F;
		float slam = entity.getSlamAmount(ageInTicks - entity.activeWaveTicks) * 1.4F;
		this.slamPos(this.top, slam, 0.0F, -1.0F, 7.5F);
		this.top.xRot += f;
		this.top.y += f * 8.0F;
		this.top.z += f * 2.0F;
		this.scale(this.wave, waveScale, waveScale, waveScale + stretch);
		this.scale(this.top, waveScale, waveScale, waveScale + stretch);
		this.top.y += stretch * 2.0F;
		this.top.z += stretch * 2.0F;
	}

	public void scale(ModelPart modelPart, float x, float y, float z){
		modelPart.xScale = x;
		modelPart.yScale = y;
		modelPart.zScale = z;
	}

	public void slamRot(ModelPart model, float slam, float xRot, float yRot, float zRot) {
		Vector3f vector3f = new Vector3f(slam * (xRot), slam * (yRot), slam * (zRot));
		model.offsetRotation(vector3f);
	}

	public void slamPos(ModelPart model, float slam, float x, float y, float z) {
		Vector3f vector3f = new Vector3f(slam * x, slam * y, slam * z);
		model.offsetPos(vector3f);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}