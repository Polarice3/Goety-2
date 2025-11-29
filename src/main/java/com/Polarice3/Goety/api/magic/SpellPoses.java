package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class SpellPoses {
    public static final HumanoidModel.ArmPose SPELL = HumanoidModel.ArmPose.create("GOETY_SPELL", false, (model, entity, arm) -> {
        float f5 = entity.walkAnimation.position(Minecraft.getInstance().getPartialTick());
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot -= MathHelper.modelDegrees(105);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
            model.leftArm.xRot += MathHelper.modelDegrees(25);
        } else {
            model.leftArm.xRot -= MathHelper.modelDegrees(105);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
            model.rightArm.xRot += MathHelper.modelDegrees(25);
        }
    });

    public static final HumanoidModel.ArmPose FLIGHT_POSE = HumanoidModel.ArmPose.create("GOETY_FLYING", false, (model, entity, arm) -> {
        float f5 = 1.0F;
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot = -MathHelper.modelDegrees(105);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.25F;
            model.leftArm.xRot = MathHelper.modelDegrees(25);
        } else {
            model.leftArm.xRot = -MathHelper.modelDegrees(105);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.25F;
            model.rightArm.xRot = MathHelper.modelDegrees(25);
        }
        model.rightLeg.xRot = MathHelper.modelDegrees(17.5F);
        model.leftLeg.xRot = MathHelper.modelDegrees(17.5F);

        model.rightLeg.xRot += 1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
        model.leftLeg.xRot += -1.0F * Mth.sin(Minecraft.getInstance().getPartialTick() * 0.067F) * 0.05F;
    });

    public static final HumanoidModel.ArmPose HOLD_STAFF = HumanoidModel.ArmPose.create("HOLD_STAFF", false, (model, entity, arm) -> {
        float f5 = entity.walkAnimation.position(Minecraft.getInstance().getPartialTick());
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot -= MathHelper.modelDegrees(90);
            model.rightArm.zRot = Mth.cos(f5 * 0.6662F) * 0.1F;
        } else {
            model.leftArm.xRot -= MathHelper.modelDegrees(90);
            model.leftArm.zRot = -Mth.cos(f5 * 0.6662F) * 0.1F;
        }
    });
}
