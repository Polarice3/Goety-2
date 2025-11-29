package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.TramplerModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Trampler;
import com.Polarice3.Goety.common.items.TramplerArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TramplerRenderer extends MobRenderer<Trampler, TramplerModel<Trampler>> {
    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/trampler/trampler.png");

    public TramplerRenderer(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new TramplerModel<>(p_174435_.bakeLayer(ModModelLayer.TRAMPLER)), 0.5F);
        this.addLayer(new TramplerArmorLayer<>(this, p_174435_.getModelSet()));
    }

    @Override
    protected void scale(Trampler p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(1.2F, 1.2F, 1.2F);
    }

    public ResourceLocation getTextureLocation(Trampler p_116292_) {
        return TEXTURE;
    }

    public static class TramplerArmorLayer<T extends Trampler> extends RenderLayer<T, TramplerModel<T>> {
        private final TramplerModel<T> model;

        public TramplerArmorLayer(RenderLayerParent<T, TramplerModel<T>> p_174496_, EntityModelSet p_174497_) {
            super(p_174496_);
            this.model = new TramplerModel<>(p_174497_.bakeLayer(ModModelLayer.TRAMPLER));
        }

        public void render(PoseStack p_117032_, MultiBufferSource p_117033_, int p_117034_, T p_117035_, float p_117036_, float p_117037_, float p_117038_, float p_117039_, float p_117040_, float p_117041_) {
            ItemStack itemstack = p_117035_.getArmor();
            if (itemstack.getItem() instanceof TramplerArmorItem armorItem) {
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(p_117035_, p_117036_, p_117037_, p_117038_);
                this.model.setupAnim(p_117035_, p_117036_, p_117037_, p_117039_, p_117040_, p_117041_);

                VertexConsumer vertexconsumer = p_117033_.getBuffer(RenderType.entityCutoutNoCull(armorItem.getTexture()));
                this.model.renderToBuffer(p_117032_, vertexconsumer, p_117034_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
