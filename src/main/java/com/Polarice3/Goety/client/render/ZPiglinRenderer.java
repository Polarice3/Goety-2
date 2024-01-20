package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.ZPiglinBandsLayer;
import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class ZPiglinRenderer extends HumanoidMobRenderer<ZPiglinServant, ZPiglinModel<ZPiglinServant>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie/zpiglin_servant.png");
    protected static final ResourceLocation TEXTURE2 = Goety.location("textures/entity/servants/zombie/zpiglin_brute_servant.png");

    public ZPiglinRenderer(EntityRendererProvider.Context entityRendererManager) {
        super(entityRendererManager, createModel(entityRendererManager.getModelSet()), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(entityRendererManager.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR)), new HumanoidModel<>(entityRendererManager.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR))));
        this.addLayer(new ZPiglinBandsLayer<>(this, entityRendererManager.getModelSet()));
    }

    private static ZPiglinModel<ZPiglinServant> createModel(EntityModelSet p_174350_) {
        ZPiglinModel<ZPiglinServant> piglinmodel = new ZPiglinModel<>(p_174350_.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT));
        piglinmodel.rightEar.visible = false;
        return piglinmodel;
    }

    public ResourceLocation getTextureLocation(ZPiglinServant entity) {
        if (entity instanceof ZPiglinBruteServant){
            return TEXTURE2;
        } else {
            return TEXTURE;
        }
    }

}
