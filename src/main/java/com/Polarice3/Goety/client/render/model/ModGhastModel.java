package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class ModGhastModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart[] tentacles = new ModelPart[9];

    public ModGhastModel(ModelPart p_170570_) {
        this.root = p_170570_;
        this.body = root.getChild("body");

        for(int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = body.getChild(createTentacleName(i));
        }

    }

    private static String createTentacleName(int p_170573_) {
        return "tentacle" + p_170573_;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        RandomSource randomsource = RandomSource.create(1660L);

        for(int i = 0; i < 9; ++i) {
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
            float f1 = ((float)(i / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
            int j = randomsource.nextInt(7) + 8;
            body.addOrReplaceChild(createTentacleName(i), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, (float)j, 2.0F), PartPose.offset(f, 7.0F, f1));
        }

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void setupAnim(T p_102681_, float p_102682_, float p_102683_, float p_102684_, float netHeadYaw, float headPitch) {
        for(int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].xRot = 0.2F * Mth.sin(p_102684_ * 0.3F + (float)i) + 0.4F;
        }
        this.body.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.body.xRot = headPitch * ((float)Math.PI / 180F);
    }

    public ModelPart root() {
        return this.root;
    }
}
