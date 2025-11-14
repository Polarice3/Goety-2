package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SorcererModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.hostile.illagers.Sorcerer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.AbstractIllager;

public class SorcererRenderer<T extends Sorcerer> extends MobRenderer<T, SorcererModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/illagers/sorcerer/sorcerer.png");

    public SorcererRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SorcererModel<>(renderManagerIn.bakeLayer(ModModelLayer.SORCERER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
        this.addLayer(new CastLevelLayer<>(this));
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)), renderManagerIn.getModelManager()));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }

    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public static class CastLevelLayer<T extends Sorcerer, M extends SorcererModel<T>> extends RenderLayer<T, M> {
        private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), (p_117657_) -> {
            p_117657_.put(1, Goety.location("textures/entity/illagers/sorcerer/level_1.png"));
            p_117657_.put(2, Goety.location("textures/entity/illagers/sorcerer/level_2.png"));
            p_117657_.put(3, Goety.location("textures/entity/illagers/sorcerer/level_3.png"));
            p_117657_.put(4, Goety.location("textures/entity/illagers/sorcerer/level_4.png"));
            p_117657_.put(5, Goety.location("textures/entity/illagers/sorcerer/level_5.png"));
        });

        public CastLevelLayer(RenderLayerParent<T, M> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                ResourceLocation resourcelocation2 = LEVEL_LOCATIONS.get(Mth.clamp(p_116986_.getSorcererLevel(), 1, LEVEL_LOCATIONS.size()));
                renderColoredCutoutModel(this.getParentModel(), resourcelocation2, p_116983_, p_116984_, p_116985_, p_116986_, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
