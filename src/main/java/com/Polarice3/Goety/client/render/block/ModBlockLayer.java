package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Sets;
import net.minecraft.client.model.geom.ModelLayerLocation;

import java.util.Set;

public class ModBlockLayer {
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation ARCA = register("arca");
    public static final ModelLayerLocation TALL_SKULL = register("tall_skull");
    public static final ModelLayerLocation REDSTONE_GOLEM_SKULL = register("redstone_golem_skull");
    public static final ModelLayerLocation GRAVE_GOLEM_SKULL = register("grave_golem_skull");
    public static final ModelLayerLocation REDSTONE_MONSTROSITY_HEAD = register("redstone_monstrosity_head");
    public static final ModelLayerLocation LOFTY_CHEST = register("lofty_chest");

    private static ModelLayerLocation register(String p_171294_) {
        return register(p_171294_, "main");
    }

    private static ModelLayerLocation register(String p_171296_, String p_171297_) {
        ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
        if (!ALL_MODELS.add(modellayerlocation)) {
            throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
        } else {
            return modellayerlocation;
        }
    }
    private static ModelLayerLocation createLocation(String p_171301_, String p_171302_) {
        return new ModelLayerLocation(Goety.location(p_171301_), p_171302_);
    }

}
