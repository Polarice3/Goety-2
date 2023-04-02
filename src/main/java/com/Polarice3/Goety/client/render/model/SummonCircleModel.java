package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SummonCircleModel extends HierarchicalModel<SummonCircle> {
    private final ModelPart summon;
    private final ModelPart base;
    private final ModelPart sides;

    public SummonCircleModel(ModelPart root) {
        this.summon = root.getChild("summon");
        this.base = this.summon.getChild("base");
        this.sides = this.summon.getChild("sides");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition summon = partdefinition.addOrReplaceChild("summon", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition base = summon.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = base.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, 0.0F, 14.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition sides = summon.addOrReplaceChild("sides", CubeListBuilder.create().texOffs(0, 14).addBox(-7.0F, -5.0F, 7.0F, 14.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(7.0F, -5.0F, -7.0F, 0.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.0F, -5.0F, -7.0F, 0.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(SummonCircle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        base.yRot += ageInTicks * 0.5F;
        sides.yRot -= ageInTicks * 0.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        summon.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return summon;
    }
}
