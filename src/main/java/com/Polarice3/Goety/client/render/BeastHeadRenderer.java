package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.BeastHeadModel;
import com.Polarice3.Goety.common.entities.neutral.BeastHead;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BeastHeadRenderer<T extends BeastHead> extends MobRenderer<T, BeastHeadModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/servants/black_wolf/beast_head.png");

    public BeastHeadRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BeastHeadModel<>(renderManagerIn.bakeLayer(ModModelLayer.BEAST_HEAD)), 0.0F);
    }

    @Override
    public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
        this.model.setAlpha(p_115455_.alpha);
        super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
        this.model.setAlpha(1.0F);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        ResourceLocation resourcelocation = this.getTextureLocation(p_115322_);
        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }

    @Override
    public ResourceLocation getTextureLocation(BeastHead pEntity) {
        return TEXTURE;
    }
}
