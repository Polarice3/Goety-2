package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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

public class FreezeLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/freeze.png");
    private static final ResourceLocation UNMASKABLE = Goety.location("unmaskable");
    private final LivingEntityRenderer<T, M> renderer;
    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

    public FreezeLayer(LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    public static ResourceLocation getMasked(ResourceLocation baseTex, ResourceLocation freezeTex, int frame) {
        String key = baseTex + "|" + frame;
        ResourceLocation cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        ResourceLocation baked = build(baseTex, freezeTex, frame);
        CACHE.put(key, baked);
        return baked;
    }

    private static ResourceLocation build(ResourceLocation baseTex, ResourceLocation freezeTex, int frame) {
        Minecraft mc = Minecraft.getInstance();
        NativeImage base = null;
        NativeImage freeze = null;
        try {
            base = read(baseTex);
            freeze = read(freezeTex);

            if (base == null || freeze == null) {
                return UNMASKABLE;
            }

            int width = base.getWidth();
            int height = base.getHeight();
            NativeImage out = new NativeImage(width, height, true);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int baseA = (base.getPixelRGBA(x, y) >> 24) & 0xFF;
                    if (baseA == 0) {
                        out.setPixelRGBA(x, y, 0);
                        continue;
                    }
                    int bp = freeze.getPixelRGBA(x % freeze.getWidth(), y % freeze.getHeight());
                    int freezeA = (bp >> 24) & 0xFF;
                    out.setPixelRGBA(x, y, freezeA == 0 ? 0 : bp);
                }
            }

            base.close();
            freeze.close();

            DynamicTexture dyn = new DynamicTexture(out);
            ResourceLocation id = Goety.location("freeze_masked/" + baseTex.getPath().replace('/', '_') + "_" + frame);
            mc.getTextureManager().register(id, dyn);
            return id;
        } catch (Exception e) {
            return UNMASKABLE;
        } finally {
            if (base != null) {
                base.close();
            }
            if (freeze != null) {
                freeze.close();
            }
        }
    }

    private static NativeImage read(ResourceLocation rl) {
        Minecraft mc = Minecraft.getInstance();

        Optional<Resource> res = mc.getResourceManager().getResource(rl);
        if (res.isPresent()) {
            try (InputStream is = res.get().open()) {
                return NativeImage.read(is);
            } catch (Exception e) {
                return null;
            }
        }

        AbstractTexture tex = mc.getTextureManager().getTexture(rl);
        if (tex instanceof DynamicTexture dyn && dyn.getPixels() != null) {
            NativeImage src = dyn.getPixels();
            NativeImage copy = new NativeImage(src.getWidth(), src.getHeight(), false);
            copy.copyFrom(src);
            return copy;
        }

        return null;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.renderer != null) {
            if (MiscCapHelper.isFreezing(entity)) {
                ResourceLocation entityTex = this.renderer.getTextureLocation(entity);
                ResourceLocation masked = getMasked(entityTex, TEXTURE, 0);

                VertexConsumer vc = bufferIn.getBuffer(RenderType.entityTranslucent(masked));
                int level = Math.min(MiscCapHelper.freezeLevel(entity), 5);
                float alpha = level / 5.0F;
                if (masked == UNMASKABLE) {
                    return;
                }
                this.getParentModel().renderToBuffer(matrixStackIn, vc, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}
