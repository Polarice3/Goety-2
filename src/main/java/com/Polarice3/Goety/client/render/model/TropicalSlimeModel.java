package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class TropicalSlimeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;

    public TropicalSlimeModel(ModelPart p_170955_) {
        this.root = p_170955_;
    }

    public static LayerDefinition createOuterBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createInnerBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(24, 16).addBox(-3.3F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 20).addBox(1.3F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 24).addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void setupAnim(T p_103831_, float p_103832_, float p_103833_, float p_103834_, float p_103835_, float p_103836_) {
    }

    public ModelPart root() {
        return this.root;
    }
}
