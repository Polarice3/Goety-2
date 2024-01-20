package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.WraithGlowLayer;
import com.Polarice3.Goety.client.render.model.WraithModel;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WraithRenderer extends AbstractWraithRenderer {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/wraith/wraith.png");

    public WraithRenderer(EntityRendererProvider.Context renderManagerIn){
        super(renderManagerIn, new WraithModel<>(renderManagerIn.bakeLayer(ModModelLayer.WRAITH)), 0.5F);
        this.addLayer(new WraithGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractWraith entity) {
        return TEXTURE;
    }

}
