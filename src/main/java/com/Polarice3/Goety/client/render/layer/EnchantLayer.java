package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnchantLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation OUTER = Goety.location("textures/entity/enchant_outer.png");
    private static final ResourceLocation INNER = Goety.location("textures/entity/enchant_inner.png");
    private static final float OUTER_SPEED = 0.01F;
    private static final float INNER_SPEED = 0.02F;
    private static final Map<ResourceLocation, int[]> SIZES = new HashMap<>();
    private final LivingEntityRenderer<T, M> renderer;

    public EnchantLayer(LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.renderer == null || MiscCapHelper.getEnchanter(entity) == null) {
            return;
        }
        ResourceLocation entityTex = this.renderer.getTextureLocation(entity);
        float f = (float) entity.tickCount + partialTicks;
        this.renderSwirl(matrixStackIn, bufferIn, packedLightIn, entityTex, OUTER, f * OUTER_SPEED);
        this.renderSwirl(matrixStackIn, bufferIn, packedLightIn, entityTex, INNER, f * INNER_SPEED);
    }

    private void renderSwirl(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ResourceLocation entityTex, ResourceLocation swirlTex, float scroll) {
        int[] base = size(entityTex);
        int[] swirl = size(swirlTex);
        if (base.length == 0 || swirl.length == 0) {
            return;
        }
        float offset = scroll % 1.0F;
        float scaleU = (float) base[0] / (float) swirl[0];
        float scaleV = (float) base[1] / (float) swirl[1];
        VertexConsumer consumer = buffer.getBuffer(ModRenderType.enchantSwirl(swirlTex, entityTex, offset, offset, scaleU, scaleV));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int[] size(ResourceLocation rl) {
        return SIZES.computeIfAbsent(rl, EnchantLayer::readSize);
    }

    private static int[] readSize(ResourceLocation rl) {
        Minecraft mc = Minecraft.getInstance();
        Optional<Resource> res = mc.getResourceManager().getResource(rl);
        if (res.isPresent()) {
            try (InputStream is = res.get().open(); NativeImage image = NativeImage.read(is)) {
                return new int[]{image.getWidth(), image.getHeight()};
            } catch (Exception e) {
                return new int[0];
            }
        }
        AbstractTexture tex = mc.getTextureManager().getTexture(rl);
        if (tex instanceof DynamicTexture dyn && dyn.getPixels() != null) {
            return new int[]{dyn.getPixels().getWidth(), dyn.getPixels().getHeight()};
        }
        return new int[0];
    }
}