package com.Polarice3.Goety.api.client;

public enum SpellArmPose implements net.minecraftforge.common.IExtensibleEnum {
    /*DEFAULT(SpellPoseType.DEFAULT, HumanoidModel.ArmPose.create(SpellPoseType.DEFAULT.getName(), false, (model, entity, arm) -> {
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
    })),
    FLYING(SpellPoseType.FLYING, HumanoidModel.ArmPose.create(SpellPoseType.FLYING.getName(), false, (model, entity, arm) -> {
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
    }));

    public static final List<SpellArmPose> POSE_LIST = new ArrayList<>();
    private final SpellPoseType poseType;
    @Nullable
    private final HumanoidModel.ArmPose armPose;

    SpellArmPose(SpellPoseType poseType, HumanoidModel.ArmPose transformer){
        this.poseType = poseType;
        this.armPose = transformer;
    }

    public static SpellArmPose create(String name, SpellPoseType poseType, HumanoidModel.ArmPose transformer){
        throw new IllegalStateException("Enum not extended");
    }

    public SpellPoseType getPoseType() {
        return poseType;
    }

    @Nullable
    public HumanoidModel.ArmPose getArmPose() {
        return armPose;
    }

    public static void addSpellPoseType(SpellArmPose poseType) {
        POSE_LIST.add(poseType);
    }*/
}
