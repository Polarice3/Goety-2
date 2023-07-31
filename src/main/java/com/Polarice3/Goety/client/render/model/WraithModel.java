package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.WraithAnimations;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class WraithModel<T extends LivingEntity> extends HierarchicalModel<T> {
    private final ModelPart Ghost;
    private final ModelPart head;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart body;

    public WraithModel(ModelPart root) {
        this.Ghost = root.getChild("Ghost");
        this.head = this.Ghost.getChild("head");
        this.RightArm = this.Ghost.getChild("RightArm");
        this.LeftArm = this.Ghost.getChild("LeftArm");
        this.body = this.Ghost.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Ghost = partdefinition.addOrReplaceChild("Ghost", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = Ghost.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 3).addBox(-3.0F, -7.0F, -2.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, -1.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition hat_r1 = hat.addOrReplaceChild("hat_r1", CubeListBuilder.create().texOffs(0, 51).addBox(-5.0F, -4.7F, -0.6F, 10.0F, 4.0F, 9.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(0.0F, -5.3F, 2.6F, -0.6981F, 0.0F, 0.0F));

        PartDefinition RightArm = Ghost.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-0.5F, -2.0F, -0.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, -22.0F, 0.0F));

        PartDefinition Robe2 = RightArm.addOrReplaceChild("Robe2", CubeListBuilder.create().texOffs(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = Ghost.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -22.0F, 0.0F));

        PartDefinition Robe = LeftArm.addOrReplaceChild("Robe", CubeListBuilder.create().texOffs(16, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = Ghost.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 17).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(35, 34).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 20.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch){
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (pEntity instanceof AbstractWraith wraith){
            this.animate(wraith.attackAnimationState, WraithAnimations.ATTACK, pAgeInTicks);
            this.animate(wraith.breathingAnimationState, WraithAnimations.PUKE, pAgeInTicks);
            if (!wraith.isFiring() && !wraith.isBreathing()) {
                if (wraith.isTeleporting()) {
                    float f7 = Mth.sin(((float) (wraith.teleportTime - 20) - wraith.teleportTime2) / 20.0F * (float) Math.PI * 0.25F);
                    this.head.xRot = (((float) Math.PI) * f7) + 2.0F;
                    this.RightArm.xRot = ((float) Math.PI) * f7;
                    this.LeftArm.xRot = ((float) Math.PI) * f7;
                    this.Ghost.y += (((float) Math.PI) * f7) * 5.0F;
                } else {
                    float f = pAgeInTicks * 0.0025F;
                    this.Ghost.y = Mth.sin(f * 40.0F) + 24.0F;
                    float f4 = Math.min(pLimbSwingAmount / 0.3F, 1.0F);
                    this.body.xRot = f4 * MathHelper.modelDegrees(40.0F);
                    this.body.xRot += Mth.cos(pAgeInTicks * 0.09F) * 0.1F + 0.1F;
                    this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
                    float f5 = Math.min(pLimbSwingAmount / 2.0F, 1.0F);
                    float degrees;
                    if (wraith.getLookControl().isLookingAtTarget()) {
                        degrees = 0.0F;
                    } else {
                        degrees = MathHelper.modelDegrees(17.5F) - f5;
                    }
                    this.head.xRot = pHeadPitch * ((float) Math.PI / 180F) + degrees;
                    animateArms(this.LeftArm, this.RightArm, pLimbSwingAmount, pAgeInTicks);
                }
            } else if (wraith.isBreathing()){
                this.Ghost.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
                this.Ghost.xRot = pHeadPitch * ((float)Math.PI / 180F);
            }
        } else {
            this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
        }
    }

    public static void animateArms(ModelPart leftArm, ModelPart rightArm, float attackTime, float ageInTicks) {
        rightArm.zRot = Math.min(attackTime / 0.9F, 1.0F);
        leftArm.zRot = -Math.min(attackTime / 0.9F, 1.0F);
        rightArm.yRot = -(0.1F - 0 * 0.6F);
        leftArm.yRot = 0.1F - 0 * 0.6F;
        float f2 = -MathHelper.modelDegrees(45.0F);
        rightArm.xRot = f2;
        leftArm.xRot = f2;
        AnimationUtils.bobArms(rightArm, leftArm, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Ghost.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return Ghost;
    }
}
