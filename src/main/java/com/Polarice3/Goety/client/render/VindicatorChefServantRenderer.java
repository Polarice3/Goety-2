package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.VindicatorChefModel;
import com.Polarice3.Goety.common.entities.ally.illager.VindicatorChefServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class VindicatorChefServantRenderer<T extends VindicatorChefServant> extends MobRenderer<T, VindicatorChefModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/illager/vindicator_chef.png");
    protected static final ResourceLocation ORIGINAL = Goety.location("textures/entity/servants/illager/vindicator_chef_original.png");

    public VindicatorChefServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VindicatorChefModel<>(renderManagerIn.bakeLayer(ModModelLayer.VINDICATOR_CHEF)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T illager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (illager.isAggressive() || illager.isCooking()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, illager, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }

            @Override
            protected void renderArmWithItem(LivingEntity p_117185_, ItemStack p_117186_, ItemDisplayContext p_270970_, HumanoidArm p_117188_, PoseStack p_117189_, MultiBufferSource p_117190_, int p_117191_) {
                if (p_117186_.getItem() instanceof AxeItem) {
                    p_117186_ = new ItemStack(ModItems.COOKING_LADLE.get());
                }
                super.renderArmWithItem(p_117185_, p_117186_, p_270970_, p_117188_, p_117189_, p_117190_, p_117191_);
            }
        });
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isHostile() || !MobsConfig.VindicatorServantTexture.get()){
            return ORIGINAL;
        } else {
            return TEXTURE;
        }
    }
}
