package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class ArmoredIllagerModel<T extends AbstractIllager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
    private final ModelPart root;
    private final ModelPart illager;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart arms;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart cape;

    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;

    public ArmoredIllagerModel(ModelPart root) {
        this.root = root;
        this.illager = root.getChild("illager");
        this.body = this.illager.getChild("body");
        this.head = this.body.getChild("head");
        this.arms = this.body.getChild("arms");
        this.RightArm = this.body.getChild("right_arm");
        this.LeftArm = this.body.getChild("left_arm");
        this.LeftLeg = this.illager.getChild("left_leg");
        this.RightLeg = this.illager.getChild("right_leg");
        this.cape = this.body.getChild("cape");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition illager = partdefinition.addOrReplaceChild("illager", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = illager.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(32, 0).addBox(-5.5F, -11.5F, -4.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offset(1.0F, 1.0F, 0.0F));

        PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition right_leg = illager.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition left_leg = illager.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        if (this.riding) {
            this.RightArm.xRot = (-(float) Math.PI / 5F);
            this.RightArm.yRot = 0.0F;
            this.RightArm.zRot = 0.0F;
            this.LeftArm.xRot = (-(float) Math.PI / 5F);
            this.LeftArm.yRot = 0.0F;
            this.LeftArm.zRot = 0.0F;
            this.RightLeg.xRot = -1.4137167F;
            this.RightLeg.yRot = ((float) Math.PI / 10F);
            this.RightLeg.zRot = 0.07853982F;
            this.LeftLeg.xRot = -1.4137167F;
            this.LeftLeg.yRot = (-(float) Math.PI / 10F);
            this.LeftLeg.zRot = -0.07853982F;
        } else {
            this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F;
            this.RightArm.yRot = 0.0F;
            this.RightArm.zRot = 0.0F;
            this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.yRot = 0.0F;
            this.LeftArm.zRot = 0.0F;
            this.RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.RightLeg.yRot = 0.0F;
            this.RightLeg.zRot = 0.0F;
            this.LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
            this.LeftLeg.yRot = 0.0F;
            this.LeftLeg.zRot = 0.0F;
        }

        AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = entityIn.getArmPose();
        if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
            if (entityIn.getMainHandItem().isEmpty()) {
                AnimationUtils.animateZombieArms(this.LeftArm, this.RightArm, true, this.attackTime, ageInTicks);
            } else {
                AnimationUtils.swingWeaponDown(this.RightArm, this.LeftArm, entityIn, this.attackTime, ageInTicks);
            }
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            this.RightArm.z = 0.0F;
            this.RightArm.x = -5.0F;
            this.LeftArm.z = 0.0F;
            this.LeftArm.x = 5.0F;
            this.RightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.LeftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.RightArm.zRot = 2.3561945F;
            this.LeftArm.zRot = -2.3561945F;
            this.RightArm.yRot = 0.0F;
            this.LeftArm.yRot = 0.0F;
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            this.RightArm.yRot = -0.1F + this.head.yRot;
            this.RightArm.xRot = -1.5707964F + this.head.xRot;
            this.LeftArm.xRot = -0.9424779F + this.head.xRot;
            this.LeftArm.yRot = this.head.yRot - 0.4F;
            this.LeftArm.zRot = 1.5707964F;
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.RightArm, this.LeftArm, this.head, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.RightArm, this.LeftArm, entityIn, true);
        } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
            this.RightArm.z = 0.0F;
            this.RightArm.x = -5.0F;
            this.RightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.RightArm.zRot = 2.670354F;
            this.RightArm.yRot = 0.0F;
            this.LeftArm.z = 0.0F;
            this.LeftArm.x = 5.0F;
            this.LeftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.LeftArm.zRot = -2.3561945F;
            this.LeftArm.yRot = 0.0F;
        }
    }

    public void copyPropertiesTo(ArmoredIllagerModel<T> p_102873_) {
        super.copyPropertiesTo(p_102873_);
        p_102873_.leftArmPose = this.leftArmPose;
        p_102873_.rightArmPose = this.rightArmPose;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    private ModelPart getArm(HumanoidArm p_102923_) {
        return p_102923_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(HumanoidArm p_102925_, PoseStack p_102926_) {
        this.illager.translateAndRotate(p_102926_);
        this.body.translateAndRotate(p_102926_);
        this.getArm(p_102925_).translateAndRotate(p_102926_);
    }

    @Override
    public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
        this.illager.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0, -0.1F, 0);
    }

    @Override
    public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
        this.illager.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0F, 0.0F);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    @Override
    public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
        this.illager.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
        this.illager.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        modelPart.translateAndRotate(poseStack);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    public Iterable<ModelPart> rightHandArmors() {
        return ImmutableList.of(this.RightArm);
    }

    public Iterable<ModelPart> leftHandArmors() {
        return ImmutableList.of(this.LeftArm);
    }

    public Iterable<ModelPart> rightLegPartArmors() {
        return ImmutableList.of(this.RightLeg);
    }

    public Iterable<ModelPart> leftLegPartArmors() {
        return ImmutableList.of(this.LeftLeg);
    }

    public Iterable<ModelPart> bodyPartArmors() {
        return ImmutableList.of(this.body);
    }

    public Iterable<ModelPart> headPartArmors() {
        return ImmutableList.of(this.head);
    }
}
