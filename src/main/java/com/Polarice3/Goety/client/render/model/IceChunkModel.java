package com.Polarice3.Goety.client.render.model;

import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class IceChunkModel<T extends IceChunk> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart chunk;

    public IceChunkModel(ModelPart root) {
        this.root = root;
        this.chunk = root.getChild("chunk");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition chunk = partdefinition.addOrReplaceChild("chunk", CubeListBuilder.create().texOffs(0, 20).addBox(-8.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 12).addBox(-8.0F, -4.0F, 0.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(0.0F, -8.0F, -8.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 24).addBox(0.0F, -8.0F, 0.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(0.0F, -4.0F, 4.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
        this.chunk.yRot = 0.0F;
        if (p_102618_.isStarting()){
            this.chunk.yRot += p_102621_;
        }
    }
}
