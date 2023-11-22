package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.PreacherAnimations;
import com.Polarice3.Goety.common.entities.hostile.illagers.Preacher;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class PreacherModel<T extends Preacher> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart illager;
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public PreacherModel(ModelPart root) {
        this.root = root;
        this.illager = root.getChild("illager");
        this.head = this.illager.getChild("head");
        this.rightLeg = this.illager.getChild("right_leg");
        this.leftLeg = this.illager.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = illager.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -11.5F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, -11.5F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.5F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -9.5F, 0.0F));

        PartDefinition book = right_arm.addOrReplaceChild("book", CubeListBuilder.create().texOffs(56, 0).addBox(-1.5F, 3.5F, -6.0F, 3.0F, 1.0F, 12.0F, new CubeDeformation(-0.25F))
                .texOffs(100, 0).addBox(-1.0F, 4.0F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition top = book.addOrReplaceChild("top", CubeListBuilder.create().texOffs(86, 0).addBox(-1.5F, 0.0F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(-0.25F))
                .texOffs(32, 0).addBox(-1.0F, 0.5F, -5.0F, 1.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition bottom = book.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(86, 0).addBox(0.5F, 0.0F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(-0.25F))
                .texOffs(32, 0).addBox(0.0F, 0.5F, -5.0F, 1.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -9.5F, 0.0F));

        PartDefinition staff = left_arm.addOrReplaceChild("staff", CubeListBuilder.create().texOffs(62, 31).addBox(-1.0F, 0.0F, -16.0F, 1.0F, 1.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(90, 57).addBox(-1.0F, 1.0F, -16.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 58).addBox(-1.0F, 5.0F, -15.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 8.0F, 0.0F));

        PartDefinition head = illager.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(44, 19).addBox(-5.0F, -8.0F, -3.0F, 10.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

        PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (!entity.isDeadOrDying()){
            this.animateHeadLookTarget(netHeadYaw, headPitch);
        }
        this.animate(entity.idleAnimationState, PreacherAnimations.IDLE, ageInTicks);
        if (this.riding){
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float)Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float)Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        } else {
            this.animate(entity.walkAnimationState, PreacherAnimations.WALK, ageInTicks);
        }
        this.animate(entity.healAnimationState, PreacherAnimations.HEAL, ageInTicks);
        this.animateWalk(limbSwing, limbSwingAmount);
    }

    private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
    }

    private void animateWalk(float limbSwing, float limbSwingAmount){
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
