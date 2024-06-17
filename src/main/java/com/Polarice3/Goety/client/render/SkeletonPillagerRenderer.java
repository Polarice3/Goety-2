package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SkeletonVillagerModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractSkeletonPillager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonPillagerRenderer extends HumanoidMobRenderer<AbstractSkeletonPillager, SkeletonVillagerModel<AbstractSkeletonPillager>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/skeleton/skeleton_pillager.png");

    public SkeletonPillagerRenderer(EntityRendererProvider.Context entityRendererManager) {
        super(entityRendererManager, new SkeletonVillagerModel<>(entityRendererManager.bakeLayer(ModModelLayer.SKELETON_VILLAGER_SERVANT)),0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(entityRendererManager.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER))));
        this.addLayer(new ItemInHandLayer<>(this, entityRendererManager.getItemInHandRenderer()));
    }

    protected void scale(AbstractSkeletonPillager entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    public ResourceLocation getTextureLocation(AbstractSkeletonPillager entity) {
        return TEXTURE;
    }
}
