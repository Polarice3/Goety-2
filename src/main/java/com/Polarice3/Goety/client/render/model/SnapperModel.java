package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class SnapperModel<T extends Mob> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart snapper;
    private final ModelPart bottom;
    private final ModelPart right_fin;
    private final ModelPart left_fin;
    private final ModelPart tail;
    private final ModelPart tail_fin;

    public SnapperModel(ModelPart root) {
        this.root = root;
        this.snapper = root.getChild("snapper");
        ModelPart jaw = this.snapper.getChild("jaw");
        this.bottom = jaw.getChild("bottom");
        this.right_fin = this.snapper.getChild("right_fin");
        this.left_fin = this.snapper.getChild("left_fin");
        this.tail = this.snapper.getChild("tail");
        this.tail_fin = this.tail.getChild("tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition snapper = partdefinition.addOrReplaceChild("snapper", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = snapper.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 11).addBox(-6.5F, -8.0F, -1.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, 0.0F, -2.25F));

        PartDefinition jaw = snapper.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition top = jaw.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(0.25F, -0.65F, 0.0F));

        PartDefinition top_rotate = top.addOrReplaceChild("top_rotate", CubeListBuilder.create(), PartPose.offset(0.0F, -5.3787F, -3.7553F));

        PartDefinition cube_r1 = top_rotate.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 0).addBox(-4.5F, 0.5F, -0.275F, 8.0F, 3.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -2.1213F, 0.5303F, -0.7854F, 0.0F, 0.0F));

        PartDefinition bottom = jaw.addOrReplaceChild("bottom", CubeListBuilder.create(), PartPose.offset(-0.25F, -1.5F, -3.0F));

        PartDefinition bottom_rotate = bottom.addOrReplaceChild("bottom_rotate", CubeListBuilder.create(), PartPose.offset(0.0F, -1.3447F, -2.159F));

        PartDefinition cube_r2 = bottom_rotate.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-5.25F, -3.625F, -1.5F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, -0.5303F, -1.591F, -0.7854F, 0.0F, 0.0F));

        PartDefinition top_fin = snapper.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(0, -8).addBox(0.0F, -5.0F, -4.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.25F));

        PartDefinition right_fin = snapper.addOrReplaceChild("right_fin", CubeListBuilder.create(), PartPose.offset(-4.25F, -3.5F, -0.75F));

        PartDefinition right_rotate = right_fin.addOrReplaceChild("right_rotate", CubeListBuilder.create().texOffs(0, -1).addBox(0.0F, -3.0F, -3.5F, 0.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -0.25F, 1.75F, 0.0F, -0.7854F, 0.0F));

        PartDefinition left_fin = snapper.addOrReplaceChild("left_fin", CubeListBuilder.create(), PartPose.offset(3.75F, -3.5F, -1.25F));

        PartDefinition left_rotate = left_fin.addOrReplaceChild("left_rotate", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(0.0F, -3.0F, -3.5F, 0.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.25F, -0.25F, 2.25F, 0.0F, 0.7854F, 0.0F));

        PartDefinition tail = snapper.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, -3.0F, 0.25F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 4.0F));

        PartDefinition tail_fin = tail.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, -3.25F, 0.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.25F, 3.25F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        float f = 1.0F;
        float f1 = 1.0F;
        if (!pEntity.isInWater()) {
            f = 1.3F;
            f1 = 1.7F;
        }
        this.snapper.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.snapper.xRot = pHeadPitch * ((float)Math.PI / 180F);

        this.right_fin.yRot = -0.2F + 0.4F * Mth.sin(pAgeInTicks * 0.2F);
        this.left_fin.yRot = 0.2F - 0.4F * Mth.sin(pAgeInTicks * 0.2F);

        this.bottom.xRot = this.isAggressive(pEntity) ? MathHelper.modelDegrees(45.0F) : 0.0F;

        float f2 = Mth.sin(this.attackTime * (float)Math.PI);
        float f3 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);

        if (this.isAggressive(pEntity)){
            this.bottom.xRot -= f2 * 1.2F - f3 * 0.4F;
        }

        this.tail.yRot = -f * 0.25F * Mth.sin(f1 * 0.6F * pAgeInTicks);
        this.tail_fin.yRot = -f * 0.45F * Mth.sin(0.6F * pAgeInTicks);
    }

    public boolean isAggressive(T entityIn) {
        return entityIn.isAggressive();
    }

}
