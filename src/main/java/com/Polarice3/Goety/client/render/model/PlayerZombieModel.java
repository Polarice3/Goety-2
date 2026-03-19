package com.Polarice3.Goety.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class PlayerZombieModel<T extends Mob> extends HumanoidModel<T> {
    private final List<ModelPart> parts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;

    public PlayerZombieModel(ModelPart p_170337_) {
        super(p_170337_, RenderType::entityTranslucent);
        this.leftSleeve = p_170337_.getChild("left_sleeve");
        this.rightSleeve = p_170337_.getChild("right_sleeve");
        this.leftPants = p_170337_.getChild("left_pants");
        this.rightPants = p_170337_.getChild("right_pants");
        this.jacket = p_170337_.getChild("jacket");
        this.parts = p_170337_.getAllParts().filter((p_170824_) -> {
            return !p_170824_.isEmpty();
        }).collect(ImmutableList.toImmutableList());
    }

    public static MeshDefinition createMesh(CubeDeformation p_170826_) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(p_170826_, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_), PartPose.offset(1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.ZERO);
        return meshdefinition;
    }

    public static LayerDefinition createBodyLayer(){
        return LayerDefinition.create(createMesh(CubeDeformation.NONE), 64, 64);
    }

    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(pEntity), this.attackTime, pAgeInTicks);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.hat.copyFrom(this.head);
    }

    public void setAllVisible(boolean p_103419_) {
        super.setAllVisible(p_103419_);
        this.leftSleeve.visible = p_103419_;
        this.rightSleeve.visible = p_103419_;
        this.leftPants.visible = p_103419_;
        this.rightPants.visible = p_103419_;
        this.jacket.visible = p_103419_;
    }

    public void translateToHand(HumanoidArm p_103392_, PoseStack p_103393_) {
        ModelPart modelpart = this.getArm(p_103392_);
        modelpart.translateAndRotate(p_103393_);
    }

    public ModelPart getRandomModelPart(RandomSource p_233439_) {
        return this.parts.get(p_233439_.nextInt(this.parts.size()));
    }

    public boolean isAggressive(T entityIn) {
        return entityIn.isAggressive();
    }
}
