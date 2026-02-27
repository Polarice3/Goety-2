package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.MonolithModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class ObsidianMonolithRenderer<T extends AbstractMonolith> extends AbstractMonolithRenderer<T> implements RenderLayerParent<T, MonolithModel<T>>{
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/obsidian_monolith.png");
    private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/monolith/obsidian_monolith_glow.png"));
    private static final Map<AbstractMonolith.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(AbstractMonolith.Crackiness.LOW, Goety.location("textures/entity/monolith/obsidian_monolith_crack_1.png"), AbstractMonolith.Crackiness.MEDIUM, Goety.location("textures/entity/monolith/obsidian_monolith_crack_2.png"), AbstractMonolith.Crackiness.HIGH, Goety.location("textures/entity/monolith/obsidian_monolith_crack_3.png"));
    private static final RenderType CHAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(Goety.location("textures/entity/monolith/obsidian_monolith_chain.png"), false);
    private final RenderLayer<T, MonolithModel<T>> layer;

    public ObsidianMonolithRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
        this.layer = new OMShieldLayer<>(this, p_i47208_1_.getModelSet());
    }

    @Override
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        return RENDER_TYPE;
    }

    @Override
    public Map<AbstractMonolith.Crackiness, ResourceLocation> cracknessLocation() {
        return resourceLocations;
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        LivingEntity trueOwner = pEntity.getTrueOwner();
        if (trueOwner != null && !pEntity.isEmerging()) {
            pMatrixStack.pushPose();
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 start = new Vec3(
                    Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX()),
                    Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY()) + pEntity.getBbHeight() / 2,
                    Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ())
            );
            pMatrixStack.translate(-start.x, -(start.y - pEntity.getBbHeight() / 2), -start.z);
            Vec3 end = new Vec3(
                    Mth.lerp(pPartialTicks, trueOwner.xo, trueOwner.getX()),
                    Mth.lerp(pPartialTicks, trueOwner.yo, trueOwner.getY()) + trueOwner.getBbHeight() / 2,
                    Mth.lerp(pPartialTicks, trueOwner.zo, trueOwner.getZ())
            );
            VertexConsumer vertexConsumer = pBuffer.getBuffer(CHAIN_RENDER_TYPE);
            Vec3 offset = end.subtract(start);
            Vec3 sight = camPos.subtract(start).scale(-1);
            Vec3 sideOffset = offset.cross(sight).normalize().scale(0.25);
            float age = pEntity.tickCount + pPartialTicks;
            float uOffset = -age * 0.06f;
            PoseStack.Pose pose = pMatrixStack.last();
            vertex(vertexConsumer, pose, start.add(sideOffset), uOffset, 0);
            vertex(vertexConsumer, pose, start.add(sideOffset.scale(-1)), uOffset, 1);
            vertex(vertexConsumer, pose, end.add(sideOffset.scale(-1)), (float) (offset.length() * 2) + uOffset, 1);
            vertex(vertexConsumer, pose, end.add(sideOffset), (float) (offset.length() * 2) + uOffset, 0);
            pMatrixStack.popPose();
        }
        this.layer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, 0.0F, 0.0F, pPartialTicks, 0.0F, 0.0F, 0.0F);
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, float u, float v) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z()).color(-1).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public MonolithModel<T> getModel() {
        return this.model;
    }

    public static class OMShieldLayer<T extends AbstractMonolith> extends RenderLayer<T, MonolithModel<T>> {
        private static final ResourceLocation TEXTURE = Goety.location("textures/entity/monolith/obsidian_monolith_shield.png");
        private final MonolithModel<T> model;

        public OMShieldLayer(RenderLayerParent<T, MonolithModel<T>> p_116967_, EntityModelSet p_174555_) {
            super(p_116967_);
            this.model = new MonolithModel<>(p_174555_.bakeLayer(ModModelLayer.MONOLITH));
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T monolith, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (monolith instanceof AbstractObsidianMonolith monolith1) {
                int shieldTime = monolith1.shieldTime;
                if (shieldTime > 0) {
                    matrixStackIn.pushPose();
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(monolith.getYRot()));
                    matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
                    matrixStackIn.translate(0.0D, 0.05D, 0.0D);
                    matrixStackIn.scale(1.05F, 1.025F, 1.05F);
                    float alpha = (float) shieldTime / 10;
                    float f = (monolith.tickCount + partialTicks) * 0.6F;
                    this.model.prepareMobModel(monolith, limbSwing, limbSwingAmount, partialTicks);
                    this.getParentModel().copyPropertiesTo(this.model);
                    RenderType renderType = RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
                    VertexConsumer vertexconsumer = bufferIn.getBuffer(renderType);
                    float f1 = Math.min(AbstractMonolith.getEmergingTime(), monolith.getAge());
                    this.model.setupAnim(monolith, f1, 0.0F, partialTicks, monolith.getYRot(), monolith.getXRot());
                    this.model.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1.0F);
                    matrixStackIn.popPose();
                }
            }

        }
    }
}
