package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.CultistModel;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.neutral.ApostleShade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ApostleShadeRenderer extends CultistRenderer<ApostleShade>{
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/cultist/apostle_shade.png");

    public ApostleShadeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ApostleShadeModel<>(renderManagerIn.bakeLayer(ModModelLayer.APOSTLE_SHADE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, ApostleShade p_116355_, float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_, float p_116361_) {
                if (p_116355_.getArmPose() != Cultist.CultistArmPose.CROSSED) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(ApostleShade p_114482_) {
        return TEXTURE;
    }

    public static class ApostleShadeModel<T extends ApostleShade> extends CultistModel<T> {
        public ModelPart halo;
        public ModelPart halo1;
        public ModelPart hat2;

        public ApostleShadeModel(ModelPart root) {
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
            this.hat.visible = false;
            this.halo1.zRot = entity.getSpin();
        }
    }
}
