package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.ally.Gnasher;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class GnasherModel<T extends LivingEntity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart fish;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart body;
	private final ModelPart fin;
	private final ModelPart tail;
	private final ModelPart tail_bone;
	private final ModelPart tail_fin;
	private final ModelPart left_fin;
	private final ModelPart bone;
	private final ModelPart right_fin;
	private final ModelPart bone2;

	public GnasherModel(ModelPart root) {
		this.root = root;
		this.fish = root.getChild("fish");
		this.neck = this.fish.getChild("neck");
		this.head = this.neck.getChild("head");
		this.top = this.head.getChild("top");
		this.bottom = this.head.getChild("bottom");
		this.body = this.fish.getChild("body");
		this.fin = this.body.getChild("fin");
		this.tail = this.fish.getChild("tail");
		this.tail_bone = this.tail.getChild("tail_bone");
		this.tail_fin = this.tail_bone.getChild("tail_fin");
		this.left_fin = this.fish.getChild("left_fin");
		this.bone = this.left_fin.getChild("bone");
		this.right_fin = this.fish.getChild("right_fin");
		this.bone2 = this.right_fin.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition fish = partdefinition.addOrReplaceChild("fish", CubeListBuilder.create(), PartPose.offset(-0.025F, 18.8125F, -0.8F));

		PartDefinition neck = fish.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(36, 13).addBox(-4.375F, -4.5625F, -5.5F, 9.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.1F, -0.75F, -5.7F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-0.125F, 1.0625F, -6.0F));

		PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -2.5F, -10.0F, 7.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(72, 0).addBox(-2.5F, 2.5F, -8.0F, 5.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, -1.625F, 0.75F));

		PartDefinition bottom = head.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(38, 0).addBox(-3.0F, -1.5F, -9.5F, 6.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(72, 12).addBox(-2.0F, -3.5F, -8.5F, 4.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, 2.375F, 1.0F));

		PartDefinition body = fish.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, -5.0F, -2.5F, 10.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.025F, -0.8125F, -3.7F));

		PartDefinition fin = body.addOrReplaceChild("fin", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = fin.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 35).addBox(1.0F, -12.0F, -4.0F, 0.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -3.0F, 3.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition tail = fish.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 43).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.025F, -0.8125F, 9.8F));

		PartDefinition tail_bone = tail.addOrReplaceChild("tail_bone", CubeListBuilder.create().texOffs(48, 39).addBox(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 8.0F));

		PartDefinition tail_fin = tail_bone.addOrReplaceChild("tail_fin", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.0F));

		PartDefinition body_r1 = tail_fin.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(84, 19).addBox(1.5F, -2.5F, -1.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition body_r2 = tail_fin.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(84, 22).addBox(1.5F, -2.5F, -1.0F, 0.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -2.0F, 1.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition left_fin = fish.addOrReplaceChild("left_fin", CubeListBuilder.create(), PartPose.offset(4.025F, 0.1875F, -0.2F));

		PartDefinition bone = left_fin.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(52, 29).addBox(-5.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition right_fin = fish.addOrReplaceChild("right_fin", CubeListBuilder.create(), PartPose.offset(-3.975F, 0.1875F, -0.2F));

		PartDefinition bone2 = right_fin.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(52, 29).mirror().addBox(-7.0F, -1.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	public ModelPart[] tailComponents() {
		return new ModelPart[]{this.body, this.tail, this.tail_bone, this.tail_fin};
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.neck.xRot = headPitch * (float) (Math.PI / 180F);
		this.neck.yRot = netHeadYaw * (float) (Math.PI / 180F);
		if (entity instanceof Gnasher gnasher) {
			float f = 1.0F;
			float f1 = 1.0F;
			float finWaveAnimation = gnasher.getFinAnimation(ageInTicks - gnasher.tickCount);

			this.body.yRot = -f * 0.05F * Mth.sin(f1 * 0.6F * ageInTicks);
			this.tail.yRot = -f * 0.25F * Mth.sin(f1 * 0.6F * ageInTicks);
			this.tail_fin.yRot = -f * 0.45F * Mth.sin(0.6F * ageInTicks);

			this.right_fin.zRot = (float) (-Mth.cos(finWaveAnimation) * (float) Math.PI * 0.13);
			this.left_fin.zRot = (float) (Mth.cos(finWaveAnimation) * (float) Math.PI * 0.13);

			this.top.xRot = gnasher.isAggressive() ? MathHelper.modelDegrees(-35.0F) : 0.0F;
			this.bottom.xRot = gnasher.isAggressive() ? MathHelper.modelDegrees(35.0F) : 0.0F;

			float f2 = Mth.sin(this.attackTime * (float)Math.PI);
			float f3 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);

			if (gnasher.isAggressive()){
				this.top.xRot -= -(f2 * 1.2F - f3 * 0.4F);
				this.bottom.xRot -= f2 * 1.2F - f3 * 0.4F;
			}
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}