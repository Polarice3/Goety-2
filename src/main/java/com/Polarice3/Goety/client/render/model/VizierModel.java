package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.animation.VizierAnimations;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class VizierModel extends HierarchicalModel<Vizier> implements ArmedModel, HeadedModel {
    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart body;
    private final ModelPart cape;
    private final ModelPart head;
    private final ModelPart arms;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public VizierModel(ModelPart root) {
        this.root = root;
        this.cape = root.getChild("cape");
        this.main = root.getChild("main");
        this.head = this.main.getChild("head");
        this.body = this.main.getChild("body");
        this.arms = this.body.getChild("arms");
        this.rightArm = this.body.getChild("rightArm");
        this.leftArm = this.body.getChild("leftArm");
        this.leg0 = this.main.getChild("leg0");
        this.leg1 = this.main.getChild("leg1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = main.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_eyebrow = head.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -1.5F, -4.9F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 2).addBox(-1.0F, -2.5F, -4.9F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -3.5F, 0.0F));

        PartDefinition left_eyebrow = head.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.5F, -4.9F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 2).addBox(-1.0F, -2.5F, -4.9F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -3.5F, 0.0F));

        PartDefinition right_eye = head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.5F, -4.05F));

        PartDefinition left_eye = head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -3.5F, -4.05F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Hat = head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(0, 64).addBox(-5.0F, -13.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mustache = head.addOrReplaceChild("mustache", CubeListBuilder.create().texOffs(35, 0).addBox(-6.0F, -5.0F, -4.1F, 12.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition rightItem = rightArm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-0.5F, 6.0F, 0.5F));

        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(40, 46).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition leg0 = main.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition leg1 = main.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition cape = partdefinition.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(44, 62).addBox(-4.0F, 0.0F, -0.5F, 8.0F, 20.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 3.5F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(Vizier entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.arms.y = 3.0F;
        this.arms.z = -1.0F;
        this.arms.xRot = -0.75F;
        if (this.riding) {
            this.rightArm.xRot = (-(float)Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float)Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leg0.xRot = -1.4137167F;
            this.leg0.yRot = ((float)Math.PI / 10F);
            this.leg0.zRot = 0.07853982F;
            this.leg1.xRot = -1.4137167F;
            this.leg1.yRot = (-(float)Math.PI / 10F);
            this.leg1.zRot = -0.07853982F;
        } else {
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.leg0.yRot = 0.0F;
            this.leg0.zRot = 0.0F;
            this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.leg1.yRot = 0.0F;
            this.leg1.zRot = 0.0F;
        }

        if (entity.isCharging()) {
            if (entity.getMainHandItem().isEmpty()) {
                this.rightArm.xRot = ((float)Math.PI * 1.5F);
                this.leftArm.xRot = ((float)Math.PI * 1.5F);
            } else if (entity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArm.xRot = 3.7699115F;
            } else {
                this.leftArm.xRot = 3.7699115F;
            }
        }

        AbstractIllager.IllagerArmPose abstractillagerentity$armpose = entity.getArmPose();
        if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;
        }

        if (entity.deathTime > 0 && entity.isDeadOrDying()){
            this.main.yRot += ageInTicks * 2;
            this.cape.yRot = this.main.yRot;
        } else {
            this.main.yRot = 0.0F;
        }

        this.animate(entity.introAnimationState, VizierAnimations.INTRO, ageInTicks);
        this.animate(entity.deathAnimationState, VizierAnimations.DEATH, ageInTicks);

        boolean flag = abstractillagerentity$armpose == AbstractIllager.IllagerArmPose.CROSSED;
        this.arms.visible = flag;
        this.leftArm.visible = !flag;
        this.rightArm.visible = !flag;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderCape(PoseStack pMatrixStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay) {
        this.cape.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    private ModelPart getArm(HumanoidArm p_191216_1_) {
        return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
        this.getArm(p_102108_).translateAndRotate(p_102109_);
    }

    @Override
    public ModelPart getHead() {
        return head;
    }
}
