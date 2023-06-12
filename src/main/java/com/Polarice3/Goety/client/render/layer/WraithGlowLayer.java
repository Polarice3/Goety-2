package com.Polarice3.Goety.client.render.layer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.client.render.model.WraithModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class WraithGlowLayer<T extends Mob, M extends WraithModel<T>> extends EyesLayer<T, M> {
    private static final RenderType RENDER_TYPE = ModRenderType.wraith(new ResourceLocation(Goety.MOD_ID, "textures/entity/wraith_glow.png"));

    public WraithGlowLayer(RenderLayerParent<T, M> p_i50919_1_) {
        super(p_i50919_1_);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }
}
