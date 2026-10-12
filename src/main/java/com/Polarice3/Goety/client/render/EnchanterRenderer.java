package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.EnchanterModel;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;

public class EnchanterRenderer<T extends Mob & IEnchanter> extends MobRenderer<T, EnchanterModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/enchanter.png");
    protected static final ResourceLocation ORIGINAL = Goety.location("textures/entity/illagers/enchanter.png");

    public EnchanterRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new EnchanterModel<>(p_174304_.bakeLayer(ModModelLayer.ENCHANTER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, p_174304_));
        this.addLayer(new DiamondGlowLayer<>(this));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if ((entity instanceof IServant servant && servant.isHostile()) || entity instanceof Enemy || !MobsConfig.IceologerServantTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE;
        }
    }

    public static class DiamondGlowLayer<T extends Mob & IEnchanter, M extends EnchanterModel<T>> extends EyesLayer<T, M> {
        private static final RenderType GLOW = RenderType.eyes(Goety.location("textures/entity/servants/illager/enchanter_glow.png"));

        public DiamondGlowLayer(RenderLayerParent<T, M> p_117507_) {
            super(p_117507_);
        }

        public RenderType renderType() {
            return GLOW;
        }
    }
}
