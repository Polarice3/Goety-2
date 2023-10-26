package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;

public class SkeletonVillagerModel<T extends Owned> extends VillagerServantModel<T>{

    public SkeletonVillagerModel(ModelPart p_170677_) {
        super(p_170677_);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = VillagerServantModel.createMesh();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(42, 46).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(42, 46).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(28, 46).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(28, 46).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        ItemStack itemstack = pEntity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemstack.getItem() == Items.BOW && pEntity.isAggressive()) {
            if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        if (pEntity instanceof SkeletonVillagerServant skeletonVillagerMinion){
            if (skeletonVillagerMinion.isAggressive()) {
                if (itemstack.getItem() == Items.CROSSBOW) {
                    if (skeletonVillagerMinion.getMainArm() == HumanoidArm.RIGHT) {
                        this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                        if (skeletonVillagerMinion.isCharging()) {
                            this.rightArmPose = ArmPose.CROSSBOW_CHARGE;
                        }
                    } else {
                        this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                        if (skeletonVillagerMinion.isCharging()) {
                            this.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                        }
                    }
                }
            }
        }

        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        ItemStack itemstack = pEntity.getMainHandItem();
        if (pEntity.isAggressive() && (!(itemstack.getItem() instanceof ProjectileWeaponItem))) {
            float f = Mth.sin(this.attackTime * (float)Math.PI);
            float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.yRot = -(0.1F - f * 0.6F);
            this.leftArm.yRot = 0.1F - f * 0.6F;
            this.rightArm.xRot = (-(float)Math.PI / 2F);
            this.leftArm.xRot = (-(float)Math.PI / 2F);
            this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
            AnimationUtils.bobArms(this.rightArm, this.leftArm, pAgeInTicks);
        }
        boolean flag2 = pEntity.getMainArm() == HumanoidArm.RIGHT;
        boolean flag3 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
        if (flag2 != flag3) {
            this.posingLeftArm(pEntity);
            this.posingRightArm(pEntity);
        } else {
            this.posingRightArm(pEntity);
            this.posingLeftArm(pEntity);
        }
    }

    public void translateToHand(HumanoidArm pSide, PoseStack pMatrixStack) {
        float f = pSide == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelrenderer = this.getArm(pSide);
        modelrenderer.x += f;
        modelrenderer.translateAndRotate(pMatrixStack);
        modelrenderer.x -= f;
    }
}
