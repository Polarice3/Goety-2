package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TropicalSlimeTextures {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
        for (int i = 0; i < 47; ++i){
            String s = String.valueOf(i + 1);
            map.put(i, location(s + ".png"));
        }
    });

    public static ResourceLocation location(String path) {
        return Goety.location("textures/entity/servants/slime/fishes/" + path);
    }
}
