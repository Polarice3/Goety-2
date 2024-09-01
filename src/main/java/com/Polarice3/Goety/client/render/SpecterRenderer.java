package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.SpecterModel;
import com.Polarice3.Goety.common.entities.neutral.Specter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpecterRenderer<T extends Specter> extends MobRenderer<T, SpecterModel<T>> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/specter/specter.png");

    public SpecterRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new SpecterModel<>(renderManagerIn.bakeLayer(ModModelLayer.SPECTER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

}
