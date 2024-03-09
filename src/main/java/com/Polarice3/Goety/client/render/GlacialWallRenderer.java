package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class GlacialWallRenderer extends AbstractMonolithRenderer{
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/glacial_wall.png");
    private static final Map<AbstractMonolith.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(AbstractMonolith.Crackiness.LOW, Goety.location("textures/entity/monolith/glacial_wall_crack_1.png"), AbstractMonolith.Crackiness.MEDIUM, Goety.location("textures/entity/monolith/glacial_wall_crack_1.png"), AbstractMonolith.Crackiness.HIGH, Goety.location("textures/entity/monolith/glacial_wall_crack_2.png"));

    public GlacialWallRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
    }

    @Override
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        return null;
    }

    @Override
    public Map<AbstractMonolith.Crackiness, ResourceLocation> cracknessLocation() {
        return resourceLocations;
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }
}
