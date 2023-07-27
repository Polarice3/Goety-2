package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TotemicBombRenderer extends AbstractMonolithRenderer{
    private static final ResourceLocation TEXTURE_LOCATION = Goety.location("textures/entity/monolith/totemic_bomb.png");
    private static final RenderType RENDER_TYPE = RenderType.eyes(Goety.location("textures/entity/monolith/totemic_bomb_glow.png"));
    private static final RenderType RENDER_TYPE_2 = RenderType.eyes(Goety.location("textures/entity/monolith/totemic_bomb_glow2.png"));

    public TotemicBombRenderer(EntityRendererProvider.Context p_i47208_1_) {
        super(p_i47208_1_);
    }

    @Override
    public RenderType getActivatedTextureLocation(AbstractMonolith monolith) {
        if (monolith.tickCount >= MathHelper.secondsToTicks(monolith.getLifeSpan() - 2)){
            return RENDER_TYPE_2;
        } else {
            return RENDER_TYPE;
        }
    }

    @Override
    public Map<AbstractMonolith.Crackiness, ResourceLocation> cracknessLocation() {
        return null;
    }

    public ResourceLocation getTextureLocation(AbstractMonolith pEntity) {
        return TEXTURE_LOCATION;
    }
}
