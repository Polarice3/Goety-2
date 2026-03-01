package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.blocks.entities.PlushieBlockEntity;
import com.Polarice3.Goety.utils.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class PlushieModel extends SkullModelBase {
    private final ModelPart root;
    private final ModelPart plushie;

    public PlushieModel(ModelPart root) {
        this.root = root;
        this.plushie = root.getChild("plushie");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition plushie = partdefinition.addOrReplaceChild("plushie", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition head = plushie.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer = head.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -7.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = plushie.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition outer2 = body.addOrReplaceChild("outer2", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition left_arm = plushie.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = left_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -5.0F, -6.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 7.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition outer3 = left_arm.addOrReplaceChild("outer3", CubeListBuilder.create(), PartPose.offset(1.0F, 1.0F, 0.5F));

        PartDefinition cube_r2 = outer3.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 26).addBox(-1.0F, -5.0F, -6.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(2.0F, 6.0F, 1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition right_arm = plushie.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = right_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, -5.0F, -6.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 7.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition outer4 = right_arm.addOrReplaceChild("outer4", CubeListBuilder.create(), PartPose.offset(-1.0F, 1.0F, 0.5F));

        PartDefinition cube_r4 = outer4.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 35).addBox(-2.0F, -5.0F, -6.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-2.0F, 6.0F, 1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition left_leg = plushie.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition cube_r5 = left_leg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 44).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 7.0F, 3.0F, -1.5708F, -0.3927F, 0.0F));

        PartDefinition outer5 = left_leg.addOrReplaceChild("outer5", CubeListBuilder.create(), PartPose.offset(2.0F, 7.0F, 3.0F));

        PartDefinition cube_r6 = outer5.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 44).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, -1.5708F, -0.3927F, 0.0F));

        PartDefinition right_leg = plushie.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition cube_r7 = right_leg.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 53).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 7.0F, 3.0F, -1.5708F, 0.3927F, 0.0F));

        PartDefinition outer6 = right_leg.addOrReplaceChild("outer6", CubeListBuilder.create(), PartPose.offset(-2.0F, 7.0F, 3.0F));

        PartDefinition cube_r8 = outer6.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(12, 53).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, -1.5708F, 0.3927F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(float animationTick, float yRot, float xRot) {
        this.root.yRot = yRot * ((float)Math.PI / 180F);
        this.root.xRot = xRot * ((float)Math.PI / 180F);
        if (animationTick > 0) {
            float downTicks = PlushieBlockEntity.MAX_ANIMATION_TICKS / 3.0F;
            if (animationTick < downTicks) {
                this.plushie.y = Easing.OUT_BACK.interpolate(animationTick / downTicks, -8.0F, -6.0F);
                this.plushie.yScale = Easing.OUT_BACK.interpolate(animationTick / downTicks, 1.0F, 0.75F);
            } else {
                float bounceFactor = Easing.IN_QUART.calculate((animationTick / downTicks - 1.0F) * 0.5F);
                this.plushie.y = Easing.OUT_ELASTIC.interpolate(bounceFactor, -6.0F, -8.0F);
                this.plushie.yScale = Easing.OUT_ELASTIC.interpolate(bounceFactor, 0.75F, 1.0F);
            }
            this.plushie.xScale = this.plushie.zScale = Mth.sqrt(1 / this.plushie.yScale);
        } else {
            this.plushie.y = -8.0F;
            this.plushie.xScale = this.plushie.yScale = this.plushie.zScale = 1.0F;
        }
    }

    public void renderToBuffer(PoseStack pMatrixStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.root.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
