package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.RGEmissiveLayer;
import com.Polarice3.Goety.client.render.model.RedstoneGolemModel;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneGolem;
import com.Polarice3.Goety.config.MobsConfig;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class RedstoneGolemRenderer<T extends RedstoneGolem> extends MobRenderer<T, RedstoneGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/redstone_golem/redstone_golem.png");
    private static final ResourceLocation GLOW_TEXTURE = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_glow.png");

    public RedstoneGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RedstoneGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.REDSTONE_GOLEM)), 1.5F);
        this.addLayer(new RedstoneGolemCrackinessLayer<>(this));
        this.addLayer(new RedstoneGolemEyesLayer<>(this));
        this.addLayer(new RedstoneGolemFlashLayer<>(this));
        this.addLayer(new RGEmissiveLayer<>(this, GLOW_TEXTURE, (entity, partialTicks, ageInTicks) -> {
            return entity.summonTick < 20 && !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
        }, RedstoneGolemModel::getGlowParts));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURES;
    }

    public static class RedstoneGolemEyesLayer<T extends RedstoneGolem, M extends RedstoneGolemModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation EYES = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_eyes.png");

        public RedstoneGolemEyesLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class RedstoneGolemFlashLayer<T extends RedstoneGolem, M extends RedstoneGolemModel<T>> extends EyesLayer<T, M>{
        private static final ResourceLocation FLASH = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_flash.png");
        private static final ResourceLocation SUPER_GLOW_TEXTURE = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_glow_super.png");

        public RedstoneGolemFlashLayer(RenderLayerParent<T, M> p_116981_) {
            super(p_116981_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (p_116986_.isSummoning() && p_116986_.summonTick > 20) {
                if (p_116986_.isFlash) {
                    super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_, p_116991_, p_116992_);
                } else {
                    VertexConsumer vertexconsumer = p_116984_.getBuffer(RenderType.entityTranslucentEmissive(SUPER_GLOW_TEXTURE));
                    this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        @Override
        public RenderType renderType() {
            return RenderType.entityTranslucentEmissive(FLASH);
        }
    }

    public static class RedstoneGolemCrackinessLayer<T extends RedstoneGolem> extends RenderLayer<T, RedstoneGolemModel<T>> {
        private static final Map<RedstoneGolem.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(RedstoneGolem.Crackiness.LOW, Goety.location("textures/entity/servants/redstone_golem/redstone_golem_damage_minor.png"), RedstoneGolem.Crackiness.MEDIUM, Goety.location("textures/entity/servants/redstone_golem/redstone_golem_damage_moderate.png"), RedstoneGolem.Crackiness.HIGH, Goety.location("textures/entity/servants/redstone_golem/redstone_golem_damage_heavy.png"));

        public RedstoneGolemCrackinessLayer(RenderLayerParent<T, RedstoneGolemModel<T>> p_117135_) {
            super(p_117135_);
        }

        public void render(PoseStack p_117148_, MultiBufferSource p_117149_, int p_117150_, T p_117151_, float p_117152_, float p_117153_, float p_117154_, float p_117155_, float p_117156_, float p_117157_) {
            if (MobsConfig.RedstoneGolemCrack.get()) {
                if (!p_117151_.isInvisible()) {
                    RedstoneGolem.Crackiness irongolem$crackiness = p_117151_.getCrackiness();
                    if (irongolem$crackiness != RedstoneGolem.Crackiness.NONE) {
                        ResourceLocation resourcelocation = resourceLocations.get(irongolem$crackiness);
                        renderColoredCutoutModel(this.getParentModel(), resourcelocation, p_117148_, p_117149_, p_117150_, p_117151_, 1.0F, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}
