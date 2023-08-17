package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.RGEmissiveLayer;
import com.Polarice3.Goety.client.render.model.RedstoneGolemModel;
import com.Polarice3.Goety.common.entities.ally.RedstoneGolem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class RedstoneGolemRenderer<T extends RedstoneGolem> extends MobRenderer<T, RedstoneGolemModel<T>> {
    private static final ResourceLocation TEXTURES = Goety.location("textures/entity/servants/redstone_golem/redstone_golem.png");
    private static final ResourceLocation GLOW_TEXTURE = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_glow.png");
    private static final ResourceLocation SUPER_GLOW_TEXTURE = Goety.location("textures/entity/servants/redstone_golem/redstone_golem_glow_super.png");

    public RedstoneGolemRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RedstoneGolemModel<>(renderManagerIn.bakeLayer(ModModelLayer.REDSTONE_GOLEM)), 1.5F);
        this.addLayer(new RedstoneGolemEyesLayer<>(this));
        this.addLayer(new RGEmissiveLayer<>(this, GLOW_TEXTURE, (entity, partialTicks, ageInTicks) -> {
            return !entity.isSummoning() && !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
        }, RedstoneGolemModel::getGlowParts));
        this.addLayer(new RGEmissiveLayer<>(this, SUPER_GLOW_TEXTURE, (entity, partialTicks, ageInTicks) -> {
            return entity.isSummoning() && !entity.isDeadOrDying() ? entity.getGlow : 0.0F;
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
}
