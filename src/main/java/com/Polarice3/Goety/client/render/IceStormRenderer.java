package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class IceStormRenderer extends EntityRenderer<IceStorm> {
    private static final ResourceLocation ICE_STORM_LOCATION = Goety.location("textures/entity/projectiles/ice_storm.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(ICE_STORM_LOCATION);
    private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
    private final ModelPart cube;
    private final ModelPart glass;

    public IceStormRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        this.shadowRadius = 0.0F;
        ModelPart modelpart = renderManagerIn.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void render(IceStorm p_114162_, float p_114163_, float p_114164_, PoseStack p_114165_, MultiBufferSource p_114166_, int p_114167_) {
        p_114165_.pushPose();
        float f1 = (p_114162_.tickCount + p_114164_) * 3.0F;
        VertexConsumer vertexconsumer = p_114166_.getBuffer(RENDER_TYPE);
        p_114165_.pushPose();
        float f2 = this.scale(p_114162_);
        p_114165_.scale(f2, f2, f2);
        p_114165_.scale(2.0F, 2.0F, 2.0F);
        int i = OverlayTexture.NO_OVERLAY;
        p_114165_.mulPose(Axis.YP.rotationDegrees(f1));
        p_114165_.translate(0.0D, 0.25F, 0.0D);
        p_114165_.mulPose((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        this.glass.render(p_114165_, vertexconsumer, p_114167_, i, 1.0F, 1.0F, 1.0F, 0.25F);
        p_114165_.scale(0.875F, 0.875F, 0.875F);
        p_114165_.mulPose((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        p_114165_.mulPose(Axis.YP.rotationDegrees(f1));
        this.glass.render(p_114165_, vertexconsumer, p_114167_, i, 1.0F, 1.0F, 1.0F, 0.5F);
        p_114165_.scale(0.875F, 0.875F, 0.875F);
        p_114165_.mulPose((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SIN_45, 0.0F, SIN_45));
        p_114165_.mulPose(Axis.YP.rotationDegrees(f1));
        this.cube.render(p_114165_, vertexconsumer, p_114167_, i, 1.0F, 1.0F, 1.0F, 0.75F);
        p_114165_.popPose();
        p_114165_.popPose();
        super.render(p_114162_, p_114163_, p_114164_, p_114165_, p_114166_, p_114167_);
    }

    public float scale(IceStorm entityIn) {
        return 1.0F + (entityIn.getSize() / 8.0F);
    }

    public ResourceLocation getTextureLocation(IceStorm entity) {
        return ICE_STORM_LOCATION;
    }
}
