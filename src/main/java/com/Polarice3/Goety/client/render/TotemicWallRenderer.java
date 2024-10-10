package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TotemicWallRenderer<T extends AbstractMonolith> extends AbstractMonolithRenderer<T>{
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/totemic_wall.png");

    public TotemicWallRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
    }

    @Override
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        return null;
    }

    @Override
    public Map<AbstractMonolith.Crackiness, ResourceLocation> cracknessLocation() {
        return null;
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }
}
