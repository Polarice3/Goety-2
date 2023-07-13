package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public class HauntedArmorModel extends HumanoidModel<AbstractHauntedArmor> {
    public HauntedArmorModel(ModelPart p_170821_) {
        super(p_170821_);
    }

    public void setupAnim(AbstractHauntedArmor entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        AbstractHauntedArmor.HauntedArmPose armPose = entityIn.getArmPose();
        this.rightLeg.x = -1.9F;
        this.leftLeg.x = 1.9F;
        switch (armPose) {
            case ATTACK -> {
                if (!entityIn.getMainHandItem().isEmpty()) {
                    AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
                }
            }
            case GUARD -> {
                this.body.xRot = MathHelper.modelDegrees(25.0F);
                this.rightLeg.x = -3.0F;
                this.rightLeg.z = 4.0F;
                this.leftLeg.x = 3.0F;
                this.leftLeg.z = 4.0F;
                this.getThisArm(entityIn.getMainArm()).xRot = MathHelper.modelDegrees(45.0F);
            }
        }
    }

    public boolean isAggressive(AbstractHauntedArmor entityIn) {
        return entityIn.isAggressive();
    }

    private ModelPart getThisArm(HumanoidArm p_191216_1_) {
        return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
        this.getThisArm(sideIn).translateAndRotate(matrixStackIn);
    }
}
