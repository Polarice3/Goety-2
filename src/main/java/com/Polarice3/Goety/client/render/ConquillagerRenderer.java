package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.ConquillagerModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Conquillager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ConquillagerRenderer<T extends Conquillager> extends MobRenderer<T, ConquillagerModel<T>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID, "textures/entity/illagers/conquillager.png");

    public ConquillagerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ConquillagerModel<>(renderManagerIn.bakeLayer(ModModelLayer.CONQUILLAGER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER))));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
