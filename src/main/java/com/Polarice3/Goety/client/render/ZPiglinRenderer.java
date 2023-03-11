package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class ZPiglinRenderer extends HumanoidMobRenderer<Mob, ZPiglinModel<Mob>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zpiglin_servant.png");
    protected static final ResourceLocation TEXTURE2 = Goety.location("textures/entity/servants/zpiglin_brute_servant.png");

    public ZPiglinRenderer(EntityRendererProvider.Context entityRendererManager) {
        super(entityRendererManager, createModel(entityRendererManager.getModelSet()), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(entityRendererManager.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT)), new HumanoidModel(entityRendererManager.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT))));
    }

    private static ZPiglinModel<Mob> createModel(EntityModelSet p_174350_) {
        ZPiglinModel<Mob> piglinmodel = new ZPiglinModel<>(p_174350_.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT));
        piglinmodel.rightEar.visible = false;
        return piglinmodel;
    }

    public ResourceLocation getTextureLocation(Mob entity) {
        if (entity instanceof ZPiglinBruteServant){
            return TEXTURE2;
        } else {
            return TEXTURE;
        }
    }

}
