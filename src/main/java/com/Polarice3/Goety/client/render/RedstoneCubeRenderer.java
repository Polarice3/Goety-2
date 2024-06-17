package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.RedstoneCubeModel;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneCube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class RedstoneCubeRenderer extends MobRenderer<RedstoneCube, RedstoneCubeModel<RedstoneCube>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_cube.png");
    private static final ResourceLocation ACTIVE = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_cube_active.png");
    private static final ResourceLocation GLOW = Goety.location("textures/entity/servants/redstone_monstrosity/redstone_cube_glow.png");

    public RedstoneCubeRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new RedstoneCubeModel<>(p_174435_.bakeLayer(ModModelLayer.REDSTONE_CUBE)), 0.5F);
        this.addLayer(new CubeEyesLayer<>(this));
        this.addLayer(new RCEmissiveLayer<>(this, GLOW, (entity, partialTicks, ageInTicks) -> !entity.isDeadOrDying() ? entity.bigGlow : 0.0F));
        this.addLayer(new RCEmissiveLayer<>(this, ACTIVE, (entity, partialTicks, ageInTicks) -> !entity.isDeadOrDying() && entity.bigGlow <= 0.0F ? entity.minorGlow : 0.0F));
    }

    @Override
    public void render(RedstoneCube pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!pEntity.getMainHandItem().isEmpty()){
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.0F, 1.1F, 0.0F);
            pMatrixStack.scale(1.0F, 1.0F, 1.0F);
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            minecraft.getItemRenderer().renderStatic(pEntity.getMainHandItem(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pMatrixStack, pBuffer, pEntity.level, 0);
            pMatrixStack.popPose();
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public ResourceLocation getTextureLocation(RedstoneCube p_116292_) {
        return TEXTURE;
    }

    public static class CubeEyesLayer<T extends RedstoneCube, M extends RedstoneCubeModel<T>> extends EyesLayer<T, M> {
        private static final RenderType EYES = RenderType.eyes(Goety.location("textures/entity/servants/redstone_monstrosity/redstone_cube_eyes.png"));

        public CubeEyesLayer(RenderLayerParent<T, M> p_117507_) {
            super(p_117507_);
        }

        public RenderType renderType() {
            return EYES;
        }
    }

    public static class RCEmissiveLayer<T extends RedstoneCube, M extends RedstoneCubeModel<T>> extends RenderLayer<T, M> {
        private final ResourceLocation texture;
        private final RCEmissiveLayer.AlphaFunction<T> alphaFunction;

        public RCEmissiveLayer(RenderLayerParent<T, M> p_234885_, ResourceLocation p_234886_, RCEmissiveLayer.AlphaFunction<T> p_234887_) {
            super(p_234885_);
            this.texture = p_234886_;
            this.alphaFunction = p_234887_;
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
                this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, this.alphaFunction.apply(entity, partialTicks, ageInTicks));
            }
        }

        public interface AlphaFunction<T extends RedstoneCube> {
            float apply(T p_234920_, float p_234921_, float p_234922_);
        }
    }
}
