package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.WraithBandsLayer;
import com.Polarice3.Goety.client.render.layer.WraithGlowLayer;
import com.Polarice3.Goety.client.render.layer.WraithSecretLayer;
import com.Polarice3.Goety.client.render.model.WraithModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WraithServantRenderer<T extends AbstractWraith> extends AbstractWraithRenderer<T> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/wraith/wraith.png");

    public WraithServantRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new WraithModel<>(renderManagerIn.bakeLayer(ModModelLayer.WRAITH)), 0.5F);
        this.addLayer(new WraithGlowLayer<>(this));
        this.addLayer(new WraithBandsLayer<>(this, renderManagerIn.getModelSet()));
        this.addLayer(new WraithSecretLayer<>(this, renderManagerIn.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

}
