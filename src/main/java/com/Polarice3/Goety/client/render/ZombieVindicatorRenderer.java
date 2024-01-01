package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.client.render.model.VillagerServantModel;
import com.Polarice3.Goety.common.entities.ally.ZombieVindicator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ZombieVindicatorRenderer extends HumanoidMobRenderer<ZombieVindicator, VillagerServantModel<ZombieVindicator>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/zombie_vindicator.png");

    public ZombieVindicatorRenderer(EntityRendererProvider.Context entityRendererManager) {
        super(entityRendererManager, new VillagerServantModel<>(entityRendererManager.bakeLayer(ModModelLayer.ZOMBIE_VILLAGER_SERVANT)),0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)), entityRendererManager.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, entityRendererManager.getItemInHandRenderer()));
    }

    protected void scale(ZombieVindicator entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    public ResourceLocation getTextureLocation(ZombieVindicator entity) {
        return TEXTURE;
    }
}
