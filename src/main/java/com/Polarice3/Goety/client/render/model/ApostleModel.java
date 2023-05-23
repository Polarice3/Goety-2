package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ApostleModel<T extends Apostle> extends CultistModel<T> {
    public ModelPart halo;
    public ModelPart halo1;
    public ModelPart hat2;

    public ApostleModel(ModelPart root) {
        super(root);
        this.halo = this.getHead().getChild("halo");
        this.halo1 = this.halo.getChild("halo1");
        this.hat2 = this.getHead().getChild("hat2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = CultistModel.createMesh();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, 5.0F, 0.7854F, 0.0F, 0.0F));

        halo.addOrReplaceChild("halo1", CubeListBuilder.create().texOffs(32, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(0, 64).addBox(-8.0F, -7.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(-5.0F, -11.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.nose.visible = !entity.isSecondPhase();
        this.hat.visible = false;
        this.halo1.zRot = entity.getSpin();
        this.hat2.visible = !(entity.isSecondPhase() && MobUtil.healthIsHalved(entity));
    }
}
