package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.client.render.model.VillagerServantModel;
import com.Polarice3.Goety.common.entities.hostile.servants.ZombieVillagerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class ZombieVillagerServantRenderer extends HumanoidMobRenderer<ZombieVillagerServant, VillagerServantModel<ZombieVillagerServant>> {

    public ZombieVillagerServantRenderer(EntityRendererProvider.Context entityRendererManager) {
        super(entityRendererManager, new VillagerServantModel<>(entityRendererManager.bakeLayer(ModModelLayer.ZOMBIE_VILLAGER_SERVANT)),0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER))));
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieVillagerServant entity) {
        return entity.getResourceLocation();
    }
}
