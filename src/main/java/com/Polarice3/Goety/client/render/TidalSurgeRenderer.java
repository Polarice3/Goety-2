package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TidalSurgeModel;
import com.Polarice3.Goety.common.entities.projectiles.AbstractWave;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TidalSurgeRenderer<T extends AbstractWave> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_0 = Goety.location("textures/entity/projectiles/tidal_surge/surge_0.png");
    private static final ResourceLocation TEXTURE_1 = Goety.location("textures/entity/projectiles/tidal_surge/surge_1.png");
    private static final ResourceLocation TEXTURE_2 = Goety.location("textures/entity/projectiles/tidal_surge/surge_2.png");
    private static final ResourceLocation TEXTURE_3 = Goety.location("textures/entity/projectiles/tidal_surge/surge_3.png");
    private static final ResourceLocation OVERLAY_TEXTURE_0 = Goety.location("textures/entity/projectiles/tidal_surge/overlay_0.png");
    private static final ResourceLocation OVERLAY_TEXTURE_1 = Goety.location("textures/entity/projectiles/tidal_surge/overlay_1.png");
    private static final ResourceLocation OVERLAY_TEXTURE_2 = Goety.location("textures/entity/projectiles/tidal_surge/overlay_2.png");
    private static final ResourceLocation OVERLAY_TEXTURE_3 = Goety.location("textures/entity/projectiles/tidal_surge/overlay_3.png");
    private final TidalSurgeModel<T> model;

    public TidalSurgeRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        this.model = new TidalSurgeModel<>(p_174008_.bakeLayer(ModModelLayer.TIDAL_SURGE));
    }

    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if(entityIn.isInvisible()){
            return;
        }
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, 1.5D, 0.0D);
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 180.0F));
        float ageInTicks = entityIn.activeWaveTicks + partialTicks;
        float f = ageInTicks / 10.0F;
        matrixStackIn.translate(0.0D, -0.1D + (1.0D - (f * 0.5F)) * -1.0D, -0.5D);
        matrixStackIn.scale(((0.2F + f) * 0.9F), -1.0F, 1.0F);
        this.model.setupAnim(entityIn, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        VertexConsumer waveConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(getWaveTexture(entityIn.activeWaveTicks)));
        int waterColorAt = entityIn.level.getBiome(entityIn.blockPosition()).get().getWaterColor();
        float[] color = MathHelper.rgbFloat(waterColorAt);
        this.model.renderToBuffer(matrixStackIn, waveConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
        VertexConsumer overlayConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(getOverlayTexture(entityIn.activeWaveTicks)));
        float[] overlayColor = new float[]{Math.min(1.0F, color[0] + 0.5F), Math.min(1.0F, color[1] + 0.5F), Math.min(1.0F, color[2] + 0.5F)};
        this.model.renderToBuffer(matrixStackIn, overlayConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, overlayColor[0], overlayColor[1], overlayColor[2], 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.getWaveTexture(entity.activeWaveTicks);
    }

    private ResourceLocation getWaveTexture(int tickCount) {
        int j = (tickCount % 12) / 3;
        return switch (j) {
            case 0 -> TEXTURE_0;
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_3;
        };
    }

    private ResourceLocation getOverlayTexture(int tickCount) {
        int j = (tickCount % 12) / 3;
        return switch (j) {
            case 0 -> OVERLAY_TEXTURE_0;
            case 1 -> OVERLAY_TEXTURE_1;
            case 2 -> OVERLAY_TEXTURE_2;
            default -> OVERLAY_TEXTURE_3;
        };
    }
}
