package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class MinionModel<T extends Minion> extends HierarchicalModel<T> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public MinionModel(ModelPart p_171045_) {
        super(RenderType::entityTranslucent);
        this.root = p_171045_.getChild("root");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.head = this.root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));
        partdefinition1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 20.0F, 0.0F));
        partdefinition2.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(-1.75F, 0.25F, 0.0F));
        partdefinition2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(1.75F, 0.25F, 0.0F));
        partdefinition2.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).mirror().addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, 1.0F, 1.0F));
        partdefinition2.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.0F, 1.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void setupAnim(T p_104028_, float p_104029_, float p_104030_, float p_104031_, float p_104032_, float p_104033_) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = p_104032_ * ((float)Math.PI / 180F);
        this.head.xRot = p_104033_ * ((float)Math.PI / 180F);
        float f = Mth.cos(p_104031_ * 5.5F * ((float)Math.PI / 180F)) * 0.1F;
        this.rightArm.zRot = ((float)Math.PI / 5F) + f;
        this.leftArm.zRot = -(((float)Math.PI / 5F) + f);
        if (p_104028_.isCharging()) {
            this.body.xRot = 0.0F;
            this.setArmsCharging(p_104028_.getMainHandItem(), p_104028_.getOffhandItem(), f);
        } else {
            this.body.xRot = 0.15707964F;
        }

        this.leftWing.yRot = 1.0995574F + Mth.cos(p_104031_ * 45.836624F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 16.2F;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.leftWing.xRot = 0.47123888F;
        this.leftWing.zRot = -0.47123888F;
        this.rightWing.xRot = 0.47123888F;
        this.rightWing.zRot = 0.47123888F;
    }

    private void setArmsCharging(ItemStack p_265484_, ItemStack p_265329_, float p_265125_) {
        if (p_265484_.isEmpty() && p_265329_.isEmpty()) {
            this.rightArm.xRot = -1.2217305F;
            this.rightArm.yRot = 0.2617994F;
            this.rightArm.zRot = -0.47123888F - p_265125_;
            this.leftArm.xRot = -1.2217305F;
            this.leftArm.yRot = -0.2617994F;
            this.leftArm.zRot = 0.47123888F + p_265125_;
        } else {
            if (!p_265484_.isEmpty()) {
                this.rightArm.xRot = 3.6651914F;
                this.rightArm.yRot = 0.2617994F;
                this.rightArm.zRot = -0.47123888F - p_265125_;
            }

            if (!p_265329_.isEmpty()) {
                this.leftArm.xRot = 3.6651914F;
                this.leftArm.yRot = -0.2617994F;
                this.leftArm.zRot = 0.47123888F + p_265125_;
            }

        }
    }

    public ModelPart root() {
        return this.root;
    }

    public void translateToHand(HumanoidArm p_259770_, PoseStack p_260351_) {
        boolean flag = p_259770_ == HumanoidArm.RIGHT;
        ModelPart modelpart = flag ? this.rightArm : this.leftArm;
        this.root.translateAndRotate(p_260351_);
        this.body.translateAndRotate(p_260351_);
        modelpart.translateAndRotate(p_260351_);
        p_260351_.scale(0.55F, 0.55F, 0.55F);
        this.offsetStackPosition(p_260351_, flag);
    }

    private void offsetStackPosition(PoseStack p_263343_, boolean p_263414_) {
        if (p_263414_) {
            p_263343_.translate(0.046875D, -0.15625D, 0.078125D);
        } else {
            p_263343_.translate(-0.046875D, -0.15625D, 0.078125D);
        }

    }
}
