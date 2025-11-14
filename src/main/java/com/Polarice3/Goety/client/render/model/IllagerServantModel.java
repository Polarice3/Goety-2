package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.SpyglassItem;

public class IllagerServantModel<T extends LivingEntity> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
    public final ModelPart root;
    public final ModelPart body;
    public final ModelPart clothes;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart collar;
    public final ModelPart arms;
    public final ModelPart RightArm;
    public final ModelPart LeftArm;
    public final ModelPart RightLeg;
    public final ModelPart LeftLeg;

    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;

    public IllagerServantModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hat.visible = false;
        this.collar = this.head.getChild("collar");
        this.body = root.getChild("body");
        this.clothes = this.body.getChild("clothes");
        this.arms = root.getChild("arms");
        this.RightArm = root.getChild("right_arm");
        this.LeftArm = root.getChild("left_arm");
        this.LeftLeg = root.getChild("left_leg");
        this.RightLeg = root.getChild("right_leg");
    }

    public static MeshDefinition createMesh() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition1.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        partdefinition2.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        return meshdefinition;
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMesh(), 64, 64);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entityIn instanceof AbstractIllagerServant servant) {
            if (servant.cantDo > 0) {
                this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
                this.head.xRot = 0.4F;
            } else {
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
            }
        } else {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
            this.head.xRot = headPitch * ((float) Math.PI / 180F);
        }
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

        if (entityIn instanceof AbstractIllagerServant servant){
            AbstractIllagerServant.IllagerServantArmPose armPose = servant.getArmPose();
            if (armPose == AbstractIllagerServant.IllagerServantArmPose.ATTACKING) {
                if (entityIn.getMainHandItem().isEmpty()) {
                    AnimationUtils.animateZombieArms(this.LeftArm, this.RightArm, true, this.attackTime, ageInTicks);
                } else {
                    AnimationUtils.swingWeaponDown(this.RightArm, this.LeftArm, servant, this.attackTime, ageInTicks);
                }
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING) {
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
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.BOW_AND_ARROW) {
                this.RightArm.yRot = -0.1F + this.head.yRot;
                this.RightArm.xRot = -1.5707964F + this.head.xRot;
                this.LeftArm.xRot = -0.9424779F + this.head.xRot;
                this.LeftArm.yRot = this.head.yRot - 0.4F;
                this.LeftArm.zRot = 1.5707964F;
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_HOLD) {
                AnimationUtils.animateCrossbowHold(this.RightArm, this.LeftArm, this.head, true);
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_CHARGE) {
                AnimationUtils.animateCrossbowCharge(this.RightArm, this.LeftArm, entityIn, true);
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CELEBRATING) {
                this.RightArm.z = 0.0F;
                this.RightArm.x = -5.0F;
                this.RightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
                this.RightArm.zRot = 2.3561945F;
                this.RightArm.yRot = 0.0F;
                this.LeftArm.z = 0.0F;
                this.LeftArm.x = 5.0F;
                this.LeftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
                this.LeftArm.zRot = -2.3561945F;
                this.LeftArm.yRot = 0.0F;
            } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSED && this.arms.visible) {
                this.RightArm.xRot = -0.75F;
                this.RightArm.zRot = 0.0F;
                this.RightArm.yRot = 0.0F;
                this.LeftArm.xRot = -0.75F;
                this.LeftArm.zRot = 0.0F;
                this.LeftArm.yRot = 0.0F;
            }
            boolean flag = armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSED;
            this.arms.visible = flag;
            this.LeftArm.visible = !flag;
            this.RightArm.visible = !flag;
            if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
                this.useItemRight(InteractionHand.MAIN_HAND, entityIn);
                this.useItemLeft(InteractionHand.OFF_HAND, entityIn);
            } else if (entityIn.getMainArm() == HumanoidArm.LEFT) {
                this.useItemLeft(InteractionHand.MAIN_HAND, entityIn);
                this.useItemRight(InteractionHand.OFF_HAND, entityIn);
            }
        }
        boolean flag2 = entityIn.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
                || entityIn.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
        this.clothes.visible = !flag2;
    }

    public void renderToBuffer(PoseStack p_102034_, VertexConsumer p_102035_, int p_102036_, int p_102037_, float p_102038_, float p_102039_, float p_102040_, float p_102041_) {
        if (this.young) {
            p_102034_.pushPose();
            float f = 1.5F / 2.0F;
            p_102034_.scale(f, f, f);

            p_102034_.translate(0.0F, 1.0F, 0.0F);
            this.headParts().forEach((p_102081_) -> {
                p_102081_.render(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
            });
            p_102034_.popPose();
            p_102034_.pushPose();
            float f1 = 1.0F / 2.0F;
            p_102034_.scale(f1, f1, f1);
            p_102034_.translate(0.0F, 24.0F / 16.0F, 0.0F);
            this.bodyParts().forEach((p_102071_) -> {
                p_102071_.render(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
            });
            p_102034_.popPose();
        } else {
            super.renderToBuffer(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
        }

    }

    public void copyPropertiesTo(IllagerServantModel<T> p_102873_) {
        super.copyPropertiesTo(p_102873_);
        p_102873_.leftArmPose = this.leftArmPose;
        p_102873_.rightArmPose = this.rightArmPose;
    }

    public void useItemRight(InteractionHand hand, T entityIn) {
        if (entityIn.getUsedItemHand() == hand) {
            if (entityIn.getUseItem().getItem() instanceof SpyglassItem) {
                this.RightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entityIn.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.RightArm.yRot = this.head.yRot - 0.2617994F;
            } else if (entityIn.getUseItem().getItem() instanceof InstrumentItem) {
                this.RightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.RightArm.yRot = this.head.yRot - ((float)Math.PI / 6F);
            }
        }
    }

    public void useItemLeft(InteractionHand hand, T entityIn) {
        if (entityIn.getUsedItemHand() == hand) {
            if (entityIn.getUseItem().getItem() instanceof SpyglassItem) {
                this.LeftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entityIn.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.LeftArm.yRot = this.head.yRot + 0.2617994F;
            } else if (entityIn.getUseItem().getItem() instanceof InstrumentItem) {
                this.LeftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.LeftArm.yRot = this.head.yRot + ((float)Math.PI / 6F);
            }
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    private ModelPart getArm(HumanoidArm p_102923_) {
        return p_102923_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    public ModelPart getCollar() {
        return this.collar;
    }

    public ModelPart getHead() {
        return this.head;
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.RightArm, this.LeftArm, this.arms, this.RightLeg, this.LeftLeg);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
    }

    @Override
    public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0, 0.0F, 0);
    }

    @Override
    public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0F, 0.0F);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    @Override
    public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
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
