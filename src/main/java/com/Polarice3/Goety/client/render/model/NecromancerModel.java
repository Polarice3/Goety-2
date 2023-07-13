package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class NecromancerModel<T extends AbstractNecromancer> extends HumanoidModel<T> {
    public NecromancerModel(ModelPart p_170941_) {
        super(p_170941_);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition staff = rightArm.addOrReplaceChild("staff", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, 7.0F, 1.5708F, 0.0F, 0.0F));
        staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(48, 7).addBox(0.5F, -16.0F, -19.0F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));
        staff.addOrReplaceChild("group", CubeListBuilder.create().texOffs(51, 0).addBox(0.5F, -19.0F, -21.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 0).addBox(2.5F, -19.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 0).addBox(-1.5F, -19.0F, -19.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 0).addBox(0.5F, -19.0F, -17.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 7).addBox(0.5F, -17.0F, -21.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(52, 13).addBox(-1.5F, -17.0F, -19.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));
        staff.addOrReplaceChild("staffHead", CubeListBuilder.create().texOffs(52, 0).addBox(-0.5F, -20.0F, -20.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 7.0F, 19.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void setupAnim(T entityIn, float p_103799_, float p_103800_, float p_103801_, float p_103802_, float p_103803_) {
        super.setupAnim(entityIn, p_103799_, p_103800_, p_103801_, p_103802_, p_103803_);
        ItemStack itemstack = entityIn.getMainHandItem();
        if (itemstack.getItem() == ModItems.NECRO_STAFF.get()){
            if (!entityIn.isSpellCasting()){
                if (entityIn.getMainArm() == HumanoidArm.RIGHT){
                    this.rightArm.xRot -= MathHelper.modelDegrees(85);
                } else {
                    this.leftArm.xRot -= MathHelper.modelDegrees(85);
                }
            } else {
                if (entityIn.getMainArm() == HumanoidArm.RIGHT){
                    this.rightArm.z = 0.0F;
                    this.rightArm.x = -5.0F;
                    this.rightArm.xRot -= MathHelper.modelDegrees(160) + Mth.cos(p_103801_ * 0.6662F) * 0.25F;
                    this.rightArm.zRot = Mth.cos(p_103801_ * 0.6662F) * 0.25F;
                    this.rightArm.yRot = 0.0F;
                } else {
                    this.leftArm.z = 0.0F;
                    this.leftArm.x = 5.0F;
                    this.leftArm.xRot -= MathHelper.modelDegrees(160) - Mth.cos(p_103801_ * 0.6662F) * 0.25F;
                    this.leftArm.zRot = -Mth.cos(p_103801_ * 0.6662F) * 0.25F;
                    this.leftArm.yRot = 0.0F;
                }
            }
        }
    }
}
